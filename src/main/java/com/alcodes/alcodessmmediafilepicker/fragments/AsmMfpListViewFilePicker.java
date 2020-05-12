package com.alcodes.alcodessmmediafilepicker.fragments;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmgalleryviewer.activities.AsmGvrMainActivity;
import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpDocumentFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpGithubSampleFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpMainActivity;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpListViewAdapter;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AsmMfpListViewFilePicker extends AppCompatActivity {
    GridView gridView;
    String PickerFileType = "";
    AsmMfpListViewAdapter mAdapter;

    ArrayList<MyFile> myFileList = new ArrayList<>();
    private static final int PERMISSION_STORGE_CODE = 1000;
    private Boolean setChecked = false, searching = false;
    ImageView item_checked;
    public Uri newuri = null;
    ListView listView;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Parcelable savedRecyclerLayoutState;
    private static String LIST_STATE = "list_state";
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
    public String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asm_mfp_fragment_list_view_file_picker);
        //get the grid view from this layout
        recyclerView = findViewById(R.id.recyclerview);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        if (savedInstanceState == null) {
            //get which file type user selected from album
            if (getIntent().getStringExtra("FileType") != null) {
                PickerFileType = getIntent().getStringExtra("FileType");
                init();
            } else {
                promptselection();
            }

            mAdapter = new AsmMfpListViewAdapter(myFileList);
            recyclerView.setAdapter(mAdapter);
        } else {
            myFileList = savedInstanceState.getParcelableArrayList(LIST_STATE);

            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mAdapter = new AsmMfpListViewAdapter(myFileList);
            recyclerView.setAdapter(mAdapter);


        }


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ImageView checkicon = view.findViewById(R.id.list_item_Selected_Image);

                //open folder
                name = myFileList.get(position).getFileName();
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
                        int count = 0;
                        for (int i = 0; i < myFileList.size(); i++) {

                            if (myFileList.get(i).getIsSelected())
                                count++;
                        }
                        //if user cancel the selected item
                        if (count == 0) {
                            setChecked = false;
                            invalidateOptionsMenu();
                        }

                    }

                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putParcelableArrayList(LIST_STATE, myFileList);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.asm_mfp_menu_custom_file_picker, menu);
        inflater.inflate(R.menu.asm_mfp_menu_view_type, menu);
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
//                mAdapter.getFilter().filter(newText);
                mAdapter.getFilter().filter(newText);
                searching = true;
                return true;
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
        if (item.getItemId() == R.id.gridviewVertical) {
            int spanCount = 2;

            recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        }
        if (item.getItemId() == R.id.linearVertical) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


        }

        if (item.getItemId() == R.id.sortingNameAscending) {

            Collections.sort(myFileList, new SortByName());
            mAdapter.notifyDataSetChanged();
        }

        if (item.getItemId() == R.id.sortingNameDescending) {
            Collections.sort(myFileList, Collections.reverseOrder(new SortByName()));
            mAdapter.notifyDataSetChanged();
        }

        if (item.getItemId() == R.id.sortingDateAscending) {

            Collections.sort(myFileList, new SortByDate());
            mAdapter.notifyDataSetChanged();
        }

        if (item.getItemId() == R.id.sortingDateDescending) {
            Collections.sort(myFileList, Collections.reverseOrder(new SortByDate()));
            mAdapter.notifyDataSetChanged();
        }


        return super.onOptionsItemSelected(item);
    }

    public class SortByName implements Comparator<MyFile> {
        @Override
        public int compare(MyFile a, MyFile b) {
            return a.getFileName().compareTo(b.getFileName());
        }
    }

    public class SortByDate implements Comparator<MyFile> {

        @Override
        public int compare(MyFile a, MyFile b) {
            return a.getLastModifyDate().compareTo(b.getLastModifyDate());
        }
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
//            openImageMediaStoreFile("Camera");

//            listAdapter = new AsmMfpListViewAdapter(getApplicationContext(), myFileList);
//            listView.setAdapter(listAdapter);
            mAdapter = new AsmMfpListViewAdapter(myFileList);
            recyclerView.setAdapter(mAdapter);

        } else if (PickerFileType.equals("Video")) {
            openVideoMediaStoreFolder();

            mAdapter = new AsmMfpListViewAdapter(myFileList);
            recyclerView.setAdapter(mAdapter);

        } else if (PickerFileType.equals("Document")) {
            // openDocumentMediaStore();
            //File dir = Environment.getExternalStorageDirectory();
            ProgressBar simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
            simpleProgressBar.setVisibility(View.VISIBLE);

            Intent intent = new Intent(getApplicationContext(), AsmMfpDocumentFilePickerActivity.class);
            startActivity(intent);

            // openPdfMediaStore(dir);
            // mAdapter = new AsmMfpCustomFilePickerAdapter(getApplicationContext(), myFileList);
            // gridView.setAdapter(mAdapter);
        }

    }

    private void openPdfMediaStore(File dir) {
        String pdfPattern = ".pdf";

        File FileList[] = dir.listFiles();

        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {

                if (FileList[i].isDirectory()) {
                    openPdfMediaStore(FileList[i]);
                } else {
                    if (FileList[i].getName().endsWith(pdfPattern)) {
                        //here you have that file.
                        Uri uri = Uri.fromFile(FileList[i]);
                        MyFile myFile = new MyFile(uri.toString(), String.valueOf(FileList[i].getName()), FileList[i].lastModified(), false);
                        myFileList.add(myFile);
                    }
                }
            }
        }

        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();

    }

    //get all document file
    private void openDocumentMediaStore() {
        //document format
        myFileList.clear();


        String pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String doc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");
        String docx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx");
        String xls = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls");
        String xlsx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx");
        String ppt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt");
        String pptx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx");
        String txt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt");
        String rtx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtx");
        String rtf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtf");
        String html = MimeTypeMap.getSingleton().getMimeTypeFromExtension("html");

        //Table
        Uri table = MediaStore.Files.getContentUri("external");
        //Column
        String[] column = {MediaStore.Files.FileColumns.DATA};
        //Where
        String where = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        //args
        String[] args = new String[]{pdf, doc, docx, xls, xlsx, ppt, pptx, txt, rtx, rtf, html};

        Cursor fileCursor = getContentResolver().query(table, column, where, args, null);

        while (fileCursor.moveToNext()) {

            //your code
            int dataColumn = fileCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            String filePath = fileCursor.getString(dataColumn);


            Uri uri = Uri.fromFile(new File(filePath));
            //grant permision for app with package "packegeName", eg. before starting other app via intent

            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //revoke permisions
            newuri = FileProvider.getUriForFile(this, "com.alcodes.alcodesgalleryviewerdemo.fileprovider", new File(filePath));
            DocumentFile df = DocumentFile.fromSingleUri(getApplicationContext(), newuri);
            //revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            MyFile myFile = new MyFile(df.getName(), String.valueOf(newuri), df.lastModified(),false);
            myFileList.add(myFile);

        }
        fileCursor.close();


    }


    private void openImageMediaStoreFile(String folderName) {
        myFileList.clear();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED


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
        int lastModifyColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
        while (cursor.moveToNext()) {
            // Get values of columns for a given video.


            long id = cursor.getInt(idColumn);
            fileName = cursor.getString(nameColumn);
            String size = cursor.getString(sizeColumn);
            Long lastModify = cursor.getLong(lastModifyColumn);

            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            //  for gathering path
            // int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //    path = cursor.getString(file_ColumnIndex);

            MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), lastModify,false);
            myFile.setFileType("Image");


            myFileList.add(myFile);


        }
        cursor.close();

        if (mAdapter != null)
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
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.DATE_MODIFIED


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
            int lastModifyColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
            //int BucketIdCoulmn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.


                long id = cursor.getInt(idColumn);
                fileName = cursor.getString(nameColumn);
                String size = cursor.getString(sizeColumn);
                Long lastModify = cursor.getLong(lastModifyColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                //  for gathering path
                // int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //    path = cursor.getString(file_ColumnIndex);

                MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), lastModify, true);
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
                    MediaStore.Video.Media.BUCKET_ID,
                    MediaStore.Video.Media.DATE_MODIFIED


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
            int BucketIdColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID);
            int lastModifyColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.


                long id = cursor.getInt(idColumn);
                fileName = cursor.getString(nameColumn);
                String bucketId = cursor.getString(BucketIdColumn);
                Long lastModify = cursor.getLong(lastModifyColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                //  for gathering path
                // int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //    path = cursor.getString(file_ColumnIndex);

                MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), lastModify, true);
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
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED


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
        int lastModifyColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);
        while (cursor.moveToNext()) {
            // Get values of columns for a given video.


            long id = cursor.getInt(idColumn);
            fileName = cursor.getString(nameColumn);
            String size = cursor.getString(sizeColumn);
            Long lastModify = cursor.getLong(lastModifyColumn);

            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

            //  for gathering path
            // int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //    path = cursor.getString(file_ColumnIndex);

            MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), lastModify, false);
            myFile.setFileType("Video");
            myFileList.add(myFile);


        }
        cursor.close();


        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, AsmMfpMainActivity.class);
        startActivity(intent);
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private AsmMfpListViewFilePicker.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AsmMfpListViewFilePicker.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    init();
                }
            }
        }
    }


}
