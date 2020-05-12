package com.alcodes.alcodessmmediafilepicker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpDocumentFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.adapter.AsmMfpDocumentPickerViewPagerAdapter;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpFragmentDocumentFilePickerBinding;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpDocumentViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class AsmMfpDocumentFilePickerFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AsmMfpFragmentDocumentFilePickerBinding mDataBinding;
    private NavController mNavController;
    private AsmMfpDocumentFilePickerActivity mActivity;
    private Boolean isSelected;
    private ArrayList<MyFile> myfilelist = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //   return super.onCreateView(inflater, container, savedInstanceState);
        mDataBinding = AsmMfpFragmentDocumentFilePickerBinding.inflate(inflater, container, false);

        tabLayout = mDataBinding.DocFilePickerTabLayout;

        viewPager = mDataBinding.DocFilePickerViewPager;
        isSelected = false;
        //adapter to add fragment
        AsmMfpDocumentPickerViewPagerAdapter mAdapter = new AsmMfpDocumentPickerViewPagerAdapter(getActivity().getSupportFragmentManager());

        mAdapter.AddFragment(new AsmMfpDocumentPickerPdfFragment(), "PDF");
        mAdapter.AddFragment(new AsmMfpDocumentPickerDocxFragment(), "DOCX");
        mAdapter.AddFragment(new AsmMfpDocumentPickerPttFragment(), "PPT");
        mAdapter.AddFragment(new AsmMfpDocumentPickerTxtFragment(), "TXT");
        mAdapter.AddFragment(new AsmMfpDocumentPickerXlsFragment(), "XLS");


        //adapter setup
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);


        return mDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init navigation component.
        mNavController = Navigation.findNavController(view);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.asm_mfp_menu_document_file_picker, menu);


    }

}
