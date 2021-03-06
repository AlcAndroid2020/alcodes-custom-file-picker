package com.alcodes.alcodessmmediafilepicker.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.util.ArrayList;

public class AsmMfpCustomFilePickerViewModel extends AndroidViewModel {

    private final MutableLiveData<ArrayList<MyFile>> mMyFileList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Uri>> mSelectionList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsGrid = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsInsideAlbum = new MutableLiveData<>();
    private final MutableLiveData<String> mPickerFileType = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mSearching = new MutableLiveData<>();
    private final MutableLiveData<String> mSearchingText = new MutableLiveData<>();
    private final MutableLiveData<String> mSortingStyle = new MutableLiveData<>();
    private final MutableLiveData<Integer> mMaxSelection = new MutableLiveData<>();

    public AsmMfpCustomFilePickerViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<ArrayList<MyFile>> getMyFileList() {
        return mMyFileList;
    }

    public void saveMyFileList(ArrayList<MyFile> myFileList) {
        mMyFileList.setValue(myFileList);
    }

    public void clearMyFileList() {
        mMyFileList.setValue(null);
    }

    public void addFileToMyFileList(MyFile myFile) {
        ArrayList<MyFile> tempMyFileList = new ArrayList<>();
        if (mMyFileList.getValue() != null) {
            tempMyFileList = mMyFileList.getValue();
        }

        tempMyFileList.add(myFile);
        mMyFileList.setValue(tempMyFileList);
    }

    public MutableLiveData<ArrayList<Uri>> getSelectionList() {
        return mSelectionList;
    }

    public void saveSelectionList(ArrayList<Uri> selectionList) {
        mSelectionList.setValue(selectionList);
    }

    public void clearSelectionList() {
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

    public void setPickerFileType(String pickerFileType) {
        mPickerFileType.setValue(pickerFileType);
    }

    public MutableLiveData<Boolean> getSearching() {
        return mSearching;
    }

    public void setSearching(Boolean searching) {
        mSearching.setValue(searching);
    }

    public MutableLiveData<String> getSearchingText() {
        return mSearchingText;
    }

    public void setSearchingText(String searchingText) {
        mSearchingText.setValue(searchingText);
    }

    public MutableLiveData<String> getSortingStyle() {
        return mSortingStyle;
    }

    public void setSortingStyle(String sortingStyle) {
        mSortingStyle.setValue(sortingStyle);
    }

    public MutableLiveData<Integer> getMaxSelection() {
        return mMaxSelection;
    }

    public void setMaxSelection(Integer maxSelection) {
        mMaxSelection.setValue(maxSelection);
    }


}
