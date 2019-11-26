package com.polarlooptheory.was.model.abilities

/**
 * Language's model class
 * @property name Name of the language
 * @property type Language's type
 * @property script Language's script
 * @property visible Visibility(is it visible for all players)
 */
class mLanguage (
    var name: String = "",
    var type: String? = null,
    var script: String? = null,
    var visible: Boolean = false
)