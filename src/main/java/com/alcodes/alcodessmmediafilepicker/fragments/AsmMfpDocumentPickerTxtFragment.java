package com.alcodes.alcodessmmediafilepicker.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmgalleryviewer.activities.AsmGvrMainActivity;
import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpGithubSampleFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpDocumentPickerRecyclerViewAdapter;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.io.File;
import java.util.ArrayList;

public class AsmMfpDocumentPickerTxtFragment extends Fragment implements AsmMfpDocumentPickerRecyclerViewAdapter.DocumentFilePickerCallbacks, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    View view;
    private RecyclerView recyclerView;
    private ArrayList<MyFile> mFileList = new ArrayList<>();
    private ArrayList<String> selectedList = new ArrayList<>();//only store selected file uri but can be change
    private android.view.ActionMode mActionMode;
    private AsmMfpDocumentPickerRecyclerViewAdapter mAdapter;

    public AsmMfpDocumentPickerTxtFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.asm_mfp_document_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.pdf_RecyclerView);
        mAdapter = new AsmMfpDocumentPickerRecyclerViewAdapter(getContext(), mFileList, AsmMfpDocumentPickerTxtFragment.this, selectedList.size());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openDocumentMediaStore();
        setHasOptionsMenu(true);

    }

    private void openDocumentMediaStore() {
        //document format


        String txt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt");
        String rtx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtx");
        String rtf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtf");
        String html = MimeTypeMap.getSingleton().getMimeTypeFromExtension("html");

        //Table
        Uri table = MediaStore.Files.getContentUri("external");
        //Column
        String[] column = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE};
        //Where
        String where = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"

                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        //args
        String[] args = new String[]{txt, rtf, rtx, html};

        Cursor fileCursor = getActivity().getContentResolver().query(table, column, where, args, null);

        while (fileCursor.moveToNext()) {

            //your code
            int dataColumn = fileCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            int sizeColumn = fileCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);

            String filePath = fileCursor.getString(dataColumn);
            String size = fileCursor.getString(sizeColumn);
            Uri uri = Uri.fromFile(new File(filePath));
            //grant permision for app with package "packegeName", eg. before starting other app via intent

            getActivity().grantUriPermission(getActivity().getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //revoke permisions
            Uri newuri = FileProvider.getUriForFile(getContext(), "com.alcodes.alcodesgalleryviewerdemo.fileprovider", new File(filePath));
            DocumentFile df = DocumentFile.fromSingleUri(getActivity().getApplicationContext(), newuri);
            //revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            MyFile myFile = new MyFile(df.getName(), String.valueOf(newuri), false);
            myFile.setFileType("TXT");
            myFile.setFileSize(size);
            mFileList.add(myFile);
        }
        fileCursor.close();


    }


    @Override
    public void onDocumentSelected(Uri uri) {
        selectedList.add(String.valueOf(uri));
        if (mActionMode == null)
            mActionMode = getActivity().startActionMode(mActionModeCallback);

        mActionMode.setTitle(selectedList.size() + "item(s) selected");
        //update for selection limit which allow user only select 5 item
        mAdapter.setSelectedCounter(selectedList.size());

    }

    @Override
    public void onDocumentUnSelected(Uri uri) {
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.get(i).equals(uri.toString()))
                selectedList.remove(i);
        }
        if (mActionMode != null)
            mActionMode.setTitle(selectedList.size() + "item(s) selected");

        if (selectedList.size() == 0 && mActionMode != null)
            mActionMode.finish();
        //update for selection limit which allow user only select 5 item
        mAdapter.setSelectedCounter(selectedList.size());
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.asm_mfp_menu_document_file_picker, menu);
            //for select item
            MenuItem checkItem = menu.findItem(R.id.Doc_FilePicker_DoneSelection);
            checkItem.setVisible(true);
            MenuItem unSelectItem = menu.findItem(R.id.Doc_FilePicker_UnselectAll);
            unSelectItem.setVisible(true);
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
                for (int i = 0; i < selectedList.size(); i++) {
                    mFileList.add(selectedList.get(i));
                }
                if (mFileList != null) {
                    Intent intent = new Intent(getContext(), AsmGvrMainActivity.class);
                    intent.putStringArrayListExtra(AsmMfpGithubSampleFilePickerActivity.EXTRA_STRING_ARRAY_FILE_URI, mFileList);

                    startActivity(intent);
                }
            }
            if (item.getItemId() == R.id.Doc_FilePicker_UnselectAll) {
                selectedList.clear();
                //update recycler view data and ui

                for (int i = 0; i < mFileList.size(); i++) {
                    if (mFileList.get(i).getIsSelected())
                        mFileList.get(i).setIsSelected(false);
                }

                mAdapter = new AsmMfpDocumentPickerRecyclerViewAdapter(getContext(), mFileList, AsmMfpDocumentPickerTxtFragment.this, selectedList.size());
                recyclerView.setAdapter(mAdapter);

                //close actionmode
                mActionMode.finish();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            mActionMode = null;
        }
    };
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.Doc_FilePicker_SearchFilter);


        androidx.appcompat.widget.SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search..");
        super.onCreateOptionsMenu(menu, inflater);
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
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        mAdapter.getFilter().filter(newText);

        return false;
    }
}
