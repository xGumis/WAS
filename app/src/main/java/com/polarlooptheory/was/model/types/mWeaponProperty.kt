package com.polarlooptheory.was.model.types

/**
 * Weapon property's model class
 * @property name Name of the weapon property
 * @property description Weapon property's description
 * @property visible Visibility(is it visible for all players)
 * @property custom Is it made custom for scenario
 */
class mWeaponProperty (
    var name: String = "",
    var description: String? = null,
    var visible: Boolean = false,
    var custom: Boolean = false
)