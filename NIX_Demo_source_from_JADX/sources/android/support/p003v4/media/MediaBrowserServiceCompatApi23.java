package android.support.p003v4.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.browse.MediaBrowser.MediaItem;
import android.os.Parcel;
import android.service.media.MediaBrowserService.Result;
import android.support.annotation.RequiresApi;

@TargetApi(23)
@RequiresApi(23)
/* renamed from: android.support.v4.media.MediaBrowserServiceCompatApi23 */
class MediaBrowserServiceCompatApi23 {

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompatApi23$MediaBrowserServiceAdaptor */
    static class MediaBrowserServiceAdaptor extends MediaBrowserServiceAdaptor {
        MediaBrowserServiceAdaptor(Context context, ServiceCompatProxy serviceWrapper) {
            super(context, serviceWrapper);
        }

        public void onLoadItem(String itemId, Result<MediaItem> result) {
            ((ServiceCompatProxy) this.mServiceProxy).onLoadItem(itemId, new ResultWrapper(result));
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompatApi23$ServiceCompatProxy */
    public interface ServiceCompatProxy extends android.support.p003v4.media.MediaBrowserServiceCompatApi21.ServiceCompatProxy {
        void onLoadItem(String str, ResultWrapper<Parcel> resultWrapper);
    }

    MediaBrowserServiceCompatApi23() {
    }

    public static Object createService(Context context, ServiceCompatProxy serviceProxy) {
        return new MediaBrowserServiceAdaptor(context, serviceProxy);
    }
}
