package com.example.searchphoto

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
import java.lang.Exception
import java.lang.Integer.min
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
                    ).execute().body()?: throw IllegalStateException("bodyがnullだよ！")
                    Log.d("info","${response}")
                    val tweetList = response.data
                    val imageList = response.includes?.media
//                    Log.d("info","$tweetList length : ${tweetList?.size}")
//                    Log.d("info","$imageList length : ${imageList?.size}")
                    Handler(Looper.getMainLooper()).post {
                        var t = tweetList?.get(0)?.text
//                        var ort = response.includes?.tweets?.get(0)?.text//元ツイートのテキスト
                        var n = response.includes?.users?.get(0)?.name//投稿者のユーザ名
                        var p = response.includes?.users?.get(0)?.profile_image_url//投稿者のプロフィール画像url
                        val id = response.includes?.tweets?.get(0)?.author_id//元ツイートの投稿者のid
                        Log.d("info","id is $id, text is $t")
                        /*
                        取得したデータのnull判定
                         */
                        if(t != null){
                            if(t.length > 2 && t.substring(0,3) == "RT "){//表示ツイートがリツイートかどうか判定
                                Log.d("info","there is retweet")
//                                try{
//                                    val response2 = api.fetchProfile(//userの情報を取得するAPIを使用(うまく実行できず)
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
                        val defUrl = imageList?.get(0)?.url
                        var match = "aaaaa"
                        if(defUrl != null)match = defUrl.substring(28,32)//ツイートのurlに含まれるidを取得
                        for(it in 0..imageViewList.size-1){//一旦画像をリセット(これをやらないと前の検索結果のデータが残ってしまう)
                            Picasso.get()
                                .load("https://www.shoshinsha-design.com/wp-content/uploads/2020/noimage-760x460.png")//何も表示しないためにあえて存在しないurlになっています
                                .into(imageViewList[it])
                        }
                        val size = min(imageList?.size ?: 0,imageViewList.size)
                        for(it in 0..size-1){
                            val imageUrl = imageList?.get(it)?.url
                            Log.d("info","image url $it is $imageUrl")
                            if(imageUrl != null){
                                Log.d("info","def:$defUrl this:${imageUrl.substring(28,32)}")
                                if(imageUrl.substring(28,32) != match){//一致しなけれbば同一ツイートではないとみなす
                                    break
                                }
                                else {
                                    Picasso.get()
                                        .load(imageUrl)
                                        .into(imageViewList[it])
                                }
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

    @GET("tweets/search/recent")//最新のツイートを検索
    fun fetchTweets(
        @Header("Authorization") accessToken: String,
        @Query("query") searchWord: String? = null,
        @Query("expansions")attach: String = "attachments.media_keys,author_id,referenced_tweets.id",
        @Query("media.fields")media: String = "url",
        @Query("user.fields")user: String = "entities,profile_image_url,name",
        @Query("tweet.fields")originalId: String = "author_id",
    ):Call<TweetData>

    @GET("users")//userの情報を検索
    fun fetchProfile(
        @Header("Authorization") accessToken: String,
        @Query("ids")id: String
//        @Query("user.fields") profile: String = "profile_image_url,name",
    ):Call<UserData>
}