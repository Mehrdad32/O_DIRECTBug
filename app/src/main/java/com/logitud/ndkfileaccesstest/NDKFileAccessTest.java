package com.logitud.ndkfileaccesstest;

public class NDKFileAccessTest {
    private static final String TAG = "NDKFileAccessTest";

    public NDKFileAccessTest() {

    }

    static {
        System.loadLibrary("hello-jni");
    }

    public native char writeToFileWithODirect(String path);
    public native char writeToFileWithODirect2(String path);
    public native char writeToFileWithoutODirect(String path);
}
