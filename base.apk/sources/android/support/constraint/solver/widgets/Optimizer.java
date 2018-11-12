package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.Metrics;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;

public class Optimizer {
    static final int FLAG_CHAIN_DANGLING = 1;
    static final int FLAG_RECOMPUTE_BOUNDS = 2;
    static final int FLAG_USE_OPTIMIZE = 0;
    public static final int OPTIMIZATION_BARRIER = 2;
    public static final int OPTIMIZATION_CHAIN = 4;
    public static final int OPTIMIZATION_DIMENSIONS = 8;
    public static final int OPTIMIZATION_DIRECT = 1;
    public static final int OPTIMIZATION_NONE = 0;
    public static final int OPTIMIZATION_RATIO = 16;
    public static final int OPTIMIZATION_STANDARD = 3;
    static boolean[] flags = new boolean[3];

    static void checkMatchParent(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        if (constraintWidgetContainer.mListDimensionBehaviors[0] != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_PARENT) {
            int i = constraintWidget.mLeft.mMargin;
            int width = constraintWidgetContainer.getWidth() - constraintWidget.mRight.mMargin;
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
            constraintWidget.mHorizontalResolution = 2;
            constraintWidget.setHorizontalDimension(i, width);
        }
        if (constraintWidgetContainer.mListDimensionBehaviors[1] != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_PARENT) {
            i = constraintWidget.mTop.mMargin;
            constraintWidgetContainer = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.mMargin;
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, constraintWidgetContainer);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + i);
            }
            constraintWidget.mVerticalResolution = 2;
            constraintWidget.setVerticalDimension(i, constraintWidgetContainer);
        }
    }

    private static boolean optimizableMatchConstraint(ConstraintWidget constraintWidget, int i) {
        if (constraintWidget.mListDimensionBehaviors[i] != DimensionBehaviour.MATCH_CONSTRAINT) {
            return false;
        }
        int i2 = 1;
        if (constraintWidget.mDimensionRatio != 0.0f) {
            constraintWidget = constraintWidget.mListDimensionBehaviors;
            if (i != 0) {
                i2 = 0;
            }
            return constraintWidget[i2] == DimensionBehaviour.MATCH_CONSTRAINT ? false : false;
        } else {
            if (i != 0) {
                if (constraintWidget.mMatchConstraintDefaultHeight == 0 && constraintWidget.mMatchConstraintMinHeight == 0) {
                    if (constraintWidget.mMatchConstraintMaxHeight != null) {
                    }
                }
                return false;
            } else if (constraintWidget.mMatchConstraintDefaultWidth == 0 && constraintWidget.mMatchConstraintMinWidth == 0 && constraintWidget.mMatchConstraintMaxWidth == null) {
                return true;
            } else {
                return false;
            }
            return true;
        }
    }

    static void analyze(int i, ConstraintWidget constraintWidget) {
        ConstraintWidget constraintWidget2 = constraintWidget;
        constraintWidget.updateResolutionNodes();
        ResolutionAnchor resolutionNode = constraintWidget2.mLeft.getResolutionNode();
        ResolutionAnchor resolutionNode2 = constraintWidget2.mTop.getResolutionNode();
        ResolutionAnchor resolutionNode3 = constraintWidget2.mRight.getResolutionNode();
        ResolutionAnchor resolutionNode4 = constraintWidget2.mBottom.getResolutionNode();
        Object obj = (i & 8) == 8 ? 1 : null;
        Object obj2 = (constraintWidget2.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(constraintWidget2, 0)) ? 1 : null;
        if (!(resolutionNode.type == 4 || resolutionNode3.type == 4)) {
            if (constraintWidget2.mListDimensionBehaviors[0] != DimensionBehaviour.FIXED) {
                if (obj2 == null || constraintWidget.getVisibility() != 8) {
                    if (obj2 != null) {
                        int width = constraintWidget.getWidth();
                        resolutionNode.setType(1);
                        resolutionNode3.setType(1);
                        if (constraintWidget2.mLeft.mTarget == null && constraintWidget2.mRight.mTarget == null) {
                            if (obj != null) {
                                resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                            } else {
                                resolutionNode3.dependsOn(resolutionNode, width);
                            }
                        } else if (constraintWidget2.mLeft.mTarget == null || constraintWidget2.mRight.mTarget != null) {
                            if (constraintWidget2.mLeft.mTarget != null || constraintWidget2.mRight.mTarget == null) {
                                if (!(constraintWidget2.mLeft.mTarget == null || constraintWidget2.mRight.mTarget == null)) {
                                    if (obj != null) {
                                        constraintWidget.getResolutionWidth().addDependent(resolutionNode);
                                        constraintWidget.getResolutionWidth().addDependent(resolutionNode3);
                                    }
                                    if (constraintWidget2.mDimensionRatio == 0.0f) {
                                        resolutionNode.setType(3);
                                        resolutionNode3.setType(3);
                                        resolutionNode.setOpposite(resolutionNode3, 0.0f);
                                        resolutionNode3.setOpposite(resolutionNode, 0.0f);
                                    } else {
                                        resolutionNode.setType(2);
                                        resolutionNode3.setType(2);
                                        resolutionNode.setOpposite(resolutionNode3, (float) (-width));
                                        resolutionNode3.setOpposite(resolutionNode, (float) width);
                                        constraintWidget2.setWidth(width);
                                    }
                                }
                            } else if (obj != null) {
                                resolutionNode.dependsOn(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                            } else {
                                resolutionNode.dependsOn(resolutionNode3, -width);
                            }
                        } else if (obj != null) {
                            resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                        } else {
                            resolutionNode3.dependsOn(resolutionNode, width);
                        }
                    }
                }
            }
            if (constraintWidget2.mLeft.mTarget == null && constraintWidget2.mRight.mTarget == null) {
                resolutionNode.setType(1);
                resolutionNode3.setType(1);
                if (obj != null) {
                    resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                } else {
                    resolutionNode3.dependsOn(resolutionNode, constraintWidget.getWidth());
                }
            } else if (constraintWidget2.mLeft.mTarget != null && constraintWidget2.mRight.mTarget == null) {
                resolutionNode.setType(1);
                resolutionNode3.setType(1);
                if (obj != null) {
                    resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                } else {
                    resolutionNode3.dependsOn(resolutionNode, constraintWidget.getWidth());
                }
            } else if (constraintWidget2.mLeft.mTarget == null && constraintWidget2.mRight.mTarget != null) {
                resolutionNode.setType(1);
                resolutionNode3.setType(1);
                resolutionNode.dependsOn(resolutionNode3, -constraintWidget.getWidth());
                if (obj != null) {
                    resolutionNode.dependsOn(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                } else {
                    resolutionNode.dependsOn(resolutionNode3, -constraintWidget.getWidth());
                }
            } else if (!(constraintWidget2.mLeft.mTarget == null || constraintWidget2.mRight.mTarget == null)) {
                resolutionNode.setType(2);
                resolutionNode3.setType(2);
                if (obj != null) {
                    constraintWidget.getResolutionWidth().addDependent(resolutionNode);
                    constraintWidget.getResolutionWidth().addDependent(resolutionNode3);
                    resolutionNode.setOpposite(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                    resolutionNode3.setOpposite(resolutionNode, 1, constraintWidget.getResolutionWidth());
                } else {
                    resolutionNode.setOpposite(resolutionNode3, (float) (-constraintWidget.getWidth()));
                    resolutionNode3.setOpposite(resolutionNode, (float) constraintWidget.getWidth());
                }
            }
        }
        Object obj3 = (constraintWidget2.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(constraintWidget2, 1)) ? 1 : null;
        if (resolutionNode2.type != 4 && resolutionNode4.type != 4) {
            if (constraintWidget2.mListDimensionBehaviors[1] != DimensionBehaviour.FIXED) {
                if (obj3 == null || constraintWidget.getVisibility() != 8) {
                    if (obj3 != null) {
                        int height = constraintWidget.getHeight();
                        resolutionNode2.setType(1);
                        resolutionNode4.setType(1);
                        if (constraintWidget2.mTop.mTarget == null && constraintWidget2.mBottom.mTarget == null) {
                            if (obj != null) {
                                resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                                return;
                            } else {
                                resolutionNode4.dependsOn(resolutionNode2, height);
                                return;
                            }
                        } else if (constraintWidget2.mTop.mTarget == null || constraintWidget2.mBottom.mTarget != null) {
                            if (constraintWidget2.mTop.mTarget != null || constraintWidget2.mBottom.mTarget == null) {
                                if (constraintWidget2.mTop.mTarget != null && constraintWidget2.mBottom.mTarget != null) {
                                    if (obj != null) {
                                        constraintWidget.getResolutionHeight().addDependent(resolutionNode2);
                                        constraintWidget.getResolutionWidth().addDependent(resolutionNode4);
                                    }
                                    if (constraintWidget2.mDimensionRatio == 0.0f) {
                                        resolutionNode2.setType(3);
                                        resolutionNode4.setType(3);
                                        resolutionNode2.setOpposite(resolutionNode4, 0.0f);
                                        resolutionNode4.setOpposite(resolutionNode2, 0.0f);
                                        return;
                                    }
                                    resolutionNode2.setType(2);
                                    resolutionNode4.setType(2);
                                    resolutionNode2.setOpposite(resolutionNode4, (float) (-height));
                                    resolutionNode4.setOpposite(resolutionNode2, (float) height);
                                    constraintWidget2.setHeight(height);
                                    if (constraintWidget2.mBaselineDistance > 0) {
                                        constraintWidget2.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget2.mBaselineDistance);
                                        return;
                                    }
                                    return;
                                }
                                return;
                            } else if (obj != null) {
                                resolutionNode2.dependsOn(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                                return;
                            } else {
                                resolutionNode2.dependsOn(resolutionNode4, -height);
                                return;
                            }
                        } else if (obj != null) {
                            resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                            return;
                        } else {
                            resolutionNode4.dependsOn(resolutionNode2, height);
                            return;
                        }
                    }
                    return;
                }
            }
            if (constraintWidget2.mTop.mTarget == null && constraintWidget2.mBottom.mTarget == null) {
                resolutionNode2.setType(1);
                resolutionNode4.setType(1);
                if (obj != null) {
                    resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                } else {
                    resolutionNode4.dependsOn(resolutionNode2, constraintWidget.getHeight());
                }
                if (constraintWidget2.mBaseline.mTarget != null) {
                    constraintWidget2.mBaseline.getResolutionNode().setType(1);
                    resolutionNode2.dependsOn(1, constraintWidget2.mBaseline.getResolutionNode(), -constraintWidget2.mBaselineDistance);
                }
            } else if (constraintWidget2.mTop.mTarget != null && constraintWidget2.mBottom.mTarget == null) {
                resolutionNode2.setType(1);
                resolutionNode4.setType(1);
                if (obj != null) {
                    resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                } else {
                    resolutionNode4.dependsOn(resolutionNode2, constraintWidget.getHeight());
                }
                if (constraintWidget2.mBaselineDistance > 0) {
                    constraintWidget2.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget2.mBaselineDistance);
                }
            } else if (constraintWidget2.mTop.mTarget == null && constraintWidget2.mBottom.mTarget != null) {
                resolutionNode2.setType(1);
                resolutionNode4.setType(1);
                if (obj != null) {
                    resolutionNode2.dependsOn(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                } else {
                    resolutionNode2.dependsOn(resolutionNode4, -constraintWidget.getHeight());
                }
                if (constraintWidget2.mBaselineDistance > 0) {
                    constraintWidget2.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget2.mBaselineDistance);
                }
            } else if (constraintWidget2.mTop.mTarget != null && constraintWidget2.mBottom.mTarget != null) {
                resolutionNode2.setType(2);
                resolutionNode4.setType(2);
                if (obj != null) {
                    resolutionNode2.setOpposite(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                    resolutionNode4.setOpposite(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                    constraintWidget.getResolutionHeight().addDependent(resolutionNode2);
                    constraintWidget.getResolutionWidth().addDependent(resolutionNode4);
                } else {
                    resolutionNode2.setOpposite(resolutionNode4, (float) (-constraintWidget.getHeight()));
                    resolutionNode4.setOpposite(resolutionNode2, (float) constraintWidget.getHeight());
                }
                if (constraintWidget2.mBaselineDistance > 0) {
                    constraintWidget2.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget2.mBaselineDistance);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean applyChainOptimized(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, int i2, ChainHead chainHead) {
        Object obj;
        Object obj2;
        Object obj3;
        ConstraintWidget constraintWidget;
        int i3;
        int i4;
        Object obj4;
        float f;
        float f2;
        Object obj5;
        ConstraintAnchor constraintAnchor;
        ConstraintWidget constraintWidget2;
        ResolutionAnchor resolutionNode;
        int i5;
        ResolutionAnchor resolutionNode2;
        ConstraintWidget constraintWidget3;
        float f3;
        float f4;
        float f5;
        int i6;
        LinearSystem linearSystem2;
        Metrics metrics;
        ConstraintWidget constraintWidget4;
        float width;
        Metrics metrics2;
        LinearSystem linearSystem3 = linearSystem;
        ChainHead chainHead2 = chainHead;
        ConstraintWidget constraintWidget5 = chainHead2.mFirst;
        ConstraintWidget constraintWidget6 = chainHead2.mLast;
        ConstraintWidget constraintWidget7 = chainHead2.mFirstVisibleWidget;
        ConstraintWidget constraintWidget8 = chainHead2.mLastVisibleWidget;
        ConstraintWidget constraintWidget9 = chainHead2.mHead;
        float f6 = chainHead2.mTotalWeight;
        ConstraintWidget constraintWidget10 = chainHead2.mFirstMatchConstraintWidget;
        ConstraintWidget constraintWidget11 = chainHead2.mLastMatchConstraintWidget;
        DimensionBehaviour dimensionBehaviour = constraintWidgetContainer.mListDimensionBehaviors[i];
        DimensionBehaviour dimensionBehaviour2 = DimensionBehaviour.WRAP_CONTENT;
        if (i == 0) {
            obj = constraintWidget9.mHorizontalChainStyle == 0 ? 1 : null;
            obj2 = constraintWidget9.mHorizontalChainStyle == 1 ? 1 : null;
        } else {
            obj = constraintWidget9.mVerticalChainStyle == 0 ? 1 : null;
            obj2 = constraintWidget9.mVerticalChainStyle == 1 ? 1 : null;
            if (constraintWidget9.mVerticalChainStyle == 2) {
            }
            obj3 = null;
            constraintWidget = constraintWidget5;
            i3 = 0;
            i4 = 0;
            obj4 = null;
            f = 0.0f;
            f2 = 0.0f;
            while (obj4 == null) {
                obj5 = obj4;
                if (constraintWidget.getVisibility() != 8) {
                    i3++;
                    if (i != 0) {
                        f += (float) constraintWidget.getWidth();
                    } else {
                        f += (float) constraintWidget.getHeight();
                    }
                    if (constraintWidget != constraintWidget7) {
                        f += (float) constraintWidget.mListAnchors[i2].getMargin();
                    }
                    f2 = (f2 + ((float) constraintWidget.mListAnchors[i2].getMargin())) + ((float) constraintWidget.mListAnchors[i2 + 1].getMargin());
                }
                constraintAnchor = constraintWidget.mListAnchors[i2];
                if (constraintWidget.getVisibility() != 8 && constraintWidget.mListDimensionBehaviors[i] == DimensionBehaviour.MATCH_CONSTRAINT) {
                    i4++;
                    if (i != 0) {
                        if (constraintWidget.mMatchConstraintDefaultWidth != 0) {
                            if (!(constraintWidget.mMatchConstraintMinWidth == 0 && constraintWidget.mMatchConstraintMaxWidth == 0)) {
                            }
                        }
                    } else if (!(constraintWidget.mMatchConstraintDefaultHeight == 0 && constraintWidget.mMatchConstraintMinHeight == 0 && constraintWidget.mMatchConstraintMaxHeight == 0)) {
                        return false;
                    }
                }
                constraintAnchor = constraintWidget.mListAnchors[i2 + 1].mTarget;
                if (constraintAnchor != null) {
                    constraintWidget10 = constraintAnchor.mOwner;
                    if (constraintWidget10.mListAnchors[i2].mTarget != null) {
                        if (constraintWidget10.mListAnchors[i2].mTarget.mOwner != constraintWidget) {
                            constraintWidget2 = constraintWidget10;
                            if (constraintWidget2 == null) {
                                obj4 = obj5;
                                constraintWidget = constraintWidget2;
                            } else {
                                obj4 = 1;
                            }
                        }
                    }
                }
                constraintWidget2 = null;
                if (constraintWidget2 == null) {
                    obj4 = 1;
                } else {
                    obj4 = obj5;
                    constraintWidget = constraintWidget2;
                }
            }
            resolutionNode = constraintWidget5.mListAnchors[i2].getResolutionNode();
            i5 = i2 + 1;
            resolutionNode2 = constraintWidget6.mListAnchors[i5].getResolutionNode();
            constraintWidget3 = constraintWidget5;
            if (resolutionNode.target != null) {
                if (resolutionNode2.target == null) {
                    if (resolutionNode.target.state == 1 && resolutionNode2.target.state != 1) {
                        return false;
                    }
                    if (i4 <= 0 && i4 != i3) {
                        return false;
                    }
                    if (obj3 == null && obj == null) {
                        if (obj2 != null) {
                            f3 = 0.0f;
                            f4 = resolutionNode.target.resolvedOffset;
                            f5 = resolutionNode2.target.resolvedOffset;
                            f5 = f4 >= f5 ? (f5 - f4) - f : (f4 - f5) - f;
                            if (i4 > 0 || i4 != i3) {
                                i6 = i5;
                                linearSystem2 = linearSystem;
                                if (f5 >= f) {
                                    return false;
                                }
                                if (obj3 == null) {
                                    f4 += (f5 - f3) * constraintWidget3.getHorizontalBiasPercent();
                                    while (constraintWidget7 != null) {
                                        if (LinearSystem.sMetrics != null) {
                                            metrics = LinearSystem.sMetrics;
                                            metrics.nonresolvedWidgets--;
                                            metrics = LinearSystem.sMetrics;
                                            metrics.resolvedWidgets++;
                                            metrics = LinearSystem.sMetrics;
                                            metrics.chainConnectionResolved++;
                                        }
                                        constraintWidget4 = constraintWidget7.mListNextVisibleWidget[i];
                                        if (constraintWidget4 == null || constraintWidget7 == constraintWidget8) {
                                            if (i != 0) {
                                                width = (float) constraintWidget7.getWidth();
                                            } else {
                                                width = (float) constraintWidget7.getHeight();
                                            }
                                            f4 += (float) constraintWidget7.mListAnchors[i2].getMargin();
                                            constraintWidget7.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, f4);
                                            f4 += width;
                                            constraintWidget7.mListAnchors[i6].getResolutionNode().resolve(resolutionNode.resolvedTarget, f4);
                                            constraintWidget7.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                                            constraintWidget7.mListAnchors[i6].getResolutionNode().addResolvedValue(linearSystem2);
                                            f4 += (float) constraintWidget7.mListAnchors[i6].getMargin();
                                        }
                                        constraintWidget7 = constraintWidget4;
                                    }
                                } else if (!(obj == null && obj2 == null)) {
                                    if (obj == null) {
                                        f5 -= f3;
                                    } else if (obj2 != null) {
                                        f5 -= f3;
                                    }
                                    f3 = f5 / ((float) (i3 + 1));
                                    if (obj2 != null) {
                                        f3 = i3 <= 1 ? f5 / ((float) (i3 - 1)) : f5 / 2.0f;
                                    }
                                    width = f4 + f3;
                                    if (obj2 != null && i3 > 1) {
                                        width = ((float) constraintWidget7.mListAnchors[i2].getMargin()) + f4;
                                    }
                                    if (!(obj == null || constraintWidget7 == null)) {
                                        width += (float) constraintWidget7.mListAnchors[i2].getMargin();
                                    }
                                    while (constraintWidget7 != null) {
                                        if (LinearSystem.sMetrics != null) {
                                            metrics2 = LinearSystem.sMetrics;
                                            metrics2.nonresolvedWidgets--;
                                            metrics2 = LinearSystem.sMetrics;
                                            metrics2.resolvedWidgets++;
                                            metrics2 = LinearSystem.sMetrics;
                                            metrics2.chainConnectionResolved++;
                                        }
                                        constraintWidget5 = constraintWidget7.mListNextVisibleWidget[i];
                                        if (constraintWidget5 == null || constraintWidget7 == constraintWidget8) {
                                            if (i != 0) {
                                                f5 = (float) constraintWidget7.getWidth();
                                            } else {
                                                f5 = (float) constraintWidget7.getHeight();
                                            }
                                            constraintWidget7.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, width);
                                            constraintWidget7.mListAnchors[i6].getResolutionNode().resolve(resolutionNode.resolvedTarget, width + f5);
                                            constraintWidget7.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                                            constraintWidget7.mListAnchors[i6].getResolutionNode().addResolvedValue(linearSystem2);
                                            width += f5 + f3;
                                        }
                                        constraintWidget7 = constraintWidget5;
                                    }
                                }
                                return true;
                            } else if (constraintWidget.getParent() != null && constraintWidget.getParent().mListDimensionBehaviors[i] == DimensionBehaviour.WRAP_CONTENT) {
                                return false;
                            } else {
                                f5 = (f5 + f) - f2;
                                if (obj != null) {
                                    f5 -= f2 - f3;
                                }
                                if (obj != null) {
                                    f4 += (float) constraintWidget7.mListAnchors[i5].getMargin();
                                    constraintWidget4 = constraintWidget7.mListNextVisibleWidget[i];
                                    if (constraintWidget4 != null) {
                                        f4 += (float) constraintWidget4.mListAnchors[i2].getMargin();
                                    }
                                }
                                while (constraintWidget7 != null) {
                                    if (LinearSystem.sMetrics != null) {
                                        metrics = LinearSystem.sMetrics;
                                        i6 = i5;
                                        metrics.nonresolvedWidgets--;
                                        metrics = LinearSystem.sMetrics;
                                        metrics.resolvedWidgets++;
                                        metrics = LinearSystem.sMetrics;
                                        metrics.chainConnectionResolved++;
                                    } else {
                                        i6 = i5;
                                    }
                                    constraintWidget4 = constraintWidget7.mListNextVisibleWidget[i];
                                    if (constraintWidget4 == null) {
                                        if (constraintWidget7 != constraintWidget8) {
                                            linearSystem2 = linearSystem;
                                            constraintWidget7 = constraintWidget4;
                                            i5 = i6;
                                        }
                                    }
                                    width = f5 / ((float) i4);
                                    if (f6 > 0.0f) {
                                        width = (constraintWidget7.mWeight[i] * f5) / f6;
                                    }
                                    f4 += (float) constraintWidget7.mListAnchors[i2].getMargin();
                                    constraintWidget7.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, f4);
                                    f4 += width;
                                    constraintWidget7.mListAnchors[i6].getResolutionNode().resolve(resolutionNode.resolvedTarget, f4);
                                    linearSystem2 = linearSystem;
                                    constraintWidget7.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                                    constraintWidget7.mListAnchors[i6].getResolutionNode().addResolvedValue(linearSystem2);
                                    f4 += (float) constraintWidget7.mListAnchors[i6].getMargin();
                                    constraintWidget7 = constraintWidget4;
                                    i5 = i6;
                                }
                                return true;
                            }
                        }
                    }
                    f3 = constraintWidget7 == null ? (float) constraintWidget7.mListAnchors[i2].getMargin() : 0.0f;
                    if (constraintWidget8 != null) {
                        f3 += (float) constraintWidget8.mListAnchors[i5].getMargin();
                    }
                    f4 = resolutionNode.target.resolvedOffset;
                    f5 = resolutionNode2.target.resolvedOffset;
                    if (f4 >= f5) {
                    }
                    if (i4 > 0) {
                    }
                    i6 = i5;
                    linearSystem2 = linearSystem;
                    if (f5 >= f) {
                        return false;
                    }
                    if (obj3 == null) {
                        if (obj == null) {
                            f5 -= f3;
                        } else if (obj2 != null) {
                            f5 -= f3;
                        }
                        f3 = f5 / ((float) (i3 + 1));
                        if (obj2 != null) {
                            if (i3 <= 1) {
                            }
                        }
                        width = f4 + f3;
                        width = ((float) constraintWidget7.mListAnchors[i2].getMargin()) + f4;
                        width += (float) constraintWidget7.mListAnchors[i2].getMargin();
                        while (constraintWidget7 != null) {
                            if (LinearSystem.sMetrics != null) {
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.nonresolvedWidgets--;
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.resolvedWidgets++;
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.chainConnectionResolved++;
                            }
                            constraintWidget5 = constraintWidget7.mListNextVisibleWidget[i];
                            if (constraintWidget5 == null) {
                            }
                            if (i != 0) {
                                f5 = (float) constraintWidget7.getHeight();
                            } else {
                                f5 = (float) constraintWidget7.getWidth();
                            }
                            constraintWidget7.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, width);
                            constraintWidget7.mListAnchors[i6].getResolutionNode().resolve(resolutionNode.resolvedTarget, width + f5);
                            constraintWidget7.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                            constraintWidget7.mListAnchors[i6].getResolutionNode().addResolvedValue(linearSystem2);
                            width += f5 + f3;
                            constraintWidget7 = constraintWidget5;
                        }
                    } else {
                        f4 += (f5 - f3) * constraintWidget3.getHorizontalBiasPercent();
                        while (constraintWidget7 != null) {
                            if (LinearSystem.sMetrics != null) {
                                metrics = LinearSystem.sMetrics;
                                metrics.nonresolvedWidgets--;
                                metrics = LinearSystem.sMetrics;
                                metrics.resolvedWidgets++;
                                metrics = LinearSystem.sMetrics;
                                metrics.chainConnectionResolved++;
                            }
                            constraintWidget4 = constraintWidget7.mListNextVisibleWidget[i];
                            if (constraintWidget4 == null) {
                            }
                            if (i != 0) {
                                width = (float) constraintWidget7.getHeight();
                            } else {
                                width = (float) constraintWidget7.getWidth();
                            }
                            f4 += (float) constraintWidget7.mListAnchors[i2].getMargin();
                            constraintWidget7.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, f4);
                            f4 += width;
                            constraintWidget7.mListAnchors[i6].getResolutionNode().resolve(resolutionNode.resolvedTarget, f4);
                            constraintWidget7.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                            constraintWidget7.mListAnchors[i6].getResolutionNode().addResolvedValue(linearSystem2);
                            f4 += (float) constraintWidget7.mListAnchors[i6].getMargin();
                            constraintWidget7 = constraintWidget4;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        obj3 = 1;
        constraintWidget = constraintWidget5;
        i3 = 0;
        i4 = 0;
        obj4 = null;
        f = 0.0f;
        f2 = 0.0f;
        while (obj4 == null) {
            obj5 = obj4;
            if (constraintWidget.getVisibility() != 8) {
                i3++;
                if (i != 0) {
                    f += (float) constraintWidget.getHeight();
                } else {
                    f += (float) constraintWidget.getWidth();
                }
                if (constraintWidget != constraintWidget7) {
                    f += (float) constraintWidget.mListAnchors[i2].getMargin();
                }
                f2 = (f2 + ((float) constraintWidget.mListAnchors[i2].getMargin())) + ((float) constraintWidget.mListAnchors[i2 + 1].getMargin());
            }
            constraintAnchor = constraintWidget.mListAnchors[i2];
            i4++;
            if (i != 0) {
                return false;
            }
            return constraintWidget.mMatchConstraintDefaultWidth != 0 ? false : false;
            constraintAnchor = constraintWidget.mListAnchors[i2 + 1].mTarget;
            if (constraintAnchor != null) {
                constraintWidget10 = constraintAnchor.mOwner;
                if (constraintWidget10.mListAnchors[i2].mTarget != null) {
                    if (constraintWidget10.mListAnchors[i2].mTarget.mOwner != constraintWidget) {
                        constraintWidget2 = constraintWidget10;
                        if (constraintWidget2 == null) {
                            obj4 = obj5;
                            constraintWidget = constraintWidget2;
                        } else {
                            obj4 = 1;
                        }
                    }
                }
            }
            constraintWidget2 = null;
            if (constraintWidget2 == null) {
                obj4 = 1;
            } else {
                obj4 = obj5;
                constraintWidget = constraintWidget2;
            }
        }
        resolutionNode = constraintWidget5.mListAnchors[i2].getResolutionNode();
        i5 = i2 + 1;
        resolutionNode2 = constraintWidget6.mListAnchors[i5].getResolutionNode();
        constraintWidget3 = constraintWidget5;
        if (resolutionNode.target != null) {
            if (resolutionNode2.target == null) {
                if (resolutionNode.target.state == 1) {
                }
                if (i4 <= 0) {
                }
                if (obj2 != null) {
                    f3 = 0.0f;
                    f4 = resolutionNode.target.resolvedOffset;
                    f5 = resolutionNode2.target.resolvedOffset;
                    if (f4 >= f5) {
                    }
                    if (i4 > 0) {
                    }
                    i6 = i5;
                    linearSystem2 = linearSystem;
                    if (f5 >= f) {
                        return false;
                    }
                    if (obj3 == null) {
                        f4 += (f5 - f3) * constraintWidget3.getHorizontalBiasPercent();
                        while (constraintWidget7 != null) {
                            if (LinearSystem.sMetrics != null) {
                                metrics = LinearSystem.sMetrics;
                                metrics.nonresolvedWidgets--;
                                metrics = LinearSystem.sMetrics;
                                metrics.resolvedWidgets++;
                                metrics = LinearSystem.sMetrics;
                                metrics.chainConnectionResolved++;
                            }
                            constraintWidget4 = constraintWidget7.mListNextVisibleWidget[i];
                            if (constraintWidget4 == null) {
                            }
                            if (i != 0) {
                                width = (float) constraintWidget7.getWidth();
                            } else {
                                width = (float) constraintWidget7.getHeight();
                            }
                            f4 += (float) constraintWidget7.mListAnchors[i2].getMargin();
                            constraintWidget7.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, f4);
                            f4 += width;
                            constraintWidget7.mListAnchors[i6].getResolutionNode().resolve(resolutionNode.resolvedTarget, f4);
                            constraintWidget7.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                            constraintWidget7.mListAnchors[i6].getResolutionNode().addResolvedValue(linearSystem2);
                            f4 += (float) constraintWidget7.mListAnchors[i6].getMargin();
                            constraintWidget7 = constraintWidget4;
                        }
                    } else {
                        if (obj == null) {
                            f5 -= f3;
                        } else if (obj2 != null) {
                            f5 -= f3;
                        }
                        f3 = f5 / ((float) (i3 + 1));
                        if (obj2 != null) {
                            if (i3 <= 1) {
                            }
                        }
                        width = f4 + f3;
                        width = ((float) constraintWidget7.mListAnchors[i2].getMargin()) + f4;
                        width += (float) constraintWidget7.mListAnchors[i2].getMargin();
                        while (constraintWidget7 != null) {
                            if (LinearSystem.sMetrics != null) {
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.nonresolvedWidgets--;
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.resolvedWidgets++;
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.chainConnectionResolved++;
                            }
                            constraintWidget5 = constraintWidget7.mListNextVisibleWidget[i];
                            if (constraintWidget5 == null) {
                            }
                            if (i != 0) {
                                f5 = (float) constraintWidget7.getWidth();
                            } else {
                                f5 = (float) constraintWidget7.getHeight();
                            }
                            constraintWidget7.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, width);
                            constraintWidget7.mListAnchors[i6].getResolutionNode().resolve(resolutionNode.resolvedTarget, width + f5);
                            constraintWidget7.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                            constraintWidget7.mListAnchors[i6].getResolutionNode().addResolvedValue(linearSystem2);
                            width += f5 + f3;
                            constraintWidget7 = constraintWidget5;
                        }
                    }
                    return true;
                }
                if (constraintWidget7 == null) {
                }
                if (constraintWidget8 != null) {
                    f3 += (float) constraintWidget8.mListAnchors[i5].getMargin();
                }
                f4 = resolutionNode.target.resolvedOffset;
                f5 = resolutionNode2.target.resolvedOffset;
                if (f4 >= f5) {
                }
                if (i4 > 0) {
                }
                i6 = i5;
                linearSystem2 = linearSystem;
                if (f5 >= f) {
                    return false;
                }
                if (obj3 == null) {
                    if (obj == null) {
                        f5 -= f3;
                    } else if (obj2 != null) {
                        f5 -= f3;
                    }
                    f3 = f5 / ((float) (i3 + 1));
                    if (obj2 != null) {
                        if (i3 <= 1) {
                        }
                    }
                    width = f4 + f3;
                    width = ((float) constraintWidget7.mListAnchors[i2].getMargin()) + f4;
                    width += (float) constraintWidget7.mListAnchors[i2].getMargin();
                    while (constraintWidget7 != null) {
                        if (LinearSystem.sMetrics != null) {
                            metrics2 = LinearSystem.sMetrics;
                            metrics2.nonresolvedWidgets--;
                            metrics2 = LinearSystem.sMetrics;
                            metrics2.resolvedWidgets++;
                            metrics2 = LinearSystem.sMetrics;
                            metrics2.chainConnectionResolved++;
                        }
                        constraintWidget5 = constraintWidget7.mListNextVisibleWidget[i];
                        if (constraintWidget5 == null) {
                        }
                        if (i != 0) {
                            f5 = (float) constraintWidget7.getHeight();
                        } else {
                            f5 = (float) constraintWidget7.getWidth();
                        }
                        constraintWidget7.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, width);
                        constraintWidget7.mListAnchors[i6].getResolutionNode().resolve(resolutionNode.resolvedTarget, width + f5);
                        constraintWidget7.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                        constraintWidget7.mListAnchors[i6].getResolutionNode().addResolvedValue(linearSystem2);
                        width += f5 + f3;
                        constraintWidget7 = constraintWidget5;
                    }
                } else {
                    f4 += (f5 - f3) * constraintWidget3.getHorizontalBiasPercent();
                    while (constraintWidget7 != null) {
                        if (LinearSystem.sMetrics != null) {
                            metrics = LinearSystem.sMetrics;
                            metrics.nonresolvedWidgets--;
                            metrics = LinearSystem.sMetrics;
                            metrics.resolvedWidgets++;
                            metrics = LinearSystem.sMetrics;
                            metrics.chainConnectionResolved++;
                        }
                        constraintWidget4 = constraintWidget7.mListNextVisibleWidget[i];
                        if (constraintWidget4 == null) {
                        }
                        if (i != 0) {
                            width = (float) constraintWidget7.getHeight();
                        } else {
                            width = (float) constraintWidget7.getWidth();
                        }
                        f4 += (float) constraintWidget7.mListAnchors[i2].getMargin();
                        constraintWidget7.mListAnchors[i2].getResolutionNode().resolve(resolutionNode.resolvedTarget, f4);
                        f4 += width;
                        constraintWidget7.mListAnchors[i6].getResolutionNode().resolve(resolutionNode.resolvedTarget, f4);
                        constraintWidget7.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                        constraintWidget7.mListAnchors[i6].getResolutionNode().addResolvedValue(linearSystem2);
                        f4 += (float) constraintWidget7.mListAnchors[i6].getMargin();
                        constraintWidget7 = constraintWidget4;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
