package com.mkrdeveloper.memeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mkrdeveloper.memeapp.adapter.RvAdapter
import com.mkrdeveloper.memeapp.databinding.ActivityMainBinding
import com.mkrdeveloper.memeapp.models.Meme
import com.mkrdeveloper.memeapp.utils.RetrofitInstance
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rvAdapter: RvAdapter

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                RetrofitInstance.api.getMemes()

            }catch (e: IOException){
                Toast.makeText(applicationContext, "app error ${e.message}", Toast.LENGTH_SHORT).show()
                return@launch
            }catch (e: HttpException){
                Toast.makeText(applicationContext, "http error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }
            if (response.isSuccessful && response.body() != null){
                withContext(Dispatchers.Main){
                    val memesList: List<Meme> = response.body()!!.data.memes
                    binding.apply {
                        progressBar.visibility = View.GONE
                        rvAdapter = RvAdapter(memesList)
                        recyclerView.adapter = rvAdapter
                        recyclerView.layoutManager = StaggeredGridLayoutManager(2,RecyclerView.VERTICAL)
                    }
                }
            }
        }
    }
}