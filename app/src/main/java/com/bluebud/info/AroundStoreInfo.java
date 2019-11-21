package com.bluebud.info;

/**
 * Created by user on 2017/12/26.
 */

public class AroundStoreInfo {

//    private List<StoreMapBean> storeMap;
//
//    public List<StoreMapBean> getStoreMap() {
//        return storeMap;
//    }
//
//    public void setStoreMap(List<StoreMapBean> storeMap) {
//        this.storeMap = storeMap;
//    }

//    public static class StoreMapBean {
        public String name;
        public String address;
        public Double lat;
        public Double lng;
        public String pic;
        public String phone;
        public String start_time;
        public String end_time;
        public String profile;

//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getAddress() {
//            return address;
//        }
//
//        public void setAddress(String address) {
//            this.address = address;
//        }
//
//        public double getLat() {
//            return lat;
//        }
//
//        public void setLat(double lat) {
//            this.lat = lat;
//        }
//
//        public double getLng() {
//            return lng;
//        }
//
//        public void setLng(double lng) {
//            this.lng = lng;
//        }
//
//        public String getPic() {
//            return pic;
//        }
//
//        public void setPic(String pic) {
//            this.pic = pic;
//        }
//
//        public String getPhone() {
//            return phone;
//        }
//
//        public void setPhone(String phone) {
//            this.phone = phone;
//        }
//
//        public String getStart_time() {
//            return start_time;
//        }
//
//        public void setStart_time(String start_time) {
//            this.start_time = start_time;
//        }
//
//        public String getEnd_time() {
//            return end_time;
//        }
//
//        public void setEnd_time(String end_time) {
//            this.end_time = end_time;
//        }
//
//        public String getProfile() {
//            return profile;
//        }
//
//        public void setProfile(String profile) {
//            this.profile = profile;
//        }

        @Override
        public String toString() {
            return "StoreMapBean{" +
                    "name='" + name + '\'' +
                    ", address='" + address + '\'' +
                    ", lat='" + lat + '\'' +
                    ", lng='" + lng + '\'' +
                    ", pic='" + pic + '\'' +
                    ", phone='" + phone + '\'' +
                    ", start_time='" + start_time + '\'' +
                    ", end_time='" + end_time + '\'' +
                    ", profile='" + profile + '\'' +
                    '}';
        }
//    }

//    @Override
//    public String toString() {
//        return "AroundStoreInfo{" +
//                "storeMap=" + storeMap +
//                '}';
//    }
}
