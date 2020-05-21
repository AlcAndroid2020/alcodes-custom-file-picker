package com.alcodes.alcodessmmediafilepicker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpGithubSampleFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpRecyclerViewFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpFragmentMainBinding;
import com.alcodes.alcodessmmediafilepicker.databinding.bindingcallbacks.MainBindingCallback;
import com.alcodes.alcodessmmediafilepicker.utils.AsmMfpSharedPreferenceHelper;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpCustomFilePickerViewModel;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpDocumentViewModel;

import timber.log.Timber;

public class AsmMfpMainFragment extends Fragment implements MainBindingCallback {
    public static final String EXTRA_INT_MAX_FILE_SELECTION = "EXTRA_INT_MAX_FILE_SELECTION";

    private AsmMfpFragmentMainBinding mDataBinding;
    private NavController mNavController;
    private AsmMfpCustomFilePickerViewModel mfpMainSharedViewModel;
    private int mColor;

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
        mfpMainSharedViewModel = new ViewModelProvider(
                mNavController.getBackStackEntry(R.id.asm_mfp_nav_main),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(AsmMfpCustomFilePickerViewModel.class);



        if (requireActivity().getIntent().getExtras() != null) {
            //When it was directing to here from Main Module
            mfpMainSharedViewModel.setMaxSelection(requireActivity().getIntent().getExtras().getInt(EXTRA_INT_MAX_FILE_SELECTION, 0));
            //Save maxFileSelection into Shared Preferences
            AsmMfpSharedPreferenceHelper.getInstance(requireContext())
                    .edit()
                    .putInt(EXTRA_INT_MAX_FILE_SELECTION, requireActivity().getIntent().getExtras().getInt(EXTRA_INT_MAX_FILE_SELECTION, 0))
                    .apply();

            mColor = requireActivity().getIntent().getExtras().getInt("color");

            mfpMainSharedViewModel.setBackgroundColor(mColor);
            Toast.makeText(getContext(),"value"+mColor,Toast.LENGTH_SHORT).show();

        } else {
            //When it was directing to here within Sub Module
            if (mfpMainSharedViewModel.getMaxSelection().getValue() != null) {
                mfpMainSharedViewModel.setMaxSelection(mfpMainSharedViewModel.getMaxSelection().getValue());
            } else {
                //When it returns from another acitivity, get the shared preference value
                mfpMainSharedViewModel.setMaxSelection(AsmMfpSharedPreferenceHelper.getInstance(requireContext()).getInt(EXTRA_INT_MAX_FILE_SELECTION, 0));
            }
            //for background color

            if (mfpMainSharedViewModel.getBackgroundColor().getValue() != null) {
                mColor=mfpMainSharedViewModel.getBackgroundColor().getValue();

            }
        }


        if (mColor != 0)
            mDataBinding.getRoot().setBackgroundColor(ContextCompat.getColor(getActivity(), mColor));

        Timber.e("" + mfpMainSharedViewModel.getMaxSelection().getValue());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init navigation component.
        mNavController = Navigation.findNavController(view);
    }

    @Override
    public void onImageFilePickerButtonClicked() {

        Intent intent = new Intent(getContext(), AsmMfpGithubSampleFilePickerActivity.class);
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
        Intent intent = new Intent(getContext(), AsmMfpListViewFilePicker.class);
        startActivity(intent);
    }

    @Override
    public void onRecyclerViewFilePickerButtonClicked() {
        Intent intent = new Intent(getContext(), AsmMfpRecyclerViewFilePickerActivity.class);
        startActivity(intent);
    }
}
