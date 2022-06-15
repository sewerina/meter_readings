package com.github.sewerina.meter_readings.ui.homes

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.ui.homes.EditHomeDialog.Companion.ARG_HOME_ENTITY
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeHolder(itemView: View, mViewModel: HomesViewModel) : RecyclerView.ViewHolder(itemView) {
    private val mAddressTv: TextView = itemView.findViewById(R.id.tv_address)
    private val mEditHomeIBtn: ImageButton = itemView.findViewById(R.id.iBtn_editHome)
    private val mDeleteHomeIBtn: ImageButton = itemView.findViewById(R.id.iBtn_deleteHome)
    private lateinit var mHomeEntity: HomeEntity
    fun bind(entity: HomeEntity) {
        mHomeEntity = entity
        mAddressTv.text = entity.address
    }

    init {
        mEditHomeIBtn.setOnClickListener { v ->
            v.findNavController().navigate(
                R.id.action_navigation_homes_to_navigation_editHome,
                bundleOf(ARG_HOME_ENTITY to mHomeEntity)
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