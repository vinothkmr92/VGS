package android.support.p003v4.media.session;

import android.annotation.TargetApi;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;

@TargetApi(24)
@RequiresApi(24)
/* renamed from: android.support.v4.media.session.MediaSessionCompatApi24 */
class MediaSessionCompatApi24 {
    private static final String TAG = "MediaSessionCompatApi24";

    /* renamed from: android.support.v4.media.session.MediaSessionCompatApi24$Callback */
    public interface Callback extends android.support.p003v4.media.session.MediaSessionCompatApi23.Callback {
        void onPrepare();

        void onPrepareFromMediaId(String str, Bundle bundle);

        void onPrepareFromSearch(String str, Bundle bundle);

        void onPrepareFromUri(Uri uri, Bundle bundle);
    }

    /* renamed from: android.support.v4.media.session.MediaSessionCompatApi24$CallbackProxy */
    static class CallbackProxy<T extends Callback> extends CallbackProxy<T> {
        public CallbackProxy(T callback) {
            super(callback);
        }

        public void onPrepare() {
            ((Callback) this.mCallback).onPrepare();
        }

        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            ((Callback) this.mCallback).onPrepareFromMediaId(mediaId, extras);
        }

        public void onPrepareFromSearch(String query, Bundle extras) {
            ((Callback) this.mCallback).onPrepareFromSearch(query, extras);
        }

        public void onPrepareFromUri(Uri uri, Bundle extras) {
            ((Callback) this.mCallback).onPrepareFromUri(uri, extras);
        }
    }

    MediaSessionCompatApi24() {
    }

    public static Object createCallback(Callback callback) {
        return new CallbackProxy(callback);
    }

    public static String getCallingPackage(Object sessionObj) {
        MediaSession session = (MediaSession) sessionObj;
        try {
            return (String) session.getClass().getMethod("getCallingPackage", new Class[0]).invoke(session, new Object[0]);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.e(TAG, "Cannot execute MediaSession.getCallingPackage()", e);
            return null;
        }
    }
}
