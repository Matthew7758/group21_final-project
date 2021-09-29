package com.group21.android.mp

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import android.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var musicList: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        musicList = this.findViewById(R.id.musicList)
        checkPerms()
        Toast.makeText(applicationContext,"Permissions granted!", LENGTH_SHORT).show()
        populateList()
    }

    private fun populateList() {
        TODO("Not yet implemented")
    }

    private fun checkPerms() {
        when {
            ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ->{
                return
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                Toast.makeText(applicationContext,"Permissions required!",LENGTH_SHORT).show()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO), 69)
            }
        }
    }
}