package com.polarlooptheory.was.model.abilities

/**
 * Feature's model class
 * @property name Name of the feature
 * @property description Feature's description
 * @property visible Visibility(is it visible for all players)
 * @property custom Is it made custom for scenario
 */
class mFeature (
    var name: String = "",
    var description: String? = null,
    var visible: Boolean = false,
    var custom: Boolean = false
)