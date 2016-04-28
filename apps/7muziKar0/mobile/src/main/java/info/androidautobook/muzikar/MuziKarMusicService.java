package info.androidautobook.muzikar;

/**
* Android Auto Tour Guide / muzikar0
*
* MuzikarMusicService
*/
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.Rating;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.service.media.MediaBrowserService;
import android.util.Log;

import java.util.List;

public class MuziKarMusicService extends MediaBrowserService {

    private MediaSession mediaSession;
    private PlaybackControlEventBroadcastReceiver playbackControlEventBroadcastReceiver;
    private AudioPlaybackHelper audioPlaybackHelper;
    private CarConnectStatusBroadcastReceiver carConnectStatusBroadcastReceiver ;

    final MediaSession.Callback mediaSessionCallback = new MediaSession.Callback() {
        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Log.d(TAG, "MediaSession.Callback onPlayFromMediaId() mediaId=" + mediaId) ;
            mediaSession.setActive(true);
            //mediaSession.setPlaybackState();
            MediaMetadata mediaMetadata = LocalMusicSource.getMediaMetaData(getApplicationContext(), mediaId);
            mediaSession.setMetadata(mediaMetadata);
            audioPlaybackHelper.play(mediaMetadata);
        }

        @Override
        public void onPlay() {
            Log.d (TAG, "MediaSession.Callback onPlay" ) ;
            if (audioPlaybackHelper.getCurrentMediaId() != null) {
                onPlayFromMediaId(audioPlaybackHelper.getCurrentMediaId(), null);
            }
        }

        @Override
        public void onPause() {
            Log.d (TAG, "MediaSession.Callback onPause" ) ;
            audioPlaybackHelper.pause();
        }

        @Override
        public void onStop() {

            Log.d (TAG, "MediaSession.Callback onStop" ) ;
            stopSelf();
        }

        @Override
        public void onSkipToNext() {
            Log.d (TAG, "MediaSession.Callback onSkipToNext()" ) ;
            onPlayFromMediaId(LocalMusicSource.getNextSongMediaId(audioPlaybackHelper.getCurrentMediaId()), null);
        }

        @Override
        public void onSkipToPrevious() {
            Log.d (TAG, "MediaSession.Callback onSkipToPrevious()" ) ;
            onPlayFromMediaId(LocalMusicSource.getPreviousSong(audioPlaybackHelper.getCurrentMediaId()), null);
        }

        @Override
        public void onCommand(String command, Bundle args, ResultReceiver cb) {
            super.onCommand(command, args, cb);
            Log.d (TAG, "MediaSession.Callback onCommand()=" + command ) ;
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
            Log.d (TAG, "MediaSession.Callback onMediaButtonEvent()=" + mediaButtonIntent ) ;
            return super.onMediaButtonEvent(mediaButtonIntent);

        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            super.onPlayFromSearch(query, extras);
            Log.d (TAG, "MediaSession.Callback onPlayFromSearch()=" + query ) ;
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            super.onPlayFromUri(uri, extras);
            Log.d (TAG, "MediaSession.Callback onPlayFromUri()=" + uri ) ;
        }

        @Override
        public void onSkipToQueueItem(long id) {
            super.onSkipToQueueItem(id);
            Log.d (TAG, "MediaSession.Callback onSkipToQueueItem()=" + id ) ;
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
        }

        @Override
        public void onRewind() {
            super.onRewind();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }

        @Override
        public void onSetRating(Rating rating) {
            super.onSetRating(rating);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
        }
    };

    @Override
    public void onCreate() {
        Log.d (TAG, "onCreate() " + this) ;
        super.onCreate();

        // Start a new MediaSession
        mediaSession = new MediaSession(this, "MuziKarMusicService");
        mediaSession.setCallback(mediaSessionCallback);
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mediaSession.getSessionToken());

        playbackControlEventBroadcastReceiver = new PlaybackControlEventBroadcastReceiver(this);

        audioPlaybackHelper = new AudioPlaybackHelper(getApplicationContext(), new AudioPlaybackHelper.PlayStateChangeCallback() {
            @Override
            public void onPlaybackStatusChanged(PlaybackState state) {
                mediaSession.setPlaybackState(state);
                playbackControlEventBroadcastReceiver.update(audioPlaybackHelper.getCurrentMedia(), state, getSessionToken());
            }
        });

        carConnectStatusBroadcastReceiver = new CarConnectStatusBroadcastReceiver( this ) ;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d (TAG, "onStartCommand()=" + intent + "|" +  flags + "|" + startId) ;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()") ;
        try {
           audioPlaybackHelper.stop();
            mediaSession.release();
        } catch ( Exception e) {
            Log.w ( TAG, "onDestroy() exception/warning =" + e) ;
        }

    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        Log.d (TAG, "onGetRoot()" ) ;
        return new BrowserRoot(LocalMusicSource.getRoot(), null);
    }

    @Override
    public void onLoadChildren(final String parentMediaId, final Result<List<MediaItem>> result) {
        Log.d (TAG, "onLoadChildren()" ) ;
        result.sendResult(LocalMusicSource.getMediaItems());
    }

    private static final String TAG = MuziKarMusicService.class.getName() ;
}