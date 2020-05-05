package com.alcodes.alcodessmmediafilepicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AsmMfpDocumentPickerRecyclerViewAdapter extends RecyclerView.Adapter<AsmMfpDocumentPickerRecyclerViewAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<MyFile> mFileList;
    DocumentFilePickerCallbacks callback;

    public AsmMfpDocumentPickerRecyclerViewAdapter(Context Context, ArrayList<MyFile> FileList, DocumentFilePickerCallbacks callbacks) {
        this.mContext = Context;
        this.mFileList = FileList;
        this.callback = callbacks;
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
                    holder.iv_CheckIcon.setVisibility(View.VISIBLE);
                    mFileList.get(position).setIsSelected(true);
                    callback.onDocumentSelected(Uri.parse(mFileList.get(position).getFileUri()));
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
}
