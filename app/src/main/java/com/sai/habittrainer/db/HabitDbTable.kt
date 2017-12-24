package com.sai.habittrainer.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
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
        values.put(HabitEntry.TITLE_COLUMN, habit.title)
        values.put(HabitEntry.DESCRIPTION_COLUMN, habit.description)
        values.put(HabitEntry.IMAGE_COLUMN, toByteArray(habit.image))

        val id = db.transaction {
            insert(HabitEntry.TABLE_NAME, null, values)
        }
        Log.d(TAG, "New habit stored to the db $habit")

        return id
    }

    private fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 , stream)
        return stream.toByteArray()
    }
}

// Function that is being passed is itself an extension function of SQLiteDatabase
private fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.() -> T): T {
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