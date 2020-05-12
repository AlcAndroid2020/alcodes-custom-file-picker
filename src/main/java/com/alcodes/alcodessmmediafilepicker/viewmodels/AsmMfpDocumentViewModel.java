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

import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.io.File;
import java.util.ArrayList;

public class AsmMfpDocumentViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<String>> Selectionlist = new MutableLiveData<ArrayList<String>>();
    private MutableLiveData<Integer> SelectionLimit = new MutableLiveData<Integer>(10);
    private MutableLiveData<ArrayList<MyFile>> mFileList = new MutableLiveData<ArrayList<MyFile>>();

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
    }

    public LiveData<ArrayList<MyFile>> getFileList(ArrayList<String> FileTypes, String type) {

        ArrayList<MyFile> FileList = new ArrayList<>();
        //Table
        Uri table = MediaStore.Files.getContentUri("external");
        //Column
        String[] column = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE};

        //Where
        String where = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        if (FileTypes.size() > 1) {
            for(int i=1;i<FileTypes.size();i++)
            where += " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        }

        //args

        String[] args = FileTypes.toArray(new String[FileTypes.size()]);


        Cursor fileCursor = getApplication().getContentResolver().query(table, column, where, args, null);

        while (fileCursor.moveToNext()) {

            //your code
            int dataColumn = fileCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            int sizeColumn = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);

            String filePath = fileCursor.getString(dataColumn);
            String size = fileCursor.getString(sizeColumn);
            Uri uri = Uri.fromFile(new File(filePath));
            //grant permision for app with package "packegeName", eg. before starting other app via intent

            getApplication().grantUriPermission(getApplication().getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //revoke permisions
            Uri newuri = FileProvider.getUriForFile(getApplication(), "com.alcodes.alcodesgalleryviewerdemo.fileprovider", new File(filePath));
            DocumentFile df = DocumentFile.fromSingleUri(getApplication().getApplicationContext(), newuri);
            //revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            MyFile myFile = new MyFile(df.getName(), String.valueOf(newuri), false);
            myFile.setFileType(type);
            myFile.setFileSize(size);
            FileList.add(myFile);
        }
        fileCursor.close();
        mFileList.setValue(FileList);
        return mFileList;
    }


}
