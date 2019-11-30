package com.polarlooptheory.was.model.equipment

/**
 * Tool's model class
 * @property name Name of the tool
 * @property description Tool's description
 * @property category Tool's category
 * @property cost Tool's cost
 * @property weight Tool's weight
 * @property visible Visibility(is it visible for all players)
 * @property custom Is it made custom for scenario
 */
class mTool (
    var name: String = "",
    var description: String? = null,
    var category: String? = null,
    var cost: String? = null,
    var weight: Int = 0,
    var visible: Boolean = false,
    var custom: Boolean = false
    )