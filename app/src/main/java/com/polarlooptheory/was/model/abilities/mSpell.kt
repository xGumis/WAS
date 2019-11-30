package com.polarlooptheory.was.model.abilities

/**
 * Spell's model class
 * @property name Name of the spell
 * @property magicSchool Magic school the spell belongs to
 * @property description Spell's description
 * @property castingTime Spell's casting time
 * @property components Components needed to cast the spell
 * @property concentration Is concentration required
 * @property duration Duration of the spell
 * @property higherLevels Effects when casting on higher levels
 * @property level Spell's level
 * @property material Materials required to cast the spell
 * @property range Spell's range
 * @property ritual Can the spell be casted as a ritual
 * @property visible Visibility(is it visible for all players)
 * @property custom Is it made custom for scenario
 */
class mSpell (
    var name: String = "",
    var magicSchool: String = "",
    var description: String? = null,
    var castingTime: String? = null,
    var components: String? = null,
    var concentration: Boolean = false,
    var duration: String? = null,
    var higherLevels: String? = null,
    var level: Int = 0,
    var material: String? = null,
    var range: String? = null,
    var ritual: Boolean = false,
    var visible: Boolean = false,
    var custom: Boolean = false
)