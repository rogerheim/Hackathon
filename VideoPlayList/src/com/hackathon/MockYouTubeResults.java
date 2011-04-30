package com.hackathon;

import java.util.ArrayList;

/**
 * A class to return mock youtube results. For testing purposes
 */
public class MockYouTubeResults {

    public ArrayList<YouTubeVideo> createSampleVideoList() {
        ArrayList<YouTubeVideo> results = new ArrayList<YouTubeVideo>();

        for (int i = 0; i <= 25; i++) {
            YouTubeVideo ytv = new YouTubeVideo();
            ytv.setId(i);
            ytv.setVideoTitle(String.format("Video %d Title", i));
            ytv.setVideoTitle2(String.format("Video %d Title 2", i));
            results.add(ytv);
        }
        return results;
    }
    
}
