package info.androidautobook.muzikar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Android Auto Tour Guide / muzikar0
 *
 * LocalMusicSource
 */


class LocalMusicSource {
    private static final TreeMap<String, MediaMetadata> musicCollectionMap = new TreeMap<>();
    private static final String[] GENRE = {"FOLK", "CLASSICAL"} ;
    private static final String SONG_PREFIX = "android.resource://" + BuildConfig.APPLICATION_ID + "/" ;
    private static final String ART_PREFIX = "android.resource://" + BuildConfig.APPLICATION_ID + "/drawable/" ;

    static {
        init() ;
    }

    private static void init () {
        initMusicCollectionItem("1", "Thoughts are free","Andreas, Franz, Gunter & Thomas",
                GENRE[0], GENRE[0], 103  );

        initMusicCollectionItem("2",  "The Blue  Danube", "Johann Strauss II",
                GENRE[1], GENRE[1], 576 );

        initMusicCollectionItem("3","Prelude in C Major", "Johann Sebastian Bach",
                GENRE[1], GENRE[1] , 26 );
    }

    /**
     *
     * @return
     */
    public static String getRoot() {
        return "";
    }

    /**
     * Gets the usri for the song, included in app resources
     * @param mediaId
     * @return
     */

    public static String getSongUri(String mediaId) {
        String retVal =  SONG_PREFIX  + getMusicRawResource(mediaId);

        Log.d(TAG, "getSongUri retVal=" + retVal);

        return retVal;
    }

    private static String getAlbumArtUri(String mediaId) {
      return ART_PREFIX + getAlbumArtResource(mediaId);
    }

    private static int getMusicRawResource(String mediaId) {
        int retVal = R.raw.s3 ;

        int mediaIdNum = Integer.parseInt(mediaId) ;
        switch ( mediaIdNum) {
            case 1 :
                retVal = R.raw.s1 ;
                break;
            case 2 :
                retVal = R.raw.s2 ;
                break;

            case 3 :
                retVal = R.raw.s3 ;
                break;

        }
       return retVal ;
    }

    private static int getAlbumArtResource(String mediaId) {
        int retVal = R.drawable.art ;

        int mediaIdNum = Integer.parseInt(mediaId) ;
        switch ( mediaIdNum) {
            case 1 :
                retVal = R.drawable.s1 ;
                break;
            case 2 :
                retVal = R.drawable.s2 ;
                break;

            case 3 :
                retVal = R.drawable.s3 ;
                break;

        }
        return  retVal ;
    }



    /**
     *
     * @return
     */

    public static List<MediaBrowser.MediaItem> getMediaItems() {
        List<MediaBrowser.MediaItem> retVal = new ArrayList<MediaBrowser.MediaItem>()  ;

        MediaBrowser.MediaItem newMediaItem = null ;

        for (MediaMetadata aMetadata:  musicCollectionMap.values()) {
            newMediaItem = new MediaBrowser.MediaItem( aMetadata.getDescription(), MediaBrowser.MediaItem.FLAG_PLAYABLE) ;
            retVal.add( newMediaItem) ;
        }

        return retVal ;
    }

    /**
     * Obtains mediametadata associated with mediaId, after introducing album art bitmap
     * @param context
     * @param mediaId
     * @return
     */
    public static MediaMetadata getMediaMetaData (Context context, String mediaId) {

        MediaMetadata sourceMediaMetaData = musicCollectionMap.get(mediaId);
        Bitmap albumArt = getAlbumBitmap(context, mediaId);
        MediaMetadata retVal = new MediaMetadata.Builder( sourceMediaMetaData)
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, albumArt)
                .build() ;
        return retVal ;
    }


    /**
     * Gets the next song's mediaId
     * @param mediaId
     * @return
     */

    public static String getNextSongMediaId(String mediaId) {
        String retVal = musicCollectionMap.higherKey(mediaId);
        if (retVal == null) {
            retVal = musicCollectionMap.firstKey();
        }
        return retVal;
    }


    /**
     *  Gets the previous song's mediaId
     * @param mediaId
     * @return
     */
    public static String getPreviousSong(String mediaId) {
        String prevMediaId = musicCollectionMap.lowerKey(mediaId);
        if (prevMediaId == null) {
            prevMediaId = musicCollectionMap.firstKey();
        }
        return prevMediaId;
    }

    /**
     * Initializes a song item in the local music collection tree map
     *
     * @param mediaId
     * @param title
     * @param artist
     * @param album
     * @param genre
     * @param duration

     */
    private static void initMusicCollectionItem(String mediaId, String title, String artist, String album, String genre, long duration) {

        MediaMetadata aMediaMetaData = new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, duration * 1000)
                .putString(MediaMetadata.METADATA_KEY_GENRE, genre)
                .putString(MediaMetadata.METADATA_KEY_TITLE, title)
                .build() ;

        musicCollectionMap.put(mediaId,aMediaMetaData) ;
    }

    /**
     *
     * @param context
     * @param mediaId
     * @return
     */
    public static Bitmap getAlbumBitmap(Context context, String mediaId) {
        return BitmapFactory.decodeResource(context.getResources(), LocalMusicSource.getAlbumArtResource(mediaId));
    }

    private static final String TAG = LocalMusicSource.class.toString();
}