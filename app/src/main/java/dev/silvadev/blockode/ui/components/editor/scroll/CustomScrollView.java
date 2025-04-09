package dev.silvadev.blockode.ui.components.editor.scroll;

/**
 * Originally Decompiled from Sketchware 1.1.13
 * Refactored by Aquiles Trindade (trindadedev)
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {
  private boolean enabled = true;

  public CustomScrollView(Context context) {
    super(context);
  }

  public CustomScrollView(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
  }

  public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
    return this.enabled ? super.onInterceptTouchEvent(motionEvent) : false;
  }

  public boolean onTouchEvent(MotionEvent motionEvent) {
    return this.enabled ? super.onTouchEvent(motionEvent) : false;
  }

  public void setScrollDisabled() {
    this.enabled = false;
  }

  public void setScrollEnabled() {
    this.enabled = true;
  }
}
