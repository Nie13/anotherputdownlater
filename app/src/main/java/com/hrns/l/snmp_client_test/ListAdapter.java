package com.hrns.l.snmp_client_test;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.content.Context;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by l on 3/6/2018.
 */

public class ListAdapter extends BaseExpandableListAdapter {
    private List<String> listFolders;
    private HashMap<String, List<String>> listChild;
    private Context context;

    public ListAdapter(Context context, List<String> listFolders, HashMap<String, List<String>> listChild){
        this.context = context;
        this.listFolders = listFolders;
        this.listChild = listChild;
    }


    @Override
    public View getChildView(int groupPosotion, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
        final String childText = (String) getChild(groupPosotion, childPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item ,null);
        }

        TextView txtlistChild = (TextView) convertView.findViewById(R.id.lblListItem);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        String sf = shared.getString("pref_key_child_font_list", "24");
        Float f = Float.parseFloat(sf);
        txtlistChild.setTextSize(f);
        txtlistChild.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition){
        return true;
    }

    @Override
    public int getGroupCount(){
        return this.listFolders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition){
        return this.listChild.get(this.listFolders.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listFolders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lbListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        String sf = shared.getString("pref_key_font_list", "24");
        Float f = Float.parseFloat(sf);
        lblListHeader.setTextSize(f);

        return convertView;
    }

}
