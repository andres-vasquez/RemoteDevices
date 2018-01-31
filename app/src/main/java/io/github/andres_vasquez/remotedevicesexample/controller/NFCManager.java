package io.github.andres_vasquez.remotedevicesexample.controller;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * Created by andresvasquez on 1/29/18.
 */

// Step 4: Creamos NFCManager con su constructor activity y el getter para adapter
public class NFCManager {

    private Activity mActivity;
    private NfcAdapter mNfcAdapter;

    /**
     * Constructor del NFC Manager
     * @param mActivity activity
     */
    public NFCManager(Activity mActivity) {
        this.mActivity = mActivity;
    }


    /**
     * Getter adapter
     * @return nfc adapter
     */
    public NfcAdapter getmNfcAdapter() {
        return mNfcAdapter;
    }


    //Step 5: Creamos las exceptions custom
    /**
     * Excepcion custom
     */
    public static class NFCNotSupported extends Exception {

        public NFCNotSupported() {
            super();
        }
    }

    /**
     * Excepcion custom
     */
    public static class NFCNotEnabled extends Exception {
        public NFCNotEnabled() {
            super();
        }
    }

    // Step 6: Verificamos el chip NFC
    /**
     * Verifica que el chip NFC este activo y habilitado
     * @throws NFCNotSupported Dispositivo que no tiene NFC
     * @throws NFCNotEnabled NFC deshabilitado
     */
    public void verifyNFC() throws NFCNotSupported, NFCNotEnabled {

        mNfcAdapter = NfcAdapter.getDefaultAdapter(mActivity);

        if (mNfcAdapter == null)
            throw new NFCNotSupported();

        if (!mNfcAdapter.isEnabled())
            throw new NFCNotEnabled();

    }

    // Step 7: Adicionamos el método que libera el chip
    /**
     * Libera el adapter de escuchar los cambios
     */
    public void disableDispatch() {
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(mActivity);
        }
    }

    // Step 8: Creamos el mensaje de texto a enviar en formato NdefMessage
    /**
     * Creamos el mensaje
     * @param content Contenido
     * @return mensaje en formato de NdefMessage
     */
    public NdefMessage createTextMessage(String content) {
        try {

            byte[] lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] text = content.getBytes("UTF-8");

            int langSize = lang.length;
            int textLength = text.length;

            ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + langSize + textLength);
            payload.write((byte) (langSize & 0x1F));
            //payload.write(lang, 0, langSize);
            payload.write(text, 0, textLength);
            NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
            return new NdefMessage(new NdefRecord[]{record});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    // Step 9: Agregamos el método de escritura de datos
    /**
     * Funcion para escribir un Tag
     * @param tag Tag NFC
     * @param message Mensaje a escribir
     */
    public void writeTag(Tag tag, NdefMessage message) {
        if (tag != null) {
            try {
                Ndef ndefTag = Ndef.get(tag);
                //ndefTag.isWritable() -> Para explicar
                //ndefTag.makeReadOnly() -> Para explicar

                if (ndefTag == null) {
                    // Se trataraá de escribir en NDEF format
                    NdefFormatable nForm = NdefFormatable.get(tag);
                    if (nForm != null) {
                        nForm.connect();
                        nForm.format(message);
                        nForm.close();
                    }
                } else {
                    ndefTag.connect();
                    ndefTag.writeNdefMessage(message);
                    ndefTag.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}