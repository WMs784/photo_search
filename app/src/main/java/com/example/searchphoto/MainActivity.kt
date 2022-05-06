package com.example.searchphoto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAAA7fcAEAAAAAazfEXOuU%2FL2B3RvnJggamNX88G8%3DGzthCc2JSyt4pzUTjm83CLN6D80Sx3Bb7cBPBWHHdTOPJkHGZw"
    private val BASE_URL = "https://api.twitter.com/2/tweets/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .build()
    private val api = retrofit.create(TwitterApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        val textBox = findViewById<EditText>(R.id.edit)
        val textView = findViewById<TextView>(R.id.text)

        button.setOnClickListener{
            val query = textBox.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = api.fetchTweets(accessToken = "Bearer $BEARER_TOKEN", searchWord = query, attach = "attachments.media_keys",).string()
                    val jsonData = JSONObject(response)
                    val tweetList = listOf(jsonData["data"])
                    for(i in tweetList.indices){
                        withContext(Dispatchers.Main) {
                            textView.text = tweetList[i].toString()
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
    ):ResponseBody
}