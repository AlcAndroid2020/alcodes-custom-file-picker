package com.alcodes.alcodessmmediafilepicker.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpActivityDocumentFilePickerBinding;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpActivityMainBinding;

public class AsmMfpDocumentFilePickerActivity extends AppCompatActivity {
    //The main activity for document picker to assign viewpager,fragments and tablayout


    private AsmMfpActivityDocumentFilePickerBinding mDataBinding;
    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = AsmMfpActivityDocumentFilePickerBinding.inflate(getLayoutInflater());

        setContentView(mDataBinding.getRoot());

        // Init navigation components.
        mNavController = Navigation.findNavController(this, R.id.nav_document_fragment);

        NavigationUI.setupActionBarWithNavController(this, mNavController);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(this, AsmMfpMainActivity.class);
        startActivity(intent);
    }
}
