package com.polarlooptheory.was.apiCalls

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
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

/**
 * Object for handling scenarios
 * @property scenariosList List of [scenarios][mScenario] joined/created by the [user][User]
 */
object Scenario {
    private val endpoint = Settings.server_address + "api/v1/scenario"
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
    }

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
     * Receives the characters in the [scenario][mScenario] and updates the [list][connectedScenario.charactersList]
     * @param scenario Chosen [scenario][mScenario]
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getCharacters(scenario: mScenario): Boolean {
        //todo(First do characters in model)
        val endpoint = "$endpoint/${scenario.scenarioKey}/character"
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
                        getCharacters(scenario)
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


    /**
     * Clears cached scenarios
     */
    fun clear() {
        scenariosList = listOf()
        connectedScenario.scenario = mScenario()
        connectedScenario.notesList = listOf()
        connectedScenario.messagesList = listOf()
        connectedScenario.charactersList = listOf()
    }
}