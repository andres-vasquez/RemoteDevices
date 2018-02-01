package io.github.andres_vasquez.remotedevicesexample.ui;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.andres_vasquez.remotedevicesexample.R;
import io.github.andres_vasquez.remotedevicesexample.adapters.BleAdapter;
import io.github.andres_vasquez.remotedevicesexample.controller.BLEManager;
import io.github.andres_vasquez.remotedevicesexample.controller.NFCManager;
import io.github.andres_vasquez.remotedevicesexample.model.BLEDevice;
import io.github.andres_vasquez.remotedevicesexample.model.interfaces.OnBLEClickListener;
import io.github.andres_vasquez.remotedevicesexample.model.interfaces.OnBLEDiscoveryEventsListener;
import io.github.andres_vasquez.remotedevicesexample.model.interfaces.OnBLEEventsListener;
import io.github.andres_vasquez.remotedevicesexample.utils.Constants;

//Step XXII: Implementamos la interface y sus metodos
public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OnBLEClickListener,
        OnBLEDiscoveryEventsListener{

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

    //Step 10: Adicionamos las variables de NFC en Main activity
    //NFC
    private NFCManager mNfcManager;
    private NdefMessage message = null;
    private ProgressDialog dialog;
    private Tag currentTag;

    //Step XX: Adicionamos las variables
    //Blue
    private BLEManager mBleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        //Step 11: Inicializamos la variable mNfcManager
        mNfcManager = new NFCManager(this);

        //Step XXI: Iniciamos la clase y su listener
        mBleManager = BLEManager.getInstance(getApplication());
        mBleManager.setmDiscoveryCallback(this);

        initUI();
        initBleList();

        //Add click events
        writeNfcImageButton.setOnClickListener(this);
        readNfcImageButton.setOnClickListener(this);
        bleOptionsButton.setOnClickListener(this);

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

    //Step 12: Adicionamos las 2 funciones de ciclo de vida de la App
    //Step XXIII: Iniciamos/paramos la busqueda BLE
    @Override
    protected void onResume() {
        super.onResume();
        //Llenamos la lista de BLE devices
        try
        {
            mBleManager.verifyBLE();
            mBleManager.initDiscovery();
        }catch (BLEManager.BluetoothNotSupported ex) {
            Toast.makeText(mContext, "BLE not supported", Toast.LENGTH_LONG).show();
        } catch (BLEManager.BlueetoothAdapterNotAvailable ex) {
            Toast.makeText(mContext, "BLE adapter not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcManager.disableDispatch();
        mBleManager.stopDiscovery();
    }

    private void initUI() {
        dataEditText = (EditText) findViewById(R.id.dataEditText);
        writeNfcImageButton = (ImageButton) findViewById(R.id.writeNfcImageButton);
        readNfcImageButton = (ImageButton) findViewById(R.id.readNfcImageButton);
        bleDevicesRecyclerView = (RecyclerView) findViewById(R.id.bleDevicesRecyclerView);
        bleOptionsButton = (Button) findViewById(R.id.bleOptionsButton);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.writeNfcImageButton:
                writeTag();
                break;
            case R.id.readNfcImageButton:
                //Step 13: Cambiamos el metodo readTag por registerToReceiveTags
                registerToReceiveTags();
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

    // Step 14: Escuchamos los Tags NFC

    /**
     * Método que inicializa las lecturas NFC
     */
    private void registerToReceiveTags() {
        try {
            mNfcManager.verifyNFC();

            Intent nfcIntent = new Intent(this, getClass());
            nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    nfcIntent, 0);
            IntentFilter[] intentFiltersArray = new IntentFilter[]{};
            String[][] techList = new String[][]{
                    {android.nfc.tech.Ndef.class.getName()},
                    {android.nfc.tech.NdefFormatable.class.getName()}
            };

            mNfcManager.getmNfcAdapter().enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
        } catch (NFCManager.NFCNotSupported nfcnsup) {
            Toast.makeText(mContext, "NFC not supported", Toast.LENGTH_LONG).show();
        } catch (NFCManager.NFCNotEnabled nfcnEn) {
            Toast.makeText(mContext, "NFC Not enabled", Toast.LENGTH_LONG).show();
        }
    }


    //Step 15: Sobreescribimos el método onNewIntent para recibir datos del SO
    @Override
    public void onNewIntent(Intent intent) {
        Log.d(LOG, "New intent");
        currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (message != null) {
            //Escritura
            mNfcManager.writeTag(currentTag, message);
            dialog.dismiss();
            Toast.makeText(mContext, "Tag written", Toast.LENGTH_LONG).show();
            message = null;
        } else {
            // Lectura
            readTag(intent);
        }
    }


    //Step 16: Adicionamos el método de escritura
    private void writeTag() {
        String data = dataEditText.getText().toString().trim();
        if (!data.isEmpty()) {
            registerToReceiveTags();

            message = mNfcManager.createTextMessage(data);
            if (message != null) {
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Tag NFC Tag please");
                dialog.show();
                dataEditText.setText("");
            }
        }
    }

    //Step 17: Adicionamos/cambiamos la función de lectura
    private void readTag(Intent intent) {
        dataEditText.setEnabled(false);
        if ((NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()))) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage message = (NdefMessage) rawMessages[0];
                String data = new String(message.getRecords()[0].getPayload());
                dataEditText.setText(data);

                //Get more details
                processTag(intent);
            }
        }
    }


    //Step 18: Obtenemos más detalles del chip
    public static void processTag(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String[] techList = tag.getTechList();
        for (String itemTechList : techList) {
            if (itemTechList.equals(MifareClassic.class.getName())) {
                MifareClassic mifareClassicTag = MifareClassic.get(tag);
                switch (mifareClassicTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        Log.d(LOG, "MifareClassic.TYPE_CLASSIC");
                        break;
                    case MifareClassic.TYPE_PLUS:
                        Log.d(LOG, "MifareClassic.TYPE_PLUS");
                        break;
                    case MifareClassic.TYPE_PRO:
                        Log.d(LOG, "MifareClassic.TYPE_PRO");
                        break;
                }
            } else if (itemTechList.equals(MifareUltralight.class.getName())) {
                //For Mifare Ultralight
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                Log.e("mirafareultralight", mifareUlTag.toString());
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        Log.d(LOG, "MifareUltralight.TYPE_ULTRALIGHT");
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        Log.d(LOG, "MifareUltralight.TYPE_ULTRALIGHT_C");
                        break;
                }
            } else if (itemTechList.equals(IsoDep.class.getName())) {
                // info[1] = "IsoDep";
                IsoDep isoDepTag = IsoDep.get(tag);
                Log.d(LOG, "IsoDep");

            } else if (itemTechList.equals(Ndef.class.getName())) {
                Ndef.get(tag);
                Log.d(LOG, "Ndef");

            } else if (itemTechList.equals(NdefFormatable.class.getName())) {
                NdefFormatable ndefFormatableTag = NdefFormatable.get(tag);
                Log.d(LOG, "NdefFormatable");

            }
        }

    }

    @Override
    public void onDeviceClick(BLEDevice device) {
        mBleManager.stopDiscovery();
        Intent logsActivityIntent = new Intent(mContext, LogsActivity.class);
        logsActivityIntent.putExtra(Constants.EXTRA_BLE, device.getAddress());
        startActivity(logsActivityIntent);
    }

    @Override
    public void onDeviceFound(final BLEDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBleAdapter.addItem(device);
            }
        });
    }

    @Override
    public void onDeviceUpdate(final BLEDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBleAdapter.updateItem(device);
            }
        });
    }
}
