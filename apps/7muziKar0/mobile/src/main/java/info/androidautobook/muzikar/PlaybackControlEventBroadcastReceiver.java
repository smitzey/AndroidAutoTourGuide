package info.androidautobook.muzikar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.util.Log;


/**
 * Android Auto Tour Guide / muzikar0
 *
 * PlaybackControlEventBroadcastReceiver
 */
public class PlaybackControlEventBroadcastReceiver extends BroadcastReceiver {

    private final MuziKarMusicService muziKarMusicService ;

    private final NotificationManager notificationManager;
    private  Notification.Action playAction;
    private  Notification.Action pauseAction;
    private  Notification.Action previousAction;
    private  Notification.Action nextAction;

    private boolean runningFlag ;

    public PlaybackControlEventBroadcastReceiver(MuziKarMusicService service) {
        muziKarMusicService = service;

        initIntents() ;
        notificationManager = (NotificationManager) muziKarMusicService.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void initIntents () {
        PendingIntent playIntent = PendingIntent.getBroadcast(muziKarMusicService, REQUEST_CODE,
                new Intent(ACTION_PLAY).setPackage(muziKarMusicService.getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pauseIntent = PendingIntent.getBroadcast(muziKarMusicService, REQUEST_CODE,
                new Intent(ACTION_PAUSE).setPackage(muziKarMusicService.getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent nextIntent = PendingIntent.getBroadcast(muziKarMusicService, REQUEST_CODE,
                new Intent(ACTION_NEXT).setPackage(muziKarMusicService.getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent prevIntent = PendingIntent.getBroadcast(muziKarMusicService, REQUEST_CODE,
                new Intent(ACTION_PREVIOUS).setPackage(muziKarMusicService.getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT);

        playAction = new Notification.Action(R.drawable.ic_play_arrow_white_18dp,"Play", playIntent);
        pauseAction = new Notification.Action(R.drawable.ic_pause_circle_outline_white_18dp, "Pause", pauseIntent);
        previousAction = new Notification.Action(R.drawable.ic_skip_previous_white_18dp,"Prev", prevIntent);
        nextAction = new Notification.Action(R.drawable.ic_skip_next_white_18dp,"Next", nextIntent);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PREVIOUS);

        muziKarMusicService.registerReceiver(this, filter);
    }

    public void update(MediaMetadata metadata, PlaybackState state, MediaSession.Token token) {
        if (state == null || state.getState() == PlaybackState.STATE_STOPPED ||
                state.getState() == PlaybackState.STATE_NONE) {
            muziKarMusicService.stopForeground(true);
            try {
                muziKarMusicService.unregisterReceiver(this);
            } catch (IllegalArgumentException ex) {
                Log.w ( TAG, "update metadata=" + metadata) ;
            }
            muziKarMusicService.stopSelf();
            return;
        }
        if (metadata == null) {
            return;
        }
        boolean isPlaying = state.getState() == PlaybackState.STATE_PLAYING;
        Notification.Builder notificationBuilder = new Notification.Builder(muziKarMusicService);
        MediaDescription description = metadata.getDescription();

        notificationBuilder
                .setContentTitle(description.getTitle())
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(isPlaying)
                .setWhen(isPlaying ? System.currentTimeMillis() - state.getPosition() : 0)
                .setShowWhen(isPlaying)
                .setUsesChronometer(isPlaying)
                .setContentText(description.getSubtitle())
                .setLargeIcon(LocalMusicSource.getAlbumBitmap(muziKarMusicService, description.getMediaId()))
                .setStyle(new Notification.MediaStyle().setMediaSession(token).setShowActionsInCompactView(0, 1, 2))
                .setColor(muziKarMusicService.getApplication().getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.art);

        if ((state.getActions() & PlaybackState.ACTION_SKIP_TO_PREVIOUS) != 0) {
            notificationBuilder.addAction(previousAction);
        }

        notificationBuilder.addAction(isPlaying ? pauseAction : playAction);
        if ((state.getActions() & PlaybackState.ACTION_SKIP_TO_NEXT) != 0) {
            notificationBuilder.addAction(nextAction);
        }

        Notification notification = notificationBuilder.build();

        if (isPlaying && !runningFlag) {
            muziKarMusicService.startService(new Intent(muziKarMusicService.getApplicationContext(), MuziKarMusicService.class));
            muziKarMusicService.startForeground(NOTIFICATION_ID, notification);
            runningFlag = true;
        } else {
            if (!isPlaying) {
                muziKarMusicService.stopForeground(false);
                runningFlag = false;
            }
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case ACTION_PLAY:
                muziKarMusicService.mediaSessionCallback.onPause();
                break;
            case ACTION_PAUSE:
                muziKarMusicService.mediaSessionCallback.onPlay();
                break;
            case ACTION_NEXT:
                muziKarMusicService.mediaSessionCallback.onSkipToNext();
                break;
            case ACTION_PREVIOUS:
                muziKarMusicService.mediaSessionCallback.onSkipToPrevious();
                break;
        }

    }


    private static final String ACTION_PLAY  = "info.androidautobook.muzikar.play" ;
    private static final String ACTION_PAUSE  = "info.androidautobook.muzikar.pause" ;
    private static final String ACTION_PREVIOUS  = "info.androidautobook.muzikar.previous" ;
    private static final String ACTION_NEXT  = "info.androidautobook.muzikar.next" ;

    private static final int NOTIFICATION_ID = 987654 ;
    private static final int REQUEST_CODE = 101 ;

    private static final String TAG = PlaybackControlEventBroadcastReceiver.class.getName() ;
}
