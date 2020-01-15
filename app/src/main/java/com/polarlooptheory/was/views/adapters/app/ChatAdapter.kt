package com.example.chat

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.polarlooptheory.was.R
import com.polarlooptheory.was.apiCalls.Scenario
import com.polarlooptheory.was.model.User
import com.polarlooptheory.was.model.mMessage

class ChatAdapter(val messageList: MutableList<mMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {


    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val sender : TextView
        val message :  TextView
        val background : Drawable
        val context : Context
        val frame : LinearLayout
        init{
            sender = view.findViewById(R.id.sender)
            message = view.findViewById(R.id.message)
            background = view.findViewById<LinearLayout>(R.id.cloudLayout).background
            context = view.context
            frame = (view as LinearLayout)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_cloud, parent, false) as View
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        if(messageList[position].type == mMessage.Type.SYSTEM)
            holder.sender.text = "SYSTEM"
        else holder.sender.text = messageList[position].sender
        holder.message.text = messageList[position].content
        val drawable = holder.background as GradientDrawable
        when(messageList[position].type){
            mMessage.Type.WHISPER->{
                drawable.setStroke(5, ContextCompat.getColor(holder.context,R.color.messagePrivateDark))
                drawable.setColor(ContextCompat.getColor(holder.context,R.color.messagePrivate))
                holder.sender.setTextColor(ContextCompat.getColor(holder.context,R.color.messagePrivateText))
                holder.message.setTextColor(ContextCompat.getColor(holder.context,R.color.messagePrivateText))
            }
            mMessage.Type.OOC->{
                if(messageList[position].sender == Scenario.connectedScenario.scenario.gameMaster){
                    drawable.setStroke(5, ContextCompat.getColor(holder.context,R.color.messageGMDark))
                    drawable.setColor(ContextCompat.getColor(holder.context,R.color.messageGM))
                    holder.sender.setTextColor(ContextCompat.getColor(holder.context,R.color.messageGMText))
                    holder.message.setTextColor(ContextCompat.getColor(holder.context,R.color.messageGMText))
                }
                else{
                    drawable.setStroke(5, ContextCompat.getColor(holder.context,R.color.messageOOCDark))
                    drawable.setColor(ContextCompat.getColor(holder.context,R.color.messageOOC))
                    holder.sender.setTextColor(ContextCompat.getColor(holder.context,R.color.messageOOCText))
                    holder.message.setTextColor(ContextCompat.getColor(holder.context,R.color.messageOOCText))
                }
            }
            mMessage.Type.SYSTEM->{
                drawable.setStroke(5, ContextCompat.getColor(holder.context,R.color.messageSystemDark))
                drawable.setColor(ContextCompat.getColor(holder.context,R.color.messageSystem))
                holder.sender.setTextColor(ContextCompat.getColor(holder.context,R.color.messageSystemText))
                holder.message.setTextColor(ContextCompat.getColor(holder.context,R.color.messageSystemText))
            }
            mMessage.Type.CHARACTER->{
                drawable.setStroke(5, ContextCompat.getColor(holder.context,R.color.messageCharacterDark))
                drawable.setColor(ContextCompat.getColor(holder.context,R.color.messageCharacter))
                holder.sender.setTextColor(ContextCompat.getColor(holder.context,R.color.messageCharacterText))
                holder.message.setTextColor(ContextCompat.getColor(holder.context,R.color.messageCharacterText))
            }
        }
        if(messageList[position].sender==User.username) holder.frame.gravity = Gravity.END
        else if(Scenario.connectedScenario.charactersList.containsKey(messageList[position].sender))
            if(Scenario.connectedScenario.charactersList[messageList[position].sender]?.owner == User.username)
                holder.frame.gravity = Gravity.END
        else  holder.frame.gravity = Gravity.START
        }
    override fun getItemCount() = messageList.size
}
