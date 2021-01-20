package android.support.p003v4.p005os;

import android.annotation.TargetApi;
import android.os.Parcelable.Creator;
import android.support.annotation.RequiresApi;

@TargetApi(13)
@RequiresApi(13)
/* renamed from: android.support.v4.os.ParcelableCompatCreatorHoneycombMR2Stub */
/* compiled from: ParcelableCompatHoneycombMR2 */
class ParcelableCompatCreatorHoneycombMR2Stub {
    ParcelableCompatCreatorHoneycombMR2Stub() {
    }

    static <T> Creator<T> instantiate(ParcelableCompatCreatorCallbacks<T> callbacks) {
        return new ParcelableCompatCreatorHoneycombMR2(callbacks);
    }
}
