package com.example.artem.menupreferense;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Artem on 03.01.2018.
 */

public class GlobalVeriable {
    public static final String sTAG = "LOGIS";
    public static String sBluetoothAdress;
    public static BluetoothAdapter sBluetoothAdapter;
    public static BluetoothDevice sBluetoothDevice;
    public static BluetoothSocket sBluetoothSocket;
    public static InputStream sInputStream;
    public static OutputStream sOutputStream;


    public static void sRestartGlobalVeriable() {




        try {
            if (sInputStream != null) {
                sInputStream.close();
            }

            if (sOutputStream != null) {
                sOutputStream.close();
            }


            try {
                if(sBluetoothSocket!=null){
                    sBluetoothSocket.close();
                    sBluetoothSocket = null;
                }

            } catch (IOException e) {
                Log.d(sTAG, "Не удалось закрыть socket ");
                e.printStackTrace();
            }


        } catch (IOException e) {
            Log.d(sTAG, "Не удалось закрыть потоки ");
            e.printStackTrace();
        }


    }
}
