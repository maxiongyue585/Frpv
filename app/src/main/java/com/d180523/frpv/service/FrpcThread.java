package com.d180523.frpv.service;

import android.app.Service;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;


public class FrpcThread extends Thread {

    private static final String TAG = FrpcThread.class.getSimpleName();
    private Service service;

    private String APP_HOME = null;

    private String FRPC_HOME = null;

    private String INI_PATH = null;

    private String iniFileName;

    private DataOutputStream os = null;

    private Process process = null;

    private FrpcService.PrintCallback mPrintCallback;

    public FrpcThread(Service service, String iniFileName, FrpcService.PrintCallback mPrintCallback) {

        this.service = service;
        this.iniFileName = iniFileName;
        this.mPrintCallback = mPrintCallback;

        APP_HOME = service.getFilesDir().getParent();
        FRPC_HOME = String.format("%s/frpc", APP_HOME);

        INI_PATH = service.getFilesDir() + File.separator + "ini";
    }

    @Override
    public void run() {
        //给予执行权限
        grantfrpc();
        //启动frpc
        startFrpcDaemon(iniFileName);
    }

    private void start_frpc(String iniName) {

        StringBuffer cmds = new StringBuffer();

        cmds.append("cd ");
        cmds.append(FRPC_HOME);
        cmds.append("\n");

        cmds.append("./frpc  -c ");
        cmds.append(String.format("%s/", INI_PATH));
        cmds.append(iniName);
        cmds.append(" &");

//        exec(new String[]{
//                cmds.toString()
//        });
        exec(new String[]{
                cmds.toString()
        }, true);
    }

    /**
     * 赋于可执行权限 chmod -R u+x frpc/
     */
    private void grantfrpc() {

        StringBuffer cmds = new StringBuffer();

        cmds.append("cd ");
        cmds.append(APP_HOME);
        cmds.append("\n");

        cmds.append("chmod -R 0755 frpc");
        cmds.append("\nexit\n");

        exec(new String[]{
                cmds.toString()
        }, false);
    }


    private OutputThread ot = null;

    private class OutputThread extends Thread {

        private Process process;

        public OutputThread(Process process) {

            this.process = process;
        }

        @Override
        public void run() {

            InputStream is = null;
            BufferedReader res = null;

            try {
                is = process.getInputStream();
                res = new BufferedReader(new InputStreamReader(is));

                String line = null;
                while ((line = res.readLine()) != null) {

                    Log.d(TAG, line);
                    if (mPrintCallback != null) {
                        mPrintCallback.print(line);
                    }

                    String[] sub = line.split("\\s+");
                    if (sub[sub.length - 1].contains("./frpc")) {
                        pid = Long.valueOf(sub[1]);
                        Log.d(TAG, "run: pid=" + pid);
                    }

                    if ("exit".equalsIgnoreCase(line)) {
                        break;
                    }
                }

                Log.d(TAG, "run: output thread exit!");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {
                    if (is != null) {
                        is.close();
                    }
                    if (res != null) {
                        res.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private long pid;

    public void startFrpcDaemon(String iniName) {

        StringBuffer cmds = new StringBuffer();

        cmds.append("cd ");
        cmds.append(FRPC_HOME);
        cmds.append("\n");

        cmds.append("./frpc  -c ");
        cmds.append(String.format("%s/", INI_PATH));
        cmds.append(iniName);
        cmds.append(" &\n");
//        cmds.append("echo ps_start\n");
        cmds.append("ps|grep frpc\n");
//        cmds.append("echo ps_end\n");


        BufferedReader res = null;
        StringBuffer out = new StringBuffer();

        try {
            process = Runtime.getRuntime().exec("sh");
            os = new DataOutputStream(process.getOutputStream());

            Log.d(TAG, "exec: " + cmds.toString());

            os.writeBytes(cmds.toString());
            os.flush();

            ot = new OutputThread(process);
            ot.start();

            process.waitFor();

        } catch (InterruptedException e) {

            try {
                if (pid != 0) {
                    os.writeBytes(String.format("kill -9 %d\n", pid));
                    os.flush();
                }


                os.writeBytes("exit\n");
                os.flush();

                process.waitFor();
                Log.d(TAG, "startFrpcDaemon: run exited!");
            } catch (Exception e1) {
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            if (process != null) {
                process.destroy();
                Log.d(TAG, "startFrpcDaemon: process destoryed");
            }

            if (res != null) {

                try {
                    res.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (os != null) {

                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.gc();

        Log.d(TAG, "daemon: exit");
    }


    /**
     * 执行命令
     *
     * @param cmds
     * @param isSend 发送打印信息
     */
    private void exec(String[] cmds, boolean isSend) {

        if (cmds == null || cmds.length == 0)
            return;

        DataOutputStream os = null;
        Process process = null;
        BufferedReader res = null;
        StringBuffer out = new StringBuffer();

        try {
            process = Runtime.getRuntime().exec("sh");
            os = new DataOutputStream(process.getOutputStream());

            for (String cmd : cmds) {

                Log.d(TAG, "exec: " + cmd);
                if (TextUtils.isEmpty(cmd))
                    continue;
                os.writeBytes(cmd);
                os.writeBytes("\n");
            }

            //os.writeBytes("exit\n");
            os.flush();

            res = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = null;
            while ((line = res.readLine()) != null) {

                Log.d(TAG, line);
                if (isSend && mPrintCallback != null) {
                    mPrintCallback.print(line);
//                    senMsg(line);
                }

                if ("exit".equalsIgnoreCase(line)) {

                    os.writeBytes("exit\n");
                    break;
                }
            }

            process.waitFor();

        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            if (process != null) {
                process.destroy();
            }

            if (res != null) {

                try {
                    res.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (os != null) {

                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void terminate() throws Exception {

        if (os != null && ot.isAlive()) {
            this.interrupt();
        }
    }

}
