package com.github.sewerina.meter_readings.ui.homes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sewerina.meter_readings.R;
import com.github.sewerina.meter_readings.database.HomeEntity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomesActivity extends AppCompatActivity {
    private static final String TAG = "HomesActivity";

    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddHomeFab;

    private HomesViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homes);

        final HomeAdapter homeAdapter = new HomeAdapter();

        mViewModel = ViewModelProviders.of(this).get(HomesViewModel.class);
        mViewModel.getHomes().observe(this, new Observer<List<HomeEntity>>() {
            @Override
            public void onChanged(List<HomeEntity> homeEntities) {
                homeAdapter.update(homeEntities);
            }
        });

        mRecyclerView = findViewById(R.id.recyclerHomes);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(homeAdapter);

        mAddHomeFab = findViewById(R.id.fab_addHome);
        mAddHomeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewHomeDialog.showDialog(getSupportFragmentManager());
            }
        });

//        mViewModel.loadHomesAsync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class HomeHolder extends RecyclerView.ViewHolder {
        private TextView mAddressTv;
        private ImageButton mEditHomeIBtn;
        private ImageButton mDeleteHomeIBtn;

        private HomeEntity mHomeEntity;

        public HomeHolder(@NonNull View itemView) {
            super(itemView);

            mAddressTv = itemView.findViewById(R.id.tv_address);

            mEditHomeIBtn = itemView.findViewById(R.id.iBtn_editHome);
            mEditHomeIBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditHomeDialog.showDialog(getSupportFragmentManager(), mHomeEntity);
                }
            });

            mDeleteHomeIBtn = itemView.findViewById(R.id.iBtn_deleteHome);
            mDeleteHomeIBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialAlertDialogBuilder(v.getContext())
                            .setTitle(R.string.title_deleteHome)
                            .setMessage(R.string.delete_home_message)
                            .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mViewModel.deleteHome(mHomeEntity);
                                }
                            })
                            .setNegativeButton(R.string.btn_cancel, null)
                            .show();
                }
            });
        }

        void bind(HomeEntity entity) {
            mHomeEntity = entity;
            mAddressTv.setText(entity.address);
        }
    }

    private class HomeAdapter extends RecyclerView.Adapter<HomeHolder> {
        private final List<HomeEntity> mHomes = new ArrayList<>();

        @NonNull
        @Override
        public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, parent, false);
            return new HomeHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
            holder.bind(mHomes.get(position));
        }

        @Override
        public int getItemCount() {
            return mHomes.size();
        }

        void update(List<HomeEntity> entities) {
            mHomes.clear();
            mHomes.addAll(entities);
            notifyDataSetChanged();
        }

        void add(HomeEntity entity) {
            mHomes.add(entity);
            notifyItemInserted(mHomes.size() - 1);
        }
    }
}
