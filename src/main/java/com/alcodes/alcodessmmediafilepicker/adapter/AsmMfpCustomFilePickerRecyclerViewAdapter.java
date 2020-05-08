package com.alcodes.alcodessmmediafilepicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AsmMfpCustomFilePickerRecyclerViewAdapter extends RecyclerView.Adapter<AsmMfpCustomFilePickerRecyclerViewAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<MyFile> myFileList;
    private ArrayList<MyFile> FilterList;
    private CustomFilePickerCallback callback;
    private CustomFilter filter;
    private int SelectionCount;

    public AsmMfpCustomFilePickerRecyclerViewAdapter(Context context, ArrayList<MyFile> filelist, CustomFilePickerCallback callbacks,int selectedCount) {
        this.myFileList = filelist;
        this.mContext = context;
        this.callback = callbacks;
        this.FilterList = myFileList;
        this.SelectionCount=selectedCount;
        filter = new CustomFilter();
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
        //to show selected when go back to album
        if (myFileList.get(position).getIsSelected()) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(true);
        }
        if (myFileList.get(position).getFileType() != null) {
            if (myFileList.get(position).getFileType().equals("Image"))
                Glide.with(mContext)
                        .load(Uri.parse(myFileList.get(position).getFileUri()))
                        // Uri of the picture
                        .into(holder.imgView);

            else if (myFileList.get(position).getFileType().equals("Video")) {
                if (!myFileList.get(position).getIsFolder()) {
                    //to generate video thumbnial from uri


                    Glide.with(mContext)
                            .load(Uri.parse(myFileList.get(position).getFileUri()))
                            // Uri of the picture
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
                if (myFileList.get(position).getIsFolder()) {
                    if (callback != null) {


                        callback.onFolderClicked(myFileList.get(position).getFolderID());
                    }
                } else {
                    //click on file




                    //unselect
                    if (myFileList.get(position).getIsSelected()) {
                        myFileList.get(position).setIsSelected(false);
                        holder.checkBox.setChecked(false);
                        holder.checkBox.setVisibility(View.INVISIBLE);

                        callback.onAlbumItemUnSelected(Uri.parse(myFileList.get(position).getFileUri()));
                    } else {
                        //Limit user selection ,maximum 5 items

                        if(SelectionCount<5)
                        {
                        //select
                        myFileList.get(position).setIsSelected(true);
                        holder.checkBox.setVisibility(View.VISIBLE);
                        holder.checkBox.setChecked(true);


                        callback.onAlbumItemSelected(Uri.parse(myFileList.get(position).getFileUri()));

                    }

                }}

            }
        });

    }



    public interface CustomFilePickerCallback {
        void onFolderClicked(int folderid);

    //    void onAlbumItemSelected(int position);

      //  void onAlbumItemUnSelected(int position);
      void onAlbumItemUnSelected(Uri uri);
      void onAlbumItemSelected(Uri uri);



    }

    @Override
    public int getItemCount() {
        return myFileList.size();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //to declare item in recyclerview (textview,image)
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imgView;
        private ImageView checkIC;
        private CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.Album_item_ImgView);
            textView = itemView.findViewById(R.id.Album_item_TextView);
            checkIC = itemView.findViewById(R.id.Album_item_Selected_Image);
            checkBox = itemView.findViewById(R.id.FilePicker_checkbox);
        }
    }

    //update the selection count from picker so to limit user selection
    public void setSelectionCount(int count){
        this.SelectionCount=count;
    }



    @Override
    public Filter getFilter() {

        return filter;
    }

    public class CustomFilter extends Filter {

        @Override

        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<MyFile> resultlist = new ArrayList<>();
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
                myFileList = (ArrayList<MyFile>) results.values;
                notifyDataSetChanged();
            }
        }


    }


}
