package com.williamito.popularmovies;

import android.content.Context;
import android.util.Log;
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
        ImageView imageView = (ImageView) convertView;
        if (imageView == null) {
            Log.v("", "Was null");
            imageView = new ImageView(context);
        } else {
            Log.v("", "Not null");
        }
        Movie movie = getItem(position);
        String url = movie.getPosterPath();
        Log.v("", "Loading " + url);
        Picasso.with(context).setLoggingEnabled(true);
        Picasso.with(context).setIndicatorsEnabled(true);
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.sample_0)
                .error(R.drawable.sample_1)
                .into(imageView);
        Log.v("", "Picasso?");
        return imageView;
    }
}