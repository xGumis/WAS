package com.polarlooptheory.was.model

/**
 * Note's model class
 * @property id Id of the note
 * @property name Name/Title of the note
 * @property content Content text of the note
 */
class mNote (
    val id: Int = 0,
    var name: String = "",
    var content: String = ""
)