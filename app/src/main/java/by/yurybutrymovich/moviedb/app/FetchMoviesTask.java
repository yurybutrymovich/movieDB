package by.yurybutrymovich.moviedb.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Yury Butrymovich on 27.08.2015.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

    public final static String LATEST_MOVIES = "latest";
    public final static String API_KEY = "api_key";

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    @Override
    protected void onPostExecute(String[] strings) {
        if (strings != null && strings.length > 0) {

        }
    }

    @Override
    protected String[] doInBackground(String... strings) {
        String param, apiKey;
        if (strings.length == 0 || strings.length > 2) {
            return null;
        } else  {
            param = strings[0];
            apiKey = strings[1];
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

    private String[] getMovieDataFromJson(String moviesJsonStr) throws JSONException {
        final String JSON_RESULTS = "results";
        final String JSON_POSTER_PATH = "poster_path";
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(JSON_RESULTS);
        String[] resultPaths = new String[moviesArray.length()];
        for (int i=0;i<moviesArray.length();i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            resultPaths[i] = movie.getString(JSON_POSTER_PATH);
        }
        return resultPaths;
    }
}
