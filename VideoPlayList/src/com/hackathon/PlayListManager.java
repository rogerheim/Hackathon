package com.hackathon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Key;



public class PlayListManager {
	

	public static class VideoFeed {
		@Key List<Video> items;
	}

	public static class PlayListFeed {
		@Key List<PlayList> items;
	}

	public static class PlayList {
		@Key String id;
	}

	public static class PlayListEntryFeed {
		@Key List<PlayListEntry> items;
	}

	public static class PlayListEntry {
		@Key Video video;
	}

	public static class Video {
		@Key String id;
		@Key String title;
        @Key String description;
        @Key Thumbnail thumbnail;
	}

	public static class Thumbnail {
        @Key("sqDefault") String sqDefault;
        @Key("hqDefault") String hqDefault;
    }

	
	
	public static class Player {
		@Key("default") String defaultUrl;
	}

	public static class YouTubeUrl extends GenericUrl {
		@Key final String alt = "jsonc";
		@Key String author;
		@Key("max-results") Integer maxResults;

		YouTubeUrl(String url) { super(url); }
	}

	public static class PlayListUrl extends GenericUrl {
		@Key final String alt = "jsonc";
		PlayListUrl(String url) { super(url); }
	}




	public static ArrayList<YouTubeVideo> buildList(String user) {
		ArrayList<YouTubeVideo> alv = new ArrayList<YouTubeVideo>();
		String playListId = findFirstPlayList(user);
		try {
			JsonCParser parser = new JsonCParser();
			parser.jsonFactory = new JacksonFactory();
			// set up the Google headers
			GoogleHeaders headers = new GoogleHeaders();
			headers.setApplicationName("Google-YouTubeSample/1.0");
			headers.gdataVersion = "2";
			// set up the HTTP transport
			HttpTransport transport = new NetHttpTransport();
			transport.defaultHeaders = headers;
			transport.addParser(parser);

			PlayListUrl url = new PlayListUrl("http://gdata.youtube.com/feeds/api/playlists/"+ playListId +"?v=2");
			// build the HTTP GET request
			HttpRequest request = transport.buildGetRequest();
			request.url = url;
			// execute the request and the parse video feed
			int counter = 1;
			PlayListEntryFeed feed = request.execute().parseAs(PlayListEntryFeed.class);
			for (PlayListEntry pl : feed.items) {
				YouTubeVideo v = new YouTubeVideo();
				v.setId(counter++);
				v.setVideoTitle(pl.video.title);
				v.setVideoTitle2(pl.video.description);
				v.setThumbnailURL(pl.video.thumbnail.sqDefault);
				alv.add(v);
			}
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
		return alv;
	}

	private static String findFirstPlayList(String user) {
		try {
			JsonCParser parser = new JsonCParser();
			parser.jsonFactory = new JacksonFactory();
			// set up the Google headers
			GoogleHeaders headers = new GoogleHeaders();
			headers.setApplicationName("Google-YouTubeSample/1.0");
			headers.gdataVersion = "2";
			// set up the HTTP transport
			HttpTransport transport = new NetHttpTransport();
			transport.defaultHeaders = headers;
			transport.addParser(parser);
			// build the YouTube URL

			PlayListUrl url = new PlayListUrl("http://gdata.youtube.com/feeds/api/users/" + user + "/playlists?v=2");
			// build the HTTP GET request
			HttpRequest request = transport.buildGetRequest();
			request.url = url;
			// execute the request and the parse video feed
			HttpResponse hr = request.execute();
			String s = hr.parseAsString();
			System.out.println( s);
			PlayListFeed feed = request.execute().parseAs(PlayListFeed.class);
			for (PlayList pl : feed.items) {
				return pl.id;
			}
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
		return "";
	}

}
