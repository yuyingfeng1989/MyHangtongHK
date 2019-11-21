package com.bluebud.info;

import java.util.List;

public class IMMessage {

    /**
     * code : 1
     * im : [{"message":"我儿童歌问","user_type":"2","from_user_id":"4362","to_user_id":"61","dialog_id":"349","name":"User-4362","time":"2019-08-21 11:25:33","message_type":"come_wait"},{"message":"额大体过问题","user_type":"2","from_user_id":"4362","to_user_id":"61","dialog_id":"349","name":"User-4362","time":"2019-08-21 11:25:31","message_type":"come_wait"}]
     */

    public String code;
    public List<ImBean> im;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ImBean> getIm() {
        return im;
    }

    public void setIm(List<ImBean> im) {
        this.im = im;
    }

    public static class ImBean {
        /**
         * message : 我儿童歌问
         * user_type : 2
         * from_user_id : 4362
         * to_user_id : 61
         * dialog_id : 349
         * name : User-4362
         * time : 2019-08-21 11:25:33
         * message_type : come_wait
         */

        public String message;
        public String user_type;
        public String from_user_id;
        public String to_user_id;
        public String name;
        public String message_type;
        public String store_id;
        public String goods_id;

        public String getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(String goods_id) {
            this.goods_id = goods_id;
        }

        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUser_type() {
            return user_type;
        }

        public void setUser_type(String user_type) {
            this.user_type = user_type;
        }

        public String getFrom_user_id() {
            return from_user_id;
        }

        public void setFrom_user_id(String from_user_id) {
            this.from_user_id = from_user_id;
        }

        public String getTo_user_id() {
            return to_user_id;
        }

        public void setTo_user_id(String to_user_id) {
            this.to_user_id = to_user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage_type() {
            return message_type;
        }

        public void setMessage_type(String message_type) {
            this.message_type = message_type;
        }

        @Override
        public String toString() {
            return "ImBean{" +
                    "message='" + message + '\'' +
                    ", user_type='" + user_type + '\'' +
                    ", from_user_id='" + from_user_id + '\'' +
                    ", to_user_id='" + to_user_id + '\'' +
                    ", name='" + name + '\'' +
                    ", message_type='" + message_type + '\'' +
                    ", store_id='" + store_id + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "IMMessage{" +
                "code='" + code + '\'' +
                ", im=" + im +
                '}';
    }
}
