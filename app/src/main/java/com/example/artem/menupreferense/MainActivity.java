package com.example.artem.menupreferense;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import android.widget.TabHost;
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
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.artem.menupreferense.FirstActivity.mbluetoothAdapter;
import static com.example.artem.menupreferense.GlobalVeriable.sBluetoothDevice;
import static com.example.artem.menupreferense.GlobalVeriable.sBluetoothSocket;
import static com.example.artem.menupreferense.GlobalVeriable.sInputStream;
import static com.example.artem.menupreferense.GlobalVeriable.sOutputStream;
import static com.example.artem.menupreferense.GlobalVeriable.sRestartGlobalVeriable;
import static com.example.artem.menupreferense.GlobalVeriable.sTAG;
import static com.example.artem.menupreferense.StaticValue.DELAY;
import static com.example.artem.menupreferense.StaticValue.INFRATED_ILLUMINATION;
import static com.example.artem.menupreferense.StaticValue.NAME_DELAY;
import static com.example.artem.menupreferense.StaticValue.NAME_INFRATED_ILLUMINATION;
import static com.example.artem.menupreferense.StaticValue.NAME_QUALITY_CAMFOTO;
import static com.example.artem.menupreferense.StaticValue.NAME_RESOLUTION_CAMFOTO;
import static com.example.artem.menupreferense.StaticValue.NAME_SAVE_POWER;
import static com.example.artem.menupreferense.StaticValue.NAME_SENSITIVITY;
import static com.example.artem.menupreferense.StaticValue.NAME_SHOOTING_FOTO;
import static com.example.artem.menupreferense.StaticValue.NAME_SHOOTING_MODE;
import static com.example.artem.menupreferense.StaticValue.NAME_SPEED_CAM;
import static com.example.artem.menupreferense.StaticValue.NAME_TRANSMISSION_FREGENCY;
import static com.example.artem.menupreferense.StaticValue.NAME_TRANSMITTER_POWER;
import static com.example.artem.menupreferense.StaticValue.QUALITY_CAMFOTO;
import static com.example.artem.menupreferense.StaticValue.RESOLUTION_CAMFOTO;
import static com.example.artem.menupreferense.StaticValue.SAVE_POWER;
import static com.example.artem.menupreferense.StaticValue.SENSITIVITY;
import static com.example.artem.menupreferense.StaticValue.SHOOTING_FOTO;
import static com.example.artem.menupreferense.StaticValue.SHOOTING_MODE;
import static com.example.artem.menupreferense.StaticValue.SPEED_CAM;
import static com.example.artem.menupreferense.StaticValue.TRANSMISSION_FREGENCY;
import static com.example.artem.menupreferense.StaticValue.TRANSMITTER_POWER;
import static com.example.artem.menupreferense.StaticValue.VALUE_DELAY;
import static com.example.artem.menupreferense.StaticValue.VALUE_INFRATED_ILLUMINATION;
import static com.example.artem.menupreferense.StaticValue.VALUE_QUALITY_CAMFOTO;
import static com.example.artem.menupreferense.StaticValue.VALUE_RESOLUTION_CAMFOTO;
import static com.example.artem.menupreferense.StaticValue.VALUE_SAVE_POWER;
import static com.example.artem.menupreferense.StaticValue.VALUE_SENSITIVITY;
import static com.example.artem.menupreferense.StaticValue.VALUE_SHOOTING_FOTO;
import static com.example.artem.menupreferense.StaticValue.VALUE_SHOOTING_MODE;
import static com.example.artem.menupreferense.StaticValue.VALUE_SPEED_CAM;
import static com.example.artem.menupreferense.StaticValue.VALUE_TRANSMISSION_FREGENCY;
import static com.example.artem.menupreferense.StaticValue.VALUE_TRANSMITTER_POWER;
import static com.example.artem.menupreferense.StaticValue.sharedPreferences;

public class MainActivity extends AppCompatActivity {


    TextView tvFragment;
    android.support.v4.app.Fragment fragment;
    Toolbar toolbar;
    Menu menu;
    Button button_battery_camera;
    Button button_battery_transmitter;
    Button button_mode_camera;
    TouchImageView touchImageView;
    ReturnSettings returnSetting;
    BTConnectThread btConnectThread;
    ProgressDialog progressDialog;
    TabLayout tabLayout;
    TabHost tabHost;


    DownloadImageTask downloadImageTask;
    Loop loop;
    LoopShot loopShot;
    SavePhoto mSavePhoto;

    String adressBluetooth;
    String nameBluetooth;
    Context mContext;
    static Bitmap bitmap;
    boolean error;

    int quantity_cam;
    int number_cam;
    int energy_camera;
    int energy_transmiter;

    // данные для отправки камере
    int mNumberCam = 0; // номер камеры 0
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
    //  static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static Button button_make_foto;
    private Button button_save_photo;
    private ProgressBar mProgressBar;
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
                    Log.e(sTAG, "ACTION_ACL_DISCONNECTED ");
                    Toast.makeText(context, "Потеряна связь с устройством", Toast.LENGTH_LONG).show();

                    finish();
                    break;
            }


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


    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkIfAlreadyhavePermission() {


        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if ((checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {

            //show dialog to ask permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            return true;
        } else {
            return false;
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
            Log.e(sTAG, "...Данные отправлены. " + new String(b));
        } catch (IOException e) {
            Log.e(sTAG, "...Ошибка отправки данных: " + e.getMessage() + "...");
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


        button_make_foto = (Button) findViewById(R.id.buttonMakephoto);
        button_make_foto.setClickable(false);

        button_save_photo = (Button) findViewById(R.id.buttonSavePhoto);
        button_save_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int MyVersion = Build.VERSION.SDK_INT;
                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    checkIfAlreadyhavePermission();
                }


                //   savePicture();
                if (bitmap != null) {
                    if (mSavePhoto == null) {
                        mSavePhoto = new SavePhoto();
                        mSavePhoto.execute();
                    } else if (mSavePhoto.getStatus() == AsyncTask.Status.FINISHED) {
                        Log.e(sTAG, "mSavePhoto.getStatus() == AsyncTask.Status.FINISHED");
                        mSavePhoto = new SavePhoto();
                        mSavePhoto.execute();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Нет данных для сохранения",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        mProgressBar = findViewById(R.id.progressBar);

        tabLayout = findViewById(R.id.tabLayout);


    }

    public void setNumberCamera(int quantityCam, int numberCam) {
        tabLayout.removeAllTabs(); // Удаляем все табы
        tabLayout.clearOnTabSelectedListeners(); // Удаляем ранее созданные слушатели
        for (int i = 0; i < quantityCam; i++) {
            tabLayout.addTab(tabLayout.newTab().setText("К " + String.valueOf(i + 1)));
        }
        tabLayout.getTabAt(numberCam - 1).select(); //активная вкладка с номером камеры

        tabLayout.setTabTextColors(getResources().getColor(R.color.green), getResources().getColor(R.color.red));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                mNumberCam = tab.getPosition() + 1;
                getSettings();

                cancelFotos();
                sRestartGlobalVeriable();
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                btConnect();

                Toast.makeText(getApplicationContext(),
                        String.valueOf(tab.getPosition() + 1),
                        Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Метод возвращает настройки приложения
     */
    private void getSettings() {
        // Частота передачи
        String frequencytTansmission = sharedPreferences.getString(TRANSMISSION_FREGENCY, "111");
        Log.e(sTAG, "Частота передачи =  " + frequencytTansmission);
        this.frequencytTansmission = Integer.parseInt(frequencytTansmission);

        // Мощност передатчика
        String powerTransmitter = sharedPreferences.getString(TRANSMITTER_POWER, "121");
        Log.e(sTAG, "мощность передатчика =  " + powerTransmitter);
        this.powerTransmitter = Integer.parseInt(powerTransmitter);

        // Разрешение фото
        String resolution = sharedPreferences.getString(RESOLUTION_CAMFOTO, "6");
        Log.e(sTAG, "Разрешение фото =  " + resolution);
        this.resolution = Integer.parseInt(resolution);

        // Качество фото
        String qualityFoto = sharedPreferences.getString(QUALITY_CAMFOTO, "201");
        Log.e(sTAG, "Качество фото =  " + qualityFoto);
        this.qualityFoto = Integer.parseInt(qualityFoto);

        // ИК подсветка
        boolean infratedIllumination = sharedPreferences.getBoolean(INFRATED_ILLUMINATION, false);
        this.infratedIllumination = infratedIllumination ? 1 : 0;
        Log.e(sTAG, "ИК подсветка  =  " + this.infratedIllumination);

        // Фото по выстрелу
        boolean shootingFoto = sharedPreferences.getBoolean(SHOOTING_FOTO, false);
        Log.e(sTAG, "Фото по выстрелу  =  " + shootingFoto);
        // Задержка
        if (shootingFoto) {
            String delay = sharedPreferences.getString(DELAY, "3");
            this.shootingFoto = Integer.parseInt(delay);
            Log.e(sTAG, "Задержка  =  " + this.shootingFoto);
            String sensitivity = sharedPreferences.getString(SENSITIVITY, "3");
            this.sensitivity = Integer.parseInt(sensitivity);
            Log.e(sTAG, "Чувствительность  =  " + this.sensitivity);
        } else {
            this.shootingFoto = 0;
            this.sensitivity = 0;
            Log.e(sTAG, "Задержка  =  " + this.shootingFoto);
            Log.e(sTAG, "Чувствительность  =  " + this.shootingFoto);
        }

        // Бипер
        beep = 0;

        // Резерв
        reserve = 0;

        returnSettings = new byte[]{
                (byte) mNumberCam, // 0
                (byte) numberCommandSettings, //1
                (byte) this.frequencytTansmission, //2
                (byte) this.powerTransmitter, //3
                (byte) this.resolution, // 4
                (byte) this.qualityFoto,  //5
                (byte) this.infratedIllumination, // 6
                (byte) this.shootingFoto, // 7
                (byte) sensitivity, //8
                (byte) reserve, // 9
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

    /**
     * Метод задает обработчики кнопки фото в зависимости от выбранного режима меню
     */
    public void setMethod() {
        if (this.shootingFoto == 0) { // фото по выстрелу выключено
            if (this.shootingMode == 211) {
                // режим съемки непрерывный
                button_make_foto.setText(R.string.snapshot);
                button_make_foto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (loop == null) {
                            loop = new Loop();
                            loop.execute();
                            Log.e(sTAG, "loop = new Loop();");
                            Log.e(sTAG, "Зпускаем Loop ");
                        } else if (loop.getStatus() == AsyncTask.Status.FINISHED) {
                            loop = new Loop();
                            loop.execute();
                            Log.e(sTAG, "loop.getStatus() == AsyncTask.Status.FINISHED");
                            Log.e(sTAG, "Зпускаем Loop ");
                        } else if ((loop.getStatus() == AsyncTask.Status.RUNNING || loop.getStatus() == AsyncTask.Status.RUNNING) && !loop.isCancelled()) {
                            loop.cancel(true);
                            Log.e(sTAG, "loop.cancel(true);");
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
                Log.e(sTAG, "Зпускаем Loop по выстрелу ");
            } else if (loopShot.getStatus() == AsyncTask.Status.FINISHED) {
                loopShot = new LoopShot();
                loopShot.execute();
                Log.e(sTAG, "Зпускаем Loop по выстрелу loopShot.getStatus() == AsyncTask.Status.FINISHED ");
            }


        }
    }


    /**
     * Метод очищает поток от лишних байт
     */
    private void resetInputStream(InputStream is) throws IOException {

        int n = 0;
        do {
            // TimeUnit.SECONDS.toMillis(1000);
            n = is.available();
            Log.e(sTAG, "возможно прочитать  " + n + " байт");
            long l = is.skip(n);
            Log.e(sTAG, "прочитано  " + l + " байт");
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

            cancelFotos();
            sRestartGlobalVeriable();


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


    /**
     * Класс соединения Bluetooth в отдельном потоке
     */
    public class BTConnectThread extends AsyncTask<Void, Void, Integer> {


        Integer result;

        public void seveAdress(String adressBluetooth) {
            // Сохраняем адресс устройства BLUETOOTH
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(GlobalVeriable.BLUETOOTH_KEY, adressBluetooth);
            editor.commit();
            Log.e(sTAG, "Адресс сохранен  " + adressBluetooth);
        }


        @Override
        protected void onPreExecute() {
            button_make_foto.setEnabled(false);
            //showProgress();

        }

        @SuppressLint("NewApi")
        @Override
        protected Integer doInBackground(Void... voids) {

            if (sBluetoothSocket != null) {
                Log.e(sTAG, "BluetoothSocket ! = null ");
                // Toast.makeText(getApplicationContext(), "Соединение установлено",  Toast.LENGTH_SHORT).show();

                try {
                    if (sBluetoothSocket.isConnected()) {
                        Log.e(sTAG, "sBluetoothSocket.isConnected() == true ");
                        sInputStream = sBluetoothSocket.getInputStream();
                        sOutputStream = sBluetoothSocket.getOutputStream();
                        result = 1;

                    } else {
                        Log.e(sTAG, "sBluetoothSocket.isConnected() == false ");
                        sBluetoothSocket.connect();
                        Log.e(sTAG, "sBluetoothSocket.isConnected() = " + sBluetoothSocket.isConnected());
                        sInputStream = sBluetoothSocket.getInputStream();
                        sOutputStream = sBluetoothSocket.getOutputStream();
                        result = 1;

                    }

                } catch (IOException e) {
                    Log.e(sTAG, "Не удается создать потоки");
                    e.printStackTrace();
                }

            } else {


                Log.e(sTAG, "BluetoothSocket = null ");
                Log.e(sTAG, "Bluetooth adress " + adressBluetooth);

                // Подключаемcя к удаленному устройству по заданному адрессу
                // Используем статическую переменную mbluetoothAdapter
                sBluetoothDevice = mbluetoothAdapter.getRemoteDevice(adressBluetooth);


                try {
                    sBluetoothSocket = sBluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
                    Log.d(sTAG, "Создаем сокет");
                    try {
                        sBluetoothSocket.connect();
                        Log.e(sTAG, "sBluetoothSocket =  " + sBluetoothSocket.toString());
                        Log.e(sTAG, "sBluetoothSocket.connect()  =  " + sBluetoothSocket.isConnected());


                        if (sBluetoothSocket.isConnected()) {
                            Log.e(sTAG, "Сокет подключен");
                            try {

                                sInputStream = sBluetoothSocket.getInputStream();
                                sOutputStream = sBluetoothSocket.getOutputStream();

                            } catch (IOException e) {
                                Log.e(sTAG, "Не удается создать потоки");
                                e.printStackTrace();
                                finish();
                            }

                            seveAdress(adressBluetooth);
                            Log.e(sTAG, "...Соединение установлено и готово к передачи данных...");

                            /** Здесь необходимо отправить настройки приложения*/
                            result = 1;
                        }

                    } catch (IOException e) {
                        Log.e(sTAG, "Не удалось подключиться к socket");
                        e.printStackTrace();
                        finish();
                    }

                } catch (IOException e) {
                    Log.e(sTAG, "Не удалось создать socket подключения");
                    e.printStackTrace();
                    finish();
                }

            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != null) {
                returnSettingsMethod();

            }

        }
    }

    public void returnSettingsMethod() {
        if (returnSetting == null) {
            Log.e(sTAG, "Создали returnSetting");
            returnSetting = new ReturnSettings();
            returnSetting.execute();
        } else if (returnSetting.getStatus() == AsyncTask.Status.FINISHED) {
            Log.e(sTAG, "returnSetting.getStatus() == AsyncTask.Status.FINISHED");
            returnSetting = new ReturnSettings();
            returnSetting.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (exit) {
            super.finish();
            Log.e(sTAG, "Сработал метод finish");
        }
        Log.e(sTAG, "onResume");
    }

    @Override
    protected void onStart() {

        if (mbluetoothAdapter.isEnabled()) {
            Log.e(sTAG, "onStart");

            Log.e(sTAG, "Регистрируем приемник");
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            registerReceiver(mBroadCastReceiver4, filter);

            // Возварщаем последние настройки
            getSettings();
            // Устанавливаем цвет кнопки
            //    button_make_foto.setBackgroundColor(getResources().getColor(R.color.green));
            btConnect();

            error = false;
        } else {
            exit = true;
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        cancelFotos();
        sRestartGlobalVeriable();
        unregisterReceiver(mBroadCastReceiver4);
        error = true;

        Log.e(sTAG, "onStop");
        super.onStop();
    }


    @Override
    protected void onPause() {
        Log.e(sTAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        Log.d(sTAG, "onDestroy");
//        error = true;
//        sRestartGlobalVeriable();
        Log.e(sTAG, "onStop");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.e(sTAG, "onRestart");
        super.onRestart();
    }


    /**
     * Класс отправляет запрос на настройки камере и обрабатывает ответ
     */
    class ReturnSettings extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            button_make_foto.setEnabled(false);
            //   button_make_foto.setClickable(false);


        }

        @Override
        protected Integer doInBackground(Void... voids) {

            countSettings = 0;
            int status = 0;
            for (int i = 0; i < 7; i++) {
                write(returnSettings);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    if (sInputStream.available() > 0) {
                        quantity_cam = sInputStream.read();                   // 1. Количество камер
                        Log.e(sTAG, "Количество камер " + quantity_cam);
                        number_cam = sInputStream.read();                   // 1. Номер камеры
                        Log.e(sTAG, "Номер камеры " + number_cam);
                        commandSettings = (byte) sInputStream.read();                      // 3. Номер команды (команда 11)
                        Log.e(sTAG, "Номер команды " + commandSettings);
                        if (commandSettings == numberCommandSettings) {
                            energy_camera = sInputStream.read();
                            Log.d(sTAG, "заряд камеры " + energy_camera);
                            energy_transmiter = sInputStream.read();
                            Log.d(sTAG, "заряд передатчика " + energy_transmiter);
                            mProgressBar.setProgress(0);
                            commandSettings = 0;
                            status = 1;
                            break;
                        } else {
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            long k = sInputStream.skip(100000);
                            Log.e(sTAG, "Пропущено  " + k);
                            resetInputStream(sInputStream);
                            // continue;
                        }
                    } else {

                        //  continue;
//                        try {
//                            TimeUnit.SECONDS.sleep(1);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
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
                button_make_foto.setEnabled(true);
                button_make_foto.setClickable(true);
                //   button_make_foto.setBackgroundColor(getResources().getColor(R.color.backgroundButton));

                setNumberCamera(quantity_cam, number_cam);
                setMethod();

            } else {
                setPowerNull();
            }
            //  hideProgress();
        }
    }

    /**
     * Одиночное фото
     */
    class DownloadImageTask extends GetFoto {

        @Override
        protected void onPreExecute() {
            button_make_foto.setText("Делаю");
            button_make_foto.setClickable(false);
        }

        @SuppressLint("NewApi")
        @Override
        protected Bitmap doInBackground(Void... voids) {
            if (isCancelled()) {
                return null;
            }
            write(makeFoto);
            return super.doInBackground();
        }


        @Override
        protected void onPostExecute(Bitmap result) {

            if (result == null && error) {
                result = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);
                result = getBitmap(result, 3); // Увеличиваем изображение в 3 раза
                touchImageView.setImageBitmap(result);
                button_make_foto.setClickable(true);
                button_make_foto.setText(R.string.snapshot);
                Toast.makeText(getApplicationContext(), "Ошибка приема фото 1", Toast.LENGTH_SHORT).show();
            } else if (result != null && !error) {
                result = getBitmap(result, 3); // Увеличиваем изображение в 3 раза
                touchImageView.setImageBitmap(result);
                button_make_foto.setClickable(true);
                button_make_foto.setText(R.string.snapshot);
            } else {
                button_make_foto.setClickable(true);
                button_make_foto.setText(R.string.snapshot);
                Toast.makeText(getApplicationContext(), "Ошибка приема фото 2", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);

        }

        @Override
        protected void onCancelled() {
            Log.d(sTAG, "onCancelled()");

            button_make_foto.setText(R.string.snapshot);
            button_make_foto.setClickable(true);
            mProgressBar.setProgress(0);

        }

    }

    /**
     * Непрерывное фото
     */
    class Loop extends GetFoto {
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

            if (isCancelled()) {
                try {
                    resetInputStream(sInputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            return super.doInBackground();
        }

        @Override
        protected void onCancelled() {
            Log.e(sTAG, "onCancelled() loop");
            button_make_foto.setText(R.string.snapshot);
            button_make_foto.setClickable(true);
            stopFoto = false;
            mProgressBar.setProgress(0);

        }

        @Override
        protected void onPostExecute(Bitmap result) {

            if (result == null && error) {
                result = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);
                result = getBitmap(result, 3); // Увеличиваем изображение в 3 раза
                touchImageView.setImageBitmap(result);
                button_make_foto.setClickable(true);
                button_make_foto.setText(R.string.snapshot);
                Toast.makeText(getApplicationContext(), "Ошибка приема фото 1", Toast.LENGTH_SHORT).show();
            } else if (result != null && !error) {
                result = getBitmap(result, 3); // Увеличиваем изображение в 3 раза
                touchImageView.setImageBitmap(result);
                button_make_foto.setText(R.string.snapshot);
                loop = new Loop();
                loop.execute();
            } else {
                button_make_foto.setClickable(true);
                button_make_foto.setText(R.string.snapshot);
                Toast.makeText(getApplicationContext(), "Ошибка приема  2", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);

        }

    }

    /**
     * Фото по выстрелу
     */
    class LoopShot extends GetFoto {
        @Override
        protected void onPreExecute() {
            // button_make_foto.setText("Отмена");
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

            return super.doInBackground();
        }


        @Override
        protected void onPostExecute(Bitmap result) {

            if (result == null && error) {
                Log.e(sTAG, "onPostExecute loopShot Ошибка приема фото");
                result = BitmapFactory.decodeResource(getResources(), R.drawable.semarobo);
                result = getBitmap(result, 3); // Увеличиваем изображение в 3 раза
                button_make_foto.setText(R.string.shoot);
                touchImageView.setImageBitmap(result);
                Toast.makeText(getApplicationContext(), "Ошибка приема фото", Toast.LENGTH_SHORT).show();
            } else if (result != null && !error) {
                result = getBitmap(result, 3); // Увеличиваем изображение в 3 раза
                touchImageView.setImageBitmap(result);
                loopShot = new LoopShot();
                loopShot.execute();
            }

            super.onPostExecute(result);
        }


    }

    /**
     * Родительский класс получения фото
     */
    class GetFoto extends AsyncTask<Void, Integer, Bitmap> {

        int number_cam = -1;

        class Reset extends TimerTask {
            @Override
            public void run() {
                if (number_cam == -1) {
                    Log.e(sTAG, " number_cam == -1 ");
                    //   cancelFotos();
                    sRestartGlobalVeriable();
                    error = true;
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    btConnect();
                    error = false;

                }
            }
        }


        @Override
        protected Bitmap doInBackground(Void... voids) {

            int command;
            int size_foto;
            long skipByte;
            try {
                // Timer t = new Timer();
                //  Reset reset = new Reset();
                //   t.schedule(reset,1000);
                number_cam = sInputStream.read();                   // 1. Номер камеры
                Log.e(sTAG, "Номер камеры " + number_cam);
                command = sInputStream.read();                      // 2. Номер комаенды (команда 11)
                Log.e(sTAG, "Номер команды " + command);
                if (command == 12) {

                    int raz1;
                    int raz2;
                    int raz3;

                    raz1 = sInputStream.read();                          // 6. 1-й байт размера
                    raz2 = sInputStream.read();                          // 7. 2-й байт размера
                    raz3 = sInputStream.read();                          // 7. 3-й байт размера
                    Log.e(sTAG, "Байт 1 размера  " + raz1);
                    Log.e(sTAG, "Байт 2 размера  " + raz2);
                    Log.e(sTAG, "Байт 3 размера  " + raz3);
                    size_foto = raz1 * 65536 + raz2 * 256 + raz3;
                    Log.e(sTAG, "Размер фото " + size_foto);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    int count = 0;
                    boolean flagEndOfJpg = true;
                    int new_temp = 0;
                    int previous_temp = 0;
                    mProgressBar.setMax(size_foto);

                    for (int i = 0; (i < size_foto) && (flagEndOfJpg); i++) {
                        if (isCancelled()) {
                            skipByte = size_foto - count;
                            Log.d(sTAG, "Получение фото отменено, прочитано" + count);
                            long skipsInput = sInputStream.skip(skipByte);
                            Log.e(sTAG, "пропускаем " + skipByte + "  Байт");
                            Log.e(sTAG, "пропустили " + skipsInput + "  Байт");
                            resetInputStream(sInputStream);
                            return null;
                        }
                        new_temp = sInputStream.read();
                        byteArrayOutputStream.write(new_temp);
                        if ((previous_temp == 0xFF) && (new_temp == 0xD9)) {
                            flagEndOfJpg = false;
                            Log.e(sTAG, "конец фото ");
                        }
                        previous_temp = new_temp;
                        count++;
                        publishProgress(count);
                    }
                    Log.e(sTAG, "Прочитано " + count);

                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                } else {
                    resetInputStream(sInputStream);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(sTAG, "Ошибка приема фото ");

                error = true;

            }
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mProgressBar.setProgress(0);
        }


    }

    /**
     * Сохронение фотографии
     */
    class SavePhoto extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            button_save_photo.setClickable(false);

        }

        @Override
        protected Integer doInBackground(Void... voids) {

            Date date = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy '  ' kk:mm:ss");
            final String filename = formatter.format(date) + ".jpg";
            //filename = filename +;
            String fullpath = getFilesDir() + "/" + "OcoCam";
            //Создание объекта файла.
            File fhandle = new File(fullpath, filename);

            try {

                Log.d(sTAG, "...Сохранено.     " + filename);
                //Если нет директорий в пути, то они будут созданы:
                if (!fhandle.getParentFile().exists())
                    fhandle.getParentFile().mkdirs();
                //Если файл существует, то он будет перезаписан:
                fhandle.createNewFile();
                OutputStream fOut = new FileOutputStream(fhandle);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                // регистрация в фотоальбоме
                MediaStore.Images.Media.insertImage(getContentResolver(), fhandle.getAbsolutePath(), fhandle.getName(), fhandle.getName());

                Log.d(sTAG, "...Сохранено.     " + fhandle.getName());

                return 1;
            } catch (IOException e) {

                return 0;
            }

        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 1) {
                Toast.makeText(getApplicationContext(),
                        "Сохранено ",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Не удалость сохранить",
                        Toast.LENGTH_LONG).show();
            }
            button_save_photo.setClickable(true);

        }
    }

    /**
     * Отмена фотографирования
     */
    public void cancelFotos() {

        if (downloadImageTask != null) {
            downloadImageTask.cancel(true);
        }
        if (loop != null) {
            loop.cancel(true);
        }
        if (loopShot != null) {
            loopShot.cancel(true);
        }
        mProgressBar.setProgress(0);
    }

    /**
     * Подключение BT
     */
    private void btConnect() {
        if (btConnectThread == null) {
            btConnectThread = new BTConnectThread();
            btConnectThread.execute();
        } else if (btConnectThread.getStatus() == AsyncTask.Status.FINISHED) {
            Log.d(sTAG, "btConnectThread.getStatus() == AsyncTask.Status.FINISHED");
            btConnectThread = new BTConnectThread();
            btConnectThread.execute();
        }
    }

//    public void showProgress() {
//        progressDialog = ProgressDialog.show(this, "", getString(R.string.please_wait));
//    }
//
//    public void hideProgress() {
//        if (progressDialog != null) {
//            progressDialog.dismiss();
//        }
//    }

}



