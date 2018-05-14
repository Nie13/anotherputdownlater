package com.hrns.l.android_snmp_neo;

import android.graphics.Color;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;
import org.snmp4j.Target;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MainActivity extends AppCompatActivity {

    private String[] wPlanetTitles;
    private DrawerLayout wDrawerLayout;
    private TextView wTextContent;
    private ListView wTitleList;
    private ListView wContentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wDrawerLayout = (DrawerLayout) findViewById(R.id.wdrawer_layout);
        wPlanetTitles = getResources().getStringArray(R.array.table_list);
        wTextContent = (TextView) findViewById(R.id.contentText);
        //wDrawerList = (ListView) findViewById(R.id.wlistview);
        wContentList = (ListView)findViewById(R.id.wcontent);
        wTitleList = (ListView)findViewById(R.id.wtitle);

        Toolbar wtoolbar = findViewById(R.id.wtoolBar);
        setSupportActionBar(wtoolbar);
        ActionBar wactionBar = getSupportActionBar();
        wactionBar.setDisplayHomeAsUpEnabled(true);
        wactionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        final CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        //target.setAddress(GenericAddress.parse("udp:172.16.28.198/161"));
        target.setAddress(new UdpAddress("172.16.28.198/161"));
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);

        String[] prev = getResources().getStringArray(R.array.prev);
        wTitleList.setAdapter(new ArrayAdapter(MainActivity.this,  R.layout.list_content_item , prev ));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem witem) {
                int id = witem.getItemId();
                witem.setChecked(true);
                wDrawerLayout.closeDrawers();
                if(id == R.id.nav_system && target.getAddress().toString().equals(getResources().getString(R.string.generic_add)) ){
                    String[] titles = getResources().getStringArray(R.array.title_system_arr);
                    String[] Contents = getResources().getStringArray(R.array.system_arr);
                    wTitleList.setAdapter(new ArrayAdapter(MainActivity.this, R.layout.list_content_item, titles));
                    wContentList.setAdapter(new ArrayAdapter(MainActivity.this, R.layout.list_content_item, Contents));
                    try{
                        TransportMapping trans = new DefaultUdpTransportMapping();
                        trans.listen();
                        wTextContent.append("\n try part start,,,,,");
                        //PDU response = snmpGet();
                        PDU pdu = new PDU();
                        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3.0")));
                        pdu.setRequestID(new Integer32(1));
                        pdu.setType(PDU.GETNEXT);
                        wTextContent.append("\n try 2nd part start,,,,,");
                        Snmp snmp = new Snmp(trans);
                        wTextContent.append("\n try 2.2nd part start,,,,,");
                        ResponseEvent response = snmp.getNext(pdu, target);
                        wTextContent.append("\n try 3rd part start,,,,,");
                        if(response == null){
                            wTextContent.append("\n NOTHING");
                        }else{
                            PDU responsePDU = response.getResponse();
                            wTextContent.append("\n" + responsePDU.getVariableBindings().toString());
                        }
                        snmp.close();
                    }catch (IOException e){
                        wTextContent.append("\n IO exception" + e.getMessage());
                    }catch (Exception e){
                        wTextContent.append("\n ERROR DUDE" + e.getMessage());
                    }

                    int totalH = 0;
                    int desireW = View.MeasureSpec.makeMeasureSpec(wContentList.getWidth(), View.MeasureSpec.AT_MOST);
                    int BB = 0;
                    for(int i = 0; i<wContentList.getAdapter().getCount(); i++){
                        View itemB = wContentList.getAdapter().getView(i, null, wContentList);
                        itemB.measure(desireW, View.MeasureSpec.UNSPECIFIED);
                        totalH += itemB.getMeasuredHeight();
                        String s = wContentList.getItemAtPosition(i).toString();
                        if(s.equals("")){
                          itemB.setBackgroundColor(Color.YELLOW);
                          BB ++;
                            //wContentList.getChildAt(i).setBackgroundColor(Color.YELLOW);


                        }
                    }
                    ViewGroup.LayoutParams params = wTitleList.getLayoutParams();
                    params.height = totalH + (wTitleList.getDividerHeight() * (wTitleList.getAdapter().getCount() -1));
                    wTitleList.setLayoutParams(params);
                    wTitleList.requestLayout();

                    wContentList.setLayoutParams(params);
                    wContentList.requestLayout();
                    Toast.makeText(MainActivity.this, "Total " + BB + " times of null appeared", Toast.LENGTH_LONG).show();
                    /*StringBuilder terminalResult = new StringBuilder();
                    try{
                        terminalResult.append("STEP 0");
                        Map<String, String> result = doWalk(".1.3.6.1.2.1.1", target);
                        terminalResult.append("STEP 1");
                        if(result == null){
                            terminalResult.append("\n RESULT IS NULL");
                        }
                        terminalResult.append("STEP 2");
                        for(Map.Entry<String, String> entry : result.entrySet()){
                            if(entry.getKey().startsWith(".1.3.6.1.2.1.1.1.")){
                                //System.out.println("sysDescr" + entry.getKey().replace(".1.3.6.1.2.1.1.1.0", "") + ": " + entry.getValue());
                                //Log.i("", "sysDescr " + entry.getKey().replace(".1.3.6.1.2.1.1.1.0", "") + ": " + entry.getValue());
                                terminalResult.append("\n sysDescr " + entry.getKey() + ": " + entry.getValue());
                            }
                            if(entry.getKey().startsWith(".1.3.6.1.2.1.1.2.")){
                                //System.out.println("sysObjectID" + entry.getKey().replace(".1.3.6.1.2.1.1.2.0", "") + ": " + entry.getValue());
                                terminalResult.append("\n sysObjectID " + entry.getKey() + entry.getValue());
                            }
                        }
                    }catch (Exception e){
                        Log.e("", "HAVING ERRORS");
                        terminalResult.append("\n" + e.getMessage() + e.getStackTrace() + e.getCause());
                    }
                    wTextContent.append(terminalResult.toString());*/

                } else if( id == R.id.nav_ip && target.getAddress().toString().equals(getResources().getString(R.string.generic_add))){
                    String[] titles = getResources().getStringArray(R.array.ip_title_arr);
                    String[] Contents = getResources().getStringArray(R.array.ip_arr);
                    wTitleList.setAdapter(new ArrayAdapter(MainActivity.this, R.layout.list_content_item, titles));
                    wContentList.setAdapter(new ArrayAdapter(MainActivity.this, R.layout.list_content_item, Contents));
                    try{
                       String newresult = snmpresult(target);
                       wTextContent.append(newresult);
                    }catch (Exception e){
                        wTextContent.append("\n" + e.getMessage() + "FUCKED");
                    }
                    int totalH = 0;
                    int desireW = View.MeasureSpec.makeMeasureSpec(wTitleList.getWidth(), View.MeasureSpec.AT_MOST);
                    for(int i = 0; i<wTitleList.getAdapter().getCount(); i++){
                        View itemB = wTitleList.getAdapter().getView(i, null, wTitleList);
                        itemB.measure(desireW, View.MeasureSpec.UNSPECIFIED);
                        totalH += itemB.getMeasuredHeight();
                    }
                    ViewGroup.LayoutParams params = wTitleList.getLayoutParams();
                    params.height = totalH + (wTitleList.getDividerHeight() * (wTitleList.getAdapter().getCount() -1));
                    wTitleList.setLayoutParams(params);
                    wTitleList.requestLayout();

                    wContentList.setLayoutParams(params);
                    wContentList.requestLayout();

                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem witem){
        switch (witem.getItemId()){
            case android.R.id.home:
                wDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(witem);
    }

    public static Map<String, String> doWalk(String tableOid, Target target) throws IOException{
        Map<String, String> result = new TreeMap<>();
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();

        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List <TreeEvent> events = treeUtils.getSubtree(target, new OID(tableOid));
        if(events == null || events.size() == 0){
            Log.e("", "Error: Unable to read table,,,");
            return result;
        }

        for (TreeEvent event : events) {
            if(event == null){
                continue;
            }
            if(event.isError()){
                Log.i("", "ERROR: table OID: " + tableOid + "] " + event.getErrorMessage());
                continue;
            }
            VariableBinding[] varBindings = event.getVariableBindings();
            if(varBindings == null || varBindings.length == 0){
                continue;
            }
            for(VariableBinding varBinding : varBindings){
                if (varBinding == null){
                    continue;
                }

                result.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());

            }
        }
        snmp.close();
        return result;
    }

    public static String snmpresult(Target wtarget) throws IOException{
        String result;
        TransportMapping transportMapping = new DefaultUdpTransportMapping();
        transportMapping.listen();

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3.0")));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));

        Snmp snmp = new Snmp(transportMapping);
        ResponseEvent responseEvent = snmp.get(pdu, wtarget);

        if(responseEvent != null){
            PDU responsePDU = responseEvent.getResponse();
            if (responsePDU != null ){
                if(responsePDU.getErrorStatus() == PDU.noError){
                    result = responsePDU.getVariableBindings().toString();
                }else{
                    result = "PDU ERROR";
                }
            }else{
                result = "responsePDU null";
            }
        }else{
            result = "response event null";
        }
        snmp.close();
        return result;
    }

    public static PDU snmpGet(){
        /*ScopedPDU pdu = new ScopedPDU();
        pdu.setType(PDU.GET);
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.4.0")));
        Snmp snmp = new Snmp();

        ResponseEvent responseEvent = snmp.send(pdu, wtarget);
        PDU response =  responseEvent.getResponse();

        return response;*/
        try{
            TransportMapping trans = new DefaultUdpTransportMapping();
            trans.listen();

            CommunityTarget wtarget = new CommunityTarget();
            wtarget.setCommunity(new OctetString("public"));
            //target.setAddress(GenericAddress.parse("udp:172.16.28.198/161"));
            wtarget.setAddress(new UdpAddress("172.16.28.198/161"));
            wtarget.setRetries(2);
            wtarget.setTimeout(1500);
            wtarget.setVersion(SnmpConstants.version2c);


            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3.0")));
            pdu.setRequestID(new Integer32(1));
            pdu.setType(PDU.GETNEXT);

            Snmp snmp = new Snmp(trans);

            ResponseEvent response = snmp.getNext(pdu, wtarget);

            if(response != null){
                PDU responsePDU = response.getResponse();
                snmp.close();

                if(responsePDU != null){
                    String errorText = responsePDU.getErrorStatusText();
                    int errorStatus = responsePDU.getErrorStatus();

                    if(errorStatus == PDU.noError){
                        return responsePDU;
                    }else{
                        return responsePDU;
                    }
                }else{
                    return null;
                }
            }else{
                return null;
            }

        }catch (IOException e){
            return null;
        }

    }

}
