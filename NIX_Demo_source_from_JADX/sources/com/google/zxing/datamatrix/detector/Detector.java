package com.google.zxing.datamatrix.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.common.GridSampler;
import com.google.zxing.common.detector.MathUtils;
import com.google.zxing.common.detector.WhiteRectangleDetector;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Detector {
    private final BitMatrix image;
    private final WhiteRectangleDetector rectangleDetector;

    private static final class ResultPointsAndTransitions {
        private final ResultPoint from;

        /* renamed from: to */
        private final ResultPoint f40to;
        private final int transitions;

        private ResultPointsAndTransitions(ResultPoint from2, ResultPoint to, int transitions2) {
            this.from = from2;
            this.f40to = to;
            this.transitions = transitions2;
        }

        /* synthetic */ ResultPointsAndTransitions(ResultPoint resultPoint, ResultPoint resultPoint2, int i, ResultPointsAndTransitions resultPointsAndTransitions) {
            this(resultPoint, resultPoint2, i);
        }

        /* access modifiers changed from: 0000 */
        public ResultPoint getFrom() {
            return this.from;
        }

        /* access modifiers changed from: 0000 */
        public ResultPoint getTo() {
            return this.f40to;
        }

        public int getTransitions() {
            return this.transitions;
        }

        public String toString() {
            return this.from + "/" + this.f40to + '/' + this.transitions;
        }
    }

    private static final class ResultPointsAndTransitionsComparator implements Comparator<ResultPointsAndTransitions>, Serializable {
        private ResultPointsAndTransitionsComparator() {
        }

        /* synthetic */ ResultPointsAndTransitionsComparator(ResultPointsAndTransitionsComparator resultPointsAndTransitionsComparator) {
            this();
        }

        public int compare(ResultPointsAndTransitions o1, ResultPointsAndTransitions o2) {
            return o1.getTransitions() - o2.getTransitions();
        }
    }

    public Detector(BitMatrix image2) throws NotFoundException {
        this.image = image2;
        this.rectangleDetector = new WhiteRectangleDetector(image2);
    }

    public DetectorResult detect() throws NotFoundException {
        ResultPoint topRight;
        BitMatrix bits;
        ResultPoint correctedTopRight;
        ResultPoint[] cornerPoints = this.rectangleDetector.detect();
        ResultPoint pointA = cornerPoints[0];
        ResultPoint pointB = cornerPoints[1];
        ResultPoint pointC = cornerPoints[2];
        ResultPoint pointD = cornerPoints[3];
        ArrayList arrayList = new ArrayList(4);
        arrayList.add(transitionsBetween(pointA, pointB));
        arrayList.add(transitionsBetween(pointA, pointC));
        arrayList.add(transitionsBetween(pointB, pointD));
        arrayList.add(transitionsBetween(pointC, pointD));
        Collections.sort(arrayList, new ResultPointsAndTransitionsComparator(null));
        ResultPointsAndTransitions lSideOne = (ResultPointsAndTransitions) arrayList.get(0);
        ResultPointsAndTransitions lSideTwo = (ResultPointsAndTransitions) arrayList.get(1);
        HashMap hashMap = new HashMap();
        increment(hashMap, lSideOne.getFrom());
        increment(hashMap, lSideOne.getTo());
        increment(hashMap, lSideTwo.getFrom());
        increment(hashMap, lSideTwo.getTo());
        ResultPoint maybeTopLeft = null;
        ResultPoint bottomLeft = null;
        ResultPoint maybeBottomRight = null;
        for (Entry<ResultPoint, Integer> entry : hashMap.entrySet()) {
            ResultPoint point = (ResultPoint) entry.getKey();
            if (((Integer) entry.getValue()).intValue() == 2) {
                bottomLeft = point;
            } else if (maybeTopLeft == null) {
                maybeTopLeft = point;
            } else {
                maybeBottomRight = point;
            }
        }
        if (maybeTopLeft == null || bottomLeft == null || maybeBottomRight == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        ResultPoint[] corners = {maybeTopLeft, bottomLeft, maybeBottomRight};
        ResultPoint.orderBestPatterns(corners);
        ResultPoint bottomRight = corners[0];
        ResultPoint bottomLeft2 = corners[1];
        ResultPoint topLeft = corners[2];
        if (!hashMap.containsKey(pointA)) {
            topRight = pointA;
        } else if (!hashMap.containsKey(pointB)) {
            topRight = pointB;
        } else if (!hashMap.containsKey(pointC)) {
            topRight = pointC;
        } else {
            topRight = pointD;
        }
        int dimensionTop = transitionsBetween(topLeft, topRight).getTransitions();
        int dimensionRight = transitionsBetween(bottomRight, topRight).getTransitions();
        if ((dimensionTop & 1) == 1) {
            dimensionTop++;
        }
        int dimensionTop2 = dimensionTop + 2;
        if ((dimensionRight & 1) == 1) {
            dimensionRight++;
        }
        int dimensionRight2 = dimensionRight + 2;
        if (dimensionTop2 * 4 >= dimensionRight2 * 7 || dimensionRight2 * 4 >= dimensionTop2 * 7) {
            ResultPoint correctedTopRight2 = correctTopRightRectangular(bottomLeft2, bottomRight, topLeft, topRight, dimensionTop2, dimensionRight2);
            if (correctedTopRight2 == null) {
                correctedTopRight2 = topRight;
            }
            int dimensionTop3 = transitionsBetween(topLeft, correctedTopRight).getTransitions();
            int dimensionRight3 = transitionsBetween(bottomRight, correctedTopRight).getTransitions();
            if ((dimensionTop3 & 1) == 1) {
                dimensionTop3++;
            }
            if ((dimensionRight3 & 1) == 1) {
                dimensionRight3++;
            }
            bits = sampleGrid(this.image, topLeft, bottomLeft2, bottomRight, correctedTopRight, dimensionTop3, dimensionRight3);
        } else {
            correctedTopRight = correctTopRight(bottomLeft2, bottomRight, topLeft, topRight, Math.min(dimensionRight2, dimensionTop2));
            if (correctedTopRight == null) {
                correctedTopRight = topRight;
            }
            int dimensionCorrected = Math.max(transitionsBetween(topLeft, correctedTopRight).getTransitions(), transitionsBetween(bottomRight, correctedTopRight).getTransitions()) + 1;
            if ((dimensionCorrected & 1) == 1) {
                dimensionCorrected++;
            }
            bits = sampleGrid(this.image, topLeft, bottomLeft2, bottomRight, correctedTopRight, dimensionCorrected, dimensionCorrected);
        }
        return new DetectorResult(bits, new ResultPoint[]{topLeft, bottomLeft2, bottomRight, correctedTopRight});
    }

    private ResultPoint correctTopRightRectangular(ResultPoint bottomLeft, ResultPoint bottomRight, ResultPoint topLeft, ResultPoint topRight, int dimensionTop, int dimensionRight) {
        float corr = ((float) distance(bottomLeft, bottomRight)) / ((float) dimensionTop);
        int norm = distance(topLeft, topRight);
        ResultPoint c1 = new ResultPoint(topRight.getX() + (corr * ((topRight.getX() - topLeft.getX()) / ((float) norm))), topRight.getY() + (corr * ((topRight.getY() - topLeft.getY()) / ((float) norm))));
        float corr2 = ((float) distance(bottomLeft, topLeft)) / ((float) dimensionRight);
        int norm2 = distance(bottomRight, topRight);
        ResultPoint c2 = new ResultPoint(topRight.getX() + (corr2 * ((topRight.getX() - bottomRight.getX()) / ((float) norm2))), topRight.getY() + (corr2 * ((topRight.getY() - bottomRight.getY()) / ((float) norm2))));
        if (!isValid(c1)) {
            if (isValid(c2)) {
                return c2;
            }
            return null;
        } else if (!isValid(c2)) {
            return c1;
        } else {
            if (Math.abs(dimensionTop - transitionsBetween(topLeft, c1).getTransitions()) + Math.abs(dimensionRight - transitionsBetween(bottomRight, c1).getTransitions()) <= Math.abs(dimensionTop - transitionsBetween(topLeft, c2).getTransitions()) + Math.abs(dimensionRight - transitionsBetween(bottomRight, c2).getTransitions())) {
                return c1;
            }
            return c2;
        }
    }

    private ResultPoint correctTopRight(ResultPoint bottomLeft, ResultPoint bottomRight, ResultPoint topLeft, ResultPoint topRight, int dimension) {
        float corr = ((float) distance(bottomLeft, bottomRight)) / ((float) dimension);
        int norm = distance(topLeft, topRight);
        ResultPoint c1 = new ResultPoint(topRight.getX() + (corr * ((topRight.getX() - topLeft.getX()) / ((float) norm))), topRight.getY() + (corr * ((topRight.getY() - topLeft.getY()) / ((float) norm))));
        float corr2 = ((float) distance(bottomLeft, topLeft)) / ((float) dimension);
        int norm2 = distance(bottomRight, topRight);
        ResultPoint c2 = new ResultPoint(topRight.getX() + (corr2 * ((topRight.getX() - bottomRight.getX()) / ((float) norm2))), topRight.getY() + (corr2 * ((topRight.getY() - bottomRight.getY()) / ((float) norm2))));
        if (isValid(c1)) {
            return (!isValid(c2) || Math.abs(transitionsBetween(topLeft, c1).getTransitions() - transitionsBetween(bottomRight, c1).getTransitions()) <= Math.abs(transitionsBetween(topLeft, c2).getTransitions() - transitionsBetween(bottomRight, c2).getTransitions())) ? c1 : c2;
        }
        if (isValid(c2)) {
            return c2;
        }
        return null;
    }

    private boolean isValid(ResultPoint p) {
        return p.getX() >= 0.0f && p.getX() < ((float) this.image.getWidth()) && p.getY() > 0.0f && p.getY() < ((float) this.image.getHeight());
    }

    private static int distance(ResultPoint a, ResultPoint b) {
        return MathUtils.round(ResultPoint.distance(a, b));
    }

    private static void increment(Map<ResultPoint, Integer> table, ResultPoint key) {
        Integer value = (Integer) table.get(key);
        table.put(key, Integer.valueOf(value == null ? 1 : value.intValue() + 1));
    }

    private static BitMatrix sampleGrid(BitMatrix image2, ResultPoint topLeft, ResultPoint bottomLeft, ResultPoint bottomRight, ResultPoint topRight, int dimensionX, int dimensionY) throws NotFoundException {
        return GridSampler.getInstance().sampleGrid(image2, dimensionX, dimensionY, 0.5f, 0.5f, ((float) dimensionX) - 0.5f, 0.5f, ((float) dimensionX) - 0.5f, ((float) dimensionY) - 0.5f, 0.5f, ((float) dimensionY) - 0.5f, topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY(), bottomRight.getX(), bottomRight.getY(), bottomLeft.getX(), bottomLeft.getY());
    }

    private ResultPointsAndTransitions transitionsBetween(ResultPoint from, ResultPoint to) {
        int i;
        int i2;
        int fromX = (int) from.getX();
        int fromY = (int) from.getY();
        int toX = (int) to.getX();
        int toY = (int) to.getY();
        boolean steep = Math.abs(toY - fromY) > Math.abs(toX - fromX);
        if (steep) {
            int temp = fromX;
            fromX = fromY;
            fromY = temp;
            int temp2 = toX;
            toX = toY;
            toY = temp2;
        }
        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);
        int error = (-dx) / 2;
        int ystep = fromY < toY ? 1 : -1;
        int xstep = fromX < toX ? 1 : -1;
        int transitions = 0;
        BitMatrix bitMatrix = this.image;
        if (steep) {
            i = fromY;
        } else {
            i = fromX;
        }
        boolean inBlack = bitMatrix.get(i, steep ? fromX : fromY);
        int y = fromY;
        for (int x = fromX; x != toX; x += xstep) {
            BitMatrix bitMatrix2 = this.image;
            if (steep) {
                i2 = y;
            } else {
                i2 = x;
            }
            boolean isBlack = bitMatrix2.get(i2, steep ? x : y);
            if (isBlack != inBlack) {
                transitions++;
                inBlack = isBlack;
            }
            error += dy;
            if (error > 0) {
                if (y == toY) {
                    break;
                }
                y += ystep;
                error -= dx;
            }
        }
        ResultPointsAndTransitions resultPointsAndTransitions = new ResultPointsAndTransitions(from, to, transitions, null);
        return resultPointsAndTransitions;
    }
}
