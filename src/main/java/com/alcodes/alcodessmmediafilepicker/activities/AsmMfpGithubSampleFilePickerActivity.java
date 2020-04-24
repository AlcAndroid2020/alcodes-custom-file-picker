package com.alcodes.alcodessmmediafilepicker.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.alcodes.alcodessmgalleryviewer.activities.AsmGvrMainActivity;
import com.alcodes.alcodessmmediafilepicker.R;

import java.io.File;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class AsmMfpGithubSampleFilePickerActivity extends AppCompatActivity {
    private static final int PERMISSION_STORGE_CODE = 1000;
    private static final int CUSTOM_REQUEST_CODE = 532;
    public static final String EXTRA_INTEGER_SELECTED_THEME = "EXTRA_INTEGER_SELECTED_THEME";

    private ArrayList<Uri> photoPaths = new ArrayList<>();
    public ArrayList<String> mFileList;
    public static final String EXTRA_STRING_ARRAY_FILE_URI = "EXTRA_STRING_ARRAY_FILE_URI";

    public ArrayList<Uri> docPaths = new ArrayList<>();
    private Button PicFilekButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asm_mfp_activity_github_sample_file_picker);
        PicFilekButton = findViewById(R.id.pickFileButton);
        PicFilekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prmoptSelection();
            }
        });

    }

    public void prmoptSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Which file type you prefer ?");

        builder.setMessage("select one of these");


        builder.setPositiveButton("Image", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RunImageFilePicker();
            }
        });
        builder.setNegativeButton("Document file", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RunDocFilePicker();
            }
        });
        builder.show();
    }

    public void RunImageFilePicker() {

        // Ask for one permission

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, PERMISSION_STORGE_CODE);
        } else {
            FilePickerBuilder.getInstance().setMaxCount(5)
                    .setSelectedFiles(photoPaths)
                    .pickPhoto(this, CUSTOM_REQUEST_CODE);

        }
    }


    private void RunDocFilePicker() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, PERMISSION_STORGE_CODE);
        } else {
            FilePickerBuilder.getInstance().setMaxCount(10)
                    .setSelectedFiles(docPaths)
                    .pickFile(this);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //for file picker
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);

                    docPaths = new ArrayList<Uri>();
                    docPaths.addAll(dataList);
                }
                break;


            case CUSTOM_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                    if (dataList != null) {
                        photoPaths = new ArrayList<Uri>();
                        photoPaths.addAll(dataList);
                    }
                }
                break;

        }

        addThemToView(photoPaths, docPaths);

    }

    private void addThemToView(ArrayList<Uri> photoPaths, ArrayList<Uri> docPaths) {
        ArrayList<Uri> filePaths = new ArrayList<>();
        mFileList = new ArrayList<>();

        if (photoPaths != null) filePaths.addAll(photoPaths);

        if (docPaths != null) filePaths.addAll(docPaths);

        for (int i = 0; i < filePaths.size(); i++) {
            Uri uri = filePaths.get(i);

           mFileList.add(uri.toString());

}
        Intent intent = new Intent(this, AsmGvrMainActivity.class);
        intent.putStringArrayListExtra(AsmMfpGithubSampleFilePickerActivity.EXTRA_STRING_ARRAY_FILE_URI, mFileList);

        startActivity(intent);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prmoptSelection();
                }
            }
        }
    }

}
