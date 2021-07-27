package com.yuqirong.flexiblesearchbarview;

import ohos.agp.utils.RectFloat;

/**
 * RectF class.
 */
public class RectF extends RectFloat {
    /**
     * Default constructor for RectF.
     */
    public RectF() {
        super();
    }

    /**
     * * Parameterised constructor for RectF.
     *
     * @param left left
     * @param top top
     * @param right right
     * @param bottom bottom
     */
    public RectF(final float left, final float top, final float right, final float bottom) {
        super(left, top, right, bottom);
    }

    /**
     * * set RectF coordinates.
     *
     * @param src src
     */
    public void set(final RectF src) {
        if (src == null) {
            return;
        }
        left = src.left;
        top = src.top;
        right = src.right;
        bottom = src.bottom;
    }
}
