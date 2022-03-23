package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HandlerThreadActivity extends AppCompatActivity {

    // 新的handler类要声明成静态类
    static class MyHandler extends Handler{
        WeakReference<HandlerThreadActivity> activityWeakReference;

        // 构造函数，传来的是外部类的this
        public MyHandler(@NonNull Looper looper, HandlerThreadActivity activity){
            super(looper);// 调用父类的显式指明的构造函数
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            HandlerThreadActivity threadActivity = activityWeakReference.get();
            if(threadActivity == null)
                return ;//avtivity都没了还处理个XXX

            System.out.print(msg.toString());
            threadActivity.tv_Content.setText(msg.toString());

            switch (msg.what) {
                case 0:
                    //在这里通过nactivity引用外部类
                    System.out.print(msg.what);
                    break;
                default:
                    break;
            }
        }
    }

    MyHandler myHandler = new MyHandler(Looper.myLooper(),this);//获取Looper并传递

    private Button btn_handler, btn_handler1;
    private TextView tv_Content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);

        tv_Content = findViewById(R.id.tv_content);
        btn_handler = findViewById(R.id.btn_handler);
        btn_handler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myHandler.sendEmptyMessage(5);
            }
        });

        testHandlerThread();
    }

    /**
     * 测试子线程中的Handler、Message
     */
    private void testHandlerThread(){
        HandlerThread handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Message message = new Message();
                if (msg.what == 1) {
                    message.what = 6;
                }
                myHandler.sendMessage(message);
            }
        };
        btn_handler1 = findViewById(R.id.btn_handler1);
        btn_handler1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.sendEmptyMessage(1);
            }
        });
    }
}
