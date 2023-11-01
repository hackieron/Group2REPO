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

        val createTagsTable = "CREATE TABLE $TABLE_TAGS (" +
                "$COL_TAG_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_TAG_NAME TEXT)"

        val createTagsTable2 = "CREATE TABLE $TABLE_TAGS2 (" +
                "$COL_TAG_ID2 INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_TAG_NAME2 TEXT)"

        db?.execSQL(createTable)
        db?.execSQL(createTagsTable)
        db?.execSQL(createTagsTable2)
        db?.execSQL(createTableKeywords)
        db?.execSQL(createTableKeywords1)
        db?.execSQL(createTablePreferences)
        db?.execSQL(createTablePrograms)

        insertInitialTags(db)
        insertInitialTags2(db)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
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
    fun insertTags(tags: List<String>): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        try {
            for (tag in tags) {
                values.put(COL_TAG_NAME, tag)
                db.insert(TABLE_TAGS, null, values)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return false
    }
    fun insertTags2(tags: List<String>): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        try {
            for (tag in tags) {
                values.put(COL_TAG_NAME2, tag)
                db.insert(TABLE_TAGS2, null, values)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return false
    }

    fun clearAllData() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.delete(TABLE_PREFERENCES, null, null)
        db.delete(TABLE_KEYWORDS, null, null)
        db.delete(TABLE_KEYWORDS1, null, null)
        db.close()
    }

    private fun insertInitialTags(db: SQLiteDatabase?) {
        val initialTags = listOf(
            "Humanities", "History", "Philosophy of History", "Historical Methodology", "Asian Civilizations", "Western Civilizations", "European History", "East Asian", "Islamic History", "Economic History", "Cultural History", "Nationalism", "Revolution", "Metaphysics", "Epistemology", "Ethics", "Logic", "Political Philosophy", "Philosophy of Person", "Existentialism", "Filipino Philosophy", "Philosophy of Science", "Philosophy of Religion", "Moral Philosophy", "Arts & Design", "Design Theory", "Ergonomics", "Package Design", "Systems Design", "Production Techniques", "Drawing Techniques", "Materials for Industrial Design", "Photography", "Painting", "Artistic representations", "Perceptual skills", "Technical skills", "Mediums (oil", "acrylic", "watercolor", "mixed media)", "Color", "Form", "Texture", "Composition", "Art history", "Visual Perception", "Visual Studies", "Visual Communication", "Drawing", "Art Theory", "Art Workshop", "Research Method in the Arts", "Sculpture", "Three-dimensional art", "Expressing interests and ideas", "Materials (clay", "wood", "metal", "stone", "plaster", "found objects)", "Space", "Sculpting techniques", "Artistic voice", "Traditional and modern technology", "Advertisements", "Web design", "New media design", "Graphic design", "Illustration", "Typography", "Motion graphics", "Figure Drawing", "Visual Design", "Visual Verbal Communication", "Advertising Design", "Production Methods", "Design Workshop", "Production for Electronic Media", "Social Sciences", "Economic principles", "Economic analysis", "Real-world contexts", "Microeconomics", "Macroeconomics", "Econometrics", "Economic policy", "International economics", "Development economics", "Financial economics", "Environmental Economics", "Industrial Organization", "Labor Economics", "Economic Research", "Philippine Constitution and Government", "Mathematical Economics", "Economic Statistics", "Obligations and Contracts", "History of Economic Thought", "Public Economics", "Economic Planning and Development", "Health Economics", "Managerial Economics", "Economic systems", "Basic Microeconomics", "Basic Macroeconomics", "Intermediate Microeconomics", "Intermediate Macroeconomics", "Philippine Economics History", "Urban Economics", "Regional Economics", "Money and Banking", "Resource Economics", "Integral Calculus", "Differential Calculus", "Introduction to Financial Accounting", "Psychology", "Human behavior", "Mental processes", "Psychological theories", "Research methods", "Cognitive psychology", "Social psychology", "Developmental psychology", "Abnormal psychology", "Personality psychology", "Physiological psychology", "Counseling and Psychotherapy", "Industrial and Organizational Psychology", "Health Psychology", "Cross-Cultural Psychology", "Neuropsychology", "Positive Psychology", "Forensic Psychology", "Educational Psychology", "History and Systems of Psychology", "Political Science", "Political systems", "Institutions", "Political processes", "Political theories", "Comparative politics", "International relations", "Public policy", "Political economy", "Political Theory", "Public Policy Analysis", "Research Methods in Political Science", "American Government and Politics", "Political Sociology", "Public Administration", "Global Governance", "Human Rights and International Law", "Political Communication", "Electoral Systems and Voting Behavior", "Gender and Politics", "Environmental Politics", "Conflict Resolution and Peace Studies", "Public Opinion and Political Behavior", "Political Parties and Interest Groups", "Business", "Accounting principles", "Financial management", "Auditing", "Taxation", "Business ethics", "Financial Accounting", "Managerial Accounting", "Cost Accounting", "Accounting Information Systems", "Business Law and Ethics", "Economics", "Statistics", "Quantitative Methods", "Financial Statement Analysis", "Advanced Accounting", "Advanced Auditing", "Advanced Taxation", "Strategic Management", "Professional Ethics and Values", "Research in Accounting", "Internship/Practicum", "Business management", "Marketing", "Finance", "Accounting", "Operations management", "Human resource management", "Entrepreneurship", "Introduction to Business", "Principles of Marketing", "Business Statistics", "Organizational Behavior", "International Business", "Business Communication", "Business Information Systems", "Supply Chain Management", "Business Research Methods", "Business Ethics and Corporate Social Responsibility", "Financial analysis", "Investment management", "Risk management", "Financial planning", "Corporate finance", "International finance", "Financial markets", "Quantitative methods in finance", "Financial Modeling and Forecasting", "Strategic Financial Management", "Taxation and Tax Planning", "Financial Derivatives and Risk Management", "Entrepreneurial Finance", "Mergers and Acquisitions", "Financial Technology (Fintech) and Innovation in Finance", "Investment Analysis and Portfolio Management", "Risk Management and Insurance", "Financial Markets and Institutions", "Cost Analysis", "Budgeting", "Performance Evaluation", "Strategic Decision-making", "Budgeting and Forecasting", "Performance Evaluation and Control", "Business Ethics and Corporate Governance", "Auditing and Assurance Services", "Business Law and Regulations", "Information Systems for Management Accounting", "Advanced Management Accounting", "Risk Management and Internal Control", "Research in Management Accounting", "Internship/Practicum in Management Accounting", "Cooperatives", "Principles", "Practices", "Management", "Cooperative law", "Cooperative finance", "Cooperative marketing", "Cooperative governance", "Cooperative development", "Cooperative Principles and Philosophy", "Cooperative Law and Policy", "Cooperative Finance and Accounting", "Cooperative Marketing and Sales", "Cooperative Governance and Management", "Cooperative Development and Entrepreneurship", "Cooperative Economics and Policy", "Cooperative Education and Training", "Cooperative Case Studies and Fieldwork", "Past events", "Societies", "Civilizations", "Comprehensive understanding", "Causes", "Impact", "Ancient history", "Medieval history", "Modern history", "World history", "Regional History", "Historiography", "Research Methods in History", "History of Ideas", "Social and Cultural History", "History of Science and Technology", "Economic theories", "Economic Development", "Research Methods in Economics", "Human mind", "Behavior", "Cognitive processes", "Social behavior", "Statistical analysis", "Empirical studies", "Introduction to Psychology", "Research Methods in Psychology", "Personality Theories", "Psychopathology", "Statistics for Psychology", "Biological Psychology", "Sociology", "Sociological Theories", "Social Statistics", "Social Class", "Race and Ethnicity", "Gender", "Social Change", "Social Inequality", "Introductory Sociology", "Classical Sociological Theories", "Family", "Education", "Religion", "Crime", "Global Sociology", "Urban Sociology", "Rural Sociology", "Economic Sociology", "Environmental Sociology", "Medical Sociology", "Law and Society", "Social Policy", "Social Movements", "Qualitative Research Methods", "Quantitative Research Methods", "Sociological Data Analysis", "Sociological Research Ethics", "Natural Sciences", "Applied Statistics", "Statistical Theory", "Statistical Methods", "Data Analysis", "Probability", "Regression Analysis", "Data Visualization", "Machine Learning", "Survey Sampling", "Statistical Consulting", "Applied Mathematics", "Actuarial Mathematics", "Mathematical Theory", "Statistical Modeling", "Calculus", "Probability Theory", "Insurance Mathematics", "Pension Mathematics", "Biology", "Living organisms", "Cellular biology", "Molecular biology", "Genetics", "Ecology", "Evolution", "Physiology", "Biochemistry", "Chemistry", "Matter", "Properties", "General Chemistry", "Organic Chemistry", "Physical Chemistry", "Food Technology", "Food Science", "Food Processing", "Food Safety", "Food Chemistry", "Food Microbiology", "Food Engineering", "Food Analysis", "Food Preservation", "Food Quality Assurance", "Mathematics", "Mathematical Principles", "Algebra", "Geometry", "Number Theory", "Mathematical Modeling", "Differential Equations", "Numerical Analysis", "Mathematical Logic", "Nutrition Science", "Dietetics", "Human Health", "Nutritional Assessment", "Medical Nutrition Therapy", "Community Nutrition", "Public Health", "Wellness Coaching", "Physics", "Classical Mechanics", "Electromagnetism", "Quantum Mechanics", "Thermodynamics", "Statistical Mechanics", "Optics", "Astrophysics", "Quantum Optics", "Solid-State Physics", "Nuclear Physics", "Architecture and Design", "Architectural Design", "Architectural History", "Building Construction", "Structural Systems", "Environmental Systems", "Architectural Theory", "Urban Design", "Sustainable Architecture", "Digital Design", "Interior Design", "Landscape Architecture", "Historic Preservation", "Construction Management", "Building Codes and Regulations", "Professional Practice", "Design Theory and Principles", "Architectural Drawing and Drafting", "Space Planning and Layout", "Color Theory and Application", "Materials and Finishes", "Lighting Design", "Furniture Design and Selection", "Textiles and Upholstery", "History of Interior Design", "Sustainable Design Practices", "Computer-Aided Design (CAD) and Visualization", "Interior Design Studio", "Residential Design", "Commercial Design", "Hospitality Design", "Retail Design", "Exhibition and Set Design", "Professional Practice and Ethics in Interior Design", "Environmental Planning", "Sustainable Development and Land Use Planning", "Environmental Policy and Governance", "Urban Design and Planning", "Geographic Information Systems (GIS) for Environmental Planning", "Environmental Impact Assessment", "Climate Change Adaptation and Resilience Planning", "Transportation Planning and Sustainable Mobility", "Natural Resource Management", "Environmental Law and Regulations", "Social Equity and Environmental Justice", "Coastal Zone Management", "Sustainable Tourism Planning", "Urban Revitalization and Regeneration", "Environmental Economics and Finance", "Environmental Psychology and Behavior", "Community Engagement and Participatory Planning", "Research Methods in Environmental Planning", "Environmental Modeling and Simulation", "Professional Practice and Ethics in Environmental Planning", "Health Sciences", "Clinical Chemistry", "Analytical Chemistry", "Pharmacology", "Bacteriology", "Mycology-virology", "Parasitology", "Hematology", "Immunology/Serology", "Immunohematology (Blood Banking)", "Endocrinology", "Toxicology", "Drug Testing", "Histopathologic", "Cytologic Techniques", "Human Anatomy and Physiology", "Laboratory Management", "Medical Technology Laws", "Bioethics", "Nutrition", "Primary Health Care", "Reproductive Health", "Family Planning", "Pregnancy", "Neonatal Care", "Moral and Legal Principles", "Midwifery Pharmacology", "Nursing", "Health Assessments", "Postoperative Care", "Life-saving Interventions", "Medical Documentation", "First Aid Techniques", "Care for Patients with Special Needs", "Patient Classification Systems", "Delegation of Tasks", "Health Education", "Nursing Leadership and Management", "Nursing Research", "Occupational Therapy", "Mental Health", "Emotional Health", "Activities of Daily Living", "Rehabilitation", "Anatomy", "Human Development", "Orthotics and Prosthetics", "Pharmacy", "Medicinal Drugs", "Cosmetics", "Drug Delivery Systems", "Pharmaceutical Biochemistry", "Pharmacognosy", "Quality Control", "Clinical Toxicology", "Pharmacy Administration", "Healthcare System", "Physical Therapy", "Therapeutic Exercises", "Kinesiology", "Neuroanatomy", "Radiologic Technology", "Medical Imaging Equipment", "X-rays", "CT Scanners", "MRIs", "Sonogram Machines", "Radiation Therapy", "Radiobiology", "Radiation Protection", "Quality Assurance and Control", "Respiratory Therapy", "Cardiopulmonary Disorders", "Pulmonary Rehabilitation", "Airway Management", "Mechanical Ventilation", "Health Care Ethics", "Teaching in Healthcare Setting", "Human Communication", "Anatomy and Physiology", "Neuroanatomy and Physiology", "Language Development", "Speech and Hearing Science", "Counseling", "Language Processing", "Professional Development", "Audiology", "Augmentative and Alternative Communication", "Dysphagia", "Physical Activity", "Biomechanics", "Exercise Physiology", "Psychology in Sports", "Coaching", "Library and Information Science", "Information Sources", "Collection Management", "Organization of Information", "Information Technology", "Archives Management", "Library Literature", "Media Specialist", "Academic Libraries", "Public Libraries", "Special Libraries", "Aircraft", "Spacecraft", "Aerodynamics", "Aircraft Design", "Aircraft Maintenance", "Aviation Safety", "Engineering", "Research and Development", "Manufacturing", "Operation", "Maintenance"

        )
        db?.beginTransaction()
        try {
            val values = ContentValues()
            for (tag in initialTags) {
                values.clear()
                values.put(COL_TAG_NAME, tag)
                db?.insert(TABLE_TAGS, null, values)
            }
            db?.setTransactionSuccessful()
        } finally {
            db?.endTransaction()
        }
    }

    private fun insertInitialTags2(db: SQLiteDatabase?) {
        val initialTags = listOf(
            "Professional Historian", "School Administrator", "Museum Staff", "Historical Researcher", "Historical Consultant", "Legal Aide", "Diplomatic Service", "Professor", "Management Trainee", "Writer", "Human Resource Assistant", "Labor Relations Manager", "Training Officer", "Industrial Designer", "Product Designer", "Multimedia Designer", "Graphic Artist", "Packaging Designer", "Professor/Art Educator", "Art-Related Positions", "Painter", "Illustrator", "Production Designer", "Visual Communicators", "Costume Designer", "Professor/Art Educators", "Art Critics", "Art Historians", "Fine Art Appraiser", "Sculptor", "Food Stylist", "Advertising Artist", "Graphic Designer", "New Media Specialist", "Costumes and Fashion Designer", "Auditor", "Economics Instructor", "Economic Development Officer", "Government Policy Analyst", "Investment Banker", "Financial Analyst", "Researcher", "Stock Broker", "Psychologist", "Mental Health Counselor", "Human Resources Specialist", "Social Worker", "Market Research Analyst", "Rehabilitation Specialist", "School Counselor", "Case Manager", "Research Assistant", "Nonprofit Program Coordinator", "Political Analyst", "Government Official", "Diplomat/Foreign Service Officer", "Political Campaign Staff", "Nonprofit/NGO Advocate", "Policy Analyst", "Lobbyist", "Journalist/Political Correspondent", "Researcher/Academic", "Legal Professional", "Certified Public Accountant (CPA)", "Tax Consultant", "Management Accountant", "Forensic Accountant", "Internal Auditor", "Budget Analyst", "Financial Controller", "Financial Planner", "Business Analyst", "Marketing Manager", "Human Resources Manager", "Operations Manager", "Entrepreneur/Small Business Owner", "Management Consultant", "Sales Manager", "Supply Chain Manager", "Project Manager", "Business Development Manager", "Risk Analyst", "E-commerce Manager", "International Business Manager", "Corporate Social Responsibility Manager", "Business Systems Analyst", "Retail Store Manager", "Public Relations Manager", "Investment Manager", "Risk Manager", "Corporate Finance Manager", "Banking Professional", "Financial Consultant", "Insurance Manager", "Treasury Analyst", "Financial Compliance Officer", "Cost Accountant", "Cooperative Manager", "Cooperative Development Officer", "Cooperative Consultant", "Cooperative Educator", "Cooperative Researcher", "Cooperative Policy Analyst", "Cooperative Social Entrepreneur", "Cooperative Development Specialist", "Historian", "Museum Curator", "Archivist", "Teacher/Professor", "Writer/Author", "Journalist", "Cultural Heritage Specialist", "Librarian", "Economist", "Data Analyst", "Investment Analyst", "Economic Consultant", "Government Economist", "Psychiatric Technician", "Human Resources Assistant", "Mental Health Technician", "Career Counselor", "Social Services Coordinator", "Behavioral Health Technician", "Sociologist", "Community organizer", "Market researcher", "Probation officer", "Parole officer", "Teacher", "Statistician", "Research Analyst", "Data Scientist", "Quality Control Analyst", "Actuary", "Research Consultant", "Academia", "Insurance Underwriter", "Pension Consultant", "Consultant", "Research Scientist", "Laboratory Technician", "Environmental Consultant", "Wildlife Biologist", "Pharmaceutical Sales Representative", "Science Educator", "Biotechnologist", "Environmental Scientist", "Science Writer/Communicator", "Biomedical Researcher", "Chemist", "Analytical Chemist", "Materials Scientist", "Chemical Engineer", "Food Technologist", "Quality Control Specialist", "Food Safety Inspector", "Research and Development Scientist", "Food Regulatory Specialist", "Food Quality Assurance Manager", "Food Product Development Manager", "Food Packaging Specialist", "Food Safety Consultant", "Food Industry Sales Representative", "Mathematician", "Operations Research Analyst", "Software Developer", "Mathematics Educator", "Quantitative Analyst", "Registered Dietitian/Nutritionist", "Clinical Dietitian", "Community Nutritionist", "Food Service Manager", "Nutrition Researcher", "Wellness Coach", "Food and Nutrition Writer/Blogger", "Corporate Wellness Coordinator", "Nutrition Consultant", "Public Health Nutritionist", "Physicist", "Astrophysicist", "Quantum Physicist", "Optics Engineer", "Computational Physicist", "Nuclear Physicist", "Architect", "Architectural Designer", "Urban Planner", "Interior Designer", "Landscape Architect", "Construction Manager", "Building Inspector", "Historic Preservation Specialist", "Sustainability Consultant", "Building Information Modeling (BIM) Specialist", "Residential Interior Designer", "Commercial Interior Designer", "Hospitality Interior Designer", "Retail Interior Designer", "Exhibition Designer", "Set Designer", "Furniture Designer", "Design Consultant", "Design Project Manager", "Design Educator", "Design Researcher", "Design Stylist", "Design Writer or Blogger", "Design Entrepreneur", "Environmental Planner", "Land Use Planner", "Environmental Policy Analyst", "Community Development Planner", "Transportation Planner", "Climate Change Specialist", "Natural Resource Manager", "Environmental Impact Assessment Specialist", "Coastal Zone Manager", "Sustainable Tourism Planner", "Urban Revitalization Specialist", "Environmental Researcher", "Environmental Educator", "Non-profit Organization Manager", "Government Planner", "GIS Analyst", "Environmental Advocate", "Medical Technologist", "College Professor", "Midwife", "Health Facility Administrator", "Clinic Manager", "Health Program Manager", "College Professor/Trainer", "Clinic Nurse", "Community Health Nurse", "Private Nurse", "Company Nurse", "School Nurse", "Military Nurse", "Entrepreneur", "Licensed Occupational Therapist", "Advice Worker", "Art Therapist", "High Intensity Therapist", "Life Coach", "Medical Sales Representative", "Play Therapist", "Psychological Wellbeing Practitioner", "Special Educational Needs Teacher", "Teaching Assistant", "Pharmacist", "Clinical Researcher", "Product Manager", "Food and Drug Regulation Officer", "Military Pharmacist", "Forensic Analyst", "Physical Therapy Clinician", "Educator", "Radiologic Technologist", "Ultrasound Technologist", "Magnetic Resonance Imaging Technologist", "Mammography Technologist", "Computed Tomography Technologist", "Interventional Radiology Technologist", "Radiation Therapy Technologist", "Nuclear Medicine Technologist", "Respiratory Therapist", "Medical Service Practitioner", "College Instructor", "Product Specialist", "Academic Writer", "Clinician", "Administrator", "Advocate", "Physical Education Teacher", "Sports Coach", "Fitness Trainer", "Umpire/Referee", "Sports Facility Administrator", "Abstractor", "Bibliographer", "Information Scientist", "Media or Audio Visual Specialist", "Aircraft Structural Engineer", "Aircraft Design Engineer", "Aircraft Power Plant Engineer", "Aircraft Manufacturing Engineer", "Aircraft Maintenance Engineer", "Aircraft Operation/Performance Engineer", "Ceramics/Refractory Engineer", "Ceramic/Materials Engineer", "Manager in Glass Industry", "Ceramics Quality Engineer", "Ceramic Process Engineer", "Senior Ceramic Engineer", "Development Specialist", "Ceramic Technician", "Ceramic Machinist", "Material Scientist", "Ceramic Designer", "Project Head", "Academician", "Project Staff", "Food and Drug Manufacturing", "Packaging Technologies", "Environmental Management", "Petrochemical Engineering", "Energy Engineering", "Biotechnology", "Paints and Coating Technology", "Semiconductor Technology", "Entrepreneurship", "Electronics Engineer", "Geodetic Engineer", "Cartographer"   )
        db?.beginTransaction()
        try {
            val values = ContentValues()
            for (tag in initialTags) {
                values.clear()
                values.put(COL_TAG_NAME2, tag)
                db?.insert(TABLE_TAGS2, null, values)
            }
            db?.setTransactionSuccessful()
        } finally {
            db?.endTransaction()
        }
    }

    fun removeTag(tagName: String) {
        val normalizedTagName = tagName.trim().lowercase(Locale.getDefault())
        Log.d("RemoveTag", "Tag name to delete: $normalizedTagName")

        val db = this.writableDatabase

        // Check if the tag exists before deletion (ignoring case)
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TAGS WHERE LOWER($COL_TAG_NAME) = ?", arrayOf(normalizedTagName))
        if (cursor.count > 0) {
            Log.d("RemoveTag", "Tag found in the database before deletion")
        } else {
            Log.d("RemoveTag", "Tag not found in the database before deletion")
        }
        cursor.close()

        // Define the WHERE clause to match the tag name (ignoring case)
        val whereClause = "LOWER($COL_TAG_NAME) = ?"

        // Define the value to replace the placeholder in the WHERE clause
        val whereArgs = arrayOf(normalizedTagName)

        // Perform the delete operation using a parameterized query
        val deletedRows = db.delete(TABLE_TAGS, whereClause, whereArgs)

        Log.d("RemoveTag", "Deleted rows: $deletedRows")

        // Check if the tag still exists after deletion (ignoring case)
        val cursorAfterDeletion = db.rawQuery("SELECT * FROM $TABLE_TAGS WHERE LOWER($COL_TAG_NAME) = ?", arrayOf(normalizedTagName))
        if (cursorAfterDeletion.count > 0) {
            Log.d("RemoveTag", "Tag still found in the database after deletion")
        } else {
            Log.d("RemoveTag", "Tag not found in the database after deletion")
        }
        cursorAfterDeletion.close()
    }


    fun removeTag2(tagName: String) {
        val normalizedTagName = tagName.trim().lowercase(Locale.getDefault())
        Log.d("RemoveTag", "Tag name to delete: $normalizedTagName")

        val db = this.writableDatabase

        // Check if the tag exists before deletion (ignoring case)
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TAGS2 WHERE LOWER($COL_TAG_NAME2) = ?", arrayOf(normalizedTagName))
        if (cursor.count > 0) {
            Log.d("RemoveTag", "Tag found in the database before deletion")
        } else {
            Log.d("RemoveTag", "Tag not found in the database before deletion")
        }
        cursor.close()

        // Define the WHERE clause to match the tag name (ignoring case)
        val whereClause = "LOWER($COL_TAG_NAME2) = ?"

        // Define the value to replace the placeholder in the WHERE clause
        val whereArgs = arrayOf(normalizedTagName)

        // Perform the delete operation using a parameterized query
        val deletedRows = db.delete(TABLE_TAGS2, whereClause, whereArgs)

        Log.d("RemoveTag", "Deleted rows: $deletedRows")

        // Check if the tag still exists after deletion (ignoring case)
        val cursorAfterDeletion = db.rawQuery("SELECT * FROM $TABLE_TAGS2 WHERE LOWER($COL_TAG_NAME2) = ?", arrayOf(normalizedTagName))
        if (cursorAfterDeletion.count > 0) {
            Log.d("RemoveTag", "Tag still found in the database after deletion")
        } else {
            Log.d("RemoveTag", "Tag not found in the database after deletion")
        }
        cursorAfterDeletion.close()
    }





    fun getFirst20Tags(): List<String> {
        val tags = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT $COL_TAG_NAME FROM $TABLE_TAGS"
        val cursor = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val tagName = cursor.getString(cursor.getColumnIndex(COL_TAG_NAME))
                tags.add(tagName)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        db.close()

        return tags
    }

    fun getFirst20Tags2(): List<String> {
        val tags = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT $COL_TAG_NAME2 FROM $TABLE_TAGS2"
        val cursor = db.rawQuery(query, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val tagName = cursor.getString(cursor.getColumnIndex(COL_TAG_NAME2))
                tags.add(tagName)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        db.close()

        return tags
    }
}


// Add other methods for database operations related to user data here

