package com.example.arduinosensors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.Toast;

public class MainActivity extends Activity {

    public class AirQuality {
        float pm25;
        float pm10;

        public AirQuality(float pm25, float pm10) {
            this.pm25 = pm25;
            this.pm10 = pm10;
        }

        private int pm25_stat() {        // WHO 기준 초미세먼지 단계
            if (pm25 > 50)
                return 0;       // 매우 나쁨
            else if (pm25 > 25)
                return 1;       // 나쁨
            else if (pm25 > 15)
                return 2;       // 보통
            else if (pm25 > 5)
                return 3;       // 좋음
            else if (pm25 >= 0)
                return 4;       // 매우 좋음
            else
                return 5;       // default or error
        }

        private int pm10_stat() {        // WHO 기준 미세먼지 단계
            if (pm10 > 100)
                return 0;      // 매우 나쁨
            else if (pm10 > 50)
                return 1;      // 나쁨
            else if (pm10 > 30)
                return 2;      // 보통
            else if (pm10 > 15)
                return 3;      // 좋음
            else if (pm10 >= 0)
                return 4;      // 매우 좋음
            else
                return 5;      // default or error
        }

        private int stat() {             // 미세먼지와 초미세먼지의 단계 중 더 안 좋은 단계
            if (pm10_stat() < pm25_stat())
                return pm10_stat();
            else
                return pm25_stat();
        }
    }

    public class CheckSum {
        StringBuilder receivedData;
        Boolean valid = false;
        float data1 = -1;
        float data2 = -1;

        CheckSum(StringBuilder s) {
            receivedData = s;
        }

        void check() {
            int size = receivedData.length();
            String[] value = new String[5];
            int step = 0; // index of value array
            int walk = 0; // digit of each integer
            int sum = 0;
            int divisor = 64;
            boolean flag = true;

            Arrays.fill(value, "");
            if (receivedData.charAt(0) == '#') {
                for (int i = 1; i < size; i++) {
                    if (step > 4) {
                        flag = false;
                        break;
                    }
                    if (receivedData.charAt(i) >= '0' && receivedData.charAt(i) <= '9')
                        walk++;
                    else {
                        value[step] = "0" + receivedData.substring(i - walk, i); // prevent empty string
                        walk = 0;
                        step++;
                    }
                    if (receivedData.charAt(i) == '~') // end of string
                        break;
                }
                if (step < 5)
                    flag = false;
            }
            if (flag) {
                for (int i = 0; i < 4; i++)
                    sum += Integer.parseInt(value[i]);
                valid = (sum % divisor == Integer.parseInt(value[4]));
            }
            if (valid) {
                data1 = Integer.parseInt(value[0]) + Integer.parseInt(value[1]) / 100f;
                data2 = Integer.parseInt(value[2]) + Integer.parseInt(value[3]) / 100f;
            }
        } // end of check()
    } // end of CheckSum class


    TextView txtArduino, txtString, txtStringLength, sensorView0, sensorView1;
    Handler bluetoothIn;
    RelativeLayout View2;

    final int handlerState = 0; //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        txtString = (TextView) findViewById(R.id.txtString);
        txtStringLength = (TextView) findViewById(R.id.testView1);
        sensorView0 = (TextView) findViewById(R.id.sensorView0);
        sensorView1 = (TextView) findViewById(R.id.sensorView1);
        View2 = (RelativeLayout) findViewById(R.id.back);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) { //if message is what we want
                    String readMessage = (String) msg.obj; // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) { 
                        String dataInPrint = recDataString.substring(0, recDataString.length()); // extract string
                        txtString.setText("Data Received = " + dataInPrint);
                        txtStringLength.setText("String Length = " + String.valueOf(dataInPrint.length()));
                        CheckSum checkValue = new CheckSum(recDataString);
                        checkValue.check();

                        if (checkValue.valid) {

                            sensorView0.setText(" 초미세먼지 (P2.5) = " + String.format("%.2f", checkValue.data1) + "㎍/㎥");
                            sensorView1.setText(" 미세먼지 (P10) = " + String.format("%.2f", checkValue.data2) + "㎍/㎥");

                            AirQuality value = new AirQuality(checkValue.data1, checkValue.data2);
                            int stat = value.stat();
                            if (stat == 4)
                                View2.setBackgroundResource(R.drawable.best);
                            else if (stat == 3)
                                View2.setBackgroundResource(R.drawable.good);
                            else if (stat == 2)
                                View2.setBackgroundResource(R.drawable.botong);
                            else if (stat == 1)
                                View2.setBackgroundResource(R.drawable.bad);
                            else if (stat == 0)
                                View2.setBackgroundResource(R.drawable.real_bad);

                            value = null; // is it necessary?
                        } // if valid
                        recDataString.delete(0, recDataString.length()); //clear all string data
                        dataInPrint = " ";
                        checkValue = null; // is it necessary?
                    } // if end of line > 0
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        checkBTState();
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
//creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

//Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

//Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

//create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
// Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
//insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

//I send a character when resuming.beginning transmission to check device is connected
//If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
//Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
//insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
//Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

// Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer); //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
// Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer); //write bytes over BT connection via outstream
            } catch (IOException e) {
//if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}
