package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageCustom implements Parcelable {
    private String content;
    private boolean isSendSuccess;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSendSuccess() {
        return isSendSuccess;
    }

    public void setSendSuccess(boolean sendSuccess) {
        isSendSuccess = sendSuccess;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeByte(this.isSendSuccess ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.content = source.readString();
        this.isSendSuccess = source.readByte() != 0;
    }

    public MessageCustom() {
    }

    protected MessageCustom(Parcel in) {
        this.content = in.readString();
        this.isSendSuccess = in.readByte() != 0;
    }

    public static final Parcelable.Creator<MessageCustom> CREATOR = new Parcelable.Creator<MessageCustom>() {
        @Override
        public MessageCustom createFromParcel(Parcel source) {
            return new MessageCustom(source);
        }

        @Override
        public MessageCustom[] newArray(int size) {
            return new MessageCustom[size];
        }
    };

}
