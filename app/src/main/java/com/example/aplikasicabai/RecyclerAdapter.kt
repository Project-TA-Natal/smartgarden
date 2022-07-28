package com.example.aplikasicabai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    private var titles = arrayOf("Smart Garden","Smart Garden","Smart Garden","Smart Garden")
    private var details =  arrayOf("Penyiraman telah selesai dilakukan","Suhu 40 berada di atas ambang batas ",
        "Nilai pH tanah berada di bawah ambang batas","Kipas sedang menyala")
    private var images = intArrayOf(R.drawable.notif_selesai,R.drawable.notif_warning,R.drawable.notif_warning,
        R.drawable.notif_berhasil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false))

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.itemTitle.text   = titles[position]
        holder.itemDetail.text  = details[position]
        holder.itemGambar.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return titles.size
    }
    inner class ViewHolder(itemView : View) :RecyclerView.ViewHolder(itemView){
        var itemGambar  : ImageView
        var itemTitle   : TextView
        var itemDetail  : TextView

        init {
            itemGambar = itemView.findViewById(R.id.item_gambar)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)
        }
    }

}