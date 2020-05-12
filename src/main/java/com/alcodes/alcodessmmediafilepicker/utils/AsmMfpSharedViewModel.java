package com.alcodes.alcodessmmediafilepicker.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;

public class AsmMfpSharedViewModel extends AndroidViewModel {

    public AsmMfpSharedViewModel(@NonNull Application application) {
        super(application);
    }

    private ArrayList<MyFile> myFileList = new ArrayList<>();


    public void setFile(ArrayList<MyFile> file) {
        myFileList = file;
    }

    public ArrayList<MyFile> getfile() {
        return myFileList;
    }

}

