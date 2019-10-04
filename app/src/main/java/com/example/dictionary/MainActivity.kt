package com.example.dictionary

import android.content.Intent
import android.icu.util.Output
import android.net.UrlQuerySanitizer
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    fun findWord(view: View) {
        var StringUrl ="https://od-api.oxforddictionaries.com:433/api/v1/entries/en/"+edit_text.text.toString()
        var MyAsynckTask = MyAsynckTask()
        MyAsynckTask.execute(StringUrl)
    }
    inner class MyAsynckTask : AsyncTask<String,Void,Data>(){
        override fun doInBackground(vararg p0: String?): Data? {
            val url = creatUrl(p0[0])

            var jsonResponse :  String?
            try {
                jsonResponse = makeHttpResponse(url)
                val data =  extractFeatureFromJson(jsonResponse)
                return data!!
            }catch (e:IOException){
                Log.e("MainActiviyt","connection error"+e)
            }
          return  null
        }
        override fun onPostExecute(result: Data?) {
            super.onPostExecute(result)

            if (result==null){
                return
            }
            showDefinition(result.definition)
        }
    }

    private fun showDefinition(definition: String?) {
       val intent = Intent(this,DefinitionActivity::class.java)
        intent.putExtra("myDefinition",definition)
        startActivity(intent)
        }

    fun extractFeatureFromJson(definitionjson: String): Data? {
         try {
             val baseJsonResponse = JSONObject(definitionjson)
             val featureResults = baseJsonResponse.getJSONArray("results")
             val firstResult = featureResults.getJSONObject(0)
             val lexicalEntries = firstResult.getJSONArray("lexicalEntries")
             val firstLexicalEntry = lexicalEntries.getJSONObject(0)
             val entries=firstLexicalEntry.getJSONArray("entries")
             val firstEntry = entries.getJSONObject(0)
            val senses = firstEntry.getJSONArray("senses")
             val firstsense =senses.getJSONObject(0)
             val definitions=firstsense.getJSONArray("definitions")
             definitions[0]
             return Data(definitions[0].toString())
         }catch (e:JSONException){
              Log.e("MainActivity","connection error"+e)
         }
         return null
     }

     fun makeHttpResponse(url: URL?): String{
         var jsonResponse =""
         var urlConnection : HttpURLConnection
         var inputStream :InputStream? = null

         try {
             urlConnection= url?.openConnection() as HttpURLConnection
             urlConnection.requestMethod = "GET"
             URLConnection.setDefaultRequestProperty("Accept","Application/json")
             urlConnection.setRequestProperty("app_id","64388a26")
             urlConnection.setRequestProperty("app_key","eb403c7f282ab912c611cfe92ba692b4")
             urlConnection.readTimeout=10000
             urlConnection.connectTimeout=15000
             urlConnection.connect()

             if  (urlConnection.responseCode ==200){
                 inputStream =urlConnection.inputStream
                 jsonResponse  = readFromInputStream(inputStream)
             }else{
                 Log.d("MainActivity","Error Response Code"+urlConnection.responseCode)
             }
             urlConnection.disconnect()
             inputStream?.close()
         }catch (e: IOException){
             Log.e("MainActivity","connection Error"+e)
         }
         return jsonResponse
     }

    private fun readFromInputStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null){
            val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line =reader.readLine()

            while ( line!= null ){
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }


    fun creatUrl(stringurl:String?): URL? {
    var url: URL?

        try {
            url = URL(stringurl)
        }catch (exception:MalformedURLException){
            Log.d("MainActivity","ERROR IN CREATING URL")
            return null
        }
        return url
    }
}
