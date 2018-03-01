package com.example.rapha.popularmovies.movies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.Movie;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private List<Movie> movies;

    public MoviesAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    public MoviesAdapter() {
        this(new ArrayList<Movie>());
    }

    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_poster_item, parent, false);
        return new ViewHolder(view);
    }

    public void swapMovies(List<Movie> movies) {
        if (movies != null) {
            this.movies = movies;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.ViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(Movie movie) {
            ImageView posterIv = itemView.findViewById(R.id.poster_item_iv);
            posterIv.setContentDescription(itemView.getContext().getString(R.string.content_description) + movie.getTitle());
            Glide.with(itemView.getContext()).load(movie.getPosterURL()).into(posterIv);
        }

    }
}
