// Generated code from Butter Knife. Do not modify!
package com.example.beatrizgomes.beaconlocation.adapter.viewholder;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class EddystoneItemViewHolder$$ViewBinder<T extends com.example.beatrizgomes.beaconlocation.adapter.viewholder.EddystoneItemViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492956, "field 'instance'");
    target.instance = finder.castView(view, 2131492956, "field 'instance'");
    view = finder.findRequiredView(source, 2131492950, "field 'rssi'");
    target.rssi = finder.castView(view, 2131492950, "field 'rssi'");
    view = finder.findRequiredView(source, 2131492952, "field 'proximity'");
    target.proximity = finder.castView(view, 2131492952, "field 'proximity'");
  }

  @Override public void unbind(T target) {
    target.instance = null;
    target.rssi = null;
    target.proximity = null;
  }
}
