package com.example.android.newsapp;

/**
 * Created by sahil on 20/3/18.
 */

public class News {
    private String mTitle;
    private String mSectionName;
    private String mAuthor;
    private String mUrl;
    private String mPublicationDate;

    public News(String title, String sectionName, String author, String url, String publicationDate) {
        mTitle = title;
        mSectionName = sectionName;
        mAuthor = author;
        mUrl = url;
        mPublicationDate = publicationDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getAuthor()
    {
        return mAuthor;
    }
    public String getUrl() {
        return mUrl;
    }

    public String getPublicationDate() {
        return mPublicationDate;
    }
}
