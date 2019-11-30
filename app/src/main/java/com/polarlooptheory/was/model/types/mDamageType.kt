package com.polarlooptheory.was.model.types

/**
 * Damage type's model class
 * @property name Name of the damage type
 * @property description Damage type's description
 * @property visible Visibility(is it visible for all players)
 * @property custom Is it made custom for scenario
 */
class mDamageType (
    var name: String = "",
    var description: String? = null,
    var visible: Boolean = false,
    var custom: Boolean = false
)