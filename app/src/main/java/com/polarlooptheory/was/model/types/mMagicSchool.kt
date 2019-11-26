package com.polarlooptheory.was.model.types

/**
 * Magic school's model class
 * @property name Name of the magic school
 * @property description Magic school's description
 * @property visible Visibility(is it visible for all players)
 */
class mMagicSchool (
    var name: String = "",
    var description: String? = null,
    var visible: Boolean = false
)