package com.example.courseer2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.util.Log
import java.io.ByteArrayOutputStream


val DATABASE_NAME = "MyDB"
val DATABASE_VERSION = 2
val TABLE_NAME = "User"
val COL_ID = "userid"
val COL_NAME = "name"
val COL_IMAGE = "image"
val COL_STRAND = "strand"
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
val TABLE_FIELDS = "Fields"
val COL_PREFERENCE_ID = "preferenceid"
val COL_FIELD_ID = "fieldid"
val COL_BASIS = "basis"
val COL_FIELDS = "fields"
val TABLE_TESTKEYS = "Table_testkeys"
val COL_TESTKEYS_ID = "testkeyid"
val COL_TESTKEYS = "testkeys"
val TABLE_SETTINGS = "settings"
val COLUMN_CURRENT_QUESTION_INDEX = "current_question_index"
val TABLE_SCORES = "scores"
val COLUMN_SCORE_ID = "id"
val COLUMN_CATEGORY = "category"
val COLUMN_SCORE = "score"
val TABLE_FSCORES = "Table_fscores"
val COLUMN_FSCORES_ID = "fscoresid"
val COLUMN_WORD = "word"
val COLUMN_FSCORE = "fscore"
val COLUMN_DESCRIPTION = "description"
val TABLE_COUNT = "Table_count"
val COL_COUNT= "count"
val COL_COUNT_ID = "countid"

class DataBaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_SCORES_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_SCORES (" +
                "$COLUMN_SCORE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_CATEGORY TEXT NOT NULL," +
                "$COLUMN_SCORE INTEGER NOT NULL)"
        val createTableFieldScores = "CREATE TABLE IF NOT EXISTS $TABLE_FSCORES (" +
                "$COLUMN_FSCORES_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_WORD TEXT NOT NULL," +
                "$COLUMN_DESCRIPTION TEXT NOT NULL," +
                "$COLUMN_FSCORE INTEGER)"
        val createTableCounter = "CREATE TABLE IF NOT EXISTS $TABLE_COUNT (" +
                "$COL_COUNT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_COUNT INTEGER NOT NULL)"

        val createTablePrograms = "CREATE TABLE IF NOT EXISTS $TABLE_PROG_TABLE (" +
                "$COL_PROG_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_PROG_TITLE VARCHAR(256)," +
                "$COL_PROG_SHORT TEXT," +
                "$COL_PROG_LONG TEXT," +
                "$COL_PROG_SUBJECTS VARCHAR(256)," +
                "$COL_PROG_CAREERS VARCHAR(256))"

        val createTablePreferences = "CREATE TABLE IF NOT EXISTS $TABLE_PREFERENCES (" +
                "$COL_PREFERENCE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_BASIS TEXT)"

        val createTableTestKeys = "CREATE TABLE IF NOT EXISTS $TABLE_TESTKEYS (" +
                "$COL_TESTKEYS_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_TESTKEYS TEXT)"

        val createTableKeywords = "CREATE TABLE IF NOT EXISTS $TABLE_KEYWORDS (" +
                "$COL_KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_KEY_NAME TEXT)"



        val createTableKeywords1 = "CREATE TABLE IF NOT EXISTS $TABLE_KEYWORDS1 (" +
                "$COL_KEY_ID1 INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_KEY_NAME1 TEXT)"

        val createTableFields = "CREATE TABLE IF NOT EXISTS $TABLE_FIELDS (" +
                "$COL_FIELD_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_FIELDS TEXT)"

        val createTable = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_IMAGE BLOB," +
                "$COL_NAME VARCHAR(256)," +
                "$COL_STRAND VARCHAR(256))"
        val createSettingsTable =
            "CREATE TABLE IF NOT EXISTS $TABLE_SETTINGS ($COLUMN_CURRENT_QUESTION_INDEX INTEGER)"

        db?.execSQL(createTableFieldScores)
        db?.execSQL(createTableCounter)
        db?.execSQL(createSettingsTable)
        db?.execSQL(createTable)
        db?.execSQL(createTableFields)
        db?.execSQL(CREATE_SCORES_TABLE)
        db?.execSQL(createTableKeywords)
        db?.execSQL(createTableTestKeys)
        db?.execSQL(createTableKeywords1)
        db?.execSQL(createTablePreferences)
        db?.execSQL(createTablePrograms)


    }
// Inside the DataBaseHandler class

    // Function to increase the value in COL_COUNT in TABLE_COUNT
    fun increaseCount() {
        val db = this.writableDatabase
        db.beginTransaction()

        try {
            // Create the table if it doesn't exist
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_COUNT ($COL_COUNT INTEGER PRIMARY KEY)")

            // Insert the initial count value (if not already present)
            val query = "INSERT OR REPLACE INTO $TABLE_COUNT ($COL_COUNT) VALUES (0)"
            db.execSQL(query)

            // Update the count
            val updateQuery = "UPDATE $TABLE_COUNT SET $COL_COUNT = $COL_COUNT + 1"
            db.execSQL(updateQuery)

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("COUNTERRRR", "Error increasing count", e)
        } finally {
            db.endTransaction()
            db.close()
        }
    }


    // Function to get the highest value in COL_COUNT in TABLE_COUNT
    fun getHighestCount(): Int {
        val db = this.readableDatabase
        val query = "SELECT MAX($COL_COUNT) FROM $TABLE_COUNT"
        var highestCount = 0

        try {
            val cursor = db.rawQuery(query, null)
            cursor.use {
                if (it.moveToFirst()) {
                    highestCount = it.getInt(0)
                }
            }
        } catch (e: Exception) {
            Log.e("COUNTERRRR", "Error getting highest count", e)
        } finally {
            db.close()
        }

        return highestCount
    }

    // Function to decrease the value in COL_COUNT in TABLE_COUNT
    fun decreaseCount() {
        val db = this.writableDatabase
        val query = "UPDATE $TABLE_COUNT SET $COL_COUNT = $COL_COUNT - 1 WHERE $COL_COUNT > 0"
        try {
            db.execSQL(query)
        } catch (e: Exception) {
            Log.e("COUNTERRRR", "Error decreasing count", e)
        } finally {
            db.close()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    // Inside DataBaseHandler class
    fun insertInterpretation(word: String, description: String) {
        // Retrieve the highest COLUMN_SCORE value for the corresponding field from the TABLE_SCORE table
        val highestScore: Int = getHighestScoreForCategoryFromTable(word)

        // Insert the interpretation into the TABLE_FSCORES table
        val values = ContentValues().apply {
            put(COLUMN_WORD, word)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_FSCORE, highestScore)
        }

        val db = this.writableDatabase
        db.insertWithOnConflict(TABLE_FSCORES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }


    // Function to retrieve the COLUMN_SCORE value for a specific category from the TABLE_SCORE table
    private fun getScoreForCategoryFromTable(category: String): Int {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_SCORES,
            arrayOf(COLUMN_SCORE),
            "$COLUMN_CATEGORY = ?",
            arrayOf(category),
            null,
            null,
            null
        )

        var score = 0

        cursor?.use {
            if (it.moveToFirst()) {
                score = it.getInt(it.getColumnIndexOrThrow(COLUMN_SCORE))
            }
        }

        return score
    }


    private fun getHighestScoreForCategoryFromTable(category: String): Int {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_SCORES,
            arrayOf("MAX($COLUMN_SCORE) AS $COLUMN_SCORE"),
            "$COLUMN_CATEGORY = ?",
            arrayOf(category),
            null,
            null,
            null
        )

        var highestScore = 0

        cursor?.use {
            if (it.moveToFirst()) {
                highestScore = it.getInt(it.getColumnIndexOrThrow(COLUMN_SCORE))
            }
        }

        return highestScore
    }

    fun processFieldScores() {
        val db = this.readableDatabase
        val fieldsCursor =
            db.query(TABLE_FIELDS, arrayOf(COL_FIELD_ID, COL_FIELDS), null, null, null, null, null)

        while (fieldsCursor.moveToNext()) {
            val fieldId = fieldsCursor.getInt(fieldsCursor.getColumnIndex(COL_FIELD_ID))
            val fieldValue = fieldsCursor.getString(fieldsCursor.getColumnIndex(COL_FIELDS))

            val highestScore = getHighestScoreForField(fieldValue)

            // Save the results in TABLE_FSCORES
            saveFieldScore(fieldId, fieldValue, highestScore)
        }

        fieldsCursor.close()
        db.close()
    }

    fun getHighestScoreForField(fieldValue: String): Int {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_SCORES,
            arrayOf(COLUMN_SCORE),
            "$COLUMN_CATEGORY = ?",
            arrayOf(fieldValue),
            null,
            null,
            "$COLUMN_SCORE DESC",
            "1"
        )

        val highestScore = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE))
        } else {
            0 // Default value if no score found
        }

        cursor.close()
        return highestScore
    }

    fun saveFieldScore(fieldId: Int, fieldValue: String, score: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WORD, fieldValue)
            put(COLUMN_FSCORE, score)
        }

        db.insert(TABLE_FSCORES, null, values)
        db.close()
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

    fun updateScore(category: String, score: Int) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            Log.d("updateScore", "Category: $category, Score: $score")
            val values = ContentValues()
            values.put(COLUMN_CATEGORY, category)
            values.put(COLUMN_SCORE, score)

            db.insertWithOnConflict(TABLE_SCORES, null, values, SQLiteDatabase.CONFLICT_REPLACE)

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("updateScore", "Error updating score", e)
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getAllCategories(): List<String> {
        val categories = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.query(
            true,
            TABLE_SCORES,
            arrayOf(COLUMN_CATEGORY),
            null,
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            categories.add(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)))
        }
        cursor.close()
        db.close()
        return categories
    }

    fun getHighestScore(category: String): Int {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_SCORES,
            arrayOf(COLUMN_SCORE),
            "$COLUMN_CATEGORY = ?",
            arrayOf(category),
            null,
            null,
            "$COLUMN_SCORE DESC",
            "1"
        )

        val highestScore = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE))
        } else {
            0 // Default value if no score found
        }

        cursor.close()
        db.close()
        return highestScore
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

    fun insertFields(selectedChips: List<String>) {
        val db = this.writableDatabase
        db.beginTransaction()

        try {
            for (chip in selectedChips) {
                val values = ContentValues()
                values.put(COL_FIELDS, chip)

                // Insert the chip into the TABLE_PREFERENCES
                db.insert(TABLE_FIELDS, null, values)
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



        db.close()
        return result
    }

    fun clearUser(){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }
    fun clearInterests(){
        val db = this.writableDatabase
        db.delete(TABLE_KEYWORDS, null, null)
        db.close()
    }
    fun clearCareers(){
        val db = this.writableDatabase
        db.delete(TABLE_KEYWORDS1, null, null)
        db.close()
    }

    fun insertInterests(tags: List<String>): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()

        try {
            for (tag in tags) {
                val cv = ContentValues()

                cv.put(COL_KEY_NAME, tag)

                // Insert each tag into the KeywordsTable
                val result = db.insert(TABLE_KEYWORDS, null, cv)

                if (result == -1L) {
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
// Assuming your column names are declared as constants like COL_KEY_NAME, COL_KEYNAME1, COLUMN_BASIS

    fun copyKeywordsToPreferences() {
        val db = this.writableDatabase
        val cursorKeywords =
            db.rawQuery("SELECT $COL_KEY_NAME AS $COL_BASIS FROM $TABLE_KEYWORDS", null)
        val cursorKeywords1 =
            db.rawQuery("SELECT $COL_KEY_NAME1 AS $COL_BASIS FROM $TABLE_KEYWORDS1", null)
        val strand =
            db.rawQuery("SELECT $COL_STRAND AS $COL_BASIS FROM $TABLE_NAME", null)
        try {
            db.beginTransaction()

            // Clear existing data in TABLE_PREFERENCES

            while (strand.moveToNext()) {
                val basisValue = strand.getString(strand.getColumnIndex(COL_BASIS))
                db.execSQL("INSERT INTO $TABLE_PREFERENCES ($COL_BASIS) VALUES ('$basisValue')")
            }
            // Insert values from TABLE_KEYWORDS
            while (cursorKeywords.moveToNext()) {
                val basisValue = cursorKeywords.getString(cursorKeywords.getColumnIndex(COL_BASIS))
                db.execSQL("INSERT INTO $TABLE_PREFERENCES ($COL_BASIS) VALUES ('$basisValue')")
            }
            // Insert values from TABLE_KEYWORDS1
            while (cursorKeywords1.moveToNext()) {
                val basisValue =
                    cursorKeywords1.getString(cursorKeywords1.getColumnIndex(COL_BASIS))
                db.execSQL("INSERT INTO $TABLE_PREFERENCES ($COL_BASIS) VALUES ('$basisValue')")
            }

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Handle the exception (log it or throw it)
        } finally {
            db.endTransaction()
            cursorKeywords.close()
            cursorKeywords1.close()
            strand.close()
        }
    }

    fun insertCareers(tags: List<String>): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()

        try {
            for (tag in tags) {
                val cv = ContentValues()
                cv.put(COL_KEY_NAME1, tag)
                // Insert each tag into the KeywordsTable
                val result = db.insert(TABLE_KEYWORDS1, null, cv)
                if (result == -1L) {
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
    fun insertKeywords(tags: List<String>): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()

        try {
            for (tag in tags) {
                val cv = ContentValues()
                cv.put(COL_KEY_NAME, tag)
                // Insert each tag into the KeywordsTable
                val result = db.insert(TABLE_KEYWORDS, null, cv)
                if (result == -1L) {
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

                val cv2 = ContentValues()

                cv2.put(COL_BASIS, tag)
                // Insert each tag into the KeywordsTable

                val result2 = db.insert(TABLE_PREFERENCES, null, cv2)
                if (result2 == -1L) {
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
        db.delete(TABLE_FIELDS, null, null)
        db.delete(TABLE_SETTINGS, null, null)
        db.delete(TABLE_FSCORES, null, null)
        db.delete(TABLE_SCORES, null, null)
        db.delete(TABLE_COUNT, null, null)
        db.close()
    }


}


// Add other methods for database operations related to user data here