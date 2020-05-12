package com.alcodes.alcodessmmediafilepicker.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AsmMfpMainSharedViewModel extends AndroidViewModel {

    private final MutableLiveData<ArrayList<MyFile>> mMyFileList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Uri>> mSelectionList = new MutableLiveData<>();

    public AsmMfpMainSharedViewModel(@NonNull Application application) {
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

    public void addSelectionIntoSelectionList(Uri uri){
        ArrayList<Uri> tempSelectionList = new ArrayList<>();
        if(mSelectionList.getValue() != null){
            tempSelectionList = mSelectionList.getValue();
        }
        tempSelectionList.add(uri);

        mSelectionList.setValue(tempSelectionList);
    }

    public void clearSelectionList(){
        mSelectionList.setValue(null);
    }
}
