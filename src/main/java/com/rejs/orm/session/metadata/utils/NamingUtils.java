package com.rejs.orm.session.metadata.utils;

public class NamingUtils {

    public static String camelToSnake(String str){
        if(str == null || str.isEmpty()){
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<str.length();i++){
            char c = str.charAt(i);
            if(i == 0){
                sb.append(Character.toLowerCase(c));
            }else if(Character.isUpperCase(c)){
                char prev = str.charAt(i-1);
                if(!Character.isUpperCase(prev)){
                    sb.append('_');
                }else if (i != str.length()-1 && !Character.isUpperCase(str.charAt(i+1))){
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            }else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
