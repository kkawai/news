package com.initech.news.view;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import org.adw.library.widgets.discreteseekbar.internal.compat.AnimatorCompat;
import org.adw.library.widgets.discreteseekbar.internal.compat.SeekBarCompat;
import org.adw.library.widgets.discreteseekbar.internal.drawable.MarkerDrawable;
import org.adw.library.widgets.discreteseekbar.internal.drawable.ThumbDrawable;
import org.adw.library.widgets.discreteseekbar.internal.drawable.TrackRectDrawable;

import java.util.Formatter;
import java.util.Locale;

import com.initech.news.R;

public class VevoSeekBar extends View {
   private static final boolean isLollipopOrGreater;
   private static final String DEFAULT_FORMATTER = "%d";
   private static final int PRESSED_STATE = 16842919;
   private static final int FOCUSED_STATE = 16842908;
   private static final int PROGRESS_ANIMATION_DURATION = 250;
   private static final int INDICATOR_DELAY_FOR_TAPS = 150;
   private static final int DEFAULT_THUMB_COLOR = -16738680;
   private ThumbDrawable mThumb;
   private TrackRectDrawable mTrack;
   private TrackRectDrawable mScrubber;
   private Drawable mRipple;
   private int mTrackHeight;
   private int mScrubberHeight;
   private int mAddedTouchBounds;
   private int mMax;
   private int mMin;
   private int mValue;
   private int mKeyProgressIncrement;
   private boolean mMirrorForRtl;
   private boolean mAllowTrackClick;
   private boolean mIndicatorPopupEnabled;
   Formatter mFormatter;
   private String mIndicatorFormatter;
   private VevoSeekBar.NumericTransformer mNumericTransformer;
   private StringBuilder mFormatBuilder;
   private VevoSeekBar.OnProgressChangeListener mPublicChangeListener;
   private boolean mIsDragging;
   private int mDragOffset;
   private Rect mInvalidateRect;
   private Rect mTempRect;
   private VevoSeekBarPopupIndicator mIndicator;
   private AnimatorCompat mPositionAnimator;
   private float mAnimationPosition;
   private int mAnimationTarget;
   private float mDownX;
   private float mTouchSlop;
   private Runnable mShowIndicatorRunnable;
   private MarkerDrawable.MarkerAnimationListener mFloaterListener;

   public VevoSeekBar(Context context) {
      this(context, (AttributeSet) null);
   }

   public VevoSeekBar(Context context, AttributeSet attrs) {
      this(context, attrs, R.attr.discreteSeekBarStyle);
   }

   public VevoSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      this.mKeyProgressIncrement = 1;
      this.mMirrorForRtl = false;
      this.mAllowTrackClick = true;
      this.mIndicatorPopupEnabled = true;
      this.mInvalidateRect = new Rect();
      this.mTempRect = new Rect();
      this.mShowIndicatorRunnable = new Runnable() {
         public void run() {
            VevoSeekBar.this.showFloater();
         }
      };
      this.mFloaterListener = new MarkerDrawable.MarkerAnimationListener() {
         public void onClosingComplete() {
            VevoSeekBar.this.mThumb.animateToNormal();
         }

         public void onOpeningComplete() {
         }
      };
      this.setFocusable(true);
      this.setWillNotDraw(false);
      this.mTouchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
      float density = context.getResources().getDisplayMetrics().density;
      this.mTrackHeight = (int) (1.0F * density);
      this.mScrubberHeight = (int) (4.0F * density);
      int thumbSize = (int) (density * 12.0F);
      int touchBounds = (int) (density * 32.0F);
      this.mAddedTouchBounds = (touchBounds - thumbSize) / 2;
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DiscreteSeekBar, defStyleAttr, R.style.Widget_DiscreteSeekBar);
      int max = 100;
      int min = 0;
      int value = 0;
      this.mMirrorForRtl = a.getBoolean(R.styleable.DiscreteSeekBar_dsb_mirrorForRtl, this.mMirrorForRtl);
      this.mAllowTrackClick = a.getBoolean(R.styleable.DiscreteSeekBar_dsb_allowTrackClickToDrag, this.mAllowTrackClick);
      this.mIndicatorPopupEnabled = a.getBoolean(R.styleable.DiscreteSeekBar_dsb_indicatorPopupEnabled, this.mIndicatorPopupEnabled);
      int indexMax = R.styleable.DiscreteSeekBar_dsb_max;
      int indexMin = R.styleable.DiscreteSeekBar_dsb_min;
      int indexValue = R.styleable.DiscreteSeekBar_dsb_value;
      TypedValue out = new TypedValue();
      if (a.getValue(indexMax, out)) {
         if (out.type == 5) {
            max = a.getDimensionPixelSize(indexMax, max);
         } else {
            max = a.getInteger(indexMax, max);
         }
      }

      if (a.getValue(indexMin, out)) {
         if (out.type == 5) {
            min = a.getDimensionPixelSize(indexMin, min);
         } else {
            min = a.getInteger(indexMin, min);
         }
      }

      if (a.getValue(indexValue, out)) {
         if (out.type == 5) {
            value = a.getDimensionPixelSize(indexValue, value);
         } else {
            value = a.getInteger(indexValue, value);
         }
      }

      this.mMin = min;
      this.mMax = Math.max(min + 1, max);
      this.mValue = Math.max(min, Math.min(max, value));
      this.updateKeyboardRange();
      this.mIndicatorFormatter = a.getString(R.styleable.DiscreteSeekBar_dsb_indicatorFormatter);
      ColorStateList trackColor = a.getColorStateList(R.styleable.DiscreteSeekBar_dsb_trackColor);
      ColorStateList progressColor = a.getColorStateList(R.styleable.DiscreteSeekBar_dsb_progressColor);
      ColorStateList rippleColor = a.getColorStateList(R.styleable.DiscreteSeekBar_dsb_rippleColor);
      boolean editMode = this.isInEditMode();
      if (editMode || rippleColor == null) {
         rippleColor = new ColorStateList(new int[][]{new int[0]}, new int[]{-12303292});
      }

      if (editMode || trackColor == null) {
         trackColor = new ColorStateList(new int[][]{new int[0]}, new int[]{-7829368});
      }

      if (editMode || progressColor == null) {
         progressColor = new ColorStateList(new int[][]{new int[0]}, new int[]{DEFAULT_THUMB_COLOR});
      }

      this.mRipple = SeekBarCompat.getRipple(rippleColor);
      if (isLollipopOrGreater) {
         SeekBarCompat.setBackground(this, this.mRipple);
      } else {
         this.mRipple.setCallback(this);
      }

      TrackRectDrawable shapeDrawable = new TrackRectDrawable(trackColor);
      this.mTrack = shapeDrawable;
      this.mTrack.setCallback(this);
      shapeDrawable = new TrackRectDrawable(progressColor);
      this.mScrubber = shapeDrawable;
      this.mScrubber.setCallback(this);
      this.mThumb = new ThumbDrawable(progressColor, thumbSize);
      this.mThumb.setCallback(this);
      this.mThumb.setBounds(0, 0, this.mThumb.getIntrinsicWidth(), this.mThumb.getIntrinsicHeight());
      if (!editMode) {
         this.mIndicator = new VevoSeekBarPopupIndicator(context, attrs, defStyleAttr, this.convertValueToMessage(this.mMax));
         this.mIndicator.setListener(this.mFloaterListener);
      }

      a.recycle();
      this.setNumericTransformer(new VevoSeekBar.DefaultNumericTransformer());
   }

   public void onConfigurationChanged() {
      mIndicator.onConfigurationChanged();
   }

   public void setIndicatorFormatter(@Nullable String formatter) {
      this.mIndicatorFormatter = formatter;
      this.updateProgressMessage(this.mValue);
   }

   public void setNumericTransformer(@Nullable VevoSeekBar.NumericTransformer transformer) {
      this.mNumericTransformer = (VevoSeekBar.NumericTransformer) (transformer != null ? transformer : new VevoSeekBar.DefaultNumericTransformer());
      if (!this.isInEditMode()) {
         if (this.mNumericTransformer.useStringTransform()) {
            this.mIndicator.updateSizes(this.mNumericTransformer.transformToString(this.mMax));
         } else {
            this.mIndicator.updateSizes(this.convertValueToMessage(this.mNumericTransformer.transform(this.mMax)));
         }
      }

      this.updateProgressMessage(this.mValue);
   }

   public VevoSeekBar.NumericTransformer getNumericTransformer() {
      return this.mNumericTransformer;
   }

   public void setMax(int max) {
      this.mMax = max;
      if (this.mMax < this.mMin) {
         this.setMin(this.mMax - 1);
      }

      this.updateKeyboardRange();
      if (this.mValue < this.mMin || this.mValue > this.mMax) {
         this.setProgress(this.mMin);
      }

   }

   public int getMax() {
      return this.mMax;
   }

   public void setMin(int min) {
      this.mMin = min;
      if (this.mMin > this.mMax) {
         this.setMax(this.mMin + 1);
      }

      this.updateKeyboardRange();
      if (this.mValue < this.mMin || this.mValue > this.mMax) {
         this.setProgress(this.mMin);
      }

   }

   public int getMin() {
      return this.mMin;
   }

   public void setProgress(int progress) {
      this.setProgress(progress, false);
   }

   private void setProgress(int value, boolean fromUser) {
      value = Math.max(this.mMin, Math.min(this.mMax, value));
      if (this.isAnimationRunning()) {
         this.mPositionAnimator.cancel();
      }

      if (this.mValue != value) {
         this.mValue = value;
         this.notifyProgress(value, fromUser);
         this.updateProgressMessage(value);
         this.updateThumbPosFromCurrentProgress();
      }

   }

   public int getProgress() {
      return this.mValue;
   }

   public void setOnProgressChangeListener(@Nullable VevoSeekBar.OnProgressChangeListener listener) {
      this.mPublicChangeListener = listener;
   }

   public void setThumbColor(int thumbColor, int indicatorColor) {
      this.mThumb.setColorStateList(ColorStateList.valueOf(thumbColor));
      this.mIndicator.setColors(indicatorColor, thumbColor);
   }

   public void setThumbColor(@NonNull ColorStateList thumbColorStateList, int indicatorColor) {
      this.mThumb.setColorStateList(thumbColorStateList);
      int thumbColor = thumbColorStateList.getColorForState(new int[]{PRESSED_STATE}, thumbColorStateList.getDefaultColor());
      this.mIndicator.setColors(indicatorColor, thumbColor);
   }

   public void setScrubberColor(int color) {
      this.mScrubber.setColorStateList(ColorStateList.valueOf(color));
   }

   public void setScrubberColor(@NonNull ColorStateList colorStateList) {
      this.mScrubber.setColorStateList(colorStateList);
   }

   public void setTrackColor(int color) {
      this.mTrack.setColorStateList(ColorStateList.valueOf(color));
   }

   public void setTrackColor(@NonNull ColorStateList colorStateList) {
      this.mTrack.setColorStateList(colorStateList);
   }

   public void setIndicatorPopupEnabled(boolean enabled) {
      this.mIndicatorPopupEnabled = enabled;
   }

   private void notifyProgress(int value, boolean fromUser) {
      if (this.mPublicChangeListener != null) {
         this.mPublicChangeListener.onProgressChanged(this, value, fromUser);
      }

      this.onValueChanged(value);
   }

   private void notifyBubble(boolean open) {
      if (open) {
         this.onShowBubble();
      } else {
         this.onHideBubble();
      }

   }

   protected void onShowBubble() {
   }

   protected void onHideBubble() {
   }

   protected void onValueChanged(int value) {
   }

   private void updateKeyboardRange() {
      int range = this.mMax - this.mMin;
      if (this.mKeyProgressIncrement == 0 || range / this.mKeyProgressIncrement > 20) {
         this.mKeyProgressIncrement = Math.max(1, Math.round((float) range / 20.0F));
      }

   }

   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
      int height = this.mThumb.getIntrinsicHeight() + this.getPaddingTop() + this.getPaddingBottom();
      height += this.mAddedTouchBounds * 2;
      this.setMeasuredDimension(widthSize, height);
   }

   protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
      super.onLayout(changed, left, top, right, bottom);
      if (changed) {
         this.removeCallbacks(this.mShowIndicatorRunnable);
         if (!this.isInEditMode()) {
            this.mIndicator.dismissComplete();
         }

         this.updateFromDrawableState();
      }
   }

   public void scheduleDrawable(final Drawable who, final Runnable what, final long when) {
      super.scheduleDrawable(who, what, when);
   }

   protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
      super.onSizeChanged(w, h, oldw, oldh);
      int thumbWidth = this.mThumb.getIntrinsicWidth();
      int thumbHeight = this.mThumb.getIntrinsicHeight();
      int addedThumb = this.mAddedTouchBounds;
      int halfThumb = thumbWidth / 2;
      int paddingLeft = this.getPaddingLeft() + addedThumb;
      int paddingRight = this.getPaddingRight();
      int bottom = this.getHeight() - this.getPaddingBottom() - addedThumb;
      this.mThumb.setBounds(paddingLeft, bottom - thumbHeight, paddingLeft + thumbWidth, bottom);
      int trackHeight = Math.max(this.mTrackHeight / 2, 1);
      this.mTrack.setBounds(paddingLeft + halfThumb, bottom - halfThumb - trackHeight, this.getWidth() - halfThumb - paddingRight - addedThumb, bottom - halfThumb + trackHeight);
      int scrubberHeight = Math.max(this.mScrubberHeight / 2, 2);
      this.mScrubber.setBounds(paddingLeft + halfThumb, bottom - halfThumb - scrubberHeight, paddingLeft + halfThumb, bottom - halfThumb + scrubberHeight);
      this.updateThumbPosFromCurrentProgress();
   }

   protected synchronized void onDraw(final Canvas canvas) {
      if (!isLollipopOrGreater) {
         this.mRipple.draw(canvas);
      }

      super.onDraw(canvas);
      this.mTrack.draw(canvas);
      this.mScrubber.draw(canvas);
      this.mThumb.draw(canvas);
   }

   protected void drawableStateChanged() {
      super.drawableStateChanged();
      this.updateFromDrawableState();
   }

   private void updateFromDrawableState() {
      int[] state = this.getDrawableState();
      boolean focused = false;
      boolean pressed = false;
      int[] arr$ = state;
      int len$ = state.length;

      for (int i$ = 0; i$ < len$; ++i$) {
         int i = arr$[i$];
         if (i == FOCUSED_STATE) {
            focused = true;
         } else if (i == PRESSED_STATE) {
            pressed = true;
         }
      }

      if (this.isEnabled() && (focused || pressed) && this.mIndicatorPopupEnabled) {
         this.removeCallbacks(this.mShowIndicatorRunnable);
         this.postDelayed(this.mShowIndicatorRunnable, INDICATOR_DELAY_FOR_TAPS);
      } else {
         this.hideFloater();
      }

      this.mThumb.setState(state);
      this.mTrack.setState(state);
      this.mScrubber.setState(state);
      this.mRipple.setState(state);
   }

   private void updateProgressMessage(final int value) {
      if (!this.isInEditMode()) {
         if (this.mNumericTransformer.useStringTransform()) {
            this.mIndicator.setValue(this.mNumericTransformer.transformToString(value));
         } else {
            this.mIndicator.setValue(this.convertValueToMessage(this.mNumericTransformer.transform(value)));
         }
      }
   }

   private String convertValueToMessage(int value) {
      String format = this.mIndicatorFormatter != null ? this.mIndicatorFormatter : DEFAULT_FORMATTER;
      if (this.mFormatter != null && this.mFormatter.locale().equals(Locale.getDefault())) {
         this.mFormatBuilder.setLength(0);
      } else {
         int bufferSize = format.length() + String.valueOf(this.mMax).length();
         if (this.mFormatBuilder == null) {
            this.mFormatBuilder = new StringBuilder(bufferSize);
         } else {
            this.mFormatBuilder.ensureCapacity(bufferSize);
         }

         this.mFormatter = new Formatter(this.mFormatBuilder, Locale.getDefault());
      }

      return this.mFormatter.format(format, new Object[]{Integer.valueOf(value)}).toString();
   }

   public boolean onTouchEvent(final MotionEvent event) {
      if (!this.isEnabled()) {
         return false;
      } else {

         final int actionMasked = MotionEventCompat.getActionMasked(event);
         final int currentPosition = getCurrentPosition(event);

         switch (actionMasked) {
            case 0:
               this.mDownX = event.getX();
               this.startDragging(event, this.isInScrollingContainer());
               break;
            case 1:
            case 3:
               if (currentPosition != VevoSeekBar.this.getProgress()) {
                  VevoSeekBar.this.setProgress(currentPosition,true);
               }
               this.stopDragging();
               break;
            case 2:
               if (this.isDragging()) {
                  this.updateDragging(event);
               } else {
                  float x = event.getX();
                  if (Math.abs(x - this.mDownX) > this.mTouchSlop) {
                     this.startDragging(event, false);
                  }
               }
         }

         return true;
      }
   }

   private boolean isInScrollingContainer() {
      return SeekBarCompat.isInScrollingContainer(this.getParent());
   }

   private boolean startDragging(MotionEvent ev, boolean ignoreTrackIfInScrollContainer) {
      Rect bounds = this.mTempRect;
      this.mThumb.copyBounds(bounds);
      bounds.inset(-this.mAddedTouchBounds, -this.mAddedTouchBounds);
      this.mIsDragging = bounds.contains((int) ev.getX(), (int) ev.getY());
      if (!this.mIsDragging && this.mAllowTrackClick && !ignoreTrackIfInScrollContainer) {
         this.mIsDragging = true;
         this.mDragOffset = bounds.width() / 2 - this.mAddedTouchBounds;
         this.updateDragging(ev);
         this.mThumb.copyBounds(bounds);
         bounds.inset(-this.mAddedTouchBounds, -this.mAddedTouchBounds);
      }

      if (this.mIsDragging) {
         this.setPressed(true);
         this.attemptClaimDrag();
         this.setHotspot(ev.getX(), ev.getY());
         this.mDragOffset = (int) (ev.getX() - (float) bounds.left - (float) this.mAddedTouchBounds);
         if (this.mPublicChangeListener != null) {
            this.mPublicChangeListener.onStartTrackingTouch(this);
         }
      }

      return this.mIsDragging;
   }

   private boolean isDragging() {
      return this.mIsDragging;
   }

   private void stopDragging() {
      if (this.mPublicChangeListener != null) {
         this.mPublicChangeListener.onStopTrackingTouch(this);
      }

      this.mIsDragging = false;
      this.setPressed(false);
   }

   public boolean onKeyDown(int keyCode, KeyEvent event) {
      boolean handled = false;
      if (this.isEnabled()) {
         int progress = this.getAnimatedProgress();
         switch (keyCode) {
            case 21:
               handled = true;
               if (progress > this.mMin) {
                  this.animateSetProgress(progress - this.mKeyProgressIncrement);
               }
               break;
            case 22:
               handled = true;
               if (progress < this.mMax) {
                  this.animateSetProgress(progress + this.mKeyProgressIncrement);
               }
         }
      }

      return handled || super.onKeyDown(keyCode, event);
   }

   private int getAnimatedProgress() {
      return this.isAnimationRunning() ? this.getAnimationTarget() : this.mValue;
   }

   boolean isAnimationRunning() {
      return this.mPositionAnimator != null && this.mPositionAnimator.isRunning();
   }

   void animateSetProgress(int progress) {
      float curProgress = this.isAnimationRunning() ? this.getAnimationPosition() : (float) this.getProgress();
      if (progress < this.mMin) {
         progress = this.mMin;
      } else if (progress > this.mMax) {
         progress = this.mMax;
      }

      if (this.mPositionAnimator != null) {
         this.mPositionAnimator.cancel();
      }

      this.mAnimationTarget = progress;
      this.mPositionAnimator = AnimatorCompat.create(curProgress, (float) progress, new AnimatorCompat.AnimationFrameUpdateListener() {
         public void onAnimationFrame(float currentValue) {
            VevoSeekBar.this.setAnimationPosition(currentValue);
         }
      });
      this.mPositionAnimator.setDuration(PROGRESS_ANIMATION_DURATION);
      this.mPositionAnimator.start();
   }

   private int getAnimationTarget() {
      return this.mAnimationTarget;
   }

   void setAnimationPosition(float position) {
      this.mAnimationPosition = position;
      float currentScale = (position - (float) this.mMin) / (float) (this.mMax - this.mMin);
      this.updateProgressFromAnimation(currentScale);
   }

   float getAnimationPosition() {
      return this.mAnimationPosition;
   }

   private void updateDragging(final MotionEvent ev) {
      final int progress = getCurrentPosition(ev);
      this.setProgress(progress, true);
   }

   private int getCurrentPosition(final MotionEvent ev) {
      this.setHotspot(ev.getX(), ev.getY());
      int x = (int) ev.getX();
      Rect oldBounds = this.mThumb.getBounds();
      int halfThumb = oldBounds.width() / 2;
      int addedThumb = this.mAddedTouchBounds;
      int newX = x - this.mDragOffset + halfThumb;
      int left = this.getPaddingLeft() + halfThumb + addedThumb;
      int right = this.getWidth() - (this.getPaddingRight() + halfThumb + addedThumb);
      if (newX < left) {
         newX = left;
      } else if (newX > right) {
         newX = right;
      }

      int available = right - left;
      float scale = (float) (newX - left) / (float) available;
      if (this.isRtl()) {
         scale = 1.0F - scale;
      }

      return Math.round(scale * (float) (this.mMax - this.mMin) + (float) this.mMin);
   }

   private void updateProgressFromAnimation(float scale) {
      Rect bounds = this.mThumb.getBounds();
      int halfThumb = bounds.width() / 2;
      int addedThumb = this.mAddedTouchBounds;
      int left = this.getPaddingLeft() + halfThumb + addedThumb;
      int right = this.getWidth() - (this.getPaddingRight() + halfThumb + addedThumb);
      int available = right - left;
      int progress = Math.round(scale * (float) (this.mMax - this.mMin) + (float) this.mMin);
      if (progress != this.getProgress()) {
         this.mValue = progress;
         this.notifyProgress(this.mValue, true);
         this.updateProgressMessage(progress);
      }

      int thumbPos = (int) (scale * (float) available + 0.5F);
      this.updateThumbPos(thumbPos);
   }

   private void updateThumbPosFromCurrentProgress() {
      final int thumbWidth = this.mThumb.getIntrinsicWidth();
      final int addedThumb = this.mAddedTouchBounds;
      final int halfThumb = thumbWidth / 2;
      final float scaleDraw = (float) (this.mValue - this.mMin) / (float) (this.mMax - this.mMin);
      final int left = this.getPaddingLeft() + halfThumb + addedThumb;
      final int right = this.getWidth() - (this.getPaddingRight() + halfThumb + addedThumb);
      final int available = right - left;
      final int thumbPos = (int) (scaleDraw * (float) available + 0.5F);
      this.updateThumbPos(thumbPos);
   }

   private void updateThumbPos(int posX) {
      final int thumbWidth = this.mThumb.getIntrinsicWidth();
      final int halfThumb = thumbWidth / 2;
      int start;
      if (this.isRtl()) {
         start = this.getWidth() - this.getPaddingRight() - this.mAddedTouchBounds;
         posX = start - posX - thumbWidth;
      } else {
         start = this.getPaddingLeft() + this.mAddedTouchBounds;
         posX += start;
      }

      this.mThumb.copyBounds(this.mInvalidateRect);
      this.mThumb.setBounds(posX, this.mInvalidateRect.top, posX + thumbWidth, this.mInvalidateRect.bottom);
      if (this.isRtl()) {
         this.mScrubber.getBounds().right = start - halfThumb;
         this.mScrubber.getBounds().left = posX + halfThumb;
      } else {
         this.mScrubber.getBounds().left = start + halfThumb;
         this.mScrubber.getBounds().right = posX + halfThumb;
      }

      final Rect finalBounds = this.mTempRect;
      this.mThumb.copyBounds(finalBounds);
      if (!this.isInEditMode()) {
         this.mIndicator.move(finalBounds.centerX());
      }

      this.mInvalidateRect.inset(-this.mAddedTouchBounds, -this.mAddedTouchBounds);
      finalBounds.inset(-this.mAddedTouchBounds, -this.mAddedTouchBounds);
      this.mInvalidateRect.union(finalBounds);
      SeekBarCompat.setHotspotBounds(this.mRipple, finalBounds.left, finalBounds.top, finalBounds.right, finalBounds.bottom);
      this.invalidate(this.mInvalidateRect);
   }

   private void setHotspot(final float x, final float y) {
      DrawableCompat.setHotspot(this.mRipple, x, y);
   }

   protected boolean verifyDrawable(final Drawable who) {
      return who == this.mThumb || who == this.mTrack || who == this.mScrubber || who == this.mRipple || super.verifyDrawable(who);
   }

   private void attemptClaimDrag() {
      ViewParent parent = this.getParent();
      if (parent != null) {
         parent.requestDisallowInterceptTouchEvent(true);
      }

   }

   private void showFloater() {
      if (!this.isInEditMode()) {
         this.mThumb.animateToPressed();
         this.mIndicator.showIndicator(this, this.mThumb.getBounds());
         this.notifyBubble(true);
      }

   }

   private void hideFloater() {
      this.removeCallbacks(this.mShowIndicatorRunnable);
      if (!this.isInEditMode()) {
         this.mIndicator.dismiss();
         this.notifyBubble(false);
      }

   }

   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      this.removeCallbacks(this.mShowIndicatorRunnable);
      if (!this.isInEditMode()) {
         this.mIndicator.dismissComplete();
      }

   }

   public boolean isRtl() {
      return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL && this.mMirrorForRtl;
   }

   protected Parcelable onSaveInstanceState() {
      Parcelable superState = super.onSaveInstanceState();
      VevoSeekBar.CustomState state = new VevoSeekBar.CustomState(superState);
      state.progress = this.getProgress();
      state.max = this.mMax;
      state.min = this.mMin;
      return state;
   }

   protected void onRestoreInstanceState(Parcelable state) {
      if (state != null && state.getClass().equals(VevoSeekBar.CustomState.class)) {
         VevoSeekBar.CustomState customState = (VevoSeekBar.CustomState) state;
         this.setMin(customState.min);
         this.setMax(customState.max);
         this.setProgress(customState.progress, false);
         super.onRestoreInstanceState(customState.getSuperState());
      } else {
         super.onRestoreInstanceState(state);
      }
   }

   static {
      isLollipopOrGreater = Build.VERSION.SDK_INT >= 21;
   }

   static class CustomState extends BaseSavedState {
      private int progress;
      private int max;
      private int min;
      public static final Creator<CustomState> CREATOR = new Creator() {
         public VevoSeekBar.CustomState[] newArray(int size) {
            return new VevoSeekBar.CustomState[size];
         }

         public VevoSeekBar.CustomState createFromParcel(Parcel incoming) {
            return new VevoSeekBar.CustomState(incoming);
         }
      };

      public CustomState(Parcel source) {
         super(source);
         this.progress = source.readInt();
         this.max = source.readInt();
         this.min = source.readInt();
      }

      public CustomState(Parcelable superState) {
         super(superState);
      }

      public void writeToParcel(Parcel outcoming, int flags) {
         super.writeToParcel(outcoming, flags);
         outcoming.writeInt(this.progress);
         outcoming.writeInt(this.max);
         outcoming.writeInt(this.min);
      }
   }

   private static class DefaultNumericTransformer extends VevoSeekBar.NumericTransformer {
      private DefaultNumericTransformer() {
      }

      public int transform(int value) {
         return value;
      }
   }

   public abstract static class NumericTransformer {
      public NumericTransformer() {
      }

      public abstract int transform(int value);

      public String transformToString(int value) {
         return String.valueOf(value);
      }

      public boolean useStringTransform() {
         return false;
      }
   }

   public interface OnProgressChangeListener {
      void onProgressChanged(VevoSeekBar seekBar, int value, boolean isFromUser);

      void onStartTrackingTouch(VevoSeekBar seekBar);

      void onStopTrackingTouch(VevoSeekBar seekBar);

   }
}
