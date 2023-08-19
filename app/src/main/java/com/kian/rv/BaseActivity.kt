package com.kian.rv

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    var ctx : Context? = this
    var sp: SharedPreferences? = null
    var spe: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = getSharedPreferences("init", MODE_PRIVATE)
        spe = sp?.edit()


    }




    fun getTopFragmentTag(): String? {
        return if (supportFragmentManager.backStackEntryCount === 0) {
            ""
        } else supportFragmentManager.getBackStackEntryAt(
            supportFragmentManager.backStackEntryCount - 1
        ).name
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            for (fragment in supportFragmentManager.fragments) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }catch (e:Exception){}

    }


}