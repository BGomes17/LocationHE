package com.example.beatrizgomes.beaconlocation.adapter.monitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.example.beatrizgomes.beaconlocation.R;
import com.example.beatrizgomes.beaconlocation.adapter.viewholder.EddystoneItemViewHolder;
import com.example.beatrizgomes.beaconlocation.adapter.viewholder.GroupViewHolder;
import com.example.beatrizgomes.beaconlocation.adapter.viewholder.IBeaconListViewHolder;
import com.example.beatrizgomes.beaconlocation.model.BeaconWrapper;
import com.kontakt.sdk.android.ble.device.DeviceProfile;
import com.kontakt.sdk.android.ble.device.EddystoneDevice;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by beatrizgomes on 02/12/15.
 */
public class BeaconsScanMonitorAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<DeviceProfile> groupList;
    private Map<DeviceProfile, List<BeaconWrapper>> childMap;


    public BeaconsScanMonitorAdapter(Context context) {

        this.context = context;
        groupList = new ArrayList<>();
        childMap = new HashMap<>();
        createGroups();

    }

    private void createGroups() {
        //Log.i("BeaconAdapter", "createGroups()");
        groupList.add(DeviceProfile.IBEACON);
        groupList.add(DeviceProfile.EDDYSTONE);
        childMap.put(DeviceProfile.IBEACON, new ArrayList<BeaconWrapper>());
        childMap.put(DeviceProfile.EDDYSTONE, new ArrayList<BeaconWrapper>());
        
    }

    public void replaceIBeacons(List<IBeaconDevice> iBeacons) {

        List<BeaconWrapper> beaconWrappers = childMap.get(DeviceProfile.IBEACON);
        //Log.i("BeaconAdapter", "replaceIbeacons(), list size: " + beaconWrappers.size());
        beaconWrappers.clear();
        for (IBeaconDevice iBeacon : iBeacons) {
            beaconWrappers.add(new BeaconWrapper(null, iBeacon, DeviceProfile.IBEACON));
        }
        notifyDataSetChanged();
        
    }

    public void replaceEddystoneBeacons(List<IEddystoneDevice> eddystoneDevices) {
        List<BeaconWrapper> eddystoneWrappers = childMap.get(DeviceProfile.EDDYSTONE);
        //Log.i("BeaconAdapter", "replaceEddystones(), list size: " + eddystoneWrappers.size());
        eddystoneWrappers.clear();
        for (IEddystoneDevice eddystoneDevice : eddystoneDevices) {
            eddystoneWrappers.add(new BeaconWrapper(eddystoneDevice, null, DeviceProfile.EDDYSTONE));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childMap.get(getGroup(groupPosition)).size();
    }
    
    @Override
    public DeviceProfile getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public BeaconWrapper getChild(int groupPosition, int childPosition) {

        return childMap.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        
        DeviceProfile group = getGroup(groupPosition);
        
        if(convertView == null) {
            convertView = createHeader(group);
        }
        
        setHeaderTitle(group.name(), convertView, groupPosition);
        return convertView;
    }

    protected View createHeader(DeviceProfile group) {

        View convertView = createView(R.layout.monitor_section_list_header);

        GroupViewHolder groupViewHolder = new GroupViewHolder(convertView);

        /*switch (group) {

            case IBEACON: imageBeacon.setImageResource(R.drawable.ibeacon_logo);
                break;
            case EDDYSTONE: imageBeacon.setImageResource(R.drawable.eddystone_logo);
                break;

        }*/

        convertView.setTag(groupViewHolder);

        return convertView;
    }
    void setHeaderTitle(String title, View convertView, int groupPosition) {
        final GroupViewHolder groupViewHolder = (GroupViewHolder) convertView.getTag();
        groupViewHolder.header.setText(title + "( " + getChildrenCount(groupPosition) + " )");
    }

    /*   */
    View createView(final int viewId) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(viewId, null);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        BeaconWrapper child = getChild(groupPosition, childPosition);

        if (DeviceProfile.IBEACON == child.getDeviceProfile()){


            if (convertView == null || (!(convertView.getTag() instanceof IBeaconListViewHolder))) {
                convertView = createView(R.layout.beacon_list_row);
                final IBeaconListViewHolder childViewHolder = new IBeaconListViewHolder(convertView);
                convertView.setTag(childViewHolder);
            }

            IBeaconDevice device = child.getBeaconDevice();
            final IBeaconListViewHolder childViewHolder = (IBeaconListViewHolder) convertView.getTag();

            childViewHolder.nameTextView.setText(String.format("Device Name: %s", device.getUniqueId()));
            childViewHolder.proximityTextView.setText(String.format("Proximidade: %s", device.getProximity()));

        } else if (DeviceProfile.EDDYSTONE == child.getDeviceProfile()) {
            if (convertView == null || (!(convertView.getTag() instanceof EddystoneItemViewHolder))) {
                convertView = createView(R.layout.eddystone_list_row);
                EddystoneItemViewHolder childViewHolder = new EddystoneItemViewHolder(convertView);
                convertView.setTag(childViewHolder);
            }
            IEddystoneDevice eddystoneDevice = child.getEddystoneDevice();
            EddystoneDevice.EddystoneCharacteristics eddystoneCharacteristics;
            EddystoneItemViewHolder viewHolder = (EddystoneItemViewHolder) convertView.getTag();

            Context context = convertView.getContext();
            //viewHolder.namespace.setText(context.getString(R.string.namespace, eddystoneDevice.getNamespaceId()));
            viewHolder.instance.setText(context.getString(R.string.instance, eddystoneDevice.getInstanceId()));
            //viewHolder.url.setText(context.getString(R.string.url, eddystoneDevice.getUrl()));
            //viewHolder.txPowerTextView.setText(context.getString(R.string.tx_power_level, eddystoneDevice.getTxPower()));
            //viewHolder.temperature.setText(context.getString(R.string.temperature, eddystoneDevice.getTemperature()));
            //viewHolder.batteryVoltage.setText(context.getString(R.string.battery_voltage, eddystoneDevice.getBatteryVoltage()));
            //viewHolder.pduCount.setText(context.getString(R.string.pdu_count, eddystoneDevice.getPduCount()));
            //viewHolder.timeSincePowerUp.setText(context.getString(R.string.time_since_power_up, eddystoneDevice.getTimeSincePowerUp()));
            //viewHolder.telemetryVersion.setText(context.getString(R.string.telemetry_version, eddystoneDevice.getTelemetryVersion()));
            viewHolder.rssi.setText(String.format("Rssi: %.2f", eddystoneDevice.getRssi()));
            viewHolder.proximity.setText(String.format("Proximity: %s", eddystoneDevice.getProximity()));

        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}