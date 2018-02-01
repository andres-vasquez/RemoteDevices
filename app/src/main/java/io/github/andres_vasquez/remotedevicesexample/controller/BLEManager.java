package io.github.andres_vasquez.remotedevicesexample.controller;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.github.andres_vasquez.remotedevicesexample.model.BLEDevice;
import io.github.andres_vasquez.remotedevicesexample.model.interfaces.OnBLEDiscoveryEventsListener;
import io.github.andres_vasquez.remotedevicesexample.model.interfaces.OnBLEEventsListener;
import io.github.andres_vasquez.remotedevicesexample.utils.Constants;

/**
 * Created by andresvasquez on 1/31/18.
 */

//Step IX: Creamos la clase BLEManager y sus variables globales
public class BLEManager {

    private static final String LOG = BLEManager.class.getSimpleName();

    private Application mApplication;
    private static BLEManager INSTANCE;

    private OnBLEDiscoveryEventsListener mDiscoveryCallback;
    private OnBLEEventsListener mEventsCallback;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    //Map of devices: key=address, value=obj
    private Map<String, BLEDevice> mapDevices = new ConcurrentHashMap<>();

    private BluetoothLeScanner mScanner;
    private List<ScanFilter> scanFilters;
    private ScanCallback scanCallback;

    //Connection state
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public BLEManager(Application mApplication) {
        this.mApplication = mApplication;
    }

    public static BLEManager getInstance(Application app) {
        if (INSTANCE == null) {
            INSTANCE = new BLEManager(app);
        }
        return INSTANCE;
    }

    //Step X: Adicionamos los getter y setters
    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public BluetoothGattService getGateService() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getService(UUID.fromString("00000100-6b72-6170-2065-757173697571"));
    }

    public void setmDiscoveryCallback(OnBLEDiscoveryEventsListener mDiscoveryCallback) {
        this.mDiscoveryCallback = mDiscoveryCallback;
    }

    public void setmEventsCallback(OnBLEEventsListener mEventsCallback) {
        this.mEventsCallback = mEventsCallback;
    }

    //Step XI: Creamos las excepciones custom
    public static class BluetoothNotSupported extends Exception {

        public BluetoothNotSupported() {
            super();
        }
    }

    /**
     * Excepcion custom
     */
    public static class BlueetoothAdapterNotAvailable extends Exception {
        public BlueetoothAdapterNotAvailable() {
            super();
        }
    }

    //Step XII: Verificamos el soporte BLE
    public void verifyBLE() throws BlueetoothAdapterNotAvailable, BluetoothNotSupported {

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mApplication.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                throw new BLEManager.BluetoothNotSupported();
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            throw new BlueetoothAdapterNotAvailable();
        }

        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        scanFilters = new ArrayList<>();
        //scanFilters.add(new ScanFilter.Builder().setServiceUuid(Constants.EDDYSTONE_SERVICE_UUID).build());
    }

    //Step XIII: Buscamos dispositivos
    public void initDiscovery() {
        if (mScanner == null) {
            return;
        }

        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                ScanRecord scanRecord = result.getScanRecord();
                if (scanRecord == null) {
                    return;
                }

                BluetoothDevice bluetoothDevice = result.getDevice();
                if (bluetoothDevice != null) {
                    String deviceAddress = result.getDevice().getAddress();
                    BLEDevice bleDevice;

                    if (!mapDevices.containsKey(deviceAddress)) {
                        bleDevice = new BLEDevice();
                        bleDevice.setName(result.getDevice().getName());
                        bleDevice.setAddress(deviceAddress);
                        bleDevice.setRssi(result.getRssi());
                        bleDevice.setTimestamp(System.currentTimeMillis());

                        mapDevices.put(deviceAddress, bleDevice);

                        //Send new device to the list
                        if (mDiscoveryCallback != null) {
                            mDiscoveryCallback.onDeviceFound(bleDevice);
                        }
                    } else {
                        mapDevices.get(deviceAddress).setTimestamp(System.currentTimeMillis());
                        mapDevices.get(deviceAddress).setRssi(result.getRssi());

                        //Update device in the list
                        if (mDiscoveryCallback != null) {
                            mDiscoveryCallback.onDeviceUpdate(mapDevices.get(deviceAddress));
                        }
                    }
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                switch (errorCode) {
                    case SCAN_FAILED_ALREADY_STARTED:
                        break;
                    case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                        break;
                    case SCAN_FAILED_FEATURE_UNSUPPORTED:
                        break;
                    case SCAN_FAILED_INTERNAL_ERROR:
                        break;
                    default:
                        break;
                }
            }
        };

        mScanner.startScan(scanFilters, Constants.SCAN_SETTINGS, scanCallback);
        Log.d(LOG, "Start BLE scanning");
    }


    //Step XIV: Detenemos la busqueda
    public void stopDiscovery() {
        if (mScanner != null) {
            mScanner.stopScan(scanCallback);
            Log.d(LOG, "Stop BLE scanning");
        }
    }

    //Step XV: Agregamos el metodo de conexion y desconexion
    public boolean connect(final String address) throws BlueetoothAdapterNotAvailable {
        if (mBluetoothAdapter == null || address == null) {
            throw new BlueetoothAdapterNotAvailable();
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(LOG, "Device not found.  Unable to connect.");
            return false;
        }

        mBluetoothGatt = device.connectGatt(mApplication, false, mGattCallback);
        Log.d(LOG, "Trying to create a new connection.");
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(LOG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }


    //Step XVI: Metodo para cerrar el enlace BLE
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    //Step XVII: Callback de GATT
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
                Log.i(LOG, "Connected to GATT server.");

                // Una vez conectado buscar los servicios
                Log.i(LOG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i(LOG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(LOG, "onServicesDiscovered received: " + status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mEventsCallback != null) {
                    mEventsCallback.onReadResponse(characteristic);
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mEventsCallback != null) {
                    mEventsCallback.onWriteSuccess();
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.i(LOG, "Characteristic changed.");
        }
    };

    //Step XVIII: Agregamos el metodo de lectura y escritura
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(LOG, "GATT not initializaed");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null) {
            Log.w(LOG, "GATT not initializaed");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }
}
