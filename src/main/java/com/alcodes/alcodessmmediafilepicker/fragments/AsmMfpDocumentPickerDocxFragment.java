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
import android.widget.Toast;

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
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpFragmentDocumentFilePickerBinding;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpDocumentViewModel;

import java.util.ArrayList;
import java.util.Arrays;

public class AsmMfpDocumentPickerDocxFragment extends Fragment implements AsmMfpDocumentPickerRecyclerViewAdapter.DocumentFilePickerCallbacks, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    View view;
    private RecyclerView recyclerView;
    private NavController mNavController;

    private android.view.ActionMode mActionMode;
    private ArrayList<MyFile> mFileList = new ArrayList<>();
    //  private ArrayList<String> selectedList = new ArrayList<>();
    private ArrayList<String> TotalselectedList = new ArrayList<>();
    private AsmMfpDocumentPickerRecyclerViewAdapter mAdapter;
    private AsmMfpFragmentDocumentFilePickerBinding mDataBinding;
    private AsmMfpDocumentViewModel mDocumentViewModel;


    //for action mode custom search bar
    private EditText CustomSearchBar;
    private Button ClearTextBtn;
    private Boolean isSearching = false;

    //for limit selection
    private int SelecLimitCount;

    public AsmMfpDocumentPickerDocxFragment() {

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


        //for custom search bar
        CustomSearchBar = getActivity().findViewById(R.id.Doc_File_Picker_EditText);
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
        ClearTextBtn = getActivity().findViewById(R.id.Doc_File_Picker_ClearTextBtn);
        ClearTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomSearchBar.setText(null);

                initAdapter();

            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

        String doc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");
        String docx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx");
        ArrayList<String> FileType = new ArrayList<>();
        FileType.addAll(Arrays.asList(doc, docx));


        mDocumentViewModel.getFileList(FileType, "doc").observe(getViewLifecycleOwner(), new Observer<ArrayList<MyFile>>() {
            @Override
            public void onChanged(ArrayList<MyFile> myFiles) {
                if(myFiles.size() != 0){
                    if (myFiles.get(0).getFileType() == "doc") {
                        mFileList = myFiles;
                        initAdapter();
                    }
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init navigation component.
        mNavController = Navigation.findNavController(view);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        //for search filter
        MenuItem searchItem = menu.findItem(R.id.Doc_FilePicker_SearchFilter);


        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search..");

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDocumentSelected(Uri uri) {
        //    selectedList.add(String.valueOf(uri));

        //update with viewmodel
        TotalselectedList.add(uri.toString());

        mAdapter.setSelectedCounter(TotalselectedList.size());
        if (mActionMode == null)
            mActionMode = getActivity().startActionMode(mActionModeCallback);
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

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.asm_mfp_menu_document_file_picker, menu);
            //for select item
            MenuItem checkItem = menu.findItem(R.id.Doc_FilePicker_DoneSelection);
            checkItem.setVisible(true);
            MenuItem unSelectItem = menu.findItem(R.id.Doc_FilePicker_UnselectAll);
            unSelectItem.setVisible(true);
            unLimitItem = menu.findItem(R.id.Doc_FilePicker_UnLimitedSelection);

            if (SelecLimitCount != 99)
                unLimitItem.setVisible(true);


            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {


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

                //clear m file
                resetFileList();

                //reinit adapter /refresh
                initAdapter();
                TotalselectedList.clear();
                //update viewmodel as adapter will update along
                mDocumentViewModel.setSelectionList(TotalselectedList);
                mDocumentViewModel.setSelectionLimit(SelecLimitCount);
                Toast.makeText(getContext(), mAdapter.getSelectLimitCounter() + " test", Toast.LENGTH_SHORT).show();
                //close actionmode
                mActionMode.finish();

            }
            if (item.getItemId() == R.id.Doc_FilePicker_SearchFilter) {


                if (!isSearching) {
                    CustomSearchBar.setVisibility(View.VISIBLE);
                    ClearTextBtn.setVisibility(View.VISIBLE);
                    isSearching = true;
                }
                //click search btn for second time to hide the custom search bar
                else {
                    CustomSearchBar.setVisibility(View.INVISIBLE);
                    ClearTextBtn.setVisibility(View.INVISIBLE);
                    isSearching = false;
                }


            }
            if (item.getItemId() == R.id.Doc_FilePicker_UnLimitedSelection) {
                mDocumentViewModel.setSelectionLimit(99);
                unLimitItem.setVisible(false);
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            CustomSearchBar.setVisibility(View.INVISIBLE);
            ClearTextBtn.setVisibility(View.INVISIBLE);
            isSearching = false;
            //selectedList.clear();
            //for refresh

            resetFileList();
            initAdapter();
            mActionMode = null;

        }
    };

    //make all selected item to unselect
    public void resetFileList() {
        for (int i = 0; i < mFileList.size(); i++) {
            if (mFileList.get(i).getIsSelected())
                mFileList.get(i).setIsSelected(false);
        }

    }


    private void initAdapter() {
        mAdapter = new AsmMfpDocumentPickerRecyclerViewAdapter(getContext(), mFileList, AsmMfpDocumentPickerDocxFragment.this);
        recyclerView.setAdapter(mAdapter);
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        mAdapter.getFilter().filter(newText);
        return false;
    }


}
