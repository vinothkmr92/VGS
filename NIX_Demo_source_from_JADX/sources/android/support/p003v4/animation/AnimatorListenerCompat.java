package android.support.p003v4.animation;

import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;

@RestrictTo({Scope.LIBRARY_GROUP})
/* renamed from: android.support.v4.animation.AnimatorListenerCompat */
public interface AnimatorListenerCompat {
    void onAnimationCancel(ValueAnimatorCompat valueAnimatorCompat);

    void onAnimationEnd(ValueAnimatorCompat valueAnimatorCompat);

    void onAnimationRepeat(ValueAnimatorCompat valueAnimatorCompat);

    void onAnimationStart(ValueAnimatorCompat valueAnimatorCompat);
}
