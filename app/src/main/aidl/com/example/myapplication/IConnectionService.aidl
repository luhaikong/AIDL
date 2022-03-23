// IConnectionService.aidl
package com.example.myapplication;

interface IConnectionService {
    oneway void connect();

    void disconnect();

    boolean isConnected();
}