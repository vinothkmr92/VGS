package android.support.p003v4.media.session;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

@TargetApi(24)
@RequiresApi(24)
/* renamed from: android.support.v4.media.session.MediaControllerCompatApi24 */
class MediaControllerCompatApi24 {

    /* renamed from: android.support.v4.media.session.MediaControllerCompatApi24$TransportControls */
    public static class TransportControls extends android.support.p003v4.media.session.MediaControllerCompatApi23.TransportControls {
        public static void prepare(Object controlsObj) {
            ((android.media.session.MediaController.TransportControls) controlsObj).prepare();
        }

        public static void prepareFromMediaId(Object controlsObj, String mediaId, Bundle extras) {
            ((android.media.session.MediaController.TransportControls) controlsObj).prepareFromMediaId(mediaId, extras);
        }

        public static void prepareFromSearch(Object controlsObj, String query, Bundle extras) {
            ((android.media.session.MediaController.TransportControls) controlsObj).prepareFromSearch(query, extras);
        }

        public static void prepareFromUri(Object controlsObj, Uri uri, Bundle extras) {
            ((android.media.session.MediaController.TransportControls) controlsObj).prepareFromUri(uri, extras);
        }
    }

    MediaControllerCompatApi24() {
    }
}
