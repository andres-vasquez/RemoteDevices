package io.github.andres_vasquez.remotedevicesexample.model.interfaces;

import android.bluetooth.BluetoothGattCharacteristic;

import io.github.andres_vasquez.remotedevicesexample.model.BLEDevice;

/**
 * Created by andresvasquez on 1/31/18.
 */

//Step IV: Createmos el interface para las respuestas de eventos
public interface OnBLEEventsListener {

    //Write
    void onWriteSuccess();

    //Read
    void onReadResponse(BluetoothGattCharacteristic characteristic);
}
