package com.bluebud.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 2017/12/21.
 */

public class RegularUtil {


    /**
     * 正则表达式判断输入中文
     *
     * @return
     */
    public static boolean limitCN(String string) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(string);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断必须包含某些字符种类
     *
     * @param string
     * @return
     */
    public static boolean limitInput(String string) {
        String input = "^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,20}$";
        Pattern p = Pattern.compile(input);
        Matcher matcher = p.matcher(string);
        boolean isInput = matcher.matches();
        return isInput;
    }

    /**
     * 正则表达式判断字符串长度
     *
     * @param string
     * @return
     */
    public static boolean limitInputLength(String string) {
        Pattern p = Pattern.compile("^.{8,20}$");
        boolean isLength = p.matcher(string).matches();
        return isLength;
    }


    /**
     * 输入限制type
     *  绑定 1、设备号输入,
     * 围栏 2、名称输入，
     * 我的账号 3、年龄输入，
     * 信息卡  5、品种 6、车牌号 7、车型 8、车架号 9、sim卡号
     */
//    private  InputFilterHelper.Builder builder;

//    public  InputFilter[] limitUtils(int type) {
//        if (builder==null)
//            builder = new InputFilterHelper.Builder();
//
//        if(type==1) {//绑定设备输入限制
//            builder.addHandler(new NumberFilterHandler()).addHandler(new EnglishFilterHandler()).setInputTextLimitLength(20);
//        }else if(type == 2) {
//            builder .addHandler(new ChineseFilterHandler())//允许输入中文
//                    .addHandler(new EnglishFilterHandler())//允许输入英文
//                    .addHandler(new NumberFilterHandler())//允许输入数字
//                    .setInputTextLimitLength(20);//设置最大字符数
//        }
//        else if(type == 3) {
//            builder.addHandler(new NumberFilterHandler())//允许输入数字
//                    .setInputTextLimitLength(3);//设置最大字符数
//        }
//        else if(type == 4){
//            builder.addHandler(new EnglishFilterHandler())//允许输入英文
//                    .addHandler(new NumberFilterHandler())//允许输入数字
//                    .addHandler(new DecimalPointFilterHandler())//允许输入小数点"."
//                    .addHandler(new LineThroughFilterHandler())//允许输入中划线"-"
//                    .setInputTextLimitLength(30);//设置最大字符数
//        }
//        else if(type == 5){
//            builder .addHandler(new ChineseFilterHandler())//允许输入中文
//                    .addHandler(new EnglishFilterHandler())//允许输入英文
//                    .setInputTextLimitLength(20);//设置最大字符数
//        }
//        else if(type == 6){
//            builder .addHandler(new ChineseFilterHandler())//允许输入中文
//                    .addHandler(new EnglishFilterHandler())//允许输入英文
//                    .addHandler(new NumberFilterHandler())//允许输入数字
//                    .addHandler(new SpecialSymbolFilterHandler())//特殊字符
//                    .setInputTextLimitLength(12);//设置最大字符数
//        }
//        else if(type == 7){
//            builder .addHandler(new ChineseFilterHandler())//允许输入中文
//                    .addHandler(new EnglishFilterHandler())//允许输入英文
//                    .addHandler(new NumberFilterHandler());//允许输入数字
////                    .setInputTextLimitLength(30);//设置最大字符数
//        }
//        else if(type == 8){
//            builder.addHandler(new EnglishFilterHandler())//允许输入英文
//                    .addHandler(new NumberFilterHandler())//允许输入数字
//                    .setInputTextLimitLength(17);//设置最大字符数
//        }
//        else if(type == 9){
//            builder.addHandler(new NumberFilterHandler())//允许输入数字
//                    .setInputTextLimitLength(20);//设置最大字符数
//        }
//        else if(type == 10){
//            builder.addHandler(new NumberFilterHandler())//允许输入数字
//                    .setInputTextLimitLength(2);//设置最大字符数
//        }else if(type == 11){
//            builder.addHandler(new ChineseFilterHandler())//允许输入中文
//                    .addHandler(new EnglishFilterHandler())//允许输入英文
//                    .addHandler(new NumberFilterHandler())//允许输入数字
//                    .addHandler(new SpecialSymbolFilterHandler())//特殊字符
//                    .setInputTextLimitLength(20);//设置最大字符数
//        }else if(type == 12){
//            builder.addHandler(new ChineseFilterHandler())//允许输入中文
//                    .addHandler(new EnglishFilterHandler())//允许输入英文
//                    .addHandler(new NumberFilterHandler())//允许输入数字
//                    .addHandler(new SpecialSymbolFilterHandler())//特殊字符
//                    .setInputTextLimitLength(200);//设置最大字符数
//        }
//
////        builder .addHandler(new ChineseFilterHandler())//允许输入中文
////                    .addHandler(new EnglishFilterHandler())//允许输入英文
////                    .addHandler(new NumberFilterHandler())//允许输入数字
////                    .addHandler(new PunctuationFilterHandler())//允许输入标点符号
////                    .addHandler(new DecimalPointFilterHandler())//允许输入小数点"."
////                    .addHandler(new LineThroughFilterHandler())//允许输入中划线"-"
////                    .setInputTextLimitLength(11);//设置最大字符数
//
//            return InputFilterHelper.build(builder).genFilters();
//
//
//    }
}
