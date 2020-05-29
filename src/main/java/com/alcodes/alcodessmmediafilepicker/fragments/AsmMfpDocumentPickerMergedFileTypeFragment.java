package com.alcodes.alcodessmmediafilepicker.fragments;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpDocumentPickerRecyclerViewAdapter;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpFragmentDocumentFilePickerBinding;
import com.alcodes.alcodessmmediafilepicker.databinding.bindingcallbacks.SortByDialogCallback;
import com.alcodes.alcodessmmediafilepicker.dialogs.AsmMfpSortByDialog;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpCustomFilePickerViewModel;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpDocumentViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import timber.log.Timber;

import static com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpMainFragment.EXTRA_INT_MAX_FILE_SELECTION;

public class AsmMfpDocumentPickerMergedFileTypeFragment extends Fragment implements AsmMfpDocumentPickerRecyclerViewAdapter.DocumentFilePickerCallbacks, MenuItem.OnActionExpandListener, SortByDialogCallback {
    private View view;
    private RecyclerView recyclerView;
    private NavController mNavController;
    private static final String DEFAULT_SORTING_STYLE = "SortingDateDescending";

    //  private ActionMode mActionMode;
    private ArrayList<MyFile> mFileList = new ArrayList<>();
    private ArrayList<String> TotalselectedList = new ArrayList<>();
    private AsmMfpDocumentPickerRecyclerViewAdapter mAdapter;
    private AsmMfpFragmentDocumentFilePickerBinding mDataBinding;
    private AsmMfpDocumentViewModel mDocumentViewModel;

    //for action mode custom search bar
    private EditText CustomSearchBar;
    private Button ClearTextBtn;
    public static final String EXTRA_STRING_ARRAY_FILE_URI = "EXTRA_STRING_ARRAY_FILE_URI";

    //for limit selection
    private int SelecLimitCount;
    private int mMaxFileSelection;
    private Integer mViewPagerPosition;
    private SearchView searchView;
    private Boolean isSwiped = false;
    private String PickerFileType = "";
    private String FileType;
    private int mColor=0,mTheme=0;
    private ActionBar mActionBar;

    public static final String EXTRA_INTEGER_SELECTED_THEME = "EXTRA_INTEGER_SELECTED_THEME";
    private AsmMfpCustomFilePickerViewModel mfpCustomFilePickerViewModel;


    public AsmMfpDocumentPickerMergedFileTypeFragment() {
    }

    public AsmMfpDocumentPickerMergedFileTypeFragment(String fileType) {
        FileType = fileType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.asm_mfp_document_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.pdf_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDataBinding = AsmMfpFragmentDocumentFilePickerBinding.inflate(inflater, container, false);

        initAdapter();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        mDocumentViewModel = new ViewModelProvider(mNavController.getBackStackEntry(R.id.asm_mfp_nav_document),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).
                get(AsmMfpDocumentViewModel.class);

        //Set Default Sorting in View Model
        mDocumentViewModel.setSortingStyle(DEFAULT_SORTING_STYLE);

        mfpCustomFilePickerViewModel = new ViewModelProvider(mNavController.getBackStackEntry(R.id.asm_mfp_nav_document),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).
                get(AsmMfpCustomFilePickerViewModel.class);
        mfpCustomFilePickerViewModel.getTheme().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=0)
                mTheme=integer;

            }
        });
        mfpCustomFilePickerViewModel.getBackgroundColor().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=0)
                mColor=integer;

            }
        });

        if (FileType != null) {
            mDocumentViewModel.setFileType(FileType);
        } else {
            FileType = mDocumentViewModel.getCurrentFileType(mDocumentViewModel.getViewPagerPosition().getValue());
        }

        ArrayList<String> FileTypeList = new ArrayList<>();
        if (FileType.equals("PDF")) {
            String pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
            FileTypeList.add(pdf);
        } else if (FileType.equals("doc")) {
            String doc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");
            String docx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx");
            FileTypeList.addAll(Arrays.asList(doc, docx));
        } else if (FileType.equals("PTT")) {
            String ptt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt");
            String pttx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx");
            FileTypeList.addAll(Arrays.asList(ptt, pttx));
        } else if (FileType.equals("TXT")) {
            String txt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt");
            String rtx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtx");
            String rtf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtf");
            String html = MimeTypeMap.getSingleton().getMimeTypeFromExtension("html");
            FileTypeList.addAll(Arrays.asList(txt, rtx, rtf, html));
        } else if (FileType.equals("XLS")) {
            String xls = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls");
            String xlsx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx");
            FileTypeList.addAll(Arrays.asList(xls, xlsx));
        }

        mDocumentViewModel.getFileList(FileTypeList, FileType).observe(getViewLifecycleOwner(), new Observer<ArrayList<MyFile>>() {
            @Override
            public void onChanged(ArrayList<MyFile> myFiles) {
                if (myFiles.size() != 0) {
                    if (myFiles.get(0).getFileType() == FileType) {
                        mFileList = myFiles;
                        initAdapter();
                    }
                }
            }
        });

        if (FileType.equals("PDF") && mDocumentViewModel.getMyPDFFileList().getValue() != null) {
            mFileList = mDocumentViewModel.getMyPDFFileList().getValue();
        } else if (FileType.equals("doc") && mDocumentViewModel.getMyFileList().getValue() != null) {
            mFileList = mDocumentViewModel.getMyFileList().getValue();
        } else if (FileType.equals("PTT") && mDocumentViewModel.getMyPttFileList().getValue() != null) {
            mFileList = mDocumentViewModel.getMyPttFileList().getValue();
        } else if (FileType.equals("TXT") && mDocumentViewModel.getMytxtFileList().getValue() != null) {
            mFileList = mDocumentViewModel.getMytxtFileList().getValue();
        } else if (FileType.equals("XLS") && mDocumentViewModel.getMyxlsFileList().getValue() != null) {
            mFileList = mDocumentViewModel.getMyxlsFileList().getValue();
        } else {
            mFileList = mDocumentViewModel.getFileList(FileTypeList, FileType).getValue();
            if (FileType.equals("PDF")) {
                initDefaultSortingStyle();
                mDocumentViewModel.saveMyPDFFileList(mFileList);
            } else if (FileType.equals("doc")) {
                initDefaultSortingStyle();
                mDocumentViewModel.saveMyFileList(mFileList);
            } else if (FileType.equals("PTT")) {
                initDefaultSortingStyle();
                mDocumentViewModel.saveMyPttFileList(mFileList);
            } else if (FileType.equals("TXT")) {
                initDefaultSortingStyle();
                mDocumentViewModel.saveMytxtFileList(mFileList);
            } else if (FileType.equals("XLS")) {
                initDefaultSortingStyle();
                mDocumentViewModel.saveMyxlsFileList(mFileList);
            }
        }

        //get selection list from viewmodel
        mDocumentViewModel.getSelectionList().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                if (strings.size() > 0) {
                    TotalselectedList = strings;
                    mAdapter.setSelectedCounter(TotalselectedList.size());
                    mAdapter.notifyDataSetChanged();
                }

                //when unselect all this able to clear all  selected item
                if (strings.size() == 0) {
                    recyclerView.setAdapter(null);

                    for (int i = 0; i < mFileList.size(); i++) {
                        if (mFileList.get(i).getIsSelected()) {
                            mFileList.get(i).setIsSelected(false);
                        }
                    }
                    initAdapter();
                }
            }
        });
        mMaxFileSelection = requireActivity().getIntent().getExtras().getInt(EXTRA_INT_MAX_FILE_SELECTION, 0);
        mDocumentViewModel.setSelectionLimit(mMaxFileSelection);
        //get user selection limit ,by default is 10item

        mDocumentViewModel.getSelectionLimit().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mMaxFileSelection = integer;
                mAdapter.setSelectLimitCounter(mMaxFileSelection);
                mAdapter.notifyDataSetChanged();
                getActivity().invalidateOptionsMenu();
            }
        });


        if (mDocumentViewModel.getSelectionList().getValue() != null &&
                mDocumentViewModel.getSelectionList().getValue().size() != 0) {
            TotalselectedList = mDocumentViewModel.getSelectionList().getValue();

            if (TotalselectedList.size() > 0)
                mActionBar.setTitle(TotalselectedList.size() + "item(s) selected");

        }

        //to active action mode when switch to another tab
        if (mDocumentViewModel.getViewPagerPosition().getValue() != null) {
            mViewPagerPosition = mDocumentViewModel.getViewPagerPosition().getValue();
        }

        mDocumentViewModel.getViewPagerPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                if (!mViewPagerPosition.equals(position)) {
                    mViewPagerPosition = position;
                    FileType = mDocumentViewModel.getCurrentFileType(position);
                    if (FileType.equals("PDF") && mDocumentViewModel.getMyPDFFileList().getValue() != null) {
                        mFileList = mDocumentViewModel.getMyPDFFileList().getValue();
                    } else if (FileType.equals("doc") && mDocumentViewModel.getMyFileList().getValue() != null) {
                        mFileList = mDocumentViewModel.getMyFileList().getValue();
                    } else if (FileType.equals("PTT") && mDocumentViewModel.getMyPttFileList().getValue() != null) {
                        mFileList = mDocumentViewModel.getMyPttFileList().getValue();
                    } else if (FileType.equals("TXT") && mDocumentViewModel.getMytxtFileList().getValue() != null) {
                        mFileList = mDocumentViewModel.getMytxtFileList().getValue();
                    } else if (FileType.equals("XLS") && mDocumentViewModel.getMyxlsFileList().getValue() != null) {
                        mFileList = mDocumentViewModel.getMyxlsFileList().getValue();
                    }
                    initAdapter();

                    if (mDocumentViewModel.getSearchingText().getValue() != null) {
                        if (searchView != null) {
                            searchView.setQuery(mDocumentViewModel.getSearchingText().getValue(), false);
                            mAdapter.getFilter().filter(mDocumentViewModel.getSearchingText().getValue());
                        }
                    }

                }
            }
        });

        //to active action mode when switch to another tab
        mDocumentViewModel.getIsSwiped().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isSwiped = aBoolean;
            }
        });
        if (getActivity().getIntent().getExtras() != null) {
            if (getActivity().getIntent().getExtras().getInt(EXTRA_INTEGER_SELECTED_THEME) != 0){
                mTheme = getActivity().getIntent().getExtras().getInt(EXTRA_INTEGER_SELECTED_THEME);
            mfpCustomFilePickerViewModel.setTheme(mTheme);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init navigation component.
        mNavController = Navigation.findNavController(view);
    }

    private void initDefaultSortingStyle() {
        //Once every file is loaded, the files will be sorted to newest to oldest.
        if (mDocumentViewModel.getSortingStyle().getValue() != null) {
            sortingMyFileList(mDocumentViewModel.getSortingStyle().getValue());
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //for search filter
        MenuItem searchItem = menu.findItem(R.id.Doc_FilePicker_SearchFilter);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search..");

        if (mDocumentViewModel.getSearchingText().getValue() != null && !mDocumentViewModel.getSearchingText().getValue().equals("")) {
            searchView.setIconified(false);
        } else {
            searchView.setIconified(true);
        }
        if (mDocumentViewModel.getSearchingText().getValue() != null) {
            searchView.setQuery(mDocumentViewModel.getSearchingText().getValue(), false);
            mAdapter.getFilter().filter(mDocumentViewModel.getSearchingText().getValue());
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                mDocumentViewModel.setSearchingText(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                mDocumentViewModel.setSearchingText(newText);
                return false;
            }
        });

        //Sorting
        MenuItem sortingItem = menu.findItem(R.id.sorting);

        MenuItem SelectAll = menu.findItem(R.id.Doc_FilePicker_SelectAll);
        //Check whether all file is selected in this tab, then hide or show select all menu item
        if(isAllFileSelectedInThisTab()){
            SelectAll.setVisible(false);
        }else{
            if(mMaxFileSelection != 0){
                if(TotalselectedList.size() == mMaxFileSelection){
                    SelectAll.setVisible(false);
                }else{
                    SelectAll.setVisible(true);
                }
            }else{
                SelectAll.setVisible(true);
            }
        }

        MenuItem checkItem = menu.findItem(R.id.Doc_FilePicker_DoneSelection);
        MenuItem unSelectItem = menu.findItem(R.id.Doc_FilePicker_UnselectAll);

        if (TotalselectedList.size() > 0) {
            checkItem.setVisible(true);
            unSelectItem.setVisible(true);
            sortingItem.setVisible(true);
        } else {
            checkItem.setVisible(false);
            unSelectItem.setVisible(false);
            sortingItem.setVisible(true);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean isAllFileSelectedInThisTab(){
        //A temporary mFileList
        ArrayList<MyFile> tempFileList = new ArrayList<>();

        if(mViewPagerPosition == 0){
            //PDF TAB
            tempFileList = mDocumentViewModel.getMyPDFFileList().getValue();
        }else if (mViewPagerPosition == 1){
            //DOC TAB
            tempFileList = mDocumentViewModel.getMyFileList().getValue();
        }else if (mViewPagerPosition == 2){
            //PTT TAB
            tempFileList = mDocumentViewModel.getMyPttFileList().getValue();
        }else if (mViewPagerPosition == 3){
            //TXT TAB
            tempFileList = mDocumentViewModel.getMytxtFileList().getValue();
        }else if (mViewPagerPosition == 4){
            //XLS TAB
            tempFileList = mDocumentViewModel.getMyxlsFileList().getValue();
        }

        for(int i=0 ; i < tempFileList.size() ; i ++){
            if(! tempFileList.get(i).getIsSelected()){
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Doc_FilePicker_SelectAll) {
            //*************** COMMENT THIS PART USE WINSON"S SELECT ALL AT BELOW, COMMENT THIS PART JUST IN CASE SELECT ALL FOR ALL TABS OF DIFFERENT FILE TYPE NEED TO USED AGAIN ******************************
//            ArrayList<MyFile> tempPageFileList = mFileList;
//            String currentFileType = "";
//            Boolean skipPDFFlag = false, skipDocFlag = false, skipPttFlag = false, skipTxtFlag = false, skipXlsFlag = false;
//            for(int x=0; x<5; x++){
//                if (x > 0) {
//                    if(!skipPDFFlag && !FileType.equals("PDF")){
//                        if(mDocumentViewModel.getMyPDFFileList().getValue() != null){
//                            mFileList = mDocumentViewModel.getMyPDFFileList().getValue();
//                            skipPDFFlag = true;
//                            currentFileType = "PDF";
//                        }
//                    }else if(!skipDocFlag && !FileType.equals("doc")){
//                        if(mDocumentViewModel.getMyFileList().getValue() != null){
//                            mFileList = mDocumentViewModel.getMyFileList().getValue();
//                            skipDocFlag = true;
//                            currentFileType = "doc";
//                        }
//                    } else if(!skipPttFlag && !FileType.equals("PTT")){
//                        if(mDocumentViewModel.getMyPttFileList().getValue() != null){
//                            mFileList = mDocumentViewModel.getMyPttFileList().getValue();
//                            skipPttFlag = true;
//                            currentFileType = "PTT";
//                        }
//                    } else if(!skipTxtFlag && !FileType.equals("TXT")){
//                        if(mDocumentViewModel.getMytxtFileList().getValue() != null){
//                            mFileList = mDocumentViewModel.getMytxtFileList().getValue();
//                            skipTxtFlag = true;
//                            currentFileType = "TXT";
//                        }
//                    } else if(!skipXlsFlag && !FileType.equals("XLS")){
//                        if(mDocumentViewModel.getMyxlsFileList().getValue() != null){
//                            mFileList = mDocumentViewModel.getMyxlsFileList().getValue();
//                            skipXlsFlag = true;
//                            currentFileType = "XLS";
//                        }
//                    }
//                }
//
//                for (int i = 0; i < mFileList.size(); i++) {
//                    if(!mFileList.get(i).getIsSelected()){
//                        mFileList.get(i).setIsSelected(true);
//                        TotalselectedList.add(mFileList.get(i).getFileUri());
//                    }
//                }
//
//                if ((FileType.equals("PDF") && x == 0) || currentFileType.equals("PDF")) {
//                    mDocumentViewModel.saveMyPDFFileList(mFileList);
//                } else if ((FileType.equals("doc") && x == 0) || currentFileType.equals("doc")) {
//                    mDocumentViewModel.saveMyFileList(mFileList);
//                } else if ((FileType.equals("PTT") && x == 0) || currentFileType.equals("PTT")) {
//                    mDocumentViewModel.saveMyPttFileList(mFileList);
//                } else if ((FileType.equals("TXT") && x == 0) || currentFileType.equals("TXT")) {
//                    mDocumentViewModel.saveMytxtFileList(mFileList);
//                } else if ((FileType.equals("XLS") && x == 0) || currentFileType.equals("XLS")) {
//                    mDocumentViewModel.saveMyxlsFileList(mFileList);
//                }
//            mFileList = tempPageFileList;
            //*************** COMMENT THIS PART USE WINSON"S SELECT ALL AT BELOW, COMMENT THIS PART JUST IN CASE SELECT ALL FOR ALL TABS OF DIFFERENT FILE TYPE NEED TO USED AGAIN ******************************

            for (int i = 0; i < (mMaxFileSelection != 0 ? mMaxFileSelection : mFileList.size()); i++) {
                if(mMaxFileSelection != 0){
                    if(TotalselectedList.size() == mMaxFileSelection){
                        break;
                    }
                }

                if(i >= mFileList.size()){
                    break;
                }

                if(!mFileList.get(i).getIsSelected()){
                    mFileList.get(i).setIsSelected(true);
                    TotalselectedList.add(mFileList.get(i).getFileUri());
                }
            }

            mActionBar.setTitle(TotalselectedList.size() + getResources().getString(R.string.ItemSelect));

            mDocumentViewModel.setSelectionList(TotalselectedList);

            initAdapter();

            requireActivity().invalidateOptionsMenu();
        }

        if (item.getItemId() == R.id.Doc_FilePicker_UnselectAll) {
            resetFileList();
            initAdapter();

            mActionBar.setTitle(getResources().getString(R.string.asm_mfp_app_name));
            requireActivity().invalidateOptionsMenu();
        }
        if (item.getItemId() == R.id.Doc_FilePicker_ShareWith) {
            ArrayList<String> FileList = new ArrayList<>();
            for (int i = 0; i < mDocumentViewModel.getSelectionList().getValue().size(); i++) {
                FileList.add(mDocumentViewModel.getSelectionList().getValue().get(i));
            }

            if (mDocumentViewModel.getSelectionList().getValue() != null) {
                StartShare(FileList);
            }
        }
        if (item.getItemId() == R.id.Doc_FilePicker_DoneSelection) {
            ArrayList<String> mFileList = new ArrayList<>();
            for (int i = 0; i < TotalselectedList.size(); i++) {
                mFileList.add(TotalselectedList.get(i));
            }
            if (mFileList != null) {
              //  Intent intent = new Intent(getContext(), AsmGvrMainActivity.class);
            //    intent.putStringArrayListExtra(EXTRA_STRING_ARRAY_FILE_URI, mFileList);
              //  startActivity(intent);

                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setComponent(new ComponentName("com.alcodes.alcodesgalleryviewerdemo","com.alcodes.alcodesgalleryviewerdemo.activities.MainActivity"));
                intent.putExtra("color", mColor);
                intent.putExtra(EXTRA_STRING_ARRAY_FILE_URI,mFileList);

                intent.putExtra(EXTRA_INTEGER_SELECTED_THEME, mTheme);
                startActivity(intent);
            }
        }

        if (item.getItemId() == R.id.sorting) {
            //Pass Callback and CurrentSortingStyle
            AsmMfpSortByDialog.newInstance(this, mDocumentViewModel.getSortingStyle().getValue()).show(getParentFragmentManager(), AsmMfpSortByDialog.TAG);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDocumentSelected(Uri uri) {

        //update with viewmodel
        TotalselectedList.add(uri.toString());
        mAdapter.setSelectedCounter(TotalselectedList.size());
        mActionBar.setTitle(TotalselectedList.size() + getResources().getString(R.string.ItemSelect));
        requireActivity().invalidateOptionsMenu();

        mDocumentViewModel.setSelectionList(TotalselectedList);
    }

    @Override
    public void onDocumentUnSelected(Uri uri) {
        //update with viewmodel
        for (int i = 0; i < TotalselectedList.size(); i++) {
            if (TotalselectedList.get(i).equals(uri.toString()))
                TotalselectedList.remove(i);
        }
        mDocumentViewModel.setSelectionList(TotalselectedList);

        mAdapter.setSelectedCounter(TotalselectedList.size());

        if (TotalselectedList.size() == 0)
            mActionBar.setTitle(getResources().getString(R.string.asm_mfp_app_name));
        else
            mActionBar.setTitle(TotalselectedList.size() + getResources().getString(R.string.ItemSelect));
        requireActivity().invalidateOptionsMenu();
    }

    @Override
    public int onNullSelectionLimit() {
        return mMaxFileSelection;
    }

    //make all selected item to unselect
    public void resetFileList() {
        for (int i = 0; i < mFileList.size(); i++) {
            if (mFileList.get(i).getIsSelected())
                mFileList.get(i).setIsSelected(false);
        }
        if(FileType.equals("PDF")){
            mDocumentViewModel.saveMyPDFFileList(mFileList);
        }else if(FileType.equals("doc")){
            mDocumentViewModel.saveMyFileList(mFileList);
        }else if(FileType.equals("PTT")){
            mDocumentViewModel.saveMyPttFileList(mFileList);
        }else if(FileType.equals("TXT")){
            mDocumentViewModel.saveMytxtFileList(mFileList);
        }else if(FileType.equals("XLS")){
            mDocumentViewModel.saveMyxlsFileList(mFileList);
        }

        ArrayList<MyFile> mOtherFileList;

        //pdf
        if (mDocumentViewModel.getMyPDFFileList().getValue() != null && !FileType.equals("PDF")){
            mOtherFileList = mDocumentViewModel.getMyPDFFileList().getValue();
            for (int i = 0; i < mOtherFileList.size(); i++) {
                if (mOtherFileList.get(i).getIsSelected())
                    mOtherFileList.get(i).setIsSelected(false);
            }
            mDocumentViewModel.saveMyPDFFileList(mOtherFileList);
        }
        //docx
        if (mDocumentViewModel.getMyFileList().getValue() != null && !FileType.equals("doc")) {
            mOtherFileList = mDocumentViewModel.getMyFileList().getValue();

            for (int i = 0; i < mOtherFileList.size(); i++) {
                if (mOtherFileList.get(i).getIsSelected())
                    mOtherFileList.get(i).setIsSelected(false);
            }
            mDocumentViewModel.saveMyFileList(mOtherFileList);
        }
        //  ptt
        if (mDocumentViewModel.getMyPttFileList().getValue() != null && !FileType.equals("PTT")) {
            mOtherFileList = mDocumentViewModel.getMyPttFileList().getValue();

            for (int i = 0; i < mOtherFileList.size(); i++) {
                if (mOtherFileList.get(i).getIsSelected())
                    mOtherFileList.get(i).setIsSelected(false);
            }
            mDocumentViewModel.saveMyPttFileList(mOtherFileList);
        }
        //  txt
        if (mDocumentViewModel.getMytxtFileList().getValue() != null && !FileType.equals("TXT")) {
            mOtherFileList = mDocumentViewModel.getMytxtFileList().getValue();

            for (int i = 0; i < mOtherFileList.size(); i++) {
                if (mOtherFileList.get(i).getIsSelected())
                    mOtherFileList.get(i).setIsSelected(false);
            }
            mDocumentViewModel.saveMytxtFileList(mOtherFileList);
        }
        //  Xls
        if (mDocumentViewModel.getMyxlsFileList().getValue() != null && !FileType.equals("XLS")) {
            mOtherFileList = mDocumentViewModel.getMyxlsFileList().getValue();

            for (int i = 0; i < mOtherFileList.size(); i++) {
                if (mOtherFileList.get(i).getIsSelected())
                    mOtherFileList.get(i).setIsSelected(false);
            }
            mDocumentViewModel.saveMyxlsFileList(mOtherFileList);
        }
        TotalselectedList.clear();
        //update viewmodel as adapter will update along
        mDocumentViewModel.setSelectionList(TotalselectedList);
    }

    private void initAdapter() {
        mAdapter = new AsmMfpDocumentPickerRecyclerViewAdapter(getContext(), mFileList, AsmMfpDocumentPickerMergedFileTypeFragment.this);
        recyclerView.setAdapter(mAdapter);
        if (TotalselectedList.size() != 0)
            mAdapter.setSelectedCounter(TotalselectedList.size());
        mAdapter.setSelectLimitCounter(mMaxFileSelection);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FileType.equals("PDF")) {
            mFileList = mDocumentViewModel.getMyPDFFileList().getValue();
        } else if (FileType.equals("doc")) {
            mFileList = mDocumentViewModel.getMyFileList().getValue();
        } else if (FileType.equals("PTT")) {
            mFileList = mDocumentViewModel.getMyPttFileList().getValue();
        } else if (FileType.equals("TXT")) {
            mFileList = mDocumentViewModel.getMytxtFileList().getValue();
        } else if (FileType.equals("XLS")) {
            mFileList = mDocumentViewModel.getMyxlsFileList().getValue();
        }
        if (mDocumentViewModel.getSelectionList().getValue() != null && mDocumentViewModel.getSelectionList().getValue().size() != 0) {
            TotalselectedList = mDocumentViewModel.getSelectionList().getValue();
            if (TotalselectedList.size() > 0)
                mActionBar.setTitle(TotalselectedList.size() + getResources().getString(R.string.ItemSelect));
        }
        if (mfpCustomFilePickerViewModel.getSortingStyle().getValue() != null) {
            sortingMyFileList(mfpCustomFilePickerViewModel.getSortingStyle().getValue());
        }
        initAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (FileType.equals("PDF")) {
            mDocumentViewModel.saveMyPDFFileList(mFileList);
        } else if (FileType.equals("doc")) {
            mDocumentViewModel.saveMyFileList(mFileList);
        } else if (FileType.equals("PTT")) {
            mDocumentViewModel.saveMyPttFileList(mFileList);
        } else if (FileType.equals("TXT")) {
            mDocumentViewModel.saveMytxtFileList(mFileList);
        } else if (FileType.equals("XLS")) {
            mDocumentViewModel.saveMyxlsFileList(mFileList);
        }
    }

    public void StartShare(ArrayList<String> mFileList) {
        String Type = "";
        Type = "*/*";
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

private void sortingMyFileList(String sortingStyle) {
        if (sortingStyle.equals("SortingNameAscending")) {
            if(mDocumentViewModel.getMyFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyFileList().getValue(), new SortByName());
            if(mDocumentViewModel.getMyPDFFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyPDFFileList().getValue(), new SortByName());
            if(mDocumentViewModel.getMytxtFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMytxtFileList().getValue(), new SortByName());
            if(mDocumentViewModel.getMyPttFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyPttFileList().getValue(), new SortByName());
            if(mDocumentViewModel.getMyxlsFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyxlsFileList().getValue(), new SortByName());
        } else if (sortingStyle.equals("SortingNameDescending")) {
            if(mDocumentViewModel.getMyFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyFileList().getValue(), Collections.reverseOrder(new SortByName()));
            if(mDocumentViewModel.getMyPDFFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyPDFFileList().getValue(), Collections.reverseOrder(new SortByName()));
            if(mDocumentViewModel.getMytxtFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMytxtFileList().getValue(), Collections.reverseOrder(new SortByName()));
            if(mDocumentViewModel.getMyPttFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyPttFileList().getValue(), Collections.reverseOrder(new SortByName()));
            if(mDocumentViewModel.getMyxlsFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyxlsFileList().getValue(), Collections.reverseOrder(new SortByName()));
        } else if (sortingStyle.equals("SortingDateAscending")) {
            if(mDocumentViewModel.getMyFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyFileList().getValue(), new SortByDate());
            if(mDocumentViewModel.getMyPDFFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyPDFFileList().getValue(), new SortByDate());
            if(mDocumentViewModel.getMytxtFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMytxtFileList().getValue(), new SortByDate());
            if(mDocumentViewModel.getMyPttFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyPttFileList().getValue(), new SortByDate());
            if(mDocumentViewModel.getMyxlsFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyxlsFileList().getValue(), new SortByDate());
        } else if (sortingStyle.equals("SortingDateDescending")) {
            if(mDocumentViewModel.getMyFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyFileList().getValue(), Collections.reverseOrder(new SortByDate()));
            if(mDocumentViewModel.getMyPDFFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyPDFFileList().getValue(), Collections.reverseOrder(new SortByDate()));
            if(mDocumentViewModel.getMytxtFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMytxtFileList().getValue(), Collections.reverseOrder(new SortByDate()));
            if(mDocumentViewModel.getMyPttFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyPttFileList().getValue(), Collections.reverseOrder(new SortByDate()));
            if(mDocumentViewModel.getMyxlsFileList().getValue()!=null)
                Collections.sort(mDocumentViewModel.getMyxlsFileList().getValue(), Collections.reverseOrder(new SortByDate()));
        }
    }
    @Override
    public void onSortByDialogPositiveButtonClicked(String sortingStyle) {
        sortingMyFileList(sortingStyle);
        mDocumentViewModel.setSortingStyle(sortingStyle);
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
