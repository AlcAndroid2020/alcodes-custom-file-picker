package com.alcodes.alcodessmmediafilepicker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
    private NavController mNavController;
    private Integer mColor;

    private AsmMfpCustomFilePickerViewModel mfpCustomFilePickerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = AsmMfpActivityDocumentFilePickerBinding.inflate(getLayoutInflater());

        setContentView(mDataBinding.getRoot());

        // Init navigation components.
        mNavController = Navigation.findNavController(this, R.id.nav_document_fragment);

        NavigationUI.setupActionBarWithNavController(this, mNavController);
        mfpCustomFilePickerViewModel = new ViewModelProvider(mNavController.getBackStackEntry(R.id.asm_mfp_nav_document),
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).
                get(AsmMfpCustomFilePickerViewModel.class);

    /*    //init tablayout and viewpager
        tabLayout = (TabLayout) findViewById(R.id.Doc_FilePicker_TabLayout);
        viewPager = (ViewPager) findViewById(R.id.Doc_FilePicker_ViewPager);
        isSelected = false;
        //adapter to add fragment
        AsmMfpDocumentPickerViewPagerAdapter mAdapter = new AsmMfpDocumentPickerViewPagerAdapter(getSupportFragmentManager());

        mAdapter.AddFragment(new AsmMfpDocumentPickerPdfFragment(), "PDF");
        mAdapter.AddFragment(new AsmMfpDocumentPickerDocxFragment(), "DOCX");
        mAdapter.AddFragment(new AsmMfpDocumentPickerPttFragment(), "PPT");
        mAdapter.AddFragment(new AsmMfpDocumentPickerTxtFragment(), "TXT");
        mAdapter.AddFragment(new AsmMfpDocumentPickerXlsFragment(), "XLS");


        //adapter setup
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);

*/

        if (mfpCustomFilePickerViewModel.getBackgroundColor().getValue() != null)
            mColor = mfpCustomFilePickerViewModel.getBackgroundColor().getValue();
    }


    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.asm_mfp_menu_document_file_picker, menu);
            MenuItem checkItem = menu.findItem(R.id.Doc_FilePicker_DoneSelection);
            if (isSelected) {
                checkItem.setVisible(true);
            } else {
                checkItem.setVisible(false);
            }
            return super.onCreateOptionsMenu(menu);

        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            if (item.getItemId() == R.id.Doc_FilePicker_DoneSelection) {
                ArrayList<String> selectedFileList = new ArrayList<>();
                if (myfilelist != null) {
                    for (int i = 0; i < myfilelist.size(); i++) {
                        if (myfilelist.get(i).getIsSelected())
                            selectedFileList.add(myfilelist.get(i).getFileUri());
                    }

                    Intent intent = new Intent(this, AsmGvrMainActivity.class);
                    intent.putStringArrayListExtra(AsmMfpGithubSampleFilePickerActivity.EXTRA_STRING_ARRAY_FILE_URI, selectedFileList);

                    startActivity(intent);
                }

            }
            return super.onOptionsItemSelected(item);

        }


    */

}
