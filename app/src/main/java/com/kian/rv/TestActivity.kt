package com.kian.rv

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.lang.String
import kotlin.Boolean
import kotlin.Exception
import kotlin.Int
import kotlin.arrayOf


class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        //resultLauncher.launch(intent)
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }else{
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            resultLauncher.launch(intent)
        }
    }















    val requestPermissionLauncher =registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            resultLauncher.launch(intent)
        } else {

        }
    }


    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val uri = data?.data

            var cursor: Cursor? = null
            try {

                cursor = contentResolver?.query(uri!!, null, null, null, null)
                cursor?.moveToFirst()
                val phoneIndex: Int = cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)
                var has_phone_no = cursor.getString(phoneIndex)
                if(has_phone_no =="1"){
                    val id = String.valueOf(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)))
                    val phoneCursor: Cursor? = contentResolver.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = ?", arrayOf(id), null)
                    while (phoneCursor!!.moveToNext()) {
                        val phoneNumber =
                            phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER))
                        val normalizedPhoneNumber =
                            phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NORMALIZED_NUMBER))
                        Log.v("myapp", "phone # - $phoneNumber")
                        Log.v("myapp", "normalized phone # - $normalizedPhoneNumber")
                    }
                }



            } catch (e: Exception) {
                e.printStackTrace()
            }
            cursor?.close()
        }
    }
}