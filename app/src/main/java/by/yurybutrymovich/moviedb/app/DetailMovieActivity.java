package by.yurybutrymovich.moviedb.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;
import by.yurybutrymovich.moviedb.model.Movie;
import by.yurybutrymovich.moviedb.util.Const;
import com.squareup.picasso.Picasso;


public class DetailMovieActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail_movie, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(MainFragment.EXTRA_MOVIE)) {
                Movie movie = intent.getParcelableExtra(MainFragment.EXTRA_MOVIE);
                ImageView imageView = (ImageView) rootView.findViewById(R.id.posterImageView);
                Picasso.with(getActivity()).load(Const.POSTER_URL + movie.getPosterUrl()).into(imageView);
                ((TextView) rootView.findViewById(R.id.originalTitleTextView)).setText(movie.getOriginalTitle());
                ((TextView) rootView.findViewById(R.id.releaseDateTextView)).setText("Release date: " + movie.getReleaseDate());
                ((TextView) rootView.findViewById(R.id.voteCountTextView)).setText("Votes: " + Integer.toString(movie.getVoteCount()));
                ((TextView) rootView.findViewById(R.id.averageVoteCountTextView)).setText("Average score: " + Float.toString(movie.getVoteAverage()));
                ((TextView) rootView.findViewById(R.id.plotTextView)).setText(movie.getPlot());
            }

            return rootView;
        }
    }
}
