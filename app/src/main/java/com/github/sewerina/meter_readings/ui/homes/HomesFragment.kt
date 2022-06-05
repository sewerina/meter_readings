package com.github.sewerina.meter_readings.ui.homes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sewerina.meter_readings.R
import com.github.sewerina.meter_readings.ReadingApp
import com.github.sewerina.meter_readings.database.HomeEntity
import com.github.sewerina.meter_readings.databinding.FragmentHomesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class HomesFragment : Fragment() {
    private var _binding: FragmentHomesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var mViewModel: HomesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ReadingApp.sMainComponent?.inject(this)

        val homeAdapter = HomeAdapter()
        binding.recyclerHomes.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = homeAdapter
        }

        binding.fabAddHome.setOnClickListener {
            NewHomeDialog.showDialog(parentFragmentManager)
        }

        mViewModel.homes.observe(viewLifecycleOwner) { homeEntities ->
            homeAdapter.update(
                homeEntities
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                    parentFragmentManager,
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
}