package com.asimplenerd.legobattlebots

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class BattleBotAdapter(private val mBattleBots : List<BattleBot>) : RecyclerView.Adapter<BattleBotAdapter.ViewHolder>(){
    inner class ViewHolder(listMenuItemView: View) : RecyclerView.ViewHolder(listMenuItemView){
        val deviceString: TextView = listMenuItemView.findViewById(R.id.battleBotTextField)
        val selectButton: Button = listMenuItemView.findViewById(R.id.battleBotSelectBtn)
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
        textView.text = holder.itemView.context.getString(R.string.bot_id, battleBot.getID())
        val button = holder.selectButton
        button.text = holder.itemView.context.getText(R.string.select)
        button.setOnClickListener { v ->
            val fragActivity = v?.context as FragmentActivity
            val manager = fragActivity.supportFragmentManager
            for (frag in manager.fragments) {
                if (frag is ConnectionFragment) {
                    frag.selectBot(battleBot)
                }
            }
        }
        button.isEnabled = true
    }
}