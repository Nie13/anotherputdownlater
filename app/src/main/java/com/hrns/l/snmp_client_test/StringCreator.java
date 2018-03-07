package com.hrns.l.snmp_client_test;

import android.content.SharedPreferences;

/**
 * Created by l on 3/5/2018.
 */

public class StringCreator {
    public String creatQuerry(String operation, String oid, SharedPreferences sharedPrefs) {
        String out = null;
        out = operation + "/" + sharedPrefs.getString("pref_key_snmp_connection_address", "127.0.0.1") + "/" + sharedPrefs.getString("pref_key_snmp_connection_port", "161") + "/" + sharedPrefs.getString("pref_key_snmp_version","2") + "/" + sharedPrefs.getString("pref_key_connection_community", "public") + "/" + oid + ".0/";
        return out;
    }
}
