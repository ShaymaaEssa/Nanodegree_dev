package com.example.android.movieapp;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.movieapp.MoviesDB.MoviesContract;
import com.example.android.movieapp.MoviesDB.MoviesDbHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    Movie movie;

    TextView movieName;
    ImageView movieBackground;
    TextView releaseDate;
    TextView rate;
    TextView overview;
    Button btn_fav;
    Button btn_unfav;

    RecyclerView recyclerView_movieDetail;
    DetailAdapter detailAdapter;
    List data = new ArrayList();

    private SQLiteDatabase mDb;
    private MoviesDbHelper dbHelper;

    List<ReviewData>reviewDatas = new ArrayList<ReviewData>();
    List<TrailerData>trailerDatas = new ArrayList<TrailerData>();

    final static String Movie_BASE_URL = "http://api.themoviedb.org/3/movie/";
    final static String API_PARAM = "api_key";
    final static String APPID = BuildConfig.APIkey;

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
//        movieName = (TextView) view.findViewById(R.id.tv_detailfragment_moviename);
//        movieBackground = (ImageView) view.findViewById(R.id.imageview_detailfragment_movieposter);
//        releaseDate = (TextView) view.findViewById(R.id.tv_detailfragment_releasedate);
//        rate = (TextView) view.findViewById(R.id.tv_detailfragment_rate);
//        overview = (TextView) view.findViewById(R.id.tv_detailfragment_review);
//        btn_fav = (Button) view.findViewById(R.id.btn_detailfragment_fav);
//        btn_unfav = (Button) view.findViewById(R.id.btn_detailfragment_unfav);
//        btn_fav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                insertMovieInDB();
//            }
//        });
//
//        btn_unfav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                removeMovieFromDB();
//            }
//        });

        recyclerView_movieDetail = (RecyclerView)view.findViewById(R.id.recyclerview_detailfragment_moviedetails);

        dbHelper = new MoviesDbHelper(getActivity());
        mDb = dbHelper.getWritableDatabase();


        return view;
    }


    //to get review data for that movie
    private List<ReviewData> getReviews(String id) {

        final List<ReviewData> reviews = new ArrayList<ReviewData>();
        Uri builtUri = Uri.parse(Movie_BASE_URL +movie.getId() + "/reviews"+"?")
                .buildUpon()
                .appendQueryParameter(API_PARAM, APPID)
                .build();

        StringRequest getReviewDataRequest = new StringRequest(Request.Method.GET, builtUri.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("results")){
                    try{
                        JSONObject jsonResult = new JSONObject(response);
                        JSONArray reviewJsonArray = jsonResult.getJSONArray("results");

                        for (int i = 0; i < reviewJsonArray.length(); i++) {
                            JSONObject currentObject = reviewJsonArray.getJSONObject(i);
                            String author = currentObject.getString("author");
                            String content = currentObject.getString("content");

                            ReviewData review = new ReviewData(author,content);
                            reviews.add(review);

                        }

                        //data.addAll(reviews);
                        reviewDatas = reviews;
                        setAdapterData();
                        //detailAdapter.notifyDataSetChanged();

                    }catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error in handling Json Data", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                }

            }
        } ,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        Volley.newRequestQueue(getContext()).add(getReviewDataRequest);
        return reviews;
    }

    //to get trailer data for that movie
    private List<TrailerData> getTrailer(String id) {

        final List<TrailerData> trailers = new ArrayList<TrailerData>();
        Uri builtUri = Uri.parse(Movie_BASE_URL +movie.getId() + "/videos"+"?")
                .buildUpon()
                .appendQueryParameter(API_PARAM, APPID)
                .build();


        Log.v("TrailerURL",builtUri.toString());
        StringRequest getTrailerDataRequest = new StringRequest(Request.Method.GET, builtUri.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("results")){
                    try{
                        JSONObject jsonResult = new JSONObject(response);
                        JSONArray trailerJsonArray = jsonResult.getJSONArray("results");

                        for (int i = 0; i < trailerJsonArray.length(); i++) {
                            JSONObject currentObject = trailerJsonArray.getJSONObject(i);
                            String name = currentObject.getString("name");
                            String key = currentObject.getString("key");

                            TrailerData trailer = new TrailerData(name,key);
                            trailers.add(trailer);

                        }

                       // data.addAll(trailers);
                        trailerDatas = trailers;
                        setAdapterData();
                        //detailAdapter.notifyDataSetChanged();

                    }catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error in handling Json Data", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                }

            }
        } ,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        Volley.newRequestQueue(getContext()).add(getTrailerDataRequest);
        return trailers;
    }


    private void removeMovieFromDB() {
        Uri uri = MoviesContract.MovieDetailEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movie.getId()).build();
        getActivity().getContentResolver().delete(uri,null,null);
        //mDb.delete(MoviesContract.MovieDetailEntry.TABLE_NAME, MoviesContract.MovieDetailEntry.COLUMN_ID + " = " + movie.getId(), null);
        Toast.makeText(getActivity(), "Movie removed from Fav ", Toast.LENGTH_SHORT).show();
        btn_unfav.setVisibility(View.INVISIBLE);
        btn_fav.setVisibility(View.VISIBLE);
    }

    private void checkIfMovieInDB() {
        Uri uri = MoviesContract.MovieDetailEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movie.getId()).build();
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        //Cursor cursor = mDb.query(MoviesContract.MovieDetailEntry.TABLE_NAME, null, MoviesContract.MovieDetailEntry.COLUMN_ID + " = ? ", new String[]{movie.getId()}, null, null, null);
        if (cursor.moveToNext()) {
            btn_fav.setVisibility(View.INVISIBLE);
            btn_unfav.setVisibility(View.VISIBLE);
        } else {
            btn_fav.setVisibility(View.VISIBLE);
            btn_unfav.setVisibility(View.INVISIBLE);
        }
    }

    //insert Movie item values into SQLiteDB
    private void insertMovieInDB() {
        if (movie != null) {
            ContentValues cv = new ContentValues();
            cv.put(MoviesContract.MovieDetailEntry.COLUMN_TITLE, movie.getTitle());
            cv.put(MoviesContract.MovieDetailEntry.COLUMN_BACKDROP_PATH, movie.getBackdrop_path());
            cv.put(MoviesContract.MovieDetailEntry.COLUMN_ID, movie.getId());
            cv.put(MoviesContract.MovieDetailEntry.COLUMN_OVERVIEW, movie.getOverview());
            cv.put(MoviesContract.MovieDetailEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
            cv.put(MoviesContract.MovieDetailEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());
            cv.put(MoviesContract.MovieDetailEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());

            //long num = mDb.insert(MoviesContract.MovieDetailEntry.TABLE_NAME, null, cv);
            Uri uri = getActivity().getContentResolver().insert(MoviesContract.MovieDetailEntry.CONTENT_URI, cv);
            if (uri != null) {
                Toast.makeText(getActivity(), "Movie Saved to Fav ", Toast.LENGTH_SHORT).show();
            }

            btn_fav.setVisibility(View.INVISIBLE);
            btn_unfav.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();
        if (movie != null) {
            reviewDatas = getReviews(movie.getId());
            trailerDatas = getTrailer(movie.getId());
            recyclerView_movieDetail.setLayoutManager(new LinearLayoutManager(getContext()));
        }

    }

    private void setAdapterData() {
        if (reviewDatas.size()!=0 && trailerDatas.size()!=0) {
            data.addAll(reviewDatas);
            data.addAll(trailerDatas);
            detailAdapter = new DetailAdapter(movie, data, getContext(), reviewDatas.size());
            recyclerView_movieDetail.setAdapter(detailAdapter);
        }
    }

}
