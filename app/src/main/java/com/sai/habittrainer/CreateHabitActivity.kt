package com.sai.habittrainer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_create_habit.*
import java.io.IOException

class CreateHabitActivity : AppCompatActivity() {

    private val REQUEST_CODE:Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_habit)
    }

    fun chooseImage(view: View) {
        val chooseImageIntent = Intent()

        chooseImageIntent.type = "image/*"
        chooseImageIntent.action = Intent.ACTION_GET_CONTENT

        val chooser = Intent.createChooser(chooseImageIntent, "Choose image for habit")
        startActivityForResult(chooser, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null
                && data.data != null) {
            val bitmap = tryReadBitmap(data.data)

            // Run only if bitmap is not null
            bitmap?.let {
                habit_image_view.visibility = View.VISIBLE
                habit_image_view.setImageBitmap(bitmap)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun tryReadBitmap(data: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(contentResolver, data)
        } catch(e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
