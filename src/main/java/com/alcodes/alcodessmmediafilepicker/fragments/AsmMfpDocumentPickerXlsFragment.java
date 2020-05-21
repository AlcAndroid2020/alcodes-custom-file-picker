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

import com.alcodes.alcodessmgalleryviewer.activities.AsmGvrMainActivity;
import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpGithubSampleFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpDocumentPickerRecyclerViewAdapter;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpFragmentDocumentFilePickerBinding;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpDocumentViewModel;

import java.util.ArrayList;
import java.util.Arrays;

import static com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpMainFragment.EXTRA_INT_MAX_FILE_SELECTION;

public class AsmMfpDocumentPickerXlsFragment extends Fragment implements AsmMfpDocumentPickerRecyclerViewAdapter.DocumentFilePickerCallbacks, MenuItem.OnActionExpandListener {
    View view;
    private RecyclerView recyclerView;
    private NavController mNavController;

    //   private android.view.ActionMode mActionMode;
    private ArrayList<MyFile> mFileList = new ArrayList<>();
    private ArrayList<String> TotalselectedList = new ArrayList<>();
    private AsmMfpDocumentPickerRecyclerViewAdapter mAdapter;
    private AsmMfpFragmentDocumentFilePickerBinding mDataBinding;
    private AsmMfpDocumentViewModel mDocumentViewModel;


    //for action mode custom search bar
    private EditText CustomSearchBar;
    private Button ClearTextBtn;
    private Boolean isSearching;

    //for limit selection
    private int SelecLimitCount;
    //private Boolean isSelectedAll = false;
    private Boolean isLimited = false;
    private int mMaxFileSelection;
    int oldRotation;
    private Integer mViewPagerPosition;
    private SearchView searchView;
    private Boolean isSwiped = false;
    private static String LIST_STATE = "list_state";
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
    private ActionBar mActionBar;

    public AsmMfpDocumentPickerXlsFragment() {

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
        //for custom search bar


        mDocumentViewModel = new ViewModelProvider(mNavController.getBackStackEntry(R.id.asm_mfp_nav_document),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).
                get(AsmMfpDocumentViewModel.class);

        String xls = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls");
        String xlsx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx");
        ArrayList<String> FileType = new ArrayList<>();
        FileType.addAll(Arrays.asList(xls, xlsx));
        mDocumentViewModel.getFileList(FileType, "XLS").observe(getViewLifecycleOwner(), new Observer<ArrayList<MyFile>>() {
            @Override
            public void onChanged(ArrayList<MyFile> myFiles) {
                if (myFiles.size() != 0) {
                    if (myFiles.get(0).getFileType() == "XLS") {
                        mFileList = myFiles;
                        initAdapter();
                    }
                }
            }
        });

        if (mDocumentViewModel.getMyxlsFileList().getValue() != null) {
            mFileList = mDocumentViewModel.getMyxlsFileList().getValue();
        } else {
            mFileList = mDocumentViewModel.getFileList(FileType, "XLS").getValue();
        }


        //get selection list from viewmodel
        mDocumentViewModel.getSelectionList().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                if (strings.size() > 0) {
                    TotalselectedList = strings;
                    mAdapter.setSelectedCounter(TotalselectedList.size());
                    mAdapter.notifyDataSetChanged();
                    //to active action mode as pervious tab already selected item
                    // if (mActionMode == null)
                    //   mActionMode = getActivity().startActionMode(mActionModeCallback);

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

        if (mDocumentViewModel.getIsSearching().getValue() != null) {
            isSearching = mDocumentViewModel.getIsSearching().getValue();
        } else {
            isSearching = false;
        }

        if (mDocumentViewModel.getSelectionList().getValue() != null &&
                mDocumentViewModel.getSelectionList().getValue().size() != 0) {
            TotalselectedList = mDocumentViewModel.getSelectionList().getValue();
            // if (mActionMode == null)
            //     mActionMode = getActivity().startActionMode(mActionModeCallback);
            //  mActionMode.setTitle(TotalselectedList.size() + getResources().getString(R.string.ItemSelect));

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
                    if (mDocumentViewModel.getSearchingText().getValue() != null) {
                        if (searchView != null) {
                            searchView.setQuery(mDocumentViewModel.getSearchingText().getValue(), false);
                            mAdapter.getFilter().filter(mDocumentViewModel.getSearchingText().getValue());
                        }
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
                    if (mDocumentViewModel.getMyxlsFileList().getValue() != null &&
                            mDocumentViewModel.getMyxlsFileList().getValue().size() != 0) {
                        mFileList = mDocumentViewModel.getMyxlsFileList().getValue();
                        initAdapter();
                    }
                }
            }
        });
//        }

        mDocumentViewModel.getIsSearching().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    isSearching = aBoolean;
                    mAdapter.setIsSearching(isSearching);
                    mAdapter.notifyDataSetChanged();
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
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search..");

        if (!isSearching && mDocumentViewModel.getSearchingText().getValue() != null && !mDocumentViewModel.getSearchingText().getValue().equals("")) {
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

        MenuItem SelectAll = menu.findItem(R.id.Doc_FilePicker_SelectAll);
        if (isLimited)
            SelectAll.setVisible(false);


        MenuItem checkItem = menu.findItem(R.id.Doc_FilePicker_DoneSelection);
        MenuItem unSelectItem = menu.findItem(R.id.Doc_FilePicker_UnselectAll);

        if (TotalselectedList.size() > 0) {
            checkItem.setVisible(true);
            unSelectItem.setVisible(true);
        } else {

            checkItem.setVisible(false);
            unSelectItem.setVisible(false);
        }


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
            //   if (mActionMode == null)
            //      mActionMode = getActivity().startActionMode(mActionModeCallback);
            //   mActionMode.setTitle(TotalselectedList.size() + getResources().getString(R.string.ItemSelect));
            mActionBar.setTitle(TotalselectedList.size() + "item(s) selected");

            mDocumentViewModel.setSelectionList(TotalselectedList);
            initAdapter();
        }
        if (item.getItemId() == R.id.Doc_FilePicker_SetSelectLimit) {
            PromptLimitDialog();
        }
        if (item.getItemId() == R.id.Doc_FilePicker_UnselectAll) {
            resetFileList();
            initAdapter();
            mActionBar.setTitle(getResources().getString(R.string.app_name));
        }
        if (item.getItemId() == R.id.ShareWith) {
            ArrayList<String> FileList = new ArrayList<>();
            for (int i = 0; i < mDocumentViewModel.getSelectionList().getValue().size(); i++) {
                FileList.add(mDocumentViewModel.getSelectionList().getValue().get(i));
            }

            if (mDocumentViewModel.getSelectionList().getValue() != null) {
                StartShare(FileList);
            }

        }
        if (item.getItemId() == R.id.Doc_FilePicker_SetSelectLimit) {
            PromptLimitDialog();
        }
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

        return super.onOptionsItemSelected(item);
    }

    private void PromptLimitDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
        //  if (mActionMode == null)
        //    mActionMode = getActivity().startActionMode(mActionModeCallback);
        //select all remaining
        //   if (!isSelectedAll)
        //  mActionMode.invalidate();
        //    mActionMode.setTitle(TotalselectedList.size() + "item(s) selected");
        mActionBar.setTitle(TotalselectedList.size() + "item(s) selected");
        getActivity().invalidateOptionsMenu();

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
        //      if (mActionMode != null)
        //       mActionMode.invalidate();

        //     if (TotalselectedList.size() == 0 && mActionMode != null)
        //        mActionMode.finish();
        mAdapter.setSelectedCounter(TotalselectedList.size());

        //    if (mActionMode != null)
        //  mActionMode.setTitle(TotalselectedList.size() + getResources().getString(R.string.ItemSelect));
        if (TotalselectedList.size() == 0)
            mActionBar.setTitle(getResources().getString(R.string.app_name));
        else
            mActionBar.setTitle(TotalselectedList.size() + "item(s) selected");
        getActivity().invalidateOptionsMenu();


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
                //       mActionMode.finish();
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
            if (item.getItemId() == R.id.Doc_FilePicker_SetSelectLimit) {
                PromptLimitDialog();
            }
            if (item.getItemId() == R.id.Doc_FilePicker_SelectAll) {
                mDocumentViewModel.setSelectionLimit(0);
                for (int i = 0; i < mFileList.size(); i++) {
                    //to prevent adding already selected item
                    if (!mFileList.get(i).getIsSelected()) {
                        mFileList.get(i).setIsSelected(true);
                        TotalselectedList.add(mFileList.get(i).getFileUri());
                    }
                }
                //      if (mActionMode == null)
                //         mActionMode = getActivity().startActionMode(mActionModeCallback);
                //         mActionMode.setTitle(TotalselectedList.size() + getResources().getString(R.string.ItemSelect));
                mDocumentViewModel.setSelectionList(TotalselectedList);
                initAdapter();
            }

            if (item.getItemId() == R.id.ShareWith) {
                ArrayList<String> FileList = new ArrayList<>();
                for (int i = 0; i < mDocumentViewModel.getSelectionList().getValue().size(); i++) {
                    FileList.add(mDocumentViewModel.getSelectionList().getValue().get(i));
                }

                if (mDocumentViewModel.getSelectionList().getValue() != null) {
                    StartShare(FileList);
                }
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            //old code
       /*
            CustomSearchBar.setVisibility(View.INVISIBLE);
            ClearTextBtn.setVisibility(View.INVISIBLE);
            mDocumentViewModel.setIsSearching(false);

            //for refresh + clear all list
            // resetFileList();
            initAdapter();
            mActionMode = null;*/
            //if swipe
            if (isSwiped) {
                initAdapter();
                //       mActionMode = null;
                mDocumentViewModel.setIsSwiped(false);
            } else {
                //if clicked done button
                //      mActionMode = null;
                initAdapter();

            }

/* able to maintain data at first rotate, able to clear before rotate by using done button
            Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int CurrentRotation = display.getRotation();

            //if swipe
            if (isSwiped) {
                initAdapter();
                mActionMode = null;
                mDocumentViewModel.setIsSwiped(false);
            } else {
                //if no swipe

                if (CurrentRotation != oldRotation) {
                    //screen rotate


                 //mDocumentViewModel.setOriginalPosition(CurrentRotation);
                 Toast.makeText(getContext(),"",Toast.LENGTH_SHORT).show();
                }
                //if no rotate =click on done button
                else {
                    mActionMode = null;
                    resetFileList();
                    initAdapter();

                }

            }*/
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
        mDocumentViewModel.setSelectionLimit(SelecLimitCount);
    }


    private void initAdapter() {
        mAdapter = new AsmMfpDocumentPickerRecyclerViewAdapter(getContext(), mFileList, AsmMfpDocumentPickerXlsFragment.this);
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
        if (mDocumentViewModel.getMyxlsFileList().getValue() != null) {
            mFileList = mDocumentViewModel.getMyxlsFileList().getValue();
        }
        if (mDocumentViewModel.getSelectionList().getValue() != null && mDocumentViewModel.getSelectionList().getValue().size() != 0) {
            TotalselectedList = mDocumentViewModel.getSelectionList().getValue();
            //   if (mActionMode == null)
            //       mActionMode = getActivity().startActionMode(mActionModeCallback);
            //     mActionMode.setTitle(TotalselectedList.size() + getResources().getString(R.string.ItemSelect));

            if (TotalselectedList.size() > 0)
                mActionBar.setTitle(TotalselectedList.size() + getResources().getString(R.string.ItemSelect));
        }
        initAdapter();
        if (mDocumentViewModel.getIsSearching().getValue() != null) {
            isSearching = mDocumentViewModel.getIsSearching().getValue();
            if (isSearching) {
                if (CustomSearchBar.getVisibility() != View.VISIBLE) {
                    CustomSearchBar.setVisibility(View.VISIBLE);
                    ClearTextBtn.setVisibility(View.VISIBLE);
                }
                if (mDocumentViewModel.getSearchingText().getValue() != null) {
                    CustomSearchBar.setText(mDocumentViewModel.getSearchingText().getValue());
                } else {
                    CustomSearchBar.setText("");
                }
            }
            //click search btn for second time to hide the custom search bar
            else {
                if (CustomSearchBar.getVisibility() == View.VISIBLE) {
                    CustomSearchBar.setVisibility(View.INVISIBLE);
                    ClearTextBtn.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDocumentViewModel.setSelectionList(TotalselectedList);
        mDocumentViewModel.setIsSearching(isSearching);
        mDocumentViewModel.saveMyxlsFileList(mFileList);
    }

    public void StartShare(ArrayList<String> mFileList) {
        String Type = "";

        Type = "application/pdf";


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
}
