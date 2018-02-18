package com.example.artem.menupreferense;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Artem on 03.01.2018.
 */

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int mViewResorceId;

    public DeviceListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BluetoothDevice> devices) {
        super(context, resource, devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResorceId = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        convertView = mLayoutInflater.inflate(mViewResorceId,null);
        BluetoothDevice device = mDevices.get(position);
        if(device!=null){
            TextView deviceName = (TextView)convertView.findViewById(R.id.tvDeviceName);
            TextView deviceAdress = (TextView)convertView.findViewById(R.id.tvDeviceAdress);
            if(deviceName!=null){
                deviceName.setText(device.getName());
            }
            if(deviceAdress!=null){
                deviceAdress.setText(device.getAddress());
            }

        }
        return convertView;
    }
}
