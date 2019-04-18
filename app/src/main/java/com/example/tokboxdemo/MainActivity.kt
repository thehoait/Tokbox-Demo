package com.example.tokboxdemo

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.opentok.android.*
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), Session.SessionListener, PublisherKit.PublisherListener {
    override fun onStreamCreated(publisherKit: PublisherKit, stream: Stream) {
        Log.i(LOG_TAG, "Publisher onStreamCreated")
    }

    override fun onStreamDestroyed(publisherKit: PublisherKit, stream: Stream) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed")
    }

    override fun onError(publisherKit: PublisherKit, opentokError: OpentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.message)
    }

    override fun onConnected(session: Session) {
        Log.i(LOG_TAG, "Session Connected")
        publisher = Publisher.Builder(this)
            .videoTrack(false).build()
        publisher.setPublisherListener(this)
        publisher_container.addView(publisher.view)
        session.publish(publisher)
    }

    override fun onDisconnected(session: Session) {
        Log.i(LOG_TAG, "Session Disconnected")
    }

    override fun onStreamReceived(session: Session, stream: Stream) {
        Log.i(LOG_TAG, "Stream Received")
        if (subscriber == null) {
            subscriber = Subscriber.Builder(this, stream)
                .build()
            session.subscribe(subscriber)
            subscriber_container.addView(subscriber?.view)
        }
    }

    override fun onStreamDropped(session: Session, stream: Stream) {
        Log.i(LOG_TAG, "Stream Dropped")
        if (subscriber != null) {
            subscriber = null
            subscriber_container.removeAllViews()
        }
    }

    override fun onError(session: Session, opentokError: OpentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.message)
    }

    companion object {
        private const val API_KEY = "46311262"
        private const val SESSION_ID = "2_MX40NjMxMTI2Mn5-MTU1NTU1NTg1MTAyN345R1dibEk0K21ocHJTUXRDaUdXMWRnR2J-fg"
        private const val TOKEN =
            "T1==cGFydG5lcl9pZD00NjMxMTI2MiZzaWc9ZDg5MTZmMTk4YjE1YjEyMzE3MTRmYmQ4OWQwMTkxYjVmNTBkY2M4MTpzZXNzaW9uX2lkPTJfTVg0ME5qTXhNVEkyTW41LU1UVTFOVFUxTlRnMU1UQXlOMzQ1UjFkaWJFazBLMjFvY0hKVFVYUkRhVWRYTVdSblIySi1mZyZjcmVhdGVfdGltZT0xNTU1NTU1ODg2Jm5vbmNlPTAuMzM4MzA3OTMxNTAwMzU1NjUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTU1NTY0MjI4NiZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ=="
        private const val RC_SETTINGS_SCREEN_PERM = 123
        private const val RC_VIDEO_APP_PERM = 124
        private val LOG_TAG = MainActivity::class.java.simpleName
    }

    private lateinit var session: Session
    private lateinit var publisher: Publisher
    private var subscriber: Subscriber? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private fun requestPermissions() {
        val perms = arrayOf(Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // initialize view objects from your layout


            // initialize and connect to the session
            session = Session.Builder(this, API_KEY, SESSION_ID).build()
            session.setSessionListener(this)
            session.connect(TOKEN)

        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera and mic to make video calls",
                RC_VIDEO_APP_PERM,
                *perms
            )
        }
    }
}
