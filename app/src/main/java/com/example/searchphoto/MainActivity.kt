package com.example.searchphoto

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
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
        val textBox = findViewById<SearchView>(R.id.edit)
        val nameBox = findViewById<TextView>(R.id.name)
        val image1 = findViewById<ImageView>(R.id.image1)
        val image2 = findViewById<ImageView>(R.id.image2)
        val image3 = findViewById<ImageView>(R.id.image3)
        val image4 = findViewById<ImageView>(R.id.image4)
        val imageViewList = mutableListOf(image1,image2,image3,image4)

        val profileImage = findViewById<ImageView>(R.id.profileImage)

        val BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAAA7fcAEAAAAAazfEXOuU%2FL2B3RvnJggamNX88G8%3DGzthCc2JSyt4pzUTjm83CLN6D80Sx3Bb7cBPBWHHdTOPJkHGZw"
        val BASE_URL = "https://api.twitter.com/2/"

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        thread {
            val query = textBox.query
//            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val api = retrofit.create(TwitterApi::class.java)
                    val response = api.fetchTweets(
                        accessToken = "Bearer $BEARER_TOKEN",
                        searchWord = "$query 漫画 話",
//                        attach = "attachments.media_keys",
//                        media = "url"
                    ).execute().body()?: throw IllegalStateException("bodyがnullだよ！")
                    Log.d("info","${response}")

//                    val jsonData = JSONObject(response)
                    val tweetList = response.data
                    val imageList = response.includes?.media
                    Log.d("info","$tweetList length : ${tweetList?.size}")
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
                        var t = tweetList?.get(0)?.text
//                        var t = response.includes?.tweets?.get(0)?.text
                        var n = response.includes?.users?.get(0)?.name
                        var p = response.includes?.users?.get(0)?.profile_image_url
                        val id = response.includes?.tweets?.get(0)?.author_id
                        Log.d("info","id is $id, text is $t")
                        if(t != null){
                            if(t.length > 2 && t.substring(0,3) == "RT "){
                                Log.d("info","there is retweet")
//                                try{
//                                    val response2 = api.fetchProfile(
//                                        accessToken = "Bearer $BEARER_TOKEN",
//                                        id = id.toString()
//                                    ).execute().body()?: throw IllegalStateException("profile api bodyがnullだよ！")
//                                    Log.d("info","${response2}")
//                                    p = response2.profile_image_url
//                                    n = response2.name
//                                    t = response.includes?.tweets?.get(0)?.text
//                                    Log.d("info","original name $n,text $t,url $p")
//                                }catch (e: Exception){
//                                    Log.d("error","get info error : $e")
//                                    textView.text = "error : $e"
//                                }
                            }
                            textView.text = t
                        } else {
                            textView.text = "failed to get tweet text"
                        }
                        if(n != null){
                            nameBox.text = n
                        } else {
                            nameBox.text = "failed to get name data"
                        }
                        if(p != null){
                            Picasso.get()
                                .load(p)
                                .into(profileImage)
                        }
                        repeat(4){
                            val imageUrl = imageList?.get(it)?.url
                            Log.d("info","image url $it is $imageUrl")
                            if(imageUrl != null){
                                Picasso.get()
                                    .load(imageUrl)
                                    .into(imageViewList[it])
                            }
                        }
                    }
//                    textView.text = tweetList[0].text
                } catch (e: Exception){
                    Log.d("error","get info error : $e")
                    textView.text = "error : $e"
                }
//            }
        }
    }
}
interface TwitterApi {

    @GET("tweets/search/recent")
    fun fetchTweets(
        @Header("Authorization") accessToken: String,
        @Query("query") searchWord: String? = null,
        @Query("expansions")attach: String = "attachments.media_keys,author_id,referenced_tweets.id",
        @Query("media.fields")media: String = "url",
        @Query("user.fields")user: String = "entities,profile_image_url,name",
        @Query("tweet.fields")originalId: String = "author_id",
    ):Call<TweetData>

    @GET("users")
    fun fetchProfile(
        @Header("Authorization") accessToken: String,
        @Query("ids")id: String
//        @Query("user.fields") profile: String = "profile_image_url,name",
    ):Call<UserData>
}