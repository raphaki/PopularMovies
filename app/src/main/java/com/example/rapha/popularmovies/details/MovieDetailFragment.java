package com.example.rapha.popularmovies.details;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rapha.popularmovies.R;
import com.example.rapha.popularmovies.data.MovieRepository;
import com.example.rapha.popularmovies.data.local.MoviesDatabaseContract;
import com.example.rapha.popularmovies.utils.GlideApp;
import com.example.rapha.popularmovies.utils.TmdbUtils;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = getClass().getSimpleName();
    private final int DETAIL_LOADER_ID = 13452;
    private final int TRAILER_LOADER_ID = 23429;
    private final int REVIEW_LOADER_ID = 23430;
    private TextView titleTv;
    private TextView originalTitleTv;
    private ImageView posterIv;
    private TextView yearTv;
    private TextView ratingTv;
    private TextView plotTv;
    private RecyclerView trailerRv;
    private ListView reviewLv;
    private LinearLayoutManager trailerLayoutManager;
    private ImageView toolbarIv;
    private Toolbar toolbar;
    private FloatingActionButton favoriteButton;
    private ActionBar supportActionBar;

    private boolean isFavorite;

    private int movieId;
    private MovieRepository movieRepository;
    private TrailerAdapter trailerAdapter;
    private ReviewCursorAdapter reviewAdapter;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        movieRepository = MovieRepository.getInstance(getContext());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_detail_movie, container, false);

        titleTv = view.findViewById(R.id.detail_title);
        originalTitleTv = view.findViewById(R.id.detail_original_title);
        plotTv = view.findViewById(R.id.detail_plot);
        ratingTv = view.findViewById(R.id.detail_rating);
        yearTv = view.findViewById(R.id.detail_year);
        posterIv = view.findViewById(R.id.detaill_poster_iv);
        trailerRv = view.findViewById(R.id.detail_trailer_rv);
        reviewLv = view.findViewById(R.id.detail_review_lv);
        toolbarIv = view.findViewById(R.id.toolbar_iv);
        toolbar = view.findViewById(R.id.toolbar);
        favoriteButton = view.findViewById(R.id.favorite_action_button);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                movieRepository.changeMovieFavoriteStatus(movieId, isFavorite);
                setFavoriteButtonIcon();
            }
        });

        movieId = getArguments().getInt("movie_id");

        trailerAdapter = new TrailerAdapter();
        trailerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        trailerRv.setLayoutManager(trailerLayoutManager);
        trailerRv.setHasFixedSize(true);
        trailerRv.setAdapter(trailerAdapter);

        reviewAdapter = new ReviewCursorAdapter(getContext(), null);
        reviewLv.setAdapter(reviewAdapter);

        getActivity().getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        supportActionBar = activity.getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void populateView(Cursor cursor) {
        Log.d(TAG, "populateView");
        titleTv.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_TITLE)));
        originalTitleTv.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
        yearTv.setText(TmdbUtils.convertTmdbDateToLocalDateFormat(getContext(), cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_RELEASE_DATE))));
        ratingTv.setText(getString(R.string.detail_rating, String.valueOf(cursor.getDouble(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_RATING)))));
        plotTv.setText(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_OVERVIEW)));
        isFavorite = cursor.getInt(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_IS_FAVORITE)) == 1;
        setFavoriteButtonIcon();
        supportActionBar.setTitle(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_TITLE)));
        toolbar.setTitle(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_TITLE)));
        String fullPosterPath = TmdbUtils.getFullImageURL(cursor.getString(cursor.getColumnIndex(MoviesDatabaseContract.MovieEntry.COLUMN_POSTER_PATH)));
        GlideApp.with(getContext()).load(fullPosterPath).placeholder(R.drawable.placeholder).into(posterIv);
        GlideApp.with(getContext()).load(fullPosterPath).placeholder(R.drawable.ic_placeholder_trailer).into(toolbarIv);
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case DETAIL_LOADER_ID:
                return new CursorLoader(getContext(),
                        MoviesDatabaseContract.MovieEntry.buildMovieEntryUri(movieId),
                        null,
                        null,
                        null,
                        null);
            case TRAILER_LOADER_ID:
                return new CursorLoader(getContext(),
                        MoviesDatabaseContract.TrailerEntry.buildTrailerEntryUri(movieId),
                        null,
                        null,
                        null,
                        null);
            case REVIEW_LOADER_ID:
                return new CursorLoader(getContext(),
                        MoviesDatabaseContract.ReviewEntry.buildReviewEntryUri(movieId),
                        null,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case DETAIL_LOADER_ID:
                if (cursor != null && cursor.moveToFirst()) populateView(cursor);
                break;
            case TRAILER_LOADER_ID:
                if (cursor != null) {
                    if (cursor.getCount() == 0)
                        movieRepository.fetchTrailers(movieId);
                    else cursor.moveToFirst();
                    trailerAdapter.swapCursor(cursor);
                }
                break;
            case REVIEW_LOADER_ID:
                if (cursor != null) {
                    if (cursor.getCount() == 0)
                        movieRepository.fetchReviews(movieId);
                    else cursor.moveToFirst();
                    reviewAdapter.swapCursor(cursor);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    private void setFavoriteButtonIcon() {
        favoriteButton.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }
}
