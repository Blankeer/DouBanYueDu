package com.wnafee.vector.compat;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.animation.AnimationUtils;
import com.douban.book.reader.helper.AppUri;
import com.wnafee.vector.R;
import com.wnafee.vector.compat.PathParser.PathDataNode;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class PathAnimatorInflater {
    private static final boolean DBG_ANIMATOR_INFLATER = false;
    private static final int SEQUENTIALLY = 1;
    private static final String TAG = "PathAnimatorInflater";
    private static final int TOGETHER = 0;
    private static final int VALUE_TYPE_PATH = 2;

    private static class PathDataEvaluator implements TypeEvaluator<PathDataNode[]> {
        private PathDataNode[] mNodeArray;

        private PathDataEvaluator() {
        }

        public PathDataEvaluator(PathDataNode[] nodeArray) {
            this.mNodeArray = nodeArray;
        }

        public PathDataNode[] evaluate(float fraction, PathDataNode[] startPathData, PathDataNode[] endPathData) {
            if (PathParser.canMorph(startPathData, endPathData)) {
                if (this.mNodeArray == null || !PathParser.canMorph(this.mNodeArray, startPathData)) {
                    this.mNodeArray = PathParser.deepCopyNodes(startPathData);
                }
                for (int i = PathAnimatorInflater.TOGETHER; i < startPathData.length; i += PathAnimatorInflater.SEQUENTIALLY) {
                    this.mNodeArray[i].interpolatePathDataNode(startPathData[i], endPathData[i], fraction);
                }
                return this.mNodeArray;
            }
            throw new IllegalArgumentException("Can't interpolate between two incompatible pathData");
        }
    }

    public static Animator loadAnimator(Context c, Resources resources, Theme theme, int id, float pathErrorScale) throws NotFoundException {
        NotFoundException rnf;
        XmlResourceParser xmlResourceParser = null;
        try {
            xmlResourceParser = resources.getAnimation(id);
            Animator createAnimatorFromXml = createAnimatorFromXml(c, resources, theme, xmlResourceParser, pathErrorScale);
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            return createAnimatorFromXml;
        } catch (XmlPullParserException ex) {
            rnf = new NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } catch (IOException ex2) {
            rnf = new NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(ex2);
            throw rnf;
        } catch (Throwable th) {
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
        }
    }

    private static Animator createAnimatorFromXml(Context c, Resources res, Theme theme, XmlPullParser parser, float pixelSize) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(c, res, theme, parser, Xml.asAttributeSet(parser), null, TOGETHER, pixelSize);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.animation.Animator createAnimatorFromXml(android.content.Context r23, android.content.res.Resources r24, android.content.res.Resources.Theme r25, org.xmlpull.v1.XmlPullParser r26, android.util.AttributeSet r27, android.animation.AnimatorSet r28, int r29, float r30) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r14 = 0;
        r16 = 0;
        r17 = r26.getDepth();
    L_0x0007:
        r22 = r26.next();
        r5 = 3;
        r0 = r22;
        if (r0 != r5) goto L_0x0018;
    L_0x0010:
        r5 = r26.getDepth();
        r0 = r17;
        if (r5 <= r0) goto L_0x00a7;
    L_0x0018:
        r5 = 1;
        r0 = r22;
        if (r0 == r5) goto L_0x00a7;
    L_0x001d:
        r5 = 2;
        r0 = r22;
        if (r0 != r5) goto L_0x0007;
    L_0x0022:
        r21 = r26.getName();
        r5 = "objectAnimator";
        r0 = r21;
        r5 = r0.equals(r5);
        if (r5 == 0) goto L_0x004d;
    L_0x0030:
        r0 = r23;
        r1 = r24;
        r2 = r25;
        r3 = r27;
        r4 = r30;
        r14 = loadObjectAnimator(r0, r1, r2, r3, r4);
    L_0x003e:
        if (r28 == 0) goto L_0x0007;
    L_0x0040:
        if (r16 != 0) goto L_0x0047;
    L_0x0042:
        r16 = new java.util.ArrayList;
        r16.<init>();
    L_0x0047:
        r0 = r16;
        r0.add(r14);
        goto L_0x0007;
    L_0x004d:
        r5 = "animator";
        r0 = r21;
        r5 = r0.equals(r5);
        if (r5 == 0) goto L_0x0067;
    L_0x0057:
        r9 = 0;
        r5 = r23;
        r6 = r24;
        r7 = r25;
        r8 = r27;
        r10 = r30;
        r14 = loadAnimator(r5, r6, r7, r8, r9, r10);
        goto L_0x003e;
    L_0x0067:
        r5 = "set";
        r0 = r21;
        r5 = r0.equals(r5);
        if (r5 == 0) goto L_0x008a;
    L_0x0071:
        r14 = new android.animation.AnimatorSet;
        r14.<init>();
        r10 = r14;
        r10 = (android.animation.AnimatorSet) r10;
        r11 = 0;
        r5 = r23;
        r6 = r24;
        r7 = r25;
        r8 = r26;
        r9 = r27;
        r12 = r30;
        createAnimatorFromXml(r5, r6, r7, r8, r9, r10, r11, r12);
        goto L_0x003e;
    L_0x008a:
        r5 = new java.lang.RuntimeException;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "Unknown animator name: ";
        r6 = r6.append(r7);
        r7 = r26.getName();
        r6 = r6.append(r7);
        r6 = r6.toString();
        r5.<init>(r6);
        throw r5;
    L_0x00a7:
        if (r28 == 0) goto L_0x00d1;
    L_0x00a9:
        if (r16 == 0) goto L_0x00d1;
    L_0x00ab:
        r5 = r16.size();
        r15 = new android.animation.Animator[r5];
        r19 = 0;
        r18 = r16.iterator();
    L_0x00b7:
        r5 = r18.hasNext();
        if (r5 == 0) goto L_0x00ca;
    L_0x00bd:
        r13 = r18.next();
        r13 = (android.animation.Animator) r13;
        r20 = r19 + 1;
        r15[r19] = r13;
        r19 = r20;
        goto L_0x00b7;
    L_0x00ca:
        if (r29 != 0) goto L_0x00d2;
    L_0x00cc:
        r0 = r28;
        r0.playTogether(r15);
    L_0x00d1:
        return r14;
    L_0x00d2:
        r0 = r28;
        r0.playSequentially(r15);
        goto L_0x00d1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.wnafee.vector.compat.PathAnimatorInflater.createAnimatorFromXml(android.content.Context, android.content.res.Resources, android.content.res.Resources$Theme, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.animation.AnimatorSet, int, float):android.animation.Animator");
    }

    private static ObjectAnimator loadObjectAnimator(Context c, Resources res, Theme theme, AttributeSet attrs, float pathErrorScale) throws NotFoundException {
        ObjectAnimator anim = new ObjectAnimator();
        loadAnimator(c, res, theme, attrs, anim, pathErrorScale);
        return anim;
    }

    private static ValueAnimator loadAnimator(Context c, Resources res, Theme theme, AttributeSet attrs, ValueAnimator anim, float pathErrorScale) throws NotFoundException {
        TypedArray arrayAnimator;
        TypedArray arrayObjectAnimator = null;
        if (theme != null) {
            arrayAnimator = theme.obtainStyledAttributes(attrs, R.styleable.Animator, TOGETHER, TOGETHER);
        } else {
            arrayAnimator = res.obtainAttributes(attrs, R.styleable.Animator);
        }
        if (anim != null) {
            if (theme != null) {
                arrayObjectAnimator = theme.obtainStyledAttributes(attrs, R.styleable.PropertyAnimator, TOGETHER, TOGETHER);
            } else {
                arrayObjectAnimator = res.obtainAttributes(attrs, R.styleable.PropertyAnimator);
            }
        }
        if (anim == null) {
            anim = new ValueAnimator();
        }
        parseAnimatorFromTypeArray(anim, arrayAnimator, arrayObjectAnimator);
        int resId = arrayAnimator.getResourceId(R.styleable.Animator_android_interpolator, TOGETHER);
        if (resId > 0) {
            anim.setInterpolator(AnimationUtils.loadInterpolator(c, resId));
        }
        arrayAnimator.recycle();
        if (arrayObjectAnimator != null) {
            arrayObjectAnimator.recycle();
        }
        return anim;
    }

    private static void parseAnimatorFromTypeArray(ValueAnimator anim, TypedArray arrayAnimator, TypedArray arrayObjectAnimator) {
        long duration = (long) arrayAnimator.getInt(R.styleable.Animator_android_duration, AppUri.READER);
        long startDelay = (long) arrayAnimator.getInt(R.styleable.Animator_android_startOffset, TOGETHER);
        if (arrayAnimator.getInt(R.styleable.Animator_vc_valueType, TOGETHER) == VALUE_TYPE_PATH) {
            TypeEvaluator evaluator = setupAnimatorForPath(anim, arrayAnimator);
            anim.setDuration(duration);
            anim.setStartDelay(startDelay);
            if (arrayAnimator.hasValue(R.styleable.Animator_android_repeatCount)) {
                anim.setRepeatCount(arrayAnimator.getInt(R.styleable.Animator_android_repeatCount, TOGETHER));
            }
            if (arrayAnimator.hasValue(R.styleable.Animator_android_repeatMode)) {
                anim.setRepeatMode(arrayAnimator.getInt(R.styleable.Animator_android_repeatMode, SEQUENTIALLY));
            }
            if (evaluator != null) {
                anim.setEvaluator(evaluator);
            }
            if (arrayObjectAnimator != null) {
                setupObjectAnimator(anim, arrayObjectAnimator);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("target is not a pathType target");
    }

    private static TypeEvaluator setupAnimatorForPath(ValueAnimator anim, TypedArray arrayAnimator) {
        String fromString = arrayAnimator.getString(R.styleable.Animator_android_valueFrom);
        String toString = arrayAnimator.getString(R.styleable.Animator_android_valueTo);
        PathDataNode[] nodesFrom = PathParser.createNodesFromPathData(fromString);
        PathDataNode[] nodesTo = PathParser.createNodesFromPathData(toString);
        Object[] objArr;
        if (nodesFrom != null) {
            if (nodesTo != null) {
                objArr = new Object[VALUE_TYPE_PATH];
                objArr[TOGETHER] = nodesFrom;
                objArr[SEQUENTIALLY] = nodesTo;
                anim.setObjectValues(objArr);
                if (!PathParser.canMorph(nodesFrom, nodesTo)) {
                    throw new InflateException(arrayAnimator.getPositionDescription() + " Can't morph from " + fromString + " to " + toString);
                }
            }
            objArr = new Object[SEQUENTIALLY];
            objArr[TOGETHER] = nodesFrom;
            anim.setObjectValues(objArr);
            return new PathDataEvaluator(PathParser.deepCopyNodes(nodesFrom));
        } else if (nodesTo == null) {
            return null;
        } else {
            objArr = new Object[SEQUENTIALLY];
            objArr[TOGETHER] = nodesTo;
            anim.setObjectValues(objArr);
            return new PathDataEvaluator(PathParser.deepCopyNodes(nodesTo));
        }
    }

    private static void setupObjectAnimator(ValueAnimator anim, TypedArray arrayObjectAnimator) {
        ((ObjectAnimator) anim).setPropertyName(arrayObjectAnimator.getString(R.styleable.PropertyAnimator_android_propertyName));
    }
}
