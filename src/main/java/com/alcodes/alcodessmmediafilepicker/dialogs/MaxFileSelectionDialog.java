package com.alcodes.alcodessmmediafilepicker.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.databinding.DialogMaxFileSelectionBinding;
import com.alcodes.alcodessmmediafilepicker.utils.AsmMfpSharedPreferenceHelper;

import static com.alcodes.alcodessmmediafilepicker.fragments.AsmMfpMainFragment.EXTRA_INT_MAX_FILE_SELECTION;

public class MaxFileSelectionDialog extends DialogFragment {
    public static final String TAG = MaxFileSelectionDialog.class.getSimpleName();

    private DialogMaxFileSelectionBinding mDataBinding;
    private int mColor;
    private int mSoughtValue = 0; //No Limit
    private int Theme = 0;
    private String EXTRA_INTEGER_SELECTED_THEME = "EXTRA_INTEGER_SELECTED_THEME";

    public MaxFileSelectionDialog() {
    }

    public static MaxFileSelectionDialog newInstance() {
        return new MaxFileSelectionDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mDataBinding = DialogMaxFileSelectionBinding.inflate(requireActivity().getLayoutInflater());

        //Set The Cursor To The End Of The Edit Text
        mDataBinding.maxFileSelectionEdittext.setSelection(mDataBinding.maxFileSelectionEdittext.getText().length());

        //Seek Bar Change Listener
        mDataBinding.maxFileSelectionSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDataBinding.maxFileSelectionEdittext.setText(String.format("%d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Edit Text Change Listener
        mDataBinding.maxFileSelectionEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    //Edit Text Field is Empty
                    if (Integer.parseInt(s.toString()) > 100) {
                        //Over 200 (Maximum), Set the text to 200.
                        mDataBinding.maxFileSelectionEdittext.setText(String.format("%d", 100));
                        //Set Seek Value to 200 when Maximum Reach
                        mSoughtValue = 100;
                    } else {
                        //Set Current Seek Value
                        mSoughtValue = Integer.parseInt(s.toString());
                    }
                    mDataBinding.maxFileSelectionSeekbar.setProgress(Integer.parseInt(s.toString()));
                } else {
                    //Edit Text Field Not Empty
                    mDataBinding.maxFileSelectionSeekbar.setProgress(0);
                    mDataBinding.maxFileSelectionEdittext.setText(String.format("%d", 0));

                    //Empty therefore is 0 as Default
                    mSoughtValue = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Set The Cursor To The End Of The Edit Text
                mDataBinding.maxFileSelectionEdittext.setSelection(mDataBinding.maxFileSelectionEdittext.getText().length());
            }
        });

        MaterialDialog.Builder builder = new MaterialDialog.Builder(requireActivity());
        builder.title(getResources().getString(R.string.LimitFileSelection))
                .customView(mDataBinding.getRoot(), true)
                .positiveText(getResources().getString(R.string.Done))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        AsmMfpSharedPreferenceHelper.getInstance(requireContext())
                                .edit()
                                .putInt(EXTRA_INT_MAX_FILE_SELECTION,mSoughtValue)
                                .apply();
                    }
                })
                .negativeText(getResources().getString(R.string.Cancel))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getActivity().finish();
                        dialog.dismiss();
                    }
                });

        setCancelable(false);

        return builder.build();
    }

    public void setBackGroundColor(int color) {
        this.mColor = color;
    }

    public void setTheme(int theme) {
        this.Theme = theme;
    }

    public int getmSoughtValue(){
        return  mSoughtValue;
    }
}
