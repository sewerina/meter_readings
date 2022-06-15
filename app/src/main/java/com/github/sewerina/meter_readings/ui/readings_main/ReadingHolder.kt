package com.github.sewerina.meter_readings.ui.readings_main

import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.FormattedDate
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.github.sewerina.meter_readings.ui.readings_main.BottomSheetReadingDialog.Companion.ARG_READING_ENTITY

class ReadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
    private val mDateTv: TextView
    private val mColdWaterTv: TextView
    private val mHotWaterTv: TextView
    private val mDrainWaterTv: TextView
    private val mElectricityTv: TextView
    private val mGasTv: TextView

    private lateinit var mReadingEntity: ReadingEntity
    fun bind(entity: ReadingEntity) {
        mReadingEntity = entity
        ReadingPreferences().setLayoutVisibility(itemView)
        mDateTv.text = FormattedDate(entity.date).text()
        mColdWaterTv.text = entity.coldWater.toString()
        mHotWaterTv.text = entity.hotWater.toString()
        mDrainWaterTv.text = entity.drainWater.toString()
        mElectricityTv.text = entity.electricity.toString()
        mGasTv.text = entity.gas.toString()
    }

    override fun onClick(v: View) {
        v.findNavController().navigate(
            R.id.action_navigation_readings_to_navigation_bottomSheetReading,
            bundleOf(ARG_READING_ENTITY to mReadingEntity)
        )
    }

    init {
        itemView.setOnClickListener(this)
        mDateTv = itemView.findViewById(R.id.tv_date)
        mColdWaterTv = itemView.findViewById(R.id.tv_coldWater)
        mHotWaterTv = itemView.findViewById(R.id.tv_hotWater)
        mDrainWaterTv = itemView.findViewById(R.id.tv_drainWater)
        mElectricityTv = itemView.findViewById(R.id.tv_electricity)
        mGasTv = itemView.findViewById(R.id.tv_gas)
    }
}