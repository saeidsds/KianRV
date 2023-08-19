package com.kian.rv

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.kian.helper.MultiplatformHelper
import java.util.*


class SettingActivity : BaseActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""


        if(sp!!.getBoolean("fingerprint",false))
        {
            findViewById<View>(R.id.fingerpring_iv).visibility = View.VISIBLE

        }else{
            findViewById<View>(R.id.fingerpring_iv).visibility = View.GONE
        }




        findViewById<View>(R.id.back_btn).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<View>(R.id.change_password).setOnClickListener {
            startActivity(Intent(ctx,PasswordActivity::class.java))
        }

        findViewById<View>(R.id.fingerprint).setOnClickListener {
            check_biometrics()
        }


    }



    public fun check_biometrics()
    {
        val biometricManager = BiometricManager.from(ctx!!)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
            {
                if(sp!!.getBoolean("fingerprint",false))
                {
                    MultiplatformHelper.show_msg(getString(R.string.fingerprint_deactivated),"red",findViewById(R.id.fingerprint),ctx)
                    findViewById<View>(R.id.fingerpring_iv).visibility = View.GONE
                    spe!!.putBoolean("fingerprint",false).apply()
                }else{
                    MultiplatformHelper.show_msg(getString(R.string.fingerprint_activated),"green",findViewById(R.id.fingerprint),ctx)
                    findViewById<View>(R.id.fingerpring_iv).visibility = View.VISIBLE
                    spe!!.putBoolean("fingerprint",true).apply()
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                MultiplatformHelper.show_msg(getString(R.string.no_hardware),"red",findViewById(R.id.fingerprint),ctx)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                MultiplatformHelper.show_msg(getString(R.string.no_hardware),"red",findViewById(R.id.fingerprint),ctx)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                MultiplatformHelper.show_toast(getString(R.string.error_fingerprint_device),"red",ctx)
                startActivity(enrollIntent)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if(sp!!.getBoolean("fingerprint_dialog",false))
        {
            if(MultiplatformHelper.check_fingerpring(ctx!!) && !sp!!.getBoolean("fingerprint",false))
            if(MultiplatformHelper.check_fingerpring(ctx!!))
                show_fingerprint_dialog()
        }
    }


    fun show_fingerprint_dialog() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    spe!!.putBoolean("fingerprint",true).apply()
                    findViewById<View>(R.id.fingerpring_iv).visibility = View.VISIBLE
                }
                DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
            }
        }
        val builder = AlertDialog.Builder(ctx!!)
        builder.setMessage(getString(R.string.set_fingerprint))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener)
        val d: Dialog = builder.create()
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.show()
        try {
            (d as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(ctx!!, R.color.kian7))
            d.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(ctx!!, R.color.kian7))
            d.getButton(AlertDialog.BUTTON_NEUTRAL)
                .setTextColor(ContextCompat.getColor(ctx!!, R.color.kian7))
        } catch (e: Exception) {
        }
    }

}