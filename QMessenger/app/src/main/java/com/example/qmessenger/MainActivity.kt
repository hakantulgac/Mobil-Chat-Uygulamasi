package com.example.qmessenger

import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onBackPressed() {
        var exit = AlertDialog.Builder(applicationContext)
        exit.setMessage("Çıkış?")
        exit.setPositiveButton("Evet",DialogInterface.OnClickListener { dialog, which ->
            System.exit(-1)
        })
        exit.setNegativeButton("İptal",null)
    }
}