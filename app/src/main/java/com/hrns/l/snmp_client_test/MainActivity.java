package com.hrns.l.snmp_client_test;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

    private static final String commun = "public";
    private static final int SNMP_VERSION = SnmpConstants.version2c;
    private static String ipAddress = "192.168.2.1";
    private static final String port = "161";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        //init UI in following lines;

        //click listener for send

    }

    //sneding snmp request
    private void sendRequest(String comand) throws Exception {
        TransportMapping<UdpAddress> transpt = new DefaultUdpTransportMapping();
        transpt.listen();
        CommunityTarget comtgt = new CommunityTarget();
        comtgt.setCommunity(new OctetString(commun));

        comtgt.setVersion(SNMP_VERSION);

        comtgt.setAddress(new UdpAddress(ipAddress + "/" + port));

        comtgt.setRetries(2);

        comtgt.setTimeout(1000);

        PDU pdu = new PDU();

        pdu.add(new VariableBinding(new OID(comand)));

        pdu.setType(PDU.GETNEXT);

        Snmp snmp = new Snmp(transpt);

        ResponseEvent rspns = snmp.send(pdu, comtgt);

        if(rspns != null){
            PDU rspnsPDU = rspns.getResponse();

            Address pAddrs = rspns.getPeerAddress();

            if(rspnsPDU != null){
                int errStat = rspnsPDU.getErrorStatus();
                int errIndx = rspnsPDU.getErrorIndex();
                String errStatTxt = rspnsPDU.getErrorStatusText();

                if(errStat == PDU.noError){

                }else{

                }

            }else {

            }
        }else{

        }
        snmp.close();
    }

    AsyncTask<Void, Void, Void> AsyncTsk = new AsyncTask<Void, Void, Void>() {
        protected void onPreExecute(){

        }


        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
