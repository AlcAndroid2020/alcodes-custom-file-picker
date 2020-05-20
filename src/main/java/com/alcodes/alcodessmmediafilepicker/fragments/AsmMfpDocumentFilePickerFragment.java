package com.alcodes.alcodessmmediafilepicker.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
    AsmMfpDocumentPickerViewPagerAdapter mAdapter;
    private Integer mViewPagerPosition;
    private AsmMfpDocumentViewModel mDocumentViewModel;
    private  int mColor;
    private static final int PERMISSION_STORGE_CODE = 1000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDocumentViewModel = new ViewModelProvider(mNavController.getBackStackEntry(R.id.asm_mfp_nav_document),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).
                get(AsmMfpDocumentViewModel.class);

        mViewPagerPosition = viewPager.getCurrentItem();
        mDocumentViewModel.setViewPagerPosition(viewPager.getCurrentItem());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position != mViewPagerPosition) {
                    mViewPagerPosition = position;
                    mDocumentViewModel.setViewPagerPosition(position);
                }

                //maintain action mode
                mDocumentViewModel.setIsSwiped(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if(requireActivity().getIntent().getExtras()!=null) {
            mColor = requireActivity().getIntent().getExtras().getInt("color");
            mDataBinding.getRoot().setBackgroundColor(mColor);
            Toast.makeText(getContext(),"got color",Toast.LENGTH_SHORT).show();
        }

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
        mAdapter = new AsmMfpDocumentPickerViewPagerAdapter(getActivity().getSupportFragmentManager());
        if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, PERMISSION_STORGE_CODE);
        } else {
            init();
        }

        viewPager.setOffscreenPageLimit(5);
        return mDataBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDocumentViewModel.getViewPagerPosition().getValue() != null) {
            mViewPagerPosition = mDocumentViewModel.getViewPagerPosition().getValue();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDocumentViewModel.setViewPagerPosition(mViewPagerPosition);
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
    //after unselect to clear the selected item

    public void refreshFragments() {
        tabLayout.invalidate();
        tabLayout.refreshDrawableState();
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

    public void init() {
        tabLayout = mDataBinding.DocFilePickerTabLayout;

        viewPager = mDataBinding.DocFilePickerViewPager;
        isSelected = false;
        //adapter to add fragment
        mAdapter = new AsmMfpDocumentPickerViewPagerAdapter(getActivity().getSupportFragmentManager());

        mAdapter.AddFragment(new AsmMfpDocumentPickerPdfFragment(), getResources().getString(R.string.pdf));
        mAdapter.AddFragment(new AsmMfpDocumentPickerDocxFragment(), getResources().getString(R.string.Word));
        mAdapter.AddFragment(new AsmMfpDocumentPickerPttFragment(), getResources().getString(R.string.powerpoint));
        mAdapter.AddFragment(new AsmMfpDocumentPickerTxtFragment(), getResources().getString(R.string.txt));
        mAdapter.AddFragment(new AsmMfpDocumentPickerXlsFragment(), getResources().getString(R.string.excel));

        //adapter setup
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
