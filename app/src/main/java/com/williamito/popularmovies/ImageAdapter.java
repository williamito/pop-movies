package com.williamito.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<Movie> {
    private Context context;

    public ImageAdapter(Context context, int resourceId, List<Movie> movies) {
        super(context, resourceId, resourceId, movies);
        this.context = context;
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.grid_item_poster, null);
        }
        Movie movie = getItem(position);
        String url = movie.getPosterPath();
        ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item_poster_imageview);
        Picasso.with(context)
                .load(url)
                .into(imageView);
        return convertView;
    }
}