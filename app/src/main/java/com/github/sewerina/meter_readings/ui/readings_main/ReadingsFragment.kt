package com.github.sewerina.meter_readings.ui.readings_main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.FormattedDate
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.github.sewerina.meter_readings.databinding.FragmentReadingsBinding
import com.github.sewerina.meter_readings.ui.selectHome.SelectHomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadingsFragment : Fragment() {
    private var _binding: FragmentReadingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mReadingsVM: ReadingsViewModel by activityViewModels()

    private val mSelectHomeVM: SelectHomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentReadingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoToHomes.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_readings_to_navigation_homes)
        }

        val readingAdapter = ReadingAdapter()
        binding.recyclerReadings.apply {
            layoutManager = GridLayoutManager(view.context, 2)
            adapter = readingAdapter
        }

        binding.fabAddReading.setOnClickListener {
            NewReadingDialog.showDialog(parentFragmentManager)
        }

        mReadingsVM.readings.observe(viewLifecycleOwner) { readingEntities ->
            readingAdapter.update(readingEntities)
        }

        mSelectHomeVM.currentHomePosition.observe(viewLifecycleOwner) {
            mReadingsVM.loadReadings(mSelectHomeVM.getCurrentHomeEntity())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ReadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
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
            BottomSheetReadingDialog.showDialog(parentFragmentManager, mReadingEntity)
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

    private inner class ReadingAdapter : RecyclerView.Adapter<ReadingHolder>() {
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

        fun update(entities: List<ReadingEntity>?) {
            mReadings.clear()
            mReadings.addAll(entities!!)
            notifyDataSetChanged()
        }

        fun add(entity: ReadingEntity) {
            mReadings.add(entity)
            notifyItemInserted(mReadings.size - 1)
        }
    }
}