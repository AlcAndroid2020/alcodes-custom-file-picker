package com.alcodes.alcodessmmediafilepicker.activities;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.alcodes.alcodessmgalleryviewer.activities.AsmGvrMainActivity;
import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpCustomFilePickerAdapter;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.util.ArrayList;

public class AsmMfpCustomFilePicker extends AppCompatActivity {
    GridView gridView;
    String PickerFileType = "";
    AsmMfpCustomFilePickerAdapter mAdapter;
    ArrayList<MyFile> myFileList = new ArrayList<>();
    private static final int PERMISSION_STORGE_CODE = 1000;
    private Boolean setChecked = false,searching=false;
    ImageView item_checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asm_mfp_activity_custom_file_picker);
        //get the grid view from this layout
        gridView = findViewById(R.id.gridView_Album);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImageView checkicon=view.findViewById(R.id.Album_item_Selected_Image);

                //open folder
                String name = myFileList.get(position).getFileName();
                if (myFileList.get(position).getIsFolder()) {
                    if (PickerFileType.equals("Image"))
                        openImageMediaStoreFile(name);
                    else
                        openVideoMediaStoreFile(name);
                } else {
                    //click on album files
                    //show the check option
                    setChecked = true;
                    //is allow option menu to show check icon
                    invalidateOptionsMenu();

                    //havent select yet
                    if (!myFileList.get(position).getIsSelected()) {

                        checkicon.setVisibility(View.VISIBLE);

                        myFileList.get(position).setIsSelected(true);
                    } else {
                        //selected
                        checkicon.setVisibility(View.INVISIBLE);


                        myFileList.get(position).setIsSelected(false);
                        int count=0;
                        for (int i = 0; i < myFileList.size(); i++) {

                            if (myFileList.get(i).getIsSelected())
                                count++;

                        }

                        //if user cancel the selected item
                        if(count==0) {
                            setChecked = false;
                            invalidateOptionsMenu();
                        }

                    }

                }
            }
        });






        //get which file type user selected from album
        if (getIntent().getStringExtra("FileType") != null) {
            PickerFileType = getIntent().getStringExtra("FileType");
            init();
        } else {
            promptselection();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.asm_mfp_menu_custom_file_picker, menu);
        //for select item
        MenuItem checkItem = menu.findItem(R.id.DoneSelection);

        if (setChecked)
            checkItem.setVisible(true);
        else
            checkItem.setVisible(false);



        //for search filter
        MenuItem searchItem=menu.findItem(R.id.FilePicker_SearchFilter);
        SearchView searchView=(SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                searching=true;
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


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
        return super.onOptionsItemSelected(item);
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
        builder.setNeutralButton("cancel", null);
        builder.show();

    }

    public void init() {
        //identify what file type are user pick
        if (PickerFileType.equals("Image"))
            openImageMediaStoreFolder();
        else
            openVideoMediaStoreFolder();
        mAdapter = new AsmMfpCustomFilePickerAdapter(getApplicationContext(), myFileList);
        gridView.setAdapter(mAdapter);

    }


    private void openImageMediaStoreFile(String folderName) {
        myFileList.clear();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME


        };
        String path = "", fileName = "";

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Images.Media.DATA + " like ? ",
                new String[]{"%" + folderName + "%"},
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
            String size = cursor.getString(sizeColumn);

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

        mAdapter.notifyDataSetChanged();

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
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID


            };
            String path = "", fileName = "";

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
            int sizeColumn = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
            //int BucketIdCoulmn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.


                long id = cursor.getInt(idColumn);
                fileName = cursor.getString(nameColumn);
                String size = cursor.getString(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                //  for gathering path
                // int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //    path = cursor.getString(file_ColumnIndex);

                MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), true);
                myFile.setFileType("Image");
                myFile.setIsFolder(true);


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
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();


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
            // int sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
            int BucketIdCoulmn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.


                long id = cursor.getInt(idColumn);
                fileName = cursor.getString(nameColumn);
                String bucketId = cursor.getString(BucketIdCoulmn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                //  for gathering path
                // int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //    path = cursor.getString(file_ColumnIndex);

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
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();

    }

    private void openVideoMediaStoreFile(String foldername) {
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
                new String[]{"%" + foldername + "%"},
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

        mAdapter.notifyDataSetChanged();
    }

}
