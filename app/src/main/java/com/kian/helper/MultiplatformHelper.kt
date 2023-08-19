package com.kian.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.kian.rv.R
import es.dmoral.toasty.Toasty
import java.io.*
import java.nio.charset.Charset


class MultiplatformHelper {

    companion object{
        fun base64encode(normal: String): String {
            var encode_str: String = ""
            encode_str = try {
                val data = normal.toByteArray(charset("UTF-8"))
                //byte[] data = normal.getBytes("ISO-8859-1");
                Base64.encodeToString(data, Base64.NO_WRAP)
            } catch (e: Exception) {
                normal
            }
            return encode_str
        }


        fun base64decode(base64: String?): String? {
            var decode_str: String? = ""
            decode_str = try {
                val data = Base64.decode(base64, Base64.NO_WRAP)
                String(data, Charset.forName("UTF-8"))
                //decode_str = new String(data, "ISO-8859-1");
            } catch (e: Exception) {
                base64
            }
            return decode_str
        }


        fun isBase64(str: String?): Boolean {
            return if (TextUtils.isEmpty(str)) false else try {
                Base64.decode(str, Base64.DEFAULT)
                true
            } catch (e: Exception) {
                false
            }
        }



        fun open_url(link: String? , ctx: Context?) {
            try {
                val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                ctx?.startActivity(myIntent)
            } catch (e: java.lang.Exception) {

            }
        }


        fun save_bitmap(
            ctx: Context?,
            src_bitmap: Bitmap,
            dest: String?,
            is_square: Boolean
        ): Boolean {
            val directory = File(get_image_dir(ctx!!))
            if (!directory.exists()) directory.mkdirs()
            val file = File(dest)
            if (file.exists()) {
                file.delete()
            }
            var pic_croped: Bitmap? = null
            ///====================================== make square
            if (is_square) {
                pic_croped = try {
                    if (src_bitmap.width >= src_bitmap.height) {
                        Bitmap.createBitmap(
                            src_bitmap,
                            src_bitmap.width / 2 - src_bitmap.height / 2,
                            0,
                            src_bitmap.height,
                            src_bitmap.height
                        )
                    } else {
                        Bitmap.createBitmap(
                            src_bitmap,
                            0,
                            src_bitmap.height / 2 - src_bitmap.width / 2,
                            src_bitmap.width,
                            src_bitmap.width
                        )
                    }
                } catch (e: java.lang.Exception) {
                    src_bitmap
                }
                if (pic_croped == null) {
                    pic_croped = src_bitmap
                }
            } else {
                pic_croped = src_bitmap
            }
            ///====================================== make square
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(dest)
                pic_croped.compress(
                    Bitmap.CompressFormat.JPEG,
                    65,
                    out
                ) // bmp is your Bitmap instance
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return false
            } finally {
                try {
                    out?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    return false
                }
            }
            return true
        }
        fun get_image_dir(ctx: Context): String? {
            try {
                val directory = File(
                    ctx.getExternalFilesDir(null).toString() + File.separator + "MultiPlatform"
                )
                if (!directory.exists()) directory.mkdirs()
            } catch (e: java.lang.Exception) {
            }
            return ctx.getExternalFilesDir(null).toString() + File.separator + "MultiPlatform"
        }


        fun get_image_path(ctx: Context?): String {
            return ctx?.getExternalFilesDir(null)
                .toString() + File.separator + "MultiPlatform/temp.jpeg"
        }
        fun getRealPathFromURI(ctx: Context?, contentUri: Uri?): String? {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor =
                (ctx as Activity).contentResolver.query(contentUri!!, proj, null, null, null)
            var result: String? = ""
            if (cursor!!.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                result = cursor.getString(column_index)
            }
            cursor.close()
            return result
        }

        fun savefile(ctx: Context?, sourceuri: Uri?) {
            val directory: File? = ctx?.let { MultiplatformHelper.get_image_dir(it)?.let { File(it) } }
            if (!directory?.exists()!!) directory.mkdirs()
            val file: File = File(MultiplatformHelper.get_image_path(ctx))
            if (file.exists()) {
                file.delete()
            }
            val sourceFilename: String? = MultiplatformHelper.getRealPathFromURI(ctx, sourceuri)
            val destinationFilename: String = MultiplatformHelper.get_image_path(ctx)
            var bis: BufferedInputStream? = null
            var bos: BufferedOutputStream? = null
            try {
                bis = BufferedInputStream(FileInputStream(sourceFilename))
                bos = BufferedOutputStream(FileOutputStream(destinationFilename, false))
                val buf = ByteArray(1024)
                bis.read(buf)
                do {
                    bos.write(buf)
                } while (bis.read(buf) != -1)
            } catch (e: IOException) {
            } finally {
                try {
                    bis?.close()
                    bos?.close()
                } catch (e: IOException) {
                }
            }
        }
        fun get_price(input: String?): String? {
            var input = input
            var result = ""
            var float_section = ""
            if (input == null) input = ""
            var temp = input.trim { it <= ' ' }
            temp = temp.replace(",", "")
            var input_array = temp.split(".")
            var decimal_section = input_array[0]
            if(input_array.size>1)
                float_section = input_array[1]
            if (decimal_section.length > 3) {
                var num = 0
                for (i in decimal_section.length downTo 1) {
                    if (num == 3) {
                        num = 0
                        result = ",$result"
                    }
                    num++
                    result = decimal_section.substring(i - 1, i) + result
                }
                if(float_section!="")
                    result = "$result.$float_section"
            } else {
                result = decimal_section.replace(",", "")
                if(float_section!="")
                    result = "$result.$float_section"
            }
            return result
        }



        fun dp_to_px(px: Int , ctx:Context): Int {
            var result = px
            try {
                result = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    px.toFloat(),
                    ctx.getResources().getDisplayMetrics()
                ).toInt()
            }catch (e:java.lang.Exception){}
            return result
        }


        fun String?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

        fun show_toast(msg: String?, color: String, ctx: Context?) {
            try {
                (ctx as Activity).runOnUiThread {
                    if (color == "red") {
                        Toasty.error(ctx!!, msg!!, Toast.LENGTH_SHORT, true).show()
                    } else if (color == "green") {

                        //Toasty.custom(ctx,msg,null,R.color.material_green,false).show();
                        Toasty.success(ctx!!, msg!!, Toast.LENGTH_SHORT, true).show()
                    } else {
                        Toasty.normal(ctx!!, msg!!, Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e:Exception){}
        }


        fun show_msg(msg: String?, color: String,view: View, ctx: Context?) {

            try {

                val msg_snack = Snackbar.make(view!!, msg!!, Snackbar.LENGTH_LONG).setAction("Action", null)

                msg_snack.setTextColor(ContextCompat.getColor(ctx!!, R.color.kian1))
                if (color == "red") msg_snack.view.setBackgroundColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.red
                    )
                )
                if (color == "green") msg_snack.view.setBackgroundColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.green
                    )
                )
                if (color == "accent") msg_snack.view.setBackgroundColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.kian6
                    )
                )
                msg_snack.show()
            }catch (e : Exception){}

        }
        fun open_fragment(activity: Activity, fragment: Fragment) {

            try {
                try {
                    //(activity as MainActivity).fragment_changed()
                }catch (ex:Exception){}
                val transaction =(activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)

                transaction.replace(R.id.main_container, fragment, fragment.javaClass.simpleName)
                transaction.addToBackStack(fragment.javaClass.simpleName)
                transaction.commit()
            } catch (e: java.lang.Exception) {
            }
        }


        fun open_fragment(parent: Fragment, fragment: Fragment) {

            try {
                try {
                    //(activity as MainActivity).fragment_changed()
                }catch (ex:Exception){}
                val transaction =parent.childFragmentManager.beginTransaction()
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)

                transaction.replace(R.id.main_container, fragment, fragment.javaClass.simpleName)
                transaction.addToBackStack(fragment.javaClass.simpleName)
                transaction.commit()
            } catch (e: java.lang.Exception) {
            }
        }




        fun convertImageFileToBase64(imageFile: File): String {
            return ByteArrayOutputStream().use { outputStream ->
                Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                    imageFile.inputStream().use { inputStream ->
                        inputStream.copyTo(base64FilterStream)
                    }
                }
                return@use outputStream.toString()
            }
        }



        fun getDoublePrice(price: Double): String {
            try {
                if(price % 1 == 0.0){
                    return price.toInt().toString()
                }else{
                    return price.toString()
                }
            }catch (e:Exception){
                return price.toString()
            }
        }


        fun hideKeyboard(activity : Activity) {
            try {
                val view: View? = activity!!.getCurrentFocus()
                val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            }catch (e : Exception){}

        }

        fun is_valid_email(str : String):Boolean {
            return str.isValidEmail()
        }







        fun check_fingerpring(ctx : Context):Boolean {
            val biometricManager = BiometricManager.from(ctx!!)
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                BiometricManager.BIOMETRIC_SUCCESS ->
                {
                    return true
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    return false
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    return false
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    return false
                }
            }

            return false
        }




    }
    //================================================
    fun ImageView.setTint(@ColorRes colorRes: Int) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
    }
}