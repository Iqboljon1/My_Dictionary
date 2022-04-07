package com.iraimjanov.dictionary.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Words : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var name: String? = null
    var description: String? = null
    var image: String? = null
    var type: String? = null
    var like: String? = null

    constructor(name: String?, description: String?, image: String?, type: String?, like: String?) {
        this.name = name
        this.description = description
        this.image = image
        this.type = type
        this.like = like
    }


    constructor(
        id: Int?,
        name: String?,
        description: String?,
        image: String?,
        type: String?,
        like: String?,
    ) {
        this.id = id
        this.name = name
        this.description = description
        this.image = image
        this.type = type
        this.like = like
    }

    constructor()

}