package com.bluebud.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import com.bluebud.app.App;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.model.MyMapPresenter;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.ToastUtil;

public class GmapTabLocationMapFragment extends AbstractTabLocationMapFragment {
    @Override
    protected MyMapPresenter onCreateMapPresenter(Context context) {
        return new MyMapPresenter(context, MyMapPresenter.MAP_TYPE_GOOGLE);
    }

    @Override
    protected void resetMarkerView() {

    }

    @Override
    protected MyLatLng getCorrectCarLatLng(double lat, double lng) {
        return MyLatLng.from(lat, lng);
    }

    private boolean isGoogleMapsInstalled() {
        try {
            App.getContext().getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void toNavigator() {
        if (isGoogleMapsInstalled()) {
            if (null != curPointLocation) {
                String str1 = "google.navigation:q=" + curPointLocation.toGLatLng().latitude + "," + curPointLocation.toGLatLng().longitude + "&mode=a";
                Uri gmmIntentUri = Uri.parse(str1);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } else {
                ToastUtil.show(getActivity(), R.string.no_location_point);
            }
        } else {
            DialogUtil.show(getActivity(), R.string.prompt,
                    R.string.install_google_map, R.string.confirm,
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            DialogUtil.dismiss();
                            Intent intent = new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=com.google.android.apps.maps"));
                            startActivity(intent);
                        }
                    }, R.string.cancel, new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            DialogUtil.dismiss();
                        }
                    });
        }
    }
}
