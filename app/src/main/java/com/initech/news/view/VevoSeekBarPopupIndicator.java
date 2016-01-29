package com.initech.news.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

import org.adw.library.widgets.discreteseekbar.internal.Marker;
import org.adw.library.widgets.discreteseekbar.internal.compat.SeekBarCompat;
import org.adw.library.widgets.discreteseekbar.internal.drawable.MarkerDrawable.MarkerAnimationListener;

public class VevoSeekBarPopupIndicator {
   private final WindowManager mWindowManager;
   private boolean mShowing;
   private VevoSeekBarPopupIndicator.Floater mPopupView;
   private MarkerAnimationListener mListener;
   private int[] mDrawingLocation = new int[2];
   Point screenSize = new Point();

   public VevoSeekBarPopupIndicator(Context context, AttributeSet attrs, int defStyleAttr, String maxValue) {
      this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
      this.mPopupView = new VevoSeekBarPopupIndicator.Floater(context, attrs, defStyleAttr, maxValue);
      DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
      this.screenSize.set(displayMetrics.widthPixels, displayMetrics.heightPixels);
   }

   public void onConfigurationChanged() {
      DisplayMetrics displayMetrics = mPopupView.getResources().getDisplayMetrics();
      this.screenSize.set(displayMetrics.widthPixels, displayMetrics.heightPixels);
   }

   public void updateSizes(String maxValue) {
      this.dismissComplete();
      if (this.mPopupView != null) {
         this.mPopupView.mMarker.resetSizes(maxValue);
      }

   }

   public void setListener(MarkerAnimationListener listener) {
      this.mListener = listener;
   }

   private void measureFloater() {
      int specWidth = MeasureSpec.makeMeasureSpec(this.screenSize.x, 1073741824);
      int specHeight = MeasureSpec.makeMeasureSpec(this.screenSize.y, -2147483648);
      this.mPopupView.measure(specWidth, specHeight);
   }

   public void setValue(CharSequence value) {
      this.mPopupView.mMarker.setValue(value);
   }

   public boolean isShowing() {
      return this.mShowing;
   }

   public void showIndicator(View parent, Rect touchBounds) {
      if (this.isShowing()) {
         this.mPopupView.mMarker.animateOpen();
      } else {
         IBinder windowToken = parent.getWindowToken();
         if (windowToken != null) {
            LayoutParams p = this.createPopupLayout(windowToken);
            p.gravity = 8388659;
            this.updateLayoutParamsForPosiion(parent, p, touchBounds.bottom);
            this.mShowing = true;
            this.translateViewIntoPosition(touchBounds.centerX());
            this.invokePopup(p);
         }

      }
   }

   public void move(int x) {
      if (this.isShowing()) {
         this.translateViewIntoPosition(x);
      }
   }

   public void setColors(int startColor, int endColor) {
      this.mPopupView.setColors(startColor, endColor);
   }

   public void dismiss() {
      this.mPopupView.mMarker.animateClose();
   }

   public void dismissComplete() {
      if (this.isShowing()) {
         this.mShowing = false;

         try {
            this.mWindowManager.removeViewImmediate(this.mPopupView);
         } finally {
            ;
         }
      }

   }

   private void updateLayoutParamsForPosiion(View anchor, LayoutParams p, int yOffset) {
      this.measureFloater();
      int measuredHeight = this.mPopupView.getMeasuredHeight();
      int paddingBottom = this.mPopupView.mMarker.getPaddingBottom();
      anchor.getLocationInWindow(this.mDrawingLocation);
      p.x = 0;
      p.y = this.mDrawingLocation[1] - measuredHeight + yOffset + paddingBottom;
      p.width = this.screenSize.x;
      p.height = measuredHeight;
   }

   private void translateViewIntoPosition(int x) {
      this.mPopupView.setFloatOffset(x + this.mDrawingLocation[0]);
   }

   private void invokePopup(LayoutParams p) {
      this.mWindowManager.addView(this.mPopupView, p);
      this.mPopupView.mMarker.animateOpen();
   }

   private LayoutParams createPopupLayout(IBinder token) {
      LayoutParams p = new LayoutParams();
      p.gravity = 8388659;
      p.width = -1;
      p.height = -1;
      p.format = -3;
      p.flags = this.computeFlags(p.flags);
      p.type = 1000;
      p.token = token;
      p.softInputMode = 3;
      p.setTitle("DiscreteSeekBar Indicator:" + Integer.toHexString(this.hashCode()));
      return p;
   }

   private int computeFlags(int curFlags) {
      curFlags &= -426521;
      curFlags |= '耀';
      curFlags |= 8;
      curFlags |= 16;
      curFlags |= 512;
      return curFlags;
   }

   private class Floater extends FrameLayout implements MarkerAnimationListener {
      private Marker mMarker;
      private int mOffset;

      public Floater(Context context, AttributeSet attrs, int defStyleAttr, String maxValue) {
         super(context);
         this.mMarker = new Marker(context, attrs, defStyleAttr, maxValue);
         this.addView(this.mMarker, new LayoutParams(-2, -2, 51));
      }

      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
         this.measureChildren(widthMeasureSpec, heightMeasureSpec);
         int widthSize = MeasureSpec.getSize(widthMeasureSpec);
         int heightSie = this.mMarker.getMeasuredHeight();
         this.setMeasuredDimension(widthSize, heightSie);
      }

      protected void onLayout(boolean changed, int l, int t, int r, int b) {
         int centerDiffX = this.mMarker.getMeasuredWidth() / 2;
         int offset = this.mOffset - centerDiffX;
         this.mMarker.layout(offset, 0, offset + this.mMarker.getMeasuredWidth(), this.mMarker.getMeasuredHeight());
      }

      public void setFloatOffset(int x) {
         this.mOffset = x;
         int centerDiffX = this.mMarker.getMeasuredWidth() / 2;
         int offset = x - centerDiffX;
         this.mMarker.offsetLeftAndRight(offset - this.mMarker.getLeft());
         if (!SeekBarCompat.isHardwareAccelerated(this)) {
            this.invalidate();
         }

      }

      public void onClosingComplete() {
         if (VevoSeekBarPopupIndicator.this.mListener != null) {
            VevoSeekBarPopupIndicator.this.mListener.onClosingComplete();
         }

         VevoSeekBarPopupIndicator.this.dismissComplete();
      }

      public void onOpeningComplete() {
         if (VevoSeekBarPopupIndicator.this.mListener != null) {
            VevoSeekBarPopupIndicator.this.mListener.onOpeningComplete();
         }

      }

      public void setColors(int startColor, int endColor) {
         this.mMarker.setColors(startColor, endColor);
      }
   }
}