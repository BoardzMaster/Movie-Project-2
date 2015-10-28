package com.example.meshidzekv.popular_movies_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.res.Configuration;

import java.util.List;


public class MainActivity extends ActionBarActivity  implements MovieFragment.OnMovieSelectedListener, DetailedFragment.OnMovieDeletedListener {


    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane;

    public final static String EXTRA_MOVIE_ID = "com.example.meshidzekv.popular_movies_1.MOVIE_ID";
    public final static String EXTRA_MOVIE_NAME = "com.example.meshidzekv.popular_movies_1.MOVIE_NAME";
    public final static String EXTRA_BACKDROP = "com.example.meshidzekv.popular_movies_1.BACKDROP";
    public final static String EXTRA_POSTER = "com.example.meshidzekv.popular_movies_1.POSTER";
    public final static String EXTRA_SYNOPSIS = "com.example.meshidzekv.popular_movies_1.SYNOPSIS";
    public final static String EXTRA_RELEASE = "com.example.meshidzekv.popular_movies_1.RELEASE";
    public final static String EXTRA_SCORE = "com.example.meshidzekv.popular_movies_1.SCORE";
    public final static String EXTRA_TRAILERS = "com.example.meshidzekv.popular_movies_1.TRAILER";
    public final static String EXTRA_TRAILERS_NAME = "com.example.meshidzekv.popular_movies_1.TRAILER_NAME";
    public final static String EXTRA_REVIEW_AUTHOR = "com.example.meshidzekv.popular_movies_1.REVIEW_AUTHOR";
    public final static String EXTRA_REVIEW_CONTENT = "com.example.meshidzekv.popular_movies_1.REVIEW_CONTENT";

    public final static String EXTRA_UPDATE = "com.example.meshidzekv.popular_movies_1.UPDATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int orientation = this.getResources().getConfiguration().orientation;

        //if (findViewById(R.id.movie_detail_container) != null)
        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailedFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMovieSelected(Movie_thumbnail movie) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();

            args.putInt(MainActivity.EXTRA_MOVIE_ID, movie.Movie_id);
            args.putString(MainActivity.EXTRA_MOVIE_NAME, movie.Movie_Name);
            args.putString(MainActivity.EXTRA_BACKDROP, movie.Movie_Backdrop_Path);
            args.putString(MainActivity.EXTRA_POSTER, movie.Movie_Poster_Path);
            args.putString(MainActivity.EXTRA_SYNOPSIS, movie.Movie_Synopsis);
            args.putString(MainActivity.EXTRA_RELEASE, movie.Movie_Release);
            args.putString(MainActivity.EXTRA_SCORE, movie.Movie_Score);
            args.putStringArrayList(MainActivity.EXTRA_TRAILERS, movie.Movie_Trailers);
            args.putStringArrayList(MainActivity.EXTRA_TRAILERS_NAME, movie.Movie_Trailers_Name);
            args.putStringArrayList(MainActivity.EXTRA_REVIEW_CONTENT, movie.Movie_Reviews);
            args.putStringArrayList(MainActivity.EXTRA_REVIEW_AUTHOR, movie.Movie_Review_Author);


            DetailedFragment fragment = new DetailedFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailedActivity.class).
                    putExtra(EXTRA_MOVIE_NAME, movie.Movie_Name).
                    putExtra(EXTRA_BACKDROP, movie.Movie_Backdrop_Path).
                    putExtra(EXTRA_POSTER, movie.Movie_Poster_Path).
                    putExtra(EXTRA_SYNOPSIS, movie.Movie_Synopsis).
                    putExtra(EXTRA_RELEASE, movie.Movie_Release).
                    putExtra(EXTRA_SCORE, movie.Movie_Score).
                    putExtra(EXTRA_MOVIE_ID, movie.Movie_id).
                    putStringArrayListExtra(EXTRA_TRAILERS, movie.Movie_Trailers).
                    putStringArrayListExtra(EXTRA_TRAILERS_NAME, movie.Movie_Trailers_Name).
                    putStringArrayListExtra(EXTRA_REVIEW_AUTHOR, movie.Movie_Review_Author).
                    putStringArrayListExtra(EXTRA_REVIEW_CONTENT, movie.Movie_Reviews);
            startActivity(intent);
        }
    }


    @Override
    public void OnMovieDeleted(int screenSize) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            Bundle args = new Bundle();

            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            getSupportFragmentManager().beginTransaction().remove(fragmentList.get(1)).commit();

            MovieFragment movie_frag = (MovieFragment)fragmentList.get(0);
            movie_frag.updateMovie();
        }
    }

}
