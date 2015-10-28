package com.example.meshidzekv.popular_movies_2;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.example.meshidzekv.popular_movies_2.data.MovieContract.MovieEntry;
import com.example.meshidzekv.popular_movies_2.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailedActivity extends AppCompatActivity implements DetailedFragment.OnMovieDeletedListener{


    public static final String MOVIE_KEY = "movie_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Intent Movie_Intent;
            Movie_Intent = getIntent();

            ContentValues MovieValues = new ContentValues();
            String Movie_Name = Movie_Intent.getStringExtra(MainActivity.EXTRA_MOVIE_NAME);
            String Movie_Backdrop_Path = Movie_Intent.getStringExtra(MainActivity.EXTRA_BACKDROP);
            String Movie_Poster_Path = Movie_Intent.getStringExtra(MainActivity.EXTRA_POSTER);
            String Movie_Synopsis = Movie_Intent.getStringExtra(MainActivity.EXTRA_SYNOPSIS);
            String Movie_Release = Movie_Intent.getStringExtra(MainActivity.EXTRA_RELEASE);
            String Movie_Score = Movie_Intent.getStringExtra(MainActivity.EXTRA_SCORE);
            int Movie_id = Movie_Intent.getIntExtra(MainActivity.EXTRA_MOVIE_ID, 0);
            ArrayList<String> Movie_Trailers = Movie_Intent.getStringArrayListExtra(MainActivity.EXTRA_TRAILERS);
            ArrayList<String> Movie_Trailers_Name = Movie_Intent.getStringArrayListExtra(MainActivity.EXTRA_TRAILERS_NAME);
            ArrayList<String> Movie_Reviews = Movie_Intent.getStringArrayListExtra(MainActivity.EXTRA_REVIEW_CONTENT);
            ArrayList<String> Movie_Review_Authors = Movie_Intent.getStringArrayListExtra(MainActivity.EXTRA_REVIEW_AUTHOR);

            Bundle arguments = new Bundle();

            arguments.putInt(MainActivity.EXTRA_MOVIE_ID, Movie_id);
            arguments.putString(MainActivity.EXTRA_MOVIE_NAME, Movie_Name);
            arguments.putString(MainActivity.EXTRA_BACKDROP, Movie_Backdrop_Path);
            arguments.putString(MainActivity.EXTRA_POSTER, Movie_Poster_Path);
            arguments.putString(MainActivity.EXTRA_SYNOPSIS, Movie_Synopsis);
            arguments.putString(MainActivity.EXTRA_RELEASE, Movie_Release);
            arguments.putString(MainActivity.EXTRA_SCORE, Movie_Score);
            arguments.putStringArrayList(MainActivity.EXTRA_TRAILERS, Movie_Trailers);
            arguments.putStringArrayList(MainActivity.EXTRA_TRAILERS_NAME, Movie_Trailers_Name);
            arguments.putStringArrayList(MainActivity.EXTRA_REVIEW_CONTENT, Movie_Reviews);
            arguments.putStringArrayList(MainActivity.EXTRA_REVIEW_AUTHOR, Movie_Review_Authors);


            DetailedFragment fragment = new DetailedFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void OnMovieDeleted(int screenSize) {

        Bundle args = new Bundle();
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        getSupportFragmentManager().beginTransaction().remove(fragmentList.get(0)).commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}


