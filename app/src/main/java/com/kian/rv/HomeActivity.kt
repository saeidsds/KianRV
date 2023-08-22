package com.kian.rv

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
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
import androidx.biometric.BiometricPrompt
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.kian.helper.MultiplatformHelper
import com.kian.helper.passcode.PassCodeView
import com.permissionx.guolindev.PermissionX
import java.util.*
import java.util.concurrent.Executor


class HomeActivity : BaseActivity() {




    var action_cnt_array: MutableMap<String, CardView> = HashMap()
    var plus_array: MutableMap<String, TextView> = HashMap()
    var minus_array: MutableMap<String, TextView> = HashMap()
    var input_array: MutableMap<String, EditText> = HashMap()
    var d_text_array  = arrayOf("230","231","232","233","234","235","236","237","238","239","240","246");
    var checkbox_array: MutableMap<String, LinkedList<AppCompatCheckBox>> = HashMap()
    var data_array: MutableMap<String,String> = HashMap()
    lateinit var number_et : EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""


        // =========== init views
        resources.getIdentifier("titleText", "id", packageName)
        d_text_array.forEach {
            init_text_input(it)
        }
        init_checkboxe("241",3,true)
        init_checkboxe("244",3)
        init_checkboxe("245",2)
        init_checkboxe("247",2,true)
        init_checkboxe("243",10)
        init_checkboxe("242",10)
        number_et = findViewById(R.id.number_et)

        findViewById<View>(R.id.add_btn).setOnClickListener {
            PermissionX.init(HomeActivity@this)
                .permissions(android.Manifest.permission.READ_CONTACTS)
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                        contact_result.launch(intent)
                    } else {
                        MultiplatformHelper.show_msg(getString(R.string.error_contact_permission),"red",findViewById<View>(R.id.add_btn),HomeActivity@this)
                    }
                }
        }


        findViewById<View>(R.id.setting_btn).setOnClickListener {
            startActivity(Intent(ctx,SettingActivity::class.java))
        }


        findViewById<View>(R.id.tab1_btn).setOnClickListener {
            active_tab(1)
        }
        findViewById<View>(R.id.tab2_btn).setOnClickListener {
            active_tab(2)
        }
        active_tab(1)




        number_et.setText(sp?.getString("number",""))
        number_et.addTextChangedListener {
            spe?.putString("number",number_et.text.toString())?.apply()
        }
        set_current_state()

        (application as App).get_receiver()?.setListener(object : SmsBroadcastReceiver.SMSMessageListener{
            override fun onTextReceived(text: String?) {
                var text = text?:""
                var test = text.split("\n")
                if(test.size>=20){
                    data_array = parse_str_data(text)
                    spe?.putString("last_data",text)?.apply()
                    set_data()
                    show_read_dialog()
                    MultiplatformHelper.show_msg(getString(R.string.read_complete),"green",findViewById<View>(R.id.add_btn),ctx!!)
                }
                else if(text.lowercase().contains("write ok") || text.lowercase().contains("done")){
                    var current_state = sp?.getString("current_state","none")
                    spe?.putString("current_state","none")?.apply()
                    MultiplatformHelper.show_msg(getString(R.string.write_complete),"green",findViewById<View>(R.id.add_btn),ctx!!)
                    set_current_state()
                }
            }
        })

        data_array = parse_str_data(sp?.getString("last_data","")!!)
        set_data()








        findViewById<View>(R.id.send_btn).setOnClickListener {
            PermissionX.init(HomeActivity@this)
                .permissions(android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.READ_SMS, android.Manifest.permission.SEND_SMS)
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        var number = number_et.text.toString()
                        if(!check_number(number)) return@request
                        var data = make_str_data()
                        val smsManager:SmsManager=if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
                            ctx!!.getSystemService(SmsManager::class.java)
                        }
                        else {
                            SmsManager.getDefault()
                        }
                        smsManager.sendTextMessage(number, null, "Write\n" +data, null, null)
                        spe?.putString("current_state","wait1")?.apply()
                        spe?.putString("last_data",data)?.apply()
                        set_current_state()
                        MultiplatformHelper.show_msg(getString(R.string.request_sent),"green",findViewById<View>(R.id.add_btn),HomeActivity@this)

                    } else {
                        MultiplatformHelper.show_msg(getString(R.string.error_sms_permission),"red",findViewById<View>(R.id.add_btn),HomeActivity@this)
                    }
                }
        }


        findViewById<View>(R.id.receive_btn).setOnClickListener {
            PermissionX.init(HomeActivity@this)
                .permissions(android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.READ_SMS, android.Manifest.permission.SEND_SMS)
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        var number = number_et.text.toString()
                        if(!check_number(number)) return@request

                        val smsManager:SmsManager=if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
                                ctx!!.getSystemService(SmsManager::class.java)
                            }
                        else {
                            SmsManager.getDefault()
                        }
                        smsManager.sendTextMessage(number, null, "Read\n" +
                                "D230\n" +
                                "D250", null, null)
                        MultiplatformHelper.show_msg(getString(R.string.request_sent),"green",findViewById<View>(R.id.add_btn),HomeActivity@this)
                    } else {
                        MultiplatformHelper.show_msg(getString(R.string.error_sms_permission),"red",findViewById<View>(R.id.add_btn),HomeActivity@this)
                    }
                }
        }






        findViewById<View>(R.id.cancel_btn).setOnClickListener {
            spe?.putString("current_state","none")?.apply()
            set_current_state()
        }



        // =================== password==============================
        // =================== password==============================
        // =================== password==============================
        // =================== password==============================


        if(sp!!.getString("password","")!=""){
            findViewById<View>(R.id.password_cnt).visibility= View.VISIBLE
            if(sp!!.getBoolean("fingerprint",false) && MultiplatformHelper.check_fingerpring(this)){
                authenticate_biometrics()
            }
            findViewById<PassCodeView>(R.id.pass_code_view).setOnTextChangeListener {
                if (it.length === 4) {
                    if (sp!!.getString("password","") == it) {
                        findViewById<View>(R.id.password_cnt).visibility= View.GONE
                    }
                    else{
                        findViewById<PassCodeView>(R.id.pass_code_view).setError(true)
                    }
                }
            }
        }

        findViewById<View>(R.id.exit_btn).setOnClickListener {
            finishAffinity()
        }

        if(sp!!.getBoolean("first",true)){
            spe?.putBoolean("first",false)?.apply()
            startActivity(Intent(ctx,PasswordActivity::class.java))
        }


    }





    fun parse_str_data(str :String):MutableMap<String,String>{
        var resutl:MutableMap<String,String> =HashMap()
        var str = str.replace("D230\n","")
        str = str.replace("Read\n","")
        var array =str.split("\n")
        array.forEachIndexed{index,str ->
            resutl[(230+index).toString()]=str
        }
        return resutl
    }


    fun make_str_data():String{

        var str = "D230\n"
        var data =get_data()
        for (i in 230 .. 250)
        {
            if(i==250)
                str = str+data[i.toString()]
            else
                str = str+data[i.toString()]+"\n"
        }

        return str

    }


    fun get_data():MutableMap<String,String>{
        var result:MutableMap<String,String> =HashMap()
        for (i in 230 .. 250)
            result[""+i] = "0000"
        d_text_array.forEach {
            try {
                result[it] = input_array[it]?.text.toString()
                if(result[it] =="" || result[it]=="0")
                    result[it] = "0000"
                if(result[it]!!.length<4)
                    result[it] = "%04d".format(input_array[it]?.text.toString().toInt())
                else if(result[it]!!.length>4)
                    result[it] = input_array[it]?.text.toString().substring(input_array[it]?.text.toString().length-4,input_array[it]?.text.toString().length)
            }catch (e:Exception){}
        }


        result["241"] = get_checkbox_data("241")
        result["244"] = get_checkbox_data("244")
        result["245"] = get_checkbox_data("245")
        result["247"] = get_checkbox_data("247")
        result["243"] = get_checkbox_data("243")
        result["242"] = get_checkbox_data("242")

        return result
    }



    fun set_data(){
        d_text_array.forEach {
            try {
                if(data_array[it]!="")
                    input_array[it]?.setText(data_array[it])
                else
                    input_array[it]?.setText("0")
            }catch (e:Exception){}
        }


        set_checkbox_data("241")
        set_checkbox_data("244")
        set_checkbox_data("245")
        set_checkbox_data("247")
        set_checkbox_data("243")
        set_checkbox_data("242")
    }


    fun get_checkbox_data(id:String):String{
        var result = "0000"
        try {
            var binary_value = ""
            var list  = checkbox_array[id]
            list?.forEachIndexed{index,checkbox ->
                if(checkbox.isChecked)
                    binary_value = "1"+binary_value
                else
                    binary_value = "0"+binary_value

            }
            result = binary_value.toInt(2).toString()
            if(result.length<4){
                result = "%04d".format(result.toInt())
            }
        }catch (e:Exception){}

        return result
    }

    fun set_checkbox_data(id:String){
        try {
            var value = data_array[id]

            var binary_value = Integer.toBinaryString(value!!.toInt())
            if(binary_value.length<10){
                var x = 10-binary_value.length
                for (i in 0 until x)
                {
                    binary_value = "0"+binary_value
                }
            }
            else if(binary_value.length>10){
                binary_value = binary_value.substring(binary_value.length-10,binary_value.length)
            }

            var list  = checkbox_array[id]
            list?.forEachIndexed{index,checkbox ->
                checkbox.isChecked = binary_value.substring(binary_value.length-index-1,binary_value.length-index) =="1"
            }
        }catch (e:Exception){}

    }


    @SuppressLint("Range")
    var contact_result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
                    val id = java.lang.String.valueOf(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)))
                    val phoneCursor: Cursor? = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
                    while (phoneCursor!!.moveToNext()) {
                        val phoneNumber =
                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        val normalizedPhoneNumber =
                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER))
                        //Log.v("myapp", "phone # - $phoneNumber")
                        //Log.v("myapp", "normalized phone # - $normalizedPhoneNumber")
                        number_et.setText(phoneNumber)
                    }
                }



            } catch (e: Exception) {
                e.printStackTrace()
            }
            cursor?.close()
        }
    }




    fun  check_number(number :String):Boolean{
        var number = number
        if(number.startsWith("+98"))
            number = number.replaceFirst("+98","0")
        if(number=="")
        {
            MultiplatformHelper.show_msg(getString(R.string.error_no_number),"red",findViewById<View>(R.id.add_btn),HomeActivity@this)
            return false
        }
        if(number.length!=11 )
        {
            MultiplatformHelper.show_msg(getString(R.string.error_number),"red",findViewById<View>(R.id.add_btn),HomeActivity@this)
            return false
        }

        return true
    }


    fun set_current_state(){
        var current_state = sp?.getString("current_state","none").toString()
        if(current_state =="none"){
            findViewById<View>(R.id.waiting_cnt).visibility = View.GONE
            findViewById<View>(R.id.wait_btn).visibility = View.GONE
            findViewById<View>(R.id.cancel_btn).visibility = View.GONE
            findViewById<TextView>(R.id.wait_tv).text = getString(R.string.wait_for_response)
            findViewById<TextView>(R.id.wait_tv2).text = getString(R.string.wait_for_response)

            findViewById<View>(R.id.send_btn).visibility = View.VISIBLE
            findViewById<View>(R.id.receive_btn).visibility = View.VISIBLE
        }
        else if(current_state =="wait1"){
            findViewById<View>(R.id.waiting_cnt).visibility = View.VISIBLE
            findViewById<View>(R.id.wait_btn).visibility = View.VISIBLE
            findViewById<View>(R.id.cancel_btn).visibility = View.VISIBLE
            findViewById<TextView>(R.id.wait_tv).text = getString(R.string.wait_for_response1)
            findViewById<TextView>(R.id.wait_tv2).text = getString(R.string.wait_for_response1)

            findViewById<View>(R.id.send_btn).visibility = View.GONE
            findViewById<View>(R.id.receive_btn).visibility = View.GONE
        }
        else if(current_state =="wait2"){
            findViewById<View>(R.id.waiting_cnt).visibility = View.VISIBLE
            findViewById<View>(R.id.wait_btn).visibility = View.VISIBLE
            findViewById<View>(R.id.cancel_btn).visibility = View.VISIBLE
            findViewById<TextView>(R.id.wait_tv).text = getString(R.string.wait_for_response2)
            findViewById<TextView>(R.id.wait_tv2).text = getString(R.string.wait_for_response2)

            findViewById<View>(R.id.send_btn).visibility = View.GONE
            findViewById<View>(R.id.receive_btn).visibility = View.GONE
        }
    }

    fun active_tab(num: Int){
        if(num==1){
            findViewById<View>(R.id.tab1_cnt).visibility = View.VISIBLE
            findViewById<View>(R.id.tab2_cnt).visibility = View.GONE

            findViewById<CardView>(R.id.tab1_btn).setCardBackgroundColor(ContextCompat.getColor(this,R.color.kian7))
            findViewById<TextView>(R.id.tab1_tv).setTextColor(ContextCompat.getColor(this,R.color.kian1))

            findViewById<CardView>(R.id.tab2_btn).setCardBackgroundColor(ContextCompat.getColor(this,R.color.kian12))
            findViewById<TextView>(R.id.tab2_tv).setTextColor(ContextCompat.getColor(this,R.color.kian4))
        }
        else{

            findViewById<View>(R.id.tab2_cnt).visibility = View.VISIBLE
            findViewById<View>(R.id.tab1_cnt).visibility = View.GONE

            findViewById<CardView>(R.id.tab2_btn).setCardBackgroundColor(ContextCompat.getColor(this,R.color.kian7))
            findViewById<TextView>(R.id.tab2_tv).setTextColor(ContextCompat.getColor(this,R.color.kian1))

            findViewById<CardView>(R.id.tab1_btn).setCardBackgroundColor(ContextCompat.getColor(this,R.color.kian12))
            findViewById<TextView>(R.id.tab1_tv).setTextColor(ContextCompat.getColor(this,R.color.kian4))
        }
    }





    fun init_text_input(id: String){

        var action_cnt : CardView = findViewById(resources.getIdentifier("action_cnt"+id, "id", packageName))
        var plus : TextView = findViewById(resources.getIdentifier("plus"+id, "id", packageName))
        var minus : TextView = findViewById(resources.getIdentifier("minus"+id, "id", packageName))
        var input : EditText = findViewById(resources.getIdentifier("input"+id, "id", packageName))

        plus.setOnClickListener{
            try {
                var value =0
                value = input.text.toString().toInt()
                value++
                input.setText(value.toString())
            }catch (e:java.lang.Exception){}
        }

        minus.setOnClickListener{
            try {
                var value =0
                value = input.text.toString().toInt()
                value--
                if(value<0)
                    value = 0
                input.setText(value.toString())
            }catch (e:java.lang.Exception){}
        }

        action_cnt_array[id]=action_cnt
        plus_array[id]=plus
        minus_array[id]=minus
        input_array[id]=input
    }

    fun init_checkboxe(id: String, count: Int, single_select: Boolean = false){

        var temp_array : LinkedList<AppCompatCheckBox> = LinkedList<AppCompatCheckBox>()
        for(i in 1 .. count){
            var container : RelativeLayout = findViewById(resources.getIdentifier("checkbox_cnt"+id+i, "id", packageName))
            var cb : AppCompatCheckBox = findViewById(resources.getIdentifier("cb"+id+i, "id", packageName))
            cb.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    if(single_select){
                        for(j in 1 .. count){
                            if(j!=i){
                                var temp_cb : AppCompatCheckBox = findViewById(resources.getIdentifier("cb"+id+j, "id", packageName))
                                temp_cb.isChecked = false
                            }
                        }
                    }
                    container.setBackgroundResource(R.drawable.cb_checked)
                }else{
                    container.setBackgroundResource(R.drawable.cb_not_checked)
                }
            }
            container.setOnClickListener{
                cb.isChecked = !cb.isChecked
            }
            temp_array.add(cb)
        }
        checkbox_array[id] = temp_array
    }



    // =================== password==============================
    // =================== password==============================
    // =================== password==============================
    // =================== password==============================








    override fun onResume() {
        super.onResume()
        if(sp!!.getBoolean("fingerprint_dialog",false))
        {
            spe!!.putBoolean("fingerprint_dialog",false).apply()
            if(MultiplatformHelper.check_fingerpring(ctx!!) && !sp!!.getBoolean("fingerprint",false))
                show_fingerprint_dialog()
        }
    }


    fun show_fingerprint_dialog() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    spe!!.putBoolean("fingerprint",true).apply()
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


    fun show_read_dialog() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    dialog.dismiss()
                }
                DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
            }
        }
        val builder = AlertDialog.Builder(ctx!!)
        builder.setMessage(getString(R.string.read_complete))
            .setPositiveButton(getString(R.string.accept), dialogClickListener)
            //.setNegativeButton(getString(R.string.exit), dialogClickListener)
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




    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo


    public fun authenticate_biometrics()
    {
        var executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    MultiplatformHelper.show_msg(getString(R.string.error_faild_biometric),"red",number_et,this@HomeActivity)
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    findViewById<View>(R.id.password_cnt).visibility = View.GONE
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    MultiplatformHelper.show_msg(getString(R.string.error_login_biometric),"red",number_et,this@HomeActivity)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.enter))
            //.setSubtitle(getString(R.string.login_biometric))
            .setNegativeButtonText(getString(R.string.exit))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}