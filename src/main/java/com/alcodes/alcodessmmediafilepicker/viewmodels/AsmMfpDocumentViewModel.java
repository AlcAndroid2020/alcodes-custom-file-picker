package com.alcodes.alcodessmmediafilepicker.viewmodels;

import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpMainFragment;
import com.alcodes.alcodessmmediafilepicker.utils.AsmMfpSharedPreferenceHelper;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AsmMfpDocumentViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<String>> Selectionlist = new MutableLiveData<>();//selected files list
    private MutableLiveData<Integer> SelectionLimit = new MutableLiveData<>(0); //select limit counter
    private MutableLiveData<ArrayList<MyFile>> mFileList = new MutableLiveData<>(); //to store file list

    private final MutableLiveData<ArrayList<MyFile>> mMyFileList = new MutableLiveData<>();
    private final MutableLiveData<String> mSearchingText = new MutableLiveData<>();
    private final MutableLiveData<Integer> mViewPagerPosition = new MutableLiveData<>();
    private final MutableLiveData<String> mSortingStyle = new MutableLiveData<>();

    private MutableLiveData<ArrayList<MyFile>> mpdfFileList = new MutableLiveData<>(); //to store file list
    private final MutableLiveData<ArrayList<MyFile>> mMypttFileList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<MyFile>> mMytxtFileList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<MyFile>> mMyxlsFileList = new MutableLiveData<>();
    private final MutableLiveData<List<String>> mFileType = new MutableLiveData<>();

    public AsmMfpDocumentViewModel(@NonNull Application application) {
        super(application);
    }

    //for storing all user selection in all file type
    public LiveData<ArrayList<String>> getSelectionList() {
        return Selectionlist;
    }

    public void setSelectionList(ArrayList<String> filelist) {
        Selectionlist.setValue(filelist);
    }

    //for storing user limit selection (by default is 10 items)
    public LiveData<Integer> getSelectionLimit() {
        return SelectionLimit;
    }

    public void setSelectionLimit(Integer count) {
        SelectionLimit.setValue(count);

        //Save into SharedPreferences so that customFilePickerActivity is updated as well
        AsmMfpSharedPreferenceHelper.getInstance(getApplication())
                .edit()
                .putInt(AsmMfpMainFragment.EXTRA_INT_MAX_FILE_SELECTION, count)
                .apply();
    }

    public void setFileList(ArrayList<MyFile> list) {
        mFileList.setValue(list);
    }

    public LiveData<ArrayList<MyFile>> getFileList(ArrayList<String> FileTypes, String type) {

        ArrayList<MyFile> FileList = new ArrayList<>();
        //Table
        Uri table = MediaStore.Files.getContentUri("external");
        //Column
        String[] column = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED};

        //Where
        String where = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        if (FileTypes.size() > 1) {
            for (int i = 1; i < FileTypes.size(); i++)
                where += " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        }

        //args
        String[] args = FileTypes.toArray(new String[FileTypes.size()]);

        Cursor fileCursor = getApplication().getContentResolver().query(table, column, where, args, null);

        while (fileCursor.moveToNext()) {
            //your code
            int dataColumn = fileCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            int sizeColumn = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
            int dateColumn = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED);

            String filePath = fileCursor.getString(dataColumn);
            String size = fileCursor.getString(sizeColumn);
            Uri uri = Uri.fromFile(new File(filePath));
            Long date = fileCursor.getLong(dateColumn);
            //grant permision for app with package "packegeName", eg. before starting other app via intent

            getApplication().grantUriPermission(getApplication().getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //revoke permisions
            Uri newuri = FileProvider.getUriForFile(getApplication(), "com.alcodes.alcodesgalleryviewerdemo.fileprovider", new File(filePath));
            DocumentFile df = DocumentFile.fromSingleUri(getApplication().getApplicationContext(), newuri);
            //revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            MyFile myFile = new MyFile(df.getName(), String.valueOf(newuri), date, false);
            myFile.setFileType(type);
            myFile.setFileSize(size);
            FileList.add(myFile);
        }
        fileCursor.close();
        mFileList.setValue(FileList);
        return mFileList;
    }


    public MutableLiveData<String> getSearchingText() {
        return mSearchingText;
    }

    public void setSearchingText(String searchingText) {
        mSearchingText.setValue(searchingText);
    }

    public MutableLiveData<Integer> getViewPagerPosition() {
        return mViewPagerPosition;
    }

    public void setViewPagerPosition(Integer position) {
        mViewPagerPosition.setValue(position);
    }


    public void saveMyFileList(ArrayList<MyFile> myFileList) {
        mMyFileList.setValue(myFileList);
    }

    public MutableLiveData<ArrayList<MyFile>> getMyFileList() {
        return mMyFileList;
    }

    public void saveMyPDFFileList(ArrayList<MyFile> mypdfFileList) {
        mpdfFileList.setValue(mypdfFileList);
    }

    public MutableLiveData<ArrayList<MyFile>> getMyPDFFileList() {
        return mpdfFileList;
    }

    public void saveMyPttFileList(ArrayList<MyFile> mypttFileList) {
        mMypttFileList.setValue(mypttFileList);
    }

    public MutableLiveData<ArrayList<MyFile>> getMyPttFileList() {
        return mMypttFileList;
    }

    public void saveMytxtFileList(ArrayList<MyFile> mytxtFileList) {
        mMytxtFileList.setValue(mytxtFileList);
    }

    public MutableLiveData<ArrayList<MyFile>> getMytxtFileList() {
        return mMytxtFileList;
    }

    public void saveMyxlsFileList(ArrayList<MyFile> myxlsFileList) {
        mMyxlsFileList.setValue(myxlsFileList);
    }

    public MutableLiveData<ArrayList<MyFile>> getMyxlsFileList() {
        return mMyxlsFileList;
    }


    public MutableLiveData<List<String>> getFileType() {
        return mFileType;
    }

    public String getCurrentFileType(Integer position) {
        if (mFileType.getValue() == null) {
            return "";
        } else {
            return mFileType.getValue().get(position);
        }
    }

    public void setFileType(String fileType) {
        List<String> dataHolders = new ArrayList<>();

        if (mFileType.getValue() == null) {
            dataHolders.add(fileType);
        } else {
            dataHolders = mFileType.getValue();
            for (int i = 0; i < dataHolders.size(); i++) {
                if (!dataHolders.get(i).equals(fileType)) {
                    dataHolders.add(fileType);
                    break;
                }
            }
        }
        mFileType.setValue(dataHolders);
    }

    public MutableLiveData<String> getSortingStyle() {
        return mSortingStyle;
    }

    public void setSortingStyle(String sortingStyle) {
        mSortingStyle.setValue(sortingStyle);
    }
}
