package com.polarlooptheory.was.model.equipment

/**
 * Vehicle's model class
 * @property name Name of the Vehicle
 * @property description Vehicle's description
 * @property cost Vehicle's cost
 * @property weight Vehicle's weight
 * @property visible Visibility(is it visible for all players)
 */
class mVehicle (
    var name: String = "",
    var description: String? = null,
    var cost: String? = null,
    var weight: Int = 0,
    var visible: Boolean = false
)