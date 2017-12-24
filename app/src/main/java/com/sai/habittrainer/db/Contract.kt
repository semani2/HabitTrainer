package com.sai.habittrainer.db

import android.provider.BaseColumns

/**
 * Created by sai on 12/24/17.
 */

val DATABASE_NAME = "habittrainer.db"
val DATABASE_VERSION = 1

// More like a static class, cannot be instantiated.
object HabitEntry: BaseColumns {
    val TABLE_NAME = "habit"

    val _ID = "id"
    val TITLE_COLUMN = "title"
    val DESCRIPTION_COLUMN = "description"
    val IMAGE_COLUMN = "image"
}