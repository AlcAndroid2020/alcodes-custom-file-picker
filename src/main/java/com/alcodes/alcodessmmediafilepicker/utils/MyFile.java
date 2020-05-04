package com.alcodes.alcodessmmediafilepicker.utils;

public class MyFile {
    private String FileName;
    private String FileSize;
    private String FileUri;
    private String FileType;
    private int Count = 1;
    private boolean isSelected = false;
    private boolean isFolder = false;
    private int FolderID;

    public MyFile(String fileName, String fileUri, boolean isFolder) {
        FileName = fileName;
        FileUri = fileUri;
        this.isFolder = isFolder;
    }

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
}