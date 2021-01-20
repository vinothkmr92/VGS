package android.support.p003v4.view;

import android.view.MenuItem;

/* renamed from: android.support.v4.view.MenuCompat */
public final class MenuCompat {
    @Deprecated
    public static void setShowAsAction(MenuItem item, int actionEnum) {
        MenuItemCompat.setShowAsAction(item, actionEnum);
    }

    private MenuCompat() {
    }
}
