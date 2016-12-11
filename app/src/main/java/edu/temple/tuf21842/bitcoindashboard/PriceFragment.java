package edu.temple.tuf21842.bitcoindashboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class PriceFragment extends Fragment {

    private TextView priceText;
    private View v;
    private double price;

    private final String TAG = "PriceFragment";
    public PriceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_price, container, false);

        priceText = (TextView) v.findViewById(R.id.current_price);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://blockchain.info/ticker");
                    URLConnection connection = url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        sb.append(line+"\n");
                    }
                    JSONObject jsonObject = new JSONObject(sb.toString());
                    price = jsonObject.getJSONObject("USD").getDouble("15m");
                    priceHandler.sendEmptyMessage(0);
                } catch(Exception e){
                    Log.d(TAG, e.toString());
                }
            }
        }).start();
        return v;
    }

    private Handler priceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            priceText.setText(String.valueOf(price));
            return false;
        }
    });


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
