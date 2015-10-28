package com.example.meshidzekv.popular_movies_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.meshidzekv.popular_movies_2.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MovieFragment extends Fragment {
    OnMovieSelectedListener mCallback;

    private MovieAdapter movie_adapter;
    ArrayList<Movie_thumbnail> list_of_movies;
    final String API_KEY = "api_key";


    class Movie_Parameters{

        int Movie_ID;
        int List_index;

        void Movie_Parametrs() {
        }

        public int getList_index() {
            return List_index;
        }

        public int getMovie_ID() {
            return Movie_ID;
        }
    }

    public class MovieReviews{

        ArrayList<String> Movie_Reviews;
        ArrayList<String> Movie_Review_Author;
        int position;

        public  MovieReviews() {
            position = 0;

            Movie_Reviews = new ArrayList<String>();
            Movie_Reviews.clear();

            Movie_Review_Author = new ArrayList<String>();
            Movie_Review_Author.clear();

        }
    }

    public class MovieTrailers{

        ArrayList<String> Movie_Trailers;
        ArrayList<String> Movie_Trailers_Name;

        int position;

        public  MovieTrailers() {
            position = 0;

            Movie_Trailers = new ArrayList<String>();
            Movie_Trailers.clear();

            Movie_Trailers_Name = new ArrayList<String>();
            Movie_Trailers_Name.clear();

        }
    }



    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback =(OnMovieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieSelectedListener");
        }

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of movie item
     * selections.
     */
    public interface OnMovieSelectedListener {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onMovieSelected(Movie_thumbnail movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        if (savedInstanceState==null || !savedInstanceState.containsKey(getString(R.string.MOVIE_KEY)))
        {
            list_of_movies = new ArrayList<Movie_thumbnail>();
            updateMovie();
        }
        else
        {
            list_of_movies = savedInstanceState.getParcelableArrayList(getString(R.string.MOVIE_KEY));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.MOVIE_KEY), list_of_movies);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());


        movie_adapter = new MovieAdapter(getActivity(), list_of_movies);

        // Get a reference to the GridView, and attach this adapter to it.
        GridView MovieView = (GridView) rootView.findViewById(R.id.Movie_View);
        MovieView.setAdapter(movie_adapter);




        MovieView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Context con = getActivity().getApplicationContext();
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(con);
                String sort_type = sharedPrefs.getString(con.getString(R.string.pref_sort_key), con.getString(R.string.pref_type_popularity));
                boolean InternetConnected = isInternetConnected(con);

                if (InternetConnected == true) {
                    Integer movie_position = new Integer(position);
                    FetchTrailers movieTrailerTask = new FetchTrailers();
                    movieTrailerTask.execute(movie_position);
                } else if (InternetConnected == false) {
                    if (sort_type.equals(getActivity().getApplication().getString(R.string.pref_type_favorite))) {
                        mCallback.onMovieSelected(list_of_movies.get(position));
                    }
                }
            }
        });
        return rootView;

    }

    public void updateMovie() {
        FetchMovie movieTask = new FetchMovie();
        movieTask.execute();
    }

    public class FetchMovie extends AsyncTask<Void, Void, Movie_thumbnail[]> {

        private final String LOG_TAG = FetchMovie.class.getSimpleName();

        private Movie_thumbnail[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";

            final String OWM_BACKPATH = "backdrop_path";
            final String OWM_POSTER = "poster_path";
            final String OWM_MOVIE_NAME = "original_title";
            final String OWM_MOVIE_SYNOPSIS = "overview";
            final String OWM_MOVIE_RELEASE = "release_date";
            final String OWM_MOVIE_SCORE = "vote_average";
            final String OWM_MOVIE_ID = "id";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

            int movie_length = movieArray.length();
            // OWM returns movies based based on SORT BY menu's value

            /*
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_units_key),
                    getString(R.string.pref_units_metric));
            */
            Movie_thumbnail[] Movies = new Movie_thumbnail[movie_length];

            for(int i = 0; i < movie_length; i++)
            {
                Movies[i] = new Movie_thumbnail("","","");
            }

            for(int i = 0; i < movie_length; i++) {
                String poster_path;
                String backdrop_path;
                String movie_name;
                String movie_synopsis;
                String movie_release;
                Double movie_score;
                int movie_id;

                // Get the JSON object representing the movie
                JSONObject movie_item = movieArray.getJSONObject(i);

                backdrop_path = movie_item.getString(OWM_BACKPATH);
                poster_path = movie_item.getString(OWM_POSTER);
                movie_name = movie_item.getString(OWM_MOVIE_NAME);
                movie_synopsis = movie_item.getString(OWM_MOVIE_SYNOPSIS);
                movie_release = movie_item.getString(OWM_MOVIE_RELEASE);
                movie_score = movie_item.getDouble(OWM_MOVIE_SCORE);
                movie_id = movie_item.getInt(OWM_MOVIE_ID);

                Movies[i].Movie_Backdrop_Path = backdrop_path;
                Movies[i].Movie_Poster_Path = poster_path;
                Movies[i].Movie_Name = movie_name;
                Movies[i].Movie_Synopsis = movie_synopsis;
                Movies[i].Movie_Release = movie_release;
                Movies[i].Movie_Score = movie_score.toString();
                Movies[i].Movie_id = movie_id;
            }
            return Movies;

        }


        @Override
        protected  Movie_thumbnail[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            String trailerJsonStr = null;
            String reviewJsonStr = null;
            Movie_thumbnail[] Movies;

            try {

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sort_type = sharedPrefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_type_popularity));

                String sort_by = getString(R.string.POPULARITY);

                if (sort_type.equals(getString(R.string.pref_type_highest_rated))) {
                    sort_by = getString(R.string.HIGHEST_RATED);
                } else if (sort_type.equals(getString(R.string.pref_type_favorite))) {
                    Cursor cursor = getActivity().getContentResolver().query(
                            MovieContract.MovieEntry.CONTENT_URI,
                            null,   // projection
                            null,
                            null,
                            null // sort order
                    );
                    int movie_count = cursor.getCount();

                    Movies = new Movie_thumbnail[movie_count];

                    for (int i = 0; i < movie_count; i++) {
                        Movies[i] = new Movie_thumbnail("", "", "");
                    }
                    int i = 0;
                    if (cursor.moveToFirst()) {
                        do {
                            Movies[i].Movie_id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));
                            Movies[i].Movie_Name = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME));
                            Movies[i].Movie_Backdrop_Path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP));
                            Movies[i].Movie_Poster_Path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));
                            Movies[i].Movie_Release = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE));
                            Movies[i].Movie_Release = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE));
                            Movies[i].Movie_Release = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE));
                            Movies[i].Movie_Synopsis = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS));
                            Movies[i].Movie_Score = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SCORE));

                            String trailer_name_1 = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILER_1_NAME));
                            if (!trailer_name_1.isEmpty()) {
                                Movies[i].Movie_Trailers.add(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILER_1)));
                                Movies[i].Movie_Trailers_Name.add(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILER_1_NAME)));
                            }

                            String trailer_name_2 = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILER_2_NAME));
                            if (!trailer_name_2.isEmpty()) {
                                Movies[i].Movie_Trailers.add(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILER_2)));
                                Movies[i].Movie_Trailers_Name.add(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILER_2_NAME)));
                            }

                            String author = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR));
                            if (!author.isEmpty()) {
                                Movies[i].Movie_Reviews.add(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT)));
                                Movies[i].Movie_Review_Author.add(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR)));
                            }

                            i++;
                        } while (cursor.moveToNext());
                    }
                    return Movies;
                } else {
                    Log.d(LOG_TAG, "Sort type not found: " + sort_type);
                }

                final String SORT_PARAM = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(getString(R.string.MOVIE_BASE_URL)).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort_by)
                        .appendQueryParameter(API_KEY, getString(R.string.USER_KEY))
                        .build();
                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                if (isInternetConnected(getActivity().getApplicationContext()) == true) {
                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
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
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    movieJsonStr = buffer.toString();
                    try {
                        return getMovieDataFromJson(movieJsonStr);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
                else {
                    return null;
                }
            }
                catch(IOException e){
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the movie data, there's no point in attemping
                    // to parse it.
                    return null;
                }
                finally{
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
                return null;
        }



        @Override
        protected void onPostExecute(Movie_thumbnail[] result) {
            movie_adapter.clear();
            if (result != null) {
                for(Movie_thumbnail MovieItem : result) {
                    list_of_movies.add(MovieItem);

                }
            }

        }

    }


    public class FetchReviews extends AsyncTask<Integer, Void, MovieReviews> {

        private final String LOG_TAG = FetchReviews.class.getSimpleName();

        private MovieReviews getReviewDataFromJson(String movieJsonStr,MovieReviews movie_review)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            final String REVIEW_RESULTS = "results";


            final String REVIEW_CONTENT = "content";
            final String REVIEW_AUTHOR = "author";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(REVIEW_RESULTS);

            int trailer_length = movieArray.length();


            for(int i = 0; i < trailer_length; i++) {

                String review_author;
                String review_content;
                // Get the JSON object representing the movie
                JSONObject movie_item = movieArray.getJSONObject(i);
                review_author = movie_item.getString(REVIEW_AUTHOR);
                review_content = movie_item.getString(REVIEW_CONTENT);
                movie_review.Movie_Review_Author.add(review_author);
                movie_review.Movie_Reviews.add(review_content);
            }
            return movie_review;
        }

        @Override
        protected MovieReviews doInBackground(Integer... params) {

            // Will contain the raw JSON response as a string.
            String reviewJsonStr = null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            MovieFragment.MovieReviews Movie_Review;

            try {

                Uri builtUri = Uri.parse(getString(R.string.MOVIE_REVIEW_URL) + String.valueOf(movie_adapter.getItem(params[0].intValue()).Movie_id) + "/reviews" ).buildUpon()
                            .appendQueryParameter(API_KEY, getString(R.string.USER_KEY))
                            .build();
                URL url = new URL(builtUri.toString());

                Movie_Review = new MovieFragment.MovieReviews();

                Movie_Review.position = params[0].intValue();

                Log.v(LOG_TAG, "Review URI " + builtUri.toString());

                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
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
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    reviewJsonStr = buffer.toString();

                    try
                    {
                        return getReviewDataFromJson(reviewJsonStr, Movie_Review);
                    }
                    catch (JSONException e)
                    {
                        Log.e(LOG_TAG + " Parse Reviews", e.getMessage(), e);
                        e.printStackTrace();
                    }
            }
             catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            }

            finally {
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
            return null;
        }

        @Override
        protected void onPostExecute(MovieReviews movie_reviews) {

            if (movie_reviews != null) {
                int position = movie_reviews.position;
                list_of_movies.get(position).Movie_Reviews = movie_reviews.Movie_Reviews;
                list_of_movies.get(position).Movie_Review_Author = movie_reviews.Movie_Review_Author;
            }
            mCallback.onMovieSelected(list_of_movies.get(movie_reviews.position));
        }
    }

    public class FetchTrailers extends AsyncTask<Integer, Void, MovieTrailers> {

        private final String LOG_TAG = FetchTrailers.class.getSimpleName();

        private MovieTrailers getTrailerDataFromJson(String movieJsonStr,MovieTrailers movie)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TRAILER_RESULTS = "results";

            final String TRAILER_KEY = "key";
            final String TRAILER_NAME = "name";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TRAILER_RESULTS);

            int trailer_length = movieArray.length();


            for(int i = 0; i < trailer_length; i++) {

                String trailer_key;
                String trailer_name;
                // Get the JSON object representing the movie
                JSONObject movie_item = movieArray.getJSONObject(i);
                trailer_key = movie_item.getString(TRAILER_KEY);
                trailer_name = movie_item.getString(TRAILER_NAME);
                movie.Movie_Trailers.add(trailer_key);
                movie.Movie_Trailers_Name.add(trailer_name);
            }
            return movie;
        }

        @Override
        protected MovieTrailers doInBackground(Integer... params) {

            // Will contain the raw JSON response as a string.
            String trailerJsonStr = null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            MovieFragment.MovieTrailers Movie_Trailers;

            try {

                Uri builtUri = Uri.parse(getString(R.string.MOVIE_TRAILER_URL) +String.valueOf(movie_adapter.getItem(params[0].intValue()).Movie_id) + "/videos").buildUpon()
                        .appendQueryParameter(API_KEY, getString(R.string.USER_KEY))
                        .build();
                URL url = new URL(builtUri.toString());

                Movie_Trailers = new MovieFragment.MovieTrailers();

                Movie_Trailers.position = params[0].intValue();

                Log.v(LOG_TAG, "Review URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
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
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                trailerJsonStr = buffer.toString();

                try
                {
                    return getTrailerDataFromJson(trailerJsonStr, Movie_Trailers);
                }
                catch (JSONException e)
                {
                    Log.e(LOG_TAG + " Parse Reviews", e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            }

            finally {
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
            return null;
        }

        @Override
        protected void onPostExecute(MovieTrailers movie_trailers) {

            if (movie_trailers != null) {
                int position = movie_trailers.position;
                list_of_movies.get(position).Movie_Trailers = movie_trailers.Movie_Trailers;
                list_of_movies.get(position).Movie_Trailers_Name = movie_trailers.Movie_Trailers_Name;
            }
            FetchReviews movieReviewTask = new FetchReviews();
            movieReviewTask.execute(movie_trailers.position);
        }


    }

    private   boolean isInternetConnected(Context ct)
    {
        boolean connected = false;
        //get the connectivity manager object to identify the network state.
        ConnectivityManager connectivityManager = (ConnectivityManager)ct.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Check if the manager object is NULL, this check is required. to prevent crashes in few devices.
        if(connectivityManager != null)
        {
            //Check Mobile data or Wifi net is present
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            {
                //we are connected to a network
                connected = true;
            }
            else
            {
                connected = false;
            }
            return connected;
        }
        else
        {
            return false;
        }
    }

}
