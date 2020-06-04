package com.alcodes.alcodessmmediafilepicker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpDocumentFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpFragmentMainBinding;
import com.alcodes.alcodessmmediafilepicker.databinding.bindingcallbacks.MainBindingCallback;
import com.alcodes.alcodessmmediafilepicker.utils.AsmMfpSharedPreferenceHelper;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpCustomFilePickerViewModel;

import timber.log.Timber;

public class AsmMfpMainFragment extends Fragment implements MainBindingCallback {
    public static final String EXTRA_INT_MAX_FILE_SELECTION = "EXTRA_INT_MAX_FILE_SELECTION";
    private static final int OPEN_DOCUMENT_REQUEST_CODE = 42;

    private AsmMfpFragmentMainBinding mDataBinding;
    private NavController mNavController;
    private AsmMfpCustomFilePickerViewModel mfpMainSharedViewModel;
    private int mColor, mTheme;
    private AppCompatActivity mAppCompatActivity;

    String EXTRA_INTEGER_SELECTED_THEME = "EXTRA_INTEGER_SELECTED_THEME";

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
        mAppCompatActivity = ((AppCompatActivity) requireActivity());
        mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //to hide home button when back from media picker
        mAppCompatActivity = ((AppCompatActivity) requireActivity());
        mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mAppCompatActivity = ((AppCompatActivity) requireActivity());
        mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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


            if (requireActivity().getIntent().getExtras().getInt(EXTRA_INTEGER_SELECTED_THEME) != 0)
                mTheme = requireActivity().getIntent().getExtras().getInt(EXTRA_INTEGER_SELECTED_THEME);

            if(mTheme!=0)
                mfpMainSharedViewModel.setTheme(mTheme);

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
                mColor = mfpMainSharedViewModel.getBackgroundColor().getValue();
            }
        }

        if (mColor != 0)
            mDataBinding.getRoot().setBackgroundColor(ContextCompat.getColor(getActivity(), mColor));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init navigation component.
        mNavController = Navigation.findNavController(view);
    }

    public int getTheme(){

        return mTheme;
    }

    @Override
    public void onMediaFilePickerButtonClicked() {
        mNavController.navigate(R.id.asm_mfp_action_asm_mfp_mainfragment_to_asm_mfp_customfilepickerfragment);
    }

    @Override
    public void onDocumentFilePickerButtonClicked() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            //Android 10 and above
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            String[] mimeTypes = {
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtx"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtf"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("html"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls"),
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx")};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra(AsmMfpCustomFilePickerFragment.EXTRA_INTEGER_SELECTED_THEME, mTheme);
            startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_CODE);
        } else {
            //Android 7 to Android 9
            Intent intent = new Intent(requireContext(), AsmMfpDocumentFilePickerActivity.class);
            intent.putExtra(AsmMfpMainFragment.EXTRA_INT_MAX_FILE_SELECTION, AsmMfpSharedPreferenceHelper.getInstance(requireContext()).getInt(EXTRA_INT_MAX_FILE_SELECTION, 0));
            intent.putExtra(AsmMfpCustomFilePickerFragment.EXTRA_INTEGER_SELECTED_THEME, mTheme);
            intent.putExtra("color", mColor);
            startActivity(intent);
        }
    }
}
