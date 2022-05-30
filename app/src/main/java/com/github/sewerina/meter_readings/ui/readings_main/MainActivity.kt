package com.github.sewerina.meter_readings.ui.readings_main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.FormattedDate
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.ReadingApp
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.database.ReadingEntity
import com.github.sewerina.meter_readings.ui.backup_copying.BackupCopyingActivity
import com.github.sewerina.meter_readings.ui.chart.ChartActivity
import com.github.sewerina.meter_readings.ui.homes.HomesActivity
import com.github.sewerina.meter_readings.ui.report.ReportActivity
import com.github.sewerina.meter_readings.ui.settings.SettingsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var mSpinner: Spinner
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAddReadingFab: FloatingActionButton
    private lateinit var mSpinnerAdapter: ArrayAdapter<String>
    private lateinit var mReadingAdapter: ReadingAdapter

    @Inject
    lateinit var mViewModel: MainViewModel

    private val mOnItemSelectedListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                mViewModel.changeCurrentHome(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ReadingApp.sMainComponent?.inject(this)

        mSpinner = findViewById(R.id.spinner_home)
        mSpinnerAdapter =
            ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, ArrayList())
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mSpinner.apply {
            adapter = mSpinnerAdapter
            onItemSelectedListener = mOnItemSelectedListener
        }

        mReadingAdapter = ReadingAdapter()
        mRecyclerView = findViewById<RecyclerView>(R.id.recyclerReadings).apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = mReadingAdapter
        }

        mAddReadingFab = findViewById(R.id.fab_addReading)
        mAddReadingFab.setOnClickListener {
            NewReadingDialog.showDialog(supportFragmentManager)
        }

        if (savedInstanceState != null) {
            val homeEntity = savedInstanceState.getSerializable(CURRENT_HOME_ENTITY) as HomeEntity
            mViewModel.restore(homeEntity)
        }

        mViewModel.readings.observe(this) { readingEntities ->
            mReadingAdapter.update(readingEntities)
        }

        mViewModel.state.observe(this) { state ->
            mSpinnerAdapter.clear()
            val homeEntityList = state!!.homeEntityList
            for (homeEntity in homeEntityList) {
                mSpinnerAdapter.add(homeEntity.address)
            }
            mSpinnerAdapter.notifyDataSetChanged()
            mSpinner.setSelection(state.currentHomePosition)
        }

//        mViewModel.firstLoad();
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (mViewModel.state.value != null) {
            outState.putSerializable(
                CURRENT_HOME_ENTITY,
                mViewModel.state.value!!.currentHomeEntity
            )
        }
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mViewModel.load()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_homes) {
            startActivity(Intent(this, HomesActivity::class.java))
            return true
        }
        if (id == R.id.action_chart) {
            startActivity(
                mViewModel.state.value!!.currentHomeEntity?.let {
                    ChartActivity.newIntent(this, it)
                }
            )
            return true
        }
        if (id == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        if (id == R.id.action_backupCopying) {
            startActivity(Intent(this, BackupCopyingActivity::class.java))
            return true
        }
        if (id == R.id.action_reports) {
            startActivity(
                mViewModel.state.value!!.currentHomeEntity?.let {
                    ReportActivity.newIntent(this, it)
                }
            )
            return true
        }
        return super.onOptionsItemSelected(item)
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
            Log.d(TAG, "onClick: ")
            BottomSheetReadingDialog.showDialog(supportFragmentManager, mReadingEntity)
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

    companion object {
        const val CURRENT_HOME_ENTITY = "currentHomeEntity"
        private const val TAG = "MainActivity"
    }
}