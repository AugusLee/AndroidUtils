package Utils;

/**
 * Created by lixing on 2016/6/23.
 */

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * LogUtils工具说明:
 * 1 只输出等级大于等于LEVEL的日志
 * 所以在开发和产品发布后通过修改LEVEL来选择性输出日志.
 * 当LEVEL=NOTHING则屏蔽了所有的日志.
 * 2 v,d,i,w,e均对应两个方法.
 * 若不设置TAG或者TAG为空则为设置默认TAG
 */
public class LogUtils {
    //log的目录
    private static final String LOGFILEDIR = "myapplication/logs";

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static final int LEVEL = VERBOSE;
    public static final String SEPARATOR = ",";

    private static final int LOGFILEMAX = 512 * 1024;
    private static StringBuffer mWriteMsgBuffer;
    private final static int WriteMsgBufferMax = 1 * 1024;
    static HandlerThread mHandlerThread;
    static MyHandler mHandler;

    private static final int WRITE_MSG_WHAT = 0;

    public static void v(String message) {
        if (LEVEL <= VERBOSE) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.v(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void v(String tag, String message) {
        if (LEVEL <= VERBOSE) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.v(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void v(boolean saveLog,String tag, String message) {
        v(tag,message);
        if(saveLog){
            saveFile(tag, message);
        }
    }

    public static void d(String message) {
        if (LEVEL <= DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.d(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void d(String tag, String message) {
        if (LEVEL <= DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.d(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void d(boolean saveLog, String tag, String message){
        d(tag, message);
        if(saveLog){
            saveFile(tag, message);
        }
    }

    public static void i(String message) {
        if (LEVEL <= INFO) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.i(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void i(String tag, String message) {
        if (LEVEL <= INFO) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.i(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void i(boolean saveLog, String tag, String message) {
        i(tag,message);
        if(saveLog){
            saveFile(tag,message);
        }
    }

    public static void w(String message) {
        if (LEVEL <= WARN) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.w(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void w(String tag, String message) {
        if (LEVEL <= WARN) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.w(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void w(boolean saveLog, String tag, String message) {
        w(tag,message);
        if(saveLog){
            saveFile(tag,message);
        }
    }

    public static void e(String message) {
        if (LEVEL <= ERROR) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.e(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void e(String tag, String message) {
        if (LEVEL <= ERROR) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.e(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void e(boolean saveLog, String tag, String message) {
        e(tag,message);
        if(saveLog){
            saveFile(tag,message);
        }
    }

    /**
     * 获取默认的TAG名称.
     * 比如在MainActivity.java中调用了日志输出.
     * 则TAG为MainActivity
     */
    public static String getDefaultTag(StackTraceElement stackTraceElement) {
        String fileName = stackTraceElement.getFileName();
        String stringArray[] = fileName.split("\\.");
        String tag = stringArray[0];
        return tag;
    }

    /**
     * 输出日志所包含的信息
     */
    public static String getLogInfo(StackTraceElement stackTraceElement) {
        StringBuilder logInfoStringBuilder = new StringBuilder();
        // 获取线程名
        String threadName = Thread.currentThread().getName();
        // 获取线程ID
        long threadID = Thread.currentThread().getId();
        // 获取文件名.即xxx.java
        String fileName = stackTraceElement.getFileName();
        // 获取类名.即包名+类名
        String className = stackTraceElement.getClassName();
        // 获取方法名称
        String methodName = stackTraceElement.getMethodName();
        // 获取生日输出行数
        int lineNumber = stackTraceElement.getLineNumber();

        logInfoStringBuilder.append("[ ");
        logInfoStringBuilder.append("threadID=" + threadID).append(SEPARATOR);
        logInfoStringBuilder.append("threadName=" + threadName).append(SEPARATOR);
        logInfoStringBuilder.append("fileName=" + fileName).append(SEPARATOR);
        logInfoStringBuilder.append("className=" + className).append(SEPARATOR);
        logInfoStringBuilder.append("methodName=" + methodName).append(SEPARATOR);
        logInfoStringBuilder.append("lineNumber=" + lineNumber);
        logInfoStringBuilder.append(" ] ");
        return logInfoStringBuilder.toString();
    }


    /**
     * 根据通配符
     * 获取时间
     *
     * @return
     */
    private static String getCurrentTime(String str) {
        long currentTime = System.currentTimeMillis();                    //得到的是ms
        SimpleDateFormat formatter = new SimpleDateFormat(str);
        Date date = new Date(currentTime);
        String time = formatter.format(date);
        return time;
    }

    /**
     * 以默认通配符获取当前时间，
     * @return
     */
    private static String getCurrentTime(){
        long currentTime = System.currentTimeMillis();                    //得到的是ms
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
        Date date = new Date(currentTime);
        String time = formatter.format(date);
        return time;
    }

    /**
     * 获取log文件绝对路径
     * @return
     */
    private static String getLogFilePath(){
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String logDir = sdcardPath + LOGFILEDIR;
        File dir = new File(logDir);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String logName = sdcardPath + "/" + LOGFILEDIR + "/" + "log" ;
        return  logName;
    }

    /**
     * 将log信息保存到文件中
     * @param tag
     * @param content
     */
    private static void saveFile(String tag, String content) {
        synchronized (LogUtils.class) {
            String time = getCurrentTime();
            if(mWriteMsgBuffer == null){
                mWriteMsgBuffer = new StringBuffer();
            }
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            mWriteMsgBuffer.append(time + "  ").append(tag + ":").append(stackTraceElement + ":").append(content + "\n");

            if (mHandlerThread == null) {
                mHandlerThread = new HandlerThread("write_log_thread");
                mHandlerThread.start();
            }

            if (mHandler == null) {
                // Handler的handlerMessage方法在主线程执行还是在子线程执行，取决于创建Handler时传递给Handler的looper
                mHandler = new MyHandler(mHandlerThread.getLooper());
            }

            Message msg = new Message();
            msg.what = WRITE_MSG_WHAT;
            mHandler.removeMessages(WRITE_MSG_WHAT);
            if (mWriteMsgBuffer.length() >= WriteMsgBufferMax) {
                mHandler.sendMessage(msg);
            } else {
                mHandler.sendMessageDelayed(msg,3000);
            }

        }
    }


    /**
     * 写入文件中
     */
    private static void writeFile(){
        synchronized (LogUtils.class) {
            if(mWriteMsgBuffer.length() == 0){
                return;
            }
            String content = mWriteMsgBuffer.toString();
            String logPath = getLogFilePath();
            File logFile = new File(logPath);
            FileOutputStream outputStream = null;
            try {

                if (!logFile.exists()) {
                    logFile.createNewFile();
                }
                outputStream = new FileOutputStream(logFile, true);
                byte[] bytes = content.getBytes("utf-8");
                outputStream.write(bytes);
                outputStream.flush();
                mWriteMsgBuffer.delete(0,mWriteMsgBuffer.length());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class MyHandler extends Handler{
        public MyHandler(Looper loop){
            super(loop);
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case WRITE_MSG_WHAT:
                    writeFile();
                    break;
            }
        }
    }
}
