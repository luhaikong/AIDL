// IMessageService.aidl
package com.example.myapplication;

import com.example.myapplication.MessageCustom;
import com.example.myapplication.MessageReceiveListener;

interface IMessageService {
    void sendMessage(in MessageCustom msg);

    void registerMessageReceiveListener(MessageReceiveListener listener);

    void unRegisterMessageReceiveListener(MessageReceiveListener listener);
}