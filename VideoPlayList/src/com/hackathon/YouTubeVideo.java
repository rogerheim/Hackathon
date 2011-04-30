package com.hackathon;

public class YouTubeVideo {

    private long id;
    private String videoTitle;
    private String videoTitle2;
	private String thumbnailURL;
    private String youtubeID;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVideoTitle2() {
        return videoTitle2;
    }

    public void setVideoTitle2(String videoTitle2) {
        this.videoTitle2 = videoTitle2;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }
    public String getThumbnailURL() {
		return thumbnailURL;
	}

	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}

	public String getYoutubeID() {
		return youtubeID;
	}

	public void setYoutubeID(String youtubeID) {
		this.youtubeID = youtubeID;
	}

}
