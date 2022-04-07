package com.iraimjanov.dictionary.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(indices = [Index(value = ["name"], unique = true)])
class Types {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @ColumnInfo(name = "name")
    var name: String? = null

    constructor(name: String?) {
        this.name = name
    }

    constructor(id: Int?, name: String?) {
        this.id = id
        this.name = name
    }

    constructor()

}