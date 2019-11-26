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
import com.polarlooptheory.was.model.mScenario
import com.polarlooptheory.was.model.types.mCondition
import com.polarlooptheory.was.model.types.mDamageType
import com.polarlooptheory.was.model.types.mMagicSchool
import com.polarlooptheory.was.model.types.mWeaponProperty
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

/**
 * Object for handling types
 */
object Types {
    val endpoint = "${Settings.server_address}api/v1/scenario"
    //region Conditions
    /**
     * Receives a list of the [conditions][mCondition] in the [scenario]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [conditions][mCondition] matching the name
     * @return list of conditions if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getConditions(scenario: mScenario, name: String? = null): List<mCondition>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/condition"
        var list: List<mCondition>? = null
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
                    val tmplist: MutableList<mCondition> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpCondition = mCondition(
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            visible = obj.getBoolean("visible")
                        )
                        tmplist.add(tmpCondition)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getConditions(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [condition][mCondition] in the [scenario]
     * @param scenario [Scenario][mScenario] to put the [condition][mCondition] into
     * @param name Name/Title of the [condition][mCondition]
     * @param description [Condition][mCondition]'s description
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createCondition(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/condition"
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
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createCondition(scenario, name, description, visible)
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
     * Deletes a [condition][mCondition] from the [scenario]
     * @param scenario [Scenario][mScenario] from which the [condition][mCondition] is deleted
     * @param name Name of the [condition][mCondition] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteCondition(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/condition/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteCondition(scenario, name)
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
     * Patch a [condition][mCondition] in the [scenario]
     *
     * It cannot change the name of the [condition][mCondition]
     * @param scenario [Scenario][mScenario] in which the [condition][mCondition] will be patched
     * @param name Name of the [condition][mCondition] to patch
     * @param description Description of the [condition][mCondition]
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchCondition(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/condition"
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
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchCondition(scenario, name, description, visible)
                    }
                }
            }
            return success
        } else {
            Settings.error_message = "Player is not the GM of this scenario"
            return false
        }
    }
    //endregion
    //region DamageTypes
    /**
     * Receives a list of the [damage types][mDamageType] in the [scenario]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [damage types][mDamageType] matching the name
     * @return list of DamageTypes if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getDamageTypes(scenario: mScenario, name: String? = null): List<mDamageType>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/damageType"
        var list: List<mDamageType>? = null
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
                    val tmplist: MutableList<mDamageType> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpDamageType = mDamageType(
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            visible = obj.getBoolean("visible")
                        )
                        tmplist.add(tmpDamageType)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getDamageTypes(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [damage type][mDamageType] in the [scenario]
     * @param scenario [Scenario][mScenario] to put the [damage type][mDamageType] into
     * @param name Name/Title of the [damage type][mDamageType]
     * @param description [Damage type][mDamageType]'s description
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createDamageType(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/damageType"
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
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createDamageType(scenario, name, description, visible)
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
     * Deletes a [damage type][mDamageType] from the [scenario]
     * @param scenario [Scenario][mScenario] from which the [damage type][mDamageType] is deleted
     * @param name Name of the [damage type][mDamageType] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteDamageType(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/damageType/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteDamageType(scenario, name)
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
     * Patch a [damage type][mDamageType] in the [scenario]
     *
     * It cannot change the name of the [damage type][mDamageType]
     * @param scenario [Scenario][mScenario] in which the [damage type][mDamageType] will be patched
     * @param name Name of the [damage type][mDamageType] to patch
     * @param description Description of the [damage type][mDamageType]
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchDamageType(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/damageType"
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
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchDamageType(scenario, name, description, visible)
                    }
                }
            }
            return success
        } else {
            Settings.error_message = "Player is not the GM of this scenario"
            return false
        }
    }
    //endregion
    //region MagicSchools
    /**
     * Receives a list of the [magic schools][mMagicSchool] in the [scenario]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [magic schools][mMagicSchool] matching the name
     * @return list of magic schools if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getMagicSchools(scenario: mScenario, name: String? = null): List<mMagicSchool>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/magicSchool"
        var list: List<mMagicSchool>? = null
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
                    val tmplist: MutableList<mMagicSchool> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpMagicSchool = mMagicSchool(
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            visible = obj.getBoolean("visible")
                        )
                        tmplist.add(tmpMagicSchool)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getMagicSchools(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [magic school][mMagicSchool] in the [scenario]
     * @param scenario [Scenario][mScenario] to put the [magic school][mMagicSchool] into
     * @param name Name/Title of the [magic school][mMagicSchool]
     * @param description [MagicSchool][mMagicSchool]'s description
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createMagicSchool(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/magicSchool"
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
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createMagicSchool(scenario, name, description, visible)
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
     * Deletes a [magic school][mMagicSchool] from the [scenario]
     * @param scenario [Scenario][mScenario] from which the [magic school][mMagicSchool] is deleted
     * @param name Name of the [magic school][mMagicSchool] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteMagicSchool(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/magicSchool/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteMagicSchool(scenario, name)
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
     * Patch a [magic school][mMagicSchool] in the [scenario]
     *
     * It cannot change the name of the [magic school][mMagicSchool]
     * @param scenario [Scenario][mScenario] in which the [magic school][mMagicSchool] will be patched
     * @param name Name of the [magic school][mMagicSchool] to patch
     * @param description Description of the [magic school][mMagicSchool]
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchMagicSchool(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/magicSchool"
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
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchMagicSchool(scenario, name, description, visible)
                    }
                }
            }
            return success
        } else {
            Settings.error_message = "Player is not the GM of this scenario"
            return false
        }
    }
    //endregion
    //region WeaponProperties
    /**
     * Receives a list of the [weapon properties][mWeaponProperty] in the [scenario]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [weapon properties][mWeaponProperty] matching the name
     * @return list of weapon properties if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getWeaponProperties(
        scenario: mScenario,
        name: String? = null
    ): List<mWeaponProperty>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/weaponProperty"
        var list: List<mWeaponProperty>? = null
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
                    val tmplist: MutableList<mWeaponProperty> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpWeaponProperty = mWeaponProperty(
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            visible = obj.getBoolean("visible")
                        )
                        tmplist.add(tmpWeaponProperty)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getWeaponProperties(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [weapon property][mWeaponProperty] in the [scenario]
     * @param scenario [Scenario][mScenario] to put the [weapon property][mWeaponProperty] into
     * @param name Name/Title of the [weapon property][mWeaponProperty]
     * @param description [WeaponProperty][mWeaponProperty]'s description
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createWeaponProperty(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/weaponProperty"
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
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createWeaponProperty(scenario, name, description, visible)
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
     * Deletes a [weapon property][mWeaponProperty] from the [scenario]
     * @param scenario [Scenario][mScenario] from which the [weapon property][mWeaponProperty] is deleted
     * @param name Name of the [weapon property][mWeaponProperty] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteWeaponProperty(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/weaponProperty/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteWeaponProperty(scenario, name)
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
     * Patch a [weapon property][mWeaponProperty] in the [scenario]
     *
     * It cannot change the name of the [weapon property][mWeaponProperty]
     * @param scenario [Scenario][mScenario] in which the [weapon property][mWeaponProperty] will be patched
     * @param name Name of the [weapon property][mWeaponProperty] to patch
     * @param description Description of the [weapon property][mWeaponProperty]
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchWeaponProperty(
        scenario: mScenario,
        name: String,
        description: String? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/weaponProperty"
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
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchWeaponProperty(scenario, name, description, visible)
                    }
                }
            }
            return success
        } else {
            Settings.error_message = "Player is not the GM of this scenario"
            return false
        }
    }
    //endregion
}