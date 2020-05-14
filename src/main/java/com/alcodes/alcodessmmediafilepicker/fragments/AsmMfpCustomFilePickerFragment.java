package com.alcodes.alcodessmmediafilepicker.fragments;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alcodes.alcodessmgalleryviewer.activities.AsmGvrMainActivity;
import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpDocumentFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpGithubSampleFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpCustomFilePickerRecyclerViewAdapter;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpFragmentCustomFilePickerBinding;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpCustomFilePickerViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AsmMfpCustomFilePickerFragment extends Fragment implements AsmMfpCustomFilePickerRecyclerViewAdapter.CustomFilePickerCallback {

    public static final String EXTRA_INT_MAX_FILE_SELECTION = "EXTRA_INT_MAX_FILE_SELECTION";

    private AsmMfpFragmentCustomFilePickerBinding mDataBinding;
    private NavController mNavController;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;

    private ArrayList<MyFile> myFileList = new ArrayList<>();
    private AsmMfpCustomFilePickerViewModel mfpMainSharedViewModel;

    private AsmMfpCustomFilePickerRecyclerViewAdapter mAdapter;
    private ArrayList<Uri> selectionList = new ArrayList<>();
    private String PickerFileType = "";
    private Boolean isInSideAlbum;
    private Boolean IsGrid;
    public String sharefiletype = "";

    private static final int PERMISSION_STORGE_CODE = 1000;
    private ActionMode mActionMode;
    private Boolean searching;

    private AppCompatActivity mAppCompatActivity;

    private int mMaxFileSelection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Init data binding;
        mDataBinding = AsmMfpFragmentCustomFilePickerBinding.inflate(inflater, container, false);

        return mDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init navigation component.
        mNavController = Navigation.findNavController(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Init Max File Selection
        mMaxFileSelection = requireActivity().getIntent().getExtras().getInt(EXTRA_INT_MAX_FILE_SELECTION, 0);

        //Init AppCompatActivity
        mAppCompatActivity = ((AppCompatActivity) requireActivity());

        //Init Two Layout Manager - Grid + Linear
        mLinearLayoutManager = new LinearLayoutManager(requireContext());
        mGridLayoutManager = new GridLayoutManager(requireContext(), 2);

        //Set Default Layout to Linear

        mDataBinding.CustomRecyclerView.setLayoutManager(mLinearLayoutManager);

        mfpMainSharedViewModel = new ViewModelProvider(
                mNavController.getBackStackEntry(R.id.asm_mfp_mainfragment),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(AsmMfpCustomFilePickerViewModel.class);


      

        if(mfpMainSharedViewModel.getMaxSelection().getValue() != null){
            mMaxFileSelection = mfpMainSharedViewModel.getMaxSelection().getValue();
        }else{
            mfpMainSharedViewModel.setMaxSelection(mMaxFileSelection);
        }

        if(mfpMainSharedViewModel.getSearching().getValue() != null){

            searching = mfpMainSharedViewModel.getSearching().getValue();
        } else {
            mfpMainSharedViewModel.setSearching(false);
        }
        if (mfpMainSharedViewModel.getIsGrid().getValue() != null) {
            IsGrid = mfpMainSharedViewModel.getIsGrid().getValue();
        } else {
            mfpMainSharedViewModel.setIsGrid(false);
        }
        if (mfpMainSharedViewModel.getIsInsideAlbum().getValue() != null) {
            isInSideAlbum = mfpMainSharedViewModel.getIsInsideAlbum().getValue();
        } else {
            mfpMainSharedViewModel.setIsInsideAlbum(false);
        }

        if (mfpMainSharedViewModel.getMyFileList().getValue() != null &&
                mfpMainSharedViewModel.getMyFileList().getValue().size() != 0 &&
                mfpMainSharedViewModel.getPickerFileType().getValue() != null) {
            myFileList = mfpMainSharedViewModel.getMyFileList().getValue();
            initAdapter();
            PickerFileType = mfpMainSharedViewModel.getPickerFileType().getValue();
        } else {
            if (requireActivity().getIntent().getStringExtra("FileType") != null) {
                PickerFileType = requireActivity().getIntent().getStringExtra("FileType");
                mfpMainSharedViewModel.setPickerFileType(PickerFileType);
                init();
            } else {
                promptSelection();
            }

            initAdapter();

        }

        //for action mode search bar
        mDataBinding.customFilePickerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.getFilter().filter(s.toString());
            }
        });

        //the clear text btn inside custom search bar
        mDataBinding.customFilePickerClearTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataBinding.customFilePickerEditText.setText(null);

                //to reset adapter as refresh to prevent selected position duplicated after used search
                initAdapter();
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        if (mfpMainSharedViewModel.getMyFileList().getValue() != null) {
            myFileList = mfpMainSharedViewModel.getMyFileList().getValue();
        }
        if (mfpMainSharedViewModel.getSelectionList().getValue() != null && mfpMainSharedViewModel.getSelectionList().getValue().size() != 0) {
            selectionList = mfpMainSharedViewModel.getSelectionList().getValue();
            if (mActionMode == null)
                mActionMode = mAppCompatActivity.startSupportActionMode(mActionModeCallback);
        }
        initAdapter();
        if (mfpMainSharedViewModel.getIsGrid().getValue() != null) {
            IsGrid = mfpMainSharedViewModel.getIsGrid().getValue();
            if (IsGrid) {
                mDataBinding.CustomRecyclerView.setLayoutManager(mGridLayoutManager);
            } else {
                mDataBinding.CustomRecyclerView.setLayoutManager(mLinearLayoutManager);
            }
        }
        if (mfpMainSharedViewModel.getIsInsideAlbum().getValue() != null) {
            isInSideAlbum = mfpMainSharedViewModel.getIsInsideAlbum().getValue();
        }
        if (mfpMainSharedViewModel.getSearching().getValue() != null) {
            searching = mfpMainSharedViewModel.getSearching().getValue();
            if (searching) {
                mDataBinding.customFilePickerEditText.setVisibility(View.VISIBLE);
                mDataBinding.customFilePickerClearTextBtn.setVisibility(View.VISIBLE);
            }
            //click search btn for second time to hide the custom search bar
            else {
                mDataBinding.customFilePickerEditText.setVisibility(View.INVISIBLE);
                mDataBinding.customFilePickerClearTextBtn.setVisibility(View.INVISIBLE);
            }
        }
        if (mfpMainSharedViewModel.getSortingStyle().getValue() != null) {
            sortingMyFileList(mfpMainSharedViewModel.getSortingStyle().getValue());
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mfpMainSharedViewModel.saveMyFileList(myFileList);
        mfpMainSharedViewModel.saveSelectionList(selectionList);
        mfpMainSharedViewModel.setIsInsideAlbum(isInSideAlbum);
        mfpMainSharedViewModel.setSearching(searching);
        mfpMainSharedViewModel.setMaxSelection(mMaxFileSelection);
        mfpMainSharedViewModel.setPickerFileType(PickerFileType);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.asm_mfp_menu_custom_file_picker, menu);
        //for select item

        //for search filter
        MenuItem searchItem = menu.findItem(R.id.FilePicker_SearchFilter);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");
        //resume search test when rotation or leave foreground event has occurred
        if (!searching) {
            if (mfpMainSharedViewModel.getSearchingText().getValue() != null && !mfpMainSharedViewModel.getSearchingText().getValue().equals("")) {
                searchItem.expandActionView();
                searchView.setQuery(mfpMainSharedViewModel.getSearchingText().getValue() + "", true);
            }
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                mfpMainSharedViewModel.setSearchingText(newText);
                return false;
            }
        });

        MenuItem selectAllItem = menu.findItem(R.id.SelectAll);
        if (isInSideAlbum) {
            selectAllItem.setVisible(true);
        } else {
            selectAllItem.setVisible(false);
        }
    }

    private void sortingMyFileList(String sortingStyle) {
        if (sortingStyle.equals("SortingNameAscending")) {
            Collections.sort(myFileList, new SortByName());
        } else if (sortingStyle.equals("SortingNameDescending")) {
            Collections.sort(myFileList, Collections.reverseOrder(new SortByName()));
        } else if (sortingStyle.equals("SortingDateAscending")) {
            Collections.sort(myFileList, new SortByDate());
        } else if (sortingStyle.equals("SortingDateDescending")) {
            Collections.sort(myFileList, Collections.reverseOrder(new SortByDate()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isInSideAlbum) {
                if (PickerFileType.equals("Image")) {
                    myFileList.clear();
                    mfpMainSharedViewModel.clearMyFileList();
                    initAdapter();
                    openImageMediaStoreFolder();
                    isInSideAlbum = false;
                    mfpMainSharedViewModel.setIsInsideAlbum(isInSideAlbum);
                    mAppCompatActivity.invalidateOptionsMenu();
                    mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                } else {
                    myFileList.clear();
                    mfpMainSharedViewModel.clearMyFileList();
                    initAdapter();
                    openVideoMediaStoreFolder();
                    isInSideAlbum = false;
                    mfpMainSharedViewModel.setIsInsideAlbum(isInSideAlbum);
                    mAppCompatActivity.invalidateOptionsMenu();
                    mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            } else {
                mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
        //to change layout to grid or recycler view
        if (item.getItemId() == R.id.Custom_ChangeLayout) {
            //if current layout is grid then change to recycler else change to grid
            if (IsGrid) {
                mDataBinding.CustomRecyclerView.setLayoutManager(mLinearLayoutManager);
                IsGrid = false;
                mfpMainSharedViewModel.setIsGrid(IsGrid);
                item.setTitle("Grid View Format");
            } else {
                mDataBinding.CustomRecyclerView.setLayoutManager(mGridLayoutManager);
                IsGrid = true;
                mfpMainSharedViewModel.setIsGrid(IsGrid);
                item.setTitle("List View Format");
            }
        }
        if (item.getItemId() == R.id.sortingNameAscending) {
            sortingMyFileList("SortingNameAscending");
            mfpMainSharedViewModel.setSortingStyle("SortingNameAscending");
            initAdapter();
        }
        if (item.getItemId() == R.id.sortingNameDescending) {
            sortingMyFileList("SortingNameDescending");
            mfpMainSharedViewModel.setSortingStyle("SortingNameDescending");
            initAdapter();
        }
        if (item.getItemId() == R.id.sortingDateAscending) {
            sortingMyFileList("SortingDateAscending");
            mfpMainSharedViewModel.setSortingStyle("SortingDateAscending");
            initAdapter();
        }
        if (item.getItemId() == R.id.sortingDateDescending) {
            sortingMyFileList("SortingDateDescending");
            mfpMainSharedViewModel.setSortingStyle("SortingDateDescending");
            initAdapter();
        }

        if (item.getItemId() == R.id.SelectAll) {
            //Clear it to avoid duplicate data in the same array list
            selectionList.clear();
            //Before selecting all, check whether there is a max file selection.
            for (int i = 0; i < (mMaxFileSelection != 0 ? mMaxFileSelection : myFileList.size()); i++) {
                myFileList.get(i).setIsSelected(true);
                selectionList.add(Uri.parse(myFileList.get(i).getFileUri()));
            }

            mfpMainSharedViewModel.saveSelectionList(selectionList);

            if (mActionMode == null)
                mActionMode = mAppCompatActivity.startSupportActionMode(mActionModeCallback);

            initAdapter();
        }

        if (item.getItemId() == R.id.SelectFileType){
            promptSelection();
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

    private void promptSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Which file type you prefer ?");
        builder.setCancelable(false);
        builder.setMessage("select one of these");

        builder.setPositiveButton("Image", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PickerFileType = "Image";
                mfpMainSharedViewModel.setPickerFileType(PickerFileType);
                init();

            }
        });
        builder.setNegativeButton("Video", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PickerFileType = "Video";
                mfpMainSharedViewModel.setPickerFileType(PickerFileType);
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
            mDataBinding.simpleProgressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(requireContext(), AsmMfpDocumentFilePickerActivity.class);
            intent.putExtra(AsmMfpCustomFilePickerFragment.EXTRA_INT_MAX_FILE_SELECTION, mMaxFileSelection);

            startActivity(intent);
        }
    }

    // for folder
    private void openImageMediaStoreFolder() {
        //list to get file in same folder
        myFileList.clear();
        mfpMainSharedViewModel.clearMyFileList();
        ArrayList<String> filelist = new ArrayList<>();
        if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, PERMISSION_STORGE_CODE);
        } else {
            String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.DATE_MODIFIED
            };

            String path = "", foldername = "";

            Cursor cursor = requireActivity().getContentResolver().query(
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
            int lastModifyColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.

                //to get uri
                long id = cursor.getInt(idColumn);

                //to store in phonephoto
                int FolderID = cursor.getInt(folderidColumn);
                foldername = cursor.getString(nameColumn);
                Long lastModify = cursor.getLong(lastModifyColumn);


                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


                MyFile myFile = new MyFile(foldername, String.valueOf(contentUri), lastModify, true);
                myFile.setFileType("Image");
                myFile.setIsFolder(true);
                myFile.setFolderID(FolderID);

                if (!filelist.contains(foldername)) {
                    filelist.add(foldername);
                    myFileList.add(myFile);
                    mfpMainSharedViewModel.addFileToMyFileList(myFile);
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

        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
        //after finish for external then continue to internal storage
        if (mActionMode == null && selectionList.size() != 0) {
            mActionMode = mAppCompatActivity.startSupportActionMode(mActionModeCallback);
        }
    }

    private void openVideoMediaStoreFolder() {
        //list to get file in same folder
        myFileList.clear();
        mfpMainSharedViewModel.clearMyFileList();
        ArrayList<String> filelist = new ArrayList<>();
        if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
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

            Cursor cursor = requireActivity().getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );
            // Cache column indices. (all in int variable

            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            int lastModifyColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.


                long id = cursor.getInt(idColumn);
                fileName = cursor.getString(nameColumn);
                Long lastModify = cursor.getLong(lastModifyColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), lastModify, true);
                myFile.setFileType("Video");

                if (!filelist.contains(fileName)) {
                    filelist.add(fileName);
                    myFileList.add(myFile);
                    mfpMainSharedViewModel.addFileToMyFileList(myFile);
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

        if (mActionMode == null && selectionList.size() != 0) {
            mActionMode = mAppCompatActivity.startSupportActionMode(mActionModeCallback);
        }
    }

    private void openImageMediaStoreFile(int folderID) {
        myFileList.clear();
        mfpMainSharedViewModel.clearMyFileList();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED
        };

        String fileName = "";
        Cursor cursor = requireActivity().getContentResolver().query(
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
        int lastModifyColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);

        while (cursor.moveToNext()) {
            // Get values of columns for a given video.


            long id = cursor.getInt(idColumn);
            fileName = cursor.getString(nameColumn);
            Long lastModify = cursor.getLong(lastModifyColumn);

            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), lastModify, false);
            myFile.setFileType("Image");

            //to put back seleceted status to pervious selected item
            for (int i = 0; i < selectionList.size(); i++) {
                if (myFile.getFileUri().equals(selectionList.get(i))) {
                    myFile.setIsSelected(true);
                }
            }

            myFileList.add(myFile);
            mfpMainSharedViewModel.addFileToMyFileList(myFile);

        }
        cursor.close();
    }

    private void openVideoMediaStoreFile(int folderid) {
        myFileList.clear();
        mfpMainSharedViewModel.clearMyFileList();
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED
        };
        String path = "", fileName = "";

        Cursor cursor = requireActivity().getContentResolver().query(
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
            for (int i = 0; i < selectionList.size(); i++) {
                if (myFile.getFileUri().equals(selectionList.get(i))) {
                    myFile.setIsSelected(true);
                }
            }
            myFileList.add(myFile);
            mfpMainSharedViewModel.addFileToMyFileList(myFile);
        }
        cursor.close();
    }

    private void initAdapter() {
        mAdapter = new AsmMfpCustomFilePickerRecyclerViewAdapter(requireContext(), myFileList, AsmMfpCustomFilePickerFragment.this, selectionList.size());
        //Set the Maximum File Selection
        mAdapter.setMaxFileSelection(mMaxFileSelection);

        mDataBinding.CustomRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFolderClicked(int folderid) {
        if (PickerFileType.equals("Image"))
            openImageMediaStoreFile(folderid);
        else
            openVideoMediaStoreFile(folderid);

        mAppCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_black_24dp);// set drawable icon
        mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isInSideAlbum = true;
        mfpMainSharedViewModel.setIsInsideAlbum(isInSideAlbum);
        mAppCompatActivity.invalidateOptionsMenu();
        initAdapter();
    }

    @Override
    public void onAlbumItemUnSelected(Uri uri) {

        for (int i = (selectionList.size() - 1); i >= 0; i--) {
            if (selectionList.get(i).equals(uri)) {
                selectionList.remove(i);
                break;
            }
        }
        mfpMainSharedViewModel.saveSelectionList(selectionList);

        if (mActionMode != null)
            mActionMode.setTitle(selectionList.size() + "item(s) selected");

        if (selectionList.size() == 0) {
            mActionMode.setTitle("Alcodes Gallery Viewer Demo");
            mActionMode.finish();
        }

        //update the selection count for limit user selection
        mAdapter.setSelectionCount(selectionList.size());
    }

    @Override
    public void onAlbumItemSelected(Uri uri) {
        //get position
        isInSideAlbum = true;

        mfpMainSharedViewModel.setIsInsideAlbum(isInSideAlbum);
        selectionList.add(uri);

        mfpMainSharedViewModel.saveSelectionList(selectionList);

        if (mActionMode == null) {
            mActionMode = mAppCompatActivity.startSupportActionMode(mActionModeCallback);
        }
        mActionMode.setTitle(selectionList.size() + "item(s) selected");

        //update the selection count for limit user selection
        mAdapter.setSelectionCount(selectionList.size());
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        SearchView mSearchView;
        Menu actionMenu;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.asm_mfp_menu_custom_file_picker, menu);
            //for select item

            MenuItem checkItem = menu.findItem(R.id.DoneSelection);
            checkItem.setVisible(true);
            actionMenu = menu;
            //share item with other app
            MenuItem shareItem = menu.findItem(R.id.ShareWith);
            shareItem.setVisible(true);

            //unselection
            MenuItem unSelectItem = menu.findItem(R.id.UnSelectAll);
            unSelectItem.setVisible(true);

            mode.setTitle(selectionList.size() + "item(s) selected");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.DoneSelection) {
                ArrayList<String> mFileList = new ArrayList<>();
                for (int i = 0; i < selectionList.size(); i++) {
                    mFileList.add(selectionList.get(i).toString());
                }

                if (mFileList != null) {
                    Intent intent = new Intent(requireContext(), AsmGvrMainActivity.class);
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

            //unselect the user selection
            if (item.getItemId() == R.id.UnSelectAll) {
                selectionList.clear();
                mfpMainSharedViewModel.clearSelectionList();
                //update recycler view data and ui

                for (int i = 0; i < myFileList.size(); i++) {
                    if (myFileList.get(i).getIsSelected())
                        myFileList.get(i).setIsSelected(false);
                }
                mfpMainSharedViewModel.saveMyFileList(myFileList);

                initAdapter();

                //close actionmode
                mActionMode.finish();
            }

            if (item.getItemId() == R.id.FilePicker_SearchFilter) {
                if (!searching) {
                    mDataBinding.customFilePickerEditText.setVisibility(View.VISIBLE);
                    mDataBinding.customFilePickerClearTextBtn.setVisibility(View.VISIBLE);
                    searching = true;
                    mfpMainSharedViewModel.setSearching(searching);
                }
                //click search btn for second time to hide the custom search bar
                else {
                    mDataBinding.customFilePickerEditText.setVisibility(View.INVISIBLE);
                    mDataBinding.customFilePickerClearTextBtn.setVisibility(View.INVISIBLE);
                    searching = false;
                    mfpMainSharedViewModel.setSearching(searching);
                }
            }
            return true;
        }

        //when click on action mode back/home button
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mDataBinding.customFilePickerEditText.setVisibility(View.INVISIBLE);
            mDataBinding.customFilePickerClearTextBtn.setVisibility(View.INVISIBLE);
            searching = false;
            mfpMainSharedViewModel.setSearching(searching);
            mActionMode = null;


            //Deselect All
            selectionList.clear();
            //update recycler view data and ui

            for (int i = 0; i < myFileList.size(); i++) {
                if (myFileList.get(i).getIsSelected())
                    myFileList.get(i).setIsSelected(false);
            }
            //Clear selection list when back button clicked
            selectionList.clear();
            mfpMainSharedViewModel.clearSelectionList();
            initAdapter();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }
            }
        }
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
}
