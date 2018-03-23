package com.ducdungdam.popularmovies.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import com.ducdungdam.popularmovies.R;
import com.ducdungdam.popularmovies.data.MovieRepository;
import com.ducdungdam.popularmovies.databinding.ActivityDetailBinding;
import com.ducdungdam.popularmovies.model.Movie;
import com.ducdungdam.popularmovies.viewmodel.DetailViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by ducdungdam on 20.03.18.
 *
 * Activity to shows Detail of a selected Movie
 */

public class DetailActivity extends AppCompatActivity {

  public static final String EXTRA_MOVIE_ID = "extra_movie_id";
  public static final String EXTRA_MOVIE_POSTER_TRANSITION_NAME = "extra_movie_poster_transition_name";

  private ActivityDetailBinding rootView;
  private DetailViewModel model;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    rootView = DataBindingUtil.setContentView(this, R.layout.activity_detail);

    Intent intent = getIntent();
    if (intent == null) {
      closeOnError();
      return;
    }

    int movieId = intent.getIntExtra(EXTRA_MOVIE_ID, -1);
    if (movieId == -1) {
      closeOnError();
      return;
    }

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      postponeEnterTransition(); // needs to be set to avoid image load flickering
      String imageTransitionName = intent.getStringExtra(EXTRA_MOVIE_POSTER_TRANSITION_NAME);
      rootView.ivMoviePoster.setTransitionName(imageTransitionName);
    }

    setSupportActionBar(rootView.toolbar);
    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowTitleEnabled(false);
    }

    model = ViewModelProviders.of(this).get(DetailViewModel.class);
    model.getMovie(movieId).observe(this, new Observer<Movie>() {
      @Override
      public void onChanged(@Nullable final Movie movie) {
        rootView.setMovie(movie);
      }
    });
  }

  private void closeOnError() {
    finish();
    Toast.makeText(this, "Movie Data not available", Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        supportFinishAfterTransition();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Loads an Image by a given String Source to an ImageView in DetailsActivity.
   * It also can be used for DataBinding.
   *
   */
  @BindingAdapter("detailSetSharedTransitionMovieImage")
  public static void setMovieImage(ImageView view, String url) {
    String imagePath = MovieRepository.getImageUrl(url);
    final DetailActivity activity = (DetailActivity) view.getContext();
    Picasso.with(activity)
        .load(imagePath)
        .noFade()
        .into(view, new Callback() {
          @Override
          public void onSuccess() {
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
              activity.startPostponedEnterTransition();
            }
          }

          @Override
          public void onError() {
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
              activity.startPostponedEnterTransition();
            }
          }
        });
  }

}