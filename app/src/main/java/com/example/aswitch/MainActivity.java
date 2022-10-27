package com.example.aswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aswitch.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements JNIListener{

    TextView tv;
    String str = "";
    JNIDriver mDriver;

    // 스레드 받기 mSegThread
    boolean mThreadRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView1);
        mDriver = new JNIDriver();
        mDriver.setListener(this);

        if (mDriver.open("/dev/sm9s5422_interrupt") < 0) {
            Toast.makeText(MainActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause() {
        mDriver.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onReceive(int val) {
        Message text = Message.obtain();
        text.arg1 = val;
        handler.sendMessage(text);
    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1:
                    tv.setText("UP");
                    break;
                case 2:
                    tv.setText("DOWN");
                    break;
                case 3:
                    tv.setText("LEFT");
                    break;
                case 4:
                    tv.setText("RIGHT");
                    break;
                case 5:
                    tv.setText("CENTER");
                    break;
            }
        }
    };

    /**
     * A native method that is implemented by the 'aswitch' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'aswitch' library on application startup.
    static {
        System.loadLibrary("aswitch");
    }

}