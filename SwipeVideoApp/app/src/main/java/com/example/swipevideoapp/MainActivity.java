package com.example.swipevideoapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

/**
 * Main part of my swipe video app.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Sets everything up when the app starts.
     *
     * @param savedInstanceState saved app information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 videoViewPager =
                findViewById(R.id.videoViewPager);

        List<VideoItem> videoItems =
                new ArrayList<>();

        VideoItem video1 = new VideoItem(
                "NBA001",
                "https://raw.githubusercontent.com/michaeltoki10ojo-ITC/NBAHighlights/main/highlight1.mp4",
                "Nuggets Highlight",
                "A highlight from a Nuggets game."
        );

        VideoItem video2 = new VideoItem(
                "NBA002",
                "https://raw.githubusercontent.com/michaeltoki10ojo-ITC/NBAHighlights/main/highlight2.mp4",
                "Spurs Highlight",
                "A highlight from a Spurs game."
        );

        VideoItem video3 = new VideoItem(
                "NBA003",
                "https://raw.githubusercontent.com/michaeltoki10ojo-ITC/NBAHighlights/main/highlight3.mp4",
                "Hornets Highlight",
                "A highlight from a Hornets game."
        );

        VideoItem video4 = new VideoItem(
                "NBA004",
                "https://raw.githubusercontent.com/michaeltoki10ojo-ITC/NBAHighlights/main/highlight4.mp4",
                "TimberWolves Highlight",
                "A highlight from a TimberWolves game."
        );

        videoItems.add(video1);
        videoItems.add(video2);
        videoItems.add(video3);
        videoItems.add(video4);


        VideoAdapter videoAdapter =
                new VideoAdapter(videoItems);

        videoViewPager.setAdapter(videoAdapter);
        int startPosition = Integer.MAX_VALUE / 2;
        startPosition -= startPosition % videoItems.size();
        videoViewPager.setCurrentItem(startPosition, false);

        videoViewPager.setOrientation(
                ViewPager2.ORIENTATION_VERTICAL
        );
    }
}