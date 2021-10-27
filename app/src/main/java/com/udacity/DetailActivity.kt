package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val filename = intent.getStringExtra(DOWNLOAD_FILE)
        val status = intent.getBooleanExtra(DOWNLOAD_STATUS, false)

        if(status) {
            textStatus.setTextColor(Color.GREEN)
            textStatus.text = getString(R.string.success)
        } else {
            textStatus.setTextColor(Color.RED)
            textStatus.text = getString(R.string.fail)
        }

        textFilename.text = filename
        backButton.setOnClickListener{
            val notificationManager =
                this.getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.cancelNotifications()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
