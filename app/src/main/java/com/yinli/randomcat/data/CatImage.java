package com.yinli.randomcat.data;

import org.parceler.Parcel;

/**
 * RandomCat
 * Created by Yin Li on 13/05/15.
 * Copyright (c) 2015 Yin Li. All rights reserved.
 */

@Parcel
public class CatImage {
    String id;
    String imgUrl;
    String sourceUrl;

    public CatImage() {
    }

    public CatImage(String id, String imgUrl, String sourceUrl) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.sourceUrl = sourceUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
