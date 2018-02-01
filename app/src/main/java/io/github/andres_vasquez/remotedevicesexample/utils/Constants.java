package io.github.andres_vasquez.remotedevicesexample.utils;

import android.bluetooth.le.ScanSettings;
import android.os.ParcelUuid;

/**
 * Created by andresvasquez on 1/30/18.
 */

//Step VII: Modificamos las constantes
public class Constants {
    public static final String EXTRA_BLE = "bleSelected";

    public static final ParcelUuid EDDYSTONE_SERVICE_UUID =
            ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB");


    public static final ScanSettings SCAN_SETTINGS =
            new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(0)
                    .build();
}
