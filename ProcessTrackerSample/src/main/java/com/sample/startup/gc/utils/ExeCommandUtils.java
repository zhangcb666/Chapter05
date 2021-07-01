package com.sample.startup.gc.utils;


import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;


public class ExeCommandUtils {
    private static final String TAG = "ExeCommandUtils";
    static String sLineBreak = System.getProperty("line.separator");

    public static String execSu(String cmd, ICmdCallBack callBack, Object param) {
        return _execByUser("su",cmd,callBack,param);
    }
    public static String execSh(String cmd, ICmdCallBack callBack, Object param) {
        return _execByUser("sh",cmd,callBack,param);
    }

    private static String _execByUser(String use, String cmd, ICmdCallBack callBack, Object param) {
        Logu.d(TAG,"cmd:"+cmd);

        Process process = null;

        try {
            process = Runtime.getRuntime().exec(use);
        } catch (IOException e) {
            Logu.e(TAG,e);
        }

        Timer timer = new Timer();
        timer.schedule(new ExeTimerTask(process,cmd),1000*60*60);

        Logu.d(TAG,"process:"+process);
        if (process != null) {
            BufferedReader successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            DataOutputStream out = new DataOutputStream(process.getOutputStream());
//            Logu.d();
            try{
                out.write(cmd.getBytes());
                out.writeBytes("\n");
                out.writeBytes("exit");
                out.writeBytes("\n");
                out.flush();
                Logu.d(TAG,"write end");
//                close(out);
            }catch (Exception e){
//                e.printStackTrace();
                Logu.e(TAG,e);
            }

            StringBuilder result = new StringBuilder();
            result.append(sLineBreak);

            StringBuilder sucess = new StringBuilder();
            try{
//                Logu.d();
                String line = null;
                while ((line = successResult.readLine()) != null){
                    sucess.append(line);
                    sucess.append(sLineBreak);

                    result.append(line);
                    result.append(sLineBreak);
                    if (callBack != null)callBack.onReadLine(param,line);
                }
                Logu.d(TAG,"msg:"+result.toString());
            }catch (Exception e){
                Logu.e(TAG,e);
            }

            try{
//                Logu.d();
                StringBuilder error = new StringBuilder();
//                sb.append(sLineBreak);
                String line = null;
                while ((line = errorResult.readLine()) != null){
                    error.append(line);
                    error.append(sLineBreak);

                    result.append(line);
                    result.append(sLineBreak);
                    if (callBack != null)callBack.onFailed(param,line);
                }
                if (error.length() > 0)Logu.d(TAG,"cmd:"+cmd+",error:"+error.toString());
            }catch (Exception e){
                Logu.e(TAG,e);
            }
//            Logu.d();
            if (timer != null)timer.cancel();
            close(successResult);
            close(errorResult);

            if (callBack != null)callBack.onEnd(param,result.toString());
            return sucess.toString();
        }
        return null;
    }

    public static void close(Closeable c){
        try {
            if (c != null){
                c.close();
                c = null;
            }
        }catch (Exception e){

        }
    }

    public static class ExeTimerTask extends TimerTask{
        private Process mProcess;
        private String mCmd;
        public ExeTimerTask(Process process,String cmd){
            mProcess = process;
            mCmd = cmd;
        }

        @Override
        public void run() {
            Logu.e(TAG,"mCmd:"+mCmd+" timeout........");
            try {
                if (mProcess != null)mProcess.destroy();
            }catch (Exception e){
                Logu.e(TAG,e);
            }
        }
    }

    public interface ICmdCallBack<T>{
        void onReadLine(T param, String line);
        void onFailed(T param, String line);
        void onEnd(T param, String result);
    }
}
