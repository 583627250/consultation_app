/**
 * $Id: AccountUtil.java,v 1.6 2012/09/07 06:17:51 xianchao.sun Exp $
 */
package com.consultation.app.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lili.meing@downjoy.com
 */
public class AccountUtil {

    /**
     * 验证用户名是否合法 (6-32字符，以字母开头，只允许数字、字母、下划线，并验证用户名是否已注册)
     * @param username
     * @return
     */
    public static boolean isAvailableUsername(String username) {
        String regEx="^[A-Za-z]\\w{5,31}$";
        if(username.contains("-")) {// 是第三方用户含有中横线的
            regEx="^[0-9]-\\w{5,54}$";
        }
        Pattern pattern=Pattern.compile(regEx);
        Matcher matcher=pattern.matcher(username);
        if(matcher.matches()) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
    }

    /**
     * 判断是否是EMAIL
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String regEx=
            "([\\w[_-][\\.]]+@+[\\w[_-]]+\\.+[A-Za-z]{2,3})|([\\" + "w[_-][\\.]]+@+[\\w[_-]]+\\.+[\\w[_-]]+\\.+[A-Za-z]{2,3})|"
                + "([\\w[_-][\\.]]+@+[\\w[_-]]+\\.+[\\w[_-]]+\\.+[\\w[_-]]+" + "\\.+[A-Za-z]{2,3})";
        Pattern pattern=Pattern.compile(regEx);
        Matcher matcher=pattern.matcher(email);
        if(matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是手机号码
     * @param number
     * @return
     */
    public static boolean isMobileNum(String number) {
        String regEx="^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
        Pattern pattern=Pattern.compile(regEx);
        Matcher matcher=pattern.matcher(number);
        if(matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean isPhoneNum(String num) {
        String regEx="^1[3|4|5|7|8][0-9]\\d{8}$";
        Pattern pattern=Pattern.compile(regEx);
        Matcher matcher=pattern.matcher(num);
        if(matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 获取随机码
     * @param passLenth
     * @return
     */
    public static String getRandomCode(int passLenth) {
        String buffer="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb=new StringBuilder();
        Random r=new Random();
        int range=buffer.length();
        for(int i=0; i < passLenth; i++) {
            // 生成指定范围类的随机数0—字符串长度(包括0、不包括字符串长度)
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

    /**
     * 对真实密码进行加密
     * @param password
     * @param cpSecretKey
     * @return
     * @throws Exception
     */
//    public static String encodeUserInputPassword(String password, String cpSecretKey) throws Exception {
//        return DES.encode(password, cpSecretKey).trim(); // 获得加密后的密码
//    }

    /**
     * 通过用户名判断用户的账号类型
     * @param username
     * @return
     * @throws SmartNgCoreException
     */
    public static AccountType getAccountType(String username) {
        if(isEmail(username)) {
            return AccountType.EMAIL;
        } else if(isPhoneNum(username)) {
            return AccountType.MOBILE_NUMBER;
        } else if(isAvailableUsername(username)) {
            return AccountType.USERNAME;
        } else {
            return null;
        }
    }

    /**
     * 验证密码是否合法（任意字符6-20）
     * @param pwd 明文密码
     * @return
     */
    public static boolean isAvailablePassword(String pwd) {
        String regEx="^[A-Za-z0-9]\\w{5,19}$";
        Pattern pattern=Pattern.compile(regEx);
        Matcher matcher=pattern.matcher(pwd);
        if(matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 匹配2-4个中文字符
     * @param realname
     * @return
     */
    public static boolean isAvailableRealname(String realname) {
        String regEx="^[\u4E00-\u9FA5]{2,4}+$";
        Pattern pattern=Pattern.compile(regEx);
        Matcher matcher=pattern.matcher(realname);
        if(matcher.matches()) {
            return true;
        }
        return false;
    }

    public enum AccountType {

        USERNAME(1, "个性用户名"), EMAIL(2, "邮箱"), MOBILE_NUMBER(3, "手机号");

        private AccountType(Integer id, String name) {
        }
    }
}
