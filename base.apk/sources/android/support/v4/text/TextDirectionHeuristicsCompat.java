package android.support.v4.text;

import java.nio.CharBuffer;
import java.util.Locale;

public final class TextDirectionHeuristicsCompat {
    public static final TextDirectionHeuristicCompat ANYRTL_LTR = new TextDirectionHeuristicInternal(AnyStrong.INSTANCE_RTL, false);
    public static final TextDirectionHeuristicCompat FIRSTSTRONG_LTR = new TextDirectionHeuristicInternal(FirstStrong.INSTANCE, false);
    public static final TextDirectionHeuristicCompat FIRSTSTRONG_RTL = new TextDirectionHeuristicInternal(FirstStrong.INSTANCE, true);
    public static final TextDirectionHeuristicCompat LOCALE = TextDirectionHeuristicLocale.INSTANCE;
    public static final TextDirectionHeuristicCompat LTR = new TextDirectionHeuristicInternal(null, false);
    public static final TextDirectionHeuristicCompat RTL = new TextDirectionHeuristicInternal(null, true);
    private static final int STATE_FALSE = 1;
    private static final int STATE_TRUE = 0;
    private static final int STATE_UNKNOWN = 2;

    private interface TextDirectionAlgorithm {
        int checkRtl(CharSequence charSequence, int i, int i2);
    }

    private static class AnyStrong implements TextDirectionAlgorithm {
        public static final AnyStrong INSTANCE_LTR = new AnyStrong(false);
        public static final AnyStrong INSTANCE_RTL = new AnyStrong(true);
        private final boolean mLookForRtl;

        public int checkRtl(CharSequence charSequence, int i, int i2) {
            i2 += i;
            Object obj = null;
            while (i < i2) {
                switch (TextDirectionHeuristicsCompat.isRtlText(Character.getDirectionality(charSequence.charAt(i)))) {
                    case 0:
                        if (this.mLookForRtl) {
                            return 0;
                        }
                        break;
                    case 1:
                        if (!this.mLookForRtl) {
                            return 1;
                        }
                        break;
                    default:
                        continue;
                }
                obj = 1;
                i++;
            }
            return obj != null ? this.mLookForRtl : 2;
        }

        private AnyStrong(boolean z) {
            this.mLookForRtl = z;
        }
    }

    private static class FirstStrong implements TextDirectionAlgorithm {
        public static final FirstStrong INSTANCE = new FirstStrong();

        public int checkRtl(CharSequence charSequence, int i, int i2) {
            i2 += i;
            int i3 = 2;
            while (i < i2 && i3 == 2) {
                i3 = TextDirectionHeuristicsCompat.isRtlTextOrFormat(Character.getDirectionality(charSequence.charAt(i)));
                i++;
            }
            return i3;
        }

        private FirstStrong() {
        }
    }

    private static abstract class TextDirectionHeuristicImpl implements TextDirectionHeuristicCompat {
        private final TextDirectionAlgorithm mAlgorithm;

        protected abstract boolean defaultIsRtl();

        public TextDirectionHeuristicImpl(TextDirectionAlgorithm textDirectionAlgorithm) {
            this.mAlgorithm = textDirectionAlgorithm;
        }

        public boolean isRtl(char[] cArr, int i, int i2) {
            return isRtl(CharBuffer.wrap(cArr), i, i2);
        }

        public boolean isRtl(CharSequence charSequence, int i, int i2) {
            if (charSequence != null && i >= 0 && i2 >= 0) {
                if (charSequence.length() - i2 >= i) {
                    if (this.mAlgorithm == null) {
                        return defaultIsRtl();
                    }
                    return doCheck(charSequence, i, i2);
                }
            }
            throw new IllegalArgumentException();
        }

        private boolean doCheck(CharSequence charSequence, int i, int i2) {
            switch (this.mAlgorithm.checkRtl(charSequence, i, i2)) {
                case null:
                    return true;
                case 1:
                    return null;
                default:
                    return defaultIsRtl();
            }
        }
    }

    private static class TextDirectionHeuristicInternal extends TextDirectionHeuristicImpl {
        private final boolean mDefaultIsRtl;

        TextDirectionHeuristicInternal(TextDirectionAlgorithm textDirectionAlgorithm, boolean z) {
            super(textDirectionAlgorithm);
            this.mDefaultIsRtl = z;
        }

        protected boolean defaultIsRtl() {
            return this.mDefaultIsRtl;
        }
    }

    private static class TextDirectionHeuristicLocale extends TextDirectionHeuristicImpl {
        public static final TextDirectionHeuristicLocale INSTANCE = new TextDirectionHeuristicLocale();

        public TextDirectionHeuristicLocale() {
            super(null);
        }

        protected boolean defaultIsRtl() {
            return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
        }
    }

    static int isRtlText(int i) {
        switch (i) {
            case 0:
                return 1;
            case 1:
            case 2:
                return 0;
            default:
                return 2;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static int isRtlTextOrFormat(int i) {
        switch (i) {
            case 0:
                break;
            case 1:
            case 2:
                break;
            default:
                switch (i) {
                    case 14:
                    case 15:
                        break;
                    case 16:
                    case 17:
                        break;
                    default:
                        return 2;
                }
        }
    }

    private TextDirectionHeuristicsCompat() {
    }
}