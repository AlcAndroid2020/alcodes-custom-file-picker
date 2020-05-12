package com.alcodes.alcodessmmediafilepicker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpGithubSampleFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpRecyclerViewFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpFragmentMainBinding;
import com.alcodes.alcodessmmediafilepicker.databinding.bindingcallbacks.MainBindingCallback;

public class AsmMfpMainFragment extends Fragment  implements MainBindingCallback {

    private AsmMfpFragmentMainBinding mDataBinding;
    private NavController mNavController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Init data binding;
        mDataBinding = AsmMfpFragmentMainBinding.inflate(inflater, container, false);

        return mDataBinding.getRoot();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Init binding callback.
        mDataBinding.setBindingCallback(this);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init navigation component.
        mNavController = Navigation.findNavController(view);
    }

    @Override
    public void onImageFilePickerButtonClicked() {

        Intent intent=new Intent(getContext(), AsmMfpGithubSampleFilePickerActivity.class);
        startActivity(intent);

    }

    @Override
    public void onCustomPickerButtonClicked() {
/*
        Intent intent=new Intent(getContext(), AsmMfpCustomFilePicker.class);
        startActivity(intent);
*/
        mNavController.navigate(R.id.asm_mfp_action_asm_mfp_mainfragment_to_asm_mfp_customfilepickerfragment);

    }

    @Override
    public void onListViewFilePickerButtonClicked() {
        Intent intent=new Intent(getContext(), AsmMfpListViewFilePicker.class);
        startActivity(intent);
    }

    @Override
    public void onRecyclerViewFilePickerButtonClicked() {
        Intent intent = new Intent(getContext(), AsmMfpRecyclerViewFilePickerActivity.class);
        startActivity(intent);
    }
}
