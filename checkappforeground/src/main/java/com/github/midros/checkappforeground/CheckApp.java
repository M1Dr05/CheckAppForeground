package com.github.midros.checkappforeground;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

public class CheckApp {

    private int timeout = 1000;
    private Context context;
    private InterfaceCheck check;
    private Listener listener;

    public CheckApp(Context context){
        this.context = context;
        check = new CheckAppForeground();
    }

    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                try {
                    Thread.sleep(timeout);
                    startHandler();
                }catch (InterruptedException e){
                    break;
                }
            }
        }
    });

    public CheckApp getAppForeground(Listener listener){
        this.listener = listener;
        return this;
    }

    public CheckApp setTimeout(int time ){
        this.timeout = time;
        return this;
    }

    public CheckApp start(){
        thread.start();
        return this;
    }

    public void stop(){
        thread.interrupt();
    }

    private void startHandler(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                    listener.onForeground(check.getForegroundPostLollipop(context));
                else
                    listener.onForeground(check.getForegroundPreLollipop(context));
            }
        });
    }

    interface Listener{
        void onForeground(String packages);
    }
}
