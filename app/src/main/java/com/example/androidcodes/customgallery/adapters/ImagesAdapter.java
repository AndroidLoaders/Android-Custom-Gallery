package com.example.androidcodes.customgallery.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mehta on 6/18/2017.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.CustomImagesViewHolder>{

    /*
    * http://www.androidhive.info/2016/11/android-working-marshmallow-m-runtime-permissions/
    * marshmallow permission in android example
    * */

    @Override
    public CustomImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CustomImagesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CustomImagesViewHolder extends RecyclerView.ViewHolder{
        public CustomImagesViewHolder(View itemView) {
            super(itemView);
        }
    }
}
