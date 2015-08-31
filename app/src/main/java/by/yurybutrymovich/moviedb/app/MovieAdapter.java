package by.yurybutrymovich.moviedb.app;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import by.yurybutrymovich.moviedb.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Yury Butrymovich on 27.08.2015.
 * Adapter for Movie model.
 */
public class MovieAdapter extends BaseAdapter {

    private ArrayList<Movie> mImageUris;
    private Context mContext;
    private String imagePath;

    public MovieAdapter(Context context, ArrayList<Movie> imgUris, String imgPath) {
        mContext = context;
        mImageUris = imgUris;
        imagePath = imgPath;
    }

    public void clear() {
        mImageUris.clear();
    }

    @Override
    public int getCount() {
        return mImageUris.size();
    }

    @Override
    public Parcelable getItem(int i) {
        return mImageUris.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
           // imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) view;
        }
        Picasso.with(mContext).load(imagePath + mImageUris.get(i).getPosterUrl()).into(imageView);
        return imageView;
    }

    public void add(Movie entry) {
        mImageUris.add(entry);
    }
}
