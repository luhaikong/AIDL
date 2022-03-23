package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RemoteService extends Service {

    private boolean isConnected = false;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            bundle.setClassLoader(Message.class.getClassLoader());
            MessageCustom messageCustom = bundle.getParcelable("msgr");
            Toast.makeText(RemoteService.this,messageCustom.getContent(),Toast.LENGTH_LONG).show();
        }
    };

    private RemoteCallbackList<MessageReceiveListener> remoteCallbackList = new RemoteCallbackList<>();

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor; // 用于启动定时任务，模拟消息接收
    private ScheduledFuture scheduledFuture; // 用于取消定时任务

    private IConnectionService connectionService = new IConnectionService.Stub() {
        @Override
        public void connect() throws RemoteException {
            try {
//                Thread.sleep(5000);
                isConnected = true;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RemoteService.this,"connect",Toast.LENGTH_LONG).show();
                    }
                });
                scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        int size = remoteCallbackList.beginBroadcast();
                        for (int i=0;i<size;i++){
                            MessageCustom msg = new MessageCustom();
                            msg.setContent("this message from remote");
                            try{
                                remoteCallbackList.getBroadcastItem(i).onReceiveMessage(msg);
                            } catch (RemoteException e){
                                e.printStackTrace();
                            }
                        }
                        remoteCallbackList.finishBroadcast();
                    }
                }, 5000,5000, TimeUnit.MILLISECONDS);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void disconnect() throws RemoteException {
            isConnected = false;
            scheduledFuture.cancel(true);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RemoteService.this,"disconnect",Toast.LENGTH_LONG).show();
                }
            });

        }

        @Override
        public boolean isConnected() throws RemoteException {
            return isConnected;
        }
    };

    private IMessageService messageService = new IMessageService.Stub() {
        @Override
        public void sendMessage(MessageCustom msg) throws RemoteException {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RemoteService.this,msg.getContent(),Toast.LENGTH_LONG).show();
                }
            });
            if (isConnected) {
                msg.setSendSuccess(true);
            } else {
                msg.setSendSuccess(false);
            }
        }

        @Override
        public void registerMessageReceiveListener(MessageReceiveListener listener) throws RemoteException {
            if (listener != null){
                remoteCallbackList.register(listener);
            }
        }

        @Override
        public void unRegisterMessageReceiveListener(MessageReceiveListener listener) throws RemoteException {
            if (listener != null){
                remoteCallbackList.unregister(listener);
            }
        }
    };

    private Messenger messenger = new Messenger(handler);

    private IServiceManager serviceManager = new IServiceManager.Stub() {
        @Override
        public IBinder getService(String serviceName) throws RemoteException {
            if (IConnectionService.class.getSimpleName().equals(serviceName)) {
                return connectionService.asBinder();
            } else if (IMessageService.class.getSimpleName().equals(serviceName)) {
                return messageService.asBinder();
            } else if (Messenger.class.getSimpleName().equals(serviceName)){
                return messenger.getBinder();
            } else {
                return null;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceManager.asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    }
}
