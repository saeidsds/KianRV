package com.kian.rv

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.kian.helper.MultiplatformHelper
import com.kian.helper.passcode.PassCodeView


class PasswordActivity : BaseActivity() {


    lateinit var pass_view:PassCodeView
    var current_state = ""
    var pass_str = "x"
    lateinit var pass_tv:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        findViewById<View>(R.id.back_btn).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        pass_view = findViewById(R.id.pass_code_view)
        pass_tv = findViewById(R.id.pass_tv)
        pass_tv.text = getString(R.string.password_desc1)
        pass_view.setOnTextChangeListener {
            if (it.length === 4) {
                if (current_state=="") {
                    pass_str = it
                    current_state="verify"
                    pass_tv.text = getString(R.string.password_desc2)
                    pass_view.reset()
                }
                else if (current_state=="verify" && pass_str == it) {
                    pass_str = it
                    spe!!.putString("password",pass_str).apply()
                    spe!!.putBoolean("fingerprint_dialog",true).apply()
                    MultiplatformHelper.show_toast(getString(R.string.password_set_complete),"greed",ctx)
                    finish()
                }
                else{
                    pass_view.setError(true)
                    MultiplatformHelper.show_msg(getString(R.string.password_error),"red",pass_view,ctx)
                }
            }
        }

        findViewById<View>(R.id.cancel_btn).setOnClickListener {
            if (current_state=="") {
                onBackPressedDispatcher.onBackPressed()
            }
            else if (current_state=="verify") {
                current_state=""
                pass_str = "x"
                pass_tv.text = getString(R.string.password_desc1)
                pass_view.reset()
            }

        }



    }



}