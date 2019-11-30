package com.polarlooptheory.was.apiCalls

import android.util.Log
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPatch
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.polarlooptheory.was.Settings
import com.polarlooptheory.was.model.*
import com.polarlooptheory.was.model.abilities.*
import com.polarlooptheory.was.model.equipment.*
import com.polarlooptheory.was.model.mCharacter.*
import com.polarlooptheory.was.model.types.mCondition
import com.polarlooptheory.was.model.types.mDamageType
import com.polarlooptheory.was.model.types.mMagicSchool
import com.polarlooptheory.was.model.types.mWeaponProperty
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

/**
 * Object for handling scenarios
 * @property scenariosList List of [scenarios][mScenario] joined/created by the [user][User]
 */
object Scenario {
    private const val endpoint = Settings.server_address + "api/v1/scenario"
    private val mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,"wss://10.0.2.2:8080/rpg-server")
    private val compositeDisposable = CompositeDisposable()
    var scenariosList: List<mScenario> = listOf()

    /**
     * Object holding the scenario the user connected to
     * @property scenario The [scenario][mScenario] the user connected to
     * @property charactersList List of [characters][mCharacter] in the scenario
     * @property messagesList List of [messages][mMessage] in the scenario
     * @property notesList List of [notes][mNote] in the scenario
     */
    object connectedScenario {
        var scenario: mScenario = mScenario()
        var notesList: List<mNote> = listOf()
        var messagesList: List<mMessage> = listOf()
        var charactersList: List<mCharacter> = listOf()
        fun clear(){
            scenario = mScenario()
            notesList = listOf()
            messagesList = listOf()
            charactersList = listOf()
        }
    }

    /**
     * Object holding loaded resources
     *
     * Maps [NAME][String] to RESOURCE_MODEL
     * @property conditions Map of loaded [conditions][mCondition]
     * @property damageTypes Map of loaded [damage types][mDamageType]
     * @property magicSchools Map of loaded [magic schools][mMagicSchool]
     * @property weaponProperties Map of loaded [weapon properties][mWeaponProperty]
     * @property armors Map of loaded [armors][mArmor]
     * @property gear Map of loaded [gear][mGear]
     * @property tools Map of loaded [tools][mTool]
     * @property vehicles Map of loaded [vehicles][mVehicle]
     * @property weapons Map of loaded [weapons][mWeapon]
     * @property features Map of loaded [features][mFeature]
     * @property languages Map of loaded [languages][mLanguage]
     * @property proficiencies Map of loaded [proficiencies][mProficiency]
     * @property skills Map of loaded [skills][mSkill]
     * @property spells Map of loaded [spells][mSpell]
     * @property traits Map of loaded [traits][mTrait]
     */
    object loadedResources{
        //region Types
        var conditions: MutableMap<String,mCondition> = mutableMapOf()
        var damageTypes: MutableMap<String,mDamageType> = mutableMapOf()
        var magicSchools: MutableMap<String,mMagicSchool> = mutableMapOf()
        var weaponProperties: MutableMap<String,mWeaponProperty> = mutableMapOf()
        //endregion
        //region Equipment
        var armors: MutableMap<String,mArmor> = mutableMapOf()
        var gear: MutableMap<String,mGear> = mutableMapOf()
        var tools: MutableMap<String,mTool> = mutableMapOf()
        var vehicles: MutableMap<String,mVehicle> = mutableMapOf()
        var weapons: MutableMap<String,mWeapon> = mutableMapOf()
        //endregion
        //region Abilities
        var features: MutableMap<String,mFeature> = mutableMapOf()
        var languages: MutableMap<String,mLanguage> = mutableMapOf()
        var proficiencies: MutableMap<String,mProficiency> = mutableMapOf()
        var skills: MutableMap<String,mSkill> = mutableMapOf()
        var spells: MutableMap<String,mSpell> = mutableMapOf()
        var traits: MutableMap<String,mTrait> = mutableMapOf()
        //endregion
        fun clear(){
            traits.clear()
            spells.clear()
            skills.clear()
            proficiencies.clear()
            languages.clear()
            features.clear()
            weapons.clear()
            vehicles.clear()
            tools.clear()
            armors.clear()
            weaponProperties.clear()
            magicSchools.clear()
            damageTypes.clear()
            conditions.clear()
            gear.clear()
        }
    }
    //TODO(Websocket)
    //TODO(FAB do wiadomości)


    /**
     * GM only functions
     */
    object GM {

        /**
         * Checks if [user][User] is the game master of the [scenario]
         * @param scenario Chosen [scenario][mScenario]
         * @return true if the user is the GM
         */
        fun isGM(scenario: mScenario): Boolean {
            return User.username == scenario.gameMaster
        }

        /**
         * Starts the [scenario]
         *
         * Player must be a game master to start scenario
         * @param scenario [Scenario][mScenario] to start
         * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
         */
        suspend fun startScenario(scenario: mScenario): Boolean {
            if (isGM(scenario)) {
                val endpoint = "$endpoint/${scenario.scenarioKey}/start"
                var success = false
                runBlocking {
                    val (_, _, result) = endpoint.httpPost().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                    when (result) {
                        is Result.Success -> {
                            success = true
                        }
                        is Result.Failure -> {
                            if (ApiErrorHandling.handleError(result.error))
                                startScenario(scenario)
                        }
                    }
                }
                return success
            } else {
                Settings.error_message = "Player is not the GM of this scenario"
                return false
            }
        }

        /**
         * Changes the [owner][newOwner] of the [character] in the [scenario]
         * @param scenario Chosen [scenario][mScenario]
         * @param character Character which owner will change
         * @param newOwner New [character]'s owner
         * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
         */
        suspend fun changeCharacterOwner(
            scenario: mScenario,
            character: mCharacter,
            newOwner: String
        ): Boolean {
            if (isGM(scenario)) {
                val endpoint =
                    "${Settings.server_address}action/change/characterOwner/scenario/${scenario.scenarioKey}"
                var success = false
                runBlocking {
                    val json = JSONObject()
                    json.put("characterName", character.name)
                    json.put("newOwner", newOwner)
                    val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                        User.UserToken.access_token
                    ).awaitStringResponseResult()
                    when (result) {
                        is Result.Success -> {
                            success = true
                        }
                        is Result.Failure -> {
                            if (ApiErrorHandling.handleError(result.error))
                                changeCharacterOwner(scenario, character, newOwner)
                        }
                    }
                }
                return success
            } else {
                Settings.error_message = "Player is not the GM of this scenario"
                return false
            }
        }

        /**
         * Changes the [game master][player] of the [scenario]
         * @param scenario Chosen [scenario][mScenario]
         * @param player New game master
         * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
         */
        suspend fun changeGameMaster(scenario: mScenario, player: String): Boolean {
            if (isGM(scenario)) {
                val endpoint =
                    "${Settings.server_address}action/change/gameMaster/scenario/${scenario.scenarioKey}"
                var success = false
                runBlocking {
                    val (_, _, result) = endpoint.httpPost(listOf("player" to player)).authentication().bearer(
                        User.UserToken.access_token
                    ).awaitStringResponseResult()
                    when (result) {
                        is Result.Success -> {
                            success = true
                        }
                        is Result.Failure -> {
                            if (ApiErrorHandling.handleError(result.error))
                                changeGameMaster(scenario, player)
                        }
                    }
                }
                return success
            } else {
                Settings.error_message = "Player is not the GM of this scenario"
                return false
            }
        }

        /**
         * Changes the [password] of the [scenario]
         * @param scenario Chosen [scenario][mScenario]
         * @param password New password
         * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
         */
        suspend fun changeScenarioPassword(scenario: mScenario, password: String): Boolean {
            if (isGM(scenario)) {
                val endpoint =
                    "${Settings.server_address}action/change/password/scenario/${scenario.scenarioKey}"
                var success = false
                runBlocking {
                    val json = JSONObject()
                    json.put("password", password)
                    val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                        User.UserToken.access_token
                    ).awaitStringResponseResult()
                    when (result) {
                        is Result.Success -> {
                            success = true
                        }
                        is Result.Failure -> {
                            if (ApiErrorHandling.handleError(result.error))
                                changeScenarioPassword(scenario, password)
                        }
                    }
                }
                return success
            } else {
                Settings.error_message = "Player is not the GM of this scenario"
                return false
            }
        }

        /**
         * Deletes [character] from the [scenario]
         * @param scenario Chosen [scenario][mScenario]
         * @param character [Character][mCharacter] to delete
         * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
         */
        suspend fun deleteCharacter(scenario: mScenario, character: mCharacter): Boolean {
            if (isGM(scenario)) {
                val endpoint =
                    "${Settings.server_address}action/remove/character/scenario/${scenario.scenarioKey}"
                var success = false
                runBlocking {
                    val (_, _, result) = endpoint.httpDelete(listOf("character" to character.name)).authentication().bearer(
                        User.UserToken.access_token
                    ).awaitStringResponseResult()
                    when (result) {
                        is Result.Success -> {
                            success = true
                        }
                        is Result.Failure -> {
                            if (ApiErrorHandling.handleError(result.error))
                                deleteCharacter(scenario, character)
                        }
                    }
                }
                return success
            } else {
                Settings.error_message = "Player is not the GM of this scenario"
                return false
            }
        }

        /**
         * Deletes [player] from the [scenario]
         * @param scenario Chosen [scenario][mScenario]
         * @param player Name of the player to delete
         * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
         */
        suspend fun deletePlayer(scenario: mScenario, player: String): Boolean {
            if (isGM(scenario)) {
                val endpoint =
                    "${Settings.server_address}action/remove/character/scenario/${scenario.scenarioKey}"
                var success = false
                runBlocking {
                    val (_, _, result) = endpoint.httpDelete(listOf("player" to player)).authentication().bearer(
                        User.UserToken.access_token
                    ).awaitStringResponseResult()
                    when (result) {
                        is Result.Success -> {
                            success = true
                        }
                        is Result.Failure -> {
                            if (ApiErrorHandling.handleError(result.error))
                                deletePlayer(scenario, player)
                        }
                    }
                }
                return success
            } else {
                Settings.error_message = "Player is not the GM of this scenario"
                return false
            }
        }

        /**
         * Deletes the [scenario]
         * @param scenario Chosen [scenario][mScenario]
         * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
         */
        suspend fun deleteScenario(scenario: mScenario): Boolean {
            if (isGM(scenario)) {
                val endpoint =
                    "${Settings.server_address}action/remove/scenario/${scenario.scenarioKey}"
                var success = false
                runBlocking {
                    val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                    when (result) {
                        is Result.Success -> {
                            success = true
                        }
                        is Result.Failure -> {
                            if (ApiErrorHandling.handleError(result.error))
                                deleteScenario(scenario)
                        }
                    }
                }
                return success
            } else {
                Settings.error_message = "Player is not the GM of this scenario"
                return false
            }
        }
    }

    //region Scenarios
    /**
     * Creates a [scenario][mScenario]
     * @param name Created [scenario][mScenario]'s name
     * @param name Created [scenario][mScenario]'s password
     * @return [Scenario][mScenario]'s key as a string if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createScenario(name: String, password: String): String? {
        var scenarioKey: String? = null
        val json = JSONObject()
        json.put("name", name)
        json.put("password", password)
        runBlocking {
            val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                User.UserToken.access_token
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    getScenarios()
                    scenarioKey = result.value
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        createScenario(name, password)
                }
            }
        }
        return scenarioKey
    }

    /**
     * Receives the [scenarios][mScenario] and updates the [list][scenariosList]
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getScenarios(): Boolean {
        var success = false
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    val tmplist: MutableList<mScenario> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val numberOfPlayersOnline = obj.getJSONArray("onlinePlayers").length()
                        val tmpScenario = mScenario(
                            name = obj.getString("name"),
                            gameMaster = obj.getString("gameMaster"),
                            scenarioKey = obj.getString("scenarioKey"),
                            onlinePlayers = List<String>(numberOfPlayersOnline) {
                                obj.getJSONArray("onlinePlayers").getString(it)
                            }
                        )
                        tmplist.add(tmpScenario)
                    }
                    scenariosList = tmplist
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getScenarios()
                }
            }
        }
        return success
    }

    /**
     * Joins the [scenario][mScenario] with unique [key][scenarioKey]
     * @param scenarioKey Key to the [scenario][mScenario]
     * @param password Password to the [scenario][mScenario]
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     *
     */
    suspend fun joinScenario(scenarioKey: String, password: String): Boolean {
        val endpoint = "${Settings.server_address}action/join/scenario/$scenarioKey"
        var success = false
        runBlocking {
            val json = JSONObject()
            json.put("password", password)
            val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                User.UserToken.access_token
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    getScenarios()
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        joinScenario(scenarioKey, password)
                }
            }
        }
        return success
    }

    /**
     * Rolls the dice
     * @param scenario Chosen [scenario][mScenario]
     * @param character [Character][mCharacter] who do the roll
     * @param numberOfDices Number of the dices to throw
     * @param dicesValue Number of dice's sides
     * @return array of rolls if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     *
     */
    suspend fun rollDice(
        scenario: mScenario,
        character: mCharacter,
        numberOfDices: Int,
        dicesValue: Int
    ): Array<Int>? {
        val endpoint = "${Settings.server_address}action/roll/scenario/${scenario.scenarioKey}"
        var rolls: Array<Int>? = null
        runBlocking {
            val json = JSONObject()
            json.put("characterName", character.name)
            json.put("dices", numberOfDices)
            json.put("value", dicesValue)
            val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                User.UserToken.access_token
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    rolls =
                        Array(JSONArray(result.value).length()) { JSONArray(result.value).getInt(it) }
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        rollDice(scenario, character, numberOfDices, dicesValue)
                }
            }
        }
        return rolls
    }

    /**
     * Connects the player to the [scenario]
     * @param scenario Chosen [scenario][mScenario]
     * @return true if ended with success or false if mScenariorror occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun connectToScenario(scenario: mScenario): Boolean {
        val endpoint = "$endpoint/${scenario.scenarioKey}/connect"
        var success = false
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    clearScenarioCache()
                    connectedScenario.scenario = scenario
                    getPlayers(scenario)
                    getCharacters(scenario)
                    getNotes(scenario)
                    getMessages(scenario)
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        connectToScenario(scenario)
                }
            }
        }
        return success
    }

    /**
     * Receives the players in the [scenario] and updates [online][mScenario.onlinePlayers]/[offline][mScenario.offlinePlayers] lists
     * @param scenario Chosen [scenario][mScenario]
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getPlayers(scenario: mScenario): Boolean {
        val endpoint = "$endpoint/${scenario.scenarioKey}/player"
        var success = false
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    val json = JSONObject(result.value)
                    val playersList = json.getJSONArray("players")
                    val offPlayers =
                        MutableList<String>(playersList.length()) { playersList.getString(it) }
                    val onlinePlayersList = json.getJSONArray("onlinePlayers")
                    scenario.onlinePlayers =
                        List<String>(onlinePlayersList.length()) { onlinePlayersList.getString(it) }
                    scenario.onlinePlayers.forEach {
                        offPlayers.remove(it)
                    }
                    scenario.offlinePlayers = offPlayers
                    scenario.gameMaster = json.getString("gameMaster")
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getPlayers(scenario)
                }
            }
        }
        return success
    }

    //endregion
    //region Characters
    /**
     * Receives the [characters][mCharacter] in the [scenario][mScenario] and updates the [list][connectedScenario.charactersList]
     * @param scenario Chosen [scenario][mScenario]
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getCharacters(scenario: mScenario): Boolean {
        val endpoint = "$endpoint/${scenario.scenarioKey}/character"
        var success = false
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    val tmplist: MutableList<mCharacter> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpCharacter = mCharacter(
                            name = obj.getString("name"),
                            alignment = obj.getString("alignment"),
                            attributes = Attributes(
                                charisma = obj.getJSONObject("attributes").getInt("charisma"),
                                constitution = obj.getJSONObject("attributes").getInt("constitution"),
                                dexterity = obj.getJSONObject("attributes").getInt("dexterity"),
                                intelligence = obj.getJSONObject("attributes").getInt("intelligence"),
                                strength = obj.getJSONObject("attributes").getInt("strength"),
                                wisdom = obj.getJSONObject("attributes").getInt("wisdom")
                            ),
                            background = obj.getString("background"),
                            experience = obj.getInt("experience"),
                            health = Health(
                                actualHealth = obj.getJSONObject("health").getInt("actualHealth"),
                                maxHealth = obj.getJSONObject("health").getInt("maxHealth"),
                                temporaryHealth = obj.getJSONObject("health").getInt("temporaryHealth")
                            ),
                            hitDices = HitDices(
                                dice = obj.getJSONObject("hitDices").getString("dice"),
                                total = obj.getJSONObject("hitDices").getInt("total"),
                                used = obj.getJSONObject("hitDices").getInt("used")
                            ),
                            initiative = obj.getInt("initiative"),
                            inspiration = obj.getInt("inspiration"),
                            level = obj.getInt("level"),
                            passiveInsight = obj.getInt("passiveInsight"),
                            passivePerception = obj.getInt("passivePerception"),
                            profession = obj.getString("profession"),
                            proficiency = obj.getInt("proficiency"),
                            race = obj.getString("race"),
                            speed = obj.getString("speed"),
                            owner = obj.getString("owner")
                        )
                        val ab = obj.getJSONObject("abilities")
                        tmpCharacter.abilities.features = List(ab.getJSONArray("features").length()){ab.getJSONArray("features").getString(it)}
                        tmpCharacter.abilities.traits = List(ab.getJSONArray("traits").length()){ab.getJSONArray("traits").getString(it)}
                        tmpCharacter.abilities.languages = List(ab.getJSONArray("languages").length()){ab.getJSONArray("languages").getString(it)}
                        tmpCharacter.abilities.proficiencies = List(ab.getJSONArray("proficiencies").length()){ab.getJSONArray("proficiencies").getString(it)}
                        tmplist.add(tmpCharacter)
                        val eq = obj.getJSONObject("equipment")
                        tmpCharacter.equipment.armorClass = eq.getInt("armorClass")
                        tmpCharacter.equipment.armors = List(eq.getJSONArray("armors").length()){eq.getJSONArray("armors").getString(it)}
                        val attcks = eq.getJSONArray("attacks")
                        tmpCharacter.equipment.attacks = List(attcks.length()){ mCharacter.Equipment.Attack(
                            bonus = attcks.getJSONObject(it).getInt("bonus"),
                            damage = attcks.getJSONObject(it).getString("damage"),
                            name = attcks.getJSONObject(it).getString("name"),
                            type = attcks.getJSONObject(it).getString("type")
                        )}
                        tmpCharacter.equipment.currency = mCharacter.Equipment.Currency(
                            cp = eq.getJSONObject("currency").getInt("cp"),
                            sp = eq.getJSONObject("currency").getInt("sp"),
                            ep = eq.getJSONObject("currency").getInt("ep"),
                            gp = eq.getJSONObject("currency").getInt("gp"),
                            pp = eq.getJSONObject("currency").getInt("pp")
                        )
                        tmpCharacter.equipment.gear = List(eq.getJSONArray("gear").length()){eq.getJSONArray("gear").getString(it)}
                        tmpCharacter.equipment.tools = List(eq.getJSONArray("tools").length()){eq.getJSONArray("tools").getString(it)}
                        tmpCharacter.equipment.vehicles = List(eq.getJSONArray("vehicles").length()){eq.getJSONArray("vehicles").getString(it)}
                        tmpCharacter.equipment.weapons = List(eq.getJSONArray("weapons").length()){eq.getJSONArray("weapons").getString(it)}
                        val spells = obj.getJSONObject("spells")
                        tmpCharacter.spells.baseStat = spells.getString("baseStat")
                        tmpCharacter.spells.spellAttackBonus = spells.getInt("spellAttackBonus")
                        tmpCharacter.spells.spellSaveDc = spells.getInt("spellSaveDc")
                        val slots = spells.getJSONArray("spellSlots")
                        tmpCharacter.spells.spellSlots = List(slots.length()){Spells.SpellSlot(
                            level = slots.getJSONObject(it).getInt("level"),
                            total = slots.getJSONObject(it).getInt("total"),
                            used = slots.getJSONObject(it).getInt("used")
                        )}
                        tmpCharacter.spells.spells = List(spells.getJSONArray("spells").length()){spells.getJSONArray("spells").getString(it)}
                    }
                    connectedScenario.charactersList = tmplist
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getCharacters(scenario)
                }
            }
        }
        return success
    }

    /**
     * Creates a [character][mCharacter] in the [scenario]
     * @param scenario [Scenario][mScenario] to put the [character][mCharacter] into
     * @param name Name of the [character][mCharacter]
     * @param alignment [Character][mCharacter]'s alignment
     * @param attributes [Character][mCharacter]'s [attributes][Attributes]
     * @param background [Character][mCharacter]'s history/background
     * @param experience [Character][mCharacter]'s experience points
     * @param health [Character][mCharacter]'s [health][Health] statistics
     * @param hitDices [Hit dices][HitDices] [character][mCharacter] is using
     * @param initiative [Character][mCharacter]'s initiative statistic
     * @param inspiration [Character][mCharacter]'s inspiration statistic
     * @param level [Character][mCharacter]'s level
     * @param passiveInsight [Character][mCharacter]'s passive insight statistic
     * @param passivePerception [Character][mCharacter]'s passive perception statistic
     * @param profession [Character][mCharacter]'s profession
     * @param proficiency [Character][mCharacter]'s proficiency bonus
     * @param race [Character][mCharacter]'s race
     * @param speed [Character][mCharacter]'s movement speed
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createCharacter(
        scenario: mScenario,
        name: String,
        alignment: String? = null,
        attributes: Attributes? = null,
        background: String? = null,
        experience: Int? = null,
        health: Health? = null,
        hitDices: HitDices? = null,
        initiative: Int? = null,
        inspiration: Int? = null,
        level: Int? = null,
        passiveInsight: Int? = null,
        passivePerception: Int? = null,
        profession: String? = null,
        proficiency: Int? = null,
        race: String? = null, speed: String? = null
    ): Boolean {
        val endpoint =
            "${Settings.server_address}action/create/character/scenario/${scenario.scenarioKey}"
        var success = false
        val json = JSONObject()
        json.put("name", name)
        if (alignment != null)
            json.put("alignment", alignment)
        val attr = JSONObject()
        if (attributes != null) {
            attr.put("charisma", attributes.charisma)
            attr.put("constitution", attributes.constitution)
            attr.put("dexterity", attributes.dexterity)
            attr.put("intelligence", attributes.intelligence)
            attr.put("strength", attributes.strength)
            attr.put("wisdom", attributes.wisdom)
        }
        json.put("attributes", attr)
        if (background != null)
            json.put("background", background)
        if (experience != null)
            json.put("experience", experience)
        val hp = JSONObject()
        if (health != null) {
            hp.put("actualHealth", health.actualHealth)
            hp.put("maxHealth", health.maxHealth)
            hp.put("temporaryHealth", health.temporaryHealth)
        }
        json.put("health", hp)
        val hd = JSONObject()
        if (hitDices != null) {
            hd.put("dice", hitDices.dice)
            hd.put("total", hitDices.total)
            hd.put("used", hitDices.used)
        }
        json.put("hitDices", hd)
        if (initiative != null)
            json.put("initiative", initiative)
        if (inspiration != null)
            json.put("inspiration", inspiration)
        if (level != null)
            json.put("level", level)
        if (passiveInsight != null)
            json.put("passiveInsight", passiveInsight)
        if (passivePerception != null)
            json.put("passivePerception", passivePerception)
        if (profession != null)
            json.put("profession", profession)
        if (proficiency != null)
            json.put("proficiency", proficiency)
        if (race != null)
            json.put("race", race)
        if (speed != null)
            json.put("speed", speed)


        runBlocking {
            val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                User.UserToken.access_token
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        createCharacter(
                            scenario,
                            name,
                            alignment,
                            attributes,
                            background,
                            experience,
                            health,
                            hitDices,
                            initiative,
                            inspiration,
                            level,
                            passiveInsight,
                            passivePerception,
                            profession,
                            proficiency,
                            race,
                            speed
                        )
                }
            }
        }
        return success
    }

    /**
     * Patches a [character][mCharacter] in the [scenario]
     * @param scenario [Scenario][mScenario] to put the [character][mCharacter] into
     * @param name Name of the [character][mCharacter] (must match a character in [scenario])
     * @param alignment [Character][mCharacter]'s alignment
     * @param attributes [Character][mCharacter]'s [attributes][Attributes]
     * @param background [Character][mCharacter]'s history/background
     * @param experience [Character][mCharacter]'s experience points
     * @param health [Character][mCharacter]'s [health][Health] statistics
     * @param hitDices [Hit dices][HitDices] [character][mCharacter] is using
     * @param initiative [Character][mCharacter]'s initiative statistic
     * @param inspiration [Character][mCharacter]'s inspiration statistic
     * @param level [Character][mCharacter]'s level
     * @param passiveInsight [Character][mCharacter]'s passive insight statistic
     * @param passivePerception [Character][mCharacter]'s passive perception statistic
     * @param profession [Character][mCharacter]'s profession
     * @param proficiency [Character][mCharacter]'s proficiency bonus
     * @param race [Character][mCharacter]'s race
     * @param speed [Character][mCharacter]'s movement speed
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchCharacter(
        scenario: mScenario,
        name: String,
        alignment: String? = null,
        attributes: Attributes? = null,
        background: String? = null,
        experience: Int? = null,
        health: Health? = null,
        hitDices: HitDices? = null,
        initiative: Int? = null,
        inspiration: Int? = null,
        level: Int? = null,
        passiveInsight: Int? = null,
        passivePerception: Int? = null,
        profession: String? = null,
        proficiency: Int? = null,
        race: String? = null, speed: String? = null
    ): Boolean {
        val endpoint =
            "${Settings.server_address}action/update/character/scenario/${scenario.scenarioKey}"
        var success = false
        val json = JSONObject()
        json.put("name", name)
        if (alignment != null)
            json.put("alignment", alignment)
        val attr = JSONObject()
        if (attributes != null) {
            attr.put("charisma", attributes.charisma)
            attr.put("constitution", attributes.constitution)
            attr.put("dexterity", attributes.dexterity)
            attr.put("intelligence", attributes.intelligence)
            attr.put("strength", attributes.strength)
            attr.put("wisdom", attributes.wisdom)
        }
        json.put("attributes", attr)
        if (background != null)
            json.put("background", background)
        if (experience != null)
            json.put("experience", experience)
        val hp = JSONObject()
        if (health != null) {
            hp.put("actualHealth", health.actualHealth)
            hp.put("maxHealth", health.maxHealth)
            hp.put("temporaryHealth", health.temporaryHealth)
        }
        json.put("health", hp)
        val hd = JSONObject()
        if (hitDices != null) {
            hd.put("dice", hitDices.dice)
            hd.put("total", hitDices.total)
            hd.put("used", hitDices.used)
        }
        json.put("hitDices", hd)
        if (initiative != null)
            json.put("initiative", initiative)
        if (inspiration != null)
            json.put("inspiration", inspiration)
        if (level != null)
            json.put("level", level)
        if (passiveInsight != null)
            json.put("passiveInsight", passiveInsight)
        if (passivePerception != null)
            json.put("passivePerception", passivePerception)
        if (profession != null)
            json.put("profession", profession)
        if (proficiency != null)
            json.put("proficiency", proficiency)
        if (race != null)
            json.put("race", race)
        if (speed != null)
            json.put("speed", speed)


        runBlocking {
            val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                User.UserToken.access_token
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        patchCharacter(
                            scenario,
                            name,
                            alignment,
                            attributes,
                            background,
                            experience,
                            health,
                            hitDices,
                            initiative,
                            inspiration,
                            level,
                            passiveInsight,
                            passivePerception,
                            profession,
                            proficiency,
                            race,
                            speed
                        )
                }
            }
        }
        return success
    }

    /**
     * Patches a [character][mCharacter]'s [abilities][mCharacter.Abilities] in the [scenario]
     * @param scenario [Scenario][mScenario] to put the [character][mCharacter] into
     * @param name Name of the [character][mCharacter] (must match a character in [scenario])
     * @param features List of [features][com.polarlooptheory.was.model.abilities.mFeature]' names
     * @param languages List of [languages][com.polarlooptheory.was.model.abilities.mLanguage]' names
     * @param proficiencies List of [proficiencies][com.polarlooptheory.was.model.abilities.mProficiency]' names
     * @param traits List of [traits][com.polarlooptheory.was.model.abilities.mTrait]' names
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchCharacterAbilities(
        scenario: mScenario,
        name: String,
        features: List<String>? = null,
        languages: List<String>? = null,
        proficiencies: List<String>? = null,
        traits: List<String>? = null
    ): Boolean {
        val endpoint =
            "${Settings.server_address}action/update/characterAbilities/scenario/${scenario.scenarioKey}"
        var success = false
        val json = JSONObject()
        json.put("name", name)
        val feat = JSONArray()
        features?.forEach { feat.put(it) }
        json.put("features",feat)
        val lang = JSONArray()
        languages?.forEach { lang.put(it) }
        json.put("languages",lang)
        val prof = JSONArray()
        proficiencies?.forEach { prof.put(it) }
        json.put("proficiencies",prof)
        val tr = JSONArray()
        traits?.forEach { tr.put(it) }
        json.put("traits",tr)
        runBlocking {
            val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                User.UserToken.access_token
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        patchCharacterAbilities(scenario, name, features, languages, proficiencies, traits)
                }
            }
        }
        return success
    }
    
    /**
     * Patches a [character][mCharacter]'s [equipment][mCharacter.Equipment] in the [scenario]
     * @param scenario [Scenario][mScenario] to put the [character][mCharacter] into
     * @param name Name of the [character][mCharacter] (must match a character in [scenario])
     * @param armorClass [character][mCharacter]'s armor Class
     * @param armors List of [armors][com.polarlooptheory.was.model.equipment.mArmor]' names
     * @param attacks List of [attacks][mCharacter.Equipment.Attack] character knows
     * @param cp Number of copper pieces character possesses
     * @param sp Number of silver pieces character possesses
     * @param ep Number of electrum pieces character possesses
     * @param gp Number of gold pieces character possesses
     * @param pp Number of platinum pieces character possesses
     * @param gear List of [gear][com.polarlooptheory.was.model.equipment.mGear]' names
     * @param tools List of [tools][com.polarlooptheory.was.model.equipment.mTool]' names
     * @param vehicles List of [vehicles][com.polarlooptheory.was.model.equipment.mVehicle]' names
     * @param weapons List of [weapons][com.polarlooptheory.was.model.equipment.mWeapon]' names
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchCharacterEquipment(
        scenario: mScenario,
        name: String,
        armorClass: Int? = null,
        armors: List<String>? = null,
        attacks: List<mCharacter.Equipment.Attack>? = null,
        cp: Int? = null,
        sp: Int? = null,
        ep: Int? = null,
        gp: Int? = null,
        pp: Int? = null,
        gear: List<String>? = null,
        tools: List<String>? = null,
        vehicles: List<String>? = null,
        weapons: List<String>? = null
    ): Boolean {
        val endpoint =
            "${Settings.server_address}action/update/characterAbilities/scenario/${scenario.scenarioKey}"
        var success = false
        val json = JSONObject()
        json.put("name", name)
        if (armorClass != null)
            json.put("armorClass",armorClass)
        val attcs = JSONArray()
        attacks?.forEach { 
            val att = JSONObject()
            if(it.bonus != null)
                att.put("bonus",it.bonus)
            if(it.damage != null)
                att.put("damage",it.damage)
            if(it.name != null)
                att.put("name",it.name)
            if(it.type != null)
                att.put("type",it.type)
            attcs.put(att)
        }
        json.put("attacks",attcs)
        val arm = JSONArray()
        armors?.forEach { arm.put(it) }
        json.put("armors",arm)
        val curr = JSONObject()
        if(cp != null)
            curr.put("cp",cp)
        if(sp != null)
            curr.put("sp",sp)
        if(ep != null)
            curr.put("ep",ep)
        if(gp != null)
            curr.put("gp",gp)
        if(pp != null)
            curr.put("pp",pp)
        val gr = JSONArray()
        gear?.forEach { gr.put(it) }
        json.put("gear",gr)
        val tl = JSONArray()
        tools?.forEach { tl.put(it) }
        json.put("tools",tl)
        val veh = JSONArray()
        vehicles?.forEach { veh.put(it) }
        json.put("vehicles",veh)
        val weap = JSONArray()
        weapons?.forEach { weap.put(it) }
        json.put("weapons",weap)
        runBlocking {
            val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                User.UserToken.access_token
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        patchCharacterEquipment(scenario, name, armorClass, armors, attacks, cp, sp, ep, gp, pp, gear, tools, vehicles, weapons)
                }
            }
        }
        return success
    }
    
    /**
     * Patches a [character][mCharacter]'s [spells][mCharacter.Spells] in the [scenario]
     * @param scenario [Scenario][mScenario] to put the [character][mCharacter] into
     * @param name Name of the [character][mCharacter] (must match a character in [scenario])
     * @param baseStat Base [attribute][mCharacter.Attributes] to cast spells
     * @param spellAttackBonus Spells' attack bonus
     * @param spellSaveDc Spells' saving throw difficulty
     * @param spellSlots List of [spell slots][com.polarlooptheory.was.model.mCharacter.Spells.SpellSlot]
     * @param spells List of [spells][com.polarlooptheory.was.model.abilities.mSpell]' names
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchCharacterSpells(
        scenario: mScenario,
        name: String,
        baseStat: String? = null,
        spellAttackBonus: Int? = null,
        spellSaveDc: Int? = null,
        spellSlots: List<Spells.SpellSlot>? = null,
        spells: List<String>? = null
    ): Boolean {
        val endpoint =
            "${Settings.server_address}action/update/characterAbilities/scenario/${scenario.scenarioKey}"
        var success = false
        val json = JSONObject()
        json.put("name", name)
        if (baseStat != null)
            json.put("baseStat",baseStat)
        if (spellAttackBonus != null)
            json.put("spellAttackBonus",spellAttackBonus)
        if (spellSaveDc != null)
            json.put("spellSaveDc",spellSaveDc)
        val sss = JSONArray()
        spellSlots?.forEach { 
            val ss = JSONObject()
            if (it.level != null)
                ss.put("level",it.level)
            if (it.total != null)
                ss.put("total",it.total)
            if (it.used != null)
                ss.put("used",it.used)
            sss.put(ss)
        }
        json.put("spellSlots",sss)
        val spls = JSONArray()
        spells?.forEach { spls.put(it) }
        json.put("spells",spls)
        runBlocking {
            val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                User.UserToken.access_token
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        patchCharacterSpells(scenario, name, baseStat, spellAttackBonus, spellSaveDc, spellSlots, spells)
                }
            }
        }
        return success
    }

    //endregion
    //region Notes
    /**
     * Receives the [notes][mNote] in the [scenario] and updates the [list][connectedScenario.notesList]
     * @param scenario Chosen [scenario][mScenario]
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getNotes(scenario: mScenario): Boolean {
        val endpoint = "$endpoint/${scenario.scenarioKey}/note"
        var success = false
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    val tmplist: MutableList<mNote> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpNote = mNote(
                            name = obj.getString("name"),
                            id = obj.getInt("id"),
                            content = obj.getString("conten")
                        )
                        tmplist.add(tmpNote)
                    }
                    connectedScenario.notesList = tmplist
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getNotes(scenario)
                }
            }
        }
        return success
    }

    /**
     * Creates a [note][mNote] in the [scenario]
     * @param scenario [Scenario][mScenario] to put the [note][mNote] into
     * @param name Name/Title of the [note][mNote]
     * @param content Content of the [note][mNote]
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createNote(scenario: mScenario, name: String, content: String): Boolean {
        val endpoint = "$endpoint/${scenario.scenarioKey}/note"
        var success = false
        val json = JSONObject()
        json.put("name", name)
        json.put("content", content)
        runBlocking {
            val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                User.UserToken.access_token
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    getNotes(scenario)
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        createNote(scenario, name, content)
                }
            }
        }
        return success
    }

    /**
     * Deletes a [note][mNote] from the [scenario]
     * @param scenario [Scenario][mScenario] from which the [note][mNote] is deleted
     * @param id Id of the [note][mNote] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteNote(scenario: mScenario, id: Int): Boolean {
        val endpoint = "$endpoint/${scenario.scenarioKey}/note/$id"
        var success = false
        runBlocking {
            val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    getNotes(scenario)
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        deleteNote(scenario, id)
                }
            }
        }
        return success
    }

    /**
     * Patch a [note][mNote] in the [scenario]
     * @param scenario [Scenario][mScenario] in which the [note][mNote] will be patched
     * @param id Id of the [note][mNote] to patch
     * @param name Name of the [note][mNote]
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchNote(scenario: mScenario, id: Int, name: String, content: String): Boolean {
        val endpoint = "$endpoint/${scenario.scenarioKey}/note/$id"
        var success = false
        val json = JSONObject()
        json.put("name", name)
        json.put("content", content)
        runBlocking {
            val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                User.UserToken.access_token
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    getNotes(scenario)
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        patchNote(scenario, id, name, content)
                }
            }
        }
        return success
    }
    //endregion
    //region Messages
    /**
     * Receives the messages in the [scenario] and updates the [list][connectedScenario.messagesList]
     * @param scenario Chosen [scenario][mScenario]
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getMessages(scenario: mScenario): Boolean {
        val endpoint = "$endpoint/${scenario.scenarioKey}/message"
        var success = false
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    val tmplist: MutableList<mMessage> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpMessage = mMessage(
                            content = obj.getString("content"),
                            sender = obj.getString("sender"),
                            whisperTarget = obj.getString("whisperTarget"),
                            type = when (obj.getString("type")) {
                                "system" -> mMessage.Type.SYSTEM
                                "character" -> mMessage.Type.CHARACTER
                                "whisper" -> mMessage.Type.WHISPER
                                else -> mMessage.Type.OOC
                            }
                        )
                        tmplist.add(tmpMessage)
                    }
                    connectedScenario.messagesList = tmplist
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getMessages(scenario)
                }
            }
        }
        return success
    }

    /**
     * Sends a message in the [scenario]'s chat
     * @param scenario Chosen [scenario][mScenario]
     * @param character [Character][mCharacter] which sends the message(can be null if message is sent as an OOC message)
     * @param text Text to send as the message
     * @param whisperTarget Target of a whisper, if none pass empty string
     * @param type [Type][mMessage.Type] of the message
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun sendMessage(
        scenario: mScenario,
        character: mCharacter?,
        text: String,
        whisperTarget: String,
        type: mMessage.Type
    ): Boolean {
        val endpoint = "${Settings.server_address}action/message/scenario/${scenario.scenarioKey}"
        var success = false
        runBlocking {
            val json = JSONObject()
            if (character != null)
                json.put("characterName", character.name)
            else json.put("characterName", null)
            val content = when (type) {
                mMessage.Type.OOC -> "/ooc "
                mMessage.Type.WHISPER -> "/whisper "
                else -> ""
            } + "$whisperTarget $text".trim()
            json.put("content", content)
            val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                User.UserToken.access_token
            ).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    success = true
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        sendMessage(scenario, character, text, whisperTarget, type)
                }
            }
        }
        return success
    }
    //endregion

    //region WebSocket
    /*fun joinWebsocket(scenario: mScenario){
        val lifecycle = mStompClient.lifecycle().subscribe { event ->
            when(event.type){
                LifecycleEvent.Type.OPENED -> {
                    Log.i("STOMP","Connection opened")
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.i("STOMP","Error ",event.exception)
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.i("STOMP","Connection closed")
                }
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                    Log.i("STOMP","Failed server heartbeat")
                }
            }
        }
        if(!mStompClient.isConnected)
            mStompClient.connect(listOf(StompHeader("X-Authorization",User.UserToken.access_token)))
        val scenarioTopic = mStompClient.topic("/ws/scenario/${scenario.scenarioKey}").subscribe({msg -> println(msg.payload) }, {err -> Log.i("ScenarioSTOMP",err.message)})
        val playerTopic = mStompClient.topic("/ws/scenario/${scenario.scenarioKey}/player/${User.username}").subscribe({msg -> println(msg.payload) }, {err -> Log.i("PlayerSTOMP",err.message)})
        compositeDisposable.add(lifecycle)
        compositeDisposable.add(scenarioTopic)
        compositeDisposable.add(playerTopic)
    }
    fun leaveWebsocket(){
        mStompClient.disconnect()
    }*/
    //endregion

    /**
     * Clears cached scenarios
     */
    fun clear() {
        scenariosList = listOf()
        clearScenarioCache()
    }
    private fun clearScenarioCache(){
        connectedScenario.clear()
        loadedResources.clear()
    }
}