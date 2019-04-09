package com.nytimespracticaldemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result implements Parcelable {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("adx_keywords")
    @Expose
    private String adxKeywords;
    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("byline")
    @Expose
    private String byline;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("abstract")
    @Expose
    private String _abstract;
    @SerializedName("published_date")
    @Expose
    private String publishedDate;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("id")
    @Expose
    private Double id;
    @SerializedName("asset_id")
    @Expose
    private Double assetId;
    @SerializedName("views")
    @Expose
    private Integer views;

    @SerializedName("media")
    @Expose
    private List<Medium> media = null;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAdxKeywords() {
        return adxKeywords;
    }

    public void setAdxKeywords(String adxKeywords) {
        this.adxKeywords = adxKeywords;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getByline() {
        return byline;
    }

    public void setByline(String byline) {
        this.byline = byline;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstract() {
        return _abstract;
    }

    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }

    public Double getAssetId() {
        return assetId;
    }

    public void setAssetId(Double assetId) {
        this.assetId = assetId;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public List<Medium> getMedia() {
        return media;
    }

    public void setMedia(List<Medium> media) {
        this.media = media;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.adxKeywords);
        dest.writeString(this.section);
        dest.writeString(this.byline);
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeString(this._abstract);
        dest.writeString(this.publishedDate);
        dest.writeString(this.source);
        dest.writeValue(this.id);
        dest.writeValue(this.assetId);
        dest.writeValue(this.views);
        dest.writeTypedList(this.media);
    }

    public Result() {
    }

    protected Result(Parcel in) {
        this.url = in.readString();
        this.adxKeywords = in.readString();
        this.section = in.readString();
        this.byline = in.readString();
        this.type = in.readString();
        this.title = in.readString();
        this._abstract = in.readString();
        this.publishedDate = in.readString();
        this.source = in.readString();
        this.id = (Double) in.readValue(Double.class.getClassLoader());
        this.assetId = (Double) in.readValue(Double.class.getClassLoader());
        this.views = (Integer) in.readValue(Integer.class.getClassLoader());
        this.media = in.createTypedArrayList(Medium.CREATOR);
    }

    public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel source) {
            return new Result(source);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };
}
