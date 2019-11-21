//package com.bluebud.info;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class LOGINIM {
//    /**
//     * 用户ID
//     */
//    public String uid;
//    /**
//     * 用户昵称
//     */
//    public String name;
//    /**
//     * 类型
//     */
//    public String type;
//    /**
//     * 用户类型：
//     * customer: 用户
//     * service: 客服
//     */
//    public String user_type;
//    /**
//     * 用户头像
//     */
//    public String avatar;
//    /**
//     * 商铺id
//     */
//    public String store_id;
//    /**
//     * 消息来源：
//     */
//    public String origin;
//
//
//    /**
//     * 消息发送者ID
//     */
//    public int from_id;
//    /**
//     * 发送时间
//     */
//    public String time;
//    /**
//     * 消息内容
//     */
//    public String message;
//    /**
//     * 产品ID
//     */
//    public int goods_id;
//
//    /**
//     * 消息类型：
//     * 'come':有客服登录
//     * 'leave':有客服登出
//     * 'init':取得客服列表
//     * 'come_msg':获取到消息
//     * 'come_wait':待接入消息
//     * 'robbed':获取被抢客户
//     * 'user_robbed':通知用户已被接入
//     * 'uoffline':用户已下线
//     * 'close_link':客服已断开
//     * 'others_login':异地登录
//     * 'change_service':切换客服
//     */
//    public String message_type;
//    /**
//     * 接收人ID
//     */
//    public int to_id;
//    /**
//     * 状态：
//     */
//    public int status;
//
//    /**
//     * 客服消息发送
//     */
//    public String msg;//发送消息
//
//    /**
//     * 转json格式
//     *
//     * @throws JSONException
//     */
//    public JSONObject toJson(LOGINIM loginim) {
//        JSONObject localItemObject = new JSONObject();
//        try {
//            localItemObject.put("uid", loginim.uid);
//            localItemObject.put("name", loginim.name);
//            localItemObject.put("type", "login");
//            localItemObject.put("user_type", "customer");
//            localItemObject.put("avatar", loginim.avatar);
//            localItemObject.put("store_id", loginim.store_id);
//            localItemObject.put("origin", "phone");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return localItemObject;
//    }
//
//}
