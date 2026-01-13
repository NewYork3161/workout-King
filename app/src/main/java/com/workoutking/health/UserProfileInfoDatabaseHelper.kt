package com.workoutking.health

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserProfileInfoDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "user_profile_info.db"
        const val DATABASE_VERSION = 1

        const val TABLE_PROFILE = "user_profile_info"

        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_IMAGE_URI = "profile_image_uri"
        const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_PROFILE (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_IMAGE_URI TEXT,
                $COLUMN_CREATED_AT INTEGER
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PROFILE")
        onCreate(db)
    }

    fun insertProfile(username: String, imageUri: String?): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_IMAGE_URI, imageUri)
            put(COLUMN_CREATED_AT, System.currentTimeMillis())
        }

        val result = db.insert(TABLE_PROFILE, null, values)
        db.close()

        return result != -1L
    }

    fun getLatestProfile(): Cursor {
        val db = readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_PROFILE ORDER BY $COLUMN_ID DESC LIMIT 1",
            null
        )
    }
}
