package com.example.android.movieapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.movieapp.MoviesDB.MoviesContract;
import com.example.android.movieapp.MoviesDB.MoviesDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements RVMainScreenAdapter.RVItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {


    RecyclerView rv_movieGrid;

    List<Movie> movies = new ArrayList<Movie>();

    final static String Movie_BASE_URL = "http://api.themoviedb.org/3/movie/";
    final static String API_PARAM = "api_key";
    final static String APPID = BuildConfig.APIkey;
    String sortPref;
    RVMainScreenAdapter adapter;

    SharedPreferences preferences;

    private SQLiteDatabase mDb;
    private MoviesDbHelper dbHelper;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        rv_movieGrid = (RecyclerView) view.findViewById(R.id.recyclerview_mainfragment_moviegrid);

        dbHelper = new MoviesDbHelper(getActivity());
        mDb = dbHelper.getReadableDatabase();


        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rv_movieGrid.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            rv_movieGrid.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }
        adapter = new RVMainScreenAdapter(movies, getContext(), this);
        rv_movieGrid.setAdapter(adapter);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.registerOnSharedPreferenceChangeListener(this);
        getMovieData();

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    public void getMovieData() {

        movies.clear();
        sortPref = preferences.getString(getString(R.string.listpref_prefmain_sortlistkey), getString(R.string.pref_sortlist_defaultvalue));
        if (sortPref.equals("favorites")) {
            try {
                Cursor cursor = getActivity().getContentResolver().query(MoviesContract.MovieDetailEntry.CONTENT_URI, null, null, null, null);
                //retrieve the movies data from SQLite database
                // Cursor cursor = mDb.query(MoviesContract.MovieDetailEntry.TABLE_NAME,null,null,null,null,null, MoviesContract.MovieDetailEntry._ID);
                while (cursor.moveToNext()) {
                    String poster_path = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieDetailEntry.COLUMN_POSTER_PATH));
                    String overview = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieDetailEntry.COLUMN_OVERVIEW));
                    String release_date = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieDetailEntry.COLUMN_RELEASE_DATE));
                    String id = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieDetailEntry.COLUMN_ID));
                    String title = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieDetailEntry.COLUMN_TITLE));
                    String vote_average = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieDetailEntry.COLUMN_VOTE_AVERAGE));
                    String backdrop_path = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieDetailEntry.COLUMN_BACKDROP_PATH));

                    Movie movie = new Movie(poster_path, overview, release_date, id, title, vote_average, backdrop_path);
                    movies.add(movie);

                    adapter.setMoviesData(movies);
                }
            } catch (Exception e) {
                Log.e("Main Fragment", "failed to asyc load data");
                e.printStackTrace();
            }

        } else {
            Uri builtUri = Uri.parse(Movie_BASE_URL + sortPref + "?")
                    .buildUpon()
                    .appendQueryParameter(API_PARAM, APPID)
                    .build();


            StringRequest getMovieDataRequest = new StringRequest(Request.Method.POST, builtUri.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.contains("results")) {
                        try {
                            JSONObject jsonResult = new JSONObject(response);
                            JSONArray movieJsonArray = jsonResult.getJSONArray("results");

                            for (int i = 0; i < movieJsonArray.length(); i++) {
                                JSONObject currentObject = movieJsonArray.getJSONObject(i);
                                String poster_path = currentObject.getString("poster_path");
                                String overview = currentObject.getString("overview");
                                String release_date = currentObject.getString("release_date");
                                String id = currentObject.getString("id");
                                String title = currentObject.getString("title");
                                String vote_average = currentObject.getString("vote_average");
                                String backdrop_path = currentObject.getString("backdrop_path");
                                Movie movie = new Movie(poster_path, overview, release_date, id, title, vote_average, backdrop_path);
                                movies.add(movie);
                            }

                            adapter.setMoviesData(movies);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error in handling Json Data", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            Volley.newRequestQueue(getContext()).add(getMovieDataRequest);
        }
    }


    @Override
    public void onListItemClick(Movie movie) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.MOVIE_CLICKED, movie);
        startActivity(intent);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.listpref_prefmain_sortlistkey))) {
            getMovieData();
        }
    }
}
