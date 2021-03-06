package com.polarlooptheory.was.model.types

/**
 * Condition's model class
 * @property name Name of the condition
 * @property description Condition's description
 * @property visible Visibility(is it visible for all players)
 * @property custom Is it made custom for scenario
 */
class mCondition (
    var name: String = "",
    var description: String? = null,
    var visible: Boolean = false,
    var custom: Boolean = false
)