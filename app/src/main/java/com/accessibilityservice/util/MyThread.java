package com.accessibilityservice.util;

public class MyThread {
    private Fn fn;
    private boolean isRun;
    private Thread thread;

    public static class Fn {
        public void onErr(Exception exception) {
        }

        public void onRun(MyThread thread) {
        }
    }

    public MyThread(final Fn fn) {
        this.fn = fn;
        this.thread = new Thread(new Runnable() {
            public void run() {
                try {
                    fn.onRun(MyThread.this);
                } catch (Exception e) {
                    fn.onErr(e);
                }
            }
        });
    }

    public void begin() {
        begin(null);
    }

    public void begin(String str) {
        this.isRun = true;
        if (str != null) {
            this.thread.setName(str);
        }
        this.thread.start();
    }

    public void end() {
        if (this.isRun) {
            this.isRun = false;
        }
    }

    public boolean isRun() {
        return this.isRun;
    }

    public void join() {
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
