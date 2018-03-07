package com.hrns.l.snmp_client_test;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by l on 3/6/2018.
 */

public class Connection extends AsyncTask<String, String, String> {
    private PrintStream writer;
    private InputStream reader;
    private String dstadd;
    private int dstport;
    private String receivemsg = null;
    public boolean isConnected = false;
    private static Connection instance = null;
    protected Connection(){

    }
    public static Connection getInstance(){
        if(instance == null){
            instance = new Connection();
        }
        return instance;
    }

    Connection(String addr, int port){
        dstadd = addr;
        dstport = port;
        Log.i("INFO", "CONNECTION");
    }

    @Override
    public String doInBackground(String... params){
        Socket msocket = null;
        try{
            Log.i("INFO", "DST Address: " + dstadd + " Port Number: " + dstport);
            msocket = new Socket(dstadd, dstport);
            Log.i("INFO", "Connect");
            MainSelection.mconnected = true;
            isConnected = true;
            writer = new PrintStream(msocket.getOutputStream());
            reader = msocket.getInputStream();
            byte[] mbuffer = new byte[4096];
            int mread = reader.read(mbuffer, 0, 4096);
            while (mread != -1){
                byte[] tempdata = new byte[mread];
                System.arraycopy(mbuffer, 0, tempdata, 0, mread);
                receivemsg = new String(tempdata);
                Log.i("AsyTsk", receivemsg);
                mread = reader.read(mbuffer, 0, 4096);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(msocket != null){
                try {
                    writer.flush();
                    writer.close();
                    reader.close();
                    msocket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public void sendMessage(String oid){
        writer.println(oid);
        writer.flush();
        Log.i("INFO", "SendMessage");
    }

    public String getReceivemsg(){
        return receivemsg;
    }

    @Override
    protected void onPostExecute(String result){
        if(result == null){
            Log.e("007", "STH Failed");
        }else{
            Log.d("007", "POSTEXECUTE");
            super.onPostExecute(result);
        }
        MainSelection.mconnected = false;
        MainSelection.isfirst = true;
    }
}
