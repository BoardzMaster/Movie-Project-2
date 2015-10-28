package com.example.meshidzekv.popular_movies_2;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import com.example.meshidzekv.popular_movies_2.data.MovieContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class DetailedFragment extends Fragment{

    OnMovieDeletedListener mCallback;

    final String YOUTUBE_VIDEO = "v";

    Movie_thumbnail MovieItem;

    private final String LOG_TAG = DetailedActivity.class.getSimpleName();

    private ShareActionProvider mShareActionProvider;

    public DetailedFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMovieDeletedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieDeletedListener");
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of movie item
     * selections.
     */
    public interface OnMovieDeletedListener {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void OnMovieDeleted(int screenSize);
    }


    @Override
      public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          // Add this line in order for this fragment to handle menu events.
          if (savedInstanceState==null || !savedInstanceState.containsKey(getString(R.string.MOVIE_KEY)))
          {
              MovieItem = new Movie_thumbnail("","","");
          }
          else
          {
              MovieItem = savedInstanceState.getParcelable(getString(R.string.MOVIE_KEY));
          }

      }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        Bundle arguments = getArguments();
        View rootView = inflater.inflate(R.layout.fragment_detailed, container, false);
        if (arguments != null) {

            MovieItem.Movie_id = arguments.getInt(MainActivity.EXTRA_MOVIE_ID);
            MovieItem.Movie_Name =  arguments.getString(MainActivity.EXTRA_MOVIE_NAME);
            MovieItem.Movie_Backdrop_Path = arguments.getString(MainActivity.EXTRA_BACKDROP);
            MovieItem.Movie_Poster_Path = arguments.getString(MainActivity.EXTRA_POSTER);
            MovieItem.Movie_Synopsis = arguments.getString(MainActivity.EXTRA_SYNOPSIS);
            MovieItem.Movie_Release = arguments.getString(MainActivity.EXTRA_RELEASE);
            MovieItem.Movie_Score = arguments.getString(MainActivity.EXTRA_SCORE);
            MovieItem.Movie_Trailers = arguments.getStringArrayList(MainActivity.EXTRA_TRAILERS);
            MovieItem.Movie_Trailers_Name = arguments.getStringArrayList(MainActivity.EXTRA_TRAILERS_NAME);
            MovieItem.Movie_Reviews = arguments.getStringArrayList(MainActivity.EXTRA_REVIEW_CONTENT);
            MovieItem.Movie_Review_Author = arguments.getStringArrayList(MainActivity.EXTRA_REVIEW_AUTHOR);

            int orientation = this.getResources().getConfiguration().orientation;

            Application frag_application = getActivity().getApplication();

            final ContentValues MovieValues = new ContentValues();

            int trailer_size;
            int review_size;

            boolean InternetConnected = isInternetConnected(getActivity().getApplicationContext());

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {


                ImageView posterView = (ImageView) rootView.findViewById(R.id.Poster_View);
                ImageView backdropView = (ImageView) rootView.findViewById(R.id.BackdropView);
                ImageView starView = (ImageView) rootView.findViewById(R.id.StarView);

                TextView titleView = (TextView) rootView.findViewById(R.id.Title_textView);
                TextView scoreView = (TextView) rootView.findViewById(R.id.VoteAverage_textView);
                TextView releaseView = (TextView) rootView.findViewById(R.id.Release_textView);
                TextView synopsisView = (TextView) rootView.findViewById(R.id.Synopsis_textView);
                TextView trailer_1 = (TextView) rootView.findViewById(R.id.Trail_1);
                TextView trailer_2 = (TextView) rootView.findViewById(R.id.Trail_2);
                TextView Review_content = (TextView) rootView.findViewById(R.id.Review_Content);
                TextView Review_Author = (TextView) rootView.findViewById(R.id.Review_Author);

                final ImageButton AddToFavorite = (ImageButton) rootView.findViewById(R.id.FavoriteButton);
                final TextView Favorite = (TextView) rootView.findViewById(R.id.Favorite);

                synopsisView.setMovementMethod(new ScrollingMovementMethod());

                RatingBar rating = (RatingBar) rootView.findViewById(R.id.RatingBar);
                rating.setMax(10);
                rating.setNumStars(5);
                rating.setStepSize(1);
                rating.setIsIndicator(true);

                String picture_backdrop_url = getString(R.string.PICTURE_REQUEST) + MovieItem.Movie_Backdrop_Path;
                String picture_poster_url = getString(R.string.PICTURE_REQUEST) + MovieItem.Movie_Poster_Path;

                if (InternetConnected == true)
               {
                   Picasso.with(frag_application).load(picture_backdrop_url).into(backdropView);
                   Picasso.with(frag_application).load(picture_poster_url).into(posterView);
               }
               else
               {
                   File Backdrop = loadFile(MovieItem.Movie_id, getString(R.string.Backdrop_image_file));
                   if (Backdrop != null){
                        Picasso.with(frag_application).load(Backdrop).into(backdropView);
                   }
                   File Poster = loadFile(MovieItem.Movie_id, getString(R.string.Poster_image_file));
                   if (Poster != null) {
                       Picasso.with(frag_application).load(Poster).into(posterView);
                   }
               }
                starView.setImageResource(R.drawable.goldstar);

                titleView.setText(MovieItem.Movie_Name);
                scoreView.setText(MovieItem.Movie_Score);
                releaseView.setText(MovieItem.Movie_Release);
                synopsisView.setText(MovieItem.Movie_Synopsis);
                rating.setRating(Float.valueOf(MovieItem.Movie_Score) / (10 / 5));

                ImageButton Trailer_1_button = (ImageButton) rootView.findViewById(R.id.PlayButton_Trailer_1);
                ImageButton Trailer_2_button = (ImageButton) rootView.findViewById(R.id.PlayButton_Trailer_2);

                review_size = MovieItem.Movie_Reviews.size();

                if (review_size == 0) {
                    Review_Author.setText("No author");
                    Review_content.setText("No review");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR, "");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT, "");
                } else {
                    Review_Author.setText(MovieItem.Movie_Review_Author.get(0));
                    Review_content.setText(MovieItem.Movie_Reviews.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR, MovieItem.Movie_Review_Author.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT, MovieItem.Movie_Reviews.get(0));
                }

                Trailer_1_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri youtube_uri = Uri.parse(getString(R.string.YOUTUBE_WATCH_URL)).buildUpon()
                                .appendQueryParameter(YOUTUBE_VIDEO, MovieItem.Movie_Trailers.get(0))
                                .build();
                        Intent openlink = new Intent(Intent.ACTION_VIEW, youtube_uri);
                        startActivity(openlink);
                    }
                });

                Trailer_2_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri youtube_uri = Uri.parse(getString(R.string.YOUTUBE_WATCH_URL)).buildUpon()
                                .appendQueryParameter(YOUTUBE_VIDEO, MovieItem.Movie_Trailers.get(1))
                                .build();
                        Intent openlink = new Intent(Intent.ACTION_VIEW, youtube_uri);
                        startActivity(openlink);
                    }
                });

                trailer_size = MovieItem.Movie_Trailers_Name.size();

                if (trailer_size == 0) {
                    Trailer_1_button.setEnabled(false);
                    Trailer_2_button.setEnabled(false);
                    trailer_1.setText("No trailer");
                    trailer_2.setText("No trailer");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1, "");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1_NAME, "");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2, "");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2_NAME, "");
                } else if (trailer_size == 1) {
                    Trailer_1_button.setEnabled(true);
                    trailer_1.setText(MovieItem.Movie_Trailers_Name.get(0));
                    Trailer_2_button.setEnabled(false);
                    trailer_2.setText("No trailer");

                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1, MovieItem.Movie_Trailers.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1_NAME, MovieItem.Movie_Trailers_Name.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2, "");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2_NAME, "");
                } else if (trailer_size > 1) {
                    Trailer_1_button.setEnabled(true);
                    trailer_1.setText(MovieItem.Movie_Trailers_Name.get(0));

                    Trailer_2_button.setEnabled(true);
                    trailer_2.setText(MovieItem.Movie_Trailers_Name.get(1));

                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1, MovieItem.Movie_Trailers.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1_NAME, MovieItem.Movie_Trailers_Name.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2, MovieItem.Movie_Trailers.get(1));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2_NAME, MovieItem.Movie_Trailers_Name.get(1));
                }

                MovieValues.put(MovieContract.MovieEntry.COLUMN_ID, MovieItem.Movie_id);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_NAME, MovieItem.Movie_Name);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP, MovieItem.Movie_Backdrop_Path);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, MovieItem.Movie_Poster_Path);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, MovieItem.Movie_Synopsis);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, MovieItem.Movie_Release);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_SCORE, MovieItem.Movie_Score);


                // Test if the movie have been added to the favorite list
                // query the particular column COLUMN_ID
                Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,   // projection
                        MovieContract.MovieEntry.COLUMN_ID + " = ?",
                        new String[]{Integer.toString(MovieItem.Movie_id)},
                        null // sort order
                );
                int cursor_count = cursor.getCount();
                cursor.close();

                if (cursor_count == 0) {
                    Favorite.setText(R.string.activity_detailed_favorite_add);
                    AddToFavorite.setImageResource(R.drawable.ic_favorite_border_black_36dp);
                } else {
                    Favorite.setText(R.string.activity_detailed_favorite_added);
                    AddToFavorite.setImageResource(R.drawable.ic_favorite_black_36dp);
                }

                AddToFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                                MovieContract.MovieEntry.CONTENT_URI,
                                null,   // projection
                                MovieContract.MovieEntry.COLUMN_ID + " = ?",
                                new String[]{Integer.toString(MovieItem.Movie_id)},
                                null // sort order
                        );
                        int cursor_count = cursor.getCount();
                        cursor.close();

                        Resources resources = getResources();
                        Configuration config = resources.getConfiguration();
                        int screen_size = config.screenWidthDp;

                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                        String sort_type = sharedPrefs.getString(getActivity().getApplicationContext().getString(R.string.pref_sort_key), getActivity().getApplicationContext().getString(R.string.pref_type_popularity));

                        if (cursor_count == 0) {
                            Uri movieInsertUri = getActivity().getApplicationContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, MovieValues);
                            Favorite.setText(R.string.activity_detailed_favorite_added);
                            AddToFavorite.setImageResource(R.drawable.ic_favorite_black_36dp);
                            SaveBackdropView(MovieItem.Movie_Backdrop_Path, MovieItem.Movie_id);
                            SavePoster(MovieItem.Movie_Poster_Path, MovieItem.Movie_id);
                        } else {
                            int delete_count = getActivity().getApplicationContext().getContentResolver().delete(
                                    MovieContract.MovieEntry.CONTENT_URI,
                                    MovieContract.MovieEntry.COLUMN_ID + " = ?",
                                    new String[]{Integer.toString(MovieItem.Movie_id)}
                            );
                            Favorite.setText(R.string.activity_detailed_favorite_add);
                            AddToFavorite.setImageResource(R.drawable.ic_favorite_border_black_36dp);
                            deleteFile(MovieItem.Movie_id,getString(R.string.Backdrop_image_file));
                            deleteFile(MovieItem.Movie_id, getString(R.string.Poster_image_file));

                            if(sort_type.equals(getActivity().getApplicationContext().getString(R.string.pref_type_favorite)))
                            {
                                mCallback.OnMovieDeleted(screen_size);
                            }
                        }

                    }
                });
            } else {

                Resources resources = getResources();
                Configuration config = resources.getConfiguration();
                int screen_size = config.screenWidthDp;

                ImageView starView = (ImageView) rootView.findViewById(R.id.StarView);
                TextView titleView = (TextView) rootView.findViewById(R.id.Title_textView);
                TextView scoreView = (TextView) rootView.findViewById(R.id.VoteAverage_textView);
                TextView releaseView = (TextView) rootView.findViewById(R.id.Release_textView);
                TextView synopsisView = (TextView) rootView.findViewById(R.id.Synopsis_textView);
                TextView trailer_1 = (TextView) rootView.findViewById(R.id.Trail_1);
                TextView trailer_2 = (TextView) rootView.findViewById(R.id.Trail_2);
                TextView Review_Content = (TextView) rootView.findViewById(R.id.Review_Content);
                TextView Review_Author = (TextView) rootView.findViewById(R.id.Review_Author);

                synopsisView.setMovementMethod(new ScrollingMovementMethod());

                RatingBar rating = (RatingBar) rootView.findViewById(R.id.RatingBar);
                rating.setMax(10);
                rating.setNumStars(5);
                rating.setStepSize(1);
                rating.setIsIndicator(true);

                String picture_backdrop_url = getString(R.string.PICTURE_REQUEST) + MovieItem.Movie_Backdrop_Path;
                String picture_poster_url = getString(R.string.PICTURE_REQUEST) + MovieItem.Movie_Poster_Path;

                if (screen_size >= 600)
                {
                    ImageView posterView = (ImageView) rootView.findViewById(R.id.Poster_View);
                    ImageView backdropView = (ImageView) rootView.findViewById(R.id.BackdropView);


                    if (InternetConnected == true)
                    {
                        Picasso.with(frag_application).load(picture_backdrop_url).into(backdropView);
                        Picasso.with(frag_application).load(picture_poster_url).into(posterView);
                    }
                    else {
                        File Backdrop = loadFile(MovieItem.Movie_id, getString(R.string.Backdrop_image_file));
                        if (Backdrop != null) {
                            Picasso.with(frag_application).load(Backdrop).into(backdropView);
                        }
                        File Poster = loadFile(MovieItem.Movie_id, getString(R.string.Poster_image_file));
                        if (Poster != null) {
                            Picasso.with(frag_application).load(Poster).into(posterView);
                        }
                    }

                }
                else
                {
                    ImageView backdropView = (ImageView) rootView.findViewById(R.id.Poster_View);
                    if (InternetConnected == true)
                    {

                        Picasso.with(frag_application).load(picture_backdrop_url).into(backdropView);
                    }
                    else
                    {
                        File Backdrop = loadFile(MovieItem.Movie_id, getString(R.string.Backdrop_image_file));
                        if (Backdrop != null)
                        {
                        Picasso.with(frag_application).load(Backdrop).into(backdropView);
                        }
                    }
                }

                starView.setImageResource(R.drawable.goldstar);

                titleView.setText(MovieItem.Movie_Name);
                scoreView.setText(MovieItem.Movie_Score);
                releaseView.setText(MovieItem.Movie_Release);
                synopsisView.setText(MovieItem.Movie_Synopsis);
                rating.setRating(Float.valueOf(MovieItem.Movie_Score) / (10 / 5));

                ImageButton Trailer_1_button = (ImageButton) rootView.findViewById(R.id.PlayButton_Trailer_1);
                ImageButton Trailer_2_button = (ImageButton) rootView.findViewById(R.id.PlayButton_Trailer_2);

                final ImageButton AddToFavorite = (ImageButton) rootView.findViewById(R.id.FavoriteButton);
                final TextView Favorite = (TextView) rootView.findViewById(R.id.Favorite);

                Trailer_1_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri youtube_uri = Uri.parse(getString(R.string.YOUTUBE_WATCH_URL)).buildUpon()
                                .appendQueryParameter(YOUTUBE_VIDEO, MovieItem.Movie_Trailers.get(0))
                                .build();
                        Intent openlink = new Intent(Intent.ACTION_VIEW, youtube_uri);
                        startActivity(openlink);
                    }
                });

                Trailer_2_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri youtube_uri = Uri.parse(getString(R.string.YOUTUBE_WATCH_URL)).buildUpon()
                                .appendQueryParameter(YOUTUBE_VIDEO, MovieItem.Movie_Trailers.get(1))
                                .build();
                        Intent openlink = new Intent(Intent.ACTION_VIEW, youtube_uri);
                        startActivity(openlink);
                    }
                });

                review_size = MovieItem.Movie_Reviews.size();

                if (review_size == 0) {
                    Review_Author.setText("No author");
                    Review_Content.setText("No review");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR, "");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT, "");
                } else {
                    Review_Author.setText(MovieItem.Movie_Review_Author.get(0));
                    Review_Content.setText(MovieItem.Movie_Reviews.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR, MovieItem.Movie_Review_Author.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT, MovieItem.Movie_Reviews.get(0));
                }

                trailer_size = MovieItem.Movie_Trailers_Name.size();

                if (trailer_size == 0) {
                    Trailer_1_button.setEnabled(false);
                    Trailer_2_button.setEnabled(false);
                    trailer_1.setText("No trailer");
                    trailer_2.setText("No trailer");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1, "");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1_NAME, "");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2, "");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2_NAME, "");
                } else if (trailer_size == 1) {
                    Trailer_1_button.setEnabled(true);
                    trailer_1.setText(MovieItem.Movie_Trailers_Name.get(0));
                    Trailer_2_button.setEnabled(false);
                    trailer_2.setText("No trailer");

                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1, MovieItem.Movie_Trailers.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1_NAME, MovieItem.Movie_Trailers_Name.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2, "");
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2_NAME, "");
                } else if (trailer_size > 1) {
                    Trailer_1_button.setEnabled(true);
                    trailer_1.setText(MovieItem.Movie_Trailers_Name.get(0));

                    Trailer_2_button.setEnabled(true);
                    trailer_2.setText(MovieItem.Movie_Trailers_Name.get(1));

                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1, MovieItem.Movie_Trailers.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_1_NAME, MovieItem.Movie_Trailers_Name.get(0));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2, MovieItem.Movie_Trailers.get(1));
                    MovieValues.put(MovieContract.MovieEntry.COLUMN_TRAILER_2_NAME, MovieItem.Movie_Trailers_Name.get(1));
                }

                MovieValues.put(MovieContract.MovieEntry.COLUMN_ID, MovieItem.Movie_id);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_NAME, MovieItem.Movie_Name);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP, MovieItem.Movie_Backdrop_Path);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, MovieItem.Movie_Poster_Path);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, MovieItem.Movie_Synopsis);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, MovieItem.Movie_Release);
                MovieValues.put(MovieContract.MovieEntry.COLUMN_SCORE, MovieItem.Movie_Score);


                // Test if the movie have been added to the favorite list
                // query the particular column COLUMN_ID
                Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,   // projection
                        MovieContract.MovieEntry.COLUMN_ID + " = ?",
                        new String[]{Integer.toString(MovieItem.Movie_id)},
                        null // sort order
                );
                int cursor_count = cursor.getCount();
                cursor.close();

                if (cursor_count == 0) {
                    Favorite.setText(R.string.activity_detailed_favorite_add);
                    AddToFavorite.setImageResource(R.drawable.ic_favorite_border_black_36dp);
                } else {
                    Favorite.setText(R.string.activity_detailed_favorite_added);
                    AddToFavorite.setImageResource(R.drawable.ic_favorite_black_36dp);
                }

                AddToFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                                MovieContract.MovieEntry.CONTENT_URI,
                                null,   // projection
                                MovieContract.MovieEntry.COLUMN_ID + " = ?",
                                new String[]{Integer.toString(MovieItem.Movie_id)},
                                null // sort order
                        );
                        int cursor_count = cursor.getCount();
                        cursor.close();

                        Resources resources = getResources();
                        Configuration config = resources.getConfiguration();
                        int screen_size = config.screenWidthDp;

                        boolean InternetConnected = isInternetConnected(getActivity().getApplicationContext());

                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                        String sort_type = sharedPrefs.getString(getActivity().getApplicationContext().getString(R.string.pref_sort_key), getActivity().getApplicationContext().getString(R.string.pref_type_popularity));

                        if (cursor_count == 0) {
                            Uri movieInsertUri = getActivity().getApplicationContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, MovieValues);
                            Favorite.setText(R.string.activity_detailed_favorite_added);
                            AddToFavorite.setImageResource(R.drawable.ic_favorite_black_36dp);
                            SaveBackdropView(MovieItem.Movie_Backdrop_Path, MovieItem.Movie_id);
                            SavePoster(MovieItem.Movie_Poster_Path, MovieItem.Movie_id);

                        } else {
                            int delete_count = getActivity().getApplicationContext().getContentResolver().delete(
                                    MovieContract.MovieEntry.CONTENT_URI,
                                    MovieContract.MovieEntry.COLUMN_ID + " = ?",
                                    new String[]{Integer.toString(MovieItem.Movie_id)}
                            );
                            Favorite.setText(R.string.activity_detailed_favorite_add);
                            AddToFavorite.setImageResource(R.drawable.ic_favorite_border_black_36dp);
                            deleteFile(MovieItem.Movie_id, getString(R.string.Backdrop_image_file));
                            deleteFile(MovieItem.Movie_id, getString(R.string.Poster_image_file));


                            if(sort_type.equals(getActivity().getApplicationContext().getString(R.string.pref_type_favorite))){
                                    mCallback.OnMovieDeleted(screen_size);
                            }

                        }

                    }
                });

            }

        }else
        {
            rootView.setVisibility(rootView.INVISIBLE);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detailedfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if(MovieItem != null) {
            if (MovieItem.Movie_Trailers_Name.size() != 0) {
                mShareActionProvider.setShareIntent(createShareMovieTrailerIntent());
            }
        }
    }

    private Intent createShareMovieTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri youtube_uri = Uri.parse(getString(R.string.YOUTUBE_WATCH_URL)).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO, MovieItem.Movie_Trailers.get(0))
                .build();
        String youtube_url = youtube_uri.toString();

        // Movie hashtag without whitespaces
        String MOVIE_HASHTAG = " #" +   MovieItem.Movie_Name.replaceAll("\\s+", "") + " trailer";

        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, youtube_url + MOVIE_HASHTAG);
        return shareIntent;
    }

    // Function saves a backdrop image into a file in the internal memory
    private  void SaveBackdropView ( String BackDropPath, final int Movie_Id){

        String picture_backdrop_url = getString(R.string.PICTURE_REQUEST) + BackDropPath;
        Picasso.with(getActivity().getApplication()).load(picture_backdrop_url).into(new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream out = null;
                        String file_name;
                        File MovieImageDir;
                        try {
                            String root = getActivity().getApplicationContext().getFilesDir().toString();
                            MovieImageDir = new File(root + getString(R.string.IMAGES_DIR));
                            if (!MovieImageDir.exists()) {
                                MovieImageDir.mkdirs();
                            }
                            file_name = getString(R.string.Backdrop_image_file) + String.valueOf(Movie_Id) + ".jpg";
                        } catch (Exception e) {
                            Log.e(LOG_TAG, "create dir error!", e);
                            return;
                        }
                        try {
                            MovieImageDir = new File(MovieImageDir, file_name);
                            out = new FileOutputStream(MovieImageDir);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                        } catch (FileNotFoundException e) {
                            Log.e(LOG_TAG, "io error!", e);
                        } finally {
                            if (out != null) {
                                try {
                                    out.close();
                                } catch (IOException e) {
                                    Log.e(LOG_TAG, "io closing error!", e);
                                }
                            }
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });


    }

    // Function saves a poster image into a file in the internal memory
    private void SavePoster (String PosterPath, final int Movie_Id){
        String picture_poster_url = getString(R.string.PICTURE_REQUEST) + PosterPath;
        Picasso.with(getActivity().getApplication()).load(picture_poster_url).into(new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream out = null;
                        String file_name;
                        File MovieImageDir;
                        try {
                            String root = getActivity().getApplicationContext().getFilesDir().toString();
                            MovieImageDir = new File(root + getString(R.string.IMAGES_DIR));
                            if (!MovieImageDir.exists()) {
                                MovieImageDir.mkdirs();
                            }
                            file_name = getString(R.string.Poster_image_file) + String.valueOf(Movie_Id) + ".jpg";
                        } catch (Exception e)
                        {
                            Log.e(LOG_TAG, "create dir error!", e);
                            return;
                        }
                        try {
                            MovieImageDir = new File(MovieImageDir, file_name);
                            out = new FileOutputStream(MovieImageDir);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        } catch (FileNotFoundException e) {
                            Log.e(LOG_TAG, "io error!", e);
                        } finally {
                            if (out != null) {
                                try {
                                    out.close();
                                } catch (IOException e) {
                                    Log.e(LOG_TAG, "io closing error!", e);
                                }
                            }
                        }
                    }
                }).start();
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });

    }

    // Function checks the Internet connection
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

    // Function loads a file from the internal memory
   private File loadFile(int Movie_id , String type_image) {
       boolean FileExist;
       String root = getActivity().getApplicationContext().getFilesDir().toString();
       String file_name = root + getString(R.string.IMAGES_DIR) + "/" + type_image + String.valueOf(Movie_id) + ".jpg";
       File MovieImageDir = new File(file_name);
       FileExist = MovieImageDir.exists();
       if (MovieImageDir.exists())
       {
           return MovieImageDir;
       }
       else
       {
           return null;
       }
   }

    // Function deletes a file from the internal memory
    private void deleteFile(int Movie_id, String type_image)
    {
        File FileToDelete = loadFile(Movie_id,type_image);
        if (FileToDelete != null)
        {
            FileToDelete.delete();
        }
    }

}



