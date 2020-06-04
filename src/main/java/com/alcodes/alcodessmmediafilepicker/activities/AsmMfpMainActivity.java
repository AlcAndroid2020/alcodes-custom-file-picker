package com.alcodes.alcodessmmediafilepicker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpActivityMainBinding;

import java.util.ArrayList;

import static com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpCustomFilePickerFragment.EXTRA_STRING_ARRAY_FILE_URI;

public class AsmMfpMainActivity extends AppCompatActivity {

    private AsmMfpActivityMainBinding mDataBinding;
    private NavController mNavController;
    private String EXTRA_INTEGER_SELECTED_THEME = "EXTRA_INTEGER_SELECTED_THEME";
    private ActionBar mActionBar;
    private ArrayList<String> mFileList;
    private int mColor, mTheme;
    public static final String EXTRA_STRING_ARRAY_FILE_URI = "EXTRA_STRING_ARRAY_FILE_URI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set theme
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
        // Init data binding.
        mDataBinding = AsmMfpActivityMainBinding.inflate(getLayoutInflater());

        setContentView(mDataBinding.getRoot());

        // Init navigation components.
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, mNavController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return mNavController.navigateUp() || super.onSupportNavigateUp();
    }



}

