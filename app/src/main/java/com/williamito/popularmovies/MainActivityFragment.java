package com.williamito.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    List<Movie> movies;
    private ImageAdapter imageAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        movies = new ArrayList<>();
        movies.add(new Movie("", "", "", "http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg", "", ""));
        imageAdapter = new ImageAdapter(getActivity(), R.layout.grid_item_poster, movies);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private ArrayList<Movie> getMoviesFromJson(String discoverMovieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String ID = "id";
            final String TITLE = "title";
            final String RELEASE_DATE = "release_date";
            final String POSTER_PATH = "poster_path";
            final String VOTE_AVERAGE = "vote_average";
            final String OVERVIEW = "overview";

            JSONObject discoverMovieJson = new JSONObject(discoverMovieJsonStr);
            JSONArray movieArray = discoverMovieJson.getJSONArray(RESULTS);


            ArrayList<Movie> result = new ArrayList<>();

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieJson = movieArray.getJSONObject(i);

                Movie movie = new Movie(movieJson.getString(ID),
                        movieJson.getString(TITLE),
                        movieJson.getString(RELEASE_DATE),
                        movieJson.getString(POSTER_PATH),
                        movieJson.getString(VOTE_AVERAGE),
                        movieJson.getString(OVERVIEW));

                result.add(movie);
            }
            return result;

        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            final String API_KEY = "KEY_HERE";
            String sortBy;

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                sortBy = "popularity.desc";
            } else {
                sortBy = params[0];
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String discoverMovieJsonStr = null;

            try {
                final String DISCOVER_MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie";
                final String PARAM_API_KEY = "api_key";
                final String PARAM_SORT_BY = "sort_by";

                Uri builtUri = Uri.parse(DISCOVER_MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(PARAM_SORT_BY, sortBy)
                        .appendQueryParameter(PARAM_API_KEY, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                discoverMovieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesFromJson(discoverMovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (result != null) {
                movies.clear();
                movies.addAll(result);
                imageAdapter.clear();
                imageAdapter.addAll(result);
                // New data is back from the server.  Hooray!
            }
        }
    }
}

