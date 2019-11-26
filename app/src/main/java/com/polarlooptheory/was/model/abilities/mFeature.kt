package com.polarlooptheory.was.model.abilities

/**
 * Feature's model class
 * @property name Name of the feature
 * @property description Feature's description
 * @property visible Visibility(is it visible for all players)
 */
class mFeature (
    var name: String = "",
    var description: String? = null,
    var visible: Boolean = false
)