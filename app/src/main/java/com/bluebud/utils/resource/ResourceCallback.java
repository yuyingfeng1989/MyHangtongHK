package com.bluebud.utils.resource;

/**
 * Created by Administrator on 2019/7/2.
 */

public class ResourceCallback {
    private static ResourceCallback factory;

    private ResourceCallback() {
    }

    public static ResourceCallback singleResource() {
        if (factory != null)
            return factory;
        factory = new ResourceCallback();
        return factory;
    }

    /**
     * 通过产品类型返回功能名
     *
     * @param product_type 产品类型
     * @return
     */
    private int[] getMineName(String product_type) {
        int[] generals;
        switch (Integer.valueOf(product_type)) {
            case 30:/*K1设备*/
                generals = ResourceFactory.singleResource().generals_k1;
                break;
            case 24:/*HT-790*/
            case 31:/*HT-790s*/
                generals = ResourceFactory.singleResource().generals_790;
                break;
            case 22:/*HT-771*/
            case 25:/*HT-771A*/
                generals = ResourceFactory.singleResource().generals556;
                break;
            case 9:/*PT-720G*/
            case 10:/*PT-720S*/
            case 70:/*PT-720*/
                generals = ResourceFactory.singleResource().generals5;
                break;
            case 27:/*HT-891*/
            case 29:/*litefamily*/
                generals = ResourceFactory.singleResource().generalsfamily;
                break;
            case 16:/*IDD-213L*/
                generals = ResourceFactory.singleResource().generals33;
                break;
            default:/*其他设备*/
                generals = ResourceFactory.singleResource().generals55;
                break;
        }
        return generals;
    }

    /**
     * 通过设备范围返回功能名
     *
     * @param range 范围
     * @return
     */
    public int[] getMineName(int range, String product_type) {
        int[] generals;
        switch (range) {
            case 1:/*个人设备*/
                generals = ResourceFactory.singleResource().generals1;
                break;
            case 5:/*手表*/
                generals = getMineName(product_type);
                break;
            case 2:/*宠物*/
                generals = ResourceFactory.singleResource().generals2;
                break;
            case 3:/*汽车*/
                generals = ResourceFactory.singleResource().generals3;
                break;
            case 6:/*OBD车辆*/
                if (product_type.equals("16"))
                    generals = getMineName(product_type);
                else
                    generals = ResourceFactory.singleResource().generals3;
                break;
            case 7:/*蓝牙手表*/
                generals = ResourceFactory.singleResource().generals7;
                break;
            default:/*其他设备*/
                generals = ResourceFactory.singleResource().generals4;
                break;
        }
        return generals;

    }

    /**
     * 通过产品类型返回功能图标
     *
     * @param product_type 产品类型
     * @return
     */
    private int[] getMineImage(String product_type) {
        int[] images = null;
        switch (Integer.valueOf(product_type)) {
            case 27:/*HT891*/
            case 29:/*litefamily*/
                break;
            case 30:/*K1设备*/
                images = ResourceFactory.singleResource().image_k1;
                break;
            case 9:/*PT-720G*/
            case 10:/*PT-720S*/
            case 70:/*PT-720*/
                images = ResourceFactory.singleResource().image5;
                break;
            case 16:/*IDD-213L*/
                images = ResourceFactory.singleResource().image33;
                break;
            default:/*其他设备*/
                images = ResourceFactory.singleResource().image55;
        }
        return images;

    }

    /**
     * 通过设备范围返回功能名
     *
     * @param range 范围
     * @return
     */
    public int[] getMineImage(int range, String product_type) {
        int[] images;
        switch (range) {
            case 1:/*个人设备*/
                images = ResourceFactory.singleResource().image1;
                break;
            case 5:/*手表*/
                images = getMineImage(product_type);
                break;
            case 2:/*宠物*/
                images = ResourceFactory.singleResource().image2;
                break;
            case 3:/*汽车*/
                images = ResourceFactory.singleResource().image3;
                break;
            case 6:/*OBD车辆*/
                if (product_type.equals("16"))
                    images = getMineImage(product_type);
                else
                    images = ResourceFactory.singleResource().image3;
                break;
            case 7:/*蓝牙手表*/
                images = ResourceFactory.singleResource().image7;
                break;
            default:/*其他设备*/
                images = ResourceFactory.singleResource().image4;
                break;
        }
        return images;
    }
}
