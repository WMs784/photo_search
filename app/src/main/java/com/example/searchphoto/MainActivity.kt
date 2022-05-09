package com.example.searchphoto

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import twitter4j.JSONObject
import java.lang.Exception
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener{
            fetchApi()
        }
    }
    private fun fetchApi(){
        val handler = Handler()
        val textView = findViewById<TextView>(R.id.text)
        val textBox = findViewById<EditText>(R.id.edit)
        val image = findViewById<ImageView>(R.id.image)

        val BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAAA7fcAEAAAAAazfEXOuU%2FL2B3RvnJggamNX88G8%3DGzthCc2JSyt4pzUTjm83CLN6D80Sx3Bb7cBPBWHHdTOPJkHGZw"
        val BASE_URL = "https://api.twitter.com/2/tweets/"

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        thread {
            val query = textBox.text.toString()
//            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val api = retrofit.create(TwitterApi::class.java)
                    val response = api.fetchTweets(
                        accessToken = "Bearer $BEARER_TOKEN",
                        searchWord = "$query 漫画 話",
                        attach = "attachments.media_keys",
                        media = "url"
                    ).execute().body()?: throw IllegalStateException("bodyがnullだよ！")

//                    val jsonData = JSONObject(response)
                    val tweetList = response?.data
                    val imageList = response.includes?.media
                    Log.d("info","$imageList length : ${imageList?.size}")
                    Handler(Looper.getMainLooper()).post {
//                        for(i in imageList!!.indices){
//                            val imageUrl = imageList?.get(i)?.url
//                            if(imageUrl != null){
//                               textView.text = tweetList[i]?.text
//                                Picasso.get()
//                                    .load(imageUrl)
//                                    .into(image)
//                            }
//                        }
//                        textView.text = imageList?.get(0).toString()
                        val imageUrl = imageList?.get(0)?.url
                        if(imageUrl != null){
                            textView.text = tweetList?.get(0)?.text
                            Picasso.get()
                                .load(imageUrl)
                                .into(image)
                        }
                    }
//                    textView.text = tweetList[0].text
                } catch (e: Exception){
                    Log.d("error","get info error : $e")
                    textView.text = "error : ${e.toString()}"
                }
//            }
        }
    }
}
interface TwitterApi {

    @GET("search/recent")
    fun fetchTweets(
        @Header("Authorization") accessToken: String,
        @Query("query") searchWord: String? = null,
        @Query("expansions")attach: String = "attachments.media_keys",
        @Query("media.fields")media: String = "url"
    ):Call<TweetData>
}