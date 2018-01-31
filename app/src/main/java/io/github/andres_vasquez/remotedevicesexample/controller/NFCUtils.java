package io.github.andres_vasquez.remotedevicesexample.controller;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

/**
 * Created by andresvasquez on 1/29/18.
 */

public class NFCUtils {

    private NfcAdapter mNfc;
    private boolean inWriteMode = false;

    /*public NFCUtils(Context context){
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            inWriteMode = true;
            readTag(intent);
        }
        mNfc = NfcAdapter.getDefaultAdapter(context);
    }

    public void read(){
        if (inWriteMode &&
                (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
                        || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()))) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            intent.getExtras().toString();
            startProductDetail(TagReader.ReadNdefMessage(intent));
            finish();
        }
    }*/
}
