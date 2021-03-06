package com.alcodes.alcodessmmediafilepicker.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.activities.AsmMfpDocumentFilePickerActivity;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpFragmentMainBinding;
import com.alcodes.alcodessmmediafilepicker.databinding.bindingcallbacks.MainBindingCallback;
import com.alcodes.alcodessmmediafilepicker.dialogs.AsmMfpMaxFileSelectionDialog;
import com.alcodes.alcodessmmediafilepicker.utils.AsmMfpSharedPreferenceHelper;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpCustomFilePickerViewModel;

import java.util.ArrayList;

import static com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpCustomFilePickerFragment.EXTRA_STRING_ARRAY_FILE_URI;

public class AsmMfpMainFragment extends Fragment implements MainBindingCallback {
    public static final String EXTRA_INT_MAX_FILE_SELECTION = "EXTRA_INT_MAX_FILE_SELECTION";
    private static final int OPEN_DOCUMENT_REQUEST_CODE = 42;

    private AsmMfpFragmentMainBinding mDataBinding;
    private NavController mNavController;
    private AsmMfpCustomFilePickerViewModel mfpMainSharedViewModel;
    private ArrayList<String> mFileList;
    private ArrayList<String> mFileListForAndroid10 = new ArrayList<>();
    private AppCompatActivity mAppCompatActivity;


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
        //for dialog of max selection

        // Init binding callback.
        mDataBinding.setBindingCallback(this);
        mfpMainSharedViewModel = new ViewModelProvider(
                mNavController.getBackStackEntry(R.id.asm_mfp_nav_main),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(AsmMfpCustomFilePickerViewModel.class);

        AsmMfpMaxFileSelectionDialog maxFileSelectionDialog = new AsmMfpMaxFileSelectionDialog();
        if (mfpMainSharedViewModel.getPickerFileType().getValue() == null)
            maxFileSelectionDialog.show(getParentFragmentManager(), AsmMfpMaxFileSelectionDialog.TAG);

        if (requireActivity().getIntent().getExtras() != null) {
            mfpMainSharedViewModel.setMaxSelection(requireActivity().getIntent().getExtras().getInt(EXTRA_INT_MAX_FILE_SELECTION, 0));

        } else {
            //When it was directing to here within Sub Module
            if (mfpMainSharedViewModel.getMaxSelection().getValue() != null) {
                mfpMainSharedViewModel.setMaxSelection(mfpMainSharedViewModel.getMaxSelection().getValue());
            } else {
                //When it returns from another acitivity, get the shared preference value
                mfpMainSharedViewModel.setMaxSelection(AsmMfpSharedPreferenceHelper.getInstance(requireContext()).getInt(EXTRA_INT_MAX_FILE_SELECTION, 0));
            }
            //for background color

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init navigation component.
        mNavController = Navigation.findNavController(view);
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
            startActivityForResult(intent, OPEN_DOCUMENT_REQUEST_CODE);

        } else {
            //Android 7 to Android 9
            Intent intent = new Intent(requireContext(), AsmMfpDocumentFilePickerActivity.class);
            intent.putExtra(AsmMfpMainFragment.EXTRA_INT_MAX_FILE_SELECTION, AsmMfpSharedPreferenceHelper.getInstance(requireContext()).getInt(EXTRA_INT_MAX_FILE_SELECTION, 0));
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Please, use a final int instead of hardcoded int value
        mFileListForAndroid10 = new ArrayList<>();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_DOCUMENT_REQUEST_CODE) {
                if (null != data) {
                    if (null != data.getClipData()) {
                        // Multiple document is selected
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            mFileListForAndroid10.add(data.getClipData().getItemAt(i).getUri().toString());
                        }
                    } else {
                        //Single document is selected
                        mFileListForAndroid10.add(data.getData().toString());
                    }

                    Intent ResultIntent = new Intent();
                    ResultIntent.putExtra(EXTRA_STRING_ARRAY_FILE_URI, mFileListForAndroid10);
                    requireActivity().setResult(Activity.RESULT_OK, ResultIntent);
                    requireActivity().finish();
                }
            }

            if (requestCode == 1) {

                mFileList = data.getExtras().getStringArrayList(EXTRA_STRING_ARRAY_FILE_URI);
                BackToMainModule();


            }

        }

    }

    //this is for document picker to finish another time for return to main module
    private void BackToMainModule() {
        Intent ResultIntent = new Intent();
        ResultIntent.putExtra(EXTRA_STRING_ARRAY_FILE_URI, mFileList);
        requireActivity().setResult(Activity.RESULT_OK, ResultIntent);
        requireActivity().finish();
    }

}
