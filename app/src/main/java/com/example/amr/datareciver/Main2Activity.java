package com.example.amr.datareciver;


import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;

import java.util.StringTokenizer;
import java.util.UUID;
        import android.app.Activity;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
        import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
        import android.widget.Toast;

public class Main2Activity extends Activity {

    Button btnOn, btnOff;
    TextView txtArduino, txtString, txtStringLength, sensorView0, TempC, TempF, Moist;
    Handler bluetoothIn;


    final int handlerState = 0;                        //used to identify handler message
   private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
   // private StringBuilder recDataString = new StringBuilder();
    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    private String LDRSensor="0";
    private ImageView SunImage;
    private ImageView powerImage;
    private ImageView watering;
    private TextView powerText;
    private RelativeLayout mainLy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a);

        //Link the buttons and textViews to respective views
        btnOn = (Button) findViewById(R.id.button);
        btnOff = (Button) findViewById(R.id.buttonOff);
        txtString = (TextView) findViewById(R.id.txtString);
        txtStringLength = (TextView) findViewById(R.id.testView1);
        TempC = (TextView) findViewById(R.id.TempTextC);
        TempF = (TextView) findViewById(R.id.TempTextF);
        Moist = (TextView) findViewById(R.id.moist);
        SunImage=(ImageView)findViewById(R.id.imageView2);
        powerImage=(ImageView)findViewById(R.id.power);
        powerText=(TextView)findViewById(R.id.powerText);
        mainLy=(RelativeLayout)findViewById(R.id.mainLy);
        watering=(ImageView)findViewById(R.id.imageView3);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                boolean circuitIsOFF=false;
                if (msg.what == handlerState) {                                 //if message is what we want
                    String readMessage = (String) msg.obj;
                    // msg.arg1 = bytes from connect thread

                    try {
                       // LDRSensor = getLDRReadingFromMessage(readMessage, msg.getWhen());
                       LDRSensor=readMessage;
                        Log.i("Reading",readMessage);
if(LDRSensor.contains("-100")) circuitIsOFF=true;
    if (LDRSensor.charAt(12) == 'L') {
        SunImage.setImageResource(R.drawable.sundark);

    } else {
        if (LDRSensor.charAt(12) == 'H') {
            SunImage.setImageResource(R.drawable.sun);

        }
    }
    //LDRSensor=readMessage;


                    } catch (Exception e) {
                        Log.i("Error", e.getMessage());
                    }
try {
    if(!circuitIsOFF) {
        TempC.setText(LDRSensor.substring(0, 5));
        TempF.setText(String.valueOf(Double.parseDouble(LDRSensor.substring(0, 5)) * 1.8 + 32));
        Moist.setText(LDRSensor.substring(6, 11));
        powerImage.setImageResource(R.drawable.poweron);
        powerText.setText("Power is On");
        mainLy.setBackgroundResource(R.color.s);
        if(Double.parseDouble(Moist.getText().toString())<40)watering.setVisibility(View.VISIBLE);
        else watering.setVisibility(View.INVISIBLE);
    }
    else {
        TempC.setText("Off");
        TempF.setText(String.valueOf("Off"));
        Moist.setText("Off");
        powerImage.setImageResource(R.drawable.poweroff);
        powerText.setText("Power is Off");
        mainLy.setBackgroundResource(R.color.j);
        watering.setVisibility(View.INVISIBLE);
    }
    //Moist.setText(" Sensor 3 Voltage = " + sensor3 + "V");


    // strIncom =" ";
    //      // }
}catch (Exception e){
    Log.i("Error",e.getMessage());

}
                }
            }


        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        //checkBTState();

       /* // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
        btnOff.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("0");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
            }
        });

        btnOn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("1");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
            }
        });
        */
    }
public void OpenYoutubeLink(View v){
    try {
        String url = "https://www.youtube.com/watch?v=MDub1yycq7U&t=1s";
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }catch (Exception e){
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }
}
    private boolean readPushButton() {
        if(LDRSensor.charAt(LDRSensor.length()-1)=='L')
        return false;
        else if(LDRSensor.charAt(LDRSensor.length()-1)=='H')return true;
        return false;

    }

    private String getLDRReadingFromMessage(String readMessage, long when) {
        int firstIndex=-1,lastindex=-1,i=0; //23.04LL
        String reading="";
        Log.i("IN_BUFFErrrrrrrrrrrR", readMessage);
        while (true) {
            if (i != readMessage.length()) {
                if (readMessage.charAt(i)=='L') {
                    if (firstIndex != -1) {
                        lastindex = i;
                        reading = readMessage.substring(firstIndex+1, lastindex);


                        break;
                    }
                    else firstIndex = i;
                }
                i++;
            }else break;
        }
        return reading;
    }
//roo7 7ot el 2rayat kolha men el arduino fe string we seral.print mra wa7da
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
       // mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
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
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            int bytes;
            // Keep looping to listen for received messages
            while (true) {
                try {
                    try {
                        sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] buffer = new byte[256];

                    bytes = mmInStream.available();
                    Log.i("IN_BUFFER", "mmInStream-available bytes: " + Integer.toString(bytes)+ " ");
                    if (bytes>0){
                        for(int i=0; i<=13; i++){
                            buffer[i] = 0;}
                        // Read from the InputStream
                        Log.i("IN_BUFFER", "Read Stream into Buffer:");
                        bytes = mmInStream.read(buffer);

                        Log.i("IN_BUFFER", "The entire buffer after read stream into buffer: " + Integer.toString(bytes)+ " ");
                        for(int i=0; i<=13; i++)
                            Log.i("IN_BUF_AFTER", buffer[i] + " ");
                        // Send the obtained bytes to the UI Activity
                        Log.i("IN_BUFFER", "We now send to handler.");
                        String readMessage = new String(buffer, 0, bytes);
                        bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage)
                                .sendToTarget();}
                   // bytes = mmInStream.read(messageByte);            //read bytes from input buffer


                    // Send the obtained bytes to the UI Activity via handler
                    //if(bytes>0)bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();

                    //try {
                      //  sleep(100);
                    //} catch (InterruptedException e) {
                    //    e.printStackTrace();
                  //  }
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}