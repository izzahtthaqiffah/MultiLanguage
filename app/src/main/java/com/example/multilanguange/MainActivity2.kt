package com.example.multilanguange

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multilanguange.databinding.ActivityMain2Binding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity2 : AppCompatActivity() {

    lateinit var binding: ActivityMain2Binding

    lateinit var adapter: MeaningAdapter

    private lateinit var bck: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        bck = findViewById(R.id.bck)

        bck.setOnClickListener(View.OnClickListener {
            val i = Intent(
                this@MainActivity2,
                MainActivity::class.java
            )
            startActivity(i)
        })

        binding.searchBtn.setOnClickListener {
            val word = binding.searchInput.text.toString()
            getMeaning(word)
        }

        adapter = MeaningAdapter(emptyList())
        binding.meaningRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.meaningRecyclerView.adapter = adapter


    }

    private fun getMeaning(word: String) {
        setInProgress(true)
        GlobalScope.launch {
            try {
                val response = RetrofitInstance.dictionaryApi.getMeaning(word)
                if(response.body()==null){
                    throw (Exception())
                }
                runOnUiThread {
                    setInProgress(false)
                    response.body()?.first()?.let {
                        setUI(it)
                    }
                }
            }catch (e : Exception){
                runOnUiThread{
                    setInProgress(false)
                    Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    private fun setUI(response: WordResult) {
        binding.wordTextview.text = response.word
        binding.phoneticTextview.text = response.phonetic
        adapter.updateNewData(response.meanings)
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.searchBtn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.searchBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}














