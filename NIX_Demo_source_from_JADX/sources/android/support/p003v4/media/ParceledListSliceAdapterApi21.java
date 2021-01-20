package android.support.p003v4.media;

import android.annotation.TargetApi;
import android.media.browse.MediaBrowser.MediaItem;
import android.support.annotation.RequiresApi;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@TargetApi(21)
@RequiresApi(21)
/* renamed from: android.support.v4.media.ParceledListSliceAdapterApi21 */
class ParceledListSliceAdapterApi21 {
    private static Constructor sConstructor;

    ParceledListSliceAdapterApi21() {
    }

    static {
        try {
            sConstructor = Class.forName("android.content.pm.ParceledListSlice").getConstructor(new Class[]{List.class});
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    static Object newInstance(List<MediaItem> itemList) {
        Object result = null;
        try {
            return sConstructor.newInstance(new Object[]{itemList});
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return result;
        }
    }
}
