package com.github.sewerina.meter_readings.ui.homes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.database.HomeEntity

class HomeAdapter(private val mViewModel: HomesViewModel) : RecyclerView.Adapter<HomeHolder>() {
    private val mHomes: MutableList<HomeEntity> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false)
        return HomeHolder(itemView, mViewModel)
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        holder.bind(mHomes[position])
    }

    override fun getItemCount(): Int {
        return mHomes.size
    }

    fun update(entities: List<HomeEntity>?) {
        mHomes.clear()
        mHomes.addAll(entities!!)
        notifyDataSetChanged()
    }
}