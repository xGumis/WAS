package com.polarlooptheory.was.model

/**
 * Message's model class
 * @property content Message's content/text
 * @property whisperTarget Target character to which message is sent
 * @property sender Sender of the message
 * @property type Type of message
 */
class mMessage (
    var content: String = "",
    var whisperTarget: String = "",
    var sender: String = "",
    var type: Type = Type.OOC
){
    /**
     * Possible types of messages
     */
    enum class Type{
        CHARACTER, WHISPER, OOC, SYSTEM
    }
}