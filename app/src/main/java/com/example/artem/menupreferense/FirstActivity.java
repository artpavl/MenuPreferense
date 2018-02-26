package com.example.artem.menupreferense;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;

import static com.example.artem.menupreferense.DialogBTSearch.listViewSearcDevice;
import static com.example.artem.menupreferense.GlobalVeriable.BLUETOOTH_KEY;
import static com.example.artem.menupreferense.GlobalVeriable.sTAG;
import static com.example.artem.menupreferense.StaticValue.*;

public class FirstActivity extends AppCompatActivity implements  DialogBTSearch.OnHeadlineSelectedListener {
    public static String TAG = sTAG;
   public static BluetoothAdapter mbluetoothAdapter;
    Button btnFindBluetooth;
    private static final int ENABLE_BT_REQUEST_CODE = 1; //
    public static ArrayList<BluetoothDevice> mBTDevices;
    public static DeviceListAdapter mDeviceListAdapter;
    DialogBTSearch dialog;
    private Timer mTimer;

    // Создаем BroadcastReceiver для ACTION_FOUND
    private final BroadcastReceiver mBroadCastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mbluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mbluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "Получено:  Состояние OFF");
                        Toast.makeText(getApplicationContext(), "Получено:  Состояние OFF", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "Получено:  TURNING_OFF");
                        Toast.makeText(getApplicationContext(), "Получено:  TURNING_OFF", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "Получено:  STATE_ON");
                        Toast.makeText(getApplicationContext(), "Получено:  STATE_ON", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "Получено:  TURNING_ON");
                        Toast.makeText(getApplicationContext(), "Получено:  TURNING_ON", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };


    private final BroadcastReceiver mBroadCastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Broadcast Action: указывает, что режим сканирования Bluetooth локального адаптера изменился.
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, mbluetoothAdapter.ERROR);
                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        // Указывает, что как сканирование запросов, так и проверка страницы включены на локальном адаптере Bluetooth.
                        Log.d(TAG, "РЕЖИМ СКАНИРОВАНИЯ ПОДКЛЮЧЕННЫЙ ОТКРЫТЫЙ");
                        Toast.makeText(getApplicationContext(), "РЕЖИМ СКАНИРОВАНИЯ ОТКРЫТЫЙ", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "РЕЖИМ СКАНИРОВАНИЯ ПОДКЛЮЧЕН");
                        Toast.makeText(getApplicationContext(), "РЕЖИМ СКАНИРОВАНИЯ ПОДКЛЮЧЕН", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "РЕЖИМ СКАНИРОВАНИЯ ОТКЛЮЧЕН");
                        Toast.makeText(getApplicationContext(), "РЕЖИМ СКАНИРОВАНИЯ ОТКЛЮЧЕН", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "СОСТОЯНИЕ СОЕДИНЕНИЯ");
                        Toast.makeText(getApplicationContext(), "СОСТОЯНИЕ СОЕДИНЕНИЯ", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "СОСТОЯНИЕ СОЕДИНЕНО");
                        Toast.makeText(getApplicationContext(), "СОСТОЯНИЕ СОЕДИНЕНО", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };
    private final BroadcastReceiver mBroadCastReceiver3 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(sTAG, "mBroadCastReceiver3");
            //Broadcast Action: указывает, что режим сканирования Bluetooth локального адаптера изменился.
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Подключаемся к ранее подключенному устройству
                if(loadAdress() != null){
                    if(device.getAddress().equals(loadAdress()) ){
                        dialog.dismiss();
                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        i.putExtra("Adress", device.getAddress());
                        startActivity(i);
                    }
                }

                mBTDevices.add(device);
                mDeviceListAdapter = new DeviceListAdapter(getApplicationContext(), R.layout.device_adapter_view, mBTDevices);
                mDeviceListAdapter.setNotifyOnChange(true); // Обновляем адаптер
                listViewSearcDevice.setAdapter(mDeviceListAdapter); // Устанавливаем значени для listViewSearcDevice
            }


        }
    };



    public String loadAdress() {
        String adress = sharedPreferences.getString(BLUETOOTH_KEY, "");
        Log.d(sTAG,"sharedPreferenses " + adress + " загружен");
        return adress;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mBroadCastReceiver1);
            unregisterReceiver(mBroadCastReceiver2);
            unregisterReceiver(mBroadCastReceiver3);
        } catch (Exception e) {
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mbluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btnFindBluetooth = (Button) findViewById(R.id.btnFindBluetooth);
        mBTDevices = new ArrayList<>();


       //  Задание настроек по умолчанию
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // Получаем объект SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);




        btnFindBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableDisableBT();
                dialog = new DialogBTSearch();
                dialog.show(getSupportFragmentManager(), "bt");
                Log.d(TAG,"dialog.show");
                // progressBar.setVisibility(ProgressBar.VISIBLE);
                //  btnDiscover();

            }
        });


    }


    // Метод определения вклюяенности bluetooth;
    public void enableDisableBT() {
        if (mbluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth недоступен", Toast.LENGTH_SHORT).show();

        }
        if (!mbluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Включаем Bluetooth");
            //Открываем стандартное окно включения Bluetooth
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, ENABLE_BT_REQUEST_CODE);

//            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//            registerReceiver(mBroadCastReceiver1, BTIntent);
        }
        if (mbluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth включен");
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadCastReceiver1, BTIntent);
            btnDiscover();
        }

    }



    // Метод поиска удаленных устройств bluetooth
    public void btnDiscover() {

        Log.d(TAG, " btnDiscover: Поиск устройств");


        // Очищаем mBTDevices
        mBTDevices.clear();
        if (mbluetoothAdapter.isDiscovering()) {
            mbluetoothAdapter.cancelDiscovery();
            Log.d(TAG, " btnDiscover: Обнаружение отменено");


         //   checkBTPermission();

            mbluetoothAdapter.startDiscovery(); //Запустите процесс обнаружения удаленных устройств.
            IntentFilter discoverDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadCastReceiver3, discoverDeviceIntent);

        }
        if (!mbluetoothAdapter.isDiscovering()) {
         //  checkBTPermission();
            mbluetoothAdapter.startDiscovery(); //Запустите процесс обнаружения удаленных устройств.
            IntentFilter discoverDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadCastReceiver3, discoverDeviceIntent);
        }
    }

//    private void checkBTPermission() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCES_FINE_LOCATION");
//            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCES_COARSE_LOCATION");
//            if (permissionCheck != 0) {
//                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
//            } else {
//                Log.d(TAG, " checkBTPermission() Не надо выбирать разрешение");
//            }
//        }
//
//    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENABLE_BT_REQUEST_CODE) {

            // Bluetooth успешно включен!
            if (resultCode == Activity.RESULT_OK) {
                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(mBroadCastReceiver1, BTIntent);
                btnDiscover();

            } else {
                finish();
            }
        }
    }


    // Релизуем интерфейс DialogBTSearch.OnHeadlineSelectedListener
    @Override
    public void onArticleSelected(int position) {
        mbluetoothAdapter.cancelDiscovery();
        Log.d(TAG, " Нажат пункт списка ");
        String name = mBTDevices.get(position).getName();
        String adress = mBTDevices.get(position).getAddress();

        // Сдесь можно проверить MAC адресс устройства

        Log.d(TAG, " Имя устройства  " + name);
        Log.d(TAG, " Адресс устройства  " + adress);
        dialog.dismiss();
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        i.putExtra("Name", name);
        i.putExtra("Adress", adress);
        startActivity(i);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBTDevices.get(position).createBond();
        }


    }



}

