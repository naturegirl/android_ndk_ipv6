/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hellojni;

import java.util.StringTokenizer;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


public class HelloJni extends ListActivity
{
    private String []interfaces;		// name of the interfaces
    private String []v6addr;			// name of the v6 addresses in interface, one per interface
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!fileExists()) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("This device does not seem to support ipv6. Exiting...")
    		       .setCancelable(false)
    		       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                HelloJni.this.finish();
    		           }
    		       });
    		AlertDialog alert = builder.create();
    		alert.show();
    		return;
        }
        
        String test = stringFromJNI();
        StringTokenizer st = new StringTokenizer(test,";");
        interfaces = new String[st.countTokens()];
        v6addr = new String[st.countTokens()];
        
        int i = 0;
        while(st.hasMoreTokens()) {
        	String s = st.nextToken();
        	StringTokenizer st2 = new StringTokenizer(s, ":");
        	interfaces[i] = st2.nextToken();
        	v6addr[i] = st2.nextToken();
        	i++;
        }
        //setContentView(R.layout.main);
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, interfaces));
        
        ListView lv = getListView();
        lv.setOnItemClickListener(new OnItemClickListener() {
    	    public void onItemClick(AdapterView<?> parent, View view,
    	        int position, long id) {
    	      // When clicked, show a toast with the TextView text
    	      Intent intent = new Intent(HelloJni.this, NDK_InterfaceDetail.class);
    	      intent.putExtra("ipv6 addr", v6addr[position]);
    	      startActivity(intent);
    	    }
    	  });
    }


    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String  stringFromJNI();
    
    public native boolean fileExists();

    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.HelloJni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("hello-jni");
    }
}
