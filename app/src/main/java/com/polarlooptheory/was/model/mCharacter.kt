package com.polarlooptheory.was.model

import com.polarlooptheory.was.model.abilities.*
import com.polarlooptheory.was.model.equipment.*

/**
 * Character's model class
 * @property name Character's name
 * @property alignment Character's alignment
 * @property attributes Character's [attributes][Attributes]
 * @property background Character's history/background
 * @property experience Character's experience points
 * @property health Character's [health][Health] statistics
 * @property hitDices [Hit dices][HitDices] character is using
 * @property initiative Character's initiative statistic
 * @property inspiration Character's inspiration statistic
 * @property level Character's level
 * @property passiveInsight Character's passive insight statistic
 * @property passivePerception Character's passive perception statistic
 * @property profession Character's profession
 * @property proficiency Character's proficiency bonus
 * @property race Character's race
 * @property speed Character's movement speed
 * @property abilities Set of character's [abilities][Abilities]
 * @property equipment Set of character's [equipment][Equipment]
 * @property spells Set of character's [spells][Spells]
 */
class mCharacter (
    var name: String = "",
    var alignment: String? = null,
    var attributes: Attributes = Attributes(),
    var background: String? = null,
    var experience: Int = 0,
    var health: Health = Health(),
    var hitDices: HitDices = HitDices(),
    var initiative: Int = 0,
    var inspiration: Int = 0,
    var level: Int = 0,
    var passiveInsight: Int = 0,
    var passivePerception: Int = 0,
    var profession: String? = null,
    var proficiency: Int = 0,
    var race: String? = null,
    var speed: String? = null,
    var owner: String? = null
){
    /**
     * Character attributes class
     * @property charisma Character's charisma statistic
     * @property constitution Character's constitution statistic
     * @property dexterity Character's dexterity statistic
     * @property intelligence Character's intelligence statistic
     * @property strength Character's strength statistic
     * @property wisdom Character's wisdom statistic
     */
    class Attributes(
        var charisma: Int = 0,
        var constitution: Int = 0,
        var dexterity: Int = 0,
        var intelligence: Int = 0,
        var strength: Int = 0,
        var wisdom: Int = 0
    )

    /**
     * Character health statistics class
     * @property actualHealth Current character's health statistic
     * @property maxHealth Character's maximal health statistic
     * @property temporaryHealth Character's temporary bonus health statistic
     */
    class Health(
        var actualHealth: Int = 0,
        var maxHealth: Int = 0,
        var temporaryHealth: Int = 0
    )

    /**
     * Character hit dices class
     * @property dice Hit dice type
     * @property total Number of total hit dices
     * @property used Number of used hit dices
     */
    class HitDices(
        var dice: String? = null,
        var total: Int = 0,
        var used: Int = 0
    )

    /**
     * Class for holding a set of character's abilities
     * @property features List of character's [features][mFeature]
     * @property languages List of character's known [languages][mLanguage]
     * @property proficiencies List of character's [proficiencies][mProficiency]
     * @property traits List of character's [traits][mTrait]
     */
    class Abilities{
        var features: List<String> = listOf()
        var languages: List<String> = listOf()
        var proficiencies: List<String> = listOf()
        var traits: List<String> = listOf()
    }
    val abilities = Abilities()

    /**
     * Class for holding a set of character's equipment
     * @property armors List of [armors][mArmor] character possesses
     * @property attacks List of available [attacks][Attack] for character
     * @property currency Currency character possesses
     * @property gear List of [gear][mGear] character possesses
     * @property tools List of [tools][mTool] character possesses
     * @property vehicles List of [vehicles][mVehicle] character possesses
     * @property weapons List of [weapons][mWeapon] character possesses
     */
    class Equipment{
        var armorClass: Int = 0
        var armors: List<String> = listOf()
        var attacks: List<Attack> = listOf()

        /**
         * Attack's model class
         * @property bonus Bonus added to damage
         * @property damage Describes dices used to deal damage
         * @property name Name of the attack
         * @property type Attack's type
         */
        class Attack(
            val bonus: Int? = null,
            val damage: String? = null,
            val name: String? = null,
            val type: String? = null
        )
        var currency: Currency = Currency()

        /**
         * Currency's model class
         * @property cp Number of copper pieces
         * @property sp Number of silver pieces
         * @property ep Number of electrum pieces
         * @property gp Number of gold pieces
         * @property pp Number of platinum pieces
         */
        class Currency(
            var cp: Int = 0,
            var sp: Int = 0,
            var ep: Int = 0,
            var gp: Int = 0,
            var pp: Int = 0
        )
        var gear: List<String> = listOf()
        var tools: List<String> = listOf()
        var vehicles: List<String> = listOf()
        var weapons: List<String> = listOf()

    }
    val equipment = Equipment()

    /**
     * Class for holding a set of character's spells
     * @property baseStat Character's base spell [attribute][Attributes]
     * @property spellAttackBonus Character's spell attack bonus
     * @property spellSaveDc Character's spell saving throw difficulty
     * @property spellSlots Spell slots character possesses
     * @property spells List of [spells][mSpell] character knows
     */
    class Spells{
        var baseStat: String? = null
        var spellAttackBonus: Int = 0
        var spellSaveDc: Int = 0
        var spellSlots: List<SpellSlot> = listOf()
        /**
         * Spell's slot model class
         * @property level Level of a spell slot
         * @property total Total number of spell slots
         * @property used Number of slots used
         */
        class SpellSlot(
            val level: Int? = null,
            val total: Int? = null,
            val used: Int? = null
        )
        var spells: List<String> = listOf()
    }
    val spells = Spells()
}