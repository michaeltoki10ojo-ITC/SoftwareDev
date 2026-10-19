package com.example.swipevideoapp;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Connects the video information to each video screen.
 */
public class VideoAdapter
        extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final List<VideoItem> videoItems;

    /**
     * Creates the adapter using the list of videos.
     *
     * @param videoItems list of videos for the app
     */
    public VideoAdapter(List<VideoItem> videoItems) {
        this.videoItems = videoItems;
    }

    /**
     * Creates a new screen for a video.
     *
     * @param parent parent layout
     * @param viewType type of view
     * @return the new video holder
     */
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_container_video,
                        parent,
                        false
                );

        return new VideoViewHolder(view);
    }

    /**
     * Puts the correct video information on the screen.
     *
     * @param holder video screen being used
     * @param position position of the video
     */
    @Override
    public void onBindViewHolder(
            @NonNull VideoViewHolder holder,
            int position) {

        int realPosition = position % videoItems.size();
        holder.setVideoData(videoItems.get(realPosition));;
    }

    /**
     * Gets the number of videos in the list.
     *
     * @return number of videos
     */
    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    /**
     * Holds everything that is shown for one video.
     */
    static class VideoViewHolder extends RecyclerView.ViewHolder {

        private final VideoView videoView;
        private final TextView textVideoId;
        private final TextView textVideoTitle;
        private final TextView textVideoDescription;
        private final ProgressBar progressBar;

        /**
         * Connects the Java code to the XML items.
         *
         * @param itemView current video layout
         */
        public VideoViewHolder(@NonNull View itemView) {

            super(itemView);

            videoView =
                    itemView.findViewById(R.id.videoView);

            textVideoId =
                    itemView.findViewById(R.id.textVideoId);

            textVideoTitle =
                    itemView.findViewById(R.id.textVideoTitle);

            textVideoDescription =
                    itemView.findViewById(R.id.textVideoDescription);

            progressBar =
                    itemView.findViewById(R.id.progressBar);
        }

        /**
         * Loads the video and adds its information to the screen.
         *
         * @param videoItem video being displayed
         */
        void setVideoData(VideoItem videoItem) {

            textVideoId.setText(videoItem.videoId);
            textVideoTitle.setText(videoItem.videoTitle);
            textVideoDescription.setText(
                    videoItem.videoDescription
            );

            progressBar.setVisibility(View.VISIBLE);

            videoView.setVideoPath(videoItem.videoUrl);

            videoView.setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {

                        /**
                         * Starts the video after it finishes loading.
                         *
                         * @param mediaPlayer video player
                         */
                        @Override
                        public void onPrepared(
                                MediaPlayer mediaPlayer) {

                            progressBar.setVisibility(View.GONE);

                            mediaPlayer.setLooping(true);

                            videoView.start();
                        }
                    });

            videoView.setOnErrorListener(
                    (mediaPlayer, what, extra) -> {

                        progressBar.setVisibility(View.GONE);

                        return false;
                    });
        }
    }
}