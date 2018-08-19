package com.htetznaing.clonechecker;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BuildConfigs {
    public static boolean getAppName(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                if (toHexString(md.digest()).equals(FuckYou())){
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        return false;
    }

    private static String toHexString(byte[] bytes ) {
        StringBuffer sb = new StringBuffer( bytes.length*2 );
        for( int i = 0; i < bytes.length; i++ ) {
            sb.append( toHex(bytes[i] >> 4) );
            sb.append( toHex(bytes[i]) );
            sb.append(":");
        }
        String temp=sb.toString();
        return temp.substring(0,temp.length()-1);
    }

    private static char toHex(int nibble) {
        final char[] hexDigit = { '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F' };
        return hexDigit[nibble & 0xF];
    }

    private static String FuckYou(){
        byte[] valueDecoded= new byte[0];
        try {
            valueDecoded = Base64.decode("QjI6RkY6REY6QjU6MjY6OUQ6MjM6OUU6Mzc6NTY6NEQ6REM6MkY6RUI6NEU6Mjc6NDQ6N0Q6NkE6RDQ=".getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
        }
        return new String(valueDecoded);
    }
}
