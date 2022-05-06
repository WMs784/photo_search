package com.example.searchphoto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
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
        val textView = findViewById<TextView>(R.id.text)
        val textBox = findViewById<EditText>(R.id.edit)

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
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val api = retrofit.create(TwitterApi::class.java)
                    val response = api.fetchTweets(accessToken = "Bearer $BEARER_TOKEN", searchWord = query, attach = "attachments.media_keys",).execute().body()
                    Log.d("info","$response")
//                    val jsonData = JSONObject(response)
                    val tweetList = response?.data
                    if (tweetList != null) {
                        for(i in tweetList.indices){
                            withContext(Dispatchers.Main) {
                                textView.text = tweetList[i].toString()
                            }
                        }
                    }
                } catch (e: Exception){
                    Log.d("error","get info error : $e")
                    textView.text = "Failed to get info"
                }
            }
        }
    }
}
interface TwitterApi {

    @GET("search/recent")
    suspend fun fetchTweets(
        @Header("Authorization") accessToken: String,
        @Query("query",) searchWord: String? = null,
        @Query("expansions")attach: String = "attachments.media_keys",
//        @Query("media.fields")media: String = "url"
    ):Call<TweetData>
}