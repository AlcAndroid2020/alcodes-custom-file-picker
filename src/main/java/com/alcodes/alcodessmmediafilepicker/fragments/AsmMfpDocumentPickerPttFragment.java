package com.alcodes.alcodessmmediafilepicker.fragments;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
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

import static com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpCustomFilePickerFragment.EXTRA_INT_MAX_FILE_SELECTION;

public class AsmMfpDocumentPickerPttFragment extends Fragment implements AsmMfpDocumentPickerRecyclerViewAdapter.DocumentFilePickerCallbacks, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    View view;
    private RecyclerView recyclerView;
    private NavController mNavController;

    private android.view.ActionMode mActionMode;
    private ArrayList<MyFile> mFileList = new ArrayList<>();
    private ArrayList<String> TotalselectedList = new ArrayList<>();
    private AsmMfpDocumentPickerRecyclerViewAdapter mAdapter;
    private AsmMfpFragmentDocumentFilePickerBinding mDataBinding;
    private AsmMfpDocumentViewModel mDocumentViewModel;


    //for action mode custom search bar
    private EditText CustomSearchBar;
    private Button ClearTextBtn;
    private Boolean isSearching = false;

    //for limit selection
    //private Boolean isSelectedAll = false;
    private Boolean isLimited = false;
    private int mMaxFileSelection;

    public AsmMfpDocumentPickerPttFragment() {

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
                    //to active action mode as pervious tab already selected item
                    //if (mActionMode == null)
                    //    mActionMode = getActivity().startActionMode(mActionModeCallback);
                    Toast.makeText(getContext(), "active action mode ptt", Toast.LENGTH_SHORT).show();

                }
                //when unselect all this able to clear all  selected item
                if(strings.size()==0){
                    recyclerView.setAdapter(null);

                    for (int i = 0; i < mFileList.size(); i++) {
                        if (mFileList.get(i).getIsSelected())
                            mFileList.get(i).setIsSelected(false);
                    }
                    initAdapter();
                }
            }
        });
        mMaxFileSelection = requireActivity().getIntent().getExtras().getInt(EXTRA_INT_MAX_FILE_SELECTION, 0);
        if (mMaxFileSelection != 0)
            isLimited = true;
        //get user selection limit ,by default is 10item

        mDocumentViewModel.getSelectionLimit().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                mMaxFileSelection = integer;
                mAdapter.setSelectLimitCounter(mMaxFileSelection);
                mAdapter.notifyDataSetChanged();
                if (mMaxFileSelection == 0)
                    isLimited = false;
                else
                    isLimited = true;
                getActivity().invalidateOptionsMenu();
            }
        });


        String ptt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt");
        String pttx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx");

        ArrayList<String> FileType = new ArrayList<>();
        FileType.addAll(Arrays.asList(ptt, pttx));

        mDocumentViewModel.getFileList(FileType, "PTT").observe(getViewLifecycleOwner(), new Observer<ArrayList<MyFile>>() {
            @Override
            public void onChanged(ArrayList<MyFile> myFiles) {
                if (myFiles.size() != 0) {
                    if (myFiles.get(0).getFileType() == "PTT") {
                        mFileList = myFiles;
                        initAdapter();
                    }
                }
            }
        });
        mDocumentViewModel.getIsSearching().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    isSearching = aBoolean;
                }
            }
        });

        //to active action mode when switch to another tab

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init navigation component.
        mNavController = Navigation.findNavController(view);
    }

    Boolean isUnSelect = false;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        //for search filter
        MenuItem searchItem = menu.findItem(R.id.Doc_FilePicker_SearchFilter);


        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search..");

        MenuItem SelectAll = menu.findItem(R.id.Doc_FilePicker_SelectAll);
        if (isLimited)
            SelectAll.setVisible(false);


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Doc_FilePicker_SelectAll) {

            mDocumentViewModel.setSelectionLimit(0);
            for (int i = 0; i < mFileList.size(); i++) {
                mFileList.get(i).setIsSelected(true);
                TotalselectedList.add(mFileList.get(i).getFileUri());
            }
            if (mActionMode == null)
                mActionMode = getActivity().startActionMode(mActionModeCallback);
            mActionMode.setTitle(TotalselectedList.size() + "item(s) selected");
            mDocumentViewModel.setSelectionList(TotalselectedList);
            initAdapter();
        }
        if (item.getItemId() == R.id.Doc_FilePicker_SetSelectLimit) {
            PromptLimitDialog();


        }


        return super.onOptionsItemSelected(item);

    }

    private void PromptLimitDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Limit File Selection ");
        final EditText input = new EditText(getContext());
        input.setHint("Enter selection number here");

        builder.setView(input);
        builder.setMessage("0= No Limit");

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDocumentViewModel.setSelectionLimit(Integer.valueOf(input.getText().toString()));
                mMaxFileSelection = Integer.valueOf(input.getText().toString());
                mAdapter.setSelectLimitCounter(mMaxFileSelection);


                //if limited again then hide select all option
                if (mMaxFileSelection != 0)
                    isLimited = true;
                else
                    isLimited = false;
                getActivity().invalidateOptionsMenu();

                mDocumentViewModel.setSelectionLimit(mMaxFileSelection);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builder.show();
    }

    @Override
    public void onDocumentSelected(Uri uri) {
        //    selectedList.add(String.valueOf(uri));

        //update with viewmodel
        TotalselectedList.add(uri.toString());

        mAdapter.setSelectedCounter(TotalselectedList.size());
        if (mActionMode == null)
            mActionMode = getActivity().startActionMode(mActionModeCallback);
        //select all remaining
        //   if (!isSelectedAll)
        //  mActionMode.invalidate();
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
        // isL= false;
        if (mActionMode != null)
            mActionMode.invalidate();

        if (TotalselectedList.size() == 0 && mActionMode != null)
            mActionMode.finish();
        mAdapter.setSelectedCounter(TotalselectedList.size());


        if (mActionMode != null)
            mActionMode.setTitle(TotalselectedList.size() + "item(s) selected");

    }

    @Override
    public int onNullSelectionLimit() {
        return mMaxFileSelection;
    }

    Boolean isUnselected = false;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // MenuItem unLimitItem;
        MenuItem selectall;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.asm_mfp_menu_document_file_picker, menu);
            //for select item
            MenuItem checkItem = menu.findItem(R.id.Doc_FilePicker_DoneSelection);


            checkItem.setVisible(true);
            MenuItem unSelectItem = menu.findItem(R.id.Doc_FilePicker_UnselectAll);
            unSelectItem.setVisible(true);
            //unLimitItem = menu.findItem(R.id.Doc_FilePicker_UnLimitedSelection);


            selectall = menu.findItem(R.id.Doc_FilePicker_SelectAll);
            if (!isLimited)
                selectall.setVisible(true);

            else

                selectall.setVisible(false);


            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            if (!isLimited)
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

                //   AsmMfpDocumentPickerPdfFragment pdf = new AsmMfpDocumentPickerPdfFragment();
                //  pdf.refreshRecyclerView();


            }
            if (item.getItemId() == R.id.Doc_FilePicker_SearchFilter) {


                if (!isSearching) {
                    CustomSearchBar.setVisibility(View.VISIBLE);
                    ClearTextBtn.setVisibility(View.VISIBLE);
                    mDocumentViewModel.setIsSearching(true);
                }
                //click search btn for second time to hide the custom search bar
                else {
                    CustomSearchBar.setVisibility(View.INVISIBLE);
                    ClearTextBtn.setVisibility(View.INVISIBLE);
                    mDocumentViewModel.setIsSearching(false);
                }


            }
            if (item.getItemId() == R.id.Doc_FilePicker_SetSelectLimit) {
                PromptLimitDialog();
            }
            if (item.getItemId() == R.id.Doc_FilePicker_SelectAll) {
                //  unLimitItem.setVisible(false);
                //isSelectedAll = true;
                mDocumentViewModel.setSelectionLimit(0);
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

    //make all selected item to unselect
    public void resetFileList() {
        for (int i = 0; i < mFileList.size(); i++) {
            if (mFileList.get(i).getIsSelected())
                mFileList.get(i).setIsSelected(false);
        }
        TotalselectedList.clear();
        //update viewmodel as adapter will update along
        mDocumentViewModel.setSelectionList(TotalselectedList);
        mDocumentViewModel.setSelectionLimit(mMaxFileSelection);


    }


    private void initAdapter() {
        mAdapter = new AsmMfpDocumentPickerRecyclerViewAdapter(getContext(), mFileList, AsmMfpDocumentPickerPttFragment.this);
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        mAdapter.getFilter().filter(newText);
        return false;
    }


}
