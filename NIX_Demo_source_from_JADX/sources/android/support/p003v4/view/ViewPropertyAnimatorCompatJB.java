package android.support.p003v4.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.View;

@TargetApi(16)
@RequiresApi(16)
/* renamed from: android.support.v4.view.ViewPropertyAnimatorCompatJB */
class ViewPropertyAnimatorCompatJB {
    ViewPropertyAnimatorCompatJB() {
    }

    public static void withStartAction(View view, Runnable runnable) {
        view.animate().withStartAction(runnable);
    }

    public static void withEndAction(View view, Runnable runnable) {
        view.animate().withEndAction(runnable);
    }

    public static void withLayer(View view) {
        view.animate().withLayer();
    }

    public static void setListener(final View view, final ViewPropertyAnimatorListener listener) {
        if (listener != null) {
            view.animate().setListener(new AnimatorListenerAdapter() {
                public void onAnimationCancel(Animator animation) {
                    listener.onAnimationCancel(view);
                }

                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd(view);
                }

                public void onAnimationStart(Animator animation) {
                    listener.onAnimationStart(view);
                }
            });
        } else {
            view.animate().setListener(null);
        }
    }
}
