package com.wavesplatform.steps;


public class Hooks {


    static String saveTextAttachment(String attachName, String message) {
        System.out.println(attachName + " " + message);
        return message;
    }

    static String saveHtmlAsTextAttachment(String attachName, String html) {
        return html;
    }
}