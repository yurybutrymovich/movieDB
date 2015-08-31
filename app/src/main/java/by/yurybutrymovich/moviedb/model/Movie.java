package by.yurybutrymovich.moviedb.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yury Butrymovich on 28.08.2015.
 */
public class Movie implements Parcelable {


    private String posterUrl;
    private String originalTitle;
    private String plot;
    private String releaseDate;
    private Integer voteCount;
    private Float voteAverage;

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterUrl);
        parcel.writeString(originalTitle);
        parcel.writeString(plot);
        parcel.writeString(releaseDate);
        parcel.writeInt(voteCount);
        parcel.writeFloat(voteAverage);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        posterUrl = in.readString();
        originalTitle = in.readString();
        plot = in.readString();
        releaseDate = in.readString();
        voteCount = in.readInt();
        voteAverage = in.readFloat();
    }


    public Movie() {

    }
    
}
