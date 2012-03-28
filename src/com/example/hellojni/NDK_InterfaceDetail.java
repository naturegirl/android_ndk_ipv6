package com.example.hellojni;

import java.net.Inet6Address;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NDK_InterfaceDetail extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        Bundle extras = getIntent().getExtras();
        String s = extras.getString("ipv6 addr");
        if (s == null)
        	Log.e("mee", "No String with that key found in Extras!");
        byte []ipv6addr = hexStringToByteArray(s);
        Inet6Address ipv6;
        try {
			ipv6 = (Inet6Address) Inet6Address.getByAddress(null, ipv6addr);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
        TextView tv = (TextView) findViewById(R.id.tv4);
        tv.setText(ipv6.getHostAddress());
    }
    
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
