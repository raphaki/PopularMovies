package com.example.rapha.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;

import com.example.rapha.popularmovies.data.local.MoviesDatabaseContract;
import com.example.rapha.popularmovies.data.models.Movie;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TmdbUtils {

    private static final String IMAGE_URL = "https://image.tmdb.org/t/p/w185";
    private static final String TMDB_DATE_PATTERN = "yyyy-MM-dd";

    public static String getFullImageURL(String posterPath) {
        return IMAGE_URL + posterPath;
    }

    public static String convertTmdbDateToLocalDateFormat(Context context, String tmdbDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TMDB_DATE_PATTERN);
        String dateShown;
        try {
            Date date = simpleDateFormat.parse(tmdbDate);
            dateShown = DateFormat.getDateInstance(DateFormat.SHORT, context.getResources().getConfiguration().locale).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            dateShown = tmdbDate;
        }
        return dateShown;
    }

    public static ContentValues[] getMovieContentValuesFromMovieList(List<Movie> movieList, boolean isPopular, List<Integer> favoriteIds) {
        ContentValues[] contentValuesArray = new ContentValues[movieList.size()];

        for (int index = 0; index < contentValuesArray.length; index++) {
            ContentValues movieContentValues = new ContentValues();
            Movie movie = movieList.get(index);

            movieContentValues.put(MoviesDatabaseContract.MovieEntry._ID, movie.getId());
            movieContentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            movieContentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_RATING, movie.getVoteAverage());
            movieContentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            movieContentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            movieContentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            movieContentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieContentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            movieContentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE, isMovieAFavorite(movie.getId(), favoriteIds));
            movieContentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_IS_POPULAR, isPopular);
            movieContentValues.put(MoviesDatabaseContract.MovieEntry.COLUMN_IS_TOP_RATED, !isPopular);

            contentValuesArray[index] = movieContentValues;
        }
        return contentValuesArray;
    }

    private static boolean isMovieAFavorite(int movieId, List<Integer> favoriteIds) {
        for (int id : favoriteIds) {
            if (movieId == id) return true;
        }
        return false;
    }
}
