package com.accessibilityservice.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExeCommand {
    private boolean bRunning;
    private boolean bSynchronous;
    private BufferedReader errorResult;
    ReadWriteLock lock;
    private DataOutputStream os;
    private Process process;
    private StringBuffer result;
    private BufferedReader successResult;

    public ExeCommand() {
        this.bRunning = false;
        this.lock = new ReentrantReadWriteLock();
        this.result = new StringBuffer();
        this.bSynchronous = true;
    }

    public ExeCommand(boolean z) {
        this.bRunning = false;
        this.lock = new ReentrantReadWriteLock();
        this.result = new StringBuffer();
        this.bSynchronous = z;
    }

    public String getResult() {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            String str = new String(this.result);
            return str;
        } finally {
            readLock.unlock();
        }
    }

    public boolean isRunning() {
        return this.bRunning;
    }

    public ExeCommand run(String str, final int i, boolean z) {
        if (!(str == null || str.length() == 0)) {
            if (z) {
                try {
                    this.process = Runtime.getRuntime().exec("su");
                } catch (Exception e) {
                }
            } else {
                try {
                    this.process = Runtime.getRuntime().exec("sh");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.bRunning = true;
            this.successResult = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
            this.errorResult = new BufferedReader(new InputStreamReader(this.process.getErrorStream()));
            this.os = new DataOutputStream(this.process.getOutputStream());
            try {
                this.os.write(str.getBytes());
                this.os.writeBytes("\n");
                this.os.flush();
                this.os.writeBytes("exit\n");
                this.os.flush();
                this.os.close();
                if (i > 0) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep((long) i);
                            } catch (Exception e) {
                            }
                            try {
                                ExeCommand.this.process.exitValue();
                            } catch (IllegalThreadStateException e2) {
                                ExeCommand.this.process.destroy();
                            }
                        }
                    }).start();
                }
                final Thread thread = new Thread(new Runnable() {
                    public void run() {
                        Lock writeLock = ExeCommand.this.lock.writeLock();
                        while (true) {
                            try {
                                String readLine = ExeCommand.this.successResult.readLine();
                                if (readLine != null) {
                                    readLine = readLine + "\n";
                                    writeLock.lock();
                                    ExeCommand.this.result.append(readLine);
                                    writeLock.unlock();
                                } else {
                                    try {
                                        ExeCommand.this.successResult.close();
                                        return;
                                    } catch (Exception e) {
                                        Log.i("auto", "close InputStream exception:" + e.toString());
                                        return;
                                    }
                                }
                            } catch (Exception e2) {
                                Log.i("auto", "read InputStream exception:" + e2.toString());
                                try {
                                    ExeCommand.this.successResult.close();
                                    return;
                                } catch (Exception e22) {
                                    Log.i("auto", "close InputStream exception:" + e22.toString());
                                    return;
                                }
                            } catch (Throwable th) {
                                try {
                                    ExeCommand.this.successResult.close();
                                } catch (Exception e3) {
                                    Log.i("auto", "close InputStream exception:" + e3.toString());
                                }
                                throw th;
                            }
                        }
                    }
                });
                thread.start();
                final Thread thread2 = new Thread(new Runnable() {
                    public void run() {
                        Lock writeLock = ExeCommand.this.lock.writeLock();
                        while (true) {
                            try {
                                String readLine = ExeCommand.this.errorResult.readLine();
                                if (readLine != null) {
                                    readLine = readLine + "\n";
                                    writeLock.lock();
                                    ExeCommand.this.result.append(readLine);
                                    writeLock.unlock();
                                } else {
                                    try {
                                        ExeCommand.this.errorResult.close();
                                        return;
                                    } catch (Exception e) {
                                        Log.i("auto", "read ErrorStream exception:" + e.toString());
                                        return;
                                    }
                                }
                            } catch (Exception e2) {
                                Log.i("auto", "read ErrorStream exception:" + e2.toString());
                                try {
                                    ExeCommand.this.errorResult.close();
                                    return;
                                } catch (Exception e22) {
                                    Log.i("auto", "read ErrorStream exception:" + e22.toString());
                                    return;
                                }
                            } catch (Throwable th) {
                                try {
                                    ExeCommand.this.errorResult.close();
                                } catch (Exception e3) {
                                    Log.i("auto", "read ErrorStream exception:" + e3.toString());
                                }
                                throw th;
                            }
                        }
                    }
                });
                thread2.start();
                Thread thread3 = new Thread(new Runnable() {
                    public void run() {
                        try {
                            thread.join();
                            thread2.join();
                            ExeCommand.this.process.waitFor();
                        } catch (Exception e) {
                        } finally {
                            ExeCommand.this.bRunning = false;
                            Log.i("auto", "run command process end");
                        }
                    }
                });
                thread3.start();
                if (this.bSynchronous) {
                    thread3.join();
                }
            } catch (Exception e2) {
                Log.i("auto", "run command process exception:" + e2.toString());
            }
        }
        return this;
    }
}
