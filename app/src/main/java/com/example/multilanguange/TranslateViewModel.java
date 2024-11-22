package com.example.multilanguange;

import android.app.Application;
import android.util.LruCache;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TranslateViewModel extends AndroidViewModel {

    private static final int NUM_TRANSLATORS = 3;
    private final RemoteModelManager modelManager;
    private final LruCache<TranslatorOptions, Translator> translators = new LruCache<TranslatorOptions, Translator>(NUM_TRANSLATORS) {
        @Override
        protected Translator create(TranslatorOptions key) {
            return Translation.getClient(key);
        }

        @Override
        protected void entryRemoved(boolean evicted, TranslatorOptions key, Translator oldValue, Translator newValue) {
            oldValue.close();
        }
    };

    MutableLiveData<String> sourceLang = new MutableLiveData<>();
    MutableLiveData<String> targetLang = new MutableLiveData<>();
    MutableLiveData<String> sourceText = new MutableLiveData<>();
    MediatorLiveData<ResultOrError> translatedText = new MediatorLiveData<>();
    MutableLiveData<List<String>> availableModels = new MutableLiveData<>();

    public TranslateViewModel(@NonNull Application application) {
        super(application);
        modelManager = RemoteModelManager.getInstance();

        final OnCompleteListener<String> processTranslation = task -> {
            if (task.isSuccessful()) {
                translatedText.setValue(new ResultOrError(task.getResult(), null));
            } else {
                translatedText.setValue(new ResultOrError(null, task.getException()));
                Log.e("TranslateViewModel", "Translation failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        };

        fetchDownloadedModels();

        translatedText.addSource(sourceText, s -> translate().addOnCompleteListener(processTranslation));
        translatedText.addSource(sourceLang, language -> translate().addOnCompleteListener(processTranslation));
        translatedText.addSource(targetLang, language -> translate().addOnCompleteListener(processTranslation));
    }

    List<String> getAvailableLanguages() {
        List<String> languages = new ArrayList<>();
        List<String> languageIds = TranslateLanguage.getAllLanguages();
        for (String languageId : languageIds) {
            if (languageId != null && !languageId.isEmpty()) {
                languages.add(new Locale(languageId).getDisplayName());
            }
        }
        return languages;
    }

    private String getLanguageCode(String languageDisplayName) {
        List<String> languageIds = TranslateLanguage.getAllLanguages();
        for (String languageId : languageIds) {
            Locale locale = new Locale(languageId);
            String displayName = locale.getDisplayName();

            if (displayName.equalsIgnoreCase(languageDisplayName)) {
                return languageId; // Directly return the languageId as it is valid
            }
        }

        Log.e("TranslateViewModel", "No matching language code for display name: " + languageDisplayName);
        return null;
    }

    private TranslateRemoteModel getModel(String languageCode) {
        return new TranslateRemoteModel.Builder(languageCode).build();
    }

    void downloadLanguage(String languageCode) {
        TranslateRemoteModel model = getModel(languageCode);

        if (model == null) {
            Log.e("TranslateViewModel", "Failed to create model for language: " + languageCode);
            return;
        }

        modelManager.download(model, new DownloadConditions.Builder().build())
                .addOnSuccessListener(aVoid -> {
                    Log.d("TranslateViewModel", "Model downloaded successfully for language: " + languageCode);
                    fetchDownloadedModels();
                })
                .addOnFailureListener(e -> {
                    Log.e("TranslateViewModel", "Model download failed for language: " + languageCode + ". Error: " + e.getMessage());
                });
    }

    void deleteLanguage(String languageDisplayName) {
        String languageCode = getLanguageCode(languageDisplayName);

        if (languageCode == null) {
            Log.e("TranslateViewModel", "Invalid language code for deleting model: " + languageDisplayName);
            return;
        }

        TranslateRemoteModel model = getModel(languageCode);
        if (model != null) {
            modelManager.deleteDownloadedModel(model).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fetchDownloadedModels();
                }
            });
        }
    }

    public Task<String> translate() {
        final String text = sourceText.getValue();
        final String source = getLanguageCode(sourceLang.getValue());
        final String target = getLanguageCode(targetLang.getValue());

        if (source == null || target == null || text == null || text.isEmpty()) {
            Log.e("TranslateViewModel", "Source, target, or text is null/empty");
            return Tasks.forResult(""); // Exit early if there's invalid input
        }

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(source)
                .setTargetLanguage(target)
                .build();

        final Translator translator = translators.get(options);

        Log.d("TranslateViewModel", "Starting model download and translation");

        return translator.downloadModelIfNeeded().continueWithTask(task -> {
            if (task.isSuccessful()) {
                return translator.translate(text);
            } else {
                Exception e = task.getException();
                if (e == null) {
                    e = new Exception("Unknown issue occurred while downloading the model");
                }
                return Tasks.forException(e);
            }
        });
    }

    private void fetchDownloadedModels() {
        modelManager.getDownloadedModels(TranslateRemoteModel.class).addOnSuccessListener(translateRemoteModels -> {
            List<String> modelCodes = new ArrayList<>(translateRemoteModels.size());
            for (TranslateRemoteModel model : translateRemoteModels) {
                modelCodes.add(model.getLanguage());
            }
            availableModels.setValue(modelCodes);
        });
    }

    static class ResultOrError {
        final String result;
        final Exception error;

        ResultOrError(String result, Exception error) {
            this.result = result;
            this.error = error;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        translators.evictAll();
    }
}
