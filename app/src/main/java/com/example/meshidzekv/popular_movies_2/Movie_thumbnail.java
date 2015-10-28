package com.example.meshidzekv.popular_movies_2;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by MeshidzeKV on 05.08.2015.
 */
public class Movie_thumbnail implements Parcelable {

    final static String LOG_TAG = "Movie_thumbnail Logs";
    String Movie_Name;
    String Movie_Backdrop_Path;
    String Movie_Poster_Path;
    String Movie_Synopsis;
    String Movie_Release;
    String Movie_Score;
    ArrayList<String> Movie_Trailers;
    ArrayList<String> Movie_Trailers_Name;
    ArrayList<String> Movie_Reviews;
    ArrayList<String> Movie_Review_Author;
    int Movie_id;

    public Movie_thumbnail(String mName, String packdrop_path, String poster_path) {
        this.Movie_Name = mName;
        this.Movie_Backdrop_Path = packdrop_path;
        this.Movie_Poster_Path = poster_path;
        Movie_Trailers = new ArrayList<String>();
        Movie_Trailers_Name = new ArrayList<String>();
        Movie_Reviews = new ArrayList<String>();
        Movie_Review_Author = new ArrayList<String>();

    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Movie_Backdrop_Path);
        dest.writeString(Movie_Poster_Path);
        dest.writeString(Movie_Name);
        dest.writeString(Movie_Synopsis);
        dest.writeString(Movie_Release);
        dest.writeString(Movie_Score);
        dest.writeList(Movie_Trailers);
        dest.writeList(Movie_Trailers_Name);
        dest.writeList(Movie_Reviews);
        dest.writeList(Movie_Review_Author);
    }

    public static final Parcelable.Creator<Movie_thumbnail> CREATOR = new Parcelable.Creator<Movie_thumbnail>() {
        // распаковываем объект из Parcel
        @Override
        public Movie_thumbnail createFromParcel(Parcel in) {
            Log.d(LOG_TAG, "createFromParcel");
            return new Movie_thumbnail(in);
        }
        @Override
        public Movie_thumbnail[] newArray(int size) {
            return new Movie_thumbnail[size];
        }
    };

    private Movie_thumbnail(Parcel in) {
        Movie_Name = in.readString();
        Movie_Backdrop_Path = in.readString();
        Movie_Poster_Path = in.readString();
        Movie_Synopsis = in.readString();
        Movie_Release = in.readString();
        Movie_Score = in.readString();
        Movie_id = in.readInt();
        in.readStringList(Movie_Trailers);
        in.readStringList(Movie_Trailers_Name);
        in.readStringList(Movie_Reviews);
        in.readStringList(Movie_Review_Author);

    }


}
