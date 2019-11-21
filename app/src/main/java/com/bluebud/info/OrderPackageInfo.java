package com.bluebud.info;

import java.io.Serializable;
import java.util.List;

public class OrderPackageInfo implements Serializable {

        private DeviceBean device;
        private List<PackageListBean> packageList;

        public DeviceBean getDevice() {
            return device;
        }

        public void setDevice(DeviceBean device) {
            this.device = device;
        }

        public List<PackageListBean> getPackageList() {
            return packageList;
        }

        public void setPackageList(List<PackageListBean> packageList) {
            this.packageList = packageList;
        }

        public static class DeviceBean {


            private String device_sn;
            private String org_id;
            private String expired_time_de;
            private String clientId;
            private String secret;

            public String getDevice_sn() {
                return device_sn;
            }

            public void setDevice_sn(String device_sn) {
                this.device_sn = device_sn;
            }

            public String getOrg_id() {
                return org_id;
            }

            public void setOrg_id(String org_id) {
                this.org_id = org_id;
            }

            public String getExpired_time_de() {
                return expired_time_de;
            }

            public void setExpired_time_de(String expired_time_de) {
                this.expired_time_de = expired_time_de;
            }

            public String getClientId() {
                return clientId;
            }

            public void setClientId(String clientId) {
                this.clientId = clientId;
            }

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }
        }

        public static class PackageListBean {

            private String orderPackageId;
            private String name;
            private String month;
            private String serve_fee;
            private String content;
            private String currency_unit;
            private String expired_time_de;

            public String getOrderPackageId() {
                return orderPackageId;
            }

            public void setOrderPackageId(String orderPackageId) {
                this.orderPackageId = orderPackageId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMonth() {
                return month;
            }

            public void setMonth(String month) {
                this.month = month;
            }

            public String getServe_fee() {
                return serve_fee;
            }

            public void setServe_fee(String serve_fee) {
                this.serve_fee = serve_fee;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getCurrency_unit() {
                return currency_unit;
            }

            public void setCurrency_unit(String currency_unit) {
                this.currency_unit = currency_unit;
            }

            public String getExpired_time_de() {
                return expired_time_de;
            }

            public void setExpired_time_de(String expired_time_de) {
                this.expired_time_de = expired_time_de;
            }
        }
}
