package pl.szkolenie.projekty.animacja;

import android.app.Activity;
import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HttpServ extends Thread {
    private final Activity Context;
    private String address;
    private ContentValues postParam;
    private OnResponseFromServer responseListener;

    public static void GET(Activity context, String address, ContentValues postParam, OnResponseFromServer responseListener)
    {
        new HttpServ(context, address, postParam, responseListener);
    }

    //constructor
    private HttpServ(Activity context, String address, ContentValues postParam, OnResponseFromServer responseListener) {
        super();
        this.postParam=postParam;
        this.address=address;
        this.responseListener=responseListener;
        this.Context=context;
        this.start();

    }

    @Override
    public synchronized void start() {

        try {
            super.start();
        } catch (Exception e) {
           String error="";
            if(e!=null)
                error=e.getMessage();
            Log.d(MainActivity.TAG, "error in start() "+error);
        }
    }

    @Override
    public void run() {

        try {
            String result= getResponseFromServer(this.address, this.postParam);
            response(result, true, null);
        } catch (Exception e) {
            String error="";
            if(e!=null)
                error=e.getMessage();
            Log.d(MainActivity.TAG, "error in HttpServ.run()  "+error);
            response("", false, e);
        }
    }

    private void response(final String html, final boolean success, final Exception exIfError)
    {
        this.Context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(responseListener!=null)
                {
                    responseListener.Response(html, success, exIfError);
                }
            }
        });
    }

    private String getResponseFromServer(String address, ContentValues vals) throws Exception {

        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = getHttpConnection(address, vals);

            InputStream is = httpURLConnection.getInputStream();
            return convertStreamToString(is);
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
    }


    public HttpURLConnection getHttpConnection(String address,
                                               ContentValues postParam) throws Exception {

        URL servletURL;
        HttpURLConnection connection = null;

        servletURL = new URL(address);
        connection = (HttpURLConnection) servletURL.openConnection();

        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + "UTF-8");

        if(postParam!=null) {
            OutputStream output = connection.getOutputStream();
            output.write(getQuery(postParam).getBytes("UTF-8"));
        }
        return connection;

    }

    private String getQuery(ContentValues vals) throws UnsupportedEncodingException
    {
        Set<Map.Entry<String, Object>> s=vals.valueSet();
        Iterator itr = s.iterator();
        StringBuilder result = new StringBuilder();

        boolean first = true;
        while(itr.hasNext())
        {
            if (first)
                first = false;
            else
                result.append("&");

            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }


    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
