package com.workoutking.health

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserSurveyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        const val DATABASE_NAME = "user_survey.db"
        const val DATABASE_VERSION = 1

        const val TABLE_SURVEY = "user_survey_answers"

        const val COLUMN_ID = "id"

        const val HAS_HIGH_BLOOD_PRESSURE = "has_high_blood_pressure"
        const val HAS_LOW_BLOOD_PRESSURE = "has_low_blood_pressure"
        const val TAKES_BP_MEDICATION = "takes_bp_medication"

        const val CAN_WALK = "can_walk"
        const val CAN_RUN = "can_run"
        const val CAN_RUN_UPHILL = "can_run_uphill"
        const val CAN_RUN_DOWNHILL = "can_run_downhill"

        const val WORKOUT_DURATION_MINUTES = "workout_duration_minutes"
        const val CAN_LIFT_WEIGHT_LBS = "can_lift_weight_lbs"

        const val USES_WHEELCHAIR = "uses_wheelchair"

        const val INTERESTED_IN_MARTIAL_ARTS = "interested_in_martial_arts"
        const val INTERESTED_IN_JIU_JITSU = "interested_in_jiu_jitsu"
        const val INTERESTED_IN_KARATE = "interested_in_karate"

        const val PREFERS_HOME_WORKOUT = "prefers_home_workout"
        const val PREFERS_GYM_WORKOUT = "prefers_gym_workout"

        const val SMOKES = "smokes"

        const val INTERESTED_IN_DIET = "interested_in_diet"
        const val INTERESTED_IN_MEDITERRANEAN_DIET =
            "interested_in_mediterranean_diet"

        const val CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {

        val createTable = """
            CREATE TABLE $TABLE_SURVEY (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,

                $HAS_HIGH_BLOOD_PRESSURE INTEGER,
                $HAS_LOW_BLOOD_PRESSURE INTEGER,
                $TAKES_BP_MEDICATION INTEGER,

                $CAN_WALK INTEGER,
                $CAN_RUN INTEGER,
                $CAN_RUN_UPHILL INTEGER,
                $CAN_RUN_DOWNHILL INTEGER,

                $WORKOUT_DURATION_MINUTES INTEGER,
                $CAN_LIFT_WEIGHT_LBS INTEGER,

                $USES_WHEELCHAIR INTEGER,

                $INTERESTED_IN_MARTIAL_ARTS INTEGER,
                $INTERESTED_IN_JIU_JITSU INTEGER,
                $INTERESTED_IN_KARATE INTEGER,

                $PREFERS_HOME_WORKOUT INTEGER,
                $PREFERS_GYM_WORKOUT INTEGER,

                $SMOKES INTEGER,

                $INTERESTED_IN_DIET INTEGER,
                $INTERESTED_IN_MEDITERRANEAN_DIET INTEGER,

                $CREATED_AT INTEGER
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SURVEY")
        onCreate(db)
    }

    fun insertSurveyAnswers(values: ContentValues): Long {

        values.put(CREATED_AT, System.currentTimeMillis())

        return writableDatabase.insert(
            TABLE_SURVEY,
            null,
            values
        )
    }

    fun getLatestSurvey(): Cursor {
        return readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_SURVEY ORDER BY $COLUMN_ID DESC LIMIT 1",
            null
        )
    }

    fun hasCompletedSurvey(): Boolean {

        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_SURVEY",
            null
        )

        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        return count > 0
    }
}
