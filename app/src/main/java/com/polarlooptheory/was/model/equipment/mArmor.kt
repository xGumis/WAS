package com.polarlooptheory.was.model.equipment

/**
 * Armor's model class
 * @property name Name of the armor
 * @property armorClass Armor's armor class
 * @property cost Armor's cost
 * @property stealthDisadvantage Does it make stealth disadvantageous
 * @property strMinimum Minimum of strength required
 * @property weight Armor's weight
 * @property visible Visibility(is it visible for all players)
 * @property custom Is it made custom for scenario
 */
class mArmor (
    var name: String = "",
    var armorClass: ArmorClass = ArmorClass(),
    var cost: String? = null,
    var stealthDisadvantage: Boolean = false,
    var strMinimum: Int = 0,
    var weight: Int = 0,
    var visible: Boolean = false,
    var custom: Boolean = false
){
    /**
     * Armor class's model class
     * @property base Base armor class value
     * @property dexBonus Does it have bonuses from dexterity
     * @property maxBonus Maximum bonus from dexterity
     */
    class ArmorClass(
        val base: Int = 0,
        val dexBonus: Boolean = false,
        val maxBonus: Int = 0
    )
}