package com.example.meshidzekv.popular_movies_2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import java.util.ArrayList;

/**
 * Created by MeshidzeKV on 12.10.2015.
 */
public class MovieContract {

        // The "Content authority" is a name for the entire content provider, similar to the
        // relationship between a domain name and its website.  A convenient string to use for the
        // content authority is the package name for the app, which is guaranteed to be unique on the
        // device.
        public static final String CONTENT_AUTHORITY = "com.example.meshidzekv.popular_movies_2";

        // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
        // the content provider.
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        // Possible paths (appended to base content URI for possible URI's)
        // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
        // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
        // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
        // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
        public static final String PATH_MOVIE = "movies";


    /* Inner class that defines the table contents of the movie table */
        public static final class MovieEntry implements BaseColumns {

            public static final String TABLE_NAME = "movie";

            // Movie name
            public static final String COLUMN_NAME = "Movie_Name";
            // Movie name
            public static final String COLUMN_ID = "Movie_ID";
            // Movie Backdrop
            public static final String COLUMN_MOVIE_BACKDROP = "Movie_Backdrop_Path";
            // Movie Poster
            public static final String COLUMN_MOVIE_POSTER = "Movie_Poster_Path";
            // Movie Synopsis
            public static final String COLUMN_SYNOPSIS = "Movie_Synopsis";
            // Movie Release
            public static final String COLUMN_RELEASE = "Movie_Release";
            // Movie Score
            public static final String COLUMN_SCORE = "Movie_Score";

            // Movie Trailer 1
            public static final String COLUMN_TRAILER_1 = "Movie_Trailer_1";
            // Movie Trailer 2
            public static final String COLUMN_TRAILER_2 = "Movie_Trailer_2";
            // Movie Trailer 1 Name
            public static final String COLUMN_TRAILER_1_NAME = "Movie_Trailer_1_Name";
            // Movie Trailer 2 Name
            public static final String COLUMN_TRAILER_2_NAME = "Movie_Trailer_2_Name";
            // Movie Review
            public static final String COLUMN_REVIEW_CONTENT = "Movie_Review_Content";
            // Movie Review Author
            public static final String COLUMN_REVIEW_AUTHOR = "Review_Author";


            public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

            public static final String CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
            public static final String CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


            public static Uri buildMovieUri(long id) {
                return ContentUris.withAppendedId(CONTENT_URI, id);
            }
        }
    }
