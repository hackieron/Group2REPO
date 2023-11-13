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
            "Environmental Planning", "Forensic toxicology", "Uses", "Drawing", "Crime scene investigation", "Information Security", "Phonetics", "Information Technology", "Resource Economics", "Visual Communication", "Marine Vessels", "Seaman", "Agricultural Policy", "Managerial Economics", "Environmental Protection", "Form", "Quality Assurance", "Molecular Techniques", "Agroforestry", "Mathematical Economics", "Color Theory and Application", "Criminal behavior patterns", "Investigation", "Legislation", "Medical Documentation", "Psycholinguistics", "Archaeology", "Islamic theology", "Space Planning and Layout", "Franchising", "Health Psychology", "Principles of psychology", "Food Technology", "Human-Computer Interaction", "Chemical Instrumentation", "Distribution", "Medieval history", "acrylic", "Communication", "Practical experience", "Calculus", "Real Estate Management", "Care for Patients with Special Needs", "X-rays", "Basic Microeconomics", "Cooperatives", "Historical Methodology", "Issues", "Digital Marketing", "Optics", "Economic Planning and Development", "Financial Software", "Inventory Management", "Philosophy of Person", "Practical skills", "Sociological Data Analysis", "Social dynamics", "Probability Theory", "Strategic Financial Management", "Experimental Design", "Radiologic Technology", "Language Development", "Historical Context", "Social interactions", "Environmental Impact Assessment", "Socio-economic aspects of fisheries", "Risk Management and Internal Control", "Human Rights and International Law", "Psychology in Sports", "Collection Management", "Social Welfare", "Healthcare System", "Psychological theories", "Toxicology", "Computer Science", "Cardiopulmonary Disorders", "Space Planning", "Midwifery Pharmacology", "Climatology", "Financial Accounting", "Creative writing", "Network Administration", "European History", "Construction Techniques", "Professional Practices", "Graph Theory", "Artistic voice", "Nursing", "Software Engineering", "Emergency Services", "Equipment", "Mental Health", "Endocrinology", "Cooperative law", "Intermediate Microeconomics", "Social Research", "Spectroscopy", "Furniture Design", "Crime prevention strategies", "Quantitative Methods", "Wellness Coaching", "Art history", "Hematology", "Scholarly discussions", "Comprehensive understanding", "Quality Assurance and Control", "Production", "Financial Modeling and Forecasting", "Natural Language Processing", "Islamic principles and values", "Ecotourism", "Social Inequality", "Business", "Mine Feasibility", "Food and Beverage Control", "Human Development", "Basic Macroeconomics", "Crop Production", "Social Statistics", "Business management", "International Agribusiness", "Techniques", "Literary analysis", "Poetry", "Psychopathology", "Organic Chemistry", "Social behavior", "Industrial Machines", "Vehicles", "Communication Research", "Social data analysis", "Probability and Statistics", "Social inequalities", "Sanitation", "Introductory Sociology", "Molecular biology", "Biodiversity Conservation", "Cooperative Economics and Policy", "Salesmanship", "Risk Management and Insurance", "Landscape Architecture", "Accounting principles", "Social Movements", "Real-world contexts", "Research in Management Accounting", "Nutrition", "Environmental Sociology", "Residential Design", "Professional Ethics", "Human Communication", "Food Chemistry", "Parasitology", "Audiology", "Enterprise Resource Planning", "Patient Classification Systems", "Hadith studies", "Business Information Systems", "Agricultural Technology", "Health Sciences", "Sociological Theories", "Natural Sciences", "Digital Design", "Production for Electronic Media", "Chemistry", "Machine Learning", "Landscape Design", "Diet", "Package Design", "Economic analysis", "Textiles and Upholstery", "Gas", "Government", "Materials Chemistry", "Sculpting techniques", "Environmental Economics", "Political phenomena", "Auditing and Assurance Services", "Color", "Aircraft", "Advertising Design", "Abnormal psychology", "Performance Evaluation", "Writing skills", "Statistical Theory", "Differential Calculus", "Philosophy of History", "Research facilities", "Economic systems", "Physical Chemistry", "Power of language", "Housekeeping", "Research Methods in Psychology", "Advertisements", "Marketing Management", "Exhibition and Set Design", "Past events", "Histopathologic", "Electrochemistry", "Cost Accounting", "Philosophy of Religion", "Business Communication", "Professional Practice and Ethics in Interior Design", "Advanced Excel", "Ethical Issues", "Political Communication", "Language and society", "Safety", "Criminology", "Chemical Bonding", "History of Interior Design", "Retail Design", "Erosion", "Venture Capital", "Administrative", "Software Applications", "International relations", "Cooperative Marketing and Sales", "Policy", "Data Structures", "Production Techniques", "Labor", "Literature", "Navigation", "Impact", "Qualitative Research Methods", "Human Anatomy and Physiology", "Filipino literature", "Genetic Engineering", "Cultural and historical significance", "Financial Derivatives and Risk Management", "Structural Design", "Building Codes", "Statistical Quality Control", "Information Systems for Management Accounting", "Diversity of human experiences", "Filipino language", "Research methods", "Perceptual skills", "Import", "Urban Design and Planning", "Figure Drawing", "Hospitality", "Nursing Leadership and Management", "Strategic Management", "Fiction", "Linguistics", "Political Sociology", "Mathematical Logic", "Export", "Architectural History", "Data Mining", "Web Development", "Media Platforms", "Pulmonary Rehabilitation", "Regional History", "Aircraft Design", "Development", "Dietetics", "Pharmaceutical Biochemistry", "Cost", "Artistic representations", "Quality Management", "Capital", "Appliances", "Pregnancy", "Advantages", "Market Research", "Forms of expression", "Inorganic Chemistry", "Community Nutrition", "Oil", "Economic", "Business ethics", "Electronics", "Religion", "General Chemistry", "Personality psychology", "Systems Analysis", "Differential Equations", "Laboratory Management", "Artificial Intelligence", "Nanotechnology", "Building Codes and Regulations", "Delegation of Tasks", "Islamic History", "Radiation Therapy", "Library and Information Science", "Radio", "Cultures", "Financial Reporting", "Mediums (oil", "Data", "Painting", "Computational Chemistry", "Food Quality Assurance", "Immunohematology (Blood Banking)", "Geographic Information Systems (GIS) for Environmental Planning", "Cost Analysis", "Anatomy", "Natural Resource Management", "Science Reporting", "Marine biology", "Health Assessments", "Economic Benefits", "Social", "Coastal areas", "Building Systems", "Social Equity and Environmental Justice", "Medicinal Chemistry", "Technology Applications", "watercolor", "Forensic anthropology", "Architectural Visualization", "Solid State Physics", "Physical Therapy", "Reproductive Health", "Developmental psychology", "Philosophy of Science", "Neuroanatomy and Physiology", "Plant Selection", "Processes", "Scientific research", "Empirical research", "Statistical Consulting", "Drug Testing", "Immunology", "Petroleum", "Carbon Sequestration", "Radiation Protection", "Sustainable Farming Practices", "Decision-making", "Creative Vision", "Environmental Systems", "Metals", "Budgeting and Forecasting", "Data Science", "Astrophysics", "Historiography", "Ancient history", "Environmental Science", "Cellular biology", "Pharmacognosy", "Radiobiology", "Economic Development", "Algorithms", "Biochemistry", "Linear Algebra", "Cultural", "Urban Planning", "Health Care Ethics", "Engineering", "Therapeutic Exercises", "Professional Development", "Business Ethics and Corporate Governance", "Aquaculture practices", "Laboratory techniques", "Testing Devices", "Bayesian Statistics", "Econometrics", "Computer Networks", "Environmental Law and Regulations", "Political Philosophy", "Budgeting", "Office Management", "stone", "Evolution", "Properties", "Forest ecosystems", "Computer Programming", "Regression Analysis", "Research Methods in Environmental Planning", "Sustainable management of aquatic resources", "Diverse communities and cultures", "Computational Physics", "Statistical Computing", "Mycology-virology", "Rebuilding", "Forestry", "mixed media)", "International Trade", "Programming", "Watershed management", "Drilling", "Cooperative Principles and Philosophy", "Mechanical Ventilation", "Public policy", "Mobile Application Development", "Regional Economics", "Ecosystems", "Social Class", "Materials Engineering", "Drug Delivery Systems", "Organization of Information", "Climate Change Adaptation and Resilience Planning", "Art Workshop", "Opinion Writing", "Retail Management", "Landscape Ecology", "Ergonomics", "Facilities", "Online Journalism", "Gender", "Coaching", "Sales Management", "Landscape Graphics and Visualization", "Architecture and Design", "Business Law", "Metaphysics", "Computer-Aided Design (CAD) and Visualization", "Wilderness management", "Forestry practices and challenges", "Energy", "Interactions with other religions and civilizations", "Customer Relations", "Electoral Systems and Voting Behavior", "Clinical Toxicology", "Cooperative development", "Graphic design", "Business Administration", "Geology", "International Studies", "Policy and Governance", "Crime scenes", "Composition", "Human evolution", "Linguistic analysis", "Cooperative governance", "Economic theories", "Architectural Drawing and Drafting", "Airway Management", "Environmental issues", "Site Analysis", "Operations", "Language teaching", "Mathematical Principles", "Historic Preservation", "Activities of Daily Living", "Environmental Politics", "Survey Methodology", "Educational Psychology", "Macroeconomics", "Customs", "Civilizations", "Visual Perception", "Construction", "Health", "Sculptural Movements", "CT Scanners", "Investment Analysis and Portfolio Management", "Family", "Professional Ethics and Values", "Security", "Forensics", "Philippine Economics History", "Belief systems", "Matter", "Physiological psychology", "Sociological Research Ethics", "Survey Sampling", "News Reporting", "Urban Economics", "Islamic jurisprudence", "Auditing", "Political theories", "Critical thinking", "Plate tectonics", "Web design", "Leadership", "Sustainable Tourism Planning", "Social and cultural factors", "Medicinal Drugs", "Environment", "Computer Graphics", "Coastal Zone Management", "Neuroanatomy", "Economic Sociology", "Technical Specifications", "Analytical skills", "Introduction to Business", "Culinary", "Fish farms", "Dysphagia", "Maintenance", "Journalism", "Sustainable Development and Land Use Planning", "Water Supply", "Managerial Accounting", "Nationalism", "Mass Communication Research", "Consulting", "Decision Support Systems", "Research projects", "found objects)", "Business Research Methods", "English language", "Cell Biology", "Public Policy Analysis", "Industrial and Organizational Psychology", "Forensic Engineering", "Social structures", "Molecular Virology", "Cosmetics", "Environmental assessments", "Mining Methods", "Molecular Diagnostics", "Project Management", "Propulsion Plant", "Criminal Justice", "Primary Health Care", "Mergers and Acquisitions", "Data Visualization", "Industrial Chemistry", "Agriculture", "Pollution Control", "Western Civilizations", "Mathematics", "Tourism Planning", "Critical analysis", "Extraction", "Forest management techniques", "Classical Sociological Theories", "Coordination Chemistry", "Postoperative Care", "Financial economics", "Physiology", "Food Analysis", "Physical Activity", "Practices", "Financial analysis", "Library Literature", "Government structures", "Institutions", "Geological Engineering", "Gender and Politics", "Media Law and Ethics", "Social and economic aspects of forestry", "Research", "Rock formations", "Obligations and Contracts", "Sustainable Design Practices", "Hygiene", "Financial management", "Statistics", "Media Management", "Ethics", "Naval Architecture", "Manufacturing", "Visual Verbal Communication", "Expressing interests and ideas", "Soil Science", "Production Methods", "Special Libraries", "Print Media", "Islamic ethics", "Therapy", "Renewable Energy", "Neonatal Care", "Design Workshop", "Risk management", "Cross-Cultural Psychology", "Information Architecture", "Environmental policy", "Solid-State Physics", "Event Management", "Space", "Forensic biology", "Language use", "IT Governance", "Geotechnical", "Financial Markets and Institutions", "Marine conservation", "Animal Husbandry", "Color Theory", "Performance Evaluation and Control", "Strategic Decision-making", "Marine Transportation", "Introduction to Financial Accounting", "Statistical Inference", "Development Communication", "Peace", "Food Microbiology", "Statistical Methods", "Chemical Kinetics", "Spacecraft", "Financial Technology (Fintech) and Innovation in Finance", "Anatomy and Physiology", "Business Law and Ethics", "IT Strategy", "Design Theory", "Cooperative Finance and Accounting", "Genetics", "Aviation Safety", "Business Process Management", "Medical Technology Laws", "Business Law and Regulations", "Social Policy", "Advertising", "Prevention of crime", "Goods", "Family Planning", "Real-world settings", "Criminal justice system", "Public Health", "Fisheries management techniques", "Counseling and Psychotherapy", "Medical Imaging Equipment", "Financial Modeling", "Operations management", "History and Systems of Psychology", "News Writing", "Resources", "Social and economic factors", "Design Theory and Principles", "Protocol", "Time Series Analysis", "New media design", "Digital Techniques", "Visual Storytelling", "Communication Theories", "Corporate finance", "Occupational Therapy", "Agribusiness", "Applied Physics", "Trade", "Finance", "Fine Arts", "Environmental Policy and Governance", "Sculpture", "Quality Control", "Integral Calculus", "Moral and Legal Principles", "Microbiology", "Fluid Mechanics", "Program Development", "Office Administration", "Drawing Techniques", "Sustainable Design", "Political economy", "Rehabilitation", "Characteristics", "Existentialism", "Numerical Analysis", "Commercial Design", "Materials", "Maritime Law", "Academic Libraries", "Forensic Psychology", "Political Factors", "Appraisal", "Tariffs", "Kinesiology", "Communication skills", "IT Ethics", "Economic Research", "Logistics", "Conflict Resolution and Peace Studies", "Mineral Deposit Assessment", "Cloud Computing", "wood", "Financial markets", "Urban Revitalization and Regeneration", "Reservoir", "Shorthand", "Geological hazards", "Personality Theories", "Moral Philosophy", "Professional Practice and Ethics in Environmental Planning", "Marine Pollution", "Sociology", "Intermediate Macroeconomics", "Counseling", "Cultural practices", "Investigative Journalism", "Technical skills", "Community Development", "Microeconomics", "International Business", "Prototyping", "Research abilities", "Orthotics and Prosthetics", "Laboratory Methods", "Database Management", "Health Economics", "Cooperative Education and Training", "Urban Design", "Materials and Finishes", "Broadcast Journalism", "Biological Psychology", "Research Methods in Political Science", "Materials (clay", "Food Science", "Modern history", "Materials for Industrial Design", "Visual Studies", "Political Science", "Multivariate Analysis", "Construction Management", "Economic Statistics", "Medical Nutrition Therapy", "Pharmacy Administration", "Laboratory experiments", "Cybersecurity", "Optimization", "Augmentative and Alternative Communication", "Life-saving Interventions", "Islamic faith", "Building Construction", "Human Health", "Emerging Technologies", "Statistical Modeling", "Hospitality Design", "Drama", "Cooperative Law and Policy", "Humanities", "Geological processes", "Sustainability", "Culture", "Global Governance", "Dynamics of society", "Financial Statements", "Forest policy and governance", "Psychology in Interior Design", "Human Resources", "Physics", "Exercise Physiology", "Metallurgical Engineering", "Sustainable development", "Financial planning", "Cooperative Development and Entrepreneurship", "Human mind", "Social Responsibility", "Supervisory", "Hands-on experience", "Disadvantages", "Environmental Modeling and Simulation", "Revolution", "International finance", "Behavior", "Pharmacology", "Motion graphics", "Mathematical Modeling", "Bioethics", "Ethnography", "Database Systems", "Global Sociology", "Human societies", "Volcanism", "Emotional Health", "Diplomacy", "Logic", "Sampling", "Genres of literature", "Forest inventory and assessment", "Living organisms", "Management Information System", "Scientific principles", "Epistemology", "Economic principles", "Social Change", "Public Economics", "Conversion", "Information Campaigns", "Conservation biology", "Consultancy", "Causes", "Translation", "Critical thinking abilities", "Brokerage", "Agroforestry Field Techniques", "Economic policy", "Research Methods in History", "Environmental Psychology and Behavior", "Literary theory", "Scientific communication", "Mathematical Theory", "Operations Research", "Ship Business", "Social and Cultural History", "Social Work", "Biomechanics", "Fieldwork", "Agricultural Economics", "Minerals", "Editing", "Legal Issues", "Archives Management", "Human resource management", "Cognitive psychology", "Applied Statistics", "Business Statistics", "Electromagnetism", "Visual Design", "Experimental Physics", "Transportation Planning and Sustainable Mobility", "Photojournalism", "Empirical studies", "Water Management", "Plant Science", "Television", "Plasma Physics", "Consumer Behavior", "Geometry", "Supply Chain", "Molecular Evolution", "Interior Design", "Immunology/Serology", "Taxation", "Government Systems", "Medical Physics", "Plant", "Classical Mechanics", "History of Science and Technology", "Semantics", "Verbal and Non-verbal Messages", "Health Care", "Illustration", "Biotechnology", "Education", "Arts and Design", "Language variation", "Social institutions", "Nonparametric Statistics", "Bioinformatics", "Sustainable Land Management", "Data Analytics", "Management", "Contemporary Practices", "Climate Change Adaptation", "Cooperative finance", "Operation", "Language acquisition", "Nutritional Assessment", "Historical processes", "Retail", "Ecology", "User Experience Design", "Nuclear Physics", "Art Theory", "Internship/Practicum", "Science", "E-Commerce", "Statistics for Psychology", "Political systems", "Nursing Research", "Speech and Hearing Science", "Aerodynamics", "Quantitative Research Methods", "Abstract Algebra", "Race and Ethnicity", "Practical Studies", "Livestock Management", "Systems Design", "Accounting", "American Government and Politics", "MRIs", "Genres", "Quranic studies", "Technical Writing", "Public Safety", "Transcription", "Principles", "Number Theory", "Respiratory Therapy", "Biomedical Equipment", "Computer Architecture", "Topology", "Quantum Electronics", "Cooperative Governance and Management", "Reporting the Arts and Culture", "Probability", "Architectural Design", "Capstone Project", "Principles of Marketing", "Community Engagement and Participatory Planning", "Cultural History", "Programs", "Statistical analysis", "History", "Media Specialist", "Forensic chemistry", "Financial Mathematics", "Environmental Reporting", "Three-dimensional art", "Comparative politics", "Financial Statement Analysis", "Events Management", "Art Movements", "Economic Policy Analysis", "Research Methods in Economics", "Social Sciences", "Literary genres", "Quantum Optics", "Bacteriology", "Positive Psychology", "East Asian", "Evaluation", "Internship/Practicum in Management Accounting", "Data Modeling", "Arts & Design", "Forest recreation", "Data Analysis", "Information Systems", "Technology", "Neuropsychology", "Cytologic Techniques", "Labor Economics", "Aquaculture", "Polymer Chemistry", "Language Processing", "Organizational Behavior", "Information Sources", "Texture", "Cooperative marketing", "Political Sciences", "Sonogram Machines", "Public Libraries", "Asian Civilizations", "Political Parties and Interest Groups", "Cooperative Case Studies and Fieldwork", "Sedimentation", "Structure", "Theoretical Studies", "Filipino culture", "Earth\'s structure", "Business Intelligence", "Law", "Law and Society", "Aircraft Maintenance", "Rural Sociology", "Typography", "Social psychology", "Marketing", "Interpretation", "Risk Assessment", "Service", "Advanced Management Accounting", "Complex Analysis", "Mathematical Physics", "Sampling Techniques", "Mental processes", "Natural resources", "Tourism", "Digital Media Design", "Environmental Sustainability", "Landscape Construction Techniques", "First Aid Techniques", "Statistical Mechanics", "Food", "Governance", "Marketing Strategy", "Health Education", "Fisheries practices and challenges", "Environmental chemistry", "Quantum Mechanics", "Psychology", "Crime", "Hands-on training", "Clerical", "Tourism Research", "Societies", "Politics", "Taxation and Tax Planning", "Food Processing", "Research and Development", "Machinery", "Law Enforcement", "Discrete Mathematics", "World history", "Sociolinguistics", "Corporate Governance", "Biology", "Product Design", "Filipino Philosophy", "Economics", "Regulations", "Administration", "metal", "Money and Banking", "Political Theory", "Lighting Design", "Mining Engineering", "Criminological theories", "Fisheries science", "Big Data Analytics", "Urban Sociology", "Real Analysis", "Investment management", "Client Communication", "Foreign Relations", "Branding and Identity Design", "Traditional and modern technology", "Language", "Furniture Design and Selection", "Language patterns", "Algebra", "Introduction to Psychology", "Molecular Pharmacology", "Tourism Management", "Cognitive processes", "Solutions to environmental challenges", "Engineering Principles", "Cultural appreciation", "Research Method in the Arts", "Feature Writing", "Operating Systems", "Political processes", "Software Testing", "Economic History", "Business Ethics and Corporate Social Responsibility", "Fire Protection", "Mathematical Optimization", "plaster", "Environmental Economics and Finance", "Teaching in Healthcare Setting", "Business and Economics Reporting", "History of Economic Thought", "Entrepreneurial Finance", "Applied Mathematics", "Pharmacy", "Photography", "Policy Analysis", "Industrial Organization", "Philippine Constitution and Government", "Thermodynamics", "Investment", "Services", "Contemporary issues in Islam", "Materials Science", "Cultural context", "Sustainable forestry practices", "Biology and ecology of trees", "Analytical Chemistry", "Syntax", "Product Development", "Clinical Chemistry", "Marine Engineering", "Travel and Tour Operation", "Quantitative methods in finance", "Public Opinion and Political Behavior", "Brand Management", "Public Administration", "Professional Practice", "Planning", "Medical Sociology", "History of Ideas", "International economics", "Development economics", "Languages", "Mineral Resources", "Entrepreneurship", "Supply Chain Management", "Food Engineering", "Anatomy/Physiology", "Journalism Principles and Practices", "Internships", "Human behavior", "Food Safety"
        ).sorted()
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
            "Environmental Planning", "Forensic toxicology", "Uses", "Drawing", "Crime scene investigation", "Information Security", "Phonetics", "Information Technology", "Resource Economics", "Visual Communication", "Marine Vessels", "Seaman", "Agricultural Policy", "Managerial Economics", "Environmental Protection", "Form", "Quality Assurance", "Molecular Techniques", "Agroforestry", "Mathematical Economics", "Color Theory and Application", "Criminal behavior patterns", "Investigation", "Legislation", "Medical Documentation", "Psycholinguistics", "Archaeology", "Islamic theology", "Space Planning and Layout", "Franchising", "Health Psychology", "Principles of psychology", "Food Technology", "Human-Computer Interaction", "Chemical Instrumentation", "Distribution", "Medieval history", "acrylic", "Communication", "Practical experience", "Calculus", "Real Estate Management", "Care for Patients with Special Needs", "X-rays", "Basic Microeconomics", "Cooperatives", "Historical Methodology", "Issues", "Digital Marketing", "Optics", "Economic Planning and Development", "Financial Software", "Inventory Management", "Philosophy of Person", "Practical skills", "Sociological Data Analysis", "Social dynamics", "Probability Theory", "Strategic Financial Management", "Experimental Design", "Radiologic Technology", "Language Development", "Historical Context", "Social interactions", "Environmental Impact Assessment", "Socio-economic aspects of fisheries", "Risk Management and Internal Control", "Human Rights and International Law", "Psychology in Sports", "Collection Management", "Social Welfare", "Healthcare System", "Psychological theories", "Toxicology", "Computer Science", "Cardiopulmonary Disorders", "Space Planning", "Midwifery Pharmacology", "Climatology", "Financial Accounting", "Creative writing", "Network Administration", "European History", "Construction Techniques", "Professional Practices", "Graph Theory", "Artistic voice", "Nursing", "Software Engineering", "Emergency Services", "Equipment", "Mental Health", "Endocrinology", "Cooperative law", "Intermediate Microeconomics", "Social Research", "Spectroscopy", "Furniture Design", "Crime prevention strategies", "Quantitative Methods", "Wellness Coaching", "Art history", "Hematology", "Scholarly discussions", "Comprehensive understanding", "Quality Assurance and Control", "Production", "Financial Modeling and Forecasting", "Natural Language Processing", "Islamic principles and values", "Ecotourism", "Social Inequality", "Business", "Mine Feasibility", "Food and Beverage Control", "Human Development", "Basic Macroeconomics", "Crop Production", "Social Statistics", "Business management", "International Agribusiness", "Techniques", "Literary analysis", "Poetry", "Psychopathology", "Organic Chemistry", "Social behavior", "Industrial Machines", "Vehicles", "Communication Research", "Social data analysis", "Probability and Statistics", "Social inequalities", "Sanitation", "Introductory Sociology", "Molecular biology", "Biodiversity Conservation", "Cooperative Economics and Policy", "Salesmanship", "Risk Management and Insurance", "Landscape Architecture", "Accounting principles", "Social Movements", "Real-world contexts", "Research in Management Accounting", "Nutrition", "Environmental Sociology", "Residential Design", "Professional Ethics", "Human Communication", "Food Chemistry", "Parasitology", "Audiology", "Enterprise Resource Planning", "Patient Classification Systems", "Hadith studies", "Business Information Systems", "Agricultural Technology", "Health Sciences", "Sociological Theories", "Natural Sciences", "Digital Design", "Production for Electronic Media", "Chemistry", "Machine Learning", "Landscape Design", "Diet", "Package Design", "Economic analysis", "Textiles and Upholstery", "Gas", "Government", "Materials Chemistry", "Sculpting techniques", "Environmental Economics", "Political phenomena", "Auditing and Assurance Services", "Color", "Aircraft", "Advertising Design", "Abnormal psychology", "Performance Evaluation", "Writing skills", "Statistical Theory", "Differential Calculus", "Philosophy of History", "Research facilities", "Economic systems", "Physical Chemistry", "Power of language", "Housekeeping", "Research Methods in Psychology", "Advertisements", "Marketing Management", "Exhibition and Set Design", "Past events", "Histopathologic", "Electrochemistry", "Cost Accounting", "Philosophy of Religion", "Business Communication", "Professional Practice and Ethics in Interior Design", "Advanced Excel", "Ethical Issues", "Political Communication", "Language and society", "Safety", "Criminology", "Chemical Bonding", "History of Interior Design", "Retail Design", "Erosion", "Venture Capital", "Administrative", "Software Applications", "International relations", "Cooperative Marketing and Sales", "Policy", "Data Structures", "Production Techniques", "Labor", "Literature", "Navigation", "Impact", "Qualitative Research Methods", "Human Anatomy and Physiology", "Filipino literature", "Genetic Engineering", "Cultural and historical significance", "Financial Derivatives and Risk Management", "Structural Design", "Building Codes", "Statistical Quality Control", "Information Systems for Management Accounting", "Diversity of human experiences", "Filipino language", "Research methods", "Perceptual skills", "Import", "Urban Design and Planning", "Figure Drawing", "Hospitality", "Nursing Leadership and Management", "Strategic Management", "Fiction", "Linguistics", "Political Sociology", "Mathematical Logic", "Export", "Architectural History", "Data Mining", "Web Development", "Media Platforms", "Pulmonary Rehabilitation", "Regional History", "Aircraft Design", "Development", "Dietetics", "Pharmaceutical Biochemistry", "Cost", "Artistic representations", "Quality Management", "Capital", "Appliances", "Pregnancy", "Advantages", "Market Research", "Forms of expression", "Inorganic Chemistry", "Community Nutrition", "Oil", "Economic", "Business ethics", "Electronics", "Religion", "General Chemistry", "Personality psychology", "Systems Analysis", "Differential Equations", "Laboratory Management", "Artificial Intelligence", "Nanotechnology", "Building Codes and Regulations", "Delegation of Tasks", "Islamic History", "Radiation Therapy", "Library and Information Science", "Radio", "Cultures", "Financial Reporting", "Mediums (oil", "Data", "Painting", "Computational Chemistry", "Food Quality Assurance", "Immunohematology (Blood Banking)", "Geographic Information Systems (GIS) for Environmental Planning", "Cost Analysis", "Anatomy", "Natural Resource Management", "Science Reporting", "Marine biology", "Health Assessments", "Economic Benefits", "Social", "Coastal areas", "Building Systems", "Social Equity and Environmental Justice", "Medicinal Chemistry", "Technology Applications", "watercolor", "Forensic anthropology", "Architectural Visualization", "Solid State Physics", "Physical Therapy", "Reproductive Health", "Developmental psychology", "Philosophy of Science", "Neuroanatomy and Physiology", "Plant Selection", "Processes", "Scientific research", "Empirical research", "Statistical Consulting", "Drug Testing", "Immunology", "Petroleum", "Carbon Sequestration", "Radiation Protection", "Sustainable Farming Practices", "Decision-making", "Creative Vision", "Environmental Systems", "Metals", "Budgeting and Forecasting", "Data Science", "Astrophysics", "Historiography", "Ancient history", "Environmental Science", "Cellular biology", "Pharmacognosy", "Radiobiology", "Economic Development", "Algorithms", "Biochemistry", "Linear Algebra", "Cultural", "Urban Planning", "Health Care Ethics", "Engineering", "Therapeutic Exercises", "Professional Development", "Business Ethics and Corporate Governance", "Aquaculture practices", "Laboratory techniques", "Testing Devices", "Bayesian Statistics", "Econometrics", "Computer Networks", "Environmental Law and Regulations", "Political Philosophy", "Budgeting", "Office Management", "stone", "Evolution", "Properties", "Forest ecosystems", "Computer Programming", "Regression Analysis", "Research Methods in Environmental Planning", "Sustainable management of aquatic resources", "Diverse communities and cultures", "Computational Physics", "Statistical Computing", "Mycology-virology", "Rebuilding", "Forestry", "mixed media)", "International Trade", "Programming", "Watershed management", "Drilling", "Cooperative Principles and Philosophy", "Mechanical Ventilation", "Public policy", "Mobile Application Development", "Regional Economics", "Ecosystems", "Social Class", "Materials Engineering", "Drug Delivery Systems", "Organization of Information", "Climate Change Adaptation and Resilience Planning", "Art Workshop", "Opinion Writing", "Retail Management", "Landscape Ecology", "Ergonomics", "Facilities", "Online Journalism", "Gender", "Coaching", "Sales Management", "Landscape Graphics and Visualization", "Architecture and Design", "Business Law", "Metaphysics", "Computer-Aided Design (CAD) and Visualization", "Wilderness management", "Forestry practices and challenges", "Energy", "Interactions with other religions and civilizations", "Customer Relations", "Electoral Systems and Voting Behavior", "Clinical Toxicology", "Cooperative development", "Graphic design", "Business Administration", "Geology", "International Studies", "Policy and Governance", "Crime scenes", "Composition", "Human evolution", "Linguistic analysis", "Cooperative governance", "Economic theories", "Architectural Drawing and Drafting", "Airway Management", "Environmental issues", "Site Analysis", "Operations", "Language teaching", "Mathematical Principles", "Historic Preservation", "Activities of Daily Living", "Environmental Politics", "Survey Methodology", "Educational Psychology", "Macroeconomics", "Customs", "Civilizations", "Visual Perception", "Construction", "Health", "Sculptural Movements", "CT Scanners", "Investment Analysis and Portfolio Management", "Family", "Professional Ethics and Values", "Security", "Forensics", "Philippine Economics History", "Belief systems", "Matter", "Physiological psychology", "Sociological Research Ethics", "Survey Sampling", "News Reporting", "Urban Economics", "Islamic jurisprudence", "Auditing", "Political theories", "Critical thinking", "Plate tectonics", "Web design", "Leadership", "Sustainable Tourism Planning", "Social and cultural factors", "Medicinal Drugs", "Environment", "Computer Graphics", "Coastal Zone Management", "Neuroanatomy", "Economic Sociology", "Technical Specifications", "Analytical skills", "Introduction to Business", "Culinary", "Fish farms", "Dysphagia", "Maintenance", "Journalism", "Sustainable Development and Land Use Planning", "Water Supply", "Managerial Accounting", "Nationalism", "Mass Communication Research", "Consulting", "Decision Support Systems", "Research projects", "found objects)", "Business Research Methods", "English language", "Cell Biology", "Public Policy Analysis", "Industrial and Organizational Psychology", "Forensic Engineering", "Social structures", "Molecular Virology", "Cosmetics", "Environmental assessments", "Mining Methods", "Molecular Diagnostics", "Project Management", "Propulsion Plant", "Criminal Justice", "Primary Health Care", "Mergers and Acquisitions", "Data Visualization", "Industrial Chemistry", "Agriculture", "Pollution Control", "Western Civilizations", "Mathematics", "Tourism Planning", "Critical analysis", "Extraction", "Forest management techniques", "Classical Sociological Theories", "Coordination Chemistry", "Postoperative Care", "Financial economics", "Physiology", "Food Analysis", "Physical Activity", "Practices", "Financial analysis", "Library Literature", "Government structures", "Institutions", "Geological Engineering", "Gender and Politics", "Media Law and Ethics", "Social and economic aspects of forestry", "Research", "Rock formations", "Obligations and Contracts", "Sustainable Design Practices", "Hygiene", "Financial management", "Statistics", "Media Management", "Ethics", "Naval Architecture", "Manufacturing", "Visual Verbal Communication", "Expressing interests and ideas", "Soil Science", "Production Methods", "Special Libraries", "Print Media", "Islamic ethics", "Therapy", "Renewable Energy", "Neonatal Care", "Design Workshop", "Risk management", "Cross-Cultural Psychology", "Information Architecture", "Environmental policy", "Solid-State Physics", "Event Management", "Space", "Forensic biology", "Language use", "IT Governance", "Geotechnical", "Financial Markets and Institutions", "Marine conservation", "Animal Husbandry", "Color Theory", "Performance Evaluation and Control", "Strategic Decision-making", "Marine Transportation", "Introduction to Financial Accounting", "Statistical Inference", "Development Communication", "Peace", "Food Microbiology", "Statistical Methods", "Chemical Kinetics", "Spacecraft", "Financial Technology (Fintech) and Innovation in Finance", "Anatomy and Physiology", "Business Law and Ethics", "IT Strategy", "Design Theory", "Cooperative Finance and Accounting", "Genetics", "Aviation Safety", "Business Process Management", "Medical Technology Laws", "Business Law and Regulations", "Social Policy", "Advertising", "Prevention of crime", "Goods", "Family Planning", "Real-world settings", "Criminal justice system", "Public Health", "Fisheries management techniques", "Counseling and Psychotherapy", "Medical Imaging Equipment", "Financial Modeling", "Operations management", "History and Systems of Psychology", "News Writing", "Resources", "Social and economic factors", "Design Theory and Principles", "Protocol", "Time Series Analysis", "New media design", "Digital Techniques", "Visual Storytelling", "Communication Theories", "Corporate finance", "Occupational Therapy", "Agribusiness", "Applied Physics", "Trade", "Finance", "Fine Arts", "Environmental Policy and Governance", "Sculpture", "Quality Control", "Integral Calculus", "Moral and Legal Principles", "Microbiology", "Fluid Mechanics", "Program Development", "Office Administration", "Drawing Techniques", "Sustainable Design", "Political economy", "Rehabilitation", "Characteristics", "Existentialism", "Numerical Analysis", "Commercial Design", "Materials", "Maritime Law", "Academic Libraries", "Forensic Psychology", "Political Factors", "Appraisal", "Tariffs", "Kinesiology", "Communication skills", "IT Ethics", "Economic Research", "Logistics", "Conflict Resolution and Peace Studies", "Mineral Deposit Assessment", "Cloud Computing", "wood", "Financial markets", "Urban Revitalization and Regeneration", "Reservoir", "Shorthand", "Geological hazards", "Personality Theories", "Moral Philosophy", "Professional Practice and Ethics in Environmental Planning", "Marine Pollution", "Sociology", "Intermediate Macroeconomics", "Counseling", "Cultural practices", "Investigative Journalism", "Technical skills", "Community Development", "Microeconomics", "International Business", "Prototyping", "Research abilities", "Orthotics and Prosthetics", "Laboratory Methods", "Database Management", "Health Economics", "Cooperative Education and Training", "Urban Design", "Materials and Finishes", "Broadcast Journalism", "Biological Psychology", "Research Methods in Political Science", "Materials (clay", "Food Science", "Modern history", "Materials for Industrial Design", "Visual Studies", "Political Science", "Multivariate Analysis", "Construction Management", "Economic Statistics", "Medical Nutrition Therapy", "Pharmacy Administration", "Laboratory experiments", "Cybersecurity", "Optimization", "Augmentative and Alternative Communication", "Life-saving Interventions", "Islamic faith", "Building Construction", "Human Health", "Emerging Technologies", "Statistical Modeling", "Hospitality Design", "Drama", "Cooperative Law and Policy", "Humanities", "Geological processes", "Sustainability", "Culture", "Global Governance", "Dynamics of society", "Financial Statements", "Forest policy and governance", "Psychology in Interior Design", "Human Resources", "Physics", "Exercise Physiology", "Metallurgical Engineering", "Sustainable development", "Financial planning", "Cooperative Development and Entrepreneurship", "Human mind", "Social Responsibility", "Supervisory", "Hands-on experience", "Disadvantages", "Environmental Modeling and Simulation", "Revolution", "International finance", "Behavior", "Pharmacology", "Motion graphics", "Mathematical Modeling", "Bioethics", "Ethnography", "Database Systems", "Global Sociology", "Human societies", "Volcanism", "Emotional Health", "Diplomacy", "Logic", "Sampling", "Genres of literature", "Forest inventory and assessment", "Living organisms", "Management Information System", "Scientific principles", "Epistemology", "Economic principles", "Social Change", "Public Economics", "Conversion", "Information Campaigns", "Conservation biology", "Consultancy", "Causes", "Translation", "Critical thinking abilities", "Brokerage", "Agroforestry Field Techniques", "Economic policy", "Research Methods in History", "Environmental Psychology and Behavior", "Literary theory", "Scientific communication", "Mathematical Theory", "Operations Research", "Ship Business", "Social and Cultural History", "Social Work", "Biomechanics", "Fieldwork", "Agricultural Economics", "Minerals", "Editing", "Legal Issues", "Archives Management", "Human resource management", "Cognitive psychology", "Applied Statistics", "Business Statistics", "Electromagnetism", "Visual Design", "Experimental Physics", "Transportation Planning and Sustainable Mobility", "Photojournalism", "Empirical studies", "Water Management", "Plant Science", "Television", "Plasma Physics", "Consumer Behavior", "Geometry", "Supply Chain", "Molecular Evolution", "Interior Design", "Immunology/Serology", "Taxation", "Government Systems", "Medical Physics", "Plant", "Classical Mechanics", "History of Science and Technology", "Semantics", "Verbal and Non-verbal Messages", "Health Care", "Illustration", "Biotechnology", "Education", "Arts and Design", "Language variation", "Social institutions", "Nonparametric Statistics", "Bioinformatics", "Sustainable Land Management", "Data Analytics", "Management", "Contemporary Practices", "Climate Change Adaptation", "Cooperative finance", "Operation", "Language acquisition", "Nutritional Assessment", "Historical processes", "Retail", "Ecology", "User Experience Design", "Nuclear Physics", "Art Theory", "Internship/Practicum", "Science", "E-Commerce", "Statistics for Psychology", "Political systems", "Nursing Research", "Speech and Hearing Science", "Aerodynamics", "Quantitative Research Methods", "Abstract Algebra", "Race and Ethnicity", "Practical Studies", "Livestock Management", "Systems Design", "Accounting", "American Government and Politics", "MRIs", "Genres", "Quranic studies", "Technical Writing", "Public Safety", "Transcription", "Principles", "Number Theory", "Respiratory Therapy", "Biomedical Equipment", "Computer Architecture", "Topology", "Quantum Electronics", "Cooperative Governance and Management", "Reporting the Arts and Culture", "Probability", "Architectural Design", "Capstone Project", "Principles of Marketing", "Community Engagement and Participatory Planning", "Cultural History", "Programs", "Statistical analysis", "History", "Media Specialist", "Forensic chemistry", "Financial Mathematics", "Environmental Reporting", "Three-dimensional art", "Comparative politics", "Financial Statement Analysis", "Events Management", "Art Movements", "Economic Policy Analysis", "Research Methods in Economics", "Social Sciences", "Literary genres", "Quantum Optics", "Bacteriology", "Positive Psychology", "East Asian", "Evaluation", "Internship/Practicum in Management Accounting", "Data Modeling", "Arts & Design", "Forest recreation", "Data Analysis", "Information Systems", "Technology", "Neuropsychology", "Cytologic Techniques", "Labor Economics", "Aquaculture", "Polymer Chemistry", "Language Processing", "Organizational Behavior", "Information Sources", "Texture", "Cooperative marketing", "Sonogram Machines", "Public Libraries", "Asian Civilizations", "Political Parties and Interest Groups", "Cooperative Case Studies and Fieldwork", "Sedimentation", "Structure", "Theoretical Studies", "Filipino culture", "Business Intelligence", "Law", "Law and Society", "Aircraft Maintenance", "Rural Sociology", "Typography", "Social psychology", "Marketing", "Interpretation", "Risk Assessment", "Service", "Advanced Management Accounting", "Complex Analysis", "Mathematical Physics", "Sampling Techniques", "Mental processes", "Natural resources", "Tourism", "Digital Media Design", "Environmental Sustainability", "Landscape Construction Techniques", "First Aid Techniques", "Statistical Mechanics", "Food", "Governance", "Marketing Strategy", "Health Education", "Fisheries practices and challenges", "Environmental chemistry", "Quantum Mechanics", "Psychology", "Crime", "Hands-on training", "Clerical", "Tourism Research", "Societies", "Politics", "Taxation and Tax Planning", "Food Processing", "Research and Development", "Machinery", "Law Enforcement", "Discrete Mathematics", "World history", "Sociolinguistics", "Corporate Governance", "Biology", "Product Design", "Filipino Philosophy", "Economics", "Regulations", "Administration", "metal", "Money and Banking", "Political Theory", "Lighting Design", "Mining Engineering", "Criminological theories", "Fisheries science", "Big Data Analytics", "Urban Sociology", "Real Analysis", "Investment management", "Client Communication", "Foreign Relations", "Branding and Identity Design", "Traditional and modern technology", "Language", "Furniture Design and Selection", "Language patterns", "Algebra", "Introduction to Psychology", "Molecular Pharmacology", "Tourism Management", "Cognitive processes", "Solutions to environmental challenges", "Engineering Principles", "Cultural appreciation", "Research Method in the Arts", "Feature Writing", "Operating Systems", "Political processes", "Software Testing", "Economic History", "Business Ethics and Corporate Social Responsibility", "Fire Protection", "Mathematical Optimization", "plaster", "Environmental Economics and Finance", "Teaching in Healthcare Setting", "Business and Economics Reporting", "History of Economic Thought", "Entrepreneurial Finance", "Applied Mathematics", "Pharmacy", "Photography", "Policy Analysis", "Industrial Organization", "Philippine Constitution and Government", "Thermodynamics", "Investment", "Services", "Contemporary issues in Islam", "Materials Science", "Cultural context", "Sustainable forestry practices", "Biology and ecology of trees", "Analytical Chemistry", "Syntax", "Product Development", "Clinical Chemistry", "Marine Engineering", "Travel and Tour Operation", "Quantitative methods in finance", "Public Opinion and Political Behavior", "Brand Management", "Public Administration", "Professional Practice", "Planning", "Medical Sociology", "History of Ideas", "International economics", "Development economics", "Languages", "Mineral Resources", "Entrepreneurship", "Supply Chain Management", "Food Engineering", "Anatomy/Physiology", "Journalism Principles and Practices", "Internships", "Human behavior", "Food Safety", "Management Accounting", "Management", "Systems", "Procedures", "Policies", "Decision Making", "Theater Arts", "Philippine Arts and Culture", "Theater History", "Folk Dance", "Scriptwriting", "Design", "Technical Management", "Arts Management", "Performance", "Culture", "Business Administration", "Human resource management", "Workforce Organization", "Job Analysis", "Recruitment", "Compensation", "Personnel Training", "Legal Provisions", "Labor Concerns", "Total Quality Management", "Railway Engineering", "Railway Construction", "Railway Traffic Control", "Railway Geotechnical Engineering", "Railway Safety", "Railway Transportation Planning", "Railway Environmental Impact Assessment"
        ).sorted()
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