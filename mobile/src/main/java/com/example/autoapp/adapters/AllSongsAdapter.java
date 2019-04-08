package com.example.autoapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.session.PlaybackState;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.autoapp.R;
import com.example.autoapp.helpers.MusicLibrary;

import java.util.ArrayList;
import java.util.List;

import static com.example.autoapp.adapters.MediaItemViewHolder.STATE_PAUSED;
import static com.example.autoapp.adapters.MediaItemViewHolder.STATE_PLAYABLE;
import static com.example.autoapp.adapters.MediaItemViewHolder.STATE_PLAYING;

public class AllSongsAdapter extends RecyclerView.Adapter<AllSongsAdapter.AllSongsViewholder>
{
    List<MediaBrowserCompat.MediaItem> mediaItems;
    PlaybackStateCompat mCurrentState;
    MediaMetadataCompat mCurrentMetadata;
    Context context;


    public AllSongsAdapter(List<MediaBrowserCompat.MediaItem> mediaItems,
                           PlaybackStateCompat mCurrentState, MediaMetadataCompat mCurrentMetadata, Context context) {
        this.mediaItems = mediaItems;
        this.mCurrentState = mCurrentState;
        this.mCurrentMetadata = mCurrentMetadata;
        this.context = context;
    }

    @NonNull
    @Override
    public AllSongsViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.allsongs_list_item_row, viewGroup, false);
        return new AllSongsViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllSongsViewholder allSongsViewholder, int position) {

        MediaBrowserCompat.MediaItem item = mediaItems.get(position);
        int itemState = MediaItemViewHolder.STATE_NONE;
        if (item.isPlayable()) {
            String itemMediaId = item.getDescription().getMediaId();
            int playbackState = PlaybackStateCompat.STATE_NONE;
            if (mCurrentState != null) {
                playbackState = mCurrentState.getState();
            }
            if (mCurrentMetadata != null
                    && itemMediaId.equals(mCurrentMetadata.getDescription().getMediaId())) {
                if (playbackState == PlaybackState.STATE_PLAYING
                        || playbackState == PlaybackState.STATE_BUFFERING) {
                    itemState = STATE_PLAYING;
                } else if (playbackState != PlaybackState.STATE_ERROR) {
                    itemState = STATE_PAUSED;
                }
            }else{
                itemState = STATE_PLAYABLE;
            }
        }

        MediaDescriptionCompat description = item.getDescription();
        allSongsViewholder.tv_trackName.setText(description.getTitle());
        allSongsViewholder.tv_trackDesc.setText(description.getSubtitle());
        allSongsViewholder.iv_songArt.setImageBitmap(MusicLibrary.getAlbumBitmap(
                context, description.getMediaId()));

        switch (itemState){

            case STATE_PLAYING:
            case STATE_PAUSED:
                allSongsViewholder.tv_trackName.setTextColor(Color.WHITE);
                allSongsViewholder.tv_trackDesc.setTextColor(Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    allSongsViewholder.cv_container.setBackgroundColor(context.getColor(R.color.colorAccent));
                }else{
                    allSongsViewholder.cv_container.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }
                break;

            case STATE_PLAYABLE:
            default:
                allSongsViewholder.tv_trackName.setTextColor(Color.BLACK);
                allSongsViewholder.tv_trackDesc.setTextColor(Color.BLACK);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    allSongsViewholder.cv_container.setBackgroundColor(context.getColor(R.color.white));
                }else{
                    allSongsViewholder.cv_container.setBackgroundColor(context.getResources().getColor(R.color.white));
                }
        }
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    public void updatePlayback(PlaybackStateCompat mCurrentState,
            MediaMetadataCompat mCurrentMetadata){
        this.mCurrentMetadata = mCurrentMetadata;
        this.mCurrentState = mCurrentState;
        notifyDataSetChanged();
    }
    public class AllSongsViewholder extends RecyclerView.ViewHolder{

        private TextView tv_trackName,tv_trackDesc;
        private ImageView iv_songArt;
        private CardView cv_container;

        public AllSongsViewholder(@NonNull View itemView) {
            super(itemView);
            tv_trackName = itemView.findViewById(R.id.tv_trackname);
            tv_trackDesc = itemView.findViewById(R.id.tv_trackdesc);
            iv_songArt = itemView.findViewById(R.id.iv_song_art);
            cv_container = itemView.findViewById(R.id.cv_song_container);

        }
    }

}
