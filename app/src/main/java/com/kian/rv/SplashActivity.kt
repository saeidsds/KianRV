package com.kian.rv

import android.R.attr.level
import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation.AnimationListener
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat


class SplashActivity : BaseActivity() {




    lateinit var logo_iv:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.kian7)


        logo_iv = findViewById<ImageView>(R.id.logo_iv)
        findViewById<TextView>(R.id.version_tv).setText("Version "+packageManager.getPackageInfo(packageName, 0).versionName)
        supportActionBar?.hide()

        var handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                goto_next()
            }
        }
        //handler.sendEmptyMessageDelayed(0,1000)

    }






    fun goto_next(){
        startActivity(Intent(ctx,HomeActivity::class.java))
        finish()
    }


    var begin = 0
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        begin++
        if (begin <= 1) {
            if (logo_iv != null) {
                logo_iv.setVisibility(View.VISIBLE)
                var anim = ObjectAnimator.ofFloat(logo_iv, "alpha", 0f, 1f)
                anim.setDuration(100)

                val animator = ObjectAnimator.ofFloat(logo_iv, "alpha", 0f, 1f)
                animator.apply {
                    duration = 1000
                    startDelay = 0
                    addListener(onEnd = {
                        goto_next()
                    })
                    //AccelerateDecelerateInterpolator()
                    start()
                }

            }

        }
    }







    override fun onBackPressed() {

    }
}