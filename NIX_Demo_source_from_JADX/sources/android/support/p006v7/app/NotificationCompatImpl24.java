package android.support.p006v7.app;

import android.annotation.TargetApi;
import android.app.Notification.DecoratedCustomViewStyle;
import android.app.Notification.DecoratedMediaCustomViewStyle;
import android.support.annotation.RequiresApi;
import android.support.p003v4.app.NotificationBuilderWithBuilderAccessor;

@TargetApi(24)
@RequiresApi(24)
/* renamed from: android.support.v7.app.NotificationCompatImpl24 */
class NotificationCompatImpl24 {
    NotificationCompatImpl24() {
    }

    public static void addDecoratedCustomViewStyle(NotificationBuilderWithBuilderAccessor b) {
        b.getBuilder().setStyle(new DecoratedCustomViewStyle());
    }

    public static void addDecoratedMediaCustomViewStyle(NotificationBuilderWithBuilderAccessor b) {
        b.getBuilder().setStyle(new DecoratedMediaCustomViewStyle());
    }
}
