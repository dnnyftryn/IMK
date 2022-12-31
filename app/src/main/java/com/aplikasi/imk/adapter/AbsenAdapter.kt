package com.aplikasi.firebaserealtimedatabse.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.imk.R
import com.aplikasi.imk.model.Absen

class AbsenAdapter(private val empList: ArrayList<Absen>) :
    RecyclerView.Adapter<AbsenAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.emp_list_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = empList[position]
        holder.tvNama.text = currentEmp.nama
        holder.tvNim.text = currentEmp.nim
        holder.tvProdi.text = currentEmp.prodi
        holder.tvFaku.text = currentEmp.fakultas
        holder.btnDelete.setOnClickListener {
            mListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return empList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val tvNama : TextView = itemView.findViewById(R.id.nama)
        val tvNim : TextView = itemView.findViewById(R.id.nim)
        val tvProdi : TextView = itemView.findViewById(R.id.prodi)
        val tvFaku : TextView = itemView.findViewById(R.id.fakultas)
        val btnDelete : Button = itemView.findViewById(R.id.btnUpdate)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }

    }

}