package com.example.arduinosensors;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable{
    private String age;
    private String gender;
    private String weight;
    private String height;

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public  UserInfo(String age, String gender, String weight, String height){
        this.age = age;
        this.gender = gender;
        this.weight= weight;
        this.height= height;
    }
    protected UserInfo(Parcel in) {
        age = in.readString();
        gender = in.readString();
        weight = in.readString();
        height = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(age);
        dest.writeString(gender);
        dest.writeString(weight);
        dest.writeString(height);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
