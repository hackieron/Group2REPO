package com.example.courseer2

import android.graphics.Bitmap

class User {

    var id : Int = 0
    var name : String = ""
    var image: Bitmap? = null
    var strand: String = ""

    constructor(name:String, image: Bitmap, strand:String){
        this.name = name
        this.image = image
        this.strand = strand
    }
}

data class Tag(
    val id: Long, // Primary key for tags
    val tagName: String
)

