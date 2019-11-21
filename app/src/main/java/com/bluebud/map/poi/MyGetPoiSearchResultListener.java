package com.bluebud.map.poi;

import com.bluebud.info.PeripherDetail;

import java.util.List;

public interface MyGetPoiSearchResultListener {

    void onGetPoiSucceed(boolean hasResult, List<PeripherDetail> detailList);

//    void onGetPoiResult(PoiResult poiResult);
//
//    void onGetPoiDetailResult(PoiDetailResult poiDetailResult);
//
//    void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult);
}
