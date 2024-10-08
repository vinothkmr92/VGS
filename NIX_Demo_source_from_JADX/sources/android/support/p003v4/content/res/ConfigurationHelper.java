package android.support.p003v4.content.res;

import android.content.res.Resources;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;

/* renamed from: android.support.v4.content.res.ConfigurationHelper */
public final class ConfigurationHelper {
    private static final ConfigurationHelperImpl IMPL;

    /* renamed from: android.support.v4.content.res.ConfigurationHelper$ConfigurationHelperImpl */
    private interface ConfigurationHelperImpl {
        int getDensityDpi(@NonNull Resources resources);

        int getScreenHeightDp(@NonNull Resources resources);

        int getScreenWidthDp(@NonNull Resources resources);

        int getSmallestScreenWidthDp(@NonNull Resources resources);
    }

    /* renamed from: android.support.v4.content.res.ConfigurationHelper$GingerbreadImpl */
    private static class GingerbreadImpl implements ConfigurationHelperImpl {
        GingerbreadImpl() {
        }

        public int getScreenHeightDp(@NonNull Resources resources) {
            return ConfigurationHelperGingerbread.getScreenHeightDp(resources);
        }

        public int getScreenWidthDp(@NonNull Resources resources) {
            return ConfigurationHelperGingerbread.getScreenWidthDp(resources);
        }

        public int getSmallestScreenWidthDp(@NonNull Resources resources) {
            return ConfigurationHelperGingerbread.getSmallestScreenWidthDp(resources);
        }

        public int getDensityDpi(@NonNull Resources resources) {
            return ConfigurationHelperGingerbread.getDensityDpi(resources);
        }
    }

    /* renamed from: android.support.v4.content.res.ConfigurationHelper$HoneycombMr2Impl */
    private static class HoneycombMr2Impl extends GingerbreadImpl {
        HoneycombMr2Impl() {
        }

        public int getScreenHeightDp(@NonNull Resources resources) {
            return ConfigurationHelperHoneycombMr2.getScreenHeightDp(resources);
        }

        public int getScreenWidthDp(@NonNull Resources resources) {
            return ConfigurationHelperHoneycombMr2.getScreenWidthDp(resources);
        }

        public int getSmallestScreenWidthDp(@NonNull Resources resources) {
            return ConfigurationHelperHoneycombMr2.getSmallestScreenWidthDp(resources);
        }
    }

    /* renamed from: android.support.v4.content.res.ConfigurationHelper$JellybeanMr1Impl */
    private static class JellybeanMr1Impl extends HoneycombMr2Impl {
        JellybeanMr1Impl() {
        }

        public int getDensityDpi(@NonNull Resources resources) {
            return ConfigurationHelperJellybeanMr1.getDensityDpi(resources);
        }
    }

    static {
        int sdk = VERSION.SDK_INT;
        if (sdk >= 17) {
            IMPL = new JellybeanMr1Impl();
        } else if (sdk >= 13) {
            IMPL = new HoneycombMr2Impl();
        } else {
            IMPL = new GingerbreadImpl();
        }
    }

    private ConfigurationHelper() {
    }

    public static int getScreenHeightDp(@NonNull Resources resources) {
        return IMPL.getScreenHeightDp(resources);
    }

    public static int getScreenWidthDp(@NonNull Resources resources) {
        return IMPL.getScreenWidthDp(resources);
    }

    public static int getSmallestScreenWidthDp(@NonNull Resources resources) {
        return IMPL.getSmallestScreenWidthDp(resources);
    }

    public static int getDensityDpi(@NonNull Resources resources) {
        return IMPL.getDensityDpi(resources);
    }
}
