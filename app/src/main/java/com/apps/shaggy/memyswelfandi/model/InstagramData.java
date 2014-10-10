package com.apps.shaggy.memyswelfandi.model;

public class InstagramData {

    private ImageData lowResolution;
    private ImageData thumbnail;
    private ImageData standardResolution;

    public InstagramData(){

    }

    public InstagramData(ImageData lowResolution, ImageData thumbnail, ImageData standardResolution){
        this.lowResolution = lowResolution;
        this.thumbnail = thumbnail;
        this.standardResolution = standardResolution;
    }

    public ImageData getLowResolution() {
        return lowResolution;
    }

    public void setLowResolution(ImageData lowResolution) {
        this.lowResolution = lowResolution;
    }

    public ImageData getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ImageData thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ImageData getStandardResolution() {
        return standardResolution;
    }

    public void setStandardResolution(ImageData standardResolution) {
        this.standardResolution = standardResolution;
    }

}
