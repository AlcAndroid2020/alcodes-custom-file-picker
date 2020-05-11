package com.alcodes.alcodessmmediafilepicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AsmMfpDocumentPickerRecyclerViewAdapter extends RecyclerView.Adapter<AsmMfpDocumentPickerRecyclerViewAdapter.MyViewHolder> implements Filterable {
    Context mContext;
    ArrayList<MyFile> mFileList;
    ArrayList<MyFile> FilterList;

    DocumentFilePickerCallbacks callback;
    private CustomFilter filter;
    ArrayList<MyFile> resultlist;
    private int SelectedCounter;

    public AsmMfpDocumentPickerRecyclerViewAdapter(Context Context, ArrayList<MyFile> FileList, DocumentFilePickerCallbacks callbacks, int selectedCounter) {
        this.mContext = Context;
        this.mFileList = FileList;
        this.callback = callbacks;
        this.FilterList = mFileList;

        this.SelectedCounter = selectedCounter;
        filter = new CustomFilter();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.asm_mfp_item_document, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public long getItemId(int position) {

        //for solving search view onitem select problem


        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //init value to view
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_FileName.setText(mFileList.get(position).getFileName());

        if (mFileList.get(position).getIsSelected())
            holder.iv_CheckIcon.setVisibility(View.VISIBLE);


        //detect which file type then set suitable file icon
        if (mFileList.get(position).getFileType() != null) {
            switch (mFileList.get(position).getFileType()) {
                case "PDF":
                    holder.iv_FileIcon.setImageResource(R.drawable.ic_pdf);
                    break;
                case "DOCX":
                    holder.iv_FileIcon.setImageResource(R.drawable.ic_word);
                    break;
                case "PTT":
                    holder.iv_FileIcon.setImageResource(R.drawable.ic_ppt);
                    break;
                case "TXT":
                    holder.iv_FileIcon.setImageResource(R.drawable.ic_txt);
                    break;
                case "XLS":
                    holder.iv_FileIcon.setImageResource(R.drawable.ic_xls);
                    break;
            }
        } else
            holder.iv_FileIcon.setImageResource(R.drawable.ic_txt);

        if (mFileList.get(position).getFileSize() != null) {
            Long size = Long.parseLong(mFileList.get(position).getFileSize());
            holder.tv_FileSize.setText(readableFileSize(size));

        }

        //onclick on item of recyclerview
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFileList.get(position).getIsSelected()) {
                    holder.iv_CheckIcon.setVisibility(View.INVISIBLE);
                    mFileList.get(position).setIsSelected(false);
                    callback.onDocumentUnSelected(Uri.parse(mFileList.get(position).getFileUri()));
                } else {
                    //limit user selection,maximum 5 item only
                    if (SelectedCounter < 5) {

                        holder.iv_CheckIcon.setVisibility(View.VISIBLE);
                        mFileList.get(position).setIsSelected(true);
                        callback.onDocumentSelected(Uri.parse(mFileList.get(position).getFileUri()));

                    }


                }
            }
        });


    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public interface DocumentFilePickerCallbacks {

        void onDocumentSelected(Uri uri);

        void onDocumentUnSelected(Uri uri);
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    public void setSelectedCounter(int selectcounter) {
        this.SelectedCounter = selectcounter;
    }

    //to declare item in recyclerview (textview,image)
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_FileName;
        private TextView tv_FileSize;
        private ImageView iv_FileIcon, iv_CheckIcon;


        public MyViewHolder(View itemView) {
            super(itemView);
            tv_FileName = (TextView) itemView.findViewById(R.id.Text_view_Item_Document_FileName);
            tv_FileSize = (TextView) itemView.findViewById(R.id.Text_view_Item_Document_FileSize);
            iv_FileIcon = (ImageView) itemView.findViewById(R.id.Image_view_Item_Document_Icon);
            iv_CheckIcon = (ImageView) itemView.findViewById(R.id._Image_view_item_Document_check);
        }


    }


    @Override
    public Filter getFilter() {

        return filter;
    }

    public class CustomFilter extends Filter {

        @Override

        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            resultlist = new ArrayList<>();
            //filtering
            String searchvalue = constraint.toString().toLowerCase();
            for (int i = 0; i < FilterList.size(); i++) {
                String title = FilterList.get(i).getFileName();

                if (title.toLowerCase().contains(searchvalue)) {
                    resultlist.add(FilterList.get(i));
                }
            }
            filterResults.count = resultlist.size();
            filterResults.values = resultlist;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null) {
                mFileList = (ArrayList<MyFile>) results.values;
                notifyDataSetChanged();
            }
        }


    }

}
