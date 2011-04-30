package com.hackathon;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

public class StartActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_layout);
    }



    class VideoAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ArrayList<YouTubeVideo> myVideos;

        public VideoAdapter(Context ctx) {
            inflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getCount() {
            return myVideos.size();
        }

        @Override
        public Object getItem(int position) {
            return myVideos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return myVideos.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            VideoViewHolder holder;
            YouTubeVideo vid = myVideos.get(position);

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
            holder.id = vid.getId();
            holder.titleTV.setText(vid.getVideoTitle());
            holder.title2TV.setText(vid.getVideoTitle2());
            //TODO: set image view

            return convertView;
        }
    }

    class VideoViewHolder {
        public long id;
        public ImageView thumbnailIV;
        public TextView titleTV;
        public TextView title2TV;
        
    }
}