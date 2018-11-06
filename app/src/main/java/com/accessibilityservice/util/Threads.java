package com.accessibilityservice.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Threads {
    private Map<String, Object> map = new HashMap();

    public static class Fn {
        public void onErr(Exception exception) {
        }

        public void onRun(MyThread thread) {
        }
    }

    public void clearInterval(String str) {
        MyThread thread = (MyThread) this.map.get(str);
        if (thread != null) {
            thread.end();
            this.map.remove(str);
        }
    }

    public void clearTimeout(String str) {
        MyThread thread = (MyThread) this.map.get(str);
        if (thread != null) {
            thread.end();
            this.map.remove(str);
        }
    }

    public void release() {
        for (String str : this.map.keySet()) {
            MyThread thread = (MyThread) this.map.get(str);
            if (thread != null) {
                thread.end();
            }
        }
        this.map.clear();
    }

    public void repeat(Fn fn) {
        repeat(fn, 0);
    }

    public void repeat(final Fn fn, int i) {
        final String uuid = UUID.randomUUID().toString();
        final MyThread thread = new MyThread(new MyThread.Fn() {
            public void onRun(MyThread thread) {
                while (thread.isRun() && map.containsKey(uuid)) {
                    try {
                        fn.onRun(thread);
                        Thread.sleep((long) 60);
                    } catch (Exception e) {
                        fn.onErr(e);
                        thread.end();
                        return;
                    }
                }
            }
        });
        if (i > 0) {
            setTimeout(new Fn() {
                public void onRun(MyThread thread) {
                    thread.end();
                    super.onRun(thread);
                }
            }, i);
        }
        this.map.put(uuid, thread);
        thread.begin();
        thread.join();
    }

    public String setInterval(final Fn fn, final int i) {
        final String uuid = UUID.randomUUID().toString();
        MyThread thread = new MyThread(new MyThread.Fn() {
            public void onRun(MyThread thread) {
                while (thread.isRun() && Threads.this.map.containsKey(uuid)) {
                    try {
                        Thread.sleep((long) (i + 60));
                        fn.onRun(thread);
                    } catch (InterruptedException e) {
                    } catch (Exception e2) {
                        fn.onErr(e2);
                        e2.printStackTrace();
                    }
                }
                map.remove(uuid);
                super.onRun(thread);
            }
        });
        this.map.put(uuid, thread);
        thread.begin();
        return uuid;
    }

    public String setTimeout(final Fn fn, final int i) {
        final String uuid = UUID.randomUUID().toString();
        MyThread thread = new MyThread(new MyThread.Fn() {
            public void onRun(MyThread thread) {
                try {
                    Thread.sleep((long) (i + 60));
                    if (thread.isRun()) {
                        fn.onRun(thread);
                    }
                    super.onRun(thread);
                } catch (Exception e) {
                    fn.onErr(e);
                } finally {
                    Threads.this.map.remove(uuid);
                }
            }
        });
        this.map.put(uuid, thread);
        thread.begin();
        return uuid;
    }

    public void task(final Fn fn) {
        final String uuid = UUID.randomUUID().toString();
        MyThread thread = new MyThread(new MyThread.Fn() {
            public void onRun(MyThread thread) {
                try {
                    if (thread.isRun()) {
                        fn.onRun(thread);
                    }
                    super.onRun(thread);
                } catch (Exception e) {
                    fn.onErr(e);
                } finally {
                    map.remove(uuid);
                }
            }
        });
        this.map.put(uuid, thread);
        thread.begin();
    }
}
