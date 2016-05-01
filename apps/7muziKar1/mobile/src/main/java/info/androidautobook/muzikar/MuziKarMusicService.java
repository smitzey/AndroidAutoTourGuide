package info.androidautobook.muzikar;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.media.MediaBrowserService;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a MediaBrowser through a service. It exposes the media library to a browsing
 * client, through the onGetRoot and onLoadChildren methods. It also creates a MediaSession and
 * exposes it through its MediaSession.Token, which allows the client to create a MediaController
 * that connects to and send control commands to the MediaSession remotely. This is useful for
 * user interfaces that need to interact with your media session, like Android Auto. You can
 * (should) also use the same service from your app's UI, which gives a seamless playback
 * experience to the user.
 * <p/>
 * To implement a MediaBrowserService, you need to:
 * <p/>
 * <ul>
 * <p/>
 * <li> Extend {@link android.service.media.MediaBrowserService}, implementing the media browsing
 * related methods {@link android.service.media.MediaBrowserService#onGetRoot} and
 * {@link android.service.media.MediaBrowserService#onLoadChildren};
 * <li> In onCreate, start a new {@link android.media.session.MediaSession} and notify its parent
 * with the session's token {@link android.service.media.MediaBrowserService#setSessionToken};
 * <p/>
 * <li> Set a callback on the
 * {@link android.media.session.MediaSession#setCallback(android.media.session.MediaSession.Callback)}.
 * The callback will receive all the user's actions, like play, pause, etc;
 * <p/>
 * <li> Handle all the actual music playing using any method your app prefers (for example,
 * {@link android.media.MediaPlayer})
 * <p/>
 * <li> Update playbackState, "now playing" metadata and queue, using MediaSession proper methods
 * {@link android.media.session.MediaSession#setPlaybackState(android.media.session.PlaybackState)}
 * {@link android.media.session.MediaSession#setMetadata(android.media.MediaMetadata)} and
 * {@link android.media.session.MediaSession#setQueue(java.util.List)})
 * <p/>
 * <li> Declare and export the service in AndroidManifest with an intent receiver for the action
 * android.media.browse.MediaBrowserService
 * <p/>
 * </ul>
 * <p/>
 * To make your app compatible with Android Auto, you also need to:
 * <p/>
 * <ul>
 * <p/>
 * <li> Declare a meta-data tag in AndroidManifest.xml linking to a xml resource
 * with a &lt;automotiveApp&gt; root element. For a media app, this must include
 * an &lt;uses name="media"/&gt; element as a child.
 * For example, in AndroidManifest.xml:
 * &lt;meta-data android:name="com.google.android.gms.car.application"
 * android:resource="@xml/automotive_app_desc"/&gt;
 * And in res/values/automotive_app_desc.xml:
 * &lt;automotiveApp&gt;
 * &lt;uses name="media"/&gt;
 * &lt;/automotiveApp&gt;
 * <p/>
 * </ul>
 *
 * @see <a href="README.md">README.md</a> for more details.
 */
public class MuziKarMusicService extends MediaBrowserService {

    private MediaSession mediaSession;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSession(this, "MuziKarMusicService");
        setSessionToken(mediaSession.getSessionToken());
        mediaSession.setCallback(new MediaSessionCallback());
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
    }

    @Override
    public void onDestroy() {
        mediaSession.release();
    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {

        Log.d(TAG, "onGetRoot() clientPackageName|clientUid=" + clientPackageName + " | " + clientUid);
        //if (!isValid(clientPackageName, clientUid)) {
        //    return null;
        //}


        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(final String parentMediaId, final Result<List<MediaItem>> result) {
        result.sendResult(LocalMusicSource.getMediaItems());
    }

    private final class MediaSessionCallback extends MediaSession.Callback {
        @Override
        public void onPlay() {
            Log.d(TAG, "onPlay()");

            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                Log.d(TAG, "onPlay() mediaPlayer created= " + mediaPlayer);
            }

            try {

                mediaPlayer.reset();

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnCompletionListener(
                        new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                Log.d(TAG, "onCompletion mediaPlayer=" + mp);
                                mp.stop();

                            }
                        }
                );
                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(LocalMusicSource.getSongUri("1")));
                // mediaPlayer.prepareAsync(); needs asynclistener
                mediaPlayer.prepare();
                mediaSession.setActive(true);
                mediaSession.setMetadata(LocalMusicSource.getMediaMetaData(getApplicationContext(), "1"));
                mediaPlayer.start();


            } catch (Exception e) {
                Log.e(TAG, "onPlay exception e=" + e);
            }


        }

        @Override
        public void onSkipToQueueItem(long queueId) {
        }

        @Override
        public void onSeekTo(long position) {
        }


        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Log.d(TAG, "onPlayFromMediaId() mediaId=" + mediaId);


            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

                mediaPlayer.setOnCompletionListener(
                        new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                Log.d(TAG, "onCompletion() mediaPlayer=" + mp);
                                mp.stop();

                            }
                        }

                );
            }


            try {
                mediaPlayer.setDataSource(getApplicationContext(),
                        Uri.parse(LocalMusicSource.getSongUri(mediaId)));
                mediaPlayer.prepare();

                Log.d(TAG, "onPlayFromMediaId() about to start()");
                mediaPlayer.start();
                Log.d(TAG, "onPlayFromMediaId() start()-ed");

            } catch (IOException e) {
                Log.e(TAG, "play() exception e=" + e);
            }



     }







/*        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
                Log.d ( TAG, "onPlayFromMediaId mediaId=" + mediaId

                ) ;

                if ( mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer() ;
                    Log.d(TAG, "onPlayFromMediaId mediaPlayer created= " + mediaPlayer) ;
                }

                try {

                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setOnCompletionListener(
                            new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    Log.d (TAG, "onCompletion() mediaPlayer=" + mp) ;
                                    mp.stop();

                                }
                            }
                    );
                    mediaPlayer.setWakeMode( getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                    mediaPlayer.setDataSource( getApplicationContext(), Uri.parse( LocalMusicSource.getSongUri( mediaId)));

                    mediaPlayer.prepare();
                    mediaSession.setActive( true);
                    mediaSession.setMetadata( LocalMusicSource.getMediaMetaData( getApplicationContext(), mediaId));
                    mediaPlayer.start();


                } catch ( Exception e) {
                    Log.e ( TAG, "onPlay exception e=" + e) ;
                }

            }*/

        @Override
        public void onPause() {
            if ( mediaPlayer != null ) {
                if ( mediaPlayer.isPlaying())
                    mediaPlayer.pause();
            }
        }

        @Override
        public void onStop() {
            if  ( mediaPlayer != null ) {
                if ( mediaPlayer.isPlaying() )
                    mediaPlayer.stop();
            }
        }

        @Override
        public void onSkipToNext() {
        }

        @Override
        public void onSkipToPrevious() {
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
        }

        @Override
        public void onPlayFromSearch(final String query, final Bundle extras) {
        }
    }

    public static  final  String TAG = MuziKarMusicService.class.getName() ;
}
