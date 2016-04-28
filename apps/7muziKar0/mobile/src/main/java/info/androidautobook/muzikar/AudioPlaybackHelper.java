package info.androidautobook.muzikar;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;


/**
 * Android Auto Tour Guide / muzikar0
 *
 * AudioPlaybackHelper
 */

public class AudioPlaybackHelper implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer mediaPlayer;
    private final AudioManager audioManager;
    private volatile MediaMetadata currentMediaMetadata;
    private int playbackState;
    private boolean playOnFocusGainFlag;
    private final Context context;
    private final PlayStateChangeCallback playStateChangeCallback;

    public AudioPlaybackHelper(Context context, PlayStateChangeCallback playStateChangeCallback) {

        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.playStateChangeCallback = playStateChangeCallback;
        Log.d ( TAG, "PlaybackManager() constructed..." + toString() ) ;
    }

    public void play(MediaMetadata metadata) {
        Log.d ( TAG, "play()..." ) ;
        String mediaId = metadata.getDescription().getMediaId();
        boolean mediaChanged = (currentMediaMetadata == null || !getCurrentMediaId().equals(mediaId));

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setWakeMode(context.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnCompletionListener(this);
        } else {
            if (mediaChanged) {
                mediaPlayer.reset();
            }
        }

        if (mediaChanged) {
            currentMediaMetadata = metadata;
            try {
                mediaPlayer.setDataSource(context,
                        Uri.parse(LocalMusicSource.getSongUri(mediaId)));
                mediaPlayer.prepare();
            } catch (IOException e) {
                Log.e ( TAG, "play() exception e=" + e) ;
            }
        }

        if (requestAudioFocus()) {
            playOnFocusGainFlag = false;
            mediaPlayer.start();
            playbackState = PlaybackState.STATE_PLAYING;
            updatePlaybackState();
        } else {
            playOnFocusGainFlag = true;
        }
    }

    public void pause() {
        Log.d ( TAG, "pause..." ) ;
        if (isPlaying()) {
            mediaPlayer.pause();
            audioManager.abandonAudioFocus(this);
        }
        playbackState = PlaybackState.STATE_PAUSED;
        updatePlaybackState();
    }

    public void stop() {
        Log.d ( TAG, "stop.." ) ;
        playbackState = PlaybackState.STATE_STOPPED;
        updatePlaybackState();
        audioManager.abandonAudioFocus(this);

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }

    private boolean requestAudioFocus() {
        boolean retVal = false ;
        int result = audioManager.requestAudioFocus( this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if ( result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            retVal = true ;
        }

        return retVal ;
    }

    private void updatePlaybackState() {
        if (playStateChangeCallback == null) {
            return;
        }
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
                .setActions(availableActions());

        stateBuilder.setState(playbackState, getCurrentStreamPosition(), 1.0f, SystemClock.elapsedRealtime());
        playStateChangeCallback.onPlaybackStatusChanged(stateBuilder.build());
    }

    private long availableActions() {
        Log.d ( TAG, "availableActions()...") ;
        long actions = PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackState.ACTION_SKIP_TO_NEXT  | PlaybackState.ACTION_SKIP_TO_PREVIOUS
                | PlaybackState.ACTION_SKIP_TO_NEXT |PlaybackState.ACTION_PLAY_FROM_SEARCH
                | PlaybackState.ACTION_PLAY_FROM_URI ;
        if (isPlaying()) {
            actions |= PlaybackState.ACTION_PAUSE;
        }
        return actions;
    }

    public MediaMetadata getCurrentMedia() {
        Log.d ( TAG, "getCurrentMedia()..." ) ;
        return currentMediaMetadata;
    }

    public String getCurrentMediaId() {
        String retVal = null ;

        if ( currentMediaMetadata != null ) {
            retVal = currentMediaMetadata.getDescription().getMediaId();
        }

        return retVal;
    }

    public int getCurrentStreamPosition() {
        Log.d ( TAG, "getCurrentStreamPosition()..." ) ;

        int retVal = 0 ;

        if ( mediaPlayer != null ) {
            retVal = mediaPlayer.getCurrentPosition() ;
        }

        return retVal ;
    }

    public boolean isPlaying() {
        boolean retVal = false  ;
        Log.d(TAG, "isPlaying()...") ;
        if ( mediaPlayer != null ) {
            if ( mediaPlayer.isPlaying() ) {
                retVal = true ;
            }
        }
        if ( playOnFocusGainFlag) {
            retVal = true;
        }
        return retVal ;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AudioPlaybackHelper{");
        sb.append("mediaPlayer=").append(mediaPlayer);
        sb.append(", audioManager=").append(audioManager);
        sb.append(", currentMediaMetadata=").append(currentMediaMetadata);
        sb.append(", playbackState=").append(playbackState);
        sb.append(", playOnFocusGainFlag=").append(playOnFocusGainFlag);
        sb.append(", context=").append(context);
        sb.append(", playStateChangeCallback=").append(playStateChangeCallback);
        sb.append('}');
        return sb.toString();
    }

    public interface PlayStateChangeCallback {

        void onPlaybackStatusChanged(PlaybackState state);
    }


    //////////////////////////// implements methods below

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d ( TAG, "onCompletion mediaplayer=" + mp ) ;
        stop() ;

    }

    /**
     * Called on the listener to notify it the audio focus for this listener has been changed.
     * The focusChange value indicates whether the focus was gained,
     * whether the focus was lost, and whether that loss is transient, or whether the new focus
     * holder will hold it for an unknown amount of time.
     * When losing focus, listeners can use the focus change information to decide what
     * behavior to adopt when losing focus. A music player could for instance elect to lower
     * the volume of its music stream (duck) for transient focus losses, and pause otherwise.
     *
     * @param focusChange the type of focus change, one of {@link AudioManager#AUDIOFOCUS_GAIN},
     *                    {@link AudioManager#AUDIOFOCUS_LOSS}, {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT}
     *                    and {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK}.
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d ( TAG, "onCompletion AUDIO_FOCUS=" ) ;

    }

    private static  final String TAG = AudioPlaybackHelper.class.getName() ;
}
