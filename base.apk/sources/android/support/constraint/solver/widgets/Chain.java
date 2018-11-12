package android.support.constraint.solver.widgets;

import android.support.constraint.solver.ArrayRow;
import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;
import java.util.ArrayList;

class Chain {
    private static final boolean DEBUG = false;

    Chain() {
    }

    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i) {
        int i2;
        ChainHead[] chainHeadArr;
        int i3;
        int i4 = 0;
        if (i == 0) {
            i2 = constraintWidgetContainer.mHorizontalChainsSize;
            chainHeadArr = constraintWidgetContainer.mHorizontalChainsArray;
            i3 = i2;
            i2 = 0;
        } else {
            i2 = 2;
            int i5 = constraintWidgetContainer.mVerticalChainsSize;
            i3 = i5;
            chainHeadArr = constraintWidgetContainer.mVerticalChainsArray;
        }
        while (i4 < i3) {
            ChainHead chainHead = chainHeadArr[i4];
            chainHead.define();
            if (!constraintWidgetContainer.optimizeFor(4)) {
                applyChainConstraints(constraintWidgetContainer, linearSystem, i, i2, chainHead);
            } else if (!Optimizer.applyChainOptimized(constraintWidgetContainer, linearSystem, i, i2, chainHead)) {
                applyChainConstraints(constraintWidgetContainer, linearSystem, i, i2, chainHead);
            }
            i4++;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, int i2, ChainHead chainHead) {
        Object obj;
        Object obj2;
        Object obj3;
        Object obj4;
        Object obj5;
        Object obj6;
        ConstraintWidget constraintWidget;
        ConstraintWidget constraintWidget2;
        ConstraintAnchor constraintAnchor;
        int i3;
        int margin;
        float f;
        int i4;
        ConstraintWidget constraintWidget3;
        Object obj7;
        int i5;
        ConstraintWidget constraintWidget4;
        Object obj8;
        ConstraintAnchor constraintAnchor2;
        int i6;
        ArrayList arrayList;
        ConstraintWidget constraintWidget5;
        int i7;
        float f2;
        float f3;
        ArrayList arrayList2;
        SolverVariable solverVariable;
        int i8;
        SolverVariable solverVariable2;
        SolverVariable solverVariable3;
        SolverVariable solverVariable4;
        ArrayRow createRow;
        Object obj9;
        ConstraintWidget constraintWidget6;
        ConstraintWidget constraintWidget7;
        ConstraintWidget constraintWidget8;
        ConstraintAnchor constraintAnchor3;
        ConstraintWidget constraintWidget9;
        ConstraintAnchor constraintAnchor4;
        ConstraintAnchor constraintAnchor5;
        ConstraintAnchor constraintAnchor6;
        SolverVariable solverVariable5;
        ConstraintAnchor constraintAnchor7;
        ConstraintWidgetContainer constraintWidgetContainer2 = constraintWidgetContainer;
        LinearSystem linearSystem2 = linearSystem;
        ChainHead chainHead2 = chainHead;
        ConstraintWidget constraintWidget10 = chainHead2.mFirst;
        ConstraintWidget constraintWidget11 = chainHead2.mLast;
        ConstraintWidget constraintWidget12 = chainHead2.mFirstVisibleWidget;
        ConstraintWidget constraintWidget13 = chainHead2.mLastVisibleWidget;
        ConstraintWidget constraintWidget14 = chainHead2.mHead;
        float f4 = chainHead2.mTotalWeight;
        ConstraintWidget constraintWidget15 = chainHead2.mFirstMatchConstraintWidget;
        constraintWidget15 = chainHead2.mLastMatchConstraintWidget;
        Object obj10 = constraintWidgetContainer2.mListDimensionBehaviors[i] == DimensionBehaviour.WRAP_CONTENT ? 1 : null;
        if (i == 0) {
            obj = constraintWidget14.mHorizontalChainStyle == 0 ? 1 : null;
            obj2 = constraintWidget14.mHorizontalChainStyle == 1 ? 1 : null;
        } else {
            obj = constraintWidget14.mVerticalChainStyle == 0 ? 1 : null;
            obj2 = constraintWidget14.mVerticalChainStyle == 1 ? 1 : null;
            if (constraintWidget14.mVerticalChainStyle == 2) {
            }
            obj3 = null;
            obj4 = obj3;
            obj5 = obj2;
            obj6 = obj;
            constraintWidget = constraintWidget10;
            obj3 = null;
            while (true) {
                constraintWidget2 = null;
                if (obj3 == null) {
                    break;
                }
                constraintAnchor = constraintWidget.mListAnchors[i2];
                if (obj10 == null) {
                    if (obj4 != null) {
                        i3 = 4;
                        margin = constraintAnchor.getMargin();
                        f = f4;
                        if (!(constraintAnchor.mTarget == null || constraintWidget == constraintWidget10)) {
                            margin += constraintAnchor.mTarget.getMargin();
                        }
                        i4 = margin;
                        if (obj4 == null && constraintWidget != constraintWidget10 && constraintWidget != constraintWidget12) {
                            constraintWidget3 = constraintWidget14;
                            obj7 = obj3;
                            i5 = 6;
                        } else if (obj6 != null || obj10 == null) {
                            constraintWidget3 = constraintWidget14;
                            obj7 = obj3;
                            i5 = i3;
                        } else {
                            constraintWidget3 = constraintWidget14;
                            obj7 = obj3;
                            i5 = 4;
                        }
                        if (constraintAnchor.mTarget == null) {
                            if (constraintWidget != constraintWidget12) {
                                constraintWidget4 = constraintWidget10;
                                obj8 = obj4;
                                linearSystem2.addGreaterThan(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, 5);
                            } else {
                                obj8 = obj4;
                                constraintWidget4 = constraintWidget10;
                                linearSystem2.addGreaterThan(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, 6);
                            }
                            linearSystem2.addEquality(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, i5);
                        } else {
                            obj8 = obj4;
                            constraintWidget4 = constraintWidget10;
                        }
                        if (obj10 != null) {
                            if (constraintWidget.getVisibility() == 8 && constraintWidget.mListDimensionBehaviors[i] == DimensionBehaviour.MATCH_CONSTRAINT) {
                                i5 = 0;
                                linearSystem2.addGreaterThan(constraintWidget.mListAnchors[i2 + 1].mSolverVariable, constraintWidget.mListAnchors[i2].mSolverVariable, 0, 5);
                            } else {
                                i5 = 0;
                            }
                            linearSystem2.addGreaterThan(constraintWidget.mListAnchors[i2].mSolverVariable, constraintWidgetContainer2.mListAnchors[i2].mSolverVariable, i5, 6);
                        }
                        constraintAnchor2 = constraintWidget.mListAnchors[i2 + 1].mTarget;
                        if (constraintAnchor2 != null) {
                            constraintWidget14 = constraintAnchor2.mOwner;
                            if (constraintWidget14.mListAnchors[i2].mTarget != null) {
                                if (constraintWidget14.mListAnchors[i2].mTarget.mOwner == constraintWidget) {
                                    constraintWidget2 = constraintWidget14;
                                }
                            }
                        }
                        if (constraintWidget2 == null) {
                            constraintWidget = constraintWidget2;
                            obj3 = obj7;
                        } else {
                            obj3 = 1;
                        }
                        f4 = f;
                        constraintWidget14 = constraintWidget3;
                        constraintWidget10 = constraintWidget4;
                        obj4 = obj8;
                    }
                }
                i3 = 1;
                margin = constraintAnchor.getMargin();
                f = f4;
                margin += constraintAnchor.mTarget.getMargin();
                i4 = margin;
                if (obj4 == null) {
                }
                if (obj6 != null) {
                }
                constraintWidget3 = constraintWidget14;
                obj7 = obj3;
                i5 = i3;
                if (constraintAnchor.mTarget == null) {
                    obj8 = obj4;
                    constraintWidget4 = constraintWidget10;
                } else {
                    if (constraintWidget != constraintWidget12) {
                        obj8 = obj4;
                        constraintWidget4 = constraintWidget10;
                        linearSystem2.addGreaterThan(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, 6);
                    } else {
                        constraintWidget4 = constraintWidget10;
                        obj8 = obj4;
                        linearSystem2.addGreaterThan(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, 5);
                    }
                    linearSystem2.addEquality(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, i5);
                }
                if (obj10 != null) {
                    if (constraintWidget.getVisibility() == 8) {
                    }
                    i5 = 0;
                    linearSystem2.addGreaterThan(constraintWidget.mListAnchors[i2].mSolverVariable, constraintWidgetContainer2.mListAnchors[i2].mSolverVariable, i5, 6);
                }
                constraintAnchor2 = constraintWidget.mListAnchors[i2 + 1].mTarget;
                if (constraintAnchor2 != null) {
                    constraintWidget14 = constraintAnchor2.mOwner;
                    if (constraintWidget14.mListAnchors[i2].mTarget != null) {
                        if (constraintWidget14.mListAnchors[i2].mTarget.mOwner == constraintWidget) {
                            constraintWidget2 = constraintWidget14;
                        }
                    }
                }
                if (constraintWidget2 == null) {
                    obj3 = 1;
                } else {
                    constraintWidget = constraintWidget2;
                    obj3 = obj7;
                }
                f4 = f;
                constraintWidget14 = constraintWidget3;
                constraintWidget10 = constraintWidget4;
                obj4 = obj8;
            }
            constraintWidget3 = constraintWidget14;
            f = f4;
            obj8 = obj4;
            constraintWidget4 = constraintWidget10;
            if (constraintWidget13 != null) {
                i4 = i2 + 1;
                if (constraintWidget11.mListAnchors[i4].mTarget != null) {
                    int margin2;
                    constraintAnchor2 = constraintWidget13.mListAnchors[i4];
                    linearSystem2.addLowerThan(constraintAnchor2.mSolverVariable, constraintWidget11.mListAnchors[i4].mTarget.mSolverVariable, -constraintAnchor2.getMargin(), 5);
                    if (obj10 != null) {
                        i6 = i2 + 1;
                        linearSystem2.addGreaterThan(constraintWidgetContainer2.mListAnchors[i6].mSolverVariable, constraintWidget11.mListAnchors[i6].mSolverVariable, constraintWidget11.mListAnchors[i6].getMargin(), 6);
                    }
                    arrayList = chainHead2.mWeightedMatchConstraintsWidgets;
                    if (arrayList != null) {
                        i6 = arrayList.size();
                        if (i6 > 1) {
                            if (chainHead2.mHasUndefinedWeights && !chainHead2.mHasComplexMatchWeights) {
                                f = (float) chainHead2.mWidgetsMatchCount;
                            }
                            f4 = 0.0f;
                            constraintWidget5 = null;
                            i7 = 0;
                            f2 = 0.0f;
                            while (i7 < i6) {
                                constraintWidget = (ConstraintWidget) arrayList.get(i7);
                                f3 = constraintWidget.mWeight[i];
                                if (f3 >= f4) {
                                    if (chainHead2.mHasComplexMatchWeights) {
                                        f3 = 1.0f;
                                    } else {
                                        linearSystem2.addEquality(constraintWidget.mListAnchors[i2 + 1].mSolverVariable, constraintWidget.mListAnchors[i2].mSolverVariable, 0, 4);
                                        arrayList2 = arrayList;
                                        i7++;
                                        arrayList = arrayList2;
                                        f4 = 0.0f;
                                    }
                                }
                                if (f3 != f4) {
                                    linearSystem2.addEquality(constraintWidget.mListAnchors[i2 + 1].mSolverVariable, constraintWidget.mListAnchors[i2].mSolverVariable, 0, 6);
                                    arrayList2 = arrayList;
                                    i7++;
                                    arrayList = arrayList2;
                                    f4 = 0.0f;
                                } else {
                                    if (constraintWidget5 == null) {
                                        solverVariable = constraintWidget5.mListAnchors[i2].mSolverVariable;
                                        i8 = i2 + 1;
                                        solverVariable2 = constraintWidget5.mListAnchors[i8].mSolverVariable;
                                        solverVariable3 = constraintWidget.mListAnchors[i2].mSolverVariable;
                                        solverVariable4 = constraintWidget.mListAnchors[i8].mSolverVariable;
                                        arrayList2 = arrayList;
                                        createRow = linearSystem.createRow();
                                        createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                                        linearSystem2.addConstraint(createRow);
                                    } else {
                                        arrayList2 = arrayList;
                                    }
                                    constraintWidget5 = constraintWidget;
                                    f2 = f3;
                                    i7++;
                                    arrayList = arrayList2;
                                    f4 = 0.0f;
                                }
                            }
                        }
                    }
                    if (constraintWidget12 != null || (constraintWidget12 != constraintWidget13 && obj8 == null)) {
                        constraintWidget10 = constraintWidget4;
                        ConstraintWidget constraintWidget16;
                        SolverVariable solverVariable6;
                        int margin3;
                        ConstraintAnchor constraintAnchor8;
                        int margin4;
                        SolverVariable solverVariable7;
                        int i9;
                        if (obj6 == null && constraintWidget12 != null) {
                            obj9 = (chainHead2.mWidgetsMatchCount <= 0 || chainHead2.mWidgetsCount != chainHead2.mWidgetsMatchCount) ? null : 1;
                            constraintWidget6 = constraintWidget12;
                            constraintWidget7 = constraintWidget6;
                            while (constraintWidget7 != null) {
                                ConstraintWidget constraintWidget17;
                                ConstraintWidget constraintWidget18;
                                SolverVariable solverVariable8;
                                constraintWidget16 = constraintWidget7.mListNextVisibleWidget[i];
                                if (constraintWidget16 == null) {
                                    if (constraintWidget7 != constraintWidget13) {
                                        constraintWidget8 = constraintWidget16;
                                        constraintWidget17 = constraintWidget7;
                                        constraintWidget7 = constraintWidget8;
                                        constraintWidget6 = constraintWidget17;
                                    }
                                }
                                constraintAnchor3 = constraintWidget7.mListAnchors[i2];
                                solverVariable6 = constraintAnchor3.mSolverVariable;
                                solverVariable = constraintAnchor3.mTarget != null ? constraintAnchor3.mTarget.mSolverVariable : null;
                                if (constraintWidget6 != constraintWidget7) {
                                    solverVariable = constraintWidget6.mListAnchors[i2 + 1].mSolverVariable;
                                } else if (constraintWidget7 == constraintWidget12 && constraintWidget6 == constraintWidget7) {
                                    solverVariable = constraintWidget10.mListAnchors[i2].mTarget != null ? constraintWidget10.mListAnchors[i2].mTarget.mSolverVariable : null;
                                }
                                margin3 = constraintAnchor3.getMargin();
                                i5 = i2 + 1;
                                i7 = constraintWidget7.mListAnchors[i5].getMargin();
                                if (constraintWidget16 != null) {
                                    constraintAnchor8 = constraintWidget16.mListAnchors[i2];
                                    constraintWidget18 = constraintWidget16;
                                    solverVariable3 = constraintAnchor8.mSolverVariable;
                                    ConstraintAnchor constraintAnchor9 = constraintAnchor8;
                                    solverVariable8 = constraintWidget7.mListAnchors[i5].mSolverVariable;
                                    constraintAnchor8 = constraintAnchor9;
                                } else {
                                    ConstraintAnchor constraintAnchor10;
                                    constraintWidget18 = constraintWidget16;
                                    constraintAnchor8 = constraintWidget11.mListAnchors[i5].mTarget;
                                    if (constraintAnchor8 != null) {
                                        solverVariable3 = constraintAnchor8.mSolverVariable;
                                        constraintAnchor10 = constraintAnchor8;
                                    } else {
                                        constraintAnchor10 = constraintAnchor8;
                                        solverVariable3 = null;
                                    }
                                    solverVariable8 = constraintWidget7.mListAnchors[i5].mSolverVariable;
                                    constraintAnchor8 = constraintAnchor10;
                                }
                                if (constraintAnchor8 != null) {
                                    i7 += constraintAnchor8.getMargin();
                                }
                                if (constraintWidget6 != null) {
                                    margin3 += constraintWidget6.mListAnchors[i5].getMargin();
                                }
                                if (solverVariable6 == null || solverVariable == null || solverVariable3 == null || solverVariable8 == null) {
                                    constraintWidget17 = constraintWidget7;
                                    constraintWidget8 = constraintWidget18;
                                    constraintWidget7 = constraintWidget8;
                                    constraintWidget6 = constraintWidget17;
                                } else {
                                    margin2 = constraintWidget7 == constraintWidget12 ? constraintWidget12.mListAnchors[i2].getMargin() : margin3;
                                    margin4 = constraintWidget7 == constraintWidget13 ? constraintWidget13.mListAnchors[i5].getMargin() : i7;
                                    SolverVariable solverVariable9 = solverVariable6;
                                    solverVariable6 = solverVariable;
                                    i4 = margin2;
                                    solverVariable2 = solverVariable3;
                                    solverVariable7 = solverVariable8;
                                    constraintWidget8 = constraintWidget18;
                                    i9 = margin4;
                                    constraintWidget17 = constraintWidget7;
                                    linearSystem2.addCentering(solverVariable9, solverVariable6, i4, 0.5f, solverVariable2, solverVariable7, i9, obj9 != null ? 6 : 4);
                                    constraintWidget7 = constraintWidget8;
                                    constraintWidget6 = constraintWidget17;
                                }
                            }
                        } else if (!(obj5 == null || constraintWidget12 == null)) {
                            obj9 = (chainHead2.mWidgetsMatchCount > 0 || chainHead2.mWidgetsCount != chainHead2.mWidgetsMatchCount) ? null : 1;
                            constraintWidget6 = constraintWidget12;
                            constraintWidget7 = constraintWidget6;
                            while (constraintWidget7 != null) {
                                constraintWidget9 = constraintWidget7.mListNextVisibleWidget[i];
                                if (constraintWidget7 != constraintWidget12 || constraintWidget7 == constraintWidget13 || constraintWidget9 == null) {
                                    constraintWidget8 = constraintWidget7;
                                    constraintWidget7 = constraintWidget9;
                                } else {
                                    ConstraintWidget constraintWidget19;
                                    SolverVariable solverVariable10;
                                    ConstraintWidget constraintWidget20;
                                    constraintWidget16 = constraintWidget9 == constraintWidget13 ? null : constraintWidget9;
                                    constraintAnchor3 = constraintWidget7.mListAnchors[i2];
                                    solverVariable6 = constraintAnchor3.mSolverVariable;
                                    if (constraintAnchor3.mTarget != null) {
                                        solverVariable = constraintAnchor3.mTarget.mSolverVariable;
                                    }
                                    i7 = i2 + 1;
                                    solverVariable = constraintWidget6.mListAnchors[i7].mSolverVariable;
                                    margin3 = constraintAnchor3.getMargin();
                                    i5 = constraintWidget7.mListAnchors[i7].getMargin();
                                    if (constraintWidget16 != null) {
                                        constraintAnchor8 = constraintWidget16.mListAnchors[i2];
                                        constraintWidget19 = constraintWidget16;
                                        solverVariable10 = constraintAnchor8.mSolverVariable;
                                        solverVariable3 = constraintAnchor8.mTarget != null ? constraintAnchor8.mTarget.mSolverVariable : null;
                                    } else {
                                        ConstraintAnchor constraintAnchor11;
                                        constraintWidget19 = constraintWidget16;
                                        constraintAnchor8 = constraintWidget7.mListAnchors[i7].mTarget;
                                        if (constraintAnchor8 != null) {
                                            solverVariable3 = constraintAnchor8.mSolverVariable;
                                            constraintAnchor11 = constraintAnchor8;
                                        } else {
                                            constraintAnchor11 = constraintAnchor8;
                                            solverVariable3 = null;
                                        }
                                        solverVariable10 = solverVariable3;
                                        solverVariable3 = constraintWidget7.mListAnchors[i7].mSolverVariable;
                                        constraintAnchor8 = constraintAnchor11;
                                    }
                                    if (constraintAnchor8 != null) {
                                        i5 += constraintAnchor8.getMargin();
                                    }
                                    i8 = i5;
                                    if (constraintWidget6 != null) {
                                        margin3 += constraintWidget6.mListAnchors[i7].getMargin();
                                    }
                                    i7 = margin3;
                                    margin4 = obj9 != null ? 6 : 4;
                                    if (solverVariable6 == null || solverVariable == null || solverVariable10 == null || solverVariable3 == null) {
                                        constraintWidget8 = constraintWidget7;
                                        constraintWidget20 = constraintWidget19;
                                    } else {
                                        solverVariable7 = solverVariable3;
                                        constraintWidget20 = constraintWidget19;
                                        i9 = i8;
                                        constraintWidget8 = constraintWidget7;
                                        linearSystem2.addCentering(solverVariable6, solverVariable, i7, 0.5f, solverVariable10, solverVariable7, i9, margin4);
                                    }
                                    constraintWidget7 = constraintWidget20;
                                }
                                constraintWidget6 = constraintWidget8;
                            }
                            constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                            constraintAnchor3 = constraintWidget10.mListAnchors[i2].mTarget;
                            i4 = i2 + 1;
                            constraintAnchor5 = constraintWidget13.mListAnchors[i4];
                            constraintAnchor = constraintWidget11.mListAnchors[i4].mTarget;
                            if (constraintAnchor3 != null) {
                                if (constraintWidget12 == constraintWidget13) {
                                    linearSystem2.addEquality(constraintAnchor4.mSolverVariable, constraintAnchor3.mSolverVariable, constraintAnchor4.getMargin(), 5);
                                } else if (constraintAnchor != null) {
                                    constraintAnchor6 = constraintAnchor;
                                    linearSystem2.addCentering(constraintAnchor4.mSolverVariable, constraintAnchor3.mSolverVariable, constraintAnchor4.getMargin(), 0.5f, constraintAnchor5.mSolverVariable, constraintAnchor.mSolverVariable, constraintAnchor5.getMargin(), 5);
                                    if (!(constraintAnchor6 == null || constraintWidget12 == constraintWidget13)) {
                                        linearSystem2.addEquality(constraintAnchor5.mSolverVariable, constraintAnchor6.mSolverVariable, -constraintAnchor5.getMargin(), 5);
                                    }
                                }
                            }
                            constraintAnchor6 = constraintAnchor;
                            linearSystem2.addEquality(constraintAnchor5.mSolverVariable, constraintAnchor6.mSolverVariable, -constraintAnchor5.getMargin(), 5);
                        }
                    } else {
                        constraintWidget10 = constraintWidget4;
                        constraintAnchor4 = constraintWidget10.mListAnchors[i2];
                        i6 = i2 + 1;
                        constraintAnchor3 = constraintWidget11.mListAnchors[i6];
                        solverVariable = constraintWidget10.mListAnchors[i2].mTarget != null ? constraintWidget10.mListAnchors[i2].mTarget.mSolverVariable : null;
                        solverVariable2 = constraintWidget11.mListAnchors[i6].mTarget != null ? constraintWidget11.mListAnchors[i6].mTarget.mSolverVariable : null;
                        if (constraintWidget12 == constraintWidget13) {
                            constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                            constraintAnchor3 = constraintWidget12.mListAnchors[i6];
                        }
                        if (!(solverVariable == null || solverVariable2 == null)) {
                            float f5;
                            if (i == 0) {
                                f5 = constraintWidget3.mHorizontalBiasPercent;
                            } else {
                                f5 = constraintWidget3.mVerticalBiasPercent;
                            }
                            linearSystem2.addCentering(constraintAnchor4.mSolverVariable, solverVariable, constraintAnchor4.getMargin(), f5, solverVariable2, constraintAnchor3.mSolverVariable, constraintAnchor3.getMargin(), 5);
                        }
                    }
                    if ((obj6 == null || obj5 != null) && constraintWidget12 != null) {
                        constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                        i6 = i2 + 1;
                        constraintAnchor3 = constraintWidget13.mListAnchors[i6];
                        solverVariable = constraintAnchor4.mTarget != null ? constraintAnchor4.mTarget.mSolverVariable : null;
                        solverVariable5 = constraintAnchor3.mTarget != null ? constraintAnchor3.mTarget.mSolverVariable : null;
                        if (constraintWidget11 != constraintWidget13) {
                            constraintAnchor7 = constraintWidget11.mListAnchors[i6];
                            solverVariable5 = constraintAnchor7.mTarget != null ? constraintAnchor7.mTarget.mSolverVariable : null;
                        }
                        solverVariable2 = solverVariable5;
                        if (constraintWidget12 == constraintWidget13) {
                            constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                            constraintAnchor3 = constraintWidget12.mListAnchors[i6];
                        }
                        if (solverVariable != null && solverVariable2 != null) {
                            margin2 = constraintAnchor4.getMargin();
                            if (constraintWidget13 != null) {
                                constraintWidget11 = constraintWidget13;
                            }
                            linearSystem2.addCentering(constraintAnchor4.mSolverVariable, solverVariable, margin2, 0.5f, solverVariable2, constraintAnchor3.mSolverVariable, constraintWidget11.mListAnchors[i6].getMargin(), 5);
                            return;
                        }
                    }
                    return;
                }
            }
            if (obj10 != null) {
                i6 = i2 + 1;
                linearSystem2.addGreaterThan(constraintWidgetContainer2.mListAnchors[i6].mSolverVariable, constraintWidget11.mListAnchors[i6].mSolverVariable, constraintWidget11.mListAnchors[i6].getMargin(), 6);
            }
            arrayList = chainHead2.mWeightedMatchConstraintsWidgets;
            if (arrayList != null) {
                i6 = arrayList.size();
                if (i6 > 1) {
                    f = (float) chainHead2.mWidgetsMatchCount;
                    f4 = 0.0f;
                    constraintWidget5 = null;
                    i7 = 0;
                    f2 = 0.0f;
                    while (i7 < i6) {
                        constraintWidget = (ConstraintWidget) arrayList.get(i7);
                        f3 = constraintWidget.mWeight[i];
                        if (f3 >= f4) {
                            if (chainHead2.mHasComplexMatchWeights) {
                                f3 = 1.0f;
                            } else {
                                linearSystem2.addEquality(constraintWidget.mListAnchors[i2 + 1].mSolverVariable, constraintWidget.mListAnchors[i2].mSolverVariable, 0, 4);
                                arrayList2 = arrayList;
                                i7++;
                                arrayList = arrayList2;
                                f4 = 0.0f;
                            }
                        }
                        if (f3 != f4) {
                            if (constraintWidget5 == null) {
                                arrayList2 = arrayList;
                            } else {
                                solverVariable = constraintWidget5.mListAnchors[i2].mSolverVariable;
                                i8 = i2 + 1;
                                solverVariable2 = constraintWidget5.mListAnchors[i8].mSolverVariable;
                                solverVariable3 = constraintWidget.mListAnchors[i2].mSolverVariable;
                                solverVariable4 = constraintWidget.mListAnchors[i8].mSolverVariable;
                                arrayList2 = arrayList;
                                createRow = linearSystem.createRow();
                                createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                                linearSystem2.addConstraint(createRow);
                            }
                            constraintWidget5 = constraintWidget;
                            f2 = f3;
                            i7++;
                            arrayList = arrayList2;
                            f4 = 0.0f;
                        } else {
                            linearSystem2.addEquality(constraintWidget.mListAnchors[i2 + 1].mSolverVariable, constraintWidget.mListAnchors[i2].mSolverVariable, 0, 6);
                            arrayList2 = arrayList;
                            i7++;
                            arrayList = arrayList2;
                            f4 = 0.0f;
                        }
                    }
                }
            }
            if (constraintWidget12 != null) {
            }
            constraintWidget10 = constraintWidget4;
            if (obj6 == null) {
            }
            if (chainHead2.mWidgetsMatchCount > 0) {
            }
            constraintWidget6 = constraintWidget12;
            constraintWidget7 = constraintWidget6;
            while (constraintWidget7 != null) {
                constraintWidget9 = constraintWidget7.mListNextVisibleWidget[i];
                if (constraintWidget7 != constraintWidget12) {
                }
                constraintWidget8 = constraintWidget7;
                constraintWidget7 = constraintWidget9;
                constraintWidget6 = constraintWidget8;
            }
            constraintAnchor4 = constraintWidget12.mListAnchors[i2];
            constraintAnchor3 = constraintWidget10.mListAnchors[i2].mTarget;
            i4 = i2 + 1;
            constraintAnchor5 = constraintWidget13.mListAnchors[i4];
            constraintAnchor = constraintWidget11.mListAnchors[i4].mTarget;
            if (constraintAnchor3 != null) {
                if (constraintWidget12 == constraintWidget13) {
                    linearSystem2.addEquality(constraintAnchor4.mSolverVariable, constraintAnchor3.mSolverVariable, constraintAnchor4.getMargin(), 5);
                } else if (constraintAnchor != null) {
                    constraintAnchor6 = constraintAnchor;
                    linearSystem2.addCentering(constraintAnchor4.mSolverVariable, constraintAnchor3.mSolverVariable, constraintAnchor4.getMargin(), 0.5f, constraintAnchor5.mSolverVariable, constraintAnchor.mSolverVariable, constraintAnchor5.getMargin(), 5);
                    linearSystem2.addEquality(constraintAnchor5.mSolverVariable, constraintAnchor6.mSolverVariable, -constraintAnchor5.getMargin(), 5);
                    if (obj6 == null) {
                    }
                    constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                    i6 = i2 + 1;
                    constraintAnchor3 = constraintWidget13.mListAnchors[i6];
                    if (constraintAnchor4.mTarget != null) {
                    }
                    if (constraintAnchor3.mTarget != null) {
                    }
                    if (constraintWidget11 != constraintWidget13) {
                        constraintAnchor7 = constraintWidget11.mListAnchors[i6];
                        if (constraintAnchor7.mTarget != null) {
                        }
                    }
                    solverVariable2 = solverVariable5;
                    if (constraintWidget12 == constraintWidget13) {
                        constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                        constraintAnchor3 = constraintWidget12.mListAnchors[i6];
                    }
                    if (solverVariable != null) {
                    }
                }
            }
            constraintAnchor6 = constraintAnchor;
            linearSystem2.addEquality(constraintAnchor5.mSolverVariable, constraintAnchor6.mSolverVariable, -constraintAnchor5.getMargin(), 5);
            if (obj6 == null) {
            }
            constraintAnchor4 = constraintWidget12.mListAnchors[i2];
            i6 = i2 + 1;
            constraintAnchor3 = constraintWidget13.mListAnchors[i6];
            if (constraintAnchor4.mTarget != null) {
            }
            if (constraintAnchor3.mTarget != null) {
            }
            if (constraintWidget11 != constraintWidget13) {
                constraintAnchor7 = constraintWidget11.mListAnchors[i6];
                if (constraintAnchor7.mTarget != null) {
                }
            }
            solverVariable2 = solverVariable5;
            if (constraintWidget12 == constraintWidget13) {
                constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                constraintAnchor3 = constraintWidget12.mListAnchors[i6];
            }
            if (solverVariable != null) {
            }
        }
        obj3 = 1;
        obj4 = obj3;
        obj5 = obj2;
        obj6 = obj;
        constraintWidget = constraintWidget10;
        obj3 = null;
        while (true) {
            constraintWidget2 = null;
            if (obj3 == null) {
                break;
            }
            constraintAnchor = constraintWidget.mListAnchors[i2];
            if (obj10 == null) {
                if (obj4 != null) {
                    i3 = 4;
                    margin = constraintAnchor.getMargin();
                    f = f4;
                    margin += constraintAnchor.mTarget.getMargin();
                    i4 = margin;
                    if (obj4 == null) {
                    }
                    if (obj6 != null) {
                    }
                    constraintWidget3 = constraintWidget14;
                    obj7 = obj3;
                    i5 = i3;
                    if (constraintAnchor.mTarget == null) {
                        if (constraintWidget != constraintWidget12) {
                            constraintWidget4 = constraintWidget10;
                            obj8 = obj4;
                            linearSystem2.addGreaterThan(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, 5);
                        } else {
                            obj8 = obj4;
                            constraintWidget4 = constraintWidget10;
                            linearSystem2.addGreaterThan(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, 6);
                        }
                        linearSystem2.addEquality(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, i5);
                    } else {
                        obj8 = obj4;
                        constraintWidget4 = constraintWidget10;
                    }
                    if (obj10 != null) {
                        if (constraintWidget.getVisibility() == 8) {
                        }
                        i5 = 0;
                        linearSystem2.addGreaterThan(constraintWidget.mListAnchors[i2].mSolverVariable, constraintWidgetContainer2.mListAnchors[i2].mSolverVariable, i5, 6);
                    }
                    constraintAnchor2 = constraintWidget.mListAnchors[i2 + 1].mTarget;
                    if (constraintAnchor2 != null) {
                        constraintWidget14 = constraintAnchor2.mOwner;
                        if (constraintWidget14.mListAnchors[i2].mTarget != null) {
                            if (constraintWidget14.mListAnchors[i2].mTarget.mOwner == constraintWidget) {
                                constraintWidget2 = constraintWidget14;
                            }
                        }
                    }
                    if (constraintWidget2 == null) {
                        constraintWidget = constraintWidget2;
                        obj3 = obj7;
                    } else {
                        obj3 = 1;
                    }
                    f4 = f;
                    constraintWidget14 = constraintWidget3;
                    constraintWidget10 = constraintWidget4;
                    obj4 = obj8;
                }
            }
            i3 = 1;
            margin = constraintAnchor.getMargin();
            f = f4;
            margin += constraintAnchor.mTarget.getMargin();
            i4 = margin;
            if (obj4 == null) {
            }
            if (obj6 != null) {
            }
            constraintWidget3 = constraintWidget14;
            obj7 = obj3;
            i5 = i3;
            if (constraintAnchor.mTarget == null) {
                obj8 = obj4;
                constraintWidget4 = constraintWidget10;
            } else {
                if (constraintWidget != constraintWidget12) {
                    obj8 = obj4;
                    constraintWidget4 = constraintWidget10;
                    linearSystem2.addGreaterThan(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, 6);
                } else {
                    constraintWidget4 = constraintWidget10;
                    obj8 = obj4;
                    linearSystem2.addGreaterThan(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, 5);
                }
                linearSystem2.addEquality(constraintAnchor.mSolverVariable, constraintAnchor.mTarget.mSolverVariable, i4, i5);
            }
            if (obj10 != null) {
                if (constraintWidget.getVisibility() == 8) {
                }
                i5 = 0;
                linearSystem2.addGreaterThan(constraintWidget.mListAnchors[i2].mSolverVariable, constraintWidgetContainer2.mListAnchors[i2].mSolverVariable, i5, 6);
            }
            constraintAnchor2 = constraintWidget.mListAnchors[i2 + 1].mTarget;
            if (constraintAnchor2 != null) {
                constraintWidget14 = constraintAnchor2.mOwner;
                if (constraintWidget14.mListAnchors[i2].mTarget != null) {
                    if (constraintWidget14.mListAnchors[i2].mTarget.mOwner == constraintWidget) {
                        constraintWidget2 = constraintWidget14;
                    }
                }
            }
            if (constraintWidget2 == null) {
                obj3 = 1;
            } else {
                constraintWidget = constraintWidget2;
                obj3 = obj7;
            }
            f4 = f;
            constraintWidget14 = constraintWidget3;
            constraintWidget10 = constraintWidget4;
            obj4 = obj8;
        }
        constraintWidget3 = constraintWidget14;
        f = f4;
        obj8 = obj4;
        constraintWidget4 = constraintWidget10;
        if (constraintWidget13 != null) {
            i4 = i2 + 1;
            if (constraintWidget11.mListAnchors[i4].mTarget != null) {
                constraintAnchor2 = constraintWidget13.mListAnchors[i4];
                linearSystem2.addLowerThan(constraintAnchor2.mSolverVariable, constraintWidget11.mListAnchors[i4].mTarget.mSolverVariable, -constraintAnchor2.getMargin(), 5);
                if (obj10 != null) {
                    i6 = i2 + 1;
                    linearSystem2.addGreaterThan(constraintWidgetContainer2.mListAnchors[i6].mSolverVariable, constraintWidget11.mListAnchors[i6].mSolverVariable, constraintWidget11.mListAnchors[i6].getMargin(), 6);
                }
                arrayList = chainHead2.mWeightedMatchConstraintsWidgets;
                if (arrayList != null) {
                    i6 = arrayList.size();
                    if (i6 > 1) {
                        f = (float) chainHead2.mWidgetsMatchCount;
                        f4 = 0.0f;
                        constraintWidget5 = null;
                        i7 = 0;
                        f2 = 0.0f;
                        while (i7 < i6) {
                            constraintWidget = (ConstraintWidget) arrayList.get(i7);
                            f3 = constraintWidget.mWeight[i];
                            if (f3 >= f4) {
                                if (chainHead2.mHasComplexMatchWeights) {
                                    linearSystem2.addEquality(constraintWidget.mListAnchors[i2 + 1].mSolverVariable, constraintWidget.mListAnchors[i2].mSolverVariable, 0, 4);
                                    arrayList2 = arrayList;
                                    i7++;
                                    arrayList = arrayList2;
                                    f4 = 0.0f;
                                } else {
                                    f3 = 1.0f;
                                }
                            }
                            if (f3 != f4) {
                                linearSystem2.addEquality(constraintWidget.mListAnchors[i2 + 1].mSolverVariable, constraintWidget.mListAnchors[i2].mSolverVariable, 0, 6);
                                arrayList2 = arrayList;
                                i7++;
                                arrayList = arrayList2;
                                f4 = 0.0f;
                            } else {
                                if (constraintWidget5 == null) {
                                    solverVariable = constraintWidget5.mListAnchors[i2].mSolverVariable;
                                    i8 = i2 + 1;
                                    solverVariable2 = constraintWidget5.mListAnchors[i8].mSolverVariable;
                                    solverVariable3 = constraintWidget.mListAnchors[i2].mSolverVariable;
                                    solverVariable4 = constraintWidget.mListAnchors[i8].mSolverVariable;
                                    arrayList2 = arrayList;
                                    createRow = linearSystem.createRow();
                                    createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                                    linearSystem2.addConstraint(createRow);
                                } else {
                                    arrayList2 = arrayList;
                                }
                                constraintWidget5 = constraintWidget;
                                f2 = f3;
                                i7++;
                                arrayList = arrayList2;
                                f4 = 0.0f;
                            }
                        }
                    }
                }
                if (constraintWidget12 != null) {
                }
                constraintWidget10 = constraintWidget4;
                if (obj6 == null) {
                }
                if (chainHead2.mWidgetsMatchCount > 0) {
                }
                constraintWidget6 = constraintWidget12;
                constraintWidget7 = constraintWidget6;
                while (constraintWidget7 != null) {
                    constraintWidget9 = constraintWidget7.mListNextVisibleWidget[i];
                    if (constraintWidget7 != constraintWidget12) {
                    }
                    constraintWidget8 = constraintWidget7;
                    constraintWidget7 = constraintWidget9;
                    constraintWidget6 = constraintWidget8;
                }
                constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                constraintAnchor3 = constraintWidget10.mListAnchors[i2].mTarget;
                i4 = i2 + 1;
                constraintAnchor5 = constraintWidget13.mListAnchors[i4];
                constraintAnchor = constraintWidget11.mListAnchors[i4].mTarget;
                if (constraintAnchor3 != null) {
                    if (constraintWidget12 == constraintWidget13) {
                        linearSystem2.addEquality(constraintAnchor4.mSolverVariable, constraintAnchor3.mSolverVariable, constraintAnchor4.getMargin(), 5);
                    } else if (constraintAnchor != null) {
                        constraintAnchor6 = constraintAnchor;
                        linearSystem2.addCentering(constraintAnchor4.mSolverVariable, constraintAnchor3.mSolverVariable, constraintAnchor4.getMargin(), 0.5f, constraintAnchor5.mSolverVariable, constraintAnchor.mSolverVariable, constraintAnchor5.getMargin(), 5);
                        linearSystem2.addEquality(constraintAnchor5.mSolverVariable, constraintAnchor6.mSolverVariable, -constraintAnchor5.getMargin(), 5);
                        if (obj6 == null) {
                        }
                        constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                        i6 = i2 + 1;
                        constraintAnchor3 = constraintWidget13.mListAnchors[i6];
                        if (constraintAnchor4.mTarget != null) {
                        }
                        if (constraintAnchor3.mTarget != null) {
                        }
                        if (constraintWidget11 != constraintWidget13) {
                            constraintAnchor7 = constraintWidget11.mListAnchors[i6];
                            if (constraintAnchor7.mTarget != null) {
                            }
                        }
                        solverVariable2 = solverVariable5;
                        if (constraintWidget12 == constraintWidget13) {
                            constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                            constraintAnchor3 = constraintWidget12.mListAnchors[i6];
                        }
                        if (solverVariable != null) {
                        }
                    }
                }
                constraintAnchor6 = constraintAnchor;
                linearSystem2.addEquality(constraintAnchor5.mSolverVariable, constraintAnchor6.mSolverVariable, -constraintAnchor5.getMargin(), 5);
                if (obj6 == null) {
                }
                constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                i6 = i2 + 1;
                constraintAnchor3 = constraintWidget13.mListAnchors[i6];
                if (constraintAnchor4.mTarget != null) {
                }
                if (constraintAnchor3.mTarget != null) {
                }
                if (constraintWidget11 != constraintWidget13) {
                    constraintAnchor7 = constraintWidget11.mListAnchors[i6];
                    if (constraintAnchor7.mTarget != null) {
                    }
                }
                solverVariable2 = solverVariable5;
                if (constraintWidget12 == constraintWidget13) {
                    constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                    constraintAnchor3 = constraintWidget12.mListAnchors[i6];
                }
                if (solverVariable != null) {
                }
            }
        }
        if (obj10 != null) {
            i6 = i2 + 1;
            linearSystem2.addGreaterThan(constraintWidgetContainer2.mListAnchors[i6].mSolverVariable, constraintWidget11.mListAnchors[i6].mSolverVariable, constraintWidget11.mListAnchors[i6].getMargin(), 6);
        }
        arrayList = chainHead2.mWeightedMatchConstraintsWidgets;
        if (arrayList != null) {
            i6 = arrayList.size();
            if (i6 > 1) {
                f = (float) chainHead2.mWidgetsMatchCount;
                f4 = 0.0f;
                constraintWidget5 = null;
                i7 = 0;
                f2 = 0.0f;
                while (i7 < i6) {
                    constraintWidget = (ConstraintWidget) arrayList.get(i7);
                    f3 = constraintWidget.mWeight[i];
                    if (f3 >= f4) {
                        if (chainHead2.mHasComplexMatchWeights) {
                            f3 = 1.0f;
                        } else {
                            linearSystem2.addEquality(constraintWidget.mListAnchors[i2 + 1].mSolverVariable, constraintWidget.mListAnchors[i2].mSolverVariable, 0, 4);
                            arrayList2 = arrayList;
                            i7++;
                            arrayList = arrayList2;
                            f4 = 0.0f;
                        }
                    }
                    if (f3 != f4) {
                        if (constraintWidget5 == null) {
                            arrayList2 = arrayList;
                        } else {
                            solverVariable = constraintWidget5.mListAnchors[i2].mSolverVariable;
                            i8 = i2 + 1;
                            solverVariable2 = constraintWidget5.mListAnchors[i8].mSolverVariable;
                            solverVariable3 = constraintWidget.mListAnchors[i2].mSolverVariable;
                            solverVariable4 = constraintWidget.mListAnchors[i8].mSolverVariable;
                            arrayList2 = arrayList;
                            createRow = linearSystem.createRow();
                            createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                            linearSystem2.addConstraint(createRow);
                        }
                        constraintWidget5 = constraintWidget;
                        f2 = f3;
                        i7++;
                        arrayList = arrayList2;
                        f4 = 0.0f;
                    } else {
                        linearSystem2.addEquality(constraintWidget.mListAnchors[i2 + 1].mSolverVariable, constraintWidget.mListAnchors[i2].mSolverVariable, 0, 6);
                        arrayList2 = arrayList;
                        i7++;
                        arrayList = arrayList2;
                        f4 = 0.0f;
                    }
                }
            }
        }
        if (constraintWidget12 != null) {
        }
        constraintWidget10 = constraintWidget4;
        if (obj6 == null) {
        }
        if (chainHead2.mWidgetsMatchCount > 0) {
        }
        constraintWidget6 = constraintWidget12;
        constraintWidget7 = constraintWidget6;
        while (constraintWidget7 != null) {
            constraintWidget9 = constraintWidget7.mListNextVisibleWidget[i];
            if (constraintWidget7 != constraintWidget12) {
            }
            constraintWidget8 = constraintWidget7;
            constraintWidget7 = constraintWidget9;
            constraintWidget6 = constraintWidget8;
        }
        constraintAnchor4 = constraintWidget12.mListAnchors[i2];
        constraintAnchor3 = constraintWidget10.mListAnchors[i2].mTarget;
        i4 = i2 + 1;
        constraintAnchor5 = constraintWidget13.mListAnchors[i4];
        constraintAnchor = constraintWidget11.mListAnchors[i4].mTarget;
        if (constraintAnchor3 != null) {
            if (constraintWidget12 == constraintWidget13) {
                linearSystem2.addEquality(constraintAnchor4.mSolverVariable, constraintAnchor3.mSolverVariable, constraintAnchor4.getMargin(), 5);
            } else if (constraintAnchor != null) {
                constraintAnchor6 = constraintAnchor;
                linearSystem2.addCentering(constraintAnchor4.mSolverVariable, constraintAnchor3.mSolverVariable, constraintAnchor4.getMargin(), 0.5f, constraintAnchor5.mSolverVariable, constraintAnchor.mSolverVariable, constraintAnchor5.getMargin(), 5);
                linearSystem2.addEquality(constraintAnchor5.mSolverVariable, constraintAnchor6.mSolverVariable, -constraintAnchor5.getMargin(), 5);
                if (obj6 == null) {
                }
                constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                i6 = i2 + 1;
                constraintAnchor3 = constraintWidget13.mListAnchors[i6];
                if (constraintAnchor4.mTarget != null) {
                }
                if (constraintAnchor3.mTarget != null) {
                }
                if (constraintWidget11 != constraintWidget13) {
                    constraintAnchor7 = constraintWidget11.mListAnchors[i6];
                    if (constraintAnchor7.mTarget != null) {
                    }
                }
                solverVariable2 = solverVariable5;
                if (constraintWidget12 == constraintWidget13) {
                    constraintAnchor4 = constraintWidget12.mListAnchors[i2];
                    constraintAnchor3 = constraintWidget12.mListAnchors[i6];
                }
                if (solverVariable != null) {
                }
            }
        }
        constraintAnchor6 = constraintAnchor;
        linearSystem2.addEquality(constraintAnchor5.mSolverVariable, constraintAnchor6.mSolverVariable, -constraintAnchor5.getMargin(), 5);
        if (obj6 == null) {
        }
        constraintAnchor4 = constraintWidget12.mListAnchors[i2];
        i6 = i2 + 1;
        constraintAnchor3 = constraintWidget13.mListAnchors[i6];
        if (constraintAnchor4.mTarget != null) {
        }
        if (constraintAnchor3.mTarget != null) {
        }
        if (constraintWidget11 != constraintWidget13) {
            constraintAnchor7 = constraintWidget11.mListAnchors[i6];
            if (constraintAnchor7.mTarget != null) {
            }
        }
        solverVariable2 = solverVariable5;
        if (constraintWidget12 == constraintWidget13) {
            constraintAnchor4 = constraintWidget12.mListAnchors[i2];
            constraintAnchor3 = constraintWidget12.mListAnchors[i6];
        }
        if (solverVariable != null) {
        }
    }
}
