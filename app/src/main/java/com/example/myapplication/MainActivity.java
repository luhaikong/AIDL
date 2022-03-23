package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_connect, btn_disconnect, btn_isconnect;
    private Button btn_send,btn_register,btn_unregister;
    private Button btn_send_by_messenger;
    private IConnectionService connectionServiceProxy;
    private IServiceManager serviceManagerProxy;
    private IMessageService messageServiceProxy;
    private Messenger messengerProxy;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            bundle.setClassLoader(Message.class.getClassLoader());
            MessageCustom messageCustom = bundle.getParcelable("msgr");
            Toast.makeText(MainActivity.this,messageCustom.getContent(),Toast.LENGTH_LONG).show();
        }
    };
    private Messenger clientMessenger = new Messenger(handler);

    private MessageReceiveListener messageReceiveListener = new MessageReceiveListener.Stub() {
        @Override
        public void onReceiveMessage(MessageCustom msg) throws RemoteException {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,msg.getContent(),Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_connect = findViewById(R.id.btn_connect);
        btn_disconnect = findViewById(R.id.btn_disconnect);
        btn_isconnect = findViewById(R.id.btn_isconnect);
        btn_connect.setOnClickListener(this);
        btn_disconnect.setOnClickListener(this);
        btn_isconnect.setOnClickListener(this);

        btn_send = findViewById(R.id.btn_send);
        btn_register = findViewById(R.id.btn_register);
        btn_unregister = findViewById(R.id.btn_unregister);
        btn_send.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_unregister.setOnClickListener(this);

        btn_send_by_messenger = findViewById(R.id.btn_send_by_messenger);
        btn_send_by_messenger.setOnClickListener(this);

        Intent intent = new Intent(this, RemoteService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                try {
                    serviceManagerProxy = IServiceManager.Stub.asInterface(iBinder);
                    connectionServiceProxy = IConnectionService.Stub.asInterface(serviceManagerProxy.getService(IConnectionService.class.getSimpleName()));
                    messageServiceProxy = IMessageService.Stub.asInterface(serviceManagerProxy.getService(IMessageService.class.getSimpleName()));

                    messengerProxy = new Messenger(serviceManagerProxy.getService(Messenger.class.getSimpleName()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        }, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_connect:
                try {
                    connectionServiceProxy.connect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_disconnect:
                try {
                    connectionServiceProxy.disconnect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_isconnect:
                boolean isConnected = false;
                try {
                    isConnected = connectionServiceProxy.isConnected();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this,String.valueOf(isConnected),Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_send:
                MessageCustom msg = new MessageCustom();
                msg.setContent("MessageCustom send from main");
                try {
                    messageServiceProxy.sendMessage(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_register:
                try {
                    messageServiceProxy.registerMessageReceiveListener(messageReceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_unregister:
                try {
                    messageServiceProxy.unRegisterMessageReceiveListener(messageReceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_send_by_messenger:
                MessageCustom msgr = new MessageCustom();
                msgr.setContent("send message from main by messenger");

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putParcelable("msgr",msgr);
                message.setData(bundle);
                try {
                    messengerProxy.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}