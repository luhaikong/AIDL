// MessageReceiveListener.aidl
package com.example.myapplication;

import com.example.myapplication.MessageCustom;

interface MessageReceiveListener {
    void onReceiveMessage(in MessageCustom msg);
}