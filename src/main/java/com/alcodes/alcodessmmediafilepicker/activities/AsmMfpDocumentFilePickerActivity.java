package com.alcodes.alcodessmmediafilepicker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpDocumentPickerViewPagerAdapter;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpDocumentPickerDocxFragment;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpDocumentPickerPdfFragment;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpDocumentPickerPttFragment;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpDocumentPickerTxtFragment;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpDocumentPickerXlsFragment;
import com.google.android.material.tabs.TabLayout;

public class AsmMfpDocumentFilePickerActivity extends AppCompatActivity {
    //The main activity for document picker to assign viewpager,fragments and tablayout
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asm_mfp_activity_document_file_picker);
        //init tablayout and viewpager
        tabLayout=(TabLayout) findViewById(R.id.Doc_FilePicker_TabLayout);
        viewPager=(ViewPager) findViewById(R.id.Doc_FilePicker_ViewPager);

        //adapter to add fragment
       AsmMfpDocumentPickerViewPagerAdapter mAdapter= new AsmMfpDocumentPickerViewPagerAdapter(getSupportFragmentManager());

       mAdapter.AddFragment(new AsmMfpDocumentPickerPdfFragment(),"PDF");
       mAdapter.AddFragment(new AsmMfpDocumentPickerDocxFragment(),"DOCX");
        mAdapter.AddFragment(new AsmMfpDocumentPickerPttFragment(),"PPT");
        mAdapter.AddFragment(new AsmMfpDocumentPickerTxtFragment(),"TXT");
        mAdapter.AddFragment(new AsmMfpDocumentPickerXlsFragment(),"XLS");



        //adapter setup
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);



    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent=new Intent(this,AsmMfpMainActivity.class);
        startActivity(intent);
    }
}
