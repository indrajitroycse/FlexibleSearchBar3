package com.yuqirong.flexiblesearchbarview;

import java.io.IOException;
import java.util.Optional;
import ohos.agp.animation.Animator;
import ohos.agp.animation.AnimatorValue;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.render.PixelMapHolder;
import ohos.agp.text.Font;
import ohos.agp.utils.Color;
import ohos.app.Context;
import ohos.global.resource.NotExistException;
import ohos.global.resource.Resource;
import ohos.global.resource.ResourceManager;
import ohos.global.resource.WrongTypeException;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.PixelMap;

/**
 * SearchBarView class is Custom View.
 */
public class SearchBarView extends Component implements Component.DrawTask, Component.EstimateSizeListener {
    /**
     * Animation duration.
     */
    private static final int DEFAULT_ANIMATION = 4000;
    /**
     * Text size for searchbarview.
     */
    private static final int DEFAULT_TEXT_SIZE = 70;
    /**
     * searchbarview is open.
     */
    private static final int STATUS_OPEN = 1;
    /**
     * searchbarview is close.
     */
    private static final int STATUS_CLOSE = 1 << 2;
    /**
     * searchbarview process - open/close.
     */
    private static final int STATUS_PROCESS = 1 << 3;
    /**
     * Increment value during animation.
     */
    private static final int OFFSETINCREMENT = 100;
    /**
     * TYPE.
     */
    private static final int HILOG_TYPE = 3;
    /**
     * DOMAIN.
     */
    private static final int HILOG_DOMAIN = 0xD000F00;
    /**
     * LABEL.
     */
    private static final HiLogLabel LABEL = new HiLogLabel(HILOG_TYPE, HILOG_DOMAIN, "SearchBarView");
    /**
     *  draw rect white circular bg.
     */
    private final RectF drawRect = new RectF();
    /**
     * image on white circular bg.
     */
    private final RectF dstRect = new RectF();
    /**
     * image paint.
     */
    private final Paint pixelMapPaint = new Paint();
    /**
     * search paint.
     */
    private final Paint searchPaint = new Paint();
    /**
     * white circular radius.
     */
    private int drawableRadius;
    /**
     * open Animation listener.
     */
    private final AnimatorValue open = new AnimatorValue();
    /**
     * close Animation listener.
     */
    private final AnimatorValue close = new AnimatorValue();
    /**
     * position from left.
     */
    private int left;
    /**
     * position from top.
     */
    private int top;
    /**
     * position from bottom.
     */
    private int bottom;
    /**
     * width.
     */
    private int width;
    /**
     * status.
     */
    private int status;
    /**
     * offset.
     */
    private float offSet;
    /**
     * isOpen.
     */
    private boolean openAnimation;
    /**
     * drawble object.
     */
    private PixelMap pixelMap;
    /**
     * paint for text.
     */
    private String textPaint;
    /**
     * Color for border.
     */
    private Color borderColor;
    /**
     * Color for border paint.
     */
    private Color borderColorPaint;

    /**
     * constructor.
     * @param context context
     */
    public SearchBarView(final Context context) {
        super(context);
        init();
    }

    /**
     * 2-arg constructor.
     * @param context context
     * @param attrs attrs
     */
    public SearchBarView(final Context context, final AttrSet attrs) {
        this(context, attrs, null);
    }

    /**
     * 3-arg constructor.
     * @param context context
     * @param attrs attrs
     * @param defStyle defstyle
     */
    public SearchBarView(final Context context, final AttrSet attrs, final String defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * intialize.
     * bitmap
     * paint
     * listeners
     */
    private void init() {
        paintAndRadiusInit();
        drawBitmap();
        addDrawTask(this);
        setEstimateSizeListener(this);
        animationListeners();
    }

    /**
     * paint and radius.
     */
    private void paintAndRadiusInit() {
        bottom = getHeight() - getPaddingBottom();
        top = getPaddingTop();
        try {
            textPaint =  getContext().getResourceManager().getElement(ResourceTable.String_search_content).getString();
        } catch (IOException | NotExistException | WrongTypeException e) {
            e.printStackTrace();
        }
        setBorderColor(Color.WHITE);
        setBorderColorPaint(Color.DKGRAY);
        searchPaint.setTextSize(DEFAULT_TEXT_SIZE);
        searchPaint.setFont(Font.DEFAULT_BOLD);
    }

    /**
     * test or set for border color.
     * @param color color
     */
    public void setBorderColor(final Color color) {
        pixelMapPaint.setColor(color);
        borderColor = color;
    }

    /**
     * get border color for test case.
     * @return color
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * test or set for border color paint.
     * @param color color
     */
    public void setBorderColorPaint(final Color color) {
        searchPaint.setColor(color);
        borderColorPaint = color;
    }

    /**
     * get border color paint for test case.
     * @return color
     */
    public Color getBorderColorPaint() {
        return borderColorPaint;
    }

    /**
     * draw search icon.
     */
    private void drawBitmap() {
        final Optional<PixelMap> pixelMapping = getPixelMapByResId(ResourceTable.Graphic_ic_search_category_default);
        pixelMapping.ifPresent(map -> this.pixelMap = map);
    }

    /**
     * get pixelmap.
     * @param resourceId resourceId
     * @return pixelmap
     */
    private Optional<PixelMap> getPixelMapByResId(final int resourceId) {
        final ResourceManager resourceManager = getContext().getResourceManager();
        Optional<PixelMap> pixelMapping = Optional.empty();
        if (resourceManager == null) {
            return Optional.empty();
        }
        try (Resource resource = resourceManager.getResource(resourceId)) {
            if (resource == null) {
                HiLog.error(LABEL, "get pixelmap failed, get resource by id is null");
                return Optional.empty();
            }
            pixelMapping = Utils.preparePixelmap(resource);
        } catch (NotExistException e) {
            HiLog.error(LABEL, "close output failed NotExistException");
        } catch (IOException e) {
            HiLog.error(LABEL, "close output failed IOException");
        }
        return pixelMapping;
    }

    /**
     * animation listeners.
     */
    private void animationListeners() {
        final AnimatorValue.ValueUpdateListener valUpdateListen = (final AnimatorValue animatorValue, final float pos) -> {
                if (openAnimation && offSet < (getWidth() - OFFSETINCREMENT)) {
                    offSet = offSet + OFFSETINCREMENT;
                    invalidate();
                } else if (!openAnimation && offSet > 0) {
                    offSet = offSet - OFFSETINCREMENT;
                    invalidate();
                }
        };
        openAnimationDurationAndListener(valUpdateListen);
        closeAnimationDurationAndListener(valUpdateListen);
    }

    /**
     * animation duration.
     * @param valUpdateListen valUpdateListen
     */
    private void openAnimationDurationAndListener(final AnimatorValue.ValueUpdateListener valUpdateListen) {
        open.setDuration(DEFAULT_ANIMATION);
        open.setValueUpdateListener(valUpdateListen);
        open.setStateChangedListener(new Animator.StateChangedListener() {
            @Override
            public void onStart(final Animator animator) {
                status = STATUS_PROCESS;
            }

            @Override
            public void onStop(final Animator animator) {
                // TO-DO
            }

            @Override
            public void onCancel(final Animator animator) {
                // TO-DO
            }

            @Override
            public void onEnd(final Animator animator) {
                status = STATUS_OPEN;
            }

            @Override
            public void onPause(final Animator animator) {
                // TO-DO
            }

            @Override
            public void onResume(final Animator animator) {
                // TO-DO
            }
        });
    }

    /**
     * closing anim duration.
     * @param valUpdateListen valUpdateListen
     */
    private void closeAnimationDurationAndListener(final AnimatorValue.ValueUpdateListener valUpdateListen) {
        close.setDuration(DEFAULT_ANIMATION);
        close.setValueUpdateListener(valUpdateListen);
        close.setStateChangedListener(new Animator.StateChangedListener() {
            @Override
            public void onStart(final Animator animator) {
                status = STATUS_PROCESS;
            }

            @Override
            public void onStop(final Animator animator) {
                // TO-DO
            }

            @Override
            public void onCancel(final Animator animator) {
                // TO-DO
            }

            @Override
            public void onEnd(final Animator animator) {
                status = STATUS_CLOSE;
            }

            @Override
            public void onPause(final Animator animator) {
                // TO-DO
            }

            @Override
            public void onResume(final Animator animator) {
                // TO-DO
            }
        });
    }

    /**
     * onDraw to re-paint.
     */
    @Override
    public void onDraw(final Component view, final Canvas canvas) {
        drawRect.set(calculateBounds());
        canvas.drawRoundRect(drawRect, drawableRadius, drawableRadius, pixelMapPaint);
        dstRect.set(calculateRectf());
        canvas.drawPixelMapHolderRect(new PixelMapHolder(pixelMap), dstRect, pixelMapPaint);
        if (openAnimation) {
            final Paint.FontMetrics fontMetrics = searchPaint.getFontMetrics();
            final double textHeight = Math.ceil(fontMetrics.descent - fontMetrics.ascent);
            canvas.drawText(searchPaint, textPaint, (float) left + 2 * drawableRadius, (float) (drawableRadius + textHeight / 2 - fontMetrics.descent));
        }
    }

    /**
     * calc icon size.
     * @return RectF
     */
    private RectF calculateRectf() {
        final int tempLeft = left + (int) ((1 - Math.sqrt(2) / 2) * drawableRadius);
        final int tempTop = top + (int) ((1 - Math.sqrt(2) / 2) * drawableRadius);
        final int tempRight = left + (int) ((1 + Math.sqrt(2) / 2) * drawableRadius);
        final int tempBottom = top + (int) ((1 + Math.sqrt(2) / 2) * drawableRadius);
        return new RectF((float) tempLeft, (float) tempTop, (float) tempRight, (float) tempBottom);
    }

    /**
     * calc white back-ground size.
     * @return RectF
     */
    private RectF calculateBounds() {
        int right;
        left = (int) (width - getPaddingRight() - 2 * drawableRadius - offSet);
        right = width - getPaddingRight();
        if (left < 0) {
            left = 0;
        }
        return new RectF((float) left, (float) top, (float) right, (float) bottom);
    }

    /**
     * to check anim is open.
     * @return isOPen
     */
    private boolean isOpen() {
        return status == STATUS_OPEN;
    }

    /**
     * to check anim is close.
     * @return isClose
     */
    private boolean isClose() {
        return status == STATUS_CLOSE;
    }

    /**
     * opens searchbarview.
     */
    public void startOpen() {
        if (isOpen()) {
            return;
        } else if (open.isRunning()) {
            return;
        } else if (close.isRunning()) {
            close.cancel();
        }
        setAnimationStatus(true);
        startAnimation();
    }

    /**
     * closes searchbarview.
     */
    public void startClose() {
        if (isClose()) {
            return;
        } else if (close.isRunning()) {
            return;
        } else if (open.isRunning()) {
            open.cancel();
        }
        setAnimationStatus(false);
        startAnimation();
    }

    /**
     * start anim.
     */
    private void startAnimation() {
        if (openAnimation) {
            open.start();
        } else {
            close.start();
        }
    }

    /**
     * to set anim status.
     * @param value value
     */
    private void setAnimationStatus(final boolean value) {
        openAnimation = value;
    }

    /**
     * check isOpened.
     * @return openAnimation
     */
    public boolean chekOpen() {
        return openAnimation;
    }

    /**
     * it will estimate.
     * width & height
     */
    @Override
    public boolean onEstimateSize(final int widthScreen, final int heightScreen) {
        final int widthMode = EstimateSpec.getMode(widthScreen);
        final int widthSize = EstimateSpec.getSize(widthScreen);
        final int heightMode = EstimateSpec.getMode(heightScreen);
        final int heightSize = EstimateSpec.getSize(heightScreen);
        int screenHeight;
        if (widthMode == EstimateSpec.PRECISE) {
            this.width = widthSize;
        }
        if (heightMode == EstimateSpec.PRECISE) {
            screenHeight = heightSize;
        } else {
            screenHeight = getHeight();
            if (heightMode == EstimateSpec.NOT_EXCEED) {
                screenHeight = Math.min(heightSize, getHeight());
            }
        }
        drawableRadius =  Math.min(this.width - getPaddingLeft() - getPaddingRight(),
                screenHeight - getPaddingTop() - getPaddingBottom()) / 2;
        if (status == STATUS_OPEN) {
            offSet = (float) this.width - drawableRadius * 2 - getPaddingRight() - getPaddingLeft();
        }
        return false;
    }
}
