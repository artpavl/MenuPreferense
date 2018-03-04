package com.example.artem.menupreferense;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.artem.menupreferense.FirstActivity.mbluetoothAdapter;
import static com.example.artem.menupreferense.GlobalVeriable.sBluetoothDevice;
import static com.example.artem.menupreferense.GlobalVeriable.sBluetoothSocket;
import static com.example.artem.menupreferense.GlobalVeriable.sInputStream;
import static com.example.artem.menupreferense.GlobalVeriable.sOutputStream;
import static com.example.artem.menupreferense.GlobalVeriable.sRestartGlobalVeriable;
import static com.example.artem.menupreferense.GlobalVeriable.sTAG;
import static com.example.artem.menupreferense.StaticValue.*;

public class MainActivity extends AppCompatActivity {


    TextView tvFragment;
    android.support.v4.app.Fragment fragment;
    Toolbar toolbar;
    Menu menu;
    Button button_battery_camera;
    Button button_battery_transmitter;
    Button button_mode_camera;
    TouchImageView touchImageView;
    DownloadImageTask downloadImageTask;
    ReturnSettings returnSetting;
    BTConnectThread btConnectThread;
    Loop loop;
    LoopShot loopShot;
    String adressBluetooth;
    String nameBluetooth;
    Bitmap bitmap;
    int energy_camera;
    int energy_transmiter;

    // данные для отправки камере
    int numberCam = 1; // номер камеры 0
    static final int numberCommandSettings = 9; // номер каманды 1
    int frequencytTansmission; // частота передачи 2
    int powerTransmitter; // мощность передатчика 3
    int resolution; // разрешение 4
    int qualityFoto; // качество фото 5
    int infratedIllumination; // ик подсветка 6
    int shootingFoto; // фото по выстрелу 7
    int sensitivity; // чувствительность 8
    int beep; // бипер резерв 9
    int reserve; // резервный байт 10
    int shootingMode; // режим съемки

    boolean exit = false;
    boolean stopFoto = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static Button button_make_foto;
    private Button save;
    private Button button_settings;
    private ProgressBar progressBar;
    // Запрос на производство фотографии
    static byte[] returnSettings;
    byte commandSettings;
    int countSettings;
    boolean SET;
    byte[] makeFoto = {(byte) 1, (byte) 12, (byte) 10, (byte) 13};  // делать фото


    //Broadcast Action: указывает на изменение состояния связи удаленного устройства.
    private final BroadcastReceiver mBroadCastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.d(sTAG, "ACTION_ACL_DISCONNECTED ");
                    Toast.makeText(context, "Потеряна связь с устройством", Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }


//            String action = intent.getAction();
//            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
//                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
//                    //Указывает, что удаленное устройство соединено (сопряжено).
//                    Log.d(sTAG, "mBroadCastReceiver4 удаленное устройство соединено ");
//                    Toast.makeText(getApplicationContext(),"Удаленное устройство соединино",Toast.LENGTH_LONG).show();
//                }
//                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
//                    //Указывает, что соединение с удаленным устройством выполняется.
//                    Log.d(sTAG, "mBroadCastReceiver4 соединение с удаленным устройством выполняется ");
//                }
//                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
//                    //  Указывает, что удаленное устройство не соединено (сопряжено).
//                    Log.d(sTAG, "mBroadCastReceiver4 устройство не соединено ");
//
//                }
//
//            }

        }
    };


    // Метод задает параметры заряда камеры и передатчика если нет связи
    public void setPowerNull() {
        button_battery_camera.setText("Нет ответа");
        button_battery_transmitter.setText("Нет ответа");
        button_make_foto.setClickable(false);
    }


    // Метод задает параметры заряда камеры и передатчика
    public void setPower(int power_camera, int power_transmiter) {
        button_battery_camera.setText(power_camera + "%");
        button_battery_transmitter.setText(power_transmiter + "%");
        button_make_foto.setClickable(true);
    }


    // Метод сохранения картинки
    public void savePictyre() {

        if (bitmap != null) {
            Date date = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy '  ' kk:mm:ss");
            String filename = formatter.format(date);
            //filename = filename + ".png";
            String fullpath = getFilesDir() + "/" + "OcoCam";
            //Создание объекта файла.
            File fhandle = new File(fullpath, filename);
            try {
                //Если нет директорий в пути, то они будут созданы:
                if (!fhandle.getParentFile().exists())
                    fhandle.getParentFile().mkdirs();
                //Если файл существует, то он будет перезаписан:
                fhandle.createNewFile();
                OutputStream fOut = new FileOutputStream(fhandle);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                // регистрация в фотоальбоме
                MediaStore.Images.Media.insertImage(getContentResolver(), fhandle.getAbsolutePath(), fhandle.getName(), fhandle.getName());
                Toast.makeText(getApplicationContext(),
                        "Сохранено ",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),
                        "Не удалость сохранить",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Отсутствует фото для сохранения",
                    Toast.LENGTH_LONG).show();
        }

    }

    // Метод увеличения размера картинки
    @SuppressLint("NewApi")
    private Bitmap getBitmap(Bitmap bm, int size) {

        int width = bm.getWidth();
        int height = bm.getHeight();
        // Увелич в size раз
        int halfWidth = width * size;
        int halfHeight = height * size;
        bm = Bitmap.createScaledBitmap(bm, halfWidth, halfHeight, true);

        return bm;

    }

    // Отправка запроса устройству
    public static void write(byte[] b) {
        try {
            sOutputStream.write(b);
            Log.d(sTAG, "...Данные отправлены. " + new String(b));
        } catch (IOException e) {
            Log.d(sTAG, "...Ошибка отправки данных: " + e.getMessage() + "...");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//Запрет выключения экрана
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Инициализируем статические переменные
        setStaticVariable();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Задание настроек по умолчанию
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        // Получаем объект SharedPreferences
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Находим главный фрагмент приложения
        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        // Находим ImageView
        touchImageView = (TouchImageView) fragment.getView().findViewById(R.id.imageView);

        button_battery_camera = findViewById(R.id.battery_cam);
        button_battery_transmitter = findViewById(R.id.battery_transmitter);
        button_mode_camera = findViewById(R.id.mode_cam);
        button_battery_camera.setText("0%");
        button_battery_transmitter.setText("0%");

        // Получаем адресс и имя устройства Bluetooth
        adressBluetooth = getIntent().getStringExtra("Adress");
        //   nameBluetooth = getIntent().getStringExtra("Name");


        button_make_foto = (Button) fragment.getView().findViewById(R.id.buttonMakephoto);
        save = (Button) findViewById(R.id.buttonSavePhoto);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePictyre();

            }
        });


    }

    // Метод возвращает настройки приложения
    private void getSettings() {
        // Частота передачи
        String frequencytTansmission = sharedPreferences.getString(TRANSMISSION_FREGENCY, "111");
        Log.d(sTAG, "Частота передачи =  " + frequencytTansmission);
        this.frequencytTansmission = Integer.parseInt(frequencytTansmission);

        // Мощност передатчика
        String powerTransmitter = sharedPreferences.getString(TRANSMITTER_POWER, "121");
        Log.d(sTAG, "мощность передатчика =  " + powerTransmitter);
        this.powerTransmitter = Integer.parseInt(powerTransmitter);

        // Разрешение фото
        String resolution = sharedPreferences.getString(RESOLUTION_CAMFOTO, "6");
        Log.d(sTAG, "Разрешение фото =  " + resolution);
        this.resolution = Integer.parseInt(resolution);

        // Качество фото
        String qualityFoto = sharedPreferences.getString(QUALITY_CAMFOTO, "201");
        Log.d(sTAG, "Качество фото =  " + qualityFoto);
        this.qualityFoto = Integer.parseInt(qualityFoto);

        // ИК подсветка
        boolean infratedIllumination = sharedPreferences.getBoolean(INFRATED_ILLUMINATION, false);
        this.infratedIllumination = infratedIllumination ? 1 : 0;
        Log.d(sTAG, "ИК подсветка  =  " + this.infratedIllumination);

        // Фото по выстрелу
        boolean shootingFoto = sharedPreferences.getBoolean(SHOOTING_FOTO, false);
        Log.d(sTAG, "Фото по выстрелу  =  " + shootingFoto);
        // Задержка
        if (shootingFoto) {
            String delay = sharedPreferences.getString(DELAY, "3");
            this.shootingFoto = Integer.parseInt(delay);
            Log.d(sTAG, "Задержка  =  " + this.shootingFoto);
            String sensitivity = sharedPreferences.getString(SENSITIVITY, "3");
            this.sensitivity = Integer.parseInt(sensitivity);
            Log.d(sTAG, "Чувствительность  =  " + this.sensitivity);
        } else {
            this.shootingFoto = 0;
            this.sensitivity = 0;
            Log.d(sTAG, "Задержка  =  " + this.shootingFoto);
            Log.d(sTAG, "Чувствительность  =  " + this.shootingFoto);
        }

        // Бипер
        beep = 0;

        // Резерв
        reserve = 0;

        returnSettings = new byte[]{
                (byte) numberCam,
                (byte) numberCommandSettings,
                (byte) this.frequencytTansmission,
                (byte) this.powerTransmitter,
                (byte) this.resolution,
                (byte) this.qualityFoto,
                (byte) this.infratedIllumination,
                (byte) this.shootingFoto,
                (byte) sensitivity,
                (byte) reserve,
                (byte) 10, (byte) 13}; // настройки приложения


        // Режим съемки
        String shootingMode = sharedPreferences.getString(SHOOTING_MODE, "211");
        Log.d(sTAG, "Режим съемки =  " + shootingMode);
        this.shootingMode = Integer.parseInt(shootingMode);
        if (this.shootingFoto == 0) { // фото по выстрелу выключено
            if (this.shootingMode == 211) { // режим съемки непрерывный
                button_mode_camera.setText(R.string.loop); // Устанавливаем название режима на кнопку
            } else if (this.shootingMode == 212) { // режим съемки одиночный
                button_mode_camera.setText(R.string.ones);
            }
        } else {
            button_mode_camera.setText(R.string.shoot);
        }

    }

    // Метод задает обработчики кнопки фото в зависимости от выбранного режима меню
    public void setMethod() {
        if (this.shootingFoto == 0) { // фото по выстрелу выключено
            if (this.shootingMode == 211) {
                // режим съемки непрерывный
                button_make_foto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (stopFoto) {
                            stopFoto = false;
                            loop.cancel(true); // отменяем Loop
                            button_make_foto.setText("Отменяю");
                            button_make_foto.setClickable(false);
                            Log.d(sTAG, "Отменяем Loop ");
                        } else {
                            stopFoto = true;
                            if (loop == null) {
                                loop = new Loop();
                                loop.execute();
                                Log.d(sTAG, "Зпускаем Loop ");
                            } else if (loop.isCancelled()) {
                                loop = new Loop();
                                loop.execute();
                                Log.d(sTAG, "Зпускаем Loop ");
                            }
                        }

                    }
                });
            } else if (this.shootingMode == 212) {
                // режим съемки одиночный
                button_make_foto.setText(R.string.snapshot);
                button_make_foto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (downloadImageTask == null) {
                            downloadImageTask = new DownloadImageTask();
                            downloadImageTask.execute();
                        } else if (downloadImageTask.getStatus() == AsyncTask.Status.FINISHED) {
                            downloadImageTask = new DownloadImageTask();
                            downloadImageTask.execute();
                        }

                    }
                });

            }
        } else {
            // Фото по выстрелу
            button_make_foto.setText(R.string.shoot);
            button_make_foto.setClickable(false);
            if (loopShot == null) {
                loopShot = new LoopShot();
                loopShot.execute();
                Log.d(sTAG, "Зпускаем Loop по выстрелу ");
            } else if (loopShot.getStatus() == AsyncTask.Status.FINISHED) {
                loopShot = new LoopShot();
                loopShot.execute();
                Log.d(sTAG, "Зпускаем Loop по выстрелу loopShot.getStatus() == AsyncTask.Status.FINISHED ");
            }


        }
    }


    //Метод сохранения адреса устройства в настройках
//    public void seveAdress(String adressBluetooth) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(GlobalVeriable.BLUETOOTH_KEY, adressBluetooth);
//        editor.commit();
//        Log.d(sTAG, "Адресс сохранен  " + adressBluetooth);
//    }

    // Метод очищает поток от лишних байт
    private void resetInputStream(InputStream is) throws IOException {
        int n = 0;
        do {
            TimeUnit.SECONDS.toMillis(1000);
            n = is.available();
            Log.d(sTAG, "возможно прочитать  " + n + " байт");
            long l = is.skip(n);
            Log.d(sTAG, "прочитано  " + l + " байт");
        } while (n > 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == toolbar.getMenu().findItem(R.id.action_settings).getItemId()) {

            if (loop != null) {
                Log.d(sTAG, "onOptionsItemSelected");
                loop.cancel(true);
                stopFoto = false;
                sRestartGlobalVeriable();
            }
            if (loopShot != null) {
                loopShot.cancel(true);
                stopFoto = false;
            }


            Intent preferencesIntent = new Intent(this, SettingsActivity.class);
            startActivity(preferencesIntent);
        }
        return super.onOptionsItemSelected(item);

    }


    private void setStaticVariable() {

        // Переменные ключей
        SPEED_CAM = getApplication().getResources().getString(R.string.key_speed_cam);
        TRANSMISSION_FREGENCY = getApplication().getResources().getString(R.string.key_transmission_frequency);
        TRANSMITTER_POWER = getApplication().getResources().getString(R.string.key_transmitter_power);
        RESOLUTION_CAMFOTO = getApplication().getResources().getString(R.string.key_resolution_camFoto);
        QUALITY_CAMFOTO = getApplication().getResources().getString(R.string.key_quality_camFoto);
        SHOOTING_MODE = getApplication().getResources().getString(R.string.key_shooting_mode);
        SHOOTING_FOTO = getApplication().getResources().getString(R.string.key_shooting_foto);
        DELAY = getApplication().getResources().getString(R.string.key_delay);
        SENSITIVITY = getApplication().getResources().getString(R.string.key_sensitivity);
        INFRATED_ILLUMINATION = getApplication().getResources().getString(R.string.key_infrared_illumination);
        SAVE_POWER = getApplication().getResources().getString(R.string.key_save_power);

        // Строковые массивы текстовых значений
        NAME_SPEED_CAM = getApplication().getResources().getStringArray(R.array.name_speed);
        NAME_RESOLUTION_CAMFOTO = getApplication().getResources().getStringArray(R.array.name_resolution);
        NAME_QUALITY_CAMFOTO = getApplication().getResources().getStringArray(R.array.name_qualityFoto);
        NAME_TRANSMISSION_FREGENCY = getApplication().getResources().getStringArray(R.array.name_freg);
        NAME_TRANSMITTER_POWER = getApplication().getResources().getStringArray(R.array.name_power);
        NAME_SHOOTING_FOTO = getApplication().getResources().getStringArray(R.array.name_shooting_foto);
        NAME_SHOOTING_MODE = getApplication().getResources().getStringArray(R.array.name_shooting_mode);
        NAME_DELAY = getApplication().getResources().getStringArray(R.array.name_delay);
        NAME_SENSITIVITY = getApplication().getResources().getStringArray(R.array.name_sensitivity);
        NAME_INFRATED_ILLUMINATION = getApplication().getResources().getStringArray(R.array.name_infrared_illumination);
        NAME_SAVE_POWER = getApplication().getResources().getStringArray(R.array.name_save_power);

        // Строковые массивы значений API
        VALUE_SPEED_CAM = getApplication().getResources().getStringArray(R.array.value_speed);
        VALUE_RESOLUTION_CAMFOTO = getApplication().getResources().getStringArray(R.array.value_resolution);
        VALUE_QUALITY_CAMFOTO = getApplication().getResources().getStringArray(R.array.value_qualityFoto);
        VALUE_TRANSMISSION_FREGENCY = getApplication().getResources().getStringArray(R.array.value_freg);
        VALUE_TRANSMITTER_POWER = getApplication().getResources().getStringArray(R.array.value_power);
        VALUE_SHOOTING_MODE = getApplication().getResources().getStringArray(R.array.value_shooting_mode);
        VALUE_SHOOTING_FOTO = getApplication().getResources().getStringArray(R.array.value_shooting_foto);
        VALUE_DELAY = getApplication().getResources().getStringArray(R.array.value_delay);
        VALUE_SENSITIVITY = getApplication().getResources().getStringArray(R.array.value_sensitivity);
        VALUE_INFRATED_ILLUMINATION = getApplication().getResources().getStringArray(R.array.value_infrared_illumination);
        VALUE_SAVE_POWER = getApplication().getResources().getStringArray(R.array.value_save_power);

    }


    /// Класс соединения Bluetooth в отдельном потоке ///////
    public class BTConnectThread extends AsyncTask<Void, Void, Void> {


        public void seveAdress(String adressBluetooth) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(GlobalVeriable.BLUETOOTH_KEY, adressBluetooth);
            editor.commit();
            Log.d(sTAG, "Адресс сохранен  " + adressBluetooth);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (sBluetoothSocket != null) {
                Log.d(sTAG, "BluetoothSocket ! = null ");
                // Toast.makeText(getApplicationContext(), "Соединение установлено",  Toast.LENGTH_SHORT).show();

                try {
                    sBluetoothSocket.connect();
                    sInputStream = sBluetoothSocket.getInputStream();
                    sOutputStream = sBluetoothSocket.getOutputStream();


                } catch (IOException e) {
                    Log.d(sTAG, "Не удается создать потоки");
                    e.printStackTrace();
                }

            } else {


                Log.d(sTAG, "BluetoothSocket = null ");
                Log.d(sTAG, "Bluetooth adress " + adressBluetooth);

                // Подключаемя к удаленному устройству по заданному адрессу
                // Используем статическую переменную mbluetoothAdapter
                sBluetoothDevice = mbluetoothAdapter.getRemoteDevice(adressBluetooth);


                try {
                    // Создаем сокет подключения
                    sBluetoothSocket = sBluetoothDevice.createRfcommSocketToServiceRecord(myUUID);

                    Log.d(sTAG, "Создаем сокет");

                    try {

                        sBluetoothSocket.connect();

                        if (sBluetoothSocket.isConnected()) {
                            Log.d(sTAG, "Сокет подключен");
                            try {

                                sInputStream = sBluetoothSocket.getInputStream();
                                sOutputStream = sBluetoothSocket.getOutputStream();

                            } catch (IOException e) {
                                Log.d(sTAG, "Не удается создать потоки");
                                e.printStackTrace();
                                finish();
                            }

                            seveAdress(adressBluetooth);
                            Log.d(sTAG, "...Соединение установлено и готово к передачи данных...");


                            /** Здесь необходимо отправить настройки приложения*/

                            if (returnSetting == null) {
                                Log.d(sTAG, "Создали returnSetting");
                                returnSetting = new ReturnSettings();
                                returnSetting.execute();
                            } else if (returnSetting.getStatus() == AsyncTask.Status.FINISHED) {
                                Log.d(sTAG, "returnSetting.getStatus() == AsyncTask.Status.FINISHED");
                                returnSetting = new ReturnSettings();
                                returnSetting.execute();
                            }


                        }


                    } catch (IOException e) {
                        Log.d(sTAG, "Не удалось подключиться к socket");
                        e.printStackTrace();
                        finish();
                    }

                } catch (IOException e) {
                    Log.d(sTAG, "Не удалось создать socket подключения");
                    e.printStackTrace();
                    finish();
                }

            }

            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (exit) {
            super.finish();
            Log.d(sTAG, "Сработал метод finish");
        }
        Log.d(sTAG, "onResume");
    }

    @Override
    protected void onStart() {
        if (mbluetoothAdapter.isEnabled()) {
            Log.d(sTAG, "onStart");
            Log.d(sTAG, "Регистрируем приемник");
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            registerReceiver(mBroadCastReceiver4, filter);
            getSettings();
            if (btConnectThread == null) {
                btConnectThread = new BTConnectThread();
                btConnectThread.execute();
            } else if (btConnectThread.getStatus() == AsyncTask.Status.FINISHED) {
                Log.d(sTAG, "btConnectThread.getStatus() == AsyncTask.Status.FINISHED");
                btConnectThread = new BTConnectThread();
                btConnectThread.execute();
            }


        } else {
            exit = true;
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mBroadCastReceiver4);
        sRestartGlobalVeriable();
        Log.d(sTAG, "onStop");
        super.onStop();
    }


    @Override
    protected void onPause() {
        Log.d(sTAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(sTAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d(sTAG, "onRestart");
        super.onRestart();
    }


    class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            button_make_foto.setText("Делаю");
            button_make_foto.setClickable(false);
        }

        @SuppressLint("NewApi")
        @Override
        protected Bitmap doInBackground(Void... voids) {
            button_make_foto.setClickable(false);
            write(makeFoto);

            byte number_cam;
            byte command;
            byte command_N;
            byte command_R;
            Bitmap bmp;

            try {

                // Добавить сброс камеры через 1 сек

                number_cam = (byte) sInputStream.read();                   // 1. Номер камеры
                Log.d(sTAG, "Номер камеры " + number_cam);
                command = (byte) sInputStream.read();                      // 2. Номер команды (команда 12)
                Log.d(sTAG, "Номер команды " + command);
                if (command == 12) {
                    int raz1;
                    int raz2;
                    int raz3;
                    int size_foto;
                    raz1 = sInputStream.read();                          // 6. 1-й байт размера
                    raz2 = sInputStream.read();                          // 7. 2-й байт размера
                    raz3 = sInputStream.read();                          // 7. 2-й байт размера
//                    energy_camera = sInputStream.read();                     // 7. Заряд камеры
//                    energy_transmiter = sInputStream.read();                 // 8. Заряд передатчика
                    Log.d(sTAG, "Байт 1 размера  " + raz1);
                    Log.d(sTAG, "Байт 2 размера  " + raz2);
                    Log.d(sTAG, "Байт 3 размера  " + raz2);
                    size_foto = raz1 * 65536 + raz2 * 256 + raz3;
                    Log.d(sTAG, "Размер фото " + size_foto);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    int count = 0;
                    boolean flagEndOfJpg = true;
                    int new_temp = 0;
                    int previous_temp = 0;

                    for (int i = 0; (i < size_foto) && (flagEndOfJpg); i++) {
                        new_temp = sInputStream.read();
                        byteArrayOutputStream.write(new_temp);
                        if ((previous_temp == 0xFF) && (new_temp == 0xD9)) {
                            flagEndOfJpg = false;
                            Log.d(sTAG, "конец фото ");
                        }
                        previous_temp = new_temp;
                        count++;
                    }
                    Log.d(sTAG, "Прочитано " + count);

                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    if (bmp != null) {
                        bitmap = bmp;
                        String config = bitmap.getConfig().toString();
                        Log.d(sTAG, "Конфигурация " + config);
                    } else {
                        resetInputStream(sInputStream);
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);
                    }

//                    while (count < size_foto) {
//                        byteArrayOutputStream.write(sInputStream.read());
//                        count++;
//                        // проверять
//                    }
//                    byte[] byteArray = byteArrayOutputStream.toByteArray();
//                    Log.d(sTAG, "Массив " + byteArray.length);
//                    Log.d(sTAG, "1 " + (byteArray[0]));
//                    Log.d(sTAG, "2 " + (byteArray[1]));
//                    Log.d(sTAG, "4 " + (byteArray[byteArray.length - 2]));
//                    Log.d(sTAG, "3 " + (byteArray[byteArray.length - 1]));
//
//                    if (byteArray[0] == (byte) 255 & (int) byteArray[1] == (byte) 216 & byteArray[byteArray.length - 2] == (byte) 255 & byteArray[byteArray.length - 1] == (byte) 217) {
//                        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                        /// Если не удалось расшифровать картинку задаем ее по умолчанию
//                    } else {
//                        resetInputStream(sInputStream);
//                        if (bitmap == null) {
//                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);
//                        }
//
//                    }

                } else {
                    resetInputStream(sInputStream);
//

                }

            } catch (Exception e) {
                try {
                    resetInputStream(sInputStream);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Log.d(sTAG, "Ошибка приема фото");
                Log.d(sTAG, e.getMessage());
                e.printStackTrace();

                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);
                }

            }

            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap result) {

            if (result == null) {
                result = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);
                result = getBitmap(result, 3); // Увеличиваем изображение в 3 раза
                touchImageView.setImageBitmap(result);
                button_make_foto.setClickable(true);
                button_make_foto.setText(R.string.snapshot);
            } else {
                result = getBitmap(result, 3); // Увеличиваем изображение в 3 раза
                touchImageView.setImageBitmap(result);
                button_make_foto.setClickable(true);
                button_make_foto.setText(R.string.snapshot);
            }


        }

    }


    // Класс отправляет запрос на настройки камере и обрабатывает ответ
    class ReturnSettings extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            button_make_foto.setClickable(false);

        }

        @Override
        protected Integer doInBackground(Void... voids) {
            countSettings = 0;
            byte number_cam;
            int status = 0;


            for (int i = 0; i < 5; i++) {
                write(returnSettings);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    if (sInputStream.available() > 0) {
                        number_cam = (byte) sInputStream.read();                   // 1. Номер камеры
                        Log.d(sTAG, "Номер камеры " + number_cam);
                        commandSettings = (byte) sInputStream.read();                      // 2. Номер команды (команда 11)
                        Log.d(sTAG, "Номер команды " + commandSettings);
                        if (commandSettings == numberCommandSettings) {
                            energy_camera = sInputStream.read();
                            Log.d(sTAG, "заряд камеры " + energy_camera);
                            energy_transmiter = sInputStream.read();
                            Log.d(sTAG, "заряд передатчика " + energy_transmiter);
                            commandSettings = 0;
                            status = 1;
                            break;
                        }
                    }
                } catch (IOException e) {
                    status = 0;
                    e.printStackTrace();
                }
            }

            return status;
        }

        @Override
        protected void onPostExecute(Integer i) {
            if (i == 1) {
                setPower(energy_camera, energy_transmiter);
                button_make_foto.setClickable(true);
                setMethod();


            } else {
                setPowerNull();
            }
        }

    }

    class Loop extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            button_make_foto.setText("Отменить");
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            if (isCancelled()) {
                try {
                    resetInputStream(sInputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }


            write(makeFoto);
            byte number_cam;
            byte command;

            try {

                if (isCancelled()) {
                    resetInputStream(sInputStream);
                    return null;
                }

                number_cam = (byte) sInputStream.read();                   // 1. Номер камеры
                Log.d(sTAG, "Номер камеры " + number_cam);
                command = (byte) sInputStream.read();                      // 2. Номер комаенды (команда 11)
                Log.d(sTAG, "Номер команды " + command);
                if (command == 12) {
                    Bitmap bmp;
                    int raz1;
                    int raz2;
                    int raz3;
                    int size_foto;
                    raz1 = sInputStream.read();                          // 6. 1-й байт размера
                    raz2 = sInputStream.read();                          // 7. 2-й байт размера
                    raz3 = sInputStream.read();                          // 7. 2-й байт размера
//                    energy_camera = sInputStream.read();                     // 7. Заряд камеры
//                    energy_transmiter = sInputStream.read();                 // 8. Заряд передатчика
                    Log.d(sTAG, "Байт 1 размера  " + raz1);
                    Log.d(sTAG, "Байт 2 размера  " + raz2);
                    Log.d(sTAG, "Байт 3 размера  " + raz2);
                    size_foto = raz1 * 65536 + raz2 * 256 + raz3;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    int count = 0;
                    boolean flagEndOfJpg = true;
                    int new_temp = 0;
                    int previous_temp = 0;

                    for (int i = 0; (i < size_foto) && (flagEndOfJpg); i++) {
                        new_temp = sInputStream.read();
                        byteArrayOutputStream.write(new_temp);
                        if ((previous_temp == 0xFF) && (new_temp == 0xD9)) {
                            flagEndOfJpg = false;
                            Log.d(sTAG, "конец фото ");
                        }
                        previous_temp = new_temp;
                        count++;
                    }
                    Log.d(sTAG, "Прочитано " + count);

                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    if (bmp != null) {
                        bitmap = bmp;
                    } else {
                        resetInputStream(sInputStream);
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);
                    }
//                    while (count < size_foto) {
//                        if (isCancelled()) {
//                            resetInputStream(sInputStream);
//                            return null;
//                        }
//                        byteArrayOutputStream.write(sInputStream.read());
//                        count++;
//
//                    }
//                    byte[] byteArray = byteArrayOutputStream.toByteArray();
//                    Log.d(sTAG, "Массив " + byteArray.length);
//                    Log.d(sTAG, "1 " + (byteArray[0]));
//                    Log.d(sTAG, "2 " + (byteArray[1]));
//                    Log.d(sTAG, "4 " + (byteArray[byteArray.length - 2]));
//                    Log.d(sTAG, "3 " + (byteArray[byteArray.length - 1]));
//
//                    if (byteArray[0] == (byte) 255 & (int) byteArray[1] == (byte) 216 & byteArray[byteArray.length - 2] == (byte) 255 & byteArray[byteArray.length - 1] == (byte) 217) {
//                        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                        /// Если не удалось расшифровать картинку задаем ее по умолчанию
//                    } else {
//                        resetInputStream(sInputStream);
//                        //  bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);
//
//                    }

                } else {
                    resetInputStream(sInputStream);
//                    sInputStream.close();
//                    sInputStream.close();
//                    sBluetoothSocket.close();
                    //  bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);
                }

            } catch (IOException e) {
                try {
                    resetInputStream(sInputStream);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Log.d(sTAG, "Ошибка приема фото");
                Log.d(sTAG, e.getMessage());
                e.printStackTrace();
                //  bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);

            }

            return bitmap;
        }

        @Override
        protected void onCancelled() {
            Log.d(sTAG, "onCancelled()");
            try {
                button_make_foto.setText(R.string.snapshot);
                button_make_foto.setClickable(true);
                resetInputStream(sInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                Log.d(sTAG, "result == null");
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);
                result = getBitmap(bitmap, 3); // Увеличиваем изображение в 3 раза
                touchImageView.setImageBitmap(result);
                if (stopFoto) {
                    loop = new Loop();
                    loop.execute();
                    return;
                } else {
                    return;
                }
            }
            result = getBitmap(result, 3); // Увеличиваем изображение в 3 раза
            touchImageView.setImageBitmap(result);
            if (stopFoto) {
                loop = new Loop();
                loop.execute();
                return;
            }

        }

    }

    //// Цикл фото по выстрелу
    class LoopShot extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            button_make_foto.setText("Отмена");
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {


            if (isCancelled()) {
                try {
                    resetInputStream(sInputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            byte number_cam;
            byte command;
            try {

                if (isCancelled()) {
                    resetInputStream(sInputStream);
                    return null;
                }

                number_cam = (byte) sInputStream.read();                   // 1. Номер камеры
                Log.d(sTAG, "Номер камеры " + number_cam);
                command = (byte) sInputStream.read();                      // 2. Номер комаенды (команда 11)
                Log.d(sTAG, "Номер команды " + command);
                if (command == 12) {
//
                    int raz1;
                    int raz2;
                    int size_foto;
                    raz1 = sInputStream.read();                          // 6. 1-й байт размера
                    raz2 = sInputStream.read();                          // 7. 2-й байт размера
//                    energy_camera = sInputStream.read();                     // 7. Заряд камеры
//                    energy_transmiter = sInputStream.read();                 // 8. Заряд передатчика
                    Log.d(sTAG, "Байт 1 размера  " + raz1);
                    Log.d(sTAG, "Байт 2 размера  " + raz2);
                    size_foto = raz1 * 256 + raz2;
                    Log.d(sTAG, "Размер фото " + size_foto);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    int count = 0;
                    boolean flagEndOfJpg = true;
                    int new_temp = 0;
                    int previous_temp = 0;

                    for (int i = 0; (i < size_foto) && (flagEndOfJpg); i++) {
                        new_temp = sInputStream.read();
                        byteArrayOutputStream.write(new_temp);
                        if ((previous_temp == 0xFF) && (new_temp == 0xD9)) {
                            flagEndOfJpg = false;
                            Log.d(sTAG, "конец фото ");
                        }
                        previous_temp = new_temp;
                        count++;
                    }
                    Log.d(sTAG, "Прочитано " + count);

                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


                } else {
                    resetInputStream(sInputStream);

                }

            } catch (IOException e) {
                try {
                    resetInputStream(sInputStream);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Log.d(sTAG, "Ошибка приема фото");
                Log.d(sTAG, e.getMessage());
                e.printStackTrace();
                //  bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);

            }

            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap result) {

            if (result != null) {
                result = getBitmap(result, 3); // Увеличиваем изображение в 3 раза
                touchImageView.setImageBitmap(result);
                loopShot = new LoopShot();
                loopShot.execute();
            } else {
                touchImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.semarobo));
                loopShot = new LoopShot();
                loopShot.execute();
            }
            Log.d(sTAG, " LoopShot onPostExecute отработал ");
            return;
        }


    }


}



