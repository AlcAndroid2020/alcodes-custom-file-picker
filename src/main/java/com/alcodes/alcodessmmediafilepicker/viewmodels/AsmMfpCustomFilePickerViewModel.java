package com.alcodes.alcodessmmediafilepicker.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.view.ActionMode;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AsmMfpCustomFilePickerViewModel extends AndroidViewModel {

    private final MutableLiveData<ArrayList<MyFile>> mMyFileList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Uri>> mSelectionList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsGrid = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsInsideAlbum = new MutableLiveData<>();
    private final MutableLiveData<String> mPickerFileType = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mSearching = new MutableLiveData<>();
    private final MutableLiveData<androidx.appcompat.view.ActionMode> mActionMode = new MutableLiveData<>();

    public AsmMfpCustomFilePickerViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<ArrayList<MyFile>> getMyFileList() {
        return mMyFileList;
    }

    public void saveMyFileList(ArrayList<MyFile> myFileList){
        mMyFileList.setValue(myFileList);
    }

    public void clearMyFileList(){
        mMyFileList.setValue(null);
    }

    public void addFileToMyFileList(MyFile myFile){
        ArrayList<MyFile> tempMyFileList = new ArrayList<>();
        if(mMyFileList.getValue() != null){
            tempMyFileList = mMyFileList.getValue();
        }

        tempMyFileList.add(myFile);
        mMyFileList.setValue(tempMyFileList);
    }

    public MutableLiveData<ArrayList<Uri>> getSelectionList() {
        return mSelectionList;
    }


    /*public void addSelectionIntoSelectionList(Uri uri){
        ArrayList<Uri> tempSelectionList = new ArrayList<>();
        if(mSelectionList.getValue() != null){
            tempSelectionList=mSelectionList.getValue();
        }
        tempSelectionList.add(uri);

        mSelectionList.setValue(tempSelectionList);
    }*/

    public void saveSelectionList(ArrayList<Uri> selectionList){
        mSelectionList.setValue(selectionList);
    }

    /*public void removeSelectionList(Uri unSelectionList){
        List<Uri> dataHolders = mSelectionList.getValue();
        int getIndex = dataHolders.indexOf(unSelectionList);

        mSelectionList.getValue().remove(getIndex);
    }*/

    public void clearSelectionList(){
        mSelectionList.setValue(null);
    }

    public MutableLiveData<Boolean> getIsGrid() {
        return mIsGrid;
    }

    public MutableLiveData<Boolean> getIsInsideAlbum() {
        return mIsInsideAlbum;
    }

    public void setIsGrid(Boolean setting) {
        mIsGrid.setValue(setting);
    }

    public void setIsInsideAlbum(Boolean setting) {
        mIsInsideAlbum.setValue(setting);
    }

    public MutableLiveData<String> getPickerFileType() {
        return mPickerFileType;
    }

    public void setPickerFileType(String pickerFileType){
        mPickerFileType.setValue(pickerFileType);
    }

    public MutableLiveData<Boolean> getSearching() {
        return mSearching;
    }

    public void setSearching(Boolean searching){
        mSearching.setValue(searching);
    }

    public MutableLiveData<androidx.appcompat.view.ActionMode> getActionMode() {
        return mActionMode;
    }

    public void setActionMode(androidx.appcompat.view.ActionMode  actionMode){
        mActionMode.setValue(actionMode);
    }

    public void clearActionMode(){
        mActionMode.setValue(null);
    }
}
