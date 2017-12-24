package com.sai.habittrainer.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.sai.habittrainer.Habit
import com.sai.habittrainer.db.HabitEntry.DESCRIPTION_COLUMN
import com.sai.habittrainer.db.HabitEntry.IMAGE_COLUMN
import com.sai.habittrainer.db.HabitEntry.TITLE_COLUMN
import com.sai.habittrainer.db.HabitEntry._ID
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
            put(TITLE_COLUMN, habit.title)
            put(DESCRIPTION_COLUMN, habit.description)
            put(IMAGE_COLUMN, toByteArray(habit.image))
        }

        val id = db.transaction {
            insert(HabitEntry.TABLE_NAME, null, values)
        }
        Log.d(TAG, "New habit stored to the db $habit")

        return id
    }

    fun readAllHabits(): List<Habit> {
        val columns = arrayOf(_ID, TITLE_COLUMN, DESCRIPTION_COLUMN, IMAGE_COLUMN)

        val db = dbHelper.readableDatabase

        val order = "${_ID} ASC"

        val cursor = db.doQuery(tableName = HabitEntry.TABLE_NAME, columns = columns, orderBy = order)

        return parseHabitsFromCursor(cursor)
    }

    private fun parseHabitsFromCursor(cursor: Cursor): MutableList<Habit> {
        val habits = mutableListOf<Habit>()
        while (cursor.moveToNext()) {
            val title = cursor.getString(TITLE_COLUMN)
            val description = cursor.getString(DESCRIPTION_COLUMN)
            val imageBitmap = cursor.getBitmap(IMAGE_COLUMN)

            habits.add(Habit(title, description, imageBitmap))
        }
        cursor.close()
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

private fun SQLiteDatabase.doQuery(tableName: String, columns: Array<String>, selection: String? = null, selectionArgs: Array<String>? = null,
                                 groupBy: String? = null, having: String? = null, orderBy: String? = null) : Cursor{
    return query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy)
}

private fun Cursor.getString(columnName: String) = getString(getColumnIndex(columnName))

private fun Cursor.getBlob(columnName: String) = getBlob(getColumnIndex(columnName))

private fun Cursor.getBitmap(columnName: String) : Bitmap {
    val imageByteArray = getBlob(columnName)
    return BitmapFactory.decodeByteArray(imageByteArray, 0 ,imageByteArray.size)
}