package com.polarlooptheory.was.model.abilities

/**
 * Skill's model class
 * @property name Name of the skill
 * @property description Skill's description
 * @property abilityScore Skill's primary ability
 * @property visible Visibility(is it visible for all players)
 */
class mSkill (
    var name: String = "",
    var description: String? = null,
    var abilityScore: String? = null,
    var visible: Boolean = false
)