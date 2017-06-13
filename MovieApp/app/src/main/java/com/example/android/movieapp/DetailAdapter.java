package com.example.android.movieapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOSTAFA on 29/04/2017.
 */

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    List data = new ArrayList();
    Context context;
    int reviewDataSize ;
    Movie movie;

    private final int VIEW_HEADER = 0;
    private final int VIEW_REVIEW_TRAILER_TITLE= 1;
    private final int VIEW_REVIEW_DATA = 2;
    private final int VIEW_TRAILER_DATA = 3;

    int trailerPosition = 0;

    public DetailAdapter(Movie movie, List data , Context context , int reviewDataSize){
        this.data = data;
        this.context = context;
        this.reviewDataSize = reviewDataSize;
        this.movie = movie;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_HEADER){
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_header,parent,false);
            HeaderViewHolder holder = new HeaderViewHolder(row);
            return holder;
        }
        else if (viewType == VIEW_REVIEW_TRAILER_TITLE){
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_review_title,parent,false);
            TitleViewHolder holder = new TitleViewHolder(row);
            return holder;
        }
        else if (viewType == VIEW_REVIEW_DATA){
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,parent,false);
            ReviewViewHolder holder = new ReviewViewHolder(row);
            return holder;
        }
        else if (viewType == VIEW_TRAILER_DATA){
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item,parent,false);
            TrailerViewHolder holder = new TrailerViewHolder(row);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + ((TrailerData)data.get(trailerPosition-3)).getKey())));
                    }catch (ActivityNotFoundException ex){
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + ((TrailerData)data.get(trailerPosition-3)).getKey()));
                        context.startActivity(intent);
                    }
                }
            });
            return holder;
        }
        else return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_HEADER){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder)holder;
            headerViewHolder.movieName.setText(movie.getTitle());
            Picasso.with(context)
                    .load(context.getString(R.string.image_url) + movie.getBackdrop_path())
                    .placeholder(R.drawable.postimg_error)
                    .error(R.drawable.postimg_error)
                    .into(headerViewHolder.movieBackground);
            headerViewHolder.releaseDate.setText(movie.getRelease_date());
            headerViewHolder.rate.setText(movie.getVote_average() + "/10");
            headerViewHolder.overview.setText(movie.getOverview());
        }
        else if (viewType == VIEW_REVIEW_TRAILER_TITLE){
            TitleViewHolder titleViewHolder = (TitleViewHolder)holder;
            if (position == 1)
                titleViewHolder.txt_title.setText("Reviews");
            else titleViewHolder.txt_title.setText("Trailers");

        }
        else if (viewType == VIEW_REVIEW_DATA){
            ReviewViewHolder reviewViewHolder = (ReviewViewHolder)holder;
            ReviewData reviewData = (ReviewData)data.get(position-2);
            reviewViewHolder.txt_author.setText(reviewData.getAuthor());
            reviewViewHolder.txt_content.setText(reviewData.getContent());
        }

        else if (viewType == VIEW_TRAILER_DATA){
            TrailerViewHolder trailerViewHolder = (TrailerViewHolder)holder;
            TrailerData trailerData = (TrailerData)data.get(position - 3); //3 for headerview, reviewtitle and trailertitle
            trailerViewHolder.txt_videoName.setText(trailerData.getName());
            trailerPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;
        else return data.size()+3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_HEADER;
        else if (position == 1)
            return VIEW_REVIEW_TRAILER_TITLE;
        else if (position>= 2 && position<reviewDataSize+2)
            return VIEW_REVIEW_DATA;
        else if (position == reviewDataSize+2)
            return VIEW_REVIEW_TRAILER_TITLE;
        else if (position>reviewDataSize+2 && position<data.size()+3)
            return VIEW_TRAILER_DATA;
        else return 100;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{

        TextView movieName;
        ImageView movieBackground;
        TextView releaseDate;
        TextView rate;
        TextView overview;
        Button btn_fav;
        Button btn_unfav;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            movieName = (TextView) itemView.findViewById(R.id.tv_detailfragment_moviename);
            movieBackground = (ImageView) itemView.findViewById(R.id.imageview_detailfragment_movieposter);
            releaseDate = (TextView) itemView.findViewById(R.id.tv_detailfragment_releasedate);
            rate = (TextView) itemView.findViewById(R.id.tv_detailfragment_rate);
            overview = (TextView) itemView.findViewById(R.id.tv_detailfragment_review);
            btn_fav = (Button) itemView.findViewById(R.id.btn_detailfragment_fav);
            btn_unfav = (Button) itemView.findViewById(R.id.btn_detailfragment_unfav);

        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder{
        TextView txt_title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            txt_title = (TextView)itemView.findViewById(R.id.txt_trailer_review_title);
        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        TextView txt_author;
        TextView txt_content;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            txt_author = (TextView)itemView.findViewById(R.id.txt_reviewitem_author);
            txt_content = (TextView)itemView.findViewById(R.id.txt_reviewitem_content);
        }
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder{

        TextView txt_videoName;
        public TrailerViewHolder(View itemView) {
            super(itemView);
            txt_videoName = (TextView)itemView.findViewById(R.id.txt_traileritem_name);
        }
    }
}
