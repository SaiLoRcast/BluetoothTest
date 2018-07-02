package com.polygalov.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int REQUEST_ENABLE_BT = 1;

    private Button buttonGetBondedDevices;
    private Button buttonFoundDevices;
    ListView listView;
    ArrayAdapter<String> mArrayAdapter;

    BluetoothAdapter mBluetooth = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewDeices);

        buttonGetBondedDevices = findViewById(R.id.get_bonded_devices_button);
        buttonFoundDevices = findViewById(R.id.find_devices_button);

        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        if (mBluetooth != null) {

            String status;
            if (mBluetooth.isEnabled()) {
                //код когда всё включено
                String myDeviceAddress = mBluetooth.getAddress();
                String myDeviceName = mBluetooth.getName();
//                String state = mBluetooth.getState();
                status = myDeviceName + " : " + myDeviceAddress + " : ";
            } else {
                //коды когда блю выключен - просит разрешения на вклбчение
                status = "Bluetooth выключен";
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
            }
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_bonded_devices_button:
                getBondedDevices();
            case R.id.find_devices_button:
                findDevices();
        }
    }

    private void findDevices() {
        // Создаем BroadcastReceiver для ACTION_FOUND
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // Когда найдено новое устройство
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Получаем объект BluetoothDevice из интента
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //Добавляем имя и адрес в array adapter, чтобы показвать в ListView
                    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        };
        // Регистрируем BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);// Не забудьте снять регистрацию в onDestroy
    }

    private void getBondedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetooth.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }


}