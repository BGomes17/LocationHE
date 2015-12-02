package com.example.beatrizgomes.beaconlocation.model;

import com.kontakt.sdk.android.ble.device.DeviceProfile;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

/**
 * Created by beatrizgomes on 02/12/15.
 */
public class BeaconWrapper {

    private IEddystoneDevice eddystoneDevice;
    private IBeaconDevice beaconDevice;
    private DeviceProfile deviceProfile;

    public BeaconWrapper(IEddystoneDevice eddystoneDevice, IBeaconDevice beaconDevice, DeviceProfile deviceProfile){

        this.eddystoneDevice = eddystoneDevice;
        this.beaconDevice = beaconDevice;
        this.deviceProfile = deviceProfile;

    }

    public DeviceProfile getDeviceProfile() {
        return deviceProfile;
    }

    public IBeaconDevice getBeaconDevice() {
        return beaconDevice;
    }

    public IEddystoneDevice getEddystoneDevice() {
        return eddystoneDevice;
    }
}
