package com.example.courseer2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.util.Log
import java.io.ByteArrayOutputStream
import java.util.Locale

val DATABASE_NAME = "MyDB"
val DATABASE_VERSION = 1
val TABLE_NAME = "User"
val COL_ID = "userid"
val COL_NAME = "name"
val COL_IMAGE = "image"
val COL_STRAND = "strand"
val TABLE_TAGS = "Tags"
val COL_TAG_ID = "id"
val COL_TAG_NAME = "tag_name"
val TABLE_TAGS2 = "Tags2"
val COL_TAG_ID2 = "id2"
val COL_TAG_NAME2 = "tag_name2"
val TABLE_KEYWORDS = "Keyword"
val COL_KEY_ID = "keyid"
val COL_KEY_NAME = "keyname"
val TABLE_KEYWORDS1 = "Keyword1"
val COL_KEY_ID1 = "keyid1"
val COL_KEY_NAME1 = "keyname1"
val TABLE_PROG_TABLE = "Programs"
val COL_PROG_ID = "progid"
val COL_PROG_TITLE = "title"
val COL_PROG_SHORT = "sdesciption"
val COL_PROG_LONG = "ldescription"
val COL_PROG_SUBJECTS = "subjects"
val COL_PROG_CAREERS = "careers"
val TABLE_PREFERENCES = "Preferences"
val COL_PREFERENCE_ID = "preferenceid"
val COL_BASIS = "basis"
val TABLE_TESTKEYS = "Table_testkeys"
val COL_TESTKEYS_ID = "testkeyid"
val COL_TESTKEYS = "testkeys"
val TABLE_SETTINGS = "settings"
val COLUMN_CURRENT_QUESTION_INDEX = "current_question_index"

class DataBaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase?) {
        val createTablePrograms = "CREATE TABLE $TABLE_PROG_TABLE (" +
                "$COL_PROG_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_PROG_TITLE VARCHAR(256)," +
                "$COL_PROG_SHORT TEXT," +
                "$COL_PROG_LONG TEXT," +
                "$COL_PROG_SUBJECTS VARCHAR(256)," +
                "$COL_PROG_CAREERS VARCHAR(256))"

        val createTablePreferences = "CREATE TABLE $TABLE_PREFERENCES (" +
                "$COL_PREFERENCE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_BASIS TEXT)"

        val createTableTestKeys = "CREATE TABLE $TABLE_TESTKEYS (" +
                "$COL_TESTKEYS_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_TESTKEYS TEXT)"

        val createTableKeywords = "CREATE TABLE $TABLE_KEYWORDS (" +
                "$COL_KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_KEY_NAME TEXT)"



        val createTableKeywords1 = "CREATE TABLE $TABLE_KEYWORDS1 (" +
                "$COL_KEY_ID1 INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_KEY_NAME1 TEXT)"

        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_IMAGE BLOB," +
                "$COL_NAME VARCHAR(256)," +
                "$COL_STRAND VARCHAR(256))"
        val createSettingsTable = "CREATE TABLE $TABLE_SETTINGS ($COLUMN_CURRENT_QUESTION_INDEX INTEGER)"
        db?.execSQL(createSettingsTable)
        db?.execSQL(createTable)

        db?.execSQL(createTableKeywords)
        db?.execSQL(createTableTestKeys)
        db?.execSQL(createTableKeywords1)
        db?.execSQL(createTablePreferences)
        db?.execSQL(createTablePrograms)


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }


    fun setCurrentQuestionIndex(index: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CURRENT_QUESTION_INDEX, index)
        }

        db.insertWithOnConflict(TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    // Function to retrieve the current question index
    fun getCurrentQuestionIndex(): Int {
        val db = this.readableDatabase
        val query = "SELECT MAX($COLUMN_CURRENT_QUESTION_INDEX) FROM $TABLE_SETTINGS"
        val cursor = db.rawQuery(query, null)
        var currentIndex = 0

        if (cursor.moveToFirst()) {
            currentIndex = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        return currentIndex
    }

    fun insertTestKeys(keywords: List<String>): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()

        try {
            for (keyword in keywords) {
                val cv = ContentValues()
                val cv2 = ContentValues()
                cv.put(COL_TESTKEYS, keyword)
                cv2.put(COL_BASIS, keyword)

                // Insert each keyword into the TABLE_TESTKEYS
                val result = db.insert(TABLE_TESTKEYS, null, cv)
                val result2 = db.insert(TABLE_PREFERENCES, null, cv2)
                if (result == -1L && result2 == -1L) {
                    // If insertion fails, rollback the transaction and return false
                    db.endTransaction()
                    return false
                }
            }

            // All insertions were successful, so commit the transaction
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        // Transaction completed successfully
        db.close()
        return true
    }
    // Inside DataBaseHandler class
    fun insertPreferences(selectedChips: List<String>) {
        val db = this.writableDatabase
        db.beginTransaction()

        try {
            for (chip in selectedChips) {
                val values = ContentValues()
                values.put(COL_BASIS, chip)

                // Insert the chip into the TABLE_PREFERENCES
                db.insert(TABLE_PREFERENCES, null, values)
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Handle any exceptions
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getAllBasisValues(): List<String> {
        val basisValues = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT $COL_BASIS FROM $TABLE_PREFERENCES"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val basisValue = it.getString(it.getColumnIndex(COL_BASIS))
                    basisValues.add(basisValue)
                } while (it.moveToNext())
            }
        }

        cursor.close()
        db.close()
        return basisValues
    }

    fun insertData(user: User): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        val byteArrayOutputStream = ByteArrayOutputStream()
        val bitmap = user.image

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)
        }
        val imageByteArray = byteArrayOutputStream.toByteArray()

        cv.put(COL_NAME, user.name)
        cv.put(COL_IMAGE, imageByteArray)
        cv.put(COL_STRAND, user.strand)

        // Insert user data into the User table
        val result = db.insert(TABLE_NAME, null, cv)

        // Now, insert user.strand into the Preferences table
        val preferencesCv = ContentValues()
        preferencesCv.put(COL_BASIS, user.strand)
        db.insert(TABLE_PREFERENCES, null, preferencesCv)

        db.close()
        return result
    }

    fun insertKeywords(tags: List<String>): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()

        try {
            for (tag in tags) {
                val cv = ContentValues()
                val cv2 = ContentValues()
                cv.put(COL_KEY_NAME, tag)
                cv2.put(COL_BASIS, tag)
                // Insert each tag into the KeywordsTable
                val result = db.insert(TABLE_KEYWORDS, null, cv)
                val result2 = db.insert(TABLE_PREFERENCES, null, cv2)
                if (result == -1L && result2 == -1L) {
                    // If any insertion fails, rollback the transaction and return false
                    db.endTransaction()
                    return false
                }
            }

            // All insertions were successful, so commit the transaction
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        // Transaction completed successfully
        db.close()
        return true
    }

    fun insertKeywords1(tags: List<String>): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()

        try {
            for (tag in tags) {
                val cv = ContentValues()
                val cv2 = ContentValues()
                cv.put(COL_KEY_NAME1, tag)
                cv2.put(COL_BASIS, tag)
                // Insert each tag into the KeywordsTable
                val result = db.insert(TABLE_KEYWORDS1, null, cv)
                val result2 = db.insert(TABLE_PREFERENCES, null, cv2)
                if (result == -1L && result2 == -1L) {
                    // If any insertion fails, rollback the transaction and return false
                    db.endTransaction()
                    return false
                }
            }

            // All insertions were successful, so commit the transaction
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }


        // Transaction completed successfully
        db.close()
        return true
    }


    fun clearAllData() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.delete(TABLE_PREFERENCES, null, null)
        db.delete(TABLE_KEYWORDS, null, null)
        db.delete(TABLE_KEYWORDS1, null, null)
        db.delete(TABLE_TESTKEYS, null, null)
        db.close()
    }




}


// Add other methods for database operations related to user data here