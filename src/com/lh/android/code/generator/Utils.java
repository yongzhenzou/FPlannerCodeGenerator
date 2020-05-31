package com.lh.android.code.generator;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.io.*;

class Utils {
    public static String firstLetterLower(String str){
        String result = "";
        if (str!=null&& str.length()>0){
            String  firstLetter =  str.charAt(0)+"";
            result = str.replaceFirst(firstLetter,firstLetter.toLowerCase());
        }
        return result;
    }
    public static String readToString(File file) {
        String encoding = "UTF-8";
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            return new String(filecontent, encoding);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String readPluginFile(AnAction action, String filename) {
        String encoding = "UTF-8";
        InputStream in = null;
        in = action.getClass().getResourceAsStream("template/" + filename);
        String content = "";
        try {
            content = new String(readStream(in),encoding);
        } catch (Exception e) {
        }
        return content;
    }
    private static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }

        } catch (IOException e) {
        } finally {
            outSteam.close();
            inStream.close();
        }
        return outSteam.toByteArray();
    }
}