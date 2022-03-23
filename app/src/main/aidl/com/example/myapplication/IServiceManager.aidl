// IServiceManager.aidl
package com.example.myapplication;

interface IServiceManager {
    IBinder getService(String serviceName);
}