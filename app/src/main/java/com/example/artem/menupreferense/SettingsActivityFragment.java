package com.example.artem.menupreferense;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.artem.menupreferense.GlobalVeriable.sTAG;
import static com.example.artem.menupreferense.MainActivity.*;
import static com.example.artem.menupreferense.StaticValue.*;



public class SettingsActivityFragment extends PreferenceFragment {

    Map<String, String> mapValueListPref;
    Map<String, String> mapValueSwitchPref;

    Map<String, Map<String, String>> mapKeyAndValyeSwichPref;
    Map<String, Map<String, String>> mapKeyAndValyeListPref;
    Context context;
    List<String> setKeyListPref;
    List<String> setKeySwichPref;
    String valueSwichPref;
    SharedPreferences mSharedPreferences;

//    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences); // load from XML

        context = getActivity().getApplicationContext();
//
//        // Получаем объект SharedPreferences
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Устанавливаем слушатель изменения настроек
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(changeListenerPreferences);


        //Создаем множество ключей Preferenses
        setKeyListPref = setListKeys(getActivity().getResources().getStringArray(R.array.value_keys_ListPreferenses));
        setKeySwichPref = setListKeys(getActivity().getResources().getStringArray(R.array.value_keys_SwichPreferenses));

        // Создаем HashMap для хранения значений относительно ключей API
        mapValueListPref = new HashMap<>();
        mapValueSwitchPref = new HashMap<>();
        // Заполняем HashMap
        inflateMapListPref();
        inflateMapSwichPref();


        // Создаем карту для ключей и значений ListPreferenses
        mapKeyAndValyeListPref = new HashMap<String, Map<String, String>>();
     //   mapKeyAndValyeListPref.put(SPEED_CAM, setMapforSwich(VALUE_SPEED_CAM, NAME_SPEED_CAM));
      //  mapKeyAndValyeListPref.put(TRANSMISSION_FREGENCY, setMapforSwich(VALUE_TRANSMISSION_FREGENCY, NAME_TRANSMISSION_FREGENCY));
     //   mapKeyAndValyeListPref.put(TRANSMITTER_POWER, setMapforSwich(VALUE_TRANSMITTER_POWER, NAME_TRANSMITTER_POWER));
        mapKeyAndValyeListPref.put(RESOLUTION_CAMFOTO, setMapforSwich(VALUE_RESOLUTION_CAMFOTO, NAME_RESOLUTION_CAMFOTO));
        mapKeyAndValyeListPref.put(QUALITY_CAMFOTO, setMapforSwich(VALUE_QUALITY_CAMFOTO, NAME_QUALITY_CAMFOTO));
        mapKeyAndValyeListPref.put(SHOOTING_MODE, setMapforSwich(VALUE_SHOOTING_MODE, NAME_SHOOTING_MODE));
        mapKeyAndValyeListPref.put(DELAY, setMapforSwich(VALUE_DELAY, NAME_DELAY));
        mapKeyAndValyeListPref.put(SENSITIVITY, setMapforSwich(VALUE_SENSITIVITY, NAME_SENSITIVITY));


        // Создаем карту для ключей и значений CheckBoxPreferenses
        mapKeyAndValyeSwichPref = new HashMap<String, Map<String, String>>();
        mapKeyAndValyeSwichPref.put(SHOOTING_FOTO, setMapforSwich(VALUE_SHOOTING_FOTO, NAME_SHOOTING_FOTO));
        mapKeyAndValyeSwichPref.put(INFRATED_ILLUMINATION, setMapforSwich(VALUE_INFRATED_ILLUMINATION, NAME_INFRATED_ILLUMINATION));
      //  mapKeyAndValyeSwichPref.put(SAVE_POWER, setMapforSwich(VALUE_SAVE_POWER, NAME_SAVE_POWER));


    }


    // Метод преобразует массивы строк в  HashMap
    private void setMap(String[] skey, String[] svalue, Map<String, String> map) {
        if (skey.length == svalue.length) {
            ArrayList<String> keys = new ArrayList<>(Arrays.asList(skey));
            ArrayList<String> values = new ArrayList<>(Arrays.asList(svalue));
            for (int i = 0; i < skey.length; i++) {
                map.put(keys.get(i), values.get(i));
            }
        }

    }


    // Метод преобразует массивы строк в  HashMap
    private Map<String, String> setMapforSwich(String[] skey, String[] svalue) {
        Map<String, String> map = new HashMap<>();
        if (skey.length == svalue.length) {
            ArrayList<String> keys = new ArrayList<>(Arrays.asList(skey));
            ArrayList<String> values = new ArrayList<>(Arrays.asList(svalue));
            for (int i = 0; i < skey.length; i++) {
                map.put(keys.get(i), values.get(i));
            }
        }
        return map;
    }


    // Метод возвращает значение параметра в текстовом представлении для ListPref
    private String getValue(String key) {
        String s = mSharedPreferences.getString(key, "");
        return mapValueListPref.get(s);
    }


//    // Метод возвращает значение параметра в текстовом представлении для SwichPref
//    private String getValueSwichPref(String key) {
//        boolean s = mSharedPreferences.getBoolean(key, false);
//
//        return mapValueListPref.get(s);
//    }


    // Метод заполняет HashMap ListPref
    private void inflateMapListPref() {
       // setMap(VALUE_SPEED_CAM, NAME_SPEED_CAM, mapValueListPref);
      //  setMap(VALUE_TRANSMISSION_FREGENCY, NAME_TRANSMISSION_FREGENCY, mapValueListPref);
      //  setMap(VALUE_TRANSMITTER_POWER, NAME_TRANSMITTER_POWER, mapValueListPref);
        setMap(VALUE_RESOLUTION_CAMFOTO, NAME_RESOLUTION_CAMFOTO, mapValueListPref);
        setMap(VALUE_QUALITY_CAMFOTO, NAME_QUALITY_CAMFOTO, mapValueListPref);
        setMap(VALUE_SHOOTING_MODE, NAME_SHOOTING_MODE, mapValueListPref);
        setMap(VALUE_DELAY, NAME_DELAY, mapValueListPref);
        setMap(VALUE_SENSITIVITY, NAME_SENSITIVITY, mapValueListPref);
    }

    // Метод заполняет HashMap SwichPref
    private void inflateMapSwichPref() {
        setMap(VALUE_SHOOTING_FOTO, NAME_SHOOTING_FOTO, mapValueSwitchPref);
        setMap(VALUE_INFRATED_ILLUMINATION, NAME_INFRATED_ILLUMINATION, mapValueSwitchPref);
       // setMap(VALUE_SAVE_POWER, NAME_SAVE_POWER, mapValueSwitchPref);
    }


    // Создаем массив ключей
    private List<String> setListKeys(String[] stringKey) {
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(stringKey));
        return arrayList;

    }


    // Метод обнавления ListPreferenses
    private void setValueListPreferenses(List<String> key) {
        for (int i = 0; i < key.size(); i++) {
            findPreference(key.get(i)).setSummary(getValue(key.get(i)));


        }
    }

    private void getAPICheckBoxPref(String key, String value) {
        Map<String, String> s = mapKeyAndValyeSwichPref.get(key);
        for (Map.Entry<String, String> entry : s.entrySet()) {
            if (entry.getValue().equals(value)) {
                valueSwichPref = entry.getKey();
              //  Log.e(sTAG, valueSwichPref);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем значение выбраных пунктов настройки
        setValueListPreferenses(setKeyListPref);
    }



    public static void printValues(Map<String, String> map)
    {
        for(Map.Entry<String, String> pair : map.entrySet())
        {
            String value = pair.getValue();
            Log.e(sTAG, value);

        }
    }


    private SharedPreferences.OnSharedPreferenceChangeListener changeListenerPreferences =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                // called when the user changes the app's preferences
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                        // Условие для ListPref
                    if (mapKeyAndValyeListPref.containsKey(key)) {
                        findPreference(key).setSummary(getValue(key));
                       // получаем значение переменной
                        valueSwichPref = sharedPreferences.getString(key, "Не задано");

                        String s = mSharedPreferences.getString(key, "Не задано");

                   //    printValues(mapValueListPref);


                        Log.e(sTAG, mapValueListPref.get(s));
                        Log.e(sTAG, valueSwichPref);



                    // Написать универсальный метод отправки запроса камере

                      //    Log.e(sTAG, sharedPreferences.getAll().toString());
                        // Условие для SwichPref
                    } else if (mapKeyAndValyeSwichPref.containsKey(key)) {
                        // Получаем значение переменной по переданному ключу из sharedPreferences
                        boolean b = sharedPreferences.getBoolean(key, false);
                        String value = Boolean.toString(b);
                        // Получаем значение api по полученному значению
                        getAPICheckBoxPref(key, value);



                    }



                }


            };
}
