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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;

import java.util.ArrayList;

public class AsmMfpCustomFilePickerAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    public ArrayList<MyFile> myFileList;
    public AsmMfpCustomFilePickerAdapter(Context context, ArrayList<MyFile> filelist){
        this.myFileList=filelist;
        this.mContext=context;
    }


    @Override
    public int getCount() {
        return myFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null)
            convertView = inflater.inflate(R.layout.asm_mfp_album_item, null);

        ImageView imgView = convertView.findViewById(R.id.Album_item_ImgView);
        TextView textView = convertView.findViewById(R.id.Album_item_TextView);
        if (myFileList.get(position).getFileType().equals("Image"))
            imgView.setImageURI(Uri.parse(myFileList.get(position).getFileUri()));
        else if (myFileList.get(position).getFileType().equals("Video")) {
            if (!myFileList.get(position).getIsFolder()){
                //to generate video thumbnial from uri
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = mContext.getContentResolver().query(Uri.parse(myFileList.get(position).getFileUri()), filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MICRO_KIND);


                imgView.setImageBitmap(bitmap);}
        }
        //check if is folder or image


        if (myFileList.get(position).getIsFolder())
            //if folder add count
            textView.setText(myFileList.get(position).getFileName() + "(" + myFileList.get(position).getCount() + ")");
            //no folder
        else
            textView.setText(myFileList.get(position).getFileName());

        return convertView;
    }

}
