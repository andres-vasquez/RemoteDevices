package io.github.andres_vasquez.remotedevicesexample.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.github.andres_vasquez.remotedevicesexample.R;
import io.github.andres_vasquez.remotedevicesexample.adapters.BleAdapter;
import io.github.andres_vasquez.remotedevicesexample.model.BLEDevice;
import io.github.andres_vasquez.remotedevicesexample.model.interfaces.OnBLEClickListener;

import static io.github.andres_vasquez.remotedevicesexample.utils.Constants.EXTRA_BLE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnBLEClickListener {

    private static final String LOG = MainActivity.class.getSimpleName();
    private Context mContext;

    //UI widgets
    private EditText dataEditText;
    private ImageButton writeNfcImageButton;
    private ImageButton readNfcImageButton;
    private Button bleOptionsButton;
    private RecyclerView bleDevicesRecyclerView;


    //Adapter
    private List<BLEDevice> mDevices;
    private BleAdapter mBleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initUI();
        initBleList();

        //Add click events
        writeNfcImageButton.setOnClickListener(this);
        readNfcImageButton.setOnClickListener(this);

        //Add long click event to support edit
        writeNfcImageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dataEditText.setEnabled(true);
                dataEditText.setText("");
                return false;
            }
        });
    }

    private void initUI() {
        dataEditText = (EditText) findViewById(R.id.dataEditText);
        writeNfcImageButton = (ImageButton) findViewById(R.id.writeNfcImageButton);
        readNfcImageButton = (ImageButton) findViewById(R.id.readNfcImageButton);
        bleDevicesRecyclerView = (RecyclerView) findViewById(R.id.bleDevicesRecyclerView);
        bleOptionsButton = (Button)findViewById(R.id.bleOptionsButton);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.writeNfcImageButton:
                writeTag();
                break;

            case R.id.readNfcImageButton:
                readTag();
                break;
            case R.id.bleOptionsButton:
                Intent logsActivityIntent = new Intent(mContext, LogsActivity.class);
                startActivity(logsActivityIntent);
                break;
            default:
                Log.w(LOG, "Option not implemented");
                break;
        }
    }

    private void initBleList() {
        mDevices = new ArrayList<>();
        bleDevicesRecyclerView.setHasFixedSize(true);
        bleDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mBleAdapter = new BleAdapter(mContext);
        bleDevicesRecyclerView.setAdapter(mBleAdapter);
        mBleAdapter.setOnBLEClickListener(this);
    }

    private void readTag() {
    }

    private void writeTag() {
    }

    @Override
    public void onDeviceClick(BLEDevice device) {
        Intent logsActivityIntent = new Intent(mContext, LogsActivity.class);
        logsActivityIntent.putExtra(EXTRA_BLE, device.getAddress());
        startActivity(logsActivityIntent);
    }
}
