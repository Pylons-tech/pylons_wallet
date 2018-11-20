package com.company.product.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private fun processIntentFromClient (data: Intent) {
        var isAMessage = data.getBooleanExtra("isAMessage", false)
        var tv = findViewById<android.widget.TextView>(R.id.testTextViewWhatsit)
        if (isAMessage) tv.text = getString(R.string.is_a_message)
        else tv.text = getString(R.string.not_a_message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        processIntentFromClient(intent)
        fab.setOnClickListener { finish()
        }
    }

    override fun finish() {

        var d = Intent()
        setResult(Activity.RESULT_OK, d)
        d.putExtra("info", "it works i guess and also this is from the ui")
        super.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
