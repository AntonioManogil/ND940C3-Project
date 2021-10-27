package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

/* bibliography
    https://classroom.udacity.com/nanodegrees/nd940/parts/23bb2f5a-fe75-45e2-a108-212ab2b195c1
    https://developer.android.com/codelabs/advanced-android-kotlin-training-custom-views
    https://github.com/catarinaccmateus/ND940C3-Project
    https://github.com/Zukka/ND940C3-Project
    https://alvinalexander.com/android/android-method-center-text-font-canvas-drawtext/
    https://camposha.info/android-examples/android-downloadmanager/
    https://stackoverflow.com/questions/3257293/measuring-text-width-to-be-drawn-on-canvas-android
    https://johncodeos.com/how-to-download-image-from-the-web-in-android-using-kotlin/
    https://developer.android.com/codelabs/advanced-android-kotlin-training-notifications
    https://developer.android.com/guide/components/broadcasts
 */


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var url = ""
    private var filename = ""

    private lateinit var downloadManager: DownloadManager
    private lateinit var notificationManager: NotificationManager

    private val _downloading = MutableLiveData<Boolean>()
    private val downloading: LiveData<Boolean> get() = _downloading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_loadapp -> {
                    url = APP_URL
                    filename = rb_loadapp.text.toString()
                }
                R.id.rb_glide -> {
                    url = GLIDE_URL
                    filename = rb_glide.text.toString()
                }
                R.id.rb_retrofit -> {
                    url = RETROFIT_URL
                    filename = rb_retrofit.text.toString()
                }
            }
        }

        custom_button.setOnClickListener {
            if (url.isNotEmpty()) {
                custom_button.selectedOption()
                radioGroupEnabling(false)
                download()
            } else {
                Toast.makeText(this, getString(R.string.SelectOption), Toast.LENGTH_SHORT).show()
            }
        }
        downloading.observe(this, {
            if (!it) {
                custom_button.downLoadCompleted()
                radioGroupEnabling(true)
            }
        })

        createChannel()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                _downloading.postValue(false)
                val query = DownloadManager.Query().setFilterById(downloadID)
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val downloadStatus =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val status = downloadStatus == DownloadManager.STATUS_SUCCESSFUL
                    sendNotification(status)
                }
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        //Thread {
        //    var downLoading = true
        //    var downloadStatus = -1
        //    while (downLoading) {
        //        val cursor: Cursor = downloadManager.query(query)
        //        if (cursor.moveToFirst()) {
        //            do {
        //                downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        //                if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL ||
        //                    downloadStatus == DownloadManager.STATUS_FAILED) {
        //                    downLoading = false
        //                    _downloading.postValue(false)
        //                    break
        //                }
        //                val status =
        //                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        //                msg = statusMessage(url, status)
        //                if (msg != lastMsg) {
        //                    this.runOnUiThread {
        //                        // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        //                    }
        //                    lastMsg = msg ?: ""
        //                }
        //            } while (cursor.moveToNext())
        //        }
        //        cursor.close()
        //    }
        //}.start()
    }

    /*private fun statusMessage(url: String, status: Int): String {
        val msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully " + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's nothing to download"
        }
        return msg
    }*/

    companion object {
        private const val APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/master.zip"
    }

    // Disabling when downloading
    private fun radioGroupEnabling(isEnabled: Boolean) {
        rb_glide.isEnabled = isEnabled
        rb_loadapp.isEnabled = isEnabled
        rb_retrofit.isEnabled = isEnabled
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                getString(R.string.LoadApp_notification_channel_id),
                getString(R.string.LoadApp_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.MAGENTA
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Downloading Files"

            notificationManager =
                this.getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(status: Boolean){
        notificationManager.cancelAll()
        notificationManager.sendNotification(filename, status, this)
    }
}
