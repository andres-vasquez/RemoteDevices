package io.github.andres_vasquez.remotedevicesexample.model.interfaces;

import io.github.andres_vasquez.remotedevicesexample.model.BLEDevice;

/**
 * Created by andresvasquez on 1/31/18.
 */

//Step III: Createmos el interface para las respuestas de conexion
public interface OnBLEDiscoveryEventsListener {
    //Discovery
    void onDeviceFound(BLEDevice device);
    void onDeviceUpdate(BLEDevice device);
}
