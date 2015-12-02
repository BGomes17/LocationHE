// Generated code from Butter Knife. Do not modify!
package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class BeaconsScanActivity$$ViewBinder<T extends com.example.beatrizgomes.beaconlocation.ui.activity.BeaconsScanActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492963, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131492963, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131492943, "field 'listBeacons'");
    target.listBeacons = finder.castView(view, 2131492943, "field 'listBeacons'");
  }

  @Override public void unbind(T target) {
    target.toolbar = null;
    target.listBeacons = null;
  }
}
