package com.github.sewerina.meter_readings.ui.homes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.ReadingApp
import com.github.sewerina.meter_readings.database.HomeEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class HomesActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAddHomeFab: FloatingActionButton

    @Inject
    lateinit var mViewModel: HomesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homes)

        ReadingApp.sMainComponent!!.inject(this)

        val homeAdapter = HomeAdapter()
        mViewModel.homes.observe(this) { homeEntities -> homeAdapter.update(homeEntities) }

        mRecyclerView = findViewById<RecyclerView?>(R.id.recyclerHomes).apply {
            layoutManager = LinearLayoutManager(this@HomesActivity)
            adapter = homeAdapter
        }

        mAddHomeFab = findViewById(R.id.fab_addHome)
        mAddHomeFab.setOnClickListener(View.OnClickListener {
            NewHomeDialog.showDialog(supportFragmentManager)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class HomeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mAddressTv: TextView = itemView.findViewById(R.id.tv_address)
        private val mEditHomeIBtn: ImageButton = itemView.findViewById(R.id.iBtn_editHome)
        private val mDeleteHomeIBtn: ImageButton = itemView.findViewById(R.id.iBtn_deleteHome)
        private lateinit var mHomeEntity: HomeEntity
        fun bind(entity: HomeEntity) {
            mHomeEntity = entity
            mAddressTv.text = entity.address
        }

        init {
            mEditHomeIBtn.setOnClickListener {
                EditHomeDialog.showDialog(
                    supportFragmentManager,
                    mHomeEntity
                )
            }

            mDeleteHomeIBtn.setOnClickListener { v ->
                MaterialAlertDialogBuilder(v.context)
                    .setTitle(R.string.title_deleteHome)
                    .setMessage(R.string.delete_home_message)
                    .setPositiveButton(R.string.btn_delete) { dialog, which ->
                        mViewModel.deleteHome(
                            mHomeEntity
                        )
                    }
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show()
            }
        }
    }

    private inner class HomeAdapter : RecyclerView.Adapter<HomeHolder>() {
        private val mHomes: MutableList<HomeEntity> = ArrayList()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false)
            return HomeHolder(itemView)
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

        fun add(entity: HomeEntity) {
            mHomes.add(entity)
            notifyItemInserted(mHomes.size - 1)
        }
    }

    companion object {
        private const val TAG = "HomesActivity"
    }
}