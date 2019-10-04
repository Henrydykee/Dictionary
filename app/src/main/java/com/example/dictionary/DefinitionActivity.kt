package com.example.dictionary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_definition.*

class DefinitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_definition)

        val definition = intent.getStringArrayExtra("myDefinition")
        text_view.text= definition.toString()
    }

    fun backArrow(view: View) {
        finish()
        intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }
}
