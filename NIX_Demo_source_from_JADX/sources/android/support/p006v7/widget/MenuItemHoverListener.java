package android.support.p006v7.widget;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.p006v7.view.menu.MenuBuilder;
import android.view.MenuItem;

@RestrictTo({Scope.LIBRARY_GROUP})
/* renamed from: android.support.v7.widget.MenuItemHoverListener */
public interface MenuItemHoverListener {
    void onItemHoverEnter(@NonNull MenuBuilder menuBuilder, @NonNull MenuItem menuItem);

    void onItemHoverExit(@NonNull MenuBuilder menuBuilder, @NonNull MenuItem menuItem);
}
