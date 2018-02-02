package com.huzaifa.project.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class BluetoothMsg extends AppCompatActivity {

    EditText input,output;
    BluetoothDevice device;
    BluetoothSocket socket;
//    AcceptThread thread1;
//    ConnectThread thread2;
    UUID uuid = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    BluetoothAdapter adapter;
    Button b1,b2;
    int state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_msg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        input = (EditText) findViewById(R.id.txt1);
        output = (EditText) findViewById(R.id.txt2);
        device = getIntent().getExtras().getParcelable("EXTRA_DEVICE");
        adapter = BluetoothAdapter.getDefaultAdapter();
        b1 = (Button) findViewById(R.id.btn);
        b2=(Button) findViewById(R.id.btn1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
              sendMsg();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver,filter);
        connect(device);
//        IO io = new IO();
//        io.start();
    }

    private void sendMsg() {
        Editable msg = input.getText();
        String y = msg.toString();
        byte[] msg_byte = y.getBytes();


        try {

            OutputStream out = socket.getOutputStream();

            out.write(msg_byte);
            output.append(""+msg);
        } catch (IOException e) {
            Log.e("err",""+e);
            output.append(""+e);

        }
    }

    private void get() {
        byte[] reply = new byte[1024];
        int buff=0;
        StringBuilder rep = new StringBuilder();
        try {
            InputStream in = socket.getInputStream();

            while (true) {
               buff = in.read(reply);
                String x = new String(reply,0,buff);
                rep.append(x);
                output.append(""+x);
            }

        } catch (IOException e) {
            output.append(""+e);
        }

    }

    private void connect(BluetoothDevice d) {


//        try {
//            socket.connect();
////            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
////            temp = d.createRfcommSocketToServiceRecord(uuid);
////            socket = temp;
////            socket.connect();
////            Thread.sleep(1000);
////            Toast.makeText(this,"success",Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            Log.e("err",""+e);
//
//        }
        try {
            socket = (BluetoothSocket) d.getClass().getMethod("createRfcommSocket",new Class[] {int.class}).invoke(d,1);
            socket.connect();
        } catch (IOException e) {
            Log.e("err",""+e);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    private class IO extends Thread {



        @Override
        public void run() {
            super.run();
            get();
        }
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                    case BluetoothAdapter.STATE_ON:
                       connect(device);
                       Toast.makeText(getApplicationContext(),"Reconnecting",Toast.LENGTH_LONG).show();
                }
            }
        }
    };


//    private class AcceptThread extends Thread {
//       private final BluetoothServerSocket serverSocket;
//
//        AcceptThread() {
//            BluetoothServerSocket temp=null;
//            try {
//                temp = adapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothChatInsecure",uuid);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            serverSocket = temp;
//            state =2;
//        }
//        public void run() {
//            BluetoothSocket socket=null;
//            while (state!=3) {
//                try {
//                    socket = serverSocket.accept();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    break;
//                }
//            }
//            if (socket != null) {
//                synchronized (BluetoothMsg.this) {
//                    switch (state) {
//                        case 2:
//                        case 1:
//                            // Situation normal. Start the connected thread.
//                            connected(socket, socket.getRemoteDevice(),
//                                    );
//                            break;
//                        case 0:
//                        case 3:
//                            // Either not ready or already connected. Terminate new socket.
//                            try {
//                                socket.close();
//                            } catch (IOException e) {
//                                Log.e("err", "Could not close unwanted socket", e);
//                            }
//                            break;
//                    }
//                }
//            }
//        }
//        }
//        public void connected(BluetoothSocket socket,BluetoothDevice d) {
//        IO io = new IO(socket,d);
//        io.start();
//        state=getState();
//        }
//
//    public synchronized int getState() {
//        return state;
//    }
    }




