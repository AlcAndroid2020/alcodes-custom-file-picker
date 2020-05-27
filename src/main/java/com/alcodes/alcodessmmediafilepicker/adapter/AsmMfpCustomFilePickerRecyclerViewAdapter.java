package com.alcodes.alcodessmmediafilepicker.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alcodes.alcodessmmediafilepicker.R;
import com.alcodes.alcodessmmediafilepicker.utils.MyFile;
import com.bumptech.glide.Glide;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

public class AsmMfpCustomFilePickerRecyclerViewAdapter extends RecyclerView.Adapter<AsmMfpCustomFilePickerRecyclerViewAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<MyFile> myFileList;
    private ArrayList<MyFile> FilterList;
    private CustomFilePickerCallback callback;
    private CustomFilter filter;
    private int SelectionCount;
    private int mMaxFileSelection;
    private int mCurrentView = 0; //0 -> List View , 1 -> GridView

    public AsmMfpCustomFilePickerRecyclerViewAdapter(Context context, ArrayList<MyFile> filelist, CustomFilePickerCallback callbacks, int selectedCount) {
        this.myFileList = filelist;
        this.mContext = context;
        this.callback = callbacks;
        this.FilterList = myFileList;
        this.SelectionCount = selectedCount;
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
        ImageView currentViewImageView;
        ImageView currentViewImageViewChecked;
        TextView currentViewTextView;

        //Set Its Appearance
        //Only one group of view is loaded at a time to reduce memory burden
        if (mCurrentView == 0) {
            //List View
            holder.mAlbumInListView.setVisibility(View.VISIBLE);
            holder.mAlbumInGridView.setVisibility(View.GONE);
            currentViewImageView = holder.mAlbumItemThumbnailInListView;
            currentViewImageViewChecked = holder.mAlbumItemCheckedInListView;
            currentViewTextView = holder.mAlbumItemFileNameInListView;
        } else {
            //Grid View
            holder.mAlbumInListView.setVisibility(View.GONE);
            holder.mAlbumInGridView.setVisibility(View.VISIBLE);
            currentViewImageView = holder.mAlbumItemThumbnailInGridView;
            currentViewImageViewChecked = holder.mAlbumItemCheckedInGridView;
            currentViewTextView = holder.mAlbumItemFileNameInGridView;
        }

        //to refresh the view prevent duplicate bug when searching
        currentViewImageViewChecked.setVisibility(View.INVISIBLE);

        //to show selected when go back to album
        if (myFileList.get(position).getIsSelected()) {
            currentViewImageViewChecked.setVisibility(View.VISIBLE);
        }

        if (myFileList.get(position).getFileType() != null) {
            //Reset Thumbnail for Album
            currentViewImageView.setImageDrawable(null);

            if (myFileList.get(position).getFileType().equals("Image")) {
                if (!myFileList.get(position).getIsFolder()) {
                Glide.with(mContext)
                            .load(Uri.parse(myFileList.get(position).getFileUri()))
                            // Uri of the picture
                            .into(currentViewImageView);
               } else {
                   Glide.with(mContext)
                            .load(R.drawable.image_folder)
                            .into(currentViewImageView);
                }
            } else if (myFileList.get(position).getFileType().equals("Video")) {
                if (!myFileList.get(position).getIsFolder()) {
                    //to generate video thumbnial from uri
                    //
                  Glide.with(mContext)
                            .load(Uri.parse(myFileList.get(position).getFileUri()))
                            // Uri of the picture
                            .into(currentViewImageView);

                } else {
                    Glide.with(mContext)
                            .load(R.drawable.video_folder)
                            .into(currentViewImageView);
                }
            }
        }

        //check if is folder or image
        if (myFileList.get(position).getIsFolder())
            //if folder add count
            currentViewTextView.setText(myFileList.get(position).getFileName() + "(" + myFileList.get(position).getCount() + ")");
            //no folder
        else
            currentViewTextView.setText(myFileList.get(position).getFileName());

        //user select a folder then pass folder name to access inner file
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //click on folder
                if (myFileList.get(position).getIsFolder()) {
                    if (callback != null) {
                        if (myFileList.get(position).getFileType().equals("Image"))
                            callback.onFolderClicked(myFileList.get(position).getFolderID());
                        else
                            callback.onVideoFolderClicked(myFileList.get(position).getFileName());

                    }
                } else {
                    //click on file

                    //unselect
                    if (myFileList.get(position).getIsSelected()) {
                        myFileList.get(position).setIsSelected(false);
                        currentViewImageViewChecked.setVisibility(View.INVISIBLE);

                        callback.onAlbumItemUnSelected(Uri.parse(myFileList.get(position).getFileUri()));
                    } else {
                        if (mMaxFileSelection != 0) {
                            // Limit Selection
                            if (SelectionCount < mMaxFileSelection) {
                                //select
                                myFileList.get(position).setIsSelected(true);
                                currentViewImageViewChecked.setVisibility(View.VISIBLE);

                                callback.onAlbumItemSelected(Uri.parse(myFileList.get(position).getFileUri()));
                            }
                        } else {
                            //No Limit Selection
                            myFileList.get(position).setIsSelected(true);
                            currentViewImageViewChecked.setVisibility(View.VISIBLE);

                            callback.onAlbumItemSelected(Uri.parse(myFileList.get(position).getFileUri()));
                        }
                    }
                }
            }
        });

    }

    public void holderItemOnClick() {

    }

    public interface CustomFilePickerCallback {
        void onFolderClicked(int folderid);

        void onAlbumItemUnSelected(Uri uri);

        void onVideoFolderClicked(String foldername);

        void onAlbumItemSelected(Uri uri);
    }

    @Override
    public int getItemCount() {
        return myFileList == null ? 0 : myFileList.size();
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
        //File Name
        private TextView mAlbumItemFileNameInGridView;
        private TextView mAlbumItemFileNameInListView;

        //Thumbnail
        private ImageView mAlbumItemThumbnailInGridView;
        private ImageView mAlbumItemThumbnailInListView;

        //Checkbox
        private ImageView mAlbumItemCheckedInGridView;
        private ImageView mAlbumItemCheckedInListView;

        //Layout
        private LinearLayout mAlbumInGridView;
        private RelativeLayout mAlbumInListView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mAlbumItemFileNameInGridView = itemView.findViewById(R.id.album_item_file_name_in_grid_view);
            mAlbumItemFileNameInListView = itemView.findViewById(R.id.album_item_file_name_in_list_view);

            mAlbumItemThumbnailInGridView = itemView.findViewById(R.id.album_item_thumbnail_in_grid_view);
            mAlbumItemThumbnailInListView = itemView.findViewById(R.id.album_item_thumbnail_in_list_view);

            mAlbumItemCheckedInGridView = itemView.findViewById(R.id.album_item_checked_in_grid_view);
            mAlbumItemCheckedInListView = itemView.findViewById(R.id.album_item_checked_in_list_view);

            mAlbumInGridView = itemView.findViewById(R.id.grid_view_album_linear_layout);
            mAlbumInListView = itemView.findViewById(R.id.list_view_album_relative_layout);
        }
    }

    //update the selection count from picker so to limit user selection
    public void setSelectionCount(int count) {
        this.SelectionCount = count;
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

    public void setMaxFileSelection(int maxFileSelection) {
        //Set the maximum file that the users could select
        this.mMaxFileSelection = maxFileSelection;
    }

    public void setCurrentView(int view) {
        this.mCurrentView = view;
    }
}
