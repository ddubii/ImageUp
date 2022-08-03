package com.hb.imageup

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val TAG = "TAG_MainActivity"//로그를 분류할 태그입니다.
    lateinit var mCallTodoList : retrofit2.Call<JsonObject>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//         val sendBtn = findViewById<Button>(R.id.sendImage)
//         val getBtn = findViewById<Button>(R.id.button1)

        sendBtn.setOnClickListener{
            getProfileImage()
        }

        getBtn.setOnClickListener {
            getBtn.visibility = View.INVISIBLE
            callTodoList()

        }
    }

    private fun callTodoList() {
        mCallTodoList = RetrofitSetting.createBaseService(RetrofitPath::class.java).getTodoList() // RetrofitAPI에서 Json객체 요청을 반환하는 메서드를 불러옵니다.
        mCallTodoList.enqueue(mRetrofitCallback) // 콜백, 즉 응답들을 큐에 넣어 대기시켜놓습니다. 응답이 생기면 뱉어내는거죠.
    }

    //http요청을 보냈고 이건 응답을 받을 콜벡메서드
    private val mRetrofitCallback  = (object : retrofit2.Callback<JsonObject>{
        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            t.printStackTrace()
            Log.d(TAG, "에러입니다. => ${t.message.toString()}")
            textView.text = "에러\n" + t.message.toString()

            getBtn.visibility = View.VISIBLE
        }

        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
            val result = response.body()
            Log.d(TAG, "결과는 => $result")


            var mGson = Gson()
           // val dataParsed1 = mGson.fromJson(result, DataModel.TodoInfo1::class.java)
           // val dataParsed2 = mGson.fromJson(result, DataModel.TodoInfo2::class.java)
//            textView.text = "해야할 일\n" +dataParsed1.result_caption_str.task+"\n"+dataParsed2.result_caption_sub.task
            textView.text = "결과는 => $result"
            getBtn.visibility = View.VISIBLE
        }
    })


    var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imagePath = result.data!!.data

            val file = File(absolutelyPath(imagePath, this))
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            val body = MultipartBody.Part.createFormData("proFile", file.name, requestFile)

            Log.d(TAG,file.name)

            var exif : ExifInterface? = null
            try{
                exif = ExifInterface(file.absolutePath)}
            catch (e : IOException){
                e.printStackTrace()}
            val filename = file.name
            val dateTime = exif?.getAttribute(ExifInterface.TAG_DATETIME)
            val latitude = exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
            val longitude = exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)

            Log.d("ExifData", "File Name : $filename")
            Log.d("ExifData", "dateTime : $dateTime")
            Log.d("ExifData", "latitude : $latitude")
            Log.d("ExifData", "longitude : $longitude")

            sendImage(body)
        }
    }
    fun getProfileImage(){
        Log.d(TAG,"사진변경 호출")
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        chooserIntent.putExtra(Intent.EXTRA_INTENT, intent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE,"사용할 앱을 선택해주세요.")
        launcher.launch(chooserIntent)
    }

    fun absolutelyPath(path: Uri?, context : Context): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)

        return result!!
    }

    fun sendImage(image : MultipartBody.Part) {
        val service = RetrofitSetting.createBaseService(RetrofitPath::class.java) //레트로핏 통신 설정
        val call = service.profileSend(image)!! //통신 API 패스 설정

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful) {
                    Log.d("로그 ",""+response?.body().toString())
                    Toast.makeText(applicationContext,"통신성공",Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(applicationContext,"통신실패",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("로그 ",t.message.toString())
            }
        })
    }

}