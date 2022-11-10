package com.example.backgroundservice

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.backgroundservice.databinding.ActivitySecondBinding


class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding
    private lateinit var mService: TimerService
    private var mBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chronometer.text = "00:00:00"

        binding.btnStartPause.setOnClickListener {
            if (!mBound) {
                bindLocalService()
                startTimerService()
            }
        }

        binding.btnReset.setOnClickListener {
            if (mBound) {
                binding.chronometer.text = "00:00:00"
                unbindService(connection)
                mBound = false

                stopTimerService()
            }
        }
    }


 private   fun startTimerService() {
        val serviceIntent = Intent(this, TimerService::class.java)
        //serviceIntent.putExtra("inputExtra", message)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

   private fun stopTimerService() {
        val serviceIntent = Intent(this, TimerService::class.java)
        stopService(serviceIntent)
    }

    override fun onStart() {
        super.onStart()
        bindLocalService()
    }

    private fun bindLocalService() {
        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, connection, 0)
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.LocalBinder
            mService = binder.getService()
            mBound = true

            mService.elapsedSeconds.observe(this@SecondActivity, androidx.lifecycle.Observer {
                binding.chronometer.text = formatStopWatchTime(it)
            })
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

}