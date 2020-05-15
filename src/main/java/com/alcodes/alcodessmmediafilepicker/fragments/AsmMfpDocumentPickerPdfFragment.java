package com.alcodes.alcodessmmediafilepicker.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmgalleryviewer.activities.AsmGvrMainActivity;
import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpGithubSampleFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpDocumentPickerRecyclerViewAdapter;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpCustomFilePickerViewModel;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpDocumentViewModel;

import java.util.ArrayList;

public class AsmMfpDocumentPickerPdfFragment extends Fragment implements AsmMfpDocumentPickerRecyclerViewAdapter.DocumentFilePickerCallbacks, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    View view;
    private RecyclerView recyclerView;
    private ArrayList<MyFile> mFileList = new ArrayList<>();//for store all file details
    //  private ArrayList<String> selectedList = new ArrayList<>();
    private ArrayList<String> TotalselectedList = new ArrayList<>();
    //only store selected file uri but can be change
    private android.view.ActionMode mActionMode;
    private AsmMfpDocumentPickerRecyclerViewAdapter mAdapter;
    private SearchView.OnQueryTextListener queryTextListener;
    SearchView searchView;
    //for action mode custom search bar
    private EditText CustomSearchBar;
    private Button ClearTextBtn;
    private AsmMfpDocumentViewModel mDocumentViewModel;
    private NavController mNavController;
    private Boolean isSearching = false;
    //for limit selection
    private int SelecLimitCount;
    private Boolean isSelectedAll = false;

    private AsmMfpCustomFilePickerViewModel mfpMainSharedViewModel;

    public AsmMfpDocumentPickerPdfFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.asm_mfp_document_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.pdf_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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


        CustomSearchBar = requireActivity().findViewById(R.id.Doc_File_Picker_EditText);
        CustomSearchBar.addTextChangedListener(new TextWatcher() {
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
        ClearTextBtn = requireActivity().findViewById(R.id.Doc_File_Picker_ClearTextBtn);
        ClearTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomSearchBar.setText(null);


            }
        });

        mDocumentViewModel = new ViewModelProvider(mNavController.getBackStackEntry(R.id.asm_mfp_documentfragment),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).
                get(AsmMfpDocumentViewModel.class);

        //get selection list from viewmodel
        mDocumentViewModel.getSelectionList().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                if (strings.size() > 0) {
                    TotalselectedList = strings;
                    mAdapter.setSelectedCounter(TotalselectedList.size());
                    mAdapter.notifyDataSetChanged();

                }
            }
        });

        //get user selection limit ,by default is 10item

        mDocumentViewModel.getSelectionLimit().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                SelecLimitCount = integer;
                mAdapter.setSelectLimitCounter(SelecLimitCount);
                mAdapter.notifyDataSetChanged();


            }
        });
        String pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        ArrayList<String> FilType = new ArrayList<>();
        FilType.add(pdf);


        if (mDocumentViewModel.getIsSearching().getValue() != null) {
            isSearching = mDocumentViewModel.getIsSearching().getValue();
        } else {
            mDocumentViewModel.setIsSearching(false);
        }

        if (mDocumentViewModel.getMyFileList().getValue() != null &&
                mDocumentViewModel.getMyFileList().getValue().size() != 0) {

            mDocumentViewModel.getFileList(FilType, "PDF").observe(getViewLifecycleOwner(), new Observer<ArrayList<MyFile>>() {
                @Override
                public void onChanged(ArrayList<MyFile> myFiles) {
                    if (myFiles.size() != 0) {
                        if (myFiles.get(0).getFileType() == "PDF") {

                            mFileList = myFiles;
                            initAdapter();
                        }
                    }
                }
            });


        } else {
            pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
            FilType = new ArrayList<>();
            FilType.add(pdf);
            mDocumentViewModel.getFileList(FilType, "PDF").observe(getViewLifecycleOwner(), new Observer<ArrayList<MyFile>>() {
                @Override
                public void onChanged(ArrayList<MyFile> myFiles) {
                    if (myFiles.size() != 0) {
                        if (myFiles.get(0).getFileType() == "PDF") {

                            mFileList = myFiles;
                            initAdapter();
                        }
                    }
                }
            });

        }

        ///////////////////////////////////////////////////


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init navigation component.
        mNavController = Navigation.findNavController(view);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {


        searchView = (SearchView) menu.findItem(R.id.Doc_FilePicker_SearchFilter).getActionView();

        if (!isSearching) {
            if (mDocumentViewModel.getSearchingText().getValue() != null && !mDocumentViewModel.getSearchingText().getValue().equals("")) {

                searchView.setQuery(mDocumentViewModel.getSearchingText().getValue() + "", true);
            }
        }
        queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                mDocumentViewModel.setSearchingText(s);
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);


        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Doc_FilePicker_SelectAll) {
            isSelectedAll = true;
            mDocumentViewModel.setSelectionLimit(99);
            for (int i = 0; i < mFileList.size(); i++) {
                //to prevent adding already selected item
                if (!mFileList.get(i).getIsSelected()) {
                    mFileList.get(i).setIsSelected(true);
                    TotalselectedList.add(mFileList.get(i).getFileUri());
                }
            }
            if (mActionMode == null)
                mActionMode = getActivity().startActionMode(mActionModeCallback);
            mActionMode.setTitle(TotalselectedList.size() + "item(s) selected");
            mDocumentViewModel.setSelectionList(TotalselectedList);
            initAdapter();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onDocumentSelected(Uri uri) {

        //update with viewmodel
        TotalselectedList.add(uri.toString());

        mAdapter.setSelectedCounter(TotalselectedList.size());
        if (mActionMode == null)
            mActionMode = getActivity().startActionMode(mActionModeCallback);
        //select all remaining
        if (!isSelectedAll)
            mActionMode.invalidate();
        mActionMode.setTitle(TotalselectedList.size() + "item(s) selected");
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
        //to reactive selectall
        isSelectedAll = false;
        //  if(mActionMode!=null)
        //  mActionMode.invalidate();

        if (TotalselectedList.size() == 0 && mActionMode != null)
            mActionMode.finish();
        mAdapter.setSelectedCounter(TotalselectedList.size());


        if (mActionMode != null)
            mActionMode.setTitle(TotalselectedList.size() + "item(s) selected");

    }

    @Override
    public int onNullSelectionLimit() {
        return SelecLimitCount;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        MenuItem unLimitItem;
        MenuItem selectall;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {


            mode.getMenuInflater().inflate(R.menu.asm_mfp_menu_document_file_picker, menu);

            //for select item
            MenuItem checkItem = menu.findItem(R.id.Doc_FilePicker_DoneSelection);
            checkItem.setVisible(true);
            MenuItem unSelectItem = menu.findItem(R.id.Doc_FilePicker_UnselectAll);
            unSelectItem.setVisible(true);
            unLimitItem = menu.findItem(R.id.Doc_FilePicker_UnLimitedSelection);
            unLimitItem.setVisible(true);
            unLimitItem = menu.findItem(R.id.Doc_FilePicker_UnLimitedSelection);

            if (SelecLimitCount != 99)
                unLimitItem.setVisible(true);


            selectall = menu.findItem(R.id.Doc_FilePicker_SelectAll);
            if (!isSelectedAll)
                selectall.setVisible(true);
            else
                selectall.setVisible(false);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (!isSelectedAll)
                selectall.setVisible(true);
            else
                selectall.setVisible(false);


            return false;
        }


        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.Doc_FilePicker_DoneSelection) {
                ArrayList<String> mFileList = new ArrayList<>();
                for (int i = 0; i < TotalselectedList.size(); i++) {
                    mFileList.add(TotalselectedList.get(i));
                }
                if (mFileList != null) {
                    Intent intent = new Intent(getContext(), AsmGvrMainActivity.class);
                    intent.putStringArrayListExtra(AsmMfpGithubSampleFilePickerActivity.EXTRA_STRING_ARRAY_FILE_URI, mFileList);

                    startActivity(intent);
                }
            }
            if (item.getItemId() == R.id.Doc_FilePicker_UnselectAll) {


                //for refresh + clear all list
                resetFileList();
                //close actionmode
                mActionMode.finish();
            }
            if (item.getItemId() == R.id.Doc_FilePicker_SearchFilter) {


                if (!isSearching) {
                    CustomSearchBar.setVisibility(View.VISIBLE);
                    ClearTextBtn.setVisibility(View.VISIBLE);
                    isSearching = true;
                    mDocumentViewModel.setIsSearching(isSearching);
                }
                //click search btn for second time to hide the custom search bar
                else {
                    CustomSearchBar.setVisibility(View.INVISIBLE);
                    ClearTextBtn.setVisibility(View.INVISIBLE);
                    isSearching = false;
                    mDocumentViewModel.setIsSearching(isSearching);
                }


            }
            if (item.getItemId() == R.id.Doc_FilePicker_UnLimitedSelection) {
                mDocumentViewModel.setSelectionLimit(99);
                unLimitItem.setVisible(false);
            }
            if (item.getItemId() == R.id.Doc_FilePicker_SelectAll) {
                unLimitItem.setVisible(false);
                isSelectedAll = true;
                mDocumentViewModel.setSelectionLimit(99);
                for (int i = 0; i < mFileList.size(); i++) {
                    //to prevent adding already selected item
                    if (!mFileList.get(i).getIsSelected()) {
                        mFileList.get(i).setIsSelected(true);
                        TotalselectedList.add(mFileList.get(i).getFileUri());
                    }
                }
                if (mActionMode == null)
                    mActionMode = getActivity().startActionMode(mActionModeCallback);
                mActionMode.setTitle(TotalselectedList.size() + "item(s) selected");
                mDocumentViewModel.setSelectionList(TotalselectedList);
                initAdapter();
            }


            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            //when user unselect all item / clicked back button action mode

            //hide custom search bar
            CustomSearchBar.setVisibility(View.INVISIBLE);
            ClearTextBtn.setVisibility(View.INVISIBLE);
            mDocumentViewModel.setIsSearching(false);

            //for refresh + clear all list
            // resetFileList();
            initAdapter();
            mActionMode = null;

        }
    };

    private void resetFileList() {
        for (int i = 0; i < mFileList.size(); i++) {
            if (mFileList.get(i).getIsSelected())
                mFileList.get(i).setIsSelected(false);
        }
        TotalselectedList.clear();
        //update viewmodel as adapter will update along
        mDocumentViewModel.setSelectionList(TotalselectedList);
        mDocumentViewModel.setSelectionLimit(SelecLimitCount);
    }

    private void initAdapter() {
        mAdapter = new AsmMfpDocumentPickerRecyclerViewAdapter(getContext(), mFileList, AsmMfpDocumentPickerPdfFragment.this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDocumentViewModel.getMyFileList().getValue() != null) {
            mFileList = mDocumentViewModel.getMyFileList().getValue();
        }
        if (mDocumentViewModel.getSelectionList().getValue() != null && mDocumentViewModel.getSelectionList().getValue().size() != 0) {
            TotalselectedList = mDocumentViewModel.getSelectionList().getValue();
        }
        if (mDocumentViewModel.getIsSearching().getValue() != null) {
            isSearching = mDocumentViewModel.getIsSearching().getValue();
            if (isSearching) {
                CustomSearchBar.setVisibility(View.VISIBLE);
                ClearTextBtn.setVisibility(View.VISIBLE);
            }
            //click search btn for second time to hide the custom search bar
            else {

                CustomSearchBar.setVisibility(View.INVISIBLE);
                ClearTextBtn.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mDocumentViewModel.saveMyFileList(mFileList);
        mDocumentViewModel.setSelectionList(TotalselectedList);
        mDocumentViewModel.setIsSearching(isSearching);
    }
}
