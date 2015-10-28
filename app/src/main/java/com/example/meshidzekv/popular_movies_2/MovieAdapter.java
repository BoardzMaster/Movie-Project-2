package com.example.meshidzekv.popular_movies_2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;

/**
 * Created by MeshidzeKV on 05.08.2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie_thumbnail> {

    /**
     * This is my own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data i want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param thumbnails A List of Movie_thumbnail objects to display in a list
     */
    public MovieAdapter(Context context, List<Movie_thumbnail> thumbnails  ) {
        // Here, i initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for a TextView and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, thumbnails);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the Movie_thumbnail object from the ArrayAdapter at the appropriate position

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());

        String sort_type = sharedPrefs.getString(getContext().getString(R.string.pref_sort_key), getContext().getString(R.string.pref_type_popularity));
        boolean InternetConnected = isInternetConnected(getContext());

        Movie_thumbnail thumbnail = getItem(position);
        String picture_url = getContext().getString(R.string.PICTURE_REQUEST) + thumbnail.Movie_Poster_Path;

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }

        ImageView posterView = (ImageView) convertView.findViewById(R.id.movie_imageView);

        if (InternetConnected == true) {
            Picasso.with(getContext()).load(picture_url).into(posterView);

        }
        else if (InternetConnected == false )
        {
            if(sort_type.equals(getContext().getString(R.string.pref_type_favorite)))
            {
                File Poster = loadFile(thumbnail.Movie_id, getContext().getString(R.string.Poster_image_file));
                if (Poster != null) {
                    Picasso.with(getContext()).load(Poster).into(posterView);
                }
            }
        }

        return convertView;
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
    private File loadFile(int Movie_id , String type_image) {
        boolean FileExist;
        String root = getContext().getFilesDir().toString();
        String file_name = root + getContext().getString(R.string.IMAGES_DIR)+ "/" + type_image + String.valueOf(Movie_id) + ".jpg";
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



}
