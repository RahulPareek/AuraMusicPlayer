package reverblabs.apps.aura.interfaces;

import android.support.v4.media.session.PlaybackStateCompat;

public interface PlaybackManagerCallback {
    void onPrepared();
    void onPause();
    void onPlay();
}
