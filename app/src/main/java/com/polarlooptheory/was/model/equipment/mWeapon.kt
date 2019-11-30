package com.polarlooptheory.was.model.equipment

/**
 * Weapon's model class
 * @property name Name of the Weapon
 * @property category Weapon's category
 * @property cost Weapon's cost
 * @property damageBonus Damage bonus of the weapon
 * @property damageDice Dice used to define damage of the weapon
 * @property damageType Weapon's damage type
 * @property longRange Weapon's extended shooting range
 * @property longThrowRange Weapon's extended throw range
 * @property normalRange Weapon's shooting range
 * @property normalThrowRange Weapon's throw range
 * @property properties List of weapon's properties
 * @property weaponRange Weapon's reach
 * @property weight Weapon's weight
 * @property visible Visibility(is it visible for all players)
 * @property custom Is it made custom for scenario
 */
class mWeapon (
    var name: String = "",
    var category: String? = null,
    var cost: String? = null,
    var damageBonus: Int = 0,
    var damageDice: String? = null,
    var damageType: String? = null,
    var longRange: Int = 0,
    var longThrowRange: Int = 0,
    var normalRange: Int = 0,
    var normalThrowRange: Int = 0,
    var properties: List<String>? = null,
    var weaponRange: String? = null,
    var weight: Int = 0,
    var visible: Boolean = false,
    var custom: Boolean = false
    )