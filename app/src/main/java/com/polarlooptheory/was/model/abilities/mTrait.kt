package com.polarlooptheory.was.model.abilities

/**
 * Trait's model class
 * @property name Name of the trait
 * @property description Trait's description
 * @property visible Visibility(is it visible for all players)
 */
class mTrait (
    var name: String = "",
    var description: String? = null,
    var visible: Boolean = false
    )