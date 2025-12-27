package com.workoutking.health

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * UserSignUpInfoDatabaseHelper
 *
 * PURPOSE:
 * Stores AUTHENTICATION credentials only.
 *
 * CONNECTED SCREENS:
 * - UserSignUpScreen (WRITE)
 * - LoginScreen (READ)
 *
 * STORES:
 * - First name
 * - Last name
 * - Email (UNIQUE)
 * - Password
 *
 * DOES NOT STORE:
 * - Usernames
 * - Profile images
 * - UI preferences
 *
 * IMPORTANT:
 * - Email is the PRIMARY AUTH IDENTIFIER
 * - Username login is resolved BEFORE reaching this database
 */
class UserSignUpInfoDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "user_signup_info.db"
        const val DATABASE_VERSION = 2 // ⬅️ VERSION BUMP REQUIRED

        const val TABLE_USERS = "user_signup_info"

        const val COLUMN_ID = "id"
        const val COLUMN_FIRST_NAME = "first_name"
        const val COLUMN_LAST_NAME = "last_name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FIRST_NAME TEXT NOT NULL,
                $COLUMN_LAST_NAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_CREATED_AT INTEGER
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    /**
     * Inserts a new user into the database.
     * Returns FALSE if email already exists.
     */
    fun insertUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Boolean {

        if (emailExists(email)) {
            return false
        }

        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_FIRST_NAME, firstName)
            put(COLUMN_LAST_NAME, lastName)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_CREATED_AT, System.currentTimeMillis())
        }

        val result = db.insert(TABLE_USERS, null, values)
        db.close()

        return result != -1L
    }

    /**
     * Checks whether an email already exists.
     * Used during SIGN-UP validation.
     */
    fun emailExists(email: String): Boolean {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT $COLUMN_ID FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?",
            arrayOf(email)
        )

        val exists = cursor.count > 0
        cursor.close()
        db.close()

        return exists
    }

    /**
     * Validates login credentials.
     * Email must already be resolved (username → email happens elsewhere).
     */
    fun validateLogin(email: String, password: String): Boolean {
        val db = readableDatabase

        val query = """
            SELECT $COLUMN_ID
            FROM $TABLE_USERS
            WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?
        """.trimIndent()

        val cursor: Cursor = db.rawQuery(query, arrayOf(email, password))
        val isValid = cursor.count > 0

        cursor.close()
        db.close()

        return isValid
    }
}
