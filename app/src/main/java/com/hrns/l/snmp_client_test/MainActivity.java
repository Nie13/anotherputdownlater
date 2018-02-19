package com.hrns.l.snmp_client_test;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;



public class MainActivity extends AppCompatActivity {
    Context context;

    private static final String TAG = "mSNMP";
    private static final String commun = "public";
    private static final int SNMP_VERSION = SnmpConstants.version2c;
    private static String ipAddress = "127.0.0.1";
    private static final String port = "161";
    private static final String OIDVALUE = "1.3.6..1.2.1.1.5.0";


    private Button msend;
    private EditText mconsel;
    private ProgressBar mSpinner;
    private StringBuffer mlog = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        //init UI in following lines;
        msend = (Button) findViewById(R.id.sendBtn);
        mconsel = (EditText) findViewById(R.id.console);
        mSpinner = (ProgressBar) findViewById(R.id.progressBar);
        //click listener for send

        msend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAsyncTsk.execute();
            }
        });

    }

    //sneding snmp request
    private void sendRequest(String comand) throws Exception {
        TransportMapping<UdpAddress> transpt = new DefaultUdpTransportMapping();
        transpt.listen();

        mlog.append("CREATING CONNECTION");
        CommunityTarget comtgt = new CommunityTarget();
        comtgt.setCommunity(new OctetString(commun));

        comtgt.setVersion(SNMP_VERSION);

        mlog.append("ADDRESS: " + ipAddress + ":" + port + "\n");

        comtgt.setAddress(new UdpAddress(ipAddress + "/" + port));

        comtgt.setRetries(2);

        comtgt.setTimeout(1000);

        mlog.append("PREPARING PDU \n");

        PDU pdu = new PDU();

        pdu.add(new VariableBinding(new OID(comand)));

        pdu.setType(PDU.GETNEXT);

        Snmp snmp = new Snmp(transpt);

        mlog.append("SNEDING REQUEST... DI DU DI \n");

        ResponseEvent rspns = snmp.send(pdu, comtgt);

        if(rspns != null){
            PDU rspnsPDU = rspns.getResponse();

            Address pAddrs = rspns.getPeerAddress();

            if(rspnsPDU != null){
                int errStat = rspnsPDU.getErrorStatus();
                int errIndx = rspnsPDU.getErrorIndex();
                String errStatTxt = rspnsPDU.getErrorStatusText();

                if(errStat == PDU.noError){
                    mlog.append("SNMP RESPONSE: " + rspnsPDU.getVariableBindings() + "\n");
                }else{
                    mlog.append("FAIL REQUEST WITH ERROR STATUTE: " + errStat + " AND WITH ERROR INDEX: " + errIndx + "\n ERROR STATUS TEXT: " + errStatTxt + "\n");

                }

            }else {
                mlog.append("ERROR: NULL PDU \n");
            }
        }else{
            mlog.append("AGENT TIME OUT, TRY AGAIN");
        }
        snmp.close();
    }

    AsyncTask<Void, Void, Void> mAsyncTsk = new AsyncTask<Void, Void, Void>() {
        protected void onPreExecute(){
            mSpinner.setVisibility(View.VISIBLE);
        };

        @Override
        protected Void doInBackground(Void... params) {
            try {
                sendRequest(OIDVALUE);
            }catch (Exception e){
                Log.d(TAG, "ERROR sending snmp request: " + e.getMessage());
                mlog.append(e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result){
            //mconsel.setText("");
            mconsel.append("\n" + mlog);
            mSpinner.setVisibility(View.GONE);
        };
    };
}
