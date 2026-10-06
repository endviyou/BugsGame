package com.endviyou.bugs.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.endviyou.bugs.R
import com.endviyou.bugs.database.PlayerScore

class RecordsAdapter(private val records: List<PlayerScore>) :
    RecyclerView.Adapter<RecordsAdapter.RecordViewHolder>() {

    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: TextView = view.findViewById(R.id.tvRank)
        val tvNickname: TextView = view.findViewById(R.id.tvNickname)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val tvScore: TextView = view.findViewById(R.id.tvScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        holder.tvRank.text = "${position + 1}"
        holder.tvNickname.text = record.nickname
        holder.tvDetails.text = "Сложность ${record.difficulty} · ${record.course}"
        holder.tvScore.text = "${record.score}"
    }

    override fun getItemCount(): Int = records.size
}