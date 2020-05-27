package com.alcodes.alcodessmmediafilepicker.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpActivityDocumentFilePickerBinding;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpCustomFilePickerFragment;
import com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpDocumentPickerMergedFileTypeFragment;
import com.alcodes.alcodessmmediafilepicker.utils.AsmMfpSharedPreferenceHelper;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpCustomFilePickerViewModel;

public class AsmMfpDocumentFilePickerActivity extends AppCompatActivity {
    //The main activity for document picker to assign viewpager,fragments and tablayout
    private String EXTRA_INTEGER_SELECTED_THEME = "EXTRA_INTEGER_SELECTED_THEME";

    private AsmMfpActivityDocumentFilePickerBinding mDataBinding;
    private Integer mColor;

    private AsmMfpCustomFilePickerViewModel mfpCustomFilePickerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getInt(EXTRA_INTEGER_SELECTED_THEME) == 1) {
                setTheme(R.style.asm_mfp_apps_theme_semi_transparent);
            } else if (getIntent().getExtras().getInt(EXTRA_INTEGER_SELECTED_THEME) == 2) {
                setTheme(R.style.asm_mfp_apps_theme_transparent);
            } else {
                setTheme(R.style.asm_mfp_apps_default);
            }
        }
        super.onCreate(savedInstanceState);

        mDataBinding = AsmMfpActivityDocumentFilePickerBinding.inflate(getLayoutInflater());

        setContentView(mDataBinding.getRoot());
        // Init navigation components.
        NavController mNavController = Navigation.findNavController(this, R.id.nav_document_fragment);

        NavigationUI.setupActionBarWithNavController(this, mNavController);
        mfpCustomFilePickerViewModel = new ViewModelProvider(mNavController.getBackStackEntry(R.id.asm_mfp_nav_document),
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).
                get(AsmMfpCustomFilePickerViewModel.class);

        if (mfpCustomFilePickerViewModel.getBackgroundColor().getValue() != null)
            mColor = mfpCustomFilePickerViewModel.getBackgroundColor().getValue();
    }

}
