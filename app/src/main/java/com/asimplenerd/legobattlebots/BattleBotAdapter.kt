package com.asimplenerd.legobattlebots

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.view.menu.ListMenuItemView
import androidx.recyclerview.widget.RecyclerView

class BattleBotAdapter(private val mBattleBots : List<BattleBot>) : RecyclerView.Adapter<BattleBotAdapter.ViewHolder>(){



    inner class ViewHolder(listMenuItemView: View) : RecyclerView.ViewHolder(listMenuItemView) {
        val deviceString = listMenuItemView.findViewById<TextView>(R.id.battleBotTextField)
        val selectButton = listMenuItemView.findViewById<Button>(R.id.battleBotSelectBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val battleBotView = inflater.inflate(R.layout.battle_bot_display, parent, false)
        return ViewHolder(battleBotView)
    }

    override fun getItemCount(): Int {
        return mBattleBots.size
    }

    override fun onBindViewHolder(holder: BattleBotAdapter.ViewHolder, position: Int) {
        val battleBot = mBattleBots[position]
        val textView = holder.deviceString
        textView.text = "Battle Bot Id: ${battleBot.getID()}"
        textView.setTextColor(0x000FF7)
        val button = holder.selectButton
        button.setBackgroundColor(0x000FF7)
        button.text = "Select!"

        button.isEnabled = true
    }
}