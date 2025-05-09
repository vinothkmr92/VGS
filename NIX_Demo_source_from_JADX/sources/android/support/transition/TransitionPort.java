package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.p003v4.util.ArrayMap;
import android.support.p003v4.util.LongSparseArray;
import android.support.p003v4.util.SimpleArrayMap;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@TargetApi(14)
@RequiresApi(14)
abstract class TransitionPort implements Cloneable {
    static final boolean DBG = false;
    private static final String LOG_TAG = "Transition";
    private static ThreadLocal<ArrayMap<Animator, AnimationInfo>> sRunningAnimators = new ThreadLocal<>();
    ArrayList<Animator> mAnimators = new ArrayList<>();
    boolean mCanRemoveViews = false;
    ArrayList<Animator> mCurrentAnimators = new ArrayList<>();
    long mDuration = -1;
    private TransitionValuesMaps mEndValues = new TransitionValuesMaps();
    private boolean mEnded = false;
    TimeInterpolator mInterpolator = null;
    ArrayList<TransitionListener> mListeners = null;
    private String mName = getClass().getName();
    int mNumInstances = 0;
    TransitionSetPort mParent = null;
    boolean mPaused = false;
    ViewGroup mSceneRoot = null;
    long mStartDelay = -1;
    private TransitionValuesMaps mStartValues = new TransitionValuesMaps();
    ArrayList<View> mTargetChildExcludes = null;
    ArrayList<View> mTargetExcludes = null;
    ArrayList<Integer> mTargetIdChildExcludes = null;
    ArrayList<Integer> mTargetIdExcludes = null;
    ArrayList<Integer> mTargetIds = new ArrayList<>();
    ArrayList<Class> mTargetTypeChildExcludes = null;
    ArrayList<Class> mTargetTypeExcludes = null;
    ArrayList<View> mTargets = new ArrayList<>();

    private static class AnimationInfo {
        String name;
        TransitionValues values;
        View view;
        WindowIdPort windowId;

        AnimationInfo(View view2, String name2, WindowIdPort windowId2, TransitionValues values2) {
            this.view = view2;
            this.name = name2;
            this.values = values2;
            this.windowId = windowId2;
        }
    }

    private static class ArrayListManager {
        private ArrayListManager() {
        }

        static <T> ArrayList<T> add(ArrayList<T> list, T item) {
            if (list == null) {
                list = new ArrayList<>();
            }
            if (!list.contains(item)) {
                list.add(item);
            }
            return list;
        }

        static <T> ArrayList<T> remove(ArrayList<T> list, T item) {
            if (list == null) {
                return list;
            }
            list.remove(item);
            if (list.isEmpty()) {
                return null;
            }
            return list;
        }
    }

    public interface TransitionListener {
        void onTransitionCancel(TransitionPort transitionPort);

        void onTransitionEnd(TransitionPort transitionPort);

        void onTransitionPause(TransitionPort transitionPort);

        void onTransitionResume(TransitionPort transitionPort);

        void onTransitionStart(TransitionPort transitionPort);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static class TransitionListenerAdapter implements TransitionListener {
        public void onTransitionStart(TransitionPort transition) {
        }

        public void onTransitionEnd(TransitionPort transition) {
        }

        public void onTransitionCancel(TransitionPort transition) {
        }

        public void onTransitionPause(TransitionPort transition) {
        }

        public void onTransitionResume(TransitionPort transition) {
        }
    }

    public abstract void captureEndValues(TransitionValues transitionValues);

    public abstract void captureStartValues(TransitionValues transitionValues);

    private static ArrayMap<Animator, AnimationInfo> getRunningAnimators() {
        ArrayMap<Animator, AnimationInfo> runningAnimators = (ArrayMap) sRunningAnimators.get();
        if (runningAnimators != null) {
            return runningAnimators;
        }
        ArrayMap<Animator, AnimationInfo> runningAnimators2 = new ArrayMap<>();
        sRunningAnimators.set(runningAnimators2);
        return runningAnimators2;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public TransitionPort setDuration(long duration) {
        this.mDuration = duration;
        return this;
    }

    public long getStartDelay() {
        return this.mStartDelay;
    }

    public TransitionPort setStartDelay(long startDelay) {
        this.mStartDelay = startDelay;
        return this;
    }

    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    public TransitionPort setInterpolator(TimeInterpolator interpolator) {
        this.mInterpolator = interpolator;
        return this;
    }

    public String[] getTransitionProperties() {
        return null;
    }

    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        return null;
    }

    /* access modifiers changed from: protected */
    @RestrictTo({Scope.LIBRARY_GROUP})
    public void createAnimators(ViewGroup sceneRoot, TransitionValuesMaps startValues, TransitionValuesMaps endValues) {
        View view;
        ArrayMap<View, TransitionValues> endCopy = new ArrayMap<>((SimpleArrayMap) endValues.viewValues);
        SparseArray<TransitionValues> endIdCopy = new SparseArray<>(endValues.idValues.size());
        for (int i = 0; i < endValues.idValues.size(); i++) {
            endIdCopy.put(endValues.idValues.keyAt(i), endValues.idValues.valueAt(i));
        }
        LongSparseArray<TransitionValues> endItemIdCopy = new LongSparseArray<>(endValues.itemIdValues.size());
        for (int i2 = 0; i2 < endValues.itemIdValues.size(); i2++) {
            endItemIdCopy.put(endValues.itemIdValues.keyAt(i2), endValues.itemIdValues.valueAt(i2));
        }
        ArrayList<TransitionValues> startValuesList = new ArrayList<>();
        ArrayList<TransitionValues> endValuesList = new ArrayList<>();
        for (View view2 : startValues.viewValues.keySet()) {
            TransitionValues end = null;
            boolean isInListView = false;
            if (view2.getParent() instanceof ListView) {
                isInListView = true;
            }
            if (!isInListView) {
                int id = view2.getId();
                TransitionValues start = startValues.viewValues.get(view2) != null ? (TransitionValues) startValues.viewValues.get(view2) : (TransitionValues) startValues.idValues.get(id);
                if (endValues.viewValues.get(view2) != null) {
                    end = (TransitionValues) endValues.viewValues.get(view2);
                    endCopy.remove(view2);
                } else if (id != -1) {
                    end = (TransitionValues) endValues.idValues.get(id);
                    View removeView = null;
                    for (View viewToRemove : endCopy.keySet()) {
                        if (viewToRemove.getId() == id) {
                            removeView = viewToRemove;
                        }
                    }
                    if (removeView != null) {
                        endCopy.remove(removeView);
                    }
                }
                endIdCopy.remove(id);
                if (isValidTarget(view2, (long) id)) {
                    startValuesList.add(start);
                    endValuesList.add(end);
                }
            } else {
                ListView parent = (ListView) view2.getParent();
                if (parent.getAdapter().hasStableIds()) {
                    long itemId = parent.getItemIdAtPosition(parent.getPositionForView(view2));
                    TransitionValues start2 = (TransitionValues) startValues.itemIdValues.get(itemId);
                    endItemIdCopy.remove(itemId);
                    startValuesList.add(start2);
                    endValuesList.add(null);
                }
            }
        }
        int startItemIdCopySize = startValues.itemIdValues.size();
        for (int i3 = 0; i3 < startItemIdCopySize; i3++) {
            long id2 = startValues.itemIdValues.keyAt(i3);
            if (isValidTarget(null, id2)) {
                TransitionValues start3 = (TransitionValues) startValues.itemIdValues.get(id2);
                TransitionValues end2 = (TransitionValues) endValues.itemIdValues.get(id2);
                endItemIdCopy.remove(id2);
                startValuesList.add(start3);
                endValuesList.add(end2);
            }
        }
        for (View view3 : endCopy.keySet()) {
            int id3 = view3.getId();
            if (isValidTarget(view3, (long) id3)) {
                TransitionValues start4 = (TransitionValues) (startValues.viewValues.get(view3) != null ? startValues.viewValues.get(view3) : startValues.idValues.get(id3));
                TransitionValues end3 = (TransitionValues) endCopy.get(view3);
                endIdCopy.remove(id3);
                startValuesList.add(start4);
                endValuesList.add(end3);
            }
        }
        int endIdCopySize = endIdCopy.size();
        for (int i4 = 0; i4 < endIdCopySize; i4++) {
            int id4 = endIdCopy.keyAt(i4);
            if (isValidTarget(null, (long) id4)) {
                TransitionValues end4 = (TransitionValues) endIdCopy.get(id4);
                startValuesList.add((TransitionValues) startValues.idValues.get(id4));
                endValuesList.add(end4);
            }
        }
        int endItemIdCopySize = endItemIdCopy.size();
        for (int i5 = 0; i5 < endItemIdCopySize; i5++) {
            long id5 = endItemIdCopy.keyAt(i5);
            TransitionValues end5 = (TransitionValues) endItemIdCopy.get(id5);
            startValuesList.add((TransitionValues) startValues.itemIdValues.get(id5));
            endValuesList.add(end5);
        }
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        for (int i6 = 0; i6 < startValuesList.size(); i6++) {
            TransitionValues start5 = (TransitionValues) startValuesList.get(i6);
            TransitionValues end6 = (TransitionValues) endValuesList.get(i6);
            if (!(start5 == null && end6 == null) && (start5 == null || !start5.equals(end6))) {
                Animator animator = createAnimator(sceneRoot, start5, end6);
                if (animator != null) {
                    TransitionValues infoValues = null;
                    if (end6 != null) {
                        view = end6.view;
                        String[] properties = getTransitionProperties();
                        if (view != null && properties != null && properties.length > 0) {
                            infoValues = new TransitionValues();
                            infoValues.view = view;
                            TransitionValues newValues = (TransitionValues) endValues.viewValues.get(view);
                            if (newValues != null) {
                                for (int j = 0; j < properties.length; j++) {
                                    infoValues.values.put(properties[j], newValues.values.get(properties[j]));
                                }
                            }
                            int numExistingAnims = runningAnimators.size();
                            int j2 = 0;
                            while (true) {
                                if (j2 >= numExistingAnims) {
                                    break;
                                }
                                AnimationInfo info = (AnimationInfo) runningAnimators.get((Animator) runningAnimators.keyAt(j2));
                                if (info.values != null && info.view == view && (((info.name == null && getName() == null) || info.name.equals(getName())) && info.values.equals(infoValues))) {
                                    animator = null;
                                    break;
                                }
                                j2++;
                            }
                        }
                    } else {
                        view = start5.view;
                    }
                    if (animator != null) {
                        AnimationInfo animationInfo = new AnimationInfo(view, getName(), WindowIdPort.getWindowId(sceneRoot), infoValues);
                        runningAnimators.put(animator, animationInfo);
                        this.mAnimators.add(animator);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isValidTarget(View target, long targetId) {
        if (this.mTargetIdExcludes != null && this.mTargetIdExcludes.contains(Integer.valueOf((int) targetId))) {
            return false;
        }
        if (this.mTargetExcludes != null && this.mTargetExcludes.contains(target)) {
            return false;
        }
        if (!(this.mTargetTypeExcludes == null || target == null)) {
            int numTypes = this.mTargetTypeExcludes.size();
            for (int i = 0; i < numTypes; i++) {
                if (((Class) this.mTargetTypeExcludes.get(i)).isInstance(target)) {
                    return false;
                }
            }
        }
        if (this.mTargetIds.size() == 0 && this.mTargets.size() == 0) {
            return true;
        }
        if (this.mTargetIds.size() > 0) {
            for (int i2 = 0; i2 < this.mTargetIds.size(); i2++) {
                if (((long) ((Integer) this.mTargetIds.get(i2)).intValue()) == targetId) {
                    return true;
                }
            }
        }
        if (target != null && this.mTargets.size() > 0) {
            for (int i3 = 0; i3 < this.mTargets.size(); i3++) {
                if (this.mTargets.get(i3) == target) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    @RestrictTo({Scope.LIBRARY_GROUP})
    public void runAnimators() {
        start();
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        Iterator it = this.mAnimators.iterator();
        while (it.hasNext()) {
            Animator anim = (Animator) it.next();
            if (runningAnimators.containsKey(anim)) {
                start();
                runAnimator(anim, runningAnimators);
            }
        }
        this.mAnimators.clear();
        end();
    }

    private void runAnimator(Animator animator, final ArrayMap<Animator, AnimationInfo> runningAnimators) {
        if (animator != null) {
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    TransitionPort.this.mCurrentAnimators.add(animation);
                }

                public void onAnimationEnd(Animator animation) {
                    runningAnimators.remove(animation);
                    TransitionPort.this.mCurrentAnimators.remove(animation);
                }
            });
            animate(animator);
        }
    }

    public TransitionPort addTarget(int targetId) {
        if (targetId > 0) {
            this.mTargetIds.add(Integer.valueOf(targetId));
        }
        return this;
    }

    public TransitionPort removeTarget(int targetId) {
        if (targetId > 0) {
            this.mTargetIds.remove(Integer.valueOf(targetId));
        }
        return this;
    }

    public TransitionPort excludeTarget(int targetId, boolean exclude) {
        this.mTargetIdExcludes = excludeId(this.mTargetIdExcludes, targetId, exclude);
        return this;
    }

    public TransitionPort excludeChildren(int targetId, boolean exclude) {
        this.mTargetIdChildExcludes = excludeId(this.mTargetIdChildExcludes, targetId, exclude);
        return this;
    }

    private ArrayList<Integer> excludeId(ArrayList<Integer> list, int targetId, boolean exclude) {
        if (targetId <= 0) {
            return list;
        }
        if (exclude) {
            return ArrayListManager.add(list, Integer.valueOf(targetId));
        }
        return ArrayListManager.remove(list, Integer.valueOf(targetId));
    }

    public TransitionPort excludeTarget(View target, boolean exclude) {
        this.mTargetExcludes = excludeView(this.mTargetExcludes, target, exclude);
        return this;
    }

    public TransitionPort excludeChildren(View target, boolean exclude) {
        this.mTargetChildExcludes = excludeView(this.mTargetChildExcludes, target, exclude);
        return this;
    }

    private ArrayList<View> excludeView(ArrayList<View> list, View target, boolean exclude) {
        if (target == null) {
            return list;
        }
        if (exclude) {
            return ArrayListManager.add(list, target);
        }
        return ArrayListManager.remove(list, target);
    }

    public TransitionPort excludeTarget(Class type, boolean exclude) {
        this.mTargetTypeExcludes = excludeType(this.mTargetTypeExcludes, type, exclude);
        return this;
    }

    public TransitionPort excludeChildren(Class type, boolean exclude) {
        this.mTargetTypeChildExcludes = excludeType(this.mTargetTypeChildExcludes, type, exclude);
        return this;
    }

    private ArrayList<Class> excludeType(ArrayList<Class> list, Class type, boolean exclude) {
        if (type == null) {
            return list;
        }
        if (exclude) {
            return ArrayListManager.add(list, type);
        }
        return ArrayListManager.remove(list, type);
    }

    public TransitionPort addTarget(View target) {
        this.mTargets.add(target);
        return this;
    }

    public TransitionPort removeTarget(View target) {
        if (target != null) {
            this.mTargets.remove(target);
        }
        return this;
    }

    public List<Integer> getTargetIds() {
        return this.mTargetIds;
    }

    public List<View> getTargets() {
        return this.mTargets;
    }

    /* access modifiers changed from: 0000 */
    public void captureValues(ViewGroup sceneRoot, boolean start) {
        clearValues(start);
        if (this.mTargetIds.size() > 0 || this.mTargets.size() > 0) {
            if (this.mTargetIds.size() > 0) {
                for (int i = 0; i < this.mTargetIds.size(); i++) {
                    int id = ((Integer) this.mTargetIds.get(i)).intValue();
                    View view = sceneRoot.findViewById(id);
                    if (view != null) {
                        TransitionValues values = new TransitionValues();
                        values.view = view;
                        if (start) {
                            captureStartValues(values);
                        } else {
                            captureEndValues(values);
                        }
                        if (start) {
                            this.mStartValues.viewValues.put(view, values);
                            if (id >= 0) {
                                this.mStartValues.idValues.put(id, values);
                            }
                        } else {
                            this.mEndValues.viewValues.put(view, values);
                            if (id >= 0) {
                                this.mEndValues.idValues.put(id, values);
                            }
                        }
                    }
                }
            }
            if (this.mTargets.size() > 0) {
                for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                    View view2 = (View) this.mTargets.get(i2);
                    if (view2 != null) {
                        TransitionValues values2 = new TransitionValues();
                        values2.view = view2;
                        if (start) {
                            captureStartValues(values2);
                        } else {
                            captureEndValues(values2);
                        }
                        if (start) {
                            this.mStartValues.viewValues.put(view2, values2);
                        } else {
                            this.mEndValues.viewValues.put(view2, values2);
                        }
                    }
                }
                return;
            }
            return;
        }
        captureHierarchy(sceneRoot, start);
    }

    /* access modifiers changed from: 0000 */
    public void clearValues(boolean start) {
        if (start) {
            this.mStartValues.viewValues.clear();
            this.mStartValues.idValues.clear();
            this.mStartValues.itemIdValues.clear();
            return;
        }
        this.mEndValues.viewValues.clear();
        this.mEndValues.idValues.clear();
        this.mEndValues.itemIdValues.clear();
    }

    private void captureHierarchy(View view, boolean start) {
        if (view != null) {
            boolean isListViewItem = false;
            if (view.getParent() instanceof ListView) {
                isListViewItem = true;
            }
            if (!isListViewItem || ((ListView) view.getParent()).getAdapter().hasStableIds()) {
                int id = -1;
                long itemId = -1;
                if (!isListViewItem) {
                    id = view.getId();
                } else {
                    ListView listview = (ListView) view.getParent();
                    itemId = listview.getItemIdAtPosition(listview.getPositionForView(view));
                }
                if (this.mTargetIdExcludes != null && this.mTargetIdExcludes.contains(Integer.valueOf(id))) {
                    return;
                }
                if (this.mTargetExcludes == null || !this.mTargetExcludes.contains(view)) {
                    if (!(this.mTargetTypeExcludes == null || view == null)) {
                        int numTypes = this.mTargetTypeExcludes.size();
                        int i = 0;
                        while (i < numTypes) {
                            if (!((Class) this.mTargetTypeExcludes.get(i)).isInstance(view)) {
                                i++;
                            } else {
                                return;
                            }
                        }
                    }
                    TransitionValues values = new TransitionValues();
                    values.view = view;
                    if (start) {
                        captureStartValues(values);
                    } else {
                        captureEndValues(values);
                    }
                    if (start) {
                        if (!isListViewItem) {
                            this.mStartValues.viewValues.put(view, values);
                            if (id >= 0) {
                                this.mStartValues.idValues.put(id, values);
                            }
                        } else {
                            this.mStartValues.itemIdValues.put(itemId, values);
                        }
                    } else if (!isListViewItem) {
                        this.mEndValues.viewValues.put(view, values);
                        if (id >= 0) {
                            this.mEndValues.idValues.put(id, values);
                        }
                    } else {
                        this.mEndValues.itemIdValues.put(itemId, values);
                    }
                    if (!(view instanceof ViewGroup)) {
                        return;
                    }
                    if (this.mTargetIdChildExcludes != null && this.mTargetIdChildExcludes.contains(Integer.valueOf(id))) {
                        return;
                    }
                    if (this.mTargetChildExcludes == null || !this.mTargetChildExcludes.contains(view)) {
                        if (!(this.mTargetTypeChildExcludes == null || view == null)) {
                            int numTypes2 = this.mTargetTypeChildExcludes.size();
                            int i2 = 0;
                            while (i2 < numTypes2) {
                                if (!((Class) this.mTargetTypeChildExcludes.get(i2)).isInstance(view)) {
                                    i2++;
                                } else {
                                    return;
                                }
                            }
                        }
                        ViewGroup parent = (ViewGroup) view;
                        for (int i3 = 0; i3 < parent.getChildCount(); i3++) {
                            captureHierarchy(parent.getChildAt(i3), start);
                        }
                    }
                }
            }
        }
    }

    public TransitionValues getTransitionValues(View view, boolean start) {
        if (this.mParent != null) {
            return this.mParent.getTransitionValues(view, start);
        }
        TransitionValuesMaps valuesMaps = start ? this.mStartValues : this.mEndValues;
        TransitionValues values = (TransitionValues) valuesMaps.viewValues.get(view);
        if (values != null) {
            return values;
        }
        int id = view.getId();
        if (id >= 0) {
            values = (TransitionValues) valuesMaps.idValues.get(id);
        }
        if (values != null || !(view.getParent() instanceof ListView)) {
            return values;
        }
        ListView listview = (ListView) view.getParent();
        return (TransitionValues) valuesMaps.itemIdValues.get(listview.getItemIdAtPosition(listview.getPositionForView(view)));
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void pause(View sceneRoot) {
        if (!this.mEnded) {
            ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
            int numOldAnims = runningAnimators.size();
            WindowIdPort windowId = WindowIdPort.getWindowId(sceneRoot);
            for (int i = numOldAnims - 1; i >= 0; i--) {
                AnimationInfo info = (AnimationInfo) runningAnimators.valueAt(i);
                if (info.view != null && windowId.equals(info.windowId)) {
                    ((Animator) runningAnimators.keyAt(i)).cancel();
                }
            }
            if (this.mListeners != null && this.mListeners.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i2 = 0; i2 < numListeners; i2++) {
                    ((TransitionListener) tmpListeners.get(i2)).onTransitionPause(this);
                }
            }
            this.mPaused = true;
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void resume(View sceneRoot) {
        if (this.mPaused) {
            if (!this.mEnded) {
                ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
                int numOldAnims = runningAnimators.size();
                WindowIdPort windowId = WindowIdPort.getWindowId(sceneRoot);
                for (int i = numOldAnims - 1; i >= 0; i--) {
                    AnimationInfo info = (AnimationInfo) runningAnimators.valueAt(i);
                    if (info.view != null && windowId.equals(info.windowId)) {
                        ((Animator) runningAnimators.keyAt(i)).end();
                    }
                }
                if (this.mListeners != null && this.mListeners.size() > 0) {
                    ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                    int numListeners = tmpListeners.size();
                    for (int i2 = 0; i2 < numListeners; i2++) {
                        ((TransitionListener) tmpListeners.get(i2)).onTransitionResume(this);
                    }
                }
            }
            this.mPaused = false;
        }
    }

    /* access modifiers changed from: 0000 */
    public void playTransition(ViewGroup sceneRoot) {
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        for (int i = runningAnimators.size() - 1; i >= 0; i--) {
            Animator anim = (Animator) runningAnimators.keyAt(i);
            if (anim != null) {
                AnimationInfo oldInfo = (AnimationInfo) runningAnimators.get(anim);
                if (!(oldInfo == null || oldInfo.view == null || oldInfo.view.getContext() != sceneRoot.getContext())) {
                    boolean cancel = false;
                    TransitionValues oldValues = oldInfo.values;
                    View oldView = oldInfo.view;
                    TransitionValues newValues = this.mEndValues.viewValues != null ? (TransitionValues) this.mEndValues.viewValues.get(oldView) : null;
                    if (newValues == null) {
                        newValues = (TransitionValues) this.mEndValues.idValues.get(oldView.getId());
                    }
                    if (oldValues != null && newValues != null) {
                        Iterator it = oldValues.values.keySet().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            String key = (String) it.next();
                            Object oldValue = oldValues.values.get(key);
                            Object newValue = newValues.values.get(key);
                            if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
                                cancel = true;
                                break;
                            }
                        }
                    }
                    if (cancel) {
                        if (anim.isRunning() || anim.isStarted()) {
                            anim.cancel();
                        } else {
                            runningAnimators.remove(anim);
                        }
                    }
                }
            }
        }
        createAnimators(sceneRoot, this.mStartValues, this.mEndValues);
        runAnimators();
    }

    /* access modifiers changed from: protected */
    @RestrictTo({Scope.LIBRARY_GROUP})
    public void animate(Animator animator) {
        if (animator == null) {
            end();
            return;
        }
        if (getDuration() >= 0) {
            animator.setDuration(getDuration());
        }
        if (getStartDelay() >= 0) {
            animator.setStartDelay(getStartDelay());
        }
        if (getInterpolator() != null) {
            animator.setInterpolator(getInterpolator());
        }
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                TransitionPort.this.end();
                animation.removeListener(this);
            }
        });
        animator.start();
    }

    /* access modifiers changed from: protected */
    @RestrictTo({Scope.LIBRARY_GROUP})
    public void start() {
        if (this.mNumInstances == 0) {
            if (this.mListeners != null && this.mListeners.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    ((TransitionListener) tmpListeners.get(i)).onTransitionStart(this);
                }
            }
            this.mEnded = false;
        }
        this.mNumInstances++;
    }

    /* access modifiers changed from: protected */
    @RestrictTo({Scope.LIBRARY_GROUP})
    public void end() {
        this.mNumInstances--;
        if (this.mNumInstances == 0) {
            if (this.mListeners != null && this.mListeners.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    ((TransitionListener) tmpListeners.get(i)).onTransitionEnd(this);
                }
            }
            for (int i2 = 0; i2 < this.mStartValues.itemIdValues.size(); i2++) {
                View view = ((TransitionValues) this.mStartValues.itemIdValues.valueAt(i2)).view;
            }
            for (int i3 = 0; i3 < this.mEndValues.itemIdValues.size(); i3++) {
                View view2 = ((TransitionValues) this.mEndValues.itemIdValues.valueAt(i3)).view;
            }
            this.mEnded = true;
        }
    }

    /* access modifiers changed from: protected */
    @RestrictTo({Scope.LIBRARY_GROUP})
    public void cancel() {
        for (int i = this.mCurrentAnimators.size() - 1; i >= 0; i--) {
            ((Animator) this.mCurrentAnimators.get(i)).cancel();
        }
        if (this.mListeners != null && this.mListeners.size() > 0) {
            ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
            int numListeners = tmpListeners.size();
            for (int i2 = 0; i2 < numListeners; i2++) {
                ((TransitionListener) tmpListeners.get(i2)).onTransitionCancel(this);
            }
        }
    }

    public TransitionPort addListener(TransitionListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(listener);
        return this;
    }

    public TransitionPort removeListener(TransitionListener listener) {
        if (this.mListeners != null) {
            this.mListeners.remove(listener);
            if (this.mListeners.size() == 0) {
                this.mListeners = null;
            }
        }
        return this;
    }

    /* access modifiers changed from: 0000 */
    public TransitionPort setSceneRoot(ViewGroup sceneRoot) {
        this.mSceneRoot = sceneRoot;
        return this;
    }

    /* access modifiers changed from: 0000 */
    public void setCanRemoveViews(boolean canRemoveViews) {
        this.mCanRemoveViews = canRemoveViews;
    }

    public String toString() {
        return toString("");
    }

    public TransitionPort clone() {
        TransitionPort clone = null;
        try {
            clone = (TransitionPort) super.clone();
            clone.mAnimators = new ArrayList<>();
            clone.mStartValues = new TransitionValuesMaps();
            clone.mEndValues = new TransitionValuesMaps();
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    public String getName() {
        return this.mName;
    }

    /* access modifiers changed from: 0000 */
    public String toString(String indent) {
        String result = indent + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ": ";
        if (this.mDuration != -1) {
            result = result + "dur(" + this.mDuration + ") ";
        }
        if (this.mStartDelay != -1) {
            result = result + "dly(" + this.mStartDelay + ") ";
        }
        if (this.mInterpolator != null) {
            result = result + "interp(" + this.mInterpolator + ") ";
        }
        if (this.mTargetIds.size() <= 0 && this.mTargets.size() <= 0) {
            return result;
        }
        String result2 = result + "tgts(";
        if (this.mTargetIds.size() > 0) {
            for (int i = 0; i < this.mTargetIds.size(); i++) {
                if (i > 0) {
                    result2 = result2 + ", ";
                }
                result2 = result2 + this.mTargetIds.get(i);
            }
        }
        if (this.mTargets.size() > 0) {
            for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                if (i2 > 0) {
                    result2 = result2 + ", ";
                }
                result2 = result2 + this.mTargets.get(i2);
            }
        }
        return result2 + ")";
    }
}
