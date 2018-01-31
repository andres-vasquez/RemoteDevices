package io.github.andres_vasquez.remotedevicesexample.model;

/**
 * Created by andresvasquez on 1/30/18.
 */

public class BLEDevice {

    private String name;

    private String address;

    private int rssi;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
