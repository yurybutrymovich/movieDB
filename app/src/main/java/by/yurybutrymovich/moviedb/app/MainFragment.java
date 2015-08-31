package by.yurybutrymovich.moviedb.app;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import by.yurybutrymovich.moviedb.model.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Fragment of the MainActivity.
 */
public class MainFragment extends Fragment {

    private MovieAdapter adapter;
    public static final String POSTER_URL = "http://image.tmdb.org/t/p/w185";
    public static final String EXTRA_MOVIE = "by.yurybutrymovich.extra.MOVIE";

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        updateMovies(FetchMoviesTask.POPULAR_MOVIES);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_most_popular) {
            updateMovies(FetchMoviesTask.POPULAR_MOVIES);
            return true;
        } else if (id == R.id.action_latest) {
            updateMovies(FetchMoviesTask.LATEST_MOVIES);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovies(String type) {
        new FetchMoviesTask().execute(type, getApiKey());
    }

    private String getApiKey() {
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        try {
            InputStream inputStream = assetManager.open("app.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("api_key");
        } catch (IOException e) {
            System.err.println("Failed to open app property file");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        adapter = new MovieAdapter(getActivity(), new ArrayList<Movie>(), POSTER_URL);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
               /* Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();*/

                Intent detailMovieIntent = new Intent(getActivity(),DetailMovieActivity.class);
                detailMovieIntent.putExtra(EXTRA_MOVIE,adapter.getItem(position));
                startActivity(detailMovieIntent);
            }
        });

        return rootView;
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        public final static String LATEST_MOVIES = "latest";
        public final static String POPULAR_MOVIES = "popular";
        public final static String API_KEY = "api_key";

        private int maxNumberToFetch = 20;
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null && movies.length > 0) {
                adapter.clear();
                for (Movie entry : movies) {
                    adapter.add(entry);
                    adapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        protected Movie[] doInBackground(String... strings) {
            String param, apiKey;
            if (strings.length == 0 || strings.length > 3) {
                return null;
            } else {
                param = strings[0];
                apiKey = strings[1];
                if (strings.length > 2) {
                    maxNumberToFetch = Integer.parseInt(strings[2]);
                }
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendPath(param)
                        .appendQueryParameter(API_KEY, apiKey)
                        .build();
                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private Movie[] getMovieDataFromJson(String moviesJsonStr) throws JSONException {
            final String JSON_RESULTS = "results";
            final String JSON_POSTER_PATH = "poster_path";
            final String JSON_ORIGINAL_TITLE = "original_title";
            final String JSON_PLOT = "overview";
            final String JSON_RELEASE_DATE = "release_date";
            final String JSON_VOTE_AVERAGE = "vote_average";
            final String JSON_VOTE_COUNT = "vote_count";
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            Movie[] resultPaths = null;
            if (moviesJson.has(JSON_RESULTS)) {
                JSONArray moviesArray = moviesJson.getJSONArray(JSON_RESULTS);
                int fetchLimit = moviesArray.length() > maxNumberToFetch ? maxNumberToFetch : moviesArray.length();
                resultPaths = new Movie[fetchLimit];
                for (int i = 0; i < fetchLimit; i++) {
                    JSONObject movie = moviesArray.getJSONObject(i);
                    Movie movieOut = new Movie();
                    movieOut.setPosterUrl(movie.getString(JSON_POSTER_PATH));
                    movieOut.setOriginalTitle(movie.getString(JSON_ORIGINAL_TITLE));
                    movieOut.setPlot(movie.getString(JSON_PLOT));
                    movieOut.setReleaseDate(movie.getString(JSON_RELEASE_DATE));
                    movieOut.setVoteAverage(Float.parseFloat(movie.getString(JSON_VOTE_AVERAGE)));
                    movieOut.setVoteCount(Integer.parseInt(movie.getString(JSON_VOTE_COUNT)));
                    resultPaths[i] = movieOut;
                }
            } else if (moviesJson.has(JSON_POSTER_PATH)) {
                Movie movie = new Movie();
                movie.setPosterUrl(moviesJson.getString(JSON_POSTER_PATH));
                resultPaths = new Movie[]{movie};
            }

            return resultPaths;
        }
    }


}
