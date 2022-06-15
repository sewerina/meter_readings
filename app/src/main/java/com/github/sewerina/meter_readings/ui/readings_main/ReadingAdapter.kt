package com.github.sewerina.meter_readings.ui.readings_main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.database.ReadingEntity

class ReadingAdapter : RecyclerView.Adapter<ReadingHolder>() {
    private val mReadings: MutableList<ReadingEntity> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.reading_list_item, parent, false)
        return ReadingHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReadingHolder, position: Int) {
        holder.bind(mReadings[position])
    }

    override fun getItemCount(): Int {
        return mReadings.size
    }

    fun update(entities: List<ReadingEntity>) {
        mReadings.clear()
        mReadings.addAll(entities)
        notifyDataSetChanged()
    }
}