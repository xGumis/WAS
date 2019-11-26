package com.polarlooptheory.was.model.equipment

/**
 * Gear's model class
 * @property name Name of the gear
 * @property description Gear's description
 * @property cost Gear's cost
 * @property weight Gear's weight
 * @property visible Visibility(is it visible for all players)
 */
class mGear (
    var name: String = "",
    var description: String? = null,
    var cost: String? = null,
    var weight: Int = 0,
    var visible: Boolean = false
)