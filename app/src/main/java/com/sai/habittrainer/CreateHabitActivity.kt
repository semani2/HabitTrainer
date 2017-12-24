package com.sai.habittrainer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.sai.habittrainer.db.HabitDbTable
import kotlinx.android.synthetic.main.activity_create_habit.*
import java.io.IOException

class CreateHabitActivity : AppCompatActivity() {

    private val REQUEST_CODE:Int = 101

    private val TAG: String = CreateHabitActivity::class.java.simpleName

    private var imageBitmap: Bitmap? = null

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

    fun storeHabit(view: View) {
        if(title_edit_text.isBlank() || description_edit_text.isBlank()) {
            Log.d(TAG, "Blank title or description")
            showErrorMessage("Please enter a valid title and/or description")
            return
        }

        if(imageBitmap == null) {
            Log.d(TAG, "No image for habit selected")
            showErrorMessage("Please select a motivational image")
            return
        }

        val habit = Habit(title_edit_text.text.toString(), description_edit_text.text.toString(), imageBitmap!!)
        val id = HabitDbTable(this).store(habit)

        if(id == -1L) {
            Log.e(TAG, "Error saving habit to dB")
            showErrorMessage("Habit could not be saved. Please try again.")
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null
                && data.data != null) {
            val bitmap = tryReadBitmap(data.data)

            // Run only if bitmap is not null
            bitmap?.let {
                habit_image_view.visibility = View.VISIBLE
                habit_image_view.setImageBitmap(bitmap)
                imageBitmap = bitmap
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

    private fun showErrorMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun EditText.isBlank(): Boolean = this.text.toString().isBlank()
}
