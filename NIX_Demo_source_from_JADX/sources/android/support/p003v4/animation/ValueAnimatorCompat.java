package android.support.p003v4.animation;

import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.view.View;

@RestrictTo({Scope.LIBRARY_GROUP})
/* renamed from: android.support.v4.animation.ValueAnimatorCompat */
public interface ValueAnimatorCompat {
    void addListener(AnimatorListenerCompat animatorListenerCompat);

    void addUpdateListener(AnimatorUpdateListenerCompat animatorUpdateListenerCompat);

    void cancel();

    float getAnimatedFraction();

    void setDuration(long j);

    void setTarget(View view);

    void start();
}
