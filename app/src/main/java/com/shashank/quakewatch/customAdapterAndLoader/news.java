package com.shashank.quakewatch.customAdapterAndLoader;

public class news {
    private final String mURL;
    private final String mImgUrl;
    private final String mDescription;
    private final String mTitle;

    public news(String description, String imgURL,String url, String title) {
        mDescription = description;
        mURL = url;
        mImgUrl = imgURL;
        mTitle = title;

    }
    public String getURL() {
        return mURL;
    }

    public String getDescription() { return mDescription; }

    public String getImgUrl() { return mImgUrl; }

    public String getTitle() { return mTitle; }
}
