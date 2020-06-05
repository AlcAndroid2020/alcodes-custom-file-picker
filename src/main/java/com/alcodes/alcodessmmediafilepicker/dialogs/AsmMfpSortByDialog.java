package com.alcodes.alcodessmmediafilepicker.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.databinding.AsmMfpDialogFileSortingBinding;
import com.alcodes.alcodessmmediafilepicker.databinding.bindingcallbacks.SortByDialogCallback;

public class AsmMfpSortByDialog extends DialogFragment {
    public static final String TAG = AsmMfpSortByDialog.class.getSimpleName();

    private AsmMfpDialogFileSortingBinding mDatabinding;

    private SortByDialogCallback mCallback;

    private String mCurrentSortingStyle;

    public AsmMfpSortByDialog() {
    }

    public static AsmMfpSortByDialog newInstance(SortByDialogCallback sortByDialogCallback, String currentSortingStyle) {

        AsmMfpSortByDialog asmMfpSortByDialog = new AsmMfpSortByDialog();

        asmMfpSortByDialog.setCallback(sortByDialogCallback);
        asmMfpSortByDialog.setCurrentSortingStyle(currentSortingStyle);

        return asmMfpSortByDialog;
    }

    private void setCallback(SortByDialogCallback sortByDialogCallback) {
        this.mCallback = sortByDialogCallback;
    }

    private void setCurrentSortingStyle(String currentSortingStyle) {
        this.mCurrentSortingStyle = currentSortingStyle;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mDatabinding = AsmMfpDialogFileSortingBinding.inflate(requireActivity().getLayoutInflater());

        //Set current sorting style.
        initSortRadioGroup();
        initOrderRadioGroup();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(requireActivity());
        builder.title(getResources().getString(R.string.Sortby))
                .customView(mDatabinding.getRoot(), true)
                .positiveText(getResources().getString(R.string.Okay))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mCallback.onSortByDialogPositiveButtonClicked(getSortingStyle());
                    }
                })
                .negativeText(getResources().getString(R.string.Cancel))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        setCancelable(false);

        return builder.build();
    }

    private String getSortingStyle() {
        if (mDatabinding.sortByNameRadioButton.isChecked()) {
            //Name is Checked
            return "SortingName" + getOrder();
        } else {
            //Date is Checked
            return "SortingDate" + getOrder();
        }
    }

    private String getOrder() {
        if (mDatabinding.ascendingOrderRadioButton.isChecked()) {
            //Ascending is Checked
            return "Ascending";
        } else {
            //Descending is Checked
            return "Descending";
        }
    }

    private void initSortRadioGroup() {
        //Sorting{Name}Ascending
        if (mCurrentSortingStyle.substring(7, 11).toLowerCase().equals("name")) {
            mDatabinding.sortByNameRadioButton.setChecked(true);
            mDatabinding.sortByDateRadioButton.setChecked(false);
        } else {
            mDatabinding.sortByNameRadioButton.setChecked(false);
            mDatabinding.sortByDateRadioButton.setChecked(true);
        }
    }

    private void initOrderRadioGroup() {
        //SortingName{Ascending}
        if (mCurrentSortingStyle.substring(11).toLowerCase().equals("ascending")) {
            mDatabinding.ascendingOrderRadioButton.setChecked(true);
            mDatabinding.descendingOrderRadioButton.setChecked(false);
        } else {
            mDatabinding.ascendingOrderRadioButton.setChecked(false);
            mDatabinding.descendingOrderRadioButton.setChecked(true);
        }
    }
}
