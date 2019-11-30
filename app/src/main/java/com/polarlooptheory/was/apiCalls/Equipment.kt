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
import com.polarlooptheory.was.model.equipment.*
import com.polarlooptheory.was.model.mScenario
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

/**
 * Object for handling equipment
 */
object Equipment {
    private const val endpoint = "${Settings.server_address}api/v1/scenario/"
    //region Armors
    /**
     * Receives a list of the [armors][mArmor] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [armors][mArmor] matching the name
     * @return list of armors if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getArmors(scenario: mScenario, name: String? = null): List<mArmor>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/armor"
        var list: List<mArmor>? = null
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
                    val tmplist: MutableList<mArmor> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpArmor = mArmor(
                            name = obj.getString("name"),
                            armorClass = mArmor.ArmorClass(
                                base = obj.getJSONObject("armorClass").getInt("base"),
                                dexBonus = obj.getJSONObject("armorClass").getBoolean("dexBonus"),
                                maxBonus = obj.getJSONObject("armorClass").getInt("maxBonus")
                            ),
                            cost = obj.getString("cost"),
                            stealthDisadvantage = obj.getBoolean("stealthDisadvantage"),
                            strMinimum = obj.getInt("strMinimum"),
                            weight = obj.getInt("weight"),
                            visible = obj.getBoolean("visible"),
                            custom = !obj.getString("scenarioKey").isNullOrEmpty()
                        )
                        if(!Scenario.loadedResources.armors.containsKey(tmpArmor.name))
                            Scenario.loadedResources.armors[tmpArmor.name] = tmpArmor
                        tmplist.add(tmpArmor)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getArmors(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [armor][mArmor] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] to put the [armor][mArmor] into
     * @param name Name/Title of the [armor][mArmor]
     * @param ac_base [Armor][mArmor]'s base [armor class][mArmor.ArmorClass]
     * @param ac_dexBonus Does [armor][mArmor] apply bonus from dexterity
     * @param ac_maxBonus Maximal possible bonus from dexterity
     * @param cost [Armor][mArmor]'s cost
     * @param stealthDisadvantage Does it give disadvantage in stealth
     * @param strMinimum Minimal required strength
     * @param weight [Armor][mArmor]'s weight
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createArmor(
        scenario: mScenario,
        name: String,
        ac_base: Int? = null,
        ac_dexBonus: Boolean? = null,
        ac_maxBonus: Int? = null,
        cost: String? = null,
        stealthDisadvantage: Boolean? = null,
        strMinimum: Int? = null,
        weight: Int? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/armor"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            val ac = JSONObject()
            if (ac_base != null)
                ac.put("base", ac_base)
            if (ac_dexBonus != null)
                ac.put("dexBonus", ac_dexBonus)
            if (ac_maxBonus != null)
                ac.put("maxBonus", ac_maxBonus)
            json.put("armorClass", ac)
            if (cost != null)
                json.put("cost", cost)
            if (stealthDisadvantage != null)
                json.put("stealthDisadvantage", stealthDisadvantage)
            if (strMinimum != null)
                json.put("strMinimum", strMinimum)
            if (weight != null)
                json.put("weight", weight)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpArmor = mArmor(
                            name = name,
                            custom = true,
                            armorClass = mArmor.ArmorClass(
                                base = ac_base ?: 0,
                                dexBonus = ac_dexBonus ?: false,
                                maxBonus = ac_maxBonus ?: 0
                            ))
                        if (cost != null)
                            tmpArmor.cost = cost
                        if (stealthDisadvantage != null)
                            tmpArmor.stealthDisadvantage = stealthDisadvantage
                        if (strMinimum != null)
                            tmpArmor.strMinimum = strMinimum
                        if (weight != null)
                            tmpArmor.weight = weight
                        if (visible != null)
                            tmpArmor.visible = visible
                        Scenario.loadedResources.armors[tmpArmor.name] = tmpArmor
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createArmor(
                                scenario,
                                name,
                                ac_base,
                                ac_dexBonus,
                                ac_maxBonus,
                                cost,
                                stealthDisadvantage,
                                strMinimum,
                                weight,
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
     * Deletes a [armor][mArmor] from the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] from which the [armor][mArmor] is deleted
     * @param name Name of the [armor][mArmor] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteArmor(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/armor/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        Scenario.loadedResources.armors.remove(name)
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteArmor(scenario, name)
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
     * Patch a [armor][mArmor] in the [scenario]
     *
     * It cannot change the name of the [armor][mArmor]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] in which the [armor][mArmor] will be patched
     * @param name Name of the [armor][mArmor] to patch
     * @param ac_base [Armor][mArmor]'s base [armor class][mArmor.ArmorClass]
     * @param ac_dexBonus Does [armor][mArmor] apply bonus from dexterity
     * @param ac_maxBonus Maximal possible bonus from dexterity
     * @param cost [Armor][mArmor]'s cost
     * @param stealthDisadvantage Does it give disadvantage in stealth
     * @param strMinimum Minimal required strength
     * @param weight [Armor][mArmor]'s weight
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchArmor(
        scenario: mScenario,
        name: String,
        ac_base: Int? = null,
        ac_dexBonus: Boolean? = null,
        ac_maxBonus: Int? = null,
        cost: String? = null,
        stealthDisadvantage: Boolean? = null,
        strMinimum: Int? = null,
        weight: Int? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/armor"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            val ac = JSONObject()
            if (ac_base != null)
                ac.put("base", ac_base)
            if (ac_dexBonus != null)
                ac.put("dexBonus", ac_dexBonus)
            if (ac_maxBonus != null)
                ac.put("maxBonus", ac_maxBonus)
            json.put("armorClass", ac)
            if (cost != null)
                json.put("cost", cost)
            if (stealthDisadvantage != null)
                json.put("stealthDisadvantage", stealthDisadvantage)
            if (strMinimum != null)
                json.put("strMinimum", strMinimum)
            json.put("weight", weight)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpArmor = mArmor(
                            name = name,
                            custom = true,
                            armorClass = mArmor.ArmorClass(
                                base = ac_base ?: 0,
                                dexBonus = ac_dexBonus ?: false,
                                maxBonus = ac_maxBonus ?: 0
                            ))
                        if (cost != null)
                            tmpArmor.cost = cost
                        if (stealthDisadvantage != null)
                            tmpArmor.stealthDisadvantage = stealthDisadvantage
                        if (strMinimum != null)
                            tmpArmor.strMinimum = strMinimum
                        if (weight != null)
                            tmpArmor.weight = weight
                        if (visible != null)
                            tmpArmor.visible = visible
                        Scenario.loadedResources.armors[tmpArmor.name] = tmpArmor
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchArmor(
                                scenario,
                                name,
                                ac_base,
                                ac_dexBonus,
                                ac_maxBonus,
                                cost,
                                stealthDisadvantage,
                                strMinimum,
                                weight,
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
    //endregion
    //region Gears
    /**
     * Receives a list of the [gear][mGear] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [gear][mGear] matching the name
     * @return list of gear if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getGears(scenario: mScenario, name: String? = null): List<mGear>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/gear"
        var list: List<mGear>? = null
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
                    val tmplist: MutableList<mGear> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpGear = mGear(
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            cost = obj.getString("cost"),
                            weight = obj.getInt("weight"),
                            visible = obj.getBoolean("visible"),
                            custom = !obj.getString("scenarioKey").isNullOrEmpty()
                        )
                        if(!Scenario.loadedResources.gear.containsKey(tmpGear.name))
                            Scenario.loadedResources.gear[tmpGear.name] = tmpGear
                        tmplist.add(tmpGear)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getGears(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [gear][mGear] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] to put the [gear][mGear] into
     * @param name Name/Title of the [gear][mGear]
     * @param description [Gear][mGear]'s description
     * @param cost [Gear][mGear]'s cost
     * @param weight [Gear][mGear]'s weight
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createGear(
        scenario: mScenario,
        name: String,
        description: String? = null,
        cost: String? = null,
        weight: Int? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/gear"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (cost != null)
                json.put("cost", cost)
            if (weight != null)
                json.put("weight", weight)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpGear = mGear(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpGear.description = description
                        if (cost != null)
                            tmpGear.cost = cost
                        if (weight != null)
                            tmpGear.weight = weight
                        if (visible != null)
                            tmpGear.visible = visible
                        Scenario.loadedResources.gear[tmpGear.name] = tmpGear
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createGear(scenario, name, description, cost, weight, visible)
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
     * Deletes a [gear][mGear] from the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] from which the [gear][mGear] is deleted
     * @param name Name of the [gear][mGear] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteGear(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/gear/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        Scenario.loadedResources.gear.remove(name)
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteGear(scenario, name)
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
     * Patch a [gear][mGear] in the [scenario]
     *
     * It cannot change the name of the [gear][mGear]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] in which the [gear][mGear] will be patched
     * @param name Name of the [gear][mGear] to patch
     * @param description Description of the [gear][mGear]
     * @param cost [Gear][mGear]'s cost
     * @param weight [Gear][mGear]'s weight
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchGear(
        scenario: mScenario,
        name: String,
        description: String? = null,
        cost: String? = null,
        weight: Int? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/gear"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (cost != null)
                json.put("cost", cost)
            if (weight != null)
                json.put("weight", weight)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpGear = mGear(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpGear.description = description
                        if (cost != null)
                            tmpGear.cost = cost
                        if (weight != null)
                            tmpGear.weight = weight
                        if (visible != null)
                            tmpGear.visible = visible
                        Scenario.loadedResources.gear[tmpGear.name] = tmpGear
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchGear(scenario, name, description, cost, weight, visible)
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
    //region Tools
    /**
     * Receives a list of the [tools][mTool] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [tools][mTool] matching the name
     * @return list of tools if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getTools(scenario: mScenario, name: String? = null): List<mTool>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/tool"
        var list: List<mTool>? = null
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
                    val tmplist: MutableList<mTool> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpTool = mTool(
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            category = obj.getString("category"),
                            cost = obj.getString("cost"),
                            weight = obj.getInt("weight"),
                            visible = obj.getBoolean("visible"),
                            custom = !obj.getString("scenarioKey").isNullOrEmpty()
                        )
                        if(!Scenario.loadedResources.tools.containsKey(tmpTool.name))
                            Scenario.loadedResources.tools[tmpTool.name] = tmpTool
                        tmplist.add(tmpTool)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getTools(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [tool][mTool] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] to put the [tool][mTool] into
     * @param name Name/Title of the [tool][mTool]
     * @param description [Tool][mTool]'s description
     * @param category [Tool][mTool]'s category
     * @param cost [Tool][mTool]'s cost
     * @param weight [Tool][mTool]'s weight
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createTool(
        scenario: mScenario,
        name: String,
        description: String? = null,
        category: String? = null,
        cost: String? = null,
        weight: Int? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/tool"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (category != null)
                json.put("category", category)
            if (cost != null)
                json.put("cost", cost)
            if (weight != null)
                json.put("weight", weight)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpTool = mTool(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpTool.description = description
                        if (category != null)
                            tmpTool.category = category
                        if (cost != null)
                            tmpTool.cost = cost
                        if (weight != null)
                            tmpTool.weight = weight
                        if (visible != null)
                            tmpTool.visible = visible
                        Scenario.loadedResources.tools[tmpTool.name] = tmpTool
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createTool(scenario, name, description, category, cost, weight, visible)
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
     * Deletes a [tool][mTool] from the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] from which the [tool][mTool] is deleted
     * @param name Name of the [tool][mTool] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteTool(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/tool/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        Scenario.loadedResources.tools.remove(name)
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteTool(scenario, name)
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
     * Patch a [tool][mTool] in the [scenario]
     *
     * It cannot change the name of the [tool][mTool]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] in which the [tool][mTool] will be patched
     * @param name Name of the [tool][mTool] to patch
     * @param description Description of the [tool][mTool]
     * @param category [Tool][mTool]'s category
     * @param cost [Tool][mTool]'s cost
     * @param weight [Tool][mTool]'s weight
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchTool(
        scenario: mScenario,
        name: String,
        description: String? = null,
        category: String? = null,
        cost: String? = null,
        weight: Int? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/tool"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (category != null)
                json.put("category", category)
            if (cost != null)
                json.put("cost", cost)
            if (weight != null)
                json.put("weight", weight)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpTool = mTool(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpTool.description = description
                        if (category != null)
                            tmpTool.category = category
                        if (cost != null)
                            tmpTool.cost = cost
                        if (weight != null)
                            tmpTool.weight = weight
                        if (visible != null)
                            tmpTool.visible = visible
                        Scenario.loadedResources.tools[tmpTool.name] = tmpTool
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchTool(scenario, name, description, category, cost, weight, visible)
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
    //region Vehicles
    /**
     * Receives a list of the [vehicles][mVehicle] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [vehicles][mVehicle] matching the name
     * @return list of vehicles if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getVehicles(scenario: mScenario, name: String? = null): List<mVehicle>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/vehicle"
        var list: List<mVehicle>? = null
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
                    val tmplist: MutableList<mVehicle> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpVehicle = mVehicle(
                            name = obj.getString("name"),
                            description = obj.getString("description"),
                            cost = obj.getString("cost"),
                            weight = obj.getInt("weight"),
                            visible = obj.getBoolean("visible"),
                            custom = !obj.getString("scenarioKey").isNullOrEmpty()
                        )
                        if(!Scenario.loadedResources.vehicles.containsKey(tmpVehicle.name))
                            Scenario.loadedResources.vehicles[tmpVehicle.name] = tmpVehicle
                        tmplist.add(tmpVehicle)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getVehicles(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [vehicle][mVehicle] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] to put the [vehicle][mVehicle] into
     * @param name Name/Title of the [vehicle][mVehicle]
     * @param description [Vehicle][mVehicle]'s description
     * @param cost [Vehicle][mVehicle]'s cost
     * @param weight [Vehicle][mVehicle]'s weight
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createVehicle(
        scenario: mScenario,
        name: String,
        description: String? = null,
        cost: String? = null,
        weight: Int? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/vehicle"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (cost != null)
                json.put("cost", cost)
            if (weight != null)
                json.put("weight", weight)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpVehicle = mVehicle(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpVehicle.description = description
                        if (cost != null)
                            tmpVehicle.cost = cost
                        if (weight != null)
                            tmpVehicle.weight = weight
                        if (visible != null)
                            tmpVehicle.visible = visible
                        Scenario.loadedResources.vehicles[tmpVehicle.name] = tmpVehicle
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createVehicle(scenario, name, description, cost, weight, visible)
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
     * Deletes a [vehicle][mVehicle] from the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] from which the [vehicle][mVehicle] is deleted
     * @param name Name of the [vehicle][mVehicle] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteVehicle(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/vehicle/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        Scenario.loadedResources.vehicles.remove(name)
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteVehicle(scenario, name)
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
     * Patch a [vehicle][mVehicle] in the [scenario]
     *
     * It cannot change the name of the [vehicle][mVehicle]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] in which the [vehicle][mVehicle] will be patched
     * @param name Name of the [vehicle][mVehicle] to patch
     * @param description Description of the [vehicle][mVehicle]
     * @param cost [Vehicle][mVehicle]'s cost
     * @param weight [Vehicle][mVehicle]'s weight
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchVehicle(
        scenario: mScenario,
        name: String,
        description: String? = null,
        cost: String? = null,
        weight: Int? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/vehicle"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            if (description != null)
                json.put("description", description)
            if (cost != null)
                json.put("cost", cost)
            if (weight != null)
                json.put("weight", weight)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpVehicle = mVehicle(
                            name = name,
                            custom = true)
                        if (description != null)
                            tmpVehicle.description = description
                        if (cost != null)
                            tmpVehicle.cost = cost
                        if (weight != null)
                            tmpVehicle.weight = weight
                        if (visible != null)
                            tmpVehicle.visible = visible
                        Scenario.loadedResources.vehicles[tmpVehicle.name] = tmpVehicle
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchVehicle(scenario, name, description, cost, weight, visible)
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
    //region Weapons

    /**
     * Receives a list of the [weapons][mWeapon] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario Chosen [scenario][mScenario]
     * @param name Search for [weapons][mWeapon] matching the name
     * @return list of weapons if ended with success or null if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun getWeapons(scenario: mScenario, name: String? = null): List<mWeapon>? {
        val endpoint = "$endpoint/${scenario.scenarioKey}/weapon"
        var list: List<mWeapon>? = null
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
                    val tmplist: MutableList<mWeapon> = mutableListOf()
                    val json = JSONArray(result.value)
                    for (i in 0 until json.length()) {
                        val obj = json.getJSONObject(i)
                        val tmpWeapon = mWeapon(
                            name = obj.getString("name"),
                            category = obj.getString("category"),
                            cost = obj.getString("cost"),
                            damageBonus = obj.getInt("damageBonus"),
                            damageDice = obj.getString("damageDice"),
                            damageType = obj.getString("damageType"),
                            longRange = obj.getInt("longRange"),
                            longThrowRange = obj.getInt("longThrowRange"),
                            normalRange = obj.getInt("normalRange"),
                            normalThrowRange = obj.getInt("normalThrowRange"),
                            properties = List<String>(obj.getJSONArray("properties").length()) {
                                obj.getJSONArray(
                                    "properties"
                                ).getString(it)
                            },
                            weaponRange = obj.getString("weaponRange"),
                            weight = obj.getInt("weight"),
                            visible = obj.getBoolean("visible"),
                            custom = !obj.getString("scenarioKey").isNullOrEmpty()
                        )
                        if(!Scenario.loadedResources.weapons.containsKey(tmpWeapon.name))
                            Scenario.loadedResources.weapons[tmpWeapon.name] = tmpWeapon
                        tmplist.add(tmpWeapon)
                    }
                    list = tmplist
                }
                is Result.Failure -> {
                    if (ApiErrorHandling.handleError(result.error))
                        getWeapons(scenario, name)
                }
            }
        }
        return list
    }

    /**
     * Creates a [weapon][mWeapon] in the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] to put the [weapon][mWeapon] into
     * @param name Name/Title of the [weapon][mWeapon]
     * @param damageType [Weapon][mWeapon]'s [damage type][com.polarlooptheory.was.model.types.mDamageType] (must match existing damage type in the [scenario])
     * @param category [Weapon][mWeapon]'s category
     * @param cost [Weapon][mWeapon]'s cost
     * @param damageBonus [Weapon][mWeapon]'s damage bonus
     * @param damageDice [Weapon][mWeapon]'s damage dice
     * @param longRange [Weapon][mWeapon]'s extended shooting range
     * @param longThrowRange [Weapon][mWeapon]'s extended throw range
     * @param normalRange [Weapon][mWeapon]'s shooting range
     * @param normalThrowRange [Weapon][mWeapon]'s throw range
     * @param properties [Weapon][mWeapon]'s list of properties
     * @param weaponRange [Weapon][mWeapon]'s reach
     * @param weight [Weapon][mWeapon]'s weight
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun createWeapon(
        scenario: mScenario,
        name: String,
        damageType: String,
        category: String? = null,
        cost: String? = null,
        damageBonus: Int? = null,
        damageDice: String? = null,
        longRange: Int? = null,
        longThrowRange: Int? = null,
        normalRange: Int? = null,
        normalThrowRange: Int? = null,
        properties: List<String>? = null,
        weaponRange: String? = null,
        weight: Int? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/weapon"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            json.put("damageType", damageType)
            if (category != null)
                json.put("category", category)
            if (cost != null)
                json.put("cost", cost)
            if (damageBonus != null)
                json.put("damageBonus", damageBonus)
            if (damageDice != null)
                json.put("damageDice", damageDice)
            if (longRange != null)
                json.put("longRange", longRange)
            if (longThrowRange != null)
                json.put("longThrowRange", longThrowRange)
            if (normalRange != null)
                json.put("normalRange", normalRange)
            if (normalThrowRange != null)
                json.put("normalThrowRange", normalThrowRange)
            val props = JSONArray()
            properties?.forEach { props.put(it) }
            json.put("properties", props)
            if (weaponRange != null)
                json.put("weaponRange", weaponRange)
            if (weight != null)
                json.put("weight", weight)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPost().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpWeapon = mWeapon(
                            name = name,
                            custom = true)

                        tmpWeapon.damageType = damageType
                        if (category != null)
                            tmpWeapon.category = category
                        if (damageBonus != null)
                            tmpWeapon.damageBonus = damageBonus
                        if (damageDice != null)
                            tmpWeapon.damageDice = damageDice
                        if (longRange != null)
                            tmpWeapon.longRange = longRange
                        if (longThrowRange != null)
                            tmpWeapon.longThrowRange = longThrowRange
                        if (normalRange != null)
                            tmpWeapon.normalRange = normalRange
                        if (normalThrowRange != null)
                            tmpWeapon.normalThrowRange = normalThrowRange
                        if (properties != null)
                            tmpWeapon.properties = properties
                        if (weaponRange != null)
                            tmpWeapon.weaponRange = weaponRange
                        if (cost != null)
                            tmpWeapon.cost = cost
                        if (weight != null)
                            tmpWeapon.weight = weight
                        if (visible != null)
                            tmpWeapon.visible = visible
                        Scenario.loadedResources.weapons[tmpWeapon.name] = tmpWeapon
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            createWeapon(
                                scenario,
                                name,
                                damageType,
                                category,
                                cost,
                                damageBonus,
                                damageDice,
                                longRange,
                                longThrowRange,
                                normalRange,
                                normalThrowRange,
                                properties,
                                weaponRange,
                                weight,
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
     * Deletes a [weapon][mWeapon] from the [scenario]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] from which the [weapon][mWeapon] is deleted
     * @param name Name of the [weapon][mWeapon] to delete
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun deleteWeapon(scenario: mScenario, name: String): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/weapon/$name"
            var success = false
            runBlocking {
                val (_, _, result) = endpoint.httpDelete().authentication().bearer(User.UserToken.access_token).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        Scenario.loadedResources.weapons.remove(name)
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            deleteWeapon(scenario, name)
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
     * Patch a [weapon][mWeapon] in the [scenario]
     *
     * It cannot change the name of the [weapon][mWeapon]
     *
     * Updates [loaded resources][Scenario.loadedResources]
     * @param scenario [Scenario][mScenario] in which the [weapon][mWeapon] will be patched
     * @param name Name of the [weapon][mWeapon] to patch
     * @param damageDice Damage dice of the [weapon][mWeapon]
     * @param category [Weapon][mWeapon]'s category
     * @param cost [Weapon][mWeapon]'s cost
     * @param weight [Weapon][mWeapon]'s weight
     * @param visible Visibility(is it visible for all players)
     * @return true if ended with success or false if error occurred(see [ErrorHandling][ApiErrorHandling.handleError])
     */
    suspend fun patchWeapon(
        scenario: mScenario,
        name: String,
        damageType: String,
        category: String? = null,
        cost: String? = null,
        damageBonus: Int? = null,
        damageDice: String? = null,
        longRange: Int? = null,
        longThrowRange: Int? = null,
        normalRange: Int? = null,
        normalThrowRange: Int? = null,
        properties: List<String>? = null,
        weaponRange: String? = null,
        weight: Int? = null,
        visible: Boolean? = null
    ): Boolean {
        if (Scenario.GM.isGM(scenario)) {
            val endpoint = "$endpoint/${scenario.scenarioKey}/weapon"
            var success = false
            if (name == "") {
                Settings.error_message = "Name cannot be empty"
                return false
            }
            val json = JSONObject()
            json.put("name", name)
            json.put("damageType", damageType)
            if (category != null)
                json.put("category", category)
            if (cost != null)
                json.put("cost", cost)
            if (damageBonus != null)
                json.put("damageBonus", damageBonus)
            if (damageDice != null)
                json.put("damageDice", damageDice)
            if (longRange != null)
                json.put("longRange", longRange)
            if (longThrowRange != null)
                json.put("longThrowRange", longThrowRange)
            if (normalRange != null)
                json.put("normalRange", normalRange)
            if (normalThrowRange != null)
                json.put("normalThrowRange", normalThrowRange)
            val props = JSONArray()
            properties?.forEach { props.put(it) }
            json.put("properties", props)
            if (weaponRange != null)
                json.put("weaponRange", weaponRange)
            json.put("weight", weight)
            if (visible != null)
                json.put("visible", visible)
            runBlocking {
                val (_, _, result) = endpoint.httpPatch().jsonBody(json.toString()).authentication().bearer(
                    User.UserToken.access_token
                ).awaitStringResponseResult()
                when (result) {
                    is Result.Success -> {
                        val tmpWeapon = mWeapon(
                            name = name,
                            custom = true)

                        tmpWeapon.damageType = damageType
                        if (category != null)
                            tmpWeapon.category = category
                        if (damageBonus != null)
                            tmpWeapon.damageBonus = damageBonus
                        if (damageDice != null)
                            tmpWeapon.damageDice = damageDice
                        if (longRange != null)
                            tmpWeapon.longRange = longRange
                        if (longThrowRange != null)
                            tmpWeapon.longThrowRange = longThrowRange
                        if (normalRange != null)
                            tmpWeapon.normalRange = normalRange
                        if (normalThrowRange != null)
                            tmpWeapon.normalThrowRange = normalThrowRange
                        if (properties != null)
                            tmpWeapon.properties = properties
                        if (weaponRange != null)
                            tmpWeapon.weaponRange = weaponRange
                        if (cost != null)
                            tmpWeapon.cost = cost
                        if (weight != null)
                            tmpWeapon.weight = weight
                        if (visible != null)
                            tmpWeapon.visible = visible
                        Scenario.loadedResources.weapons[tmpWeapon.name] = tmpWeapon
                        success = true
                    }
                    is Result.Failure -> {
                        if (ApiErrorHandling.handleError(result.error))
                            patchWeapon(
                                scenario,
                                name,
                                damageType,
                                category,
                                cost,
                                damageBonus,
                                damageDice,
                                longRange,
                                longThrowRange,
                                normalRange,
                                normalThrowRange,
                                properties,
                                weaponRange,
                                weight
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
    //endregion
}