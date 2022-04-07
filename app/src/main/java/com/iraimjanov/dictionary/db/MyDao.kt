package com.iraimjanov.dictionary.db

import androidx.room.*
import com.iraimjanov.dictionary.models.Types
import com.iraimjanov.dictionary.models.Words
import io.reactivex.rxjava3.core.Flowable

@Dao
interface MyDao {

    @Insert
    fun addTypes(types: Types)

    @Delete
    fun deleteTypes(types: Types)

    @Update
    fun updateTypes(types: Types)

    @Query("select * from Types")
    fun getAllTypes(): Flowable<List<Types>>

    @Query("select * from Types")
    fun getAllTypesNoFlowable(): List<Types>

    @Query("select * from Types where id = :id")
    fun getTypeById(id: Int): List<Types>

    @Query("select * from Types where name like :name")
    fun getTypeByName(name: String): List<Types>

    @Insert
    fun addWords(words: Words)

    @Delete
    fun deleteWords(words: Words)

    @Query("select * from Words where type like :id")
    fun selectWordsByTypesId(id: String): List<Words>

    @Update
    fun updateWords(words: Words)

    @Query("select * from Words")
    fun getAllWords(): Flowable<List<Words>>

    @Query("select * from Words")
    fun getAllWordsNoFlowable(): List<Words>

}