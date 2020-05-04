package com.alcodes.alcodessmmediafilepicker.activities;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmgalleryviewer.activities.AsmGvrMainActivity;
import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpCustomFilePickerRecyclerViewAdapter;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.util.ArrayList;

public class AsmMfpCustomFilePicker extends AppCompatActivity implements AsmMfpCustomFilePickerRecyclerViewAdapter.CustomFilePickerCallback {
    private String PickerFileType = "";
    private AsmMfpCustomFilePickerRecyclerViewAdapter rcAdapter;
    private ArrayList<MyFile> myFileList = new ArrayList<>();
    private static final int PERMISSION_STORGE_CODE = 1000;
    private Boolean setChecked = false, searching = false;
    public Uri newuri = null;
    private RecyclerView customRecyclerView;
    private Boolean IsGrid = false;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;

    private Parcelable savedRecyclerLayoutState;
    private static String LIST_STATE = "list_state";
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    private Boolean isInSideAlbum = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asm_mfp_activity_custom_file_picker);

        customRecyclerView = (RecyclerView) findViewById(R.id.Custom_Recycler_View);
        // set a GridLayoutManager with default vertical orientation and 3 number of columns

        linearLayoutManager = new LinearLayoutManager(this);

        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);

        customRecyclerView.setLayoutManager(linearLayoutManager);
        if (savedInstanceState != null) {

            myFileList = savedInstanceState.getParcelableArrayList(LIST_STATE);

            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);

            rcAdapter = new AsmMfpCustomFilePickerRecyclerViewAdapter(getApplicationContext(), myFileList, AsmMfpCustomFilePicker.this);
            customRecyclerView.setAdapter(rcAdapter);


        } else {
            if (getIntent().getStringExtra("FileType") != null) {
                PickerFileType = getIntent().getStringExtra("FileType");
                init();
            } else {
                promptselection();
            }


            rcAdapter = new AsmMfpCustomFilePickerRecyclerViewAdapter(getApplicationContext(), myFileList, AsmMfpCustomFilePicker.this);
            customRecyclerView.setAdapter(rcAdapter);
        }


    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(LIST_STATE, myFileList);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, customRecyclerView.getLayoutManager().onSaveInstanceState());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.asm_mfp_menu_custom_file_picker, menu);
        //for select item
        MenuItem checkItem = menu.findItem(R.id.DoneSelection);
        MenuItem shareItem = menu.findItem(R.id.ShareWith);

        if (setChecked) {
            checkItem.setVisible(true);
            shareItem.setVisible(true);
        } else {
            checkItem.setVisible(false);
            shareItem.setVisible(false);
        }


        //for search filter
        MenuItem searchItem = menu.findItem(R.id.FilePicker_SearchFilter);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

               rcAdapter.getFilter().filter(newText);
    /*    ArrayList<MyFile> filterlist=new ArrayList<>();
                for (int i=0;i<myFileList.size();i++){
                    if(myFileList.get(i).getFileName().equals(newText )){
                        filterlist.add(myFileList.get(i));

                    }

                }
                rcAdapter = new AsmMfpCustomFilePickerRecyclerViewAdapter(getApplicationContext(),filterlist, AsmMfpCustomFilePicker.this);
                customRecyclerView.setAdapter(rcAdapter);
                rcAdapter.notifyDataSetChanged(); */
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public String sharefiletype = "";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.DoneSelection) {
            ArrayList<String> mFileList = new ArrayList<>();
            for (int i = 0; i < myFileList.size(); i++) {

                if (myFileList.get(i).getIsSelected())
                    mFileList.add(myFileList.get(i).getFileUri());

            }
            if (mFileList != null) {
                Intent intent = new Intent(this, AsmGvrMainActivity.class);
                intent.putStringArrayListExtra(AsmMfpGithubSampleFilePickerActivity.EXTRA_STRING_ARRAY_FILE_URI, mFileList);

                startActivity(intent);
            }

        }
        if (item.getItemId() == R.id.ShareWith) {
            ArrayList<String> mFileList = new ArrayList<>();
            for (int i = 0; i < myFileList.size(); i++) {

                if (myFileList.get(i).getIsSelected()) {
                    mFileList.add(myFileList.get(i).getFileUri());
                    sharefiletype = myFileList.get(i).getFileType();
                }

            }
            if (mFileList != null) {
                StartShare(mFileList);

            }
        }
        if (item.getItemId() == android.R.id.home) {
            if (isInSideAlbum) {

                if (PickerFileType.equals("Image")) {
                    myFileList.clear();

                    rcAdapter = new AsmMfpCustomFilePickerRecyclerViewAdapter(getApplicationContext(), myFileList, AsmMfpCustomFilePicker.this);
                    customRecyclerView.setAdapter(rcAdapter);

                    openImageMediaStoreFolder();
                    isInSideAlbum = false;
                    setChecked=false;
                    invalidateOptionsMenu();

                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                } else {
                    myFileList.clear();

                    rcAdapter = new AsmMfpCustomFilePickerRecyclerViewAdapter(getApplicationContext(), myFileList, AsmMfpCustomFilePicker.this);
                    customRecyclerView.setAdapter(rcAdapter);

                    openVideoMediaStoreFolder();
                    isInSideAlbum = false;
                    setChecked=false;
                    invalidateOptionsMenu();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }

        }


        //to change layout to grid or recycler view
        if (item.getItemId() == R.id.Custom_ChangeLayout) {
            //if current layout is grid then change to recycler else change to grid
            if (IsGrid) {
                customRecyclerView.setLayoutManager(linearLayoutManager);
                IsGrid = false;
            } else {
                customRecyclerView.setLayoutManager(gridLayoutManager);
                IsGrid = true;
            }


        }
        return super.onOptionsItemSelected(item);
    }

    public void StartShare(ArrayList<String> mFileList) {
        String Type = "";

        if (sharefiletype.equals("Image")) {
            Type = "image/jpeg";
        } else if (sharefiletype.equals("Video")) {
            Type = "video/*";
        } else {
            Type = "application/pdf";
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType(Type);

        ArrayList<Uri> files = new ArrayList<>();

        for (String path : mFileList /* List of the files you want to send */) {
            String shareUri = path;

            files.add(Uri.parse(shareUri));
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);
    }

    private void promptselection() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Which file type you prefer ?");

        builder.setMessage("select one of these");


        builder.setPositiveButton("Image", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PickerFileType = "Image";
                init();

            }
        });
        builder.setNegativeButton("Video", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PickerFileType = "Video";
                init();

            }
        });
        builder.setNeutralButton("Document", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PickerFileType = "Document";
                init();
            }
        });
        builder.show();

    }

    public void init() {
        //identify what file type are user pick
        if (PickerFileType.equals("Image")) {
            openImageMediaStoreFolder();

        } else if (PickerFileType.equals("Video")) {
            openVideoMediaStoreFolder();

        } else if (PickerFileType.equals("Document")) {

            ProgressBar simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
            simpleProgressBar.setVisibility(View.VISIBLE);

            Intent intent = new Intent(getApplicationContext(), AsmMfpDocumentFilePickerActivity.class);
            startActivity(intent);


        }

    }


    private void openImageMediaStoreFile(int folderID) {
        myFileList.clear();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME


        };
        String fileName = "";

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Images.Media.BUCKET_ID + " like ? ",
                new String[]{"%" + folderID + "%"},
                null
        );
        // Cache column indices. (all in int variable

        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
        int sizeColumn = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);

        while (cursor.moveToNext()) {
            // Get values of columns for a given video.


            long id = cursor.getInt(idColumn);
            fileName = cursor.getString(nameColumn);

            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


            //  for gathering path
            // int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //    path = cursor.getString(file_ColumnIndex);

            MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), false);
            myFile.setFileType("Image");


            myFileList.add(myFile);


        }
        cursor.close();
        if (rcAdapter != null)
            rcAdapter.notifyDataSetChanged();


    }


    // for folder
    private void openImageMediaStoreFolder() {
        //list to get file in same folder

        ArrayList<String> filelist = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, PERMISSION_STORGE_CODE);
        } else {
            String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID


            };
            String path = "", foldername = "";

            Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );
            // Cache column indices. (all in int variable

            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int folderidColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.

                //to get uri
                long id = cursor.getInt(idColumn);

                //to store in phonephoto
                int FolderID = cursor.getInt(folderidColumn);
                foldername = cursor.getString(nameColumn);


                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


                MyFile myFile = new MyFile(foldername, String.valueOf(contentUri), true);
                myFile.setFileType("Image");
                myFile.setIsFolder(true);
                myFile.setFolderID(FolderID);

                if (!filelist.contains(foldername)) {
                    filelist.add(foldername);
                    myFileList.add(myFile);

                } else {
                    for (int i = 0; i < myFileList.size(); i++) {
                        if (myFileList.get(i).getFileName().equals(foldername)) {
                            int count = myFileList.get(i).getCount();
                            myFileList.get(i).setCount(count + 1);

                        }
                    }
                }


            }
            cursor.close();


        }
        if (rcAdapter != null)
            rcAdapter.notifyDataSetChanged();
        //after finish for external then continue to internal storage
    }


    private void openVideoMediaStoreFolder() {
        //list to get file in same folder
        ArrayList<String> filelist = new ArrayList<>();
        myFileList.clear();
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, PERMISSION_STORGE_CODE);
        } else {
            String[] projection = new String[]{
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID


            };
            String path = "", fileName = "";

            Cursor cursor = getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );
            // Cache column indices. (all in int variable

            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.


                long id = cursor.getInt(idColumn);
                fileName = cursor.getString(nameColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);


                MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), true);
                myFile.setFileType("Video");

                if (!filelist.contains(fileName)) {
                    filelist.add(fileName);
                    myFileList.add(myFile);
                } else {
                    for (int i = 0; i < myFileList.size(); i++) {
                        if (myFileList.get(i).getFileName().equals(fileName)) {
                            int count = myFileList.get(i).getCount();
                            myFileList.get(i).setCount(count + 1);
                        }
                    }
                }


            }
            cursor.close();


        }
        if (rcAdapter != null)
            rcAdapter.notifyDataSetChanged();
    }

    private void openVideoMediaStoreFile(int folderid) {
        myFileList.clear();
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DISPLAY_NAME


        };
        String path = "", fileName = "";

        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Video.Media.DATA + " like ? ",
                new String[]{"%" + folderid + "%"},
                null
        );
        // Cache column indices. (all in int variable

        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        int nameColumn = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
        int sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
        while (cursor.moveToNext()) {
            // Get values of columns for a given video.


            long id = cursor.getInt(idColumn);
            fileName = cursor.getString(nameColumn);
            String size = cursor.getString(sizeColumn);

            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

            //  for gathering path
            // int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //    path = cursor.getString(file_ColumnIndex);

            MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), false);
            myFile.setFileType("Video");
            myFileList.add(myFile);


        }
        cursor.close();

        rcAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, AsmMfpMainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onFolderClicked(int folderid) {
        if (PickerFileType.equals("Image"))
            openImageMediaStoreFile(folderid);
        else
            openVideoMediaStoreFile(folderid);
        //change to album form
        customRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        IsGrid = true;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_black_24dp);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isInSideAlbum = true;
        invalidateOptionsMenu();

    }

    @Override
    public void onFileCliked(ArrayList<MyFile> filelist) {
        myFileList = filelist;
        int countSelect = 0;
        for (int i = 0; i < myFileList.size(); i++) {
            if (myFileList.get(i).getIsSelected())
                countSelect++;


        }
        if (countSelect > 0)
            setChecked = true;

            else
                setChecked=false;

        //to show back button


        invalidateOptionsMenu();

    }
}
