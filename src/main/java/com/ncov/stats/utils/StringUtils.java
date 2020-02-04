package com.ncov.stats.utils;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by liuma on 2017/7/20.
 */
public class StringUtils {

    public static Long[] parseLongArray(String input) {
        if (input == null || input.length() == 0) return null;
        input = input.trim();
        input = input.replace("\"", "");
        input = input.replace("'", "");
        input = input.replace("，", ",");
        String[] idstr = input.split(",");
        Long[] idlongs = new Long[idstr.length];
        for (int i = 0; i < idlongs.length; i++) {
            idlongs[i] = Long.parseLong(idstr[i]);
        }
        return idlongs;
    }

    public static List<Long> StringToLongList(String inputs) {
        if (isBlank(inputs)) return new ArrayList<>();
        inputs = inputs.trim();
        inputs = inputs.replace("\"", "");
        inputs = inputs.replace("'", "");
        if (inputs.startsWith(","))
            inputs = inputs.substring(1, inputs.length());
        if (inputs.endsWith(","))
            inputs = inputs.substring(0, inputs.length() - 1);
        String[] idstr = inputs.split(",");
        Set<Long> longList = new LinkedHashSet<>();
        for (int i = 0; i < idstr.length; i++) {
            longList.add(Long.parseLong(idstr[i]));
        }
        return new LinkedList<>(longList);
    }


    public static List<String> StringToStringList(String inputs) {
        if (isBlank(inputs)) return new ArrayList<>();
        inputs = inputs.trim();
        inputs = inputs.replace("\"", "");
        inputs = inputs.replace("'", "");
        String[] str = inputs.split(",");
        List<String> StringList = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            StringList.add(str[i]);
        }
        return StringList;
    }

    public static List<String> StringToStringListByEnter(String inputs) {
        if (isBlank(inputs)) return new ArrayList<>();
        inputs = inputs.trim();
        String[] str = inputs.split("\n");
        List<String> StringList = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            StringList.add(str[i]);
        }
        return StringList;
    }

    public static String StringListToStringWithEnter(List<String> input) {
        if (input == null || input.size() == 0) return "";
        return join(input, '\n');
    }


    //字符串
    public static boolean isNotBlank(String input) {
        if (input == null) return false;
        return (!input.isEmpty());
    }

    public static boolean isBlank(String input) {
        if (input == null) return true;
        return input.isEmpty();
    }


    public static String join(List<String> input, char port) {
        return org.apache.tomcat.util.buf.StringUtils.join(input, port);
    }

    public static String joinLong(List<Long> inputs, char port) {
        if (inputs == null || inputs.size() == 0) return "";
        List<String> newInput = new ArrayList<>();
        for (Long input : inputs) newInput.add("" + input);
        return org.apache.tomcat.util.buf.StringUtils.join(newInput, port);
    }


    public static Long StringToLong(String id) {
        try {
            return Long.valueOf(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }

    }

    //替换标签，<br>换行
    public static String stripHtml(String input) {
        if (isBlank(input)) return null;
        // <p>段落替换为换行
        input = input.replaceAll("<p .*?>", "\r\n");
        // <br><br/>替换为换行
        input = input.replaceAll("<br\\s*/?>", "\r\n");
        // 去掉其它的<>之间的东西
        input = input.replaceAll("\\<.*?>", "");
        // 还原HTML
        // content = HTMLDecoder.decode(content);
        return input;
    }

    public static String randomCode(int length) {
        Random random = new Random();
        String code = "";
        while (length > 0) {
            length--;
            code += random.nextInt(10);
        }
        return code;
    }

    /**
     * 中间隐藏
     */
    public static String hideMiddle(String input, int length) {
        if (length <= 0) return input;
        if (StringUtils.isBlank(input)) {
            return "****";
        } else {
            if (input.length() <= length) return "****";
            else {
                String start = input.substring(0, (input.length() - length) / 2);
                String end = input.substring((input.length() - (input.length() - length) / 2), input.length());
                StringBuffer str = new StringBuffer();
                for (int i = 0; i < length - 1; i++) {
                    str.append("*");
                }
                return start + str + end;
            }
        }

    }

    public static Integer stringToInteger(String str) {
        if (StringUtils.isBlank(str)) return null;
        try {
            return Integer.valueOf(str);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 千位分隔符
     */
    public static String LongToString(Long input) {
        if (input == null) return "";
        try {
            DecimalFormat df = new DecimalFormat("#,###");
            String out = df.format(input);
            return out;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 把单位是元的金额转换为单位是分的金额
     * 只取小数点后的2位，后面舍弃
     * @param str
     * @return
     */
    public static String YuanToFen(String str) {
        String res = "";
        str = str.replaceFirst("^0*", ""); // 去掉前面多余的0
        if (str.startsWith(".")) {
            str = "0" + str;
        }
        if (StringUtils.isBlank(str)) {
            return "0";
        }
        if (str.contains(".")) {
            str = str.replaceAll("0+?$", "");//去掉后面多余的0
            String [] arr = str.split("\\.");
            if (arr.length > 1) {
                if (arr[1].length() == 1) {
                    res = arr[0] + arr[1] + "0";
                } else {
                    res = arr[0] + arr[1].substring(0, 2);
                }
            } else {
                res = arr[0] + "00";
            }
        } else {
            res = str + "00";
        }
        res = res.replaceFirst("^0*", "");// 去掉前面多余的0
        if (StringUtils.isBlank(res))
            return "0";
        return res;
    }

    /**
     * 把单位是分的金额转换为单位是元
     * 第一位必须是有效位
     * @param str
     * @return
     */
    public static String FenToYuan(String str) {
        str = str.replaceFirst("^0*", ""); // 去掉前面多余的0
        if (StringUtils.isBlank(str)) {
            return "0.00";
        }
        int strLen = str.length();
        if (strLen == 1) {
            return "0.0" + str;
        } else if (strLen == 2) {
            return "0." + str;
        } else if (strLen >= 3) {
            return str.substring(0, strLen - 2) + "." + str.substring(strLen - 2, strLen);
        } else {
            return null;
        }
    }


//    public static void main(String[] args) {
//        String input = "12";
//        System.out.println("output:" + getDPoint(input,3));
//        input = "12.1";
//        System.out.println("output:" + getDPoint(input,5));
//        input = "12.1100000";
//       System.out.println("output:" + getDPoint(input,3));
//       System.out.println("output:" + getDPoint(input,4));
//
//    }

    /**
     * 获取小数部分字符串(digit位数对齐),不够位数添0
     * @param input 输入的字符串
     * @param digit 截断的小数个数
     * @return 返回带小数位字符串
     */
    public static String getDPoint(String input,int digit) {
        if (digit < 0) {
            return "";
        }
        if (isBlank(input)) {
            return String.format("%0" + digit + "d", 0);
        }
        int index = input.indexOf(".");
        if (index < 0) {
            return String.format("%0" + digit + "d", 0);
        }
        String subInput = input.substring(index + 1);
        if (subInput.length() >= digit) {
            return subInput.substring(0, digit);
        } else {
            return subInput + String.format("%0" + (digit - subInput.length()) + "d", 0);
        }
    }

    public  static void  main(String []args){
        System.out.println(YuanToFen("1"));
        System.out.println(YuanToFen("0.00"));
        System.out.println(YuanToFen("0.1"));
        System.out.println(YuanToFen("0.111"));
        System.out.println(YuanToFen("1.11"));
        System.out.println(YuanToFen("111.10"));
        System.out.println(YuanToFen("000.10"));
        System.out.println(YuanToFen("010.10"));
        System.out.println(YuanToFen("000"));
        System.out.println(FenToYuan("1"));
        System.out.println(FenToYuan("10"));
        System.out.println(FenToYuan("1000"));
        System.out.println(FenToYuan("100"));
        System.out.println(FenToYuan("0100"));
        System.out.println(FenToYuan("00100000"));
        System.out.println(FenToYuan("0000"));
        System.out.println(FenToYuan("0"));
    }


}
