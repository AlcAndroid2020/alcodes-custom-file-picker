package com.alcodes.alcodessmmediafilepicker.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AsmMfpSharedPreferenceHelper {
    private static SharedPreferences mInstance;

    public static SharedPreferences getInstance(Context context){
        if(mInstance == null){
            synchronized (AsmMfpSharedPreferenceHelper.class){
                if(mInstance == null){
                    mInstance = context.getSharedPreferences("alcodes-sm-media-file-picker",Context.MODE_PRIVATE);
                }
            }
        }

        return mInstance;
    }

    public AsmMfpSharedPreferenceHelper() {
    }
}
