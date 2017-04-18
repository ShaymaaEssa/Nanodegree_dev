package com.example.android.movieapp;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieapp.MoviesDB.MoviesContract;
import com.example.android.movieapp.MoviesDB.MoviesDbHelper;
import com.squareup.picasso.Picasso;


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

    private SQLiteDatabase mDb;
    private MoviesDbHelper dbHelper;

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
        movieName = (TextView) view.findViewById(R.id.tv_detailfragment_moviename);
        movieBackground = (ImageView) view.findViewById(R.id.imageview_detailfragment_movieposter);
        releaseDate = (TextView) view.findViewById(R.id.tv_detailfragment_releasedate);
        rate = (TextView) view.findViewById(R.id.tv_detailfragment_rate);
        overview = (TextView) view.findViewById(R.id.tv_detailfragment_review);
        btn_fav = (Button) view.findViewById(R.id.btn_detailfragment_fav);
        btn_unfav = (Button) view.findViewById(R.id.btn_detailfragment_unfav);
        btn_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertMovieInDB();
            }
        });

        btn_unfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeMovieFromDB();
            }
        });

        dbHelper = new MoviesDbHelper(getActivity());
        mDb = dbHelper.getWritableDatabase();


        return view;
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
            checkIfMovieInDB();
            movieName.setText(movie.getTitle());
            Picasso.with(getActivity())
                    .load(getActivity().getString(R.string.image_url) + movie.getBackdrop_path())
                    .placeholder(R.drawable.postimg_error)
                    .error(R.drawable.postimg_error)
                    .into(movieBackground);
            releaseDate.setText(movie.getRelease_date());
            rate.setText(movie.getVote_average() + "/10");
            overview.setText(movie.getOverview());
        }

    }
}
