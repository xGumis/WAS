package com.polarlooptheory.was.model.abilities

/**
 * Proficiency's model class
 * @property name Name of the proficiency
 * @property type Proficiency's type
 * @property visible Visibility(is it visible for all players)
 */
class mProficiency (
    var name: String = "",
    var type: String? = null,
    var visible: Boolean = false
)