/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.autoapp.helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.example.autoapp.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class MusicLibrary {

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static final HashMap<String, Integer> favourites = new HashMap<>();


    public static String getRoot() {
        return "root";
    }

    public static String getSongUri(String mediaId) {
        return getMusicRes(mediaId);
    }


    /**
     * Returns the music data to be played
     *
     * @param mediaId
     * @return
     */
    private static String getMusicRes(String mediaId) {
        return music.containsKey(mediaId) ? music.get(mediaId).getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI) : null;
    }

    /**
     * Returns the Uri of the album art as String
     *
     * @param mediaId
     * @return
     */
    private static String getAlbumRes(String mediaId) {
        return music.containsKey(mediaId) ? music.get(mediaId).getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) : null;
    }

    /**
     * Creates album art bitmap from the Uri or sets a default bitmap if URI doesnt exist
     *
     * @param ctx
     * @param mediaId
     * @return
     */
    public static Bitmap getAlbumBitmap(Context ctx, String mediaId) {
        Bitmap bitmap = null;
        try {

            Uri albumUri = Uri.parse(getAlbumRes(mediaId));
            bitmap = MediaStore.Images.Media.getBitmap(
                    ctx.getContentResolver(), albumUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            bitmap = BitmapFactory.decodeResource(ctx.getResources(),
                    R.drawable.album_cover_default);
        } catch (IOException e) {

            e.printStackTrace();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            bitmap = BitmapFactory.decodeResource(ctx.getResources(),
                    R.drawable.album_cover_default);
        }
        return bitmap;
    }

    /**
     * switches the value of media id to 1 or 0 depending on current state.
     * 1 - favourite , 0 - non favourite
     *
     * @param mediaID
     * @return
     */
    public static int toggleFavourite(String mediaID) {
        if (favourites.containsKey(mediaID) && favourites.get(mediaID) != null) {
            if (favourites.get(mediaID) == 0) {
                favourites.put(mediaID, 1);
                return R.drawable.ic_favorite_white_24dp;
            } else {
                favourites.put(mediaID, 0);
                return R.drawable.ic_favorite_border_white_24dp;
            }
        } else {
            favourites.put(mediaID, 0);
            return R.drawable.ic_favorite_border_white_24dp;
        }
    }

    /**
     * Returns the bitmap according to favourite status of the media item
     *
     * @param mediaID
     * @return
     */
    public static int getFavouriteBitmap(String mediaID) {
        if (favourites.containsKey(mediaID) && favourites.get(mediaID) != null) {

            return favourites.get(mediaID) == 1 ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp;
        } else {
            return R.drawable.ic_favorite_border_white_24dp;
        }
    }

    public static List<MediaBrowserCompat.MediaItem> getFavouriteMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (String mediaID : favourites.keySet()) {
            if (favourites.get(mediaID) == 1 && music.containsKey(mediaID)) {
                result.add(
                        new MediaBrowserCompat.MediaItem(
                                music.get(mediaID).getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            }
        }

        return result;
    }

    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    public static String getPreviousSong(String currentMediaId) {
        if (currentMediaId == null) {
            currentMediaId = music.firstKey();
        }
        String prevMediaId = music.lowerKey(currentMediaId);
        if (prevMediaId == null) {
            prevMediaId = music.firstKey();
        }
        return prevMediaId;
    }

    public static String getNextSong(String currentMediaId) {

        if (currentMediaId == null) {
            currentMediaId = music.firstKey();
        }
        String nextMediaId = music.higherKey(currentMediaId);
        if (nextMediaId == null) {
            nextMediaId = music.firstKey();
        }
        return nextMediaId;
    }

    public static MediaMetadataCompat getMetadata(Context ctx, String mediaId) {
        MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);
        Bitmap albumArt = getAlbumBitmap(ctx, mediaId);

        // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
        // We don't set it initially on all items so that they don't take unnecessary memory.
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key :
                new String[]{
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_GENRE,
                        MediaMetadataCompat.METADATA_KEY_TITLE
                }) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        return builder.build();
    }

    /**
     * Creates Metadata object from songs fetched from external storage. Objects are added to a treemap and sorted according
     * to its key value. Title is therefore set as MediaID which is the key value and hence the list will be sorted in
     * ascending order.
     *
     * @param mediaId
     * @param title
     * @param artist
     * @param album
     * @param genre
     * @param duration
     * @param musicResId
     * @param albumArtResId
     */
    private static void createMediaMetadataCompat(
            String mediaId,
            String title,
            String artist,
            String album,
            String genre,
            long duration,
            String musicResId,
            String albumArtResId) {
        music.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration * 1000)
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, albumArtResId)
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, albumArtResId)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, musicResId)
                        .build());
        favourites.put(mediaId, 0);
    }

    /**
     * Loads music files from External Storage
     *
     * @param context
     */
    public static void loadAudio(Context context) throws IllegalStateException {

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
       // String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String selection = MediaStore.Audio.Media.DATA
                + " LIKE '%Music%'";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);


        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                // TODO: 17-04-2019 remove this log later
                for (String column :
                        cursor.getColumnNames()) {
                    Log.e("loadAudio cursor fields", column);
                }
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String genre = null;
                try {
                    genre = cursor.getString(cursor.getColumnIndex("genre_name"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long duration = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                Long albumId = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                createMediaMetadataCompat(title, title, artist, album, genre, duration, data, albumArtUri.toString());

            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }


}
