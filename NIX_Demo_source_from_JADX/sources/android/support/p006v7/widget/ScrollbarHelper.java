package android.support.p006v7.widget;

import android.support.p006v7.widget.RecyclerView.LayoutManager;
import android.support.p006v7.widget.RecyclerView.State;
import android.view.View;

/* renamed from: android.support.v7.widget.ScrollbarHelper */
class ScrollbarHelper {
    ScrollbarHelper() {
    }

    static int computeScrollOffset(State state, OrientationHelper orientation, View startChild, View endChild, LayoutManager lm, boolean smoothScrollbarEnabled, boolean reverseLayout) {
        int itemsBefore;
        if (lm.getChildCount() == 0 || state.getItemCount() == 0 || startChild == null || endChild == null) {
            return 0;
        }
        int minPosition = Math.min(lm.getPosition(startChild), lm.getPosition(endChild));
        int maxPosition = Math.max(lm.getPosition(startChild), lm.getPosition(endChild));
        if (reverseLayout) {
            itemsBefore = Math.max(0, (state.getItemCount() - maxPosition) - 1);
        } else {
            itemsBefore = Math.max(0, minPosition);
        }
        if (!smoothScrollbarEnabled) {
            return itemsBefore;
        }
        return Math.round((((float) itemsBefore) * (((float) Math.abs(orientation.getDecoratedEnd(endChild) - orientation.getDecoratedStart(startChild))) / ((float) (Math.abs(lm.getPosition(startChild) - lm.getPosition(endChild)) + 1)))) + ((float) (orientation.getStartAfterPadding() - orientation.getDecoratedStart(startChild))));
    }

    static int computeScrollExtent(State state, OrientationHelper orientation, View startChild, View endChild, LayoutManager lm, boolean smoothScrollbarEnabled) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0 || startChild == null || endChild == null) {
            return 0;
        }
        if (!smoothScrollbarEnabled) {
            return Math.abs(lm.getPosition(startChild) - lm.getPosition(endChild)) + 1;
        }
        return Math.min(orientation.getTotalSpace(), orientation.getDecoratedEnd(endChild) - orientation.getDecoratedStart(startChild));
    }

    static int computeScrollRange(State state, OrientationHelper orientation, View startChild, View endChild, LayoutManager lm, boolean smoothScrollbarEnabled) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0 || startChild == null || endChild == null) {
            return 0;
        }
        if (!smoothScrollbarEnabled) {
            return state.getItemCount();
        }
        return (int) ((((float) (orientation.getDecoratedEnd(endChild) - orientation.getDecoratedStart(startChild))) / ((float) (Math.abs(lm.getPosition(startChild) - lm.getPosition(endChild)) + 1))) * ((float) state.getItemCount()));
    }
}
