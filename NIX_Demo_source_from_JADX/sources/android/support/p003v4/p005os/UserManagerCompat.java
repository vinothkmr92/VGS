package android.support.p003v4.p005os;

import android.content.Context;

/* renamed from: android.support.v4.os.UserManagerCompat */
public class UserManagerCompat {
    private UserManagerCompat() {
    }

    public static boolean isUserUnlocked(Context context) {
        if (BuildCompat.isAtLeastN()) {
            return UserManagerCompatApi24.isUserUnlocked(context);
        }
        return true;
    }
}
