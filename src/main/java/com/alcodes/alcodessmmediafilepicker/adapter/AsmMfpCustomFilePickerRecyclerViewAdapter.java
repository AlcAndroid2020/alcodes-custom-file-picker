package com.alcodes.alcodessmmediafilepicker.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AsmMfpCustomFilePickerRecyclerViewAdapter extends RecyclerView.Adapter<AsmMfpCustomFilePickerRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    public ArrayList<MyFile> myFileList;
    private   CustomFilePickerCallback callback;

    public AsmMfpCustomFilePickerRecyclerViewAdapter(Context context, ArrayList<MyFile> filelist, CustomFilePickerCallback callbacks) {
        this.myFileList = filelist;
        this.mContext = context;
        this.callback=callbacks;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.asm_mfp_album_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        if (myFileList.get(position).getFileType() != null) {
            if (myFileList.get(position).getFileType().equals("Image"))
                Glide.with(mContext)
                        .load(Uri.parse(myFileList.get(position).getFileUri())) // Uri of the picture
                        .into(holder.imgView);

            else if (myFileList.get(position).getFileType().equals("Video")) {
                if (!myFileList.get(position).getIsFolder()) {
                    //to generate video thumbnial from uri


                    Glide.with(mContext)
                            .load(Uri.parse(myFileList.get(position).getFileUri()))// Uri of the picture
                            .into(holder.imgView);



                }

                }

            }


        //check if is folder or image


        if (myFileList.get(position).getIsFolder())
            //if folder add count
            holder.textView.setText(myFileList.get(position).getFileName() + "(" + myFileList.get(position).getCount() + ")");
            //no folder
        else
            holder.textView.setText(myFileList.get(position).getFileName());

        //user select a folder then pass folder name to access inner file
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //click on folder
                if(myFileList.get(position).getIsFolder()){
                    if(callback!=null){
                    callback.onFolderClicked(myFileList.get(position).getFileName());}
                }else{
                    //click on file
                    if(myFileList.get(position).getIsSelected()){
                        holder.checkIC.setVisibility(View.INVISIBLE);
                        myFileList.get(position).setIsSelected(false);
                        callback.onFileCliked(myFileList);
                    }
                    else
                    {
                        holder.checkIC.setVisibility(View.VISIBLE);
                        myFileList.get(position).setIsSelected(true);
                        callback.onFileCliked(myFileList);

                    }

                }

            }
        });

    }

    public interface CustomFilePickerCallback{
       void onFolderClicked(String foldername);
       void onFileCliked(ArrayList<MyFile> filelist);
    }
    @Override
    public int getItemCount() {
        return myFileList.size();

    }

    //to declare item in recyclerview (textview,image)
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imgView;
        private ImageView checkIC;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.Album_item_ImgView);
            textView = itemView.findViewById(R.id.Album_item_TextView);
            checkIC=itemView.findViewById(R.id.Album_item_Selected_Image);
        }
    }

}
