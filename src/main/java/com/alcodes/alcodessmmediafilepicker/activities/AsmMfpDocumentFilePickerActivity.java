package com.alcodes.alcodessmmediafilepicker.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpActivityDocumentFilePickerBinding;
import com.alcodes.alcodessmmediafilepicker.viewmodels.AsmMfpCustomFilePickerViewModel;

public class AsmMfpDocumentFilePickerActivity extends AppCompatActivity {
    //The main activity for document picker to assign viewpager,fragments and tablayout

    private AsmMfpActivityDocumentFilePickerBinding mDataBinding;
    private Integer mColor;

    private AsmMfpCustomFilePickerViewModel mfpCustomFilePickerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = AsmMfpActivityDocumentFilePickerBinding.inflate(getLayoutInflater());

        setContentView(mDataBinding.getRoot());
        // Init navigation components.
        NavController mNavController = Navigation.findNavController(this, R.id.nav_document_fragment);

        NavigationUI.setupActionBarWithNavController(this, mNavController);
        mfpCustomFilePickerViewModel = new ViewModelProvider(mNavController.getBackStackEntry(R.id.asm_mfp_nav_document),
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).
                get(AsmMfpCustomFilePickerViewModel.class);

    }

}
