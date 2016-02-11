// Generated code from Butter Knife. Do not modify!
package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.example.beatrizgomes.beaconlocation.ui.activity.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492992, "field 'btn_scan' and method 'startScan'");
    target.btn_scan = finder.castView(view, 2131492992, "field 'btn_scan'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.startScan();
        }
      });
    view = finder.findRequiredView(source, 2131492993, "field 'btn_test' and method 'teste'");
    target.btn_test = finder.castView(view, 2131492993, "field 'btn_test'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.teste();
        }
      });
    view = finder.findRequiredView(source, 2131493015, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131493015, "field 'toolbar'");
  }

  @Override public void unbind(T target) {
    target.btn_scan = null;
    target.btn_test = null;
    target.toolbar = null;
  }
}
