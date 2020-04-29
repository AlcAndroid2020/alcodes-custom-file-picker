package com.alcodes.alcodessmmediafilepicker.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AsmMfpDocumentPickerViewPagerAdapter extends FragmentPagerAdapter {
    //declare a list of fragment needed to put in tab layout
    private final List<Fragment> FragmentList = new ArrayList<>();
    private final List<String> FragmentListTitles = new ArrayList<>();


    public AsmMfpDocumentPickerViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    //get fragment details
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FragmentList.get(position);
    }

    @Override
    public int getCount() {
        return FragmentList.size();
    }


    //get title of fragment
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return FragmentListTitles.get(position);
    }
    //allow to add fragment to tab layout
    public void AddFragment(Fragment fragment, String Title) {

        FragmentList.add(fragment);
        FragmentListTitles.add(Title);
    }
}
