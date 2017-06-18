package com.example.androidcodes.customgallery.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mehta on 6/17/2017.
 */

public class Folders implements Parcelable {

    private String folderName, folderImagePath;

    public Folders(Parcel in) {
        folderName = in.readString();
        folderImagePath = in.readString();
    }

    public static final Creator<Folders> CREATOR = new Creator<Folders>() {
        @Override
        public Folders createFromParcel(Parcel in) {
            return new Folders(in);
        }

        @Override
        public Folders[] newArray(int size) {
            return new Folders[size];
        }
    };

    public Folders() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getFolderImagePath() {
        return folderImagePath;
    }

    public void setFolderImagePath(String folderImagePath) {
        this.folderImagePath = folderImagePath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(folderName);
        dest.writeString(folderImagePath);
    }
}
