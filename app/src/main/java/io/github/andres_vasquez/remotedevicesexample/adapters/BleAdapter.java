package io.github.andres_vasquez.remotedevicesexample.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

import io.github.andres_vasquez.remotedevicesexample.R;
import io.github.andres_vasquez.remotedevicesexample.model.BLEDevice;
import io.github.andres_vasquez.remotedevicesexample.model.interfaces.OnBLEClickListener;
import io.github.andres_vasquez.remotedevicesexample.ui.viewholder.BleViewHolder;

/**
 * Created by Adrian on 12/8/2017.
 */

public class BleAdapter extends RecyclerView.Adapter<BleViewHolder> {
    private ArrayList<BLEDevice> datos;
    private Context context;

    private OnBLEClickListener onBLEClickListener;

    public BleAdapter(Context context) {
        datos = new ArrayList<BLEDevice>();
        this.context = context;
    }

    @Override
    public BleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble, parent, false);
        return new BleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BleViewHolder holder, int position) {
        final BLEDevice c = datos.get(position);
        holder.nameTextView.setText(c.getName());
        holder.addressTextView.setText(c.getAddress());
        holder.rssiTextView.setText(String.valueOf(c.getRssi()));
        holder.containerLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onBLEClickListener !=null){
                    onBLEClickListener.onDeviceClick(c);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public void swapData(ArrayList<BLEDevice> datos) {
        this.datos = datos;
        notifyDataSetChanged();
    }

    public void clear() {
        datos.clear();
        notifyDataSetChanged();
    }

    public void setOnBLEClickListener(OnBLEClickListener onBLEClickListener) {
        this.onBLEClickListener = onBLEClickListener;
    }
}
