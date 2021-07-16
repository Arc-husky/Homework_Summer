package com.bytedance.practice5.socket;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSocketThread extends Thread {
    public ClientSocketThread(SocketActivity.SocketCallback callback) {
        this.callback = callback;
    }

    private SocketActivity.SocketCallback callback;
    private boolean stopFlag = false;
    //head请求内容
    private static String content = "HEAD / HTTP/1.1\r\nHost:www.zju.edu.cn\r\n\r\n";


    @Override
    public void run() {
        // TODO 6 用socket实现简单的HEAD请求（发送content）
        //  将返回结果用callback.onresponse(result)进行展示
        try {
            Socket socket = new Socket("www.zju.edu.cn",80);
            BufferedOutputStream os = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream is = new BufferedInputStream(socket.getInputStream());
            byte[] data = new byte[1024*5];
            os.write(content.getBytes());
            os.flush();
            int recievelen = is.read(data);
            String result = new String(data,0,recievelen);
            Log.i("ClientSocketThread",result);
            callback.onResponse(result);
            sleep(300);
            os.flush();
            os.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}