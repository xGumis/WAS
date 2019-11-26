package com.polarlooptheory.was.model

/**
 * Scenario's model class
 * @property scenarioKey Key of a scenario
 * @property name Name of a scenario
 * @property gameMaster Name of a scenario's game master
 * @property offlinePlayers List of offline players in scenario
 * @property onlinePlayers List of online players in scenario
 */
class mScenario (
    var scenarioKey:String = "",
    var name:String = "",
    var gameMaster:String = "",
    var offlinePlayers:List<String> = listOf(),
    var onlinePlayers:List<String> = listOf()
)