package android.support.p003v4.widget;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.widget.ListView;

@TargetApi(19)
@RequiresApi(19)
/* renamed from: android.support.v4.widget.ListViewCompatKitKat */
class ListViewCompatKitKat {
    ListViewCompatKitKat() {
    }

    static void scrollListBy(ListView listView, int y) {
        listView.scrollListBy(y);
    }
}
