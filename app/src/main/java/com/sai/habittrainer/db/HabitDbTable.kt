package com.sai.habittrainer.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.sai.habittrainer.Habit
import java.io.ByteArrayOutputStream

/**
 * Created by sai on 12/24/17.
 */
class HabitDbTable(context: Context) {

    private val dbHelper = HabitTrainerDb(context)

    private val TAG = HabitDbTable::class.java.simpleName

    fun store(habit: Habit): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        // Using the with() scoping
        with(values) {
            put(HabitEntry.TITLE_COLUMN, habit.title)
            put(HabitEntry.DESCRIPTION_COLUMN, habit.description)
            put(HabitEntry.IMAGE_COLUMN, toByteArray(habit.image))
        }

        val id = db.transaction {
            insert(HabitEntry.TABLE_NAME, null, values)
        }
        Log.d(TAG, "New habit stored to the db $habit")

        return id
    }

    fun readAllHabits(): List<Habit> {
        val columns = arrayOf(HabitEntry._ID, HabitEntry.TITLE_COLUMN, HabitEntry.DESCRIPTION_COLUMN, HabitEntry.IMAGE_COLUMN)

        val db = dbHelper.readableDatabase

        val order = "${HabitEntry._ID} ASC"

        val cursor = db.query(HabitEntry.TABLE_NAME, columns, null, null, null, null, order)

        val habits = mutableListOf<Habit>()

        while(cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndex(HabitEntry.TITLE_COLUMN))
            val description = cursor.getString(cursor.getColumnIndex(HabitEntry.DESCRIPTION_COLUMN))
            val imageByteArray = cursor.getBlob(cursor.getColumnIndex(HabitEntry.IMAGE_COLUMN))

            val imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0 ,imageByteArray.size)

            habits.add(Habit(title, description, imageBitmap))
        }
        cursor.close()
        db.close()

        return habits
    }

    private fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 , stream)
        return stream.toByteArray()
    }
}

// Function that is being passed is itself an extension function of SQLiteDatabase
// Inline function -> the function body is copied over to the caller's site. Modularity is achieved but with the same performance.
private inline fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.() -> T): T {
    beginTransaction()
    val result = try {
        val returnValue = function()
        setTransactionSuccessful()

        returnValue
    } finally {
        endTransaction()
    }
    close()

    return result
}