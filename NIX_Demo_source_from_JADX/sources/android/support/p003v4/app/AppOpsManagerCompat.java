package android.support.p003v4.app;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;

/* renamed from: android.support.v4.app.AppOpsManagerCompat */
public final class AppOpsManagerCompat {
    private static final AppOpsManagerImpl IMPL;
    public static final int MODE_ALLOWED = 0;
    public static final int MODE_DEFAULT = 3;
    public static final int MODE_IGNORED = 1;

    /* renamed from: android.support.v4.app.AppOpsManagerCompat$AppOpsManager23 */
    private static class AppOpsManager23 extends AppOpsManagerImpl {
        AppOpsManager23() {
        }

        public String permissionToOp(String permission) {
            return AppOpsManagerCompat23.permissionToOp(permission);
        }

        public int noteOp(Context context, String op, int uid, String packageName) {
            return AppOpsManagerCompat23.noteOp(context, op, uid, packageName);
        }

        public int noteProxyOp(Context context, String op, String proxiedPackageName) {
            return AppOpsManagerCompat23.noteProxyOp(context, op, proxiedPackageName);
        }
    }

    /* renamed from: android.support.v4.app.AppOpsManagerCompat$AppOpsManagerImpl */
    private static class AppOpsManagerImpl {
        AppOpsManagerImpl() {
        }

        public String permissionToOp(String permission) {
            return null;
        }

        public int noteOp(Context context, String op, int uid, String packageName) {
            return 1;
        }

        public int noteProxyOp(Context context, String op, String proxiedPackageName) {
            return 1;
        }
    }

    static {
        if (VERSION.SDK_INT >= 23) {
            IMPL = new AppOpsManager23();
        } else {
            IMPL = new AppOpsManagerImpl();
        }
    }

    private AppOpsManagerCompat() {
    }

    public static String permissionToOp(@NonNull String permission) {
        return IMPL.permissionToOp(permission);
    }

    public static int noteOp(@NonNull Context context, @NonNull String op, int uid, @NonNull String packageName) {
        return IMPL.noteOp(context, op, uid, packageName);
    }

    public static int noteProxyOp(@NonNull Context context, @NonNull String op, @NonNull String proxiedPackageName) {
        return IMPL.noteProxyOp(context, op, proxiedPackageName);
    }
}