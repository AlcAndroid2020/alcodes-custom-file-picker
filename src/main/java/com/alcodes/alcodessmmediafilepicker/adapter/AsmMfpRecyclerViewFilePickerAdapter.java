package com.alcodes.alcodessmmediafilepicker.adapter;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.util.ArrayList;
import java.util.List;

public class AsmMfpRecyclerViewFilePickerAdapter extends RecyclerView.Adapter<AsmMfpRecyclerViewFilePickerAdapter.MyViewHolder> implements Filterable {


    public ArrayList<MyFile> myFileList;
    public ArrayList<MyFile> FilterList;

    @Override
    public Filter getFilter() {
        return null;
    }


    public interface ChnageStatusListener {
        void onItemChangeListener(int position, MyFile myuri);
    }
    Context mContext;
    ChnageStatusListener chnageStatusListener;
    public void setModels(ArrayList<MyFile> myFileList) {
        this.myFileList = myFileList;
    }
    public AsmMfpRecyclerViewFilePickerAdapter(ArrayList<MyFile> myFileList, Context mContext, ChnageStatusListener chnageStatusListener) {
        this.myFileList = myFileList;
        this.mContext = mContext;
        this.chnageStatusListener = chnageStatusListener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name, uri;
        public MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.Image_view_List_Item);
            name = (TextView) view.findViewById(R.id.Text_view_List_View_Name);
        }
    }
    public AsmMfpRecyclerViewFilePickerAdapter(ArrayList<MyFile> myFileList) {
        this.myFileList = myFileList;
        this.FilterList = myFileList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.asm_mfp_listview_item, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
