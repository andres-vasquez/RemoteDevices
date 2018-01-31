package io.github.andres_vasquez.remotedevicesexample.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import io.github.andres_vasquez.remotedevicesexample.R;
import io.github.andres_vasquez.remotedevicesexample.utils.Constants;

public class LogsActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG = LogsActivity.class.getSimpleName();
    private Context mContext;

    private EditText dataEditText;
    private ImageButton sendImageButton;
    private ImageButton advertiseImageButton;
    private TextView logsTextView;

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        mContext = this;

        initUI();
        sendImageButton.setOnClickListener(this);
        advertiseImageButton.setOnClickListener(this);

        if(getIntent().hasExtra(Constants.EXTRA_BLE)){
            address = getIntent().getStringExtra(Constants.EXTRA_BLE);
        }
    }

    private void initUI() {
        dataEditText = (EditText) findViewById(R.id.dataEditText);
        sendImageButton = (ImageButton) findViewById(R.id.sendImageButton);
        advertiseImageButton = (ImageButton) findViewById(R.id.advertiseImageButton);
        logsTextView = (TextView) findViewById(R.id.logsTextView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendImageButton:
                sendData();
                break;

            case R.id.advertiseImageButton:
                startAdvertisingService();
                break;
            default:
                Log.w(LOG, "Option not implemented");
                break;
        }
    }


    private void writeLogs(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logsTextView.append(text);
                logsTextView.append("\n");
            }
        });
    }

    private void startAdvertisingService() {
    }

    private void sendData() {
    }
}
