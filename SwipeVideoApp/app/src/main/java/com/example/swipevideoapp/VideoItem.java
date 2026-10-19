package com.example.swipevideoapp;

/**
 * Holds the information for one video in the app.
 */
public class VideoItem {

    public String videoId;
    public String videoUrl;
    public String videoTitle;
    public String videoDescription;

    /**
     * Creates a new video item.
     *
     * @param videoId unique ID for the video
     * @param videoUrl link to the video
     * @param videoTitle title shown on the screen
     * @param videoDescription description shown on the screen
     */
    public VideoItem(String videoId, String videoUrl,
                     String videoTitle, String videoDescription) {

        this.videoId = videoId;
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
    }
}