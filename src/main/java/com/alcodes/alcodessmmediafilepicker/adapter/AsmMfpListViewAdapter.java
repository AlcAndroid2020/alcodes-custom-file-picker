package com.alcodes.alcodessmmediafilepicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AsmMfpListViewAdapter extends RecyclerView.Adapter<AsmMfpListViewAdapter.MyViewHolder> implements Filterable {

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

    public AsmMfpListViewAdapter(ArrayList<MyFile> myFileList, Context mContext, ChnageStatusListener chnageStatusListener) {
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


    public AsmMfpListViewAdapter(ArrayList<MyFile> myFileList) {
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

        if (myFileList.get(position).getFileType().equals("Image")) {
            MyFile uri = myFileList.get(position);
            Glide.with(holder.image)
                    .load(uri.getFileUri())
                    .into(holder.image);
            holder.name.setText(uri.getFileName());
        } else if (myFileList.get(position).getFileType().equals("Video")) {

            if (!myFileList.get(position).getIsFolder()) {
                //to generate video thumbnial from uri
                MyFile uri = myFileList.get(position);
                Glide.with(holder.image)
                        .load(uri.getFileUri())
                        .into(holder.image);
                holder.name.setText(uri.getFileName());
            } else {
                MyFile uri = myFileList.get(position);
                holder.name.setText(uri.getFileName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return myFileList.size();
    }

    //for search feature
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AsmMfpListViewAdapter.CustomFilter();
        }
        return filter;
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
