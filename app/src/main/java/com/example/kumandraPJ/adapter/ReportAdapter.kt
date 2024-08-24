package com.example.kumandraPJ.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kumandraPJ.R
import com.example.kumandraPJ.data.remote.response.Report
import com.example.kumandraPJ.databinding.ItemRvBinding
import com.example.kumandraPJ.ui.AddStoryActivity
import com.example.kumandraPJ.ui.DetailStoryActivity

class ReportAdapter(private val context: Context) : PagingDataAdapter<Report, ReportAdapter.ViewHolder>(DIFF_CALLBACK){

    class ViewHolder (private val binding: ItemRvBinding): RecyclerView.ViewHolder(binding.root) {
        val cardView : CardView = binding.cardView
        fun bind(data: Report, holder: ViewHolder){
            Glide.with(holder.itemView.context)
                .load(data.image_url)
                .into(holder.binding.ivGambarRv)

            holder.binding.tvFasil.text = data.nama_detail_facilities
            holder.binding.tvKelas.text = data.nama_classes
            holder.binding.tvTanggal.text = data.created_at
            holder.binding.tvStatus.text = data.nama_status
            holder.itemView.setOnClickListener{
                val intentDetail = Intent(holder.itemView.context, DetailStoryActivity::class.java)
             //   val intentAdd = Intent(holder.itemView.context, DetailStoryActivity::class.java)
                intentDetail.putExtra(DetailStoryActivity.STORY_DETAIL, data)
          //      intentAdd.putExtra(AddStoryActivity.REPORT, data)
                holder.itemView.context.startActivity(intentDetail)
         //    holder.itemView.context.startActivity(intentAdd)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        when(data?.id_status){
            "2" -> {
         //       holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status1))
                holder.itemView.findViewById<TextView>(R.id.tv_status).setBackgroundColor(ContextCompat.getColor(context, R.color.status1))
            }
            "3" -> {
           //     holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status2))
                holder.itemView.findViewById<TextView>(R.id.tv_status).setBackgroundColor(ContextCompat.getColor(context, R.color.status2))
            }
            "4" -> {
             //   holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status3))
                holder.itemView.findViewById<TextView>(R.id.tv_status).setBackgroundColor(ContextCompat.getColor(context, R.color.status3))
            }
            "5" -> {
             //   holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.status4))
                holder.itemView.findViewById<TextView>(R.id.tv_status).setBackgroundColor(ContextCompat.getColor(context, R.color.status4))
            }
        }
        if (data != null) {
            holder.bind(data, holder)
        }

    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Report>(){
            override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Report,
                newItem: Report
            ): Boolean {
                return oldItem.id_report == newItem.id_report
            }
        }
    }
}