package com.madageekscar.panaira.results;

public class YoutubeResult {

    private String videoId;
    private String videoTitle;
    private String videoUrl;
    private String videoThumbnail;

    public YoutubeResult(String id, String title, String thumbnail) {

        setVideoId(id);
        setVideoTitle(title);
        setVideoUrl(formatVideoUrl());
        setVideoThumbnail(thumbnail);

    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    private String formatVideoUrl() {
        return "https://www.youtube.com/watch?v=" + videoId;
    }
}
