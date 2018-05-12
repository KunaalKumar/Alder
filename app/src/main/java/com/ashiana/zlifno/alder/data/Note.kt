package com.ashiana.zlifno.alder.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

import java.io.Serializable
import java.util.Calendar
import java.util.Date

// Defines columns to add to database
@Entity(tableName = "note_table")
class Note(var title: String, var noteType: Int, var content: String, var timeCreated: String) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var timeEdited: String? = null

    var position: Int = 0

    var imagePath: String? = null

    companion object {

        const val NOTE_TYPE_TEXT = 1
        const val NOTE_TYPE_IMAGE = 2
    }

}
