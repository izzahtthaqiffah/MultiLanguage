package com.example.multilanguange;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.nl.translate.TranslateLanguage;
import java.util.List;

public class TranslateFragment extends Fragment {

    private ProgressBar progressBar;
    private TextInputEditText srcTextView; // Define srcTextView at the class level
    private TextView targetTextView; // Add targetTextView at the class level

    // Define the interface for clearing text
    public interface OnClearTextListener {
        void onClearText(); // Method to clear the text
    }

    private OnClearTextListener mListener;

    public TranslateFragment() {
        // Required empty public constructor
    }

    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Ensure the activity implements the interface
        if (context instanceof OnClearTextListener) {
            mListener = (OnClearTextListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnClearTextListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_translate, container, false);

        progressBar = view.findViewById(R.id.progressBar);

        final Button switchButton = view.findViewById(R.id.buttonSwitchLang);
        final ToggleButton sourceSyncButton = view.findViewById(R.id.buttonSyncSource);
        final ToggleButton targetSyncButton = view.findViewById(R.id.buttonSyncTarget);
        srcTextView = view.findViewById(R.id.sourceText);  // Initialize srcTextView here
        targetTextView = view.findViewById(R.id.targetText); // Initialize targetTextView here
        final TextView downloadedModelsTextView = view.findViewById(R.id.downloadedModels);
        final Spinner targetLangSelector = view.findViewById(R.id.targetLangSelector);
        final Spinner sourceLangSelector = view.findViewById(R.id.sourceLangSelector);

        final TranslateViewModel viewModel = new ViewModelProvider(this).get(TranslateViewModel.class);

        // Get available languages from ViewModel
        final List<String> availableLanguages = viewModel.getAvailableLanguages();

        // Adapter for the language selectors
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, availableLanguages);

        sourceLangSelector.setAdapter(adapter);
        targetLangSelector.setAdapter(adapter);

        // Set default selections (assuming English and Spanish)
        sourceLangSelector.setSelection(adapter.getPosition(TranslateLanguage.ENGLISH));
        targetLangSelector.setSelection(adapter.getPosition(TranslateLanguage.SPANISH));

        sourceLangSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setProgressText(targetTextView);
                viewModel.sourceLang.setValue(availableLanguages.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                targetTextView.setText("");
            }
        });

        targetLangSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setProgressText(targetTextView);
                viewModel.targetLang.setValue(availableLanguages.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                targetTextView.setText("");
            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressText(targetTextView);
                int sourceLangPosition = sourceLangSelector.getSelectedItemPosition();
                sourceLangSelector.setSelection(targetLangSelector.getSelectedItemPosition());
                targetLangSelector.setSelection(sourceLangPosition);
            }
        });

        sourceSyncButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String languageCode = availableLanguages.get(sourceLangSelector.getSelectedItemPosition());
                if (isChecked) {
                    viewModel.downloadLanguage(languageCode);
                } else {
                    viewModel.deleteLanguage(languageCode);
                }
            }
        });

        targetSyncButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String languageCode = availableLanguages.get(targetLangSelector.getSelectedItemPosition());
                if (isChecked) {
                    viewModel.downloadLanguage(languageCode);
                } else {
                    viewModel.deleteLanguage(languageCode);
                }
            }
        });

        srcTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Show the progress bar when typing
                progressBar.setVisibility(View.VISIBLE);
                viewModel.sourceText.postValue(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        viewModel.translatedText.observe(getViewLifecycleOwner(), new Observer<TranslateViewModel.ResultOrError>() {
            @Override
            public void onChanged(TranslateViewModel.ResultOrError resultOrError) {
                progressBar.setVisibility(View.GONE);  // Hide progress bar once translation is done
                if (resultOrError.error != null) {
                    srcTextView.setError(resultOrError.error.getLocalizedMessage());
                } else {
                    targetTextView.setText(resultOrError.result);
                }
            }
        });

        viewModel.availableModels.observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> translateRemoteModels) {
                String output = getContext().getString(R.string.downloaded_model_label, translateRemoteModels);
                downloadedModelsTextView.setText(output);
                sourceSyncButton.setChecked(translateRemoteModels.contains(
                        availableLanguages.get(sourceLangSelector.getSelectedItemPosition())
                ));
                targetSyncButton.setChecked(translateRemoteModels.contains(
                        availableLanguages.get(targetLangSelector.getSelectedItemPosition())
                ));
            }
        });

        return view;
    }

    private void setProgressText(TextView tv) {
        tv.setText(getContext().getString(R.string.translate_progress));
    }

    // Method to update the source text, called from MainActivity (e.g., after speech recognition)
    public void updateSourceText(String text) {
        if (srcTextView != null) {
            srcTextView.setText(text); // Update srcTextView with the recognized text
        }
    }

    // Add a method to clear the source text
    public void clearSourceText() {
        if (srcTextView != null) {
            srcTextView.setText(""); // Clears the text in the sourceText field
        }
    }

    // Add a method to get the translated text from targetTextView
    public String getTargetText() {
        if (targetTextView != null) {
            return targetTextView.getText().toString();
        }
        return "";
    }
}
