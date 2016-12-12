package edu.temple.tuf21842.bitcoindashboard;

import android.content.Context;
import android.graphics.Color;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class PriceFragment extends Fragment {

    private TextView priceText;
    private View v;
    private double price;
    private LineChartView chart;
    private ArrayList<PointValue> chartData;
    private final String TAG = "PriceFragment";
    public PriceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_price, container, false);

        priceText = (TextView) v.findViewById(R.id.current_price);
        getPrice();

        chart = (LineChartView)v.findViewById(R.id.chart);
        updateChart();

        return v;
    }

    private void getPrice(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL priceUrl = new URL("https://blockchain.info/ticker");
                    URLConnection priceConnection = priceUrl.openConnection();
                    BufferedReader priceReader = new BufferedReader(new InputStreamReader(priceConnection.getInputStream()));
                    StringBuilder priceSb = new StringBuilder();
                    String priceLine;
                    while((priceLine = priceReader.readLine())!=null){
                        priceSb.append(priceLine+"\n");
                    }
                    JSONObject priceJsonObject = new JSONObject(priceSb.toString());
                    price = priceJsonObject.getJSONObject("USD").getDouble("15m");
                    priceHandler.sendEmptyMessage(0);
                } catch(Exception e){
                    Log.d(TAG, e.toString());

                }
            }
        }).start();
    }

    private void updateChart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL chartUrl = new URL("https://api.blockchain.info/charts/market-price?timespan=5weeks&rollingAverage=8hours&format=json");
                    URLConnection chartConnection = chartUrl.openConnection();
                    BufferedReader chartReader = new BufferedReader(new InputStreamReader(chartConnection.getInputStream()));
                    StringBuilder chartSb = new StringBuilder();
                    String chartLine;
                    while((chartLine = chartReader.readLine())!=null){
                        chartSb.append(chartLine+"\n");
                    }
                    JSONObject chartJsonObject = new JSONObject(chartSb.toString());
                    JSONArray chartDataJsonArray = chartJsonObject.getJSONArray("values");
                    chartData = new ArrayList<>();
                    for(int i=0;i<chartDataJsonArray.length();i++){
                        chartData.add(new PointValue((float)chartDataJsonArray.getJSONObject(i).getDouble("x"),
                                (float)chartDataJsonArray.getJSONObject(i).getDouble("y")));
                    }
                    chartHandler.sendEmptyMessage(0);
                }catch(Exception e){
                    Log.d(TAG, e.toString());
                }
            }
        }).start();

    }

    private Handler priceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            priceText.setText(String.valueOf(price));
            return false;
        }
    });


    private Handler chartHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            //In most cased you can call data model methods in builder-pattern-like manner.
            if(chartData.size()!=0) {
                Line line = new Line(chartData).setColor(Color.BLUE);
                line.setHasPoints(true);
                line.setFilled(false);
                line.setStrokeWidth(5);


                //In most cased you can call data model methods in builder-pattern-like manner.
                //Line valuesline = new Line(values).setColor(Color.BLUE).setCubic(true);

                List<Line> lines = new ArrayList<>();
                lines.add(line);
                //lines.add(valuesline);
                LineChartData data = new LineChartData(lines);
                Axis distanceAxis = new Axis();
                distanceAxis.setName("Distance");
                distanceAxis.setTextColor(ChartUtils.COLOR_ORANGE);
                distanceAxis.setMaxLabelChars(4);
                distanceAxis.setHasLines(true);
                distanceAxis.setHasTiltedLabels(true);
                data.setAxisXBottom(distanceAxis);

                Axis amountAxis = new Axis();
                amountAxis.setName("Amount");
                amountAxis.setTextColor(ChartUtils.COLOR_ORANGE);
                amountAxis.setMaxLabelChars(4);
                amountAxis.setHasLines(true);
                amountAxis.setHasTiltedLabels(true);
                data.setAxisYLeft(amountAxis);



                chart.setLineChartData(data);

                //Viewport v = chart.getMaximumViewport();
                //v.set(v.left, 1478563200, v.right, 1481414400);
                //chart.setMaximumViewport(v);
                //chart.setCurrentViewport(v);
                //chart.setInteractive(true);

            }
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
