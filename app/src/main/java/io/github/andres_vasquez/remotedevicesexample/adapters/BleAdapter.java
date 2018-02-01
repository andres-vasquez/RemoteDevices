package io.github.andres_vasquez.remotedevicesexample.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.andres_vasquez.remotedevicesexample.R;
import io.github.andres_vasquez.remotedevicesexample.model.BLEDevice;
import io.github.andres_vasquez.remotedevicesexample.model.interfaces.OnBLEClickListener;
import io.github.andres_vasquez.remotedevicesexample.ui.viewholder.BleViewHolder;

/**
 * Created by Adrian on 12/8/2017.
 */

public class BleAdapter extends RecyclerView.Adapter<BleViewHolder> {

    //Step V: Cambiamos las variables de BleAdapter
    private ArrayList<BLEDevice> data;
    // Map of objects key=address, value=position
    private Map<String, Integer> mapData;

    private Context context;
    private OnBLEClickListener onBLEClickListener;

    public BleAdapter(Context context) {
        data = new ArrayList<BLEDevice>();
        mapData = new HashMap<>();
        this.context = context;
    }

    @Override
    public BleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble, parent, false);
        return new BleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BleViewHolder holder, int position) {
        final BLEDevice c = data.get(position);
        holder.nameTextView.setText(c.getName());
        holder.addressTextView.setText(c.getAddress());
        holder.rssiTextView.setText(String.valueOf(c.getRssi()));
        holder.containerLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBLEClickListener != null) {
                    onBLEClickListener.onDeviceClick(c);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    //Step VI: Adicionamos los metodos addItem y updateItem
    public void addItem(BLEDevice device) {
        mapData.put(device.getAddress(),data.size());
        data.add(device);
        notifyDataSetChanged();
    }

    public void updateItem(BLEDevice device) {
        if(mapData.containsKey(device.getAddress())){
            int position = mapData.get(device.getAddress());
            if(!data.isEmpty() && position<data.size()){
                data.get(position).setRssi(device.getRssi());
                data.get(position).setTimestamp(device.getTimestamp());
                notifyDataSetChanged();
            }
        }
    }

    public void swapData(ArrayList<BLEDevice> datos) {
        this.data = datos;
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void setOnBLEClickListener(OnBLEClickListener onBLEClickListener) {
        this.onBLEClickListener = onBLEClickListener;
    }
}
