package com.hackathon;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.AlteredCharSequence;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthDomainWideDelegation;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Key;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends ListActivity {

    QuickActionMenuManager qaMgr = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setListAdapter(new VideoAdapter(this));

        setContentView(R.layout.playlist_layout);
    }

    @Override
    protected void onPause() {
        qaMgr.destroyQuickActionMenu();
        super.onPause();
    }

    @Override
    protected void onResume() {
        qaMgr = new QuickActionMenuManager(findViewById(R.id.playlist_anchor_text));

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                VideoViewHolder holder = (VideoViewHolder) view.getTag();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://m.youtube.com/watch?v=" + holder.videoid));
                startActivity(i);
            }
        });
        Command searchGoCommand = new Command() {
            @Override
            public void execute(View view, Object data) {
                qaMgr.destroyQuickActionMenu();
//                Toast.makeText(StartActivity.this, "Search:" + data.toString(), Toast.LENGTH_SHORT).show();
                Log.i("HackersVJ", data.toString());
                if (!TextUtils.isEmpty(data.toString())) {
                    new DoYouTubeSearch(StartActivity.this, data.toString()).execute();
                }
            }
        };
        Command searchCancelCommand = new Command() {
            @Override
            public void execute(View view, Object data) {
                qaMgr.destroyQuickActionMenu();
                Toast.makeText(StartActivity.this, "Nevermind!", Toast.LENGTH_SHORT).show();
            }
        };
        Command refreshCommand = new Command() {
            @Override
            public void execute(View view, Object data) {
                qaMgr.destroyQuickActionMenu();
                Toast.makeText(StartActivity.this, "Refresh", Toast.LENGTH_SHORT).show();
            }
        };

        qaMgr.initializeQuickActionMenu(new OnViewClickListenerWrapper(searchGoCommand),
                new OnViewClickListenerWrapper(searchCancelCommand),
                new OnViewClickListenerWrapper(refreshCommand));
        super.onResume();
    }

    class DoYouTubeSearch extends AsyncTask<Void, Void, Void> {

        private Context ctx;
        private String searchString;
        VideoFeed feed;

        DoYouTubeSearch(Context ctx, String searchString) {
            this.ctx = ctx;
            this.searchString = searchString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((VideoAdapter) getListAdapter()).setData(feed.items);
            super.onPostExecute(aVoid);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonCParser parser = new JsonCParser();
            parser.jsonFactory = new JacksonFactory();
            GoogleHeaders headers = new GoogleHeaders();
            headers.setApplicationName("HackathonVJ/1.0");
            headers.gdataVersion = "2";
            HttpTransport transports = new NetHttpTransport();
            transports.defaultHeaders = headers;
            transports.addParser(parser);
            YouTubeUrl url = new YouTubeUrl("https://gdata.youtube.com/feeds/api/videos");
            url.orderby = "viewCount";
            url.q = searchString;
            url.safeSearch = "none";
            url.alt = "jsonc";

            com.google.api.client.http.HttpRequest request = transports.buildGetRequest();
            request.url = url;
            try {
//                Log.i("HackersVJ", request.execute().parseAsString());

                feed = request.execute().parseAs(VideoFeed.class);
                if (feed.items != null) {
                    long internalid = 0;
                    for (Video vid : feed.items) {
                        Log.i("HackersVJ", vid.title + "||" + vid.thumbnail.sqDefault + "||" + vid.player.defaultUrl);
                        vid.internalid = internalid++;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class VideoFeed {
        @Key ArrayList<Video> items;
    }

    public static class Video {
        @Key("id") String videoid;
        @Key String title;
        @Key String description;
        @Key Thumbnail thumbnail;
        @Key Player player;
        @Key String uploader;

        long internalid;
    }

    public static class Player {
        @Key("default") String defaultUrl;
    }

    public static class Thumbnail {
        @Key("sqDefault") String sqDefault;
        @Key("hqDefault") String hqDefault;
    }

    public static class YouTubeUrl extends GoogleUrl {
        //        @Key public String alt = "jsonc";
        @Key public String orderby;
        @Key public String q;
        @Key public String safeSearch;

        YouTubeUrl(String url) {
            super(url);
        }
    }


    class VideoAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ArrayList<Video> videos;

        public VideoAdapter(Context ctx) {
            inflater = LayoutInflater.from(ctx);
        }

        public void setData(ArrayList<Video> vids) {
            this.videos = vids;
            notifyDataSetChanged();
        }

        private Drawable downloadThumbnailFromURL(URL url) {
            Drawable image = null;
            HttpURLConnection cn = null;
            InputStream is = null;
            try {
                cn =(HttpURLConnection) url.openConnection();
                is = new BufferedInputStream(cn.getInputStream(), 8192);
                image = Drawable.createFromStream(is, "thumbnail");

            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return image;
        }
        @Override
        public int getCount() {
            if (videos == null) {
                return 0;
            }
            return videos.size();
        }

        @Override
        public Object getItem(int position) {
            return videos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return videos.get(position).internalid;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            VideoViewHolder holder;
            Video vid = videos.get(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.playlist_item_layout, null);
                holder = new VideoViewHolder();
                holder.thumbnailIV = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                holder.titleTV = (TextView) convertView.findViewById(R.id.video_title);
                holder.title2TV = (TextView) convertView.findViewById(R.id.video_title_line_2);
                convertView.setTag(holder);
            } else {
                holder = (VideoViewHolder) convertView.getTag();
            }
            holder.id = vid.internalid;
            holder.videoid = vid.videoid;
            holder.titleTV.setText(vid.title);
            holder.title2TV.setText(vid.uploader);
            try {
                holder.thumbnailIV.setImageDrawable(downloadThumbnailFromURL(new URL(vid.thumbnail.sqDefault)));
            } catch (MalformedURLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            //TODO: set image view

            return convertView;
        }
    }

    class VideoViewHolder {
        public long id;
        public ImageView thumbnailIV;
        public TextView titleTV;
        public TextView title2TV;
        public String videoid;
    }
}