package com.kian.helper

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

class CircleImageView(ctx: Context?, attrs: AttributeSet?) : AppCompatImageView(
    ctx!!, attrs
) {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        val drawable = drawable ?: return
        if (width == 0 || height == 0) {
            return
        }
        try {
            val b = (drawable as BitmapDrawable).bitmap
            val bitmap = b.copy(Bitmap.Config.ARGB_8888, true)
            val w = width
            val h = height
            val roundBitmap = getRoundedCroppedBitmap(bitmap, w)
            canvas.drawBitmap(roundBitmap, 0f, 0f, null)
        } catch (e: Exception) {
            try {
                if (drawable != null) {
                    var b: Bitmap? = null
                    if (drawable is VectorDrawableCompat) {
                        b = getBitmap(drawable)
                    } else if (drawable is VectorDrawable) {
                        b = getBitmap(drawable)
                    }
                    val bitmap = b!!.copy(Bitmap.Config.ARGB_8888, true)
                    val w = width
                    val h = height
                    val roundBitmap = getRoundedCroppedBitmap(bitmap, w)
                    canvas.drawBitmap(roundBitmap, 0f, 0f, null)
                }
            } catch (e2: Exception) {
                var i = 0
                i++
            }
            return
        }
    }

    companion object {
        fun getRoundedCroppedBitmap(bitmap: Bitmap, radius: Int): Bitmap {
            val finalBitmap: Bitmap
            finalBitmap =
                if (bitmap.width != radius || bitmap.height != radius) Bitmap.createScaledBitmap(
                    bitmap, radius, radius,
                    false
                ) else bitmap
            val output = Bitmap.createBitmap(
                finalBitmap.width,
                finalBitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val paint = Paint()
            val rect = Rect(
                0, 0, finalBitmap.width,
                finalBitmap.height
            )
            paint.isAntiAlias = true
            paint.isFilterBitmap = true
            paint.isDither = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = Color.parseColor("#BAB399")
            canvas.drawCircle(
                finalBitmap.width / 2 + 0.7f,
                finalBitmap.height / 2 + 0.7f,
                finalBitmap.width / 2 + 0.1f, paint
            )
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(finalBitmap, rect, rect, paint)
            return output
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
            val bitmap = Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
            return bitmap
        }

        private fun getBitmap(vectorDrawable: VectorDrawableCompat): Bitmap {
            val bitmap = Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
            return bitmap
        }
    }
}