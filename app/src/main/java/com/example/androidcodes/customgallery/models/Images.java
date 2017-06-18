package com.example.androidcodes.customgallery.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mehta on 6/18/2017.
 */

public class Images implements Parcelable {

    public long id;
    public String imageName;
    public String imagePath;
    public int isSelected;


    protected Images(Parcel in) {
        id = in.readLong();
        imageName = in.readString();
        imagePath = in.readString();
        isSelected = in.readInt();
    }

    public static final Creator<Images> CREATOR = new Creator<Images>() {
        @Override
        public Images createFromParcel(Parcel in) {
            return new Images(in);
        }

        @Override
        public Images[] newArray(int size) {
            return new Images[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(imageName);
        dest.writeString(imagePath);
        dest.writeInt(isSelected);
    }
}
