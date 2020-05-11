package com.alcodes.alcodessmmediafilepicker.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.util.ArrayList;

public class AsmMfpDocumentViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<String>> Selectionlist = new MutableLiveData<ArrayList<String>>();


    public AsmMfpDocumentViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ArrayList<String>> getSelectionList() {
        return Selectionlist;
    }

    public void setSelectionList(ArrayList<String> filelist) {
        Selectionlist.setValue(filelist);
    }
}
