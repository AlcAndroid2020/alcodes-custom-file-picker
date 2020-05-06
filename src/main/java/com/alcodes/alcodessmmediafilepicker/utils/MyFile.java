package com.alcodes.alcodessmmediafilepicker.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
<<<<<<< HEAD
=======
import java.util.Date;
>>>>>>> origin/OoiLiangZhi/dev

public class MyFile implements Serializable, Parcelable {
    private String FileName;
    private String FileSize;
    private String FileUri;
    private String FileType;

    private String LastModifyDate;
    private int Count = 1;
    private boolean isSelected = false;
    private boolean isFolder = false;
    private int FolderID;

    public MyFile(String fileName, String fileUri, String lastModifyDate, boolean isFolder) {
        FileName = fileName;
        FileUri = fileUri;
        LastModifyDate = lastModifyDate;
        this.isFolder = isFolder;
    }

    //Tem use will delete after all part get date
    public MyFile(String fileName, String fileUri, boolean isFolder) {
        FileName = fileName;
        FileUri = fileUri;
        this.isFolder = isFolder;
    }


    protected MyFile(Parcel in) {
        FileName = in.readString();
        FileSize = in.readString();
        FileUri = in.readString();
<<<<<<< HEAD
=======
        LastModifyDate = in.readString();
>>>>>>> origin/OoiLiangZhi/dev
        FileType = in.readString();
        Count = in.readInt();
        isSelected = in.readByte() != 0;
        isFolder = in.readByte() != 0;
    }

    public static final Creator<MyFile> CREATOR = new Creator<MyFile>() {
        @Override
        public MyFile createFromParcel(Parcel in) {
            return new MyFile(in);
        }

        @Override
        public MyFile[] newArray(int size) {
            return new MyFile[size];
        }
    };

<<<<<<< HEAD
=======
    public String getLastModifyDate() {
        return LastModifyDate;
    }

    public void setLastModifyDate(String lastModifyDate) {
        LastModifyDate = lastModifyDate;
    }

>>>>>>> origin/OoiLiangZhi/dev
    public int getFolderID() {
        return FolderID;
    }

    public void setFolderID(int folderID) {
        FolderID = folderID;
    }


    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileSize() {
        return FileSize;
    }

    public void setFileSize(String fileSize) {
        FileSize = fileSize;
    }

    public String getFileUri() {
        return FileUri;
    }

    public void setFileUri(String fileUri) {
        FileUri = fileUri;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean folder) {
        isFolder = folder;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FileName);
        dest.writeString(FileSize);
        dest.writeString(FileUri);
<<<<<<< HEAD
=======
        dest.writeString(LastModifyDate);
>>>>>>> origin/OoiLiangZhi/dev
        dest.writeString(FileType);
        dest.writeInt(Count);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (isFolder ? 1 : 0));
    }
}