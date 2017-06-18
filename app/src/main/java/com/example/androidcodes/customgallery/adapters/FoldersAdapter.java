package com.example.androidcodes.customgallery.adapters;

import android.content.Context;
import android.media.Image;
import android.os.Process;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidcodes.customgallery.R;
import com.example.androidcodes.customgallery.models.Folders;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Mehta on 6/17/2017.
 */

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.CustomFoldersView> {

    private int size;
    private Context context;
    private ArrayList<Folders> foldersList = null;

    public FoldersAdapter(Context context) {
        this.context = context;
        foldersList = new ArrayList<>();
    }

    @Override
    public CustomFoldersView onCreateViewHolder(ViewGroup parent, int viewType) {
        View customFolderView = LayoutInflater.from(context).inflate(R.layout.folders_images_list_item,
                parent, false);
        return new CustomFoldersView(customFolderView);
    }

    @Override
    public void onBindViewHolder(CustomFoldersView holder, int position) {
        try {
            Folders folders = foldersList.get(position);
            holder.tvFolderName.setText(folders.getFolderName());
            if (folders.getFolderImagePath() != null && !TextUtils.isEmpty(folders.getFolderImagePath())) {
                Picasso.with(context).load(new File(folders.getFolderImagePath())).into(holder.
                        ivAlbumImage);
            }
        } catch (Exception e) {
            System.out.println("TAG --> " + e.getMessage());
        }
    }

    public void refreshAlbumsList(ArrayList<Folders> foldersList) {
        try {
            this.foldersList.clear();
            this.foldersList.addAll(foldersList);
            notifyDataSetChanged();
        } catch (Exception e) {
            System.out.println("TAG --> " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return foldersList != null ? foldersList.size() : 0;
    }

    public class CustomFoldersView extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivAlbumImage;
        TextView tvFolderName;

        public CustomFoldersView(View itemView) {
            super(itemView);
            ivAlbumImage = (ImageView) itemView.findViewById(R.id.ivAlbumImage);
            ivAlbumImage.getLayoutParams().width=size;
            ivAlbumImage.getLayoutParams().height=size;

            tvFolderName = (TextView) itemView.findViewById(R.id.tvFolderName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public void setLayoutParams(int size) {
        this.size = size;
    }
}
