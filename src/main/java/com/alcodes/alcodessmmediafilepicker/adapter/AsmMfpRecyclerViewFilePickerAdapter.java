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
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AsmMfpRecyclerViewFilePickerAdapter extends RecyclerView.Adapter<AsmMfpRecyclerViewFilePickerAdapter.MyViewHolder> implements Filterable {


    public ArrayList<MyFile> myFileList;
    public ArrayList<MyFile> FilterList;

    CustomFilter filter;

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
            image = (ImageView) view.findViewById(R.id.image_view_recycler_item);
            name = (TextView) view.findViewById(R.id.text_view_recycler_view_name);
        }
    }
    public AsmMfpRecyclerViewFilePickerAdapter(ArrayList<MyFile> myFileList) {
        this.myFileList = myFileList;
        this.FilterList = myFileList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.asm_mfp_recycler_view_item, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyFile uri = myFileList.get(position);
        Glide.with(holder.image)
                .load(uri.getFileUri())
                .into(holder.image);
        holder.name.setText(uri.getFileName());
    }

    //for search feature
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AsmMfpRecyclerViewFilePickerAdapter.CustomFilter();
        }
        return filter;
    }

    @Override
    public int getItemCount() {
        return 0;
    }
    //inner class

    public class CustomFilter extends Filter {

        @Override

        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            //filtering
            if (constraint != null && constraint.length() > 0) {
                //constraint to upper
                constraint = constraint.toString().toUpperCase();
                ArrayList<MyFile> filter = new ArrayList<>();
                for (int i = 0; i < FilterList.size(); i++) {
                    if (FilterList.get(i).getFileName().toUpperCase().contains(constraint)) {

                        MyFile file = new MyFile(FilterList.get(i).getFileName(), FilterList.get(i).getFileUri(), FilterList.get(i).getIsFolder());
                        filter.add(file);
                    }
                }
                results.count = filter.size();
                results.values = filter;
            } else {
                results.count = FilterList.size();
                results.values = FilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            myFileList = (ArrayList<MyFile>) results.values;
            notifyDataSetChanged();
        }
    }

}
