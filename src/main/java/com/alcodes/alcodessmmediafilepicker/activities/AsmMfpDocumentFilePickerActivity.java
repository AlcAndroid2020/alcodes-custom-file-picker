package com.alcodes.alcodessmmediafilepicker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.alcodes.alcodessmgalleryviewer.activities.AsmGvrMainActivity;
import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpDocumentPickerViewPagerAdapter;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpDocumentPickerDocxFragment;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpDocumentPickerPdfFragment;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpDocumentPickerPttFragment;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpDocumentPickerTxtFragment;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpDocumentPickerXlsFragment;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class AsmMfpDocumentFilePickerActivity extends AppCompatActivity {
    //The main activity for document picker to assign viewpager,fragments and tablayout
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Boolean isSelected;
    private ArrayList<MyFile> myfilelist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asm_mfp_activity_document_file_picker);
        //init tablayout and viewpager
        tabLayout = (TabLayout) findViewById(R.id.Doc_FilePicker_TabLayout);
        viewPager = (ViewPager) findViewById(R.id.Doc_FilePicker_ViewPager);
        isSelected = false;
        //adapter to add fragment
        AsmMfpDocumentPickerViewPagerAdapter mAdapter = new AsmMfpDocumentPickerViewPagerAdapter(getSupportFragmentManager());

        mAdapter.AddFragment(new AsmMfpDocumentPickerPdfFragment(), "PDF");
        mAdapter.AddFragment(new AsmMfpDocumentPickerDocxFragment(), "DOCX");
        mAdapter.AddFragment(new AsmMfpDocumentPickerPttFragment(), "PPT");
        mAdapter.AddFragment(new AsmMfpDocumentPickerTxtFragment(), "TXT");
        mAdapter.AddFragment(new AsmMfpDocumentPickerXlsFragment(), "XLS");


        //adapter setup
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    public void getFileListFromAdapter(ArrayList<MyFile> filelist) {
        //check if anything selected
        myfilelist = filelist;
        int count = 0;
        for (int i = 0; i < myfilelist.size(); i++) {
            if (myfilelist.get(i).getIsSelected())
                count++;
        }
        //to ensure if user select/unselect any item on recycler view
        if (count > 0)
            isSelected = true;

        else
            isSelected = false;
        //hide/show the done option menu
        invalidateOptionsMenu();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.asm_mfp_menu_document_file_picker, menu);
        MenuItem checkItem = menu.findItem(R.id.Doc_FilePicker_DoneSelection);
        if (isSelected) {
            checkItem.setVisible(true);
        } else {
            checkItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.Doc_FilePicker_DoneSelection) {
            ArrayList<String> selectedFileList = new ArrayList<>();
            if (myfilelist != null) {
                for (int i = 0; i < myfilelist.size(); i++) {
                    if (myfilelist.get(i).getIsSelected())
                        selectedFileList.add(myfilelist.get(i).getFileUri());
                }

                Intent intent = new Intent(this, AsmGvrMainActivity.class);
                intent.putStringArrayListExtra(AsmMfpGithubSampleFilePickerActivity.EXTRA_STRING_ARRAY_FILE_URI, selectedFileList);

                startActivity(intent);
            }

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(this, AsmMfpMainActivity.class);
        startActivity(intent);
    }
}
