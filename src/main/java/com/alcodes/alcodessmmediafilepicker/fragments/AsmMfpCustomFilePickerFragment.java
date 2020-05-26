package com.alcodes.alcodessmmediafilepicker.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alcodes.alcodessmgalleryviewer.activities.AsmGvrMainActivity;
import com.alcodes.alcodessmgalleryviewer.fragments.AsmGvrMainFragment;
import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpDocumentFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpCustomFilePickerRecyclerViewAdapter;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpFragmentCustomFilePickerBinding;
import com.alcodes.alcodessmmediafilepicker.databinding.bindingcallbacks.SortByDialogCallback;
import com.alcodes.alcodessmmediafilepicker.dialogs.AsmMfpSortByDialog;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpCustomFilePickerViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AsmMfpCustomFilePickerFragment extends Fragment
        implements AsmMfpCustomFilePickerRecyclerViewAdapter.CustomFilePickerCallback, SortByDialogCallback {

    private static final String DEFAULT_SORTING_STYLE = "SortingDateDescending";
    private static final int OPEN_DOCUMENT_REQUEST_CODE = 42;
    private boolean mBeenDirectedToDocumentPicker = false;

    private AsmMfpFragmentCustomFilePickerBinding mDataBinding;
    private NavController mNavController;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;

    private ArrayList<MyFile> myFileList = new ArrayList<>();
    private ArrayList<String> mFileListForAndroid10 = new ArrayList<>();
    private AsmMfpCustomFilePickerViewModel mfpCustomFilePickerViewModel;

    private AsmMfpCustomFilePickerRecyclerViewAdapter mAdapter;
    private ArrayList<Uri> selectionList = new ArrayList<>();
    private String PickerFileType = "";
    private Boolean isInSideAlbum;
    private Boolean IsGrid;
    private String sharefiletype = "";
    private int mColor, mTheme;
    private Boolean IsSelectAll = false;
    private SearchView searchView;

    private static final int PERMISSION_STORGE_CODE = 1000;

    private ActionBar mActionBar;

    private AppCompatActivity mAppCompatActivity;

    private int mMaxFileSelection;
    public static final String EXTRA_STRING_ARRAY_FILE_URI = "EXTRA_STRING_ARRAY_FILE_URI";

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
        Intent intent = requireActivity().getIntent();
        Bundle extras = intent.getExtras();

        //Init AppCompatActivity
        mAppCompatActivity = ((AppCompatActivity) requireActivity());
        mAppCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_black_24dp);// set drawable icon

        //Init Two Layout Manager - Grid + Linear
        mLinearLayoutManager = new LinearLayoutManager(requireContext());
        mGridLayoutManager = new GridLayoutManager(requireContext(), 2);

        //Set Default Layout to Linear
        mDataBinding.CustomRecyclerView.setLayoutManager(mLinearLayoutManager);

        mfpCustomFilePickerViewModel = new ViewModelProvider(
                mNavController.getBackStackEntry(R.id.asm_mfp_nav_main),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(AsmMfpCustomFilePickerViewModel.class);

        if (mfpCustomFilePickerViewModel.getMaxSelection().getValue() != null) {
            mMaxFileSelection = mfpCustomFilePickerViewModel.getMaxSelection().getValue();
        } else {
            mfpCustomFilePickerViewModel.setMaxSelection(mMaxFileSelection);
        }

        //Set Default Sorting in View Model
        mfpCustomFilePickerViewModel.setSortingStyle(DEFAULT_SORTING_STYLE);


        if (mfpCustomFilePickerViewModel.getIsGrid().getValue() != null) {
            IsGrid = mfpCustomFilePickerViewModel.getIsGrid().getValue();
        } else {
            mfpCustomFilePickerViewModel.setIsGrid(false);
        }
        if (mfpCustomFilePickerViewModel.getIsInsideAlbum().getValue() != null) {
            isInSideAlbum = mfpCustomFilePickerViewModel.getIsInsideAlbum().getValue();
        } else {
            mfpCustomFilePickerViewModel.setIsInsideAlbum(false);
        }

        //Init Max File Selection
        if (mfpCustomFilePickerViewModel.getMaxSelection().getValue() != null) {
            mMaxFileSelection = mfpCustomFilePickerViewModel.getMaxSelection().getValue();
        } else {
            mMaxFileSelection = 0;
        }

        if (mfpCustomFilePickerViewModel.getMyFileList().getValue() != null &&
                mfpCustomFilePickerViewModel.getMyFileList().getValue().size() != 0 &&
                mfpCustomFilePickerViewModel.getPickerFileType().getValue() != null) {
            myFileList = mfpCustomFilePickerViewModel.getMyFileList().getValue();
            initAdapter();
            PickerFileType = mfpCustomFilePickerViewModel.getPickerFileType().getValue();
        } else {
            if (requireActivity().getIntent().getStringExtra("FileType") != null) {
                PickerFileType = requireActivity().getIntent().getStringExtra("FileType");
                mfpCustomFilePickerViewModel.setPickerFileType(PickerFileType);
                init();
            } else {
                promptSelection();
            }
            initAdapter();
        }
        if (mfpCustomFilePickerViewModel.getBackgroundColor().getValue() != null) {
            mColor = mfpCustomFilePickerViewModel.getBackgroundColor().getValue();
            if (mColor != 0)
                mDataBinding.getRoot().setBackgroundColor(ContextCompat.getColor(getActivity(), mfpCustomFilePickerViewModel.getBackgroundColor().getValue()));
        }

        if (mfpCustomFilePickerViewModel.getTheme().getValue() != null) {
            mTheme = mfpCustomFilePickerViewModel.getTheme().getValue();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Check whether it returns from Gallery Viewer
        if (mBeenDirectedToDocumentPicker) {
            mBeenDirectedToDocumentPicker = false;
            promptSelection();
            mDataBinding.simpleProgressBar.setVisibility(View.GONE);
        }

        if (mfpCustomFilePickerViewModel.getMyFileList().getValue() != null) {
            myFileList = mfpCustomFilePickerViewModel.getMyFileList().getValue();
        }
        if (mfpCustomFilePickerViewModel.getSelectionList().getValue() != null && mfpCustomFilePickerViewModel.getSelectionList().getValue().size() != 0) {
            selectionList = mfpCustomFilePickerViewModel.getSelectionList().getValue();
            if (mActionBar == null)
                mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();


            mActionBar.setTitle(selectionList.size() + getResources().getString(R.string.ItemSelect));
        }
        initAdapter();
        if (mfpCustomFilePickerViewModel.getIsGrid().getValue() != null) {
            IsGrid = mfpCustomFilePickerViewModel.getIsGrid().getValue();
            if (IsGrid) {
                mDataBinding.CustomRecyclerView.setLayoutManager(mGridLayoutManager);
            } else {
                mDataBinding.CustomRecyclerView.setLayoutManager(mLinearLayoutManager);
            }
        }
        if (mfpCustomFilePickerViewModel.getIsInsideAlbum().getValue() != null) {
            isInSideAlbum = mfpCustomFilePickerViewModel.getIsInsideAlbum().getValue();
        }
        if (mfpCustomFilePickerViewModel.getSortingStyle().getValue() != null) {
            sortingMyFileList(mfpCustomFilePickerViewModel.getSortingStyle().getValue());
        }

  /*     if(mDataBinding.simpleProgressBar.getVisibility()==View.VISIBLE){
           mDataBinding.simpleProgressBar.setVisibility(View.INVISIBLE);
           promptSelection();
       }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        mfpCustomFilePickerViewModel.saveMyFileList(myFileList);
        mfpCustomFilePickerViewModel.saveSelectionList(selectionList);
        mfpCustomFilePickerViewModel.setIsInsideAlbum(isInSideAlbum);
        mfpCustomFilePickerViewModel.setMaxSelection(mMaxFileSelection);
        mfpCustomFilePickerViewModel.setPickerFileType(PickerFileType);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.asm_mfp_menu_custom_file_picker, menu);

        //for search filter
        MenuItem searchItem = menu.findItem(R.id.FilePicker_SearchFilter);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");
        //resume search test when rotation or leave foreground event has occurred

        if (mfpCustomFilePickerViewModel.getSearchingText().getValue() != null && !mfpCustomFilePickerViewModel.getSearchingText().getValue().equals("")) {
            searchView.setIconified(false);
        } else {
            searchView.setIconified(true);
        }

        if (mfpCustomFilePickerViewModel.getSearchingText().getValue() != null) {
            searchView.setQuery(mfpCustomFilePickerViewModel.getSearchingText().getValue(), false);
            mAdapter.getFilter().filter(mfpCustomFilePickerViewModel.getSearchingText().getValue());
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                mfpCustomFilePickerViewModel.setSearchingText(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                mfpCustomFilePickerViewModel.setSearchingText(newText);
                return false;
            }
        });

        //select all item
        MenuItem selectAllItem = menu.findItem(R.id.SelectAll);

        //select file type item
        MenuItem selectFileTypeItem = menu.findItem(R.id.SelectFileType);

        //check item
        MenuItem checkItem = menu.findItem(R.id.DoneSelection);

        //share item with other app
        MenuItem shareItem = menu.findItem(R.id.ShareWith);

        //unselection
        MenuItem unSelectItem = menu.findItem(R.id.UnSelectAll);

        //Sorting
        MenuItem sortingItem = menu.findItem(R.id.sorting);

        //changeViewFormat
        MenuItem changeViewFormatItem = menu.findItem(R.id.Custom_ChangeLayout);
        //back button

        if (isInSideAlbum) {
            if (selectionList.size() != 0) {
                if (selectionList.size() == mMaxFileSelection || checkIsSelectedAll())
                    selectAllItem.setVisible(false);
                else
                    selectAllItem.setVisible(true);
                checkItem.setVisible(true);
                shareItem.setVisible(true);
                unSelectItem.setVisible(true);
                sortingItem.setVisible(true);
                selectFileTypeItem.setVisible(true);
                changeViewFormatItem.setVisible(true);
            } else {
                checkItem.setVisible(false);
                shareItem.setVisible(false);
                unSelectItem.setVisible(false);
                sortingItem.setVisible(true);
                selectFileTypeItem.setVisible(true);
                changeViewFormatItem.setVisible(true);
            }
        } else {
            if(selectionList.size() != 0){
                unSelectItem.setVisible(true);
                checkItem.setVisible(true);
                shareItem.setVisible(true);
            }else {
                unSelectItem.setVisible(false);
                checkItem.setVisible(false);
                shareItem.setVisible(false);
            }
            selectAllItem.setVisible(false);
            selectFileTypeItem.setVisible(true);
            sortingItem.setVisible(true);
            changeViewFormatItem.setVisible(true);

            mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (IsGrid) {
            changeViewFormatItem.setTitle(R.string.ListViewFormat);
        } else {
            changeViewFormatItem.setTitle(R.string.GridViewFormat);
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

    public boolean checkIsSelectedAll(){
        for (int i= 0; i < myFileList.size(); i++){
            if(!myFileList.get(i).getIsSelected())
                IsSelectAll = false;
            else
                IsSelectAll = true;
        }
        return IsSelectAll;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isInSideAlbum) {
                if (PickerFileType.equals("Image")) {
                    myFileList.clear();
                    mfpCustomFilePickerViewModel.clearMyFileList();
                    initAdapter();
                    openImageMediaStoreFolder();
                    isInSideAlbum = false;
                    mfpCustomFilePickerViewModel.setIsInsideAlbum(isInSideAlbum);
                    getActivity().invalidateOptionsMenu();
                    mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                } else {
                    myFileList.clear();
                    mfpCustomFilePickerViewModel.clearMyFileList();
                    initAdapter();
                    openVideoMediaStoreFolder();
                    isInSideAlbum = false;
                    mfpCustomFilePickerViewModel.setIsInsideAlbum(isInSideAlbum);
                    getActivity().invalidateOptionsMenu();
                    mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                }
            } else {
                mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mAppCompatActivity.getSupportActionBar().setTitle(getActivity().getTitle());

            }
            getActivity().invalidateOptionsMenu();
        }
        //to change layout to grid or recycler view
        if (item.getItemId() == R.id.Custom_ChangeLayout) {
            //if current layout is grid then change to recycler else change to grid
            if (IsGrid) {
                mDataBinding.CustomRecyclerView.setLayoutManager(mLinearLayoutManager);
                IsGrid = false;
                mfpCustomFilePickerViewModel.setIsGrid(IsGrid);
                item.setTitle(R.string.GridViewFormat);
            } else {
                mDataBinding.CustomRecyclerView.setLayoutManager(mGridLayoutManager);
                IsGrid = true;
                mfpCustomFilePickerViewModel.setIsGrid(IsGrid);
                item.setTitle(R.string.ListViewFormat);
            }
            initAdapter();
        }
        if (item.getItemId() == R.id.sorting) {
            //Pass Callback and CurrentSortingStyle
            AsmMfpSortByDialog.newInstance(this, mfpCustomFilePickerViewModel.getSortingStyle().getValue()).show(getParentFragmentManager(), AsmMfpSortByDialog.TAG);
        }
        if (item.getItemId() == R.id.SelectAll) {
            //Before selecting all, check whether there is a max file selection.
            for (int i = 0; i < (mMaxFileSelection != 0 ? mMaxFileSelection : myFileList.size()); i++) {
                // To Prevent Index Out Of Bound when the condition is based on mMaxFileSelection.
                // (E.g. maxFileSelection: 9 but myFileList: 4)
                if (i >= myFileList.size()) {
                    break;
                }
                myFileList.get(i).setIsSelected(true);
                selectionList.add(Uri.parse(myFileList.get(i).getFileUri()));
            }

            mfpCustomFilePickerViewModel.saveSelectionList(selectionList);

            if (mActionBar == null)
                mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

            mActionBar.setTitle(selectionList.size() + getResources().getString(R.string.ItemSelect));

            initAdapter();
            getActivity().invalidateOptionsMenu();

        }
        if (item.getItemId() == R.id.SelectFileType){
            promptSelection();
        }
        if (item.getItemId() == R.id.DoneSelection) {
            ArrayList<String> mFileList = new ArrayList<>();
            for (int i = 0; i < selectionList.size(); i++) {
                mFileList.add(selectionList.get(i).toString());
            }

            if (mFileList != null) {
                Intent intent = new Intent(requireContext(), AsmGvrMainActivity.class);
                intent.putStringArrayListExtra(AsmMfpCustomFilePickerFragment.EXTRA_STRING_ARRAY_FILE_URI, mFileList);
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
            mfpCustomFilePickerViewModel.clearSelectionList();
            //update recycler view data and ui

            for (int i = 0; i < myFileList.size(); i++) {
                if (myFileList.get(i).getIsSelected())
                    myFileList.get(i).setIsSelected(false);
            }
            mfpCustomFilePickerViewModel.saveMyFileList(myFileList);

            initAdapter();

            if (mActionBar == null)
                mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

            mActionBar.setTitle(R.string.app_name);

            getActivity().invalidateOptionsMenu();
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
        builder.setTitle(getResources().getString(R.string.SelectMessage1));
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.SelectMessage1));

        builder.setPositiveButton(getResources().getString(R.string.image), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PickerFileType = "Image";
                mfpCustomFilePickerViewModel.setPickerFileType(PickerFileType);
                init();

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.video), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PickerFileType = "Video";
                mfpCustomFilePickerViewModel.setPickerFileType(PickerFileType);
                init();
            }
        });
        builder.setNeutralButton(getResources().getString(R.string.document), new DialogInterface.OnClickListener() {
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
            mDataBinding.textviewFileTypeNotFound.setText(requireContext().getString(R.string.NoGeneralFilesFound, requireContext().getString(R.string.NoImagesFound)));
        } else if (PickerFileType.equals("Video")) {
            openVideoMediaStoreFolder();
            mDataBinding.textviewFileTypeNotFound.setText(requireContext().getString(R.string.NoGeneralFilesFound, requireContext().getString(R.string.NoVideosFound)));
        }

        //Check and Show No Files Found Icon when There is no files in the devices
        if (myFileList.size() != 0) {
            mDataBinding.linearLayoutNoFilesFound.setVisibility(View.GONE);
        } else {
            mDataBinding.linearLayoutNoFilesFound.setVisibility(View.VISIBLE);
        }

        if (PickerFileType.equals("Document")) {
            mDataBinding.linearLayoutNoFilesFound.setVisibility(View.GONE);
            mDataBinding.simpleProgressBar.setVisibility(View.VISIBLE);
            mBeenDirectedToDocumentPicker = true;
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                //Android 10 and above
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                String[] mimeTypes = {
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"),
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc"),
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx"),
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt"),
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx"),
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"),
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtx"),
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtf"),
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("html"),
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls"),
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx")};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_CODE);
            } else {
                //Android 7 to Android 9
                Intent intent = new Intent(requireContext(), AsmMfpDocumentFilePickerActivity.class);
                intent.putExtra(AsmMfpMainFragment.EXTRA_INT_MAX_FILE_SELECTION, mMaxFileSelection);
                intent.putExtra("color", mfpCustomFilePickerViewModel.getBackgroundColor().getValue());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFileListForAndroid10 = new ArrayList<>();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_DOCUMENT_REQUEST_CODE) {
                if (null != data) {
                    if (null != data.getClipData()) {
                        // Multiple document is selected
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            mFileListForAndroid10.add(data.getClipData().getItemAt(i).getUri().toString());
                        }
                    } else {
                        //Single document is selected
                        mFileListForAndroid10.add(data.getData().toString());
                    }


                    Intent intent = new Intent(requireActivity(), AsmGvrMainActivity.class);
                    intent.putStringArrayListExtra(AsmGvrMainFragment.EXTRA_STRING_ARRAY_FILE_URI, mFileListForAndroid10);
                    intent.putExtra("color", mfpCustomFilePickerViewModel.getBackgroundColor().getValue());
                    startActivity(intent);
                }
            }
        }
    }

    // for folder
    private void openImageMediaStoreFolder() {
        //list to get file in same folder
        myFileList.clear();
        mfpCustomFilePickerViewModel.clearMyFileList();
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
                    mfpCustomFilePickerViewModel.addFileToMyFileList(myFile);
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

        //Sorting
        initDefaultSortingStyle();

        if (mAdapter != null)
            initAdapter();
        //after finish for external then continue to internal storage
        if (mActionBar == null && selectionList.size() != 0) {
            mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        }
    }

    private void openVideoMediaStoreFolder() {
        //list to get file in same folder
        myFileList.clear();
        mfpCustomFilePickerViewModel.clearMyFileList();
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
            int folderidColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.

                long id = cursor.getInt(idColumn);
                fileName = cursor.getString(nameColumn);
                Long lastModify = cursor.getLong(lastModifyColumn);
                int folderid = cursor.getInt(folderidColumn);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), lastModify, true);
                myFile.setFileType("Video");

                if (!filelist.contains(fileName)) {
                    filelist.add(fileName);
                    myFileList.add(myFile);
                    mfpCustomFilePickerViewModel.addFileToMyFileList(myFile);
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

        //Sorting
        initDefaultSortingStyle();

        if (mAdapter != null)
            initAdapter();

        if (mActionBar == null && selectionList.size() != 0) {
            mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        }
    }

    private void openImageMediaStoreFile(int folderID) {
        myFileList.clear();
        mfpCustomFilePickerViewModel.clearMyFileList();
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
            mfpCustomFilePickerViewModel.addFileToMyFileList(myFile);

        }
        cursor.close();

        //Sorting
        initDefaultSortingStyle();
    }

    private void openVideoMediaStoreFile(String foldername) {
        myFileList.clear();
        mfpCustomFilePickerViewModel.clearMyFileList();
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
                new String[]{"%/" + foldername + "/%"},
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

            MyFile myFile = new MyFile(fileName, String.valueOf(contentUri), lastModify, false);
            myFile.setFileType("Video");
            for (int i = 0; i < selectionList.size(); i++) {
                if (myFile.getFileUri().equals(selectionList.get(i))) {
                    myFile.setIsSelected(true);
                }
            }
            myFileList.add(myFile);
            mfpCustomFilePickerViewModel.addFileToMyFileList(myFile);
        }

        cursor.close();
        //Sorting
        initDefaultSortingStyle();
    }

    private void initAdapter() {
        mAdapter = new AsmMfpCustomFilePickerRecyclerViewAdapter(requireContext(), myFileList, AsmMfpCustomFilePickerFragment.this, selectionList.size());
        //Set the Maximum File Selection
        mAdapter.setMaxFileSelection(mMaxFileSelection);

        //Set Current View Mode
        if (mfpCustomFilePickerViewModel.getIsGrid().getValue() != null) {
            if (mfpCustomFilePickerViewModel.getIsGrid().getValue()) {
                mAdapter.setCurrentView(1);
            } else {
                mAdapter.setCurrentView(0);
            }
        }
        mDataBinding.CustomRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initDefaultSortingStyle() {
        //Once every file is loaded, the files will be sorted to newest to oldest.
        if (mfpCustomFilePickerViewModel.getSortingStyle().getValue() != null) {
            sortingMyFileList(mfpCustomFilePickerViewModel.getSortingStyle().getValue());
        }
    }

    @Override
    public void onFolderClicked(int folderid) {
        if (PickerFileType.equals("Image"))
            openImageMediaStoreFile(folderid);

        mAppCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_black_24dp);// set drawable icon
        mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isInSideAlbum = true;
        mfpCustomFilePickerViewModel.setIsInsideAlbum(isInSideAlbum);
        getActivity().invalidateOptionsMenu();

        if (selectionList.size() != 0) {
            int x = 0;
            do {
                for (int i = 0; i < selectionList.size(); i++) {
                    if (myFileList.get(x).getFileUri().equals(selectionList.get(i).toString())) {
                        myFileList.get(x).setIsSelected(true);
                    }
                }
                x++;
            } while (x < myFileList.size());
        }
        initAdapter();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onAlbumItemUnSelected(Uri uri) {
        for (int i = (selectionList.size() - 1); i >= 0; i--) {
            if (selectionList.get(i).equals(uri)) {
                selectionList.remove(i);
                break;
            }
        }
        mfpCustomFilePickerViewModel.saveSelectionList(selectionList);

        if (mActionBar != null)
            mActionBar.setTitle(selectionList.size() + getResources().getString(R.string.ItemSelect));

        if (selectionList.size() == 0) {
            mActionBar.setTitle(getResources().getString(R.string.app_name));
        }

        //update the selection count for limit user selection
        mAdapter.setSelectionCount(selectionList.size());
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onVideoFolderClicked(String foldername) {
        openVideoMediaStoreFile(foldername);

        mAppCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_black_24dp);// set drawable icon
        mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isInSideAlbum = true;
        mfpCustomFilePickerViewModel.setIsInsideAlbum(isInSideAlbum);
        getActivity().invalidateOptionsMenu();

        if (selectionList.size() != 0) {
            int x = 0;
            do {
                for (int i = 0; i < selectionList.size(); i++) {
                    if (myFileList.get(x).getFileUri().equals(selectionList.get(i).toString())) {
                        myFileList.get(x).setIsSelected(true);
                    }
                }
                x++;
            } while (x < myFileList.size());
        }
        initAdapter();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onAlbumItemSelected(Uri uri) {
        //get position
        isInSideAlbum = true;
        mfpCustomFilePickerViewModel.setIsInsideAlbum(isInSideAlbum);
        selectionList.add(uri);

        mfpCustomFilePickerViewModel.saveSelectionList(selectionList);

        if (mActionBar == null) {
            mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        }
        mActionBar.setTitle(selectionList.size() + getResources().getString(R.string.ItemSelect));

        //update the selection count for limit user selection
        mAdapter.setSelectionCount(selectionList.size());
        getActivity().invalidateOptionsMenu();
    }

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

    @Override
    public void onSortByDialogPositiveButtonClicked(String sortingStyle) {
        sortingMyFileList(sortingStyle);
        mfpCustomFilePickerViewModel.setSortingStyle(sortingStyle);
        initAdapter();
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
