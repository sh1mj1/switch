package com.example.aswitch;

import android.util.Log;

public class JNIDriver implements JNIListener {

    private boolean mConnectFlag;

    private TranseThread mTranseThread;
    private JNIListener mMainActivity;

    static {
        System.loadLibrary("JNIDriver");
    }

    // JNIDriver.so 에 정의될 JNI 메서드
    private native static int openDriver(String path);
    private native static void closeDriver();
    private native char readDriver();
    private native int getInterrupt();

    public JNIDriver() {
        mConnectFlag = false;
    }

    @Override
    public void onReceive(int val) {
        Log.e("test", "4");
        if (mMainActivity != null) {
            mMainActivity.onReceive(val);
            Log.e("test", "2");
        }
    }

    public void setListener(JNIListener a) {
        mMainActivity = a;
    }

    public int open(String driver) {
        if (mConnectFlag) {         // 이미 연결이 되어있는 경우 리턴(탈출)
            return -1;
        }
        if (openDriver(driver) > 0) { // openDriver 가 가능하면
            mConnectFlag = true;    // open device file
            mTranseThread = new TranseThread();
            mTranseThread.start();  // create a thread and let it run
            return 1;
        } else {
            return -1;
        }
    }
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void close() {
        if (!mConnectFlag) {    // 이미 연결이 되어있지 않은 경우 리턴(탈출)
            return;
        }
        mConnectFlag = false;
        closeDriver();
    }
    public char read() {
        return readDriver();
    }
    private class TranseThread extends Thread {
        @Override
        public void run() {
            super.run();
            try{
                while (mConnectFlag) {
                    try {
                        Log.e("test", "1");
                        onReceive(getInterrupt());
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }catch (Exception e){

            }
        }
    }

}
