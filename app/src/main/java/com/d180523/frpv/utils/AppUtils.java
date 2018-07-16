package com.d180523.frpv.utils;


import android.app.ActivityManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.d180523.frpv.common.Const.SP_KEY.IS_FRPC;

public class AppUtils {
    private static final String TAG = "AppUtils";

    public static final String ASSETS_PATH = "packages";//assets路径

    /**
     * 从asset下解压并复制文件到应用目录下
     *
     * @param context
     * @return
     */
    public static boolean copyFromAssets(Context context) {
        if (TextUtils.isEmpty(AppUtils.getUnZipName(context))) {
            Log.e(TAG, "init_files: 需要解压文件不存在");
            return false;
        }

        String outputDirectory = context.getFilesDir().getParent() + File.separator + "frpc";
        String unzipFileAssetsPath = AppUtils.ASSETS_PATH + File.separator + AppUtils.getUnZipName(context);

        try {
            AppUtils.unZip(context, unzipFileAssetsPath, outputDirectory);
            File frpcfile = new File(outputDirectory);
            if (frpcfile.exists() && frpcfile.listFiles().length > 0) {
                SPUtils.getInstance().setProperty(IS_FRPC, true);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 准备好frpc可执行程序，装frpc_xxx从assets目录复制到：应用主目录/frpc/frpc，并添加可执行权限
     *
     * @return
     */
    public static boolean setupFrpc() {

        return true;
    }

    /**
     * Android获取手机cpu架构，支持的指令集
     *
     * @return
     */
    public static String[] getABIs() {
        String[] abis = new String[]{};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        return abis;
    }

    /**
     * 获取assets指定目录下的所有文件
     *
     * @param context
     * @return
     */
    public static String[] getAssetsZips(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] zips = null;
        try {
            zips = assetManager.list(ASSETS_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getAssets: " + e.getMessage());
        }

        return zips;
    }


    /**
     * 获取指定zip
     *
     * @return
     */
    public static String getUnZipName(Context context) {
        String getUnZip = null;
        String[] abis = getABIs();
        String[] zips = getAssetsZips(context);

        if (abis != null && abis.length > 0 && zips != null && zips.length > 0) {
            for (int i = 0; i < abis.length; i++) {
                for (int j = 0; j < zips.length; j++) {
                    if (abis[i].contains(zips[j].substring(5, zips[j].length() - 4))) {
                        getUnZip = zips[j];
                        break;
                    }
                }
            }
        }
        return getUnZip;
    }

    /**
     * @param context         context上下文对象
     * @param assetName       assetName压缩包文件名
     * @param outputDirectory outputDirectory输出目录
     * @throws IOException
     */
    public static void unZip(Context context, String assetName, String outputDirectory) throws IOException {
        Log.d(TAG, "开始解压");
        //创建解压目标目录
        File file = new File(outputDirectory);
        //如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        InputStream inputStream = null;
        //打开压缩文件
        inputStream = context.getAssets().open(assetName);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        //使用1Mbuffer
        byte[] buffer = new byte[1024 * 1024];
        //解压时字节计数
        int count = 0;
        //如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            //如果是一个目录
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                file.mkdir();
            } else {
                //如果是文件
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                //创建该文件
                file.createNewFile();
                //赋于可执行权限 chmod -R u+x
//                Runtime.getRuntime().exec("chmod -R u+x " + file);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                while ((count = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.close();
            }
            //定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
        Log.d(TAG, "解压结束");
    }

    /**
     * 命令执行输出
     * <li>
     * StringBuffer cmds = new StringBuffer();
     * cmds.append("cd ");
     * cmds.append(APP_HOME);
     * cmds.append("\n");
     * exec(new String[]{
     * cmds.toString()
     * });
     * </li>
     *
     * @param cmds
     * @return
     */
    public static String exec(String[] cmds) {

        if (cmds == null || cmds.length == 0)
            return null;

        Process process = null;
        DataOutputStream os = null;
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
                out.append(line);
                out.append("\n");
            }

            res.close();
            os.close();

            process.waitFor();

            if (process != null) {
                process.destroy();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return out.toString();
    }


    /**
     * 保存为ini文件
     *
     * @param context
     * @param content
     * @param frpcProfile
     */
    public static void saveIniFile(Context context, String content, String frpcProfile) {
        Log.d(TAG, "saveIniFile: " + content);
        File file = makeFile(context, frpcProfile);
        Log.d(TAG, "saveIniFile: " + file.getName());
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            outStream.write(content.getBytes());
            outStream.close();
            Log.d(TAG, "saveIniFile: frpc ini 文件存取成功");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建文件存入应用文件目录下
     *
     * @param context
     * @param frpcProfile
     * @return
     */
    public static File makeFile(Context context, String frpcProfile) {

        String ini_path = context.getApplicationContext().getFilesDir() + File.separator + "ini";

        File file = null;
        makeRootDirectory(ini_path);
        try {
            file = new File(ini_path + File.separator + String.format("pro_%s.ini", frpcProfile));
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 创建目录
     *
     * @param filePath
     */
    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.e(TAG, "makeRootDirectory: " + e.getMessage());
        }
    }

    /**
     * 判断本地是否存在该文件
     *
     * @param context
     * @param initName
     * @return
     */
    public static boolean isExist(Context context, String initName) {
        String ini_path = context.getApplicationContext().getFilesDir() + File.separator + "ini";
        File file = new File(ini_path);
        if (file.exists() && file.list().length > 0) {
            String name = String.format("pro_%s.ini", initName);

            for (int i = 0; i < file.list().length; i++) {
                if (name.equals(file.list()[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 展示ini信息
     *
     * @param file
     * @return
     */
    public static String showInfo(File file) {

        StringBuilder sb = new StringBuilder();
        String str = null;

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            InputStreamReader input = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(input);
            while ((str = reader.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }


    /**
     * 方法描述：判断某一Service是否正在运行
     *
     * @param context
     * @param serviceName Service的全路径： 包名 + service的类名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
