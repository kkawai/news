package com.initech.news.view.drag.transformer;

import android.view.View;

/**
 * Factory created to provide Transformer implementations like ResizeTransformer o
 * ScaleTransformer.
 *
 */
public class TransformerFactory {

  public Transformer getTransformer(final boolean resize, final View view, final View parent) {
    Transformer transformer = null;
    if (resize) {
      transformer = new ResizeTransformer(view, parent);
    } else {
      transformer = new ScaleTransformer(view, parent);
    }
    return transformer;
  }
}
