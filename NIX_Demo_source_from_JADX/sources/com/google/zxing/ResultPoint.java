package com.google.zxing;

import com.google.zxing.common.detector.MathUtils;

public class ResultPoint {

    /* renamed from: x */
    private final float f35x;

    /* renamed from: y */
    private final float f36y;

    public ResultPoint(float x, float y) {
        this.f35x = x;
        this.f36y = y;
    }

    public final float getX() {
        return this.f35x;
    }

    public final float getY() {
        return this.f36y;
    }

    public final boolean equals(Object other) {
        if (!(other instanceof ResultPoint)) {
            return false;
        }
        ResultPoint otherPoint = (ResultPoint) other;
        if (this.f35x == otherPoint.f35x && this.f36y == otherPoint.f36y) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return (Float.floatToIntBits(this.f35x) * 31) + Float.floatToIntBits(this.f36y);
    }

    public final String toString() {
        StringBuilder result = new StringBuilder(25);
        result.append('(');
        result.append(this.f35x);
        result.append(',');
        result.append(this.f36y);
        result.append(')');
        return result.toString();
    }

    public static void orderBestPatterns(ResultPoint[] patterns) {
        ResultPoint pointB;
        ResultPoint pointA;
        ResultPoint pointC;
        float zeroOneDistance = distance(patterns[0], patterns[1]);
        float oneTwoDistance = distance(patterns[1], patterns[2]);
        float zeroTwoDistance = distance(patterns[0], patterns[2]);
        if (oneTwoDistance >= zeroOneDistance && oneTwoDistance >= zeroTwoDistance) {
            pointB = patterns[0];
            pointA = patterns[1];
            pointC = patterns[2];
        } else if (zeroTwoDistance < oneTwoDistance || zeroTwoDistance < zeroOneDistance) {
            pointB = patterns[2];
            pointA = patterns[0];
            pointC = patterns[1];
        } else {
            pointB = patterns[1];
            pointA = patterns[0];
            pointC = patterns[2];
        }
        if (crossProductZ(pointA, pointB, pointC) < 0.0f) {
            ResultPoint temp = pointA;
            pointA = pointC;
            pointC = temp;
        }
        patterns[0] = pointA;
        patterns[1] = pointB;
        patterns[2] = pointC;
    }

    public static float distance(ResultPoint pattern1, ResultPoint pattern2) {
        return MathUtils.distance(pattern1.f35x, pattern1.f36y, pattern2.f35x, pattern2.f36y);
    }

    private static float crossProductZ(ResultPoint pointA, ResultPoint pointB, ResultPoint pointC) {
        float bX = pointB.f35x;
        float bY = pointB.f36y;
        return ((pointC.f35x - bX) * (pointA.f36y - bY)) - ((pointC.f36y - bY) * (pointA.f35x - bX));
    }
}
