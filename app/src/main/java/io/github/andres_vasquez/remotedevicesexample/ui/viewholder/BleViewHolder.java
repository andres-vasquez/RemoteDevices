package io.github.andres_vasquez.remotedevicesexample.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.github.andres_vasquez.remotedevicesexample.R;

/**
 * Created by andresvasquez on 1/30/18.
 */

public class BleViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout containerLinearLayout;
    public TextView nameTextView;
    public TextView addressTextView;
    public TextView rssiTextView;

    public BleViewHolder(View itemView) {
        super(itemView);

        containerLinearLayout= itemView.findViewById(R.id.containerLinearLayout);
        nameTextView = itemView.findViewById(R.id.nameTextView);
        addressTextView = itemView.findViewById(R.id.addressTextView);
        rssiTextView = itemView.findViewById(R.id.rssiTextView);
    }
}
