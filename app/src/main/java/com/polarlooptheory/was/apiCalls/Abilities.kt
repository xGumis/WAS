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
import com.polarlooptheory.was.model.User
import com.polarlooptheory.was.model.abilities.*
import com.polarlooptheory.was.model.mScenario
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

/**
 * Object for handling abilities
 */
object Abilities {
    private const val endpoint = "${Settings.server_address}api/v1/scenario"
    //region Features
    /**
     * Receives a list of the [features][mFeature] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [Features][mFeature] matching the name
     * @return list of features if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getFeatures(scenario: mScenario, name: String? = null): List<mFeature>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/feature"
        var list: List<mFeature>? = null
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
                if (name == null) {
                    null
                } else {
                    listOf("name" to name)
                }
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    val tmplist: MutableList<mFeature> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpFeature = mFeature(
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            visible = obj.getBoolean("visible"),
                            custom = !obj.getString("scenarioKey").equals("null")
                        )
                        if(!Scenario.loadedResources.features.containsKey(tmpFeature.name))
                            Scenario.loadedResources.features[tmpFeature.name] = tmpFeature
                        tmplist.add(tmpFeature)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getFeatures(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [feature][mFeature] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] to put the [feature][mFeature] into
     * @param name Name/Title of the [feature][mFeature]
     * @param description [Feature][mFeature]'s description
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createFeature(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/feature"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpFeature = mFeature(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpFeature.description = description
                        if (visible != null)
                            tmpFeature.visible = visible
                        Scenario.loadedResources.features[tmpFeature.name] = tmpFeature
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createFeature(scenario, name, description, visible)
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
     * Deletes a [feature][mFeature] from the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] from which the [feature][mFeature] is deleted
     * @param name Name of the [feature][mFeature] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteFeature(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/feature/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        Scenario.loadedResources.features.remove(name)
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteFeature(scenario, name)
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
     * Patch a [feature][mFeature] in the [scenario]
     *
     * It cannot change the name of the [feature][mFeature]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] in which the [feature][mFeature] will be patched
     * @param name Name of the [feature][mFeature] to patch
     * @param description Description of the [feature][mFeature]
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchFeature(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/feature"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (visible != null)
                json.put("visible", visible)
            val client = HttpClient()
            runBlocking {
                val result = client.patch<HttpResponse>(endpoint){
                    header("Authorization","Bearer "+User.UserToken.access_token)
                    body = TextContent(json.toString(), ContentType.Application.Json)
                }
                when (result.status.value) {
                    200 -> {
                        val tmpFeature = mFeature(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpFeature.description = description
                        if (visible != null)
                            tmpFeature.visible = visible
                        Scenario.loadedResources.features[tmpFeature.name] = tmpFeature
                        success = true
                    }
                    else -> {
                        Settings.error_message = result.readText()
                    }
                }
                client.close()
            }
            /*To use when fixed
            runBlocking {
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpFeature = mFeature(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpFeature.description = description
                        if (visible != null)
                            tmpFeature.visible = visible
                        Scenario.loadedResources.features[tmpFeature.name] = tmpFeature
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchFeature(scenario, name, description, visible)
                    }
                }
            }*/
            return success
        } else {
            Settings.error_message = "Player is not the GM of this scenario"
            return false
        }
    }
    //endregion
    //region Languages
    /**
     * Receives a list of the [languages][mLanguage] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [languages][mLanguage] matching the name
     * @return list of languages if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getLanguages(scenario: mScenario, name: String? = null): List<mLanguage>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/language"
        var list: List<mLanguage>? = null
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
                if (name == null) {
                    null
                } else {
                    listOf("name" to name)
                }
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    val tmplist: MutableList<mLanguage> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpLanguage = mLanguage(
                            name = obj.getString("name"),
                            script = obj.getString("script"),
                            type = obj.getString("type"),
                            visible = obj.getBoolean("visible"),
                            custom = !obj.getString("scenarioKey").equals("null")
                        )
                        if(!Scenario.loadedResources.languages.containsKey(tmpLanguage.name))
                            Scenario.loadedResources.languages[tmpLanguage.name] = tmpLanguage
                        tmplist.add(tmpLanguage)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getLanguages(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [language][mLanguage] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] to put the [language][mLanguage] into
     * @param name Name/Title of the [language][mLanguage]
     * @param script [Language][mLanguage]'s script
     * @param type [Language][mLanguage]'s type
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createLanguage(
        scenario: mScenario,
        name: String,
        script: String? = null,
        type: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/language"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (script != null)
                json.put("script", script)
            if (type != null)
                json.put("type", type)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpLanguage = mLanguage(
                            name = name,
                            custom = true)
                        if (script != null)
                            tmpLanguage.script = script
                        if (type != null)
                            tmpLanguage.type = type
                        if (visible != null)
                            tmpLanguage.visible = visible
                        Scenario.loadedResources.languages[tmpLanguage.name] = tmpLanguage
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createLanguage(scenario, name, type, script, visible)
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
     * Deletes a [language][mLanguage] from the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] from which the [language][mLanguage] is deleted
     * @param name Name of the [language][mLanguage] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteLanguage(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/language/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        Scenario.loadedResources.languages.remove(name)
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteLanguage(scenario, name)
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
     * Patch a [language][mLanguage] in the [scenario]
     *
     * It cannot change the name of the [language][mLanguage]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] in which the [language][mLanguage] will be patched
     * @param name Name of the [language][mLanguage] to patch
     * @param script [Language][mLanguage]'s script
     * @param type Description of the [language][mLanguage]
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchLanguage(
        scenario: mScenario,
        name: String,
        script: String? = null,
        type: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/language"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (script != null)
                json.put("script", script)
            if (type != null)
                json.put("type", type)
            if (visible != null)
                json.put("visible", visible)
            val client = HttpClient()
            runBlocking {
                val result = client.patch<HttpResponse>(endpoint){
                    header("Authorization","Bearer "+User.UserToken.access_token)
                    body = TextContent(json.toString(), ContentType.Application.Json)
                }
                when (result.status.value) {
                    200 -> {
                        val tmpLanguage = mLanguage(
                            name = name,
                            custom = true)
                        if (script != null)
                            tmpLanguage.script = script
                        if (type != null)
                            tmpLanguage.type = type
                        if (visible != null)
                            tmpLanguage.visible = visible
                        Scenario.loadedResources.languages[tmpLanguage.name] = tmpLanguage
                        success = true
                    }
                    else -> {
                        Settings.error_message = result.readText()
                    }
                }
                client.close()
            }
            /*To use when fixed
            runBlocking {
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpLanguage = mLanguage(
                            name = name,
                            custom = true)
                        if (script != null)
                            tmpLanguage.script = script
                        if (type != null)
                            tmpLanguage.type = type
                        if (visible != null)
                            tmpLanguage.visible = visible
                        Scenario.loadedResources.languages[tmpLanguage.name] = tmpLanguage
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchLanguage(scenario, name, type, script, visible)
                    }
                }
            }*/
            return success
        } else {
            Settings.error_message = "Player is not the GM of this scenario"
            return false
        }
    }
    //endregion
    //region Proficiencies
    /**
     * Receives a list of the [proficiencies][mProficiency] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [proficiencies][mProficiency] matching the name
     * @return list of proficiencies if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getProficiencies(scenario: mScenario, name: String? = null): List<mProficiency>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/proficiency"
        var list: List<mProficiency>? = null
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
                if (name == null) {
                    null
                } else {
                    listOf("name" to name)
                }
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    val tmplist: MutableList<mProficiency> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpProficiency = mProficiency(
                            name = obj.getString("name"),
                            type = obj.getString("type"),
                            visible = obj.getBoolean("visible"),
                            custom = !obj.getString("scenarioKey").equals("null")
                        )
                        if(!Scenario.loadedResources.proficiencies.containsKey(tmpProficiency.name))
                            Scenario.loadedResources.proficiencies[tmpProficiency.name] = tmpProficiency
                        tmplist.add(tmpProficiency)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getProficiencies(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [proficiency][mProficiency] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] to put the [proficiency][mProficiency] into
     * @param name Name/Title of the [proficiency][mProficiency]
     * @param type [Proficiency][mProficiency]'s type
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createProficiency(
        scenario: mScenario,
        name: String,
        type: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/proficiency"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (type != null)
                json.put("type", type)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpProficiency = mProficiency(
                            name = name,
                            custom = true)
                        if (type != null)
                            tmpProficiency.type = type
                        if (visible != null)
                            tmpProficiency.visible = visible
                        Scenario.loadedResources.proficiencies[tmpProficiency.name] = tmpProficiency
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createProficiency(scenario, name, type, visible)
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
     * Deletes a [proficiency][mProficiency] from the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] from which the [proficiency][mProficiency] is deleted
     * @param name Name of the [proficiency][mProficiency] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteProficiency(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/proficiency/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        Scenario.loadedResources.proficiencies.remove(name)
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteProficiency(scenario, name)
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
     * Patch a [proficiency][mProficiency] in the [scenario]
     *
     * It cannot change the name of the [proficiency][mProficiency]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] in which the [proficiency][mProficiency] will be patched
     * @param name Name of the [proficiency][mProficiency] to patch
     * @param type Description of the [proficiency][mProficiency]
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchProficiency(
        scenario: mScenario,
        name: String,
        type: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/proficiency"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (type != null)
                json.put("type", type)
            if (visible != null)
                json.put("visible", visible)
            val client = HttpClient()
            runBlocking {
                val result = client.patch<HttpResponse>(endpoint){
                    header("Authorization","Bearer "+User.UserToken.access_token)
                    body = TextContent(json.toString(), ContentType.Application.Json)
                }
                when (result.status.value) {
                    200 -> {
                        val tmpProficiency = mProficiency(
                            name = name,
                            custom = true)
                        if (type != null)
                            tmpProficiency.type = type
                        if (visible != null)
                            tmpProficiency.visible = visible
                        Scenario.loadedResources.proficiencies[tmpProficiency.name] = tmpProficiency
                        success = true
                    }
                    else -> {
                        Settings.error_message = result.readText()
                    }
                }
                client.close()
            }
            /*To use when fixed
            runBlocking {
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpProficiency = mProficiency(
                            name = name,
                            custom = true)
                        if (type != null)
                            tmpProficiency.type = type
                        if (visible != null)
                            tmpProficiency.visible = visible
                        Scenario.loadedResources.proficiencies[tmpProficiency.name] = tmpProficiency
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchProficiency(scenario, name, type, visible)
                    }
                }
            }*/
            return success
        } else {
            Settings.error_message = "Player is not the GM of this scenario"
            return false
        }
    }
    //endregion
    //region Skills
    /**
     * Receives a list of the [skills][mSkill] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [skills][mSkill] matching the name
     * @return list of skills if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getSkills(scenario: mScenario, name: String? = null): List<mSkill>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/skill"
        var list: List<mSkill>? = null
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
                if (name == null) {
                    null
                } else {
                    listOf("name" to name)
                }
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    val tmplist: MutableList<mSkill> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpSkill = mSkill(
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            abilityScore = obj.getString("abilityScore"),
                            visible = obj.getBoolean("visible"),
                            custom = !obj.getString("scenarioKey").equals("null")
                        )
                        if(!Scenario.loadedResources.skills.containsKey(tmpSkill.name))
                            Scenario.loadedResources.skills[tmpSkill.name] = tmpSkill
                        tmplist.add(tmpSkill)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getSkills(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [skill][mSkill] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] to put the [skill][mSkill] into
     * @param name Name/Title of the [skill][mSkill]
     * @param description [Skill][mSkill]'s description
     * @param abilityScore [Skill][mSkill]'s primary ability
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createSkill(
        scenario: mScenario,
        name: String,
        description: String? = null,
        abilityScore: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/skill"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (abilityScore != null)
                json.put("abilityScore", abilityScore)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpSkill = mSkill(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpSkill.description = description
                        if (abilityScore != null)
                            tmpSkill.abilityScore = abilityScore
                        if (visible != null)
                            tmpSkill.visible = visible
                        Scenario.loadedResources.skills[tmpSkill.name] = tmpSkill
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createSkill(scenario, name, description, abilityScore, visible)
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
     * Deletes a [skill][mSkill] from the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] from which the [skill][mSkill] is deleted
     * @param name Name of the [skill][mSkill] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteSkill(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/skill/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        Scenario.loadedResources.skills.remove(name)
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteSkill(scenario, name)
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
     * Patch a [skill][mSkill] in the [scenario]
     *
     * It cannot change the name of the [skill][mSkill]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] in which the [skill][mSkill] will be patched
     * @param name Name of the [skill][mSkill] to patch
     * @param description Description of the [skill][mSkill]
     * @param abilityScore [Skill][mSkill]'s primary ability
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchSkill(
        scenario: mScenario,
        name: String,
        description: String? = null,
        abilityScore: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/skill"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (abilityScore != null)
                json.put("abilityScore", abilityScore)
            if (visible != null)
                json.put("visible", visible)
            val client = HttpClient()
            runBlocking {
                val result = client.patch<HttpResponse>(endpoint){
                    header("Authorization","Bearer "+User.UserToken.access_token)
                    body = TextContent(json.toString(), ContentType.Application.Json)
                }
                when (result.status.value) {
                    200 -> {
                        val tmpSkill = mSkill(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpSkill.description = description
                        if (abilityScore != null)
                            tmpSkill.abilityScore = abilityScore
                        if (visible != null)
                            tmpSkill.visible = visible
                        Scenario.loadedResources.skills[tmpSkill.name] = tmpSkill
                        success = true
                    }
                    else -> {
                        Settings.error_message = result.readText()
                    }
                }
                client.close()
            }
            /*To use when fixed
            runBlocking {
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpSkill = mSkill(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpSkill.description = description
                        if (abilityScore != null)
                            tmpSkill.abilityScore = abilityScore
                        if (visible != null)
                            tmpSkill.visible = visible
                        Scenario.loadedResources.skills[tmpSkill.name] = tmpSkill
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchSkill(scenario, name, description, abilityScore, visible)
                    }
                }
            }*/
            return success
        } else {
            Settings.error_message = "Player is not the GM of this scenario"
            return false
        }
    }
    //endregion
    //region Spells
    /**
     * Receives a list of the [spells][mSpell] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [spells][mSpell] matching the name
     * @return list of spells if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getSpells(scenario: mScenario, name: String? = null): List<mSpell>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/spell"
        var list: List<mSpell>? = null
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
                if (name == null) {
                    null
                } else {
                    listOf("name" to name)
                }
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    val tmplist: MutableList<mSpell> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpSpell = mSpell(
                            name = obj.getString("name"),
                            magicSchool = obj.getString("magicSchool"),
                            description = obj.getString("description"),
                            visible = obj.getBoolean("visible"),
                            castingTime = obj.getString("castingTime"),
                            components = obj.getString("components"),
                            concentration = obj.getBoolean("concentration"),
                            duration = obj.getString("duration"),
                            higherLevels = obj.getString("higherLevels"),
                            level = obj.getInt("level"),
                            material = obj.getString("material"),
                            range = obj.getString("range"),
                            ritual = obj.getBoolean("ritual"),
                            custom = !obj.getString("scenarioKey").equals("null")
                        )
                        if(!Scenario.loadedResources.spells.containsKey(tmpSpell.name))
                            Scenario.loadedResources.spells[tmpSpell.name] = tmpSpell
                        tmplist.add(tmpSpell)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getSpells(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [spell][mSpell] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] to put the [spell][mSpell] into
     * @param name Name/Title of the [spell][mSpell]
     * @param magicSchool [Spell][mSpell]'s [magic school][com.polarlooptheory.was.model.types.mMagicSchool](must match existing magic school in the [scenario])
     * @param description [Spell][mSpell]'s description
     * @param castingTime [Spell][mSpell]'s casting time
     * @param components Components needed to cast the [spell][mSpell]
     * @param concentration Does [spell][mSpell] require concentration
     * @param duration [Spell][mSpell]'s duration
     * @param higherLevels Effect when casting on higher levels
     * @param level [Spell][mSpell]'s level
     * @param material Material required to cast the [spell][mSpell]
     * @param range [Spell][mSpell]'s range
     * @param ritual Can the [spell][mSpell] be casted as a ritual
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createSpell(
        scenario: mScenario,
        name: String,
        magicSchool: String,
        description: String? = null,
        castingTime: String? = null,
        components: String? = null,
        concentration: Boolean? = null,
        duration: String? = null,
        higherLevels: String? = null,
        level: Int? = null,
        material: String? = null,
        range: String? = null,
        ritual: Boolean? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/spell"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            json.put("magicSchool", magicSchool)
            if (description != null)
                json.put("description", description)
            if (castingTime != null)
                json.put("castingTime", castingTime)
            if (components != null)
                json.put("components", components)
            if (concentration != null)
                json.put("concentration", concentration)
            if (duration != null)
                json.put("duration", duration)
            if (higherLevels != null)
                json.put("higherLevels", higherLevels)
            if (level != null)
                json.put("level", level)
            if (material != null)
                json.put("material", material)
            if (range != null)
                json.put("range", range)
            if (ritual != null)
                json.put("ritual", ritual)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpSpell = mSpell(
                            name = name,
                            magicSchool = magicSchool,
                            custom = true)
                        if (description != null)
                            tmpSpell.description = description
                        if (castingTime != null)
                            tmpSpell.castingTime = castingTime
                        if (concentration != null)
                            tmpSpell.concentration = concentration
                        if (components != null)
                            tmpSpell.components = components
                        if (duration != null)
                            tmpSpell.duration = duration
                        if (higherLevels != null)
                            tmpSpell.higherLevels = higherLevels
                        if (level != null)
                            tmpSpell.level = level
                        if (material != null)
                            tmpSpell.material = material
                        if (range != null)
                            tmpSpell.range = range
                        if (ritual != null)
                            tmpSpell.ritual = ritual
                        if (visible != null)
                            tmpSpell.visible = visible
                        Scenario.loadedResources.spells[tmpSpell.name] = tmpSpell
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createSpell(
                                scenario,
                                name,
                                magicSchool,
                                description,
                                castingTime,
                                components,
                                concentration,
                                duration,
                                higherLevels,
                                level,
                                material,
                                range,
                                ritual,
                                visible
                            )
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
     * Deletes a [spell][mSpell] from the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] from which the [spell][mSpell] is deleted
     * @param name Name of the [spell][mSpell] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteSpell(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/spell/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        Scenario.loadedResources.spells.remove(name)
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteSpell(scenario, name)
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
     * Patch a [spell][mSpell] in the [scenario]
     *
     * It cannot change the name of the [spell][mSpell]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] in which the [spell][mSpell] will be patched
     * @param name Name of the [spell][mSpell] to patch
     * @param magicSchool [Spell][mSpell]'s magic school(must match existing [magic school][com.polarlooptheory.was.model.types.mMagicSchool] in the [scenario])
     * @param description [Spell][mSpell]'s description
     * @param castingTime [Spell][mSpell]'s casting time
     * @param components Components needed to cast the [spell][mSpell]
     * @param concentration Does [spell][mSpell] require concentration
     * @param duration [Spell][mSpell]'s duration
     * @param higherLevels Effect when casting on higher levels
     * @param level [Spell][mSpell]'s level
     * @param material Material required to cast the [spell][mSpell]
     * @param range [Spell][mSpell]'s range
     * @param ritual Can the [spell][mSpell] be casted as a ritual
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchSpell(
        scenario: mScenario,
        name: String,
        magicSchool: String,
        description: String? = null,
        castingTime: String? = null,
        components: String? = null,
        concentration: Boolean? = null,
        duration: String? = null,
        higherLevels: String? = null,
        level: Int? = null,
        material: String? = null,
        range: String? = null,
        ritual: Boolean? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/spell"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("magicSchool", magicSchool)
            if (description != null)
                json.put("description", description)
            if (castingTime != null)
                json.put("castingTime", castingTime)
            if (components != null)
                json.put("components", components)
            if (concentration != null)
                json.put("concentration", concentration)
            if (duration != null)
                json.put("duration", duration)
            if (higherLevels != null)
                json.put("higherLevels", higherLevels)
            json.put("level", level)
            if (material != null)
                json.put("material", material)
            if (range != null)
                json.put("range", range)
            if (ritual != null)
                json.put("ritual", ritual)
            if (description != null)
                json.put("description", description)
            if (visible != null)
                json.put("visible", visible)
            val client = HttpClient()
            runBlocking {
                val result = client.patch<HttpResponse>(endpoint){
                    header("Authorization","Bearer "+User.UserToken.access_token)
                    body = TextContent(json.toString(), ContentType.Application.Json)
                }
                when (result.status.value) {
                    200 -> {
                        val tmpSpell = mSpell(
                            name = name,
                            magicSchool = magicSchool,
                            custom = true)
                        if (description != null)
                            tmpSpell.description = description
                        if (castingTime != null)
                            tmpSpell.castingTime = castingTime
                        if (concentration != null)
                            tmpSpell.concentration = concentration
                        if (components != null)
                            tmpSpell.components = components
                        if (duration != null)
                            tmpSpell.duration = duration
                        if (higherLevels != null)
                            tmpSpell.higherLevels = higherLevels
                        if (level != null)
                            tmpSpell.level = level
                        if (material != null)
                            tmpSpell.material = material
                        if (range != null)
                            tmpSpell.range = range
                        if (ritual != null)
                            tmpSpell.ritual = ritual
                        if (visible != null)
                            tmpSpell.visible = visible
                        Scenario.loadedResources.spells[tmpSpell.name] = tmpSpell
                        success = true
                    }
                    else -> {
                        Settings.error_message = result.readText()
                    }
                }
                client.close()
            }
            /*To use when fixed
            runBlocking {
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpSpell = mSpell(
                            name = name,
                            magicSchool = magicSchool,
                            custom = true)
                        if (description != null)
                            tmpSpell.description = description
                        if (castingTime != null)
                            tmpSpell.castingTime = castingTime
                        if (concentration != null)
                            tmpSpell.concentration = concentration
                        if (components != null)
                            tmpSpell.components = components
                        if (duration != null)
                            tmpSpell.duration = duration
                        if (higherLevels != null)
                            tmpSpell.higherLevels = higherLevels
                        if (level != null)
                            tmpSpell.level = level
                        if (material != null)
                            tmpSpell.material = material
                        if (range != null)
                            tmpSpell.range = range
                        if (ritual != null)
                            tmpSpell.ritual = ritual
                        if (visible != null)
                            tmpSpell.visible = visible
                        Scenario.loadedResources.spells[tmpSpell.name] = tmpSpell
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchSpell(
                                scenario,
                                name,
                                magicSchool,
                                description,
                                castingTime,
                                components,
                                concentration,
                                duration,
                                higherLevels,
                                level,
                                material,
                                range,
                                ritual,
                                visible
                            )
                    }
                }
            }*/
            return success
        } else {
            Settings.error_message = "Player is not the GM of this scenario"
            return false
        }
    }
    //endregion
    //region Traits
    /**
     * Receives a list of the [traits][mTrait] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [traits][mTrait] matching the name
     * @return list of traits if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getTraits(scenario: mScenario, name: String? = null): List<mTrait>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/trait"
        var list: List<mTrait>? = null
        runBlocking {
            val (_, _, result) = endpoint.httpGet(
                if (name == null) {
                    null
                } else {
                    listOf("name" to name)
                }
            ).authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
            when (result) {
                is Result.Success -> {
                    val tmplist: MutableList<mTrait> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpTrait = mTrait(
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            visible = obj.getBoolean("visible"),
                            custom = !obj.getString("scenarioKey").equals("null")
                        )
                        if(!Scenario.loadedResources.traits.containsKey(tmpTrait.name))
                            Scenario.loadedResources.traits[tmpTrait.name] = tmpTrait
                        tmplist.add(tmpTrait)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getTraits(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [trait][mTrait] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] to put the [trait][mTrait] into
     * @param name Name/Title of the [trait][mTrait]
     * @param description [Trait][mTrait]'s description
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createTrait(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/trait"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpTrait = mTrait(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpTrait.description = description
                        if (visible != null)
                            tmpTrait.visible = visible
                        Scenario.loadedResources.traits[tmpTrait.name] = tmpTrait
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createTrait(scenario, name, description, visible)
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
     * Deletes a [trait][mTrait] from the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] from which the [trait][mTrait] is deleted
     * @param name Name of the [trait][mTrait] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteTrait(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/trait/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        Scenario.loadedResources.traits.remove(name)
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteTrait(scenario, name)
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
     * Patch a [trait][mTrait] in the [scenario]
     *
     * It cannot change the name of the [trait][mTrait]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] in which the [trait][mTrait] will be patched
     * @param name Name of the [trait][mTrait] to patch
     * @param description Description of the [trait][mTrait]
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchTrait(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/trait"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (visible != null)
                json.put("visible", visible)
            val client = HttpClient()
            runBlocking {
                val result = client.patch<HttpResponse>(endpoint){
                    header("Authorization","Bearer "+User.UserToken.access_token)
                    body = TextContent(json.toString(), ContentType.Application.Json)
                }
                when (result.status.value) {
                    200 -> {
                        val tmpTrait = mTrait(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpTrait.description = description
                        if (visible != null)
                            tmpTrait.visible = visible
                        Scenario.loadedResources.traits[tmpTrait.name] = tmpTrait
                        success = true
                    }
                    else -> {
                        Settings.error_message = result.readText()
                    }
                }
                client.close()
            }
            /*To use when fixed
            runBlocking {
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpTrait = mTrait(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpTrait.description = description
                        if (visible != null)
                            tmpTrait.visible = visible
                        Scenario.loadedResources.traits[tmpTrait.name] = tmpTrait
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchTrait(scenario, name, description, visible)
                    }
                }
            }*/
            return success
        } else {
            Settings.error_message = "Player is not the GM of this scenario"
            return false
        }
    }
    //endregion
}