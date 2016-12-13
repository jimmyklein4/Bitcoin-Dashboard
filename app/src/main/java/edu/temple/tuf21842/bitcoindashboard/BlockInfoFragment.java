package edu.temple.tuf21842.bitcoindashboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BlockInfoFragment extends Fragment {

    private final String TAG = "BlockInfoFragment";
    private JSONObject blockInfoJsonObject;
    private String blockInput;

    private ProgressBar loading;
    private TextView blockNumber;
    private TextView hashText;
    private TextView previousBlockText;
    private TextView bitsText;
    private TextView blockTime;
    private TextView transactionsText;
    private ImageButton blockSearch;
    private Button nextBlockButton;
    private Button prevBlockButton;
    private EditText enterBlockField;
    public BlockInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_block_info, container, false);
        blockNumber = (TextView)v.findViewById(R.id.block_number);
        hashText = (TextView)v.findViewById(R.id.hash);
        previousBlockText = (TextView)v.findViewById(R.id.previous_block);
        bitsText = (TextView)v.findViewById(R.id.block_bits);
        transactionsText = (TextView)v.findViewById(R.id.number_of_transactions);
        blockTime = (TextView)v.findViewById(R.id.block_time);

        blockSearch = (ImageButton)v.findViewById(R.id.block_search_button);
        enterBlockField = (EditText)v.findViewById(R.id.block_enter_field);

        nextBlockButton = (Button)v.findViewById(R.id.next_block_button);
        prevBlockButton = (Button)v.findViewById(R.id.prev_block_button);

        loading = (ProgressBar)v.findViewById(R.id.progressBar);

        nextBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(blockInput!=null){
                    blockInput = String.valueOf((Integer.parseInt(blockInput) + 1));
                    loading.setVisibility(ProgressBar.VISIBLE);
                    getBlockInfo();
                }
            }
        });

        prevBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(blockInput!=null){
                    blockInput = String.valueOf((Integer.parseInt(blockInput) - 1));
                    loading.setVisibility(ProgressBar.VISIBLE);
                    getBlockInfo();
                }
            }
        });

        blockSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockInput = enterBlockField.getText().toString();
                loading.setVisibility(ProgressBar.VISIBLE);
                getBlockInfo();
            }
        });
        return v;
    }

    private void getBlockInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL blockInfoUrl;
                    if(blockInput.length()<=6){
                        blockInfoUrl = new URL("https://blockchain.info/block-height/"+blockInput+"?format=json");
                    } else {
                        blockInfoUrl = new URL("https://blockchain.info/block/"+blockInput+"?format=json");
                    }

                    URLConnection blockInfoConnection = blockInfoUrl.openConnection();
                    BufferedReader blockInfoReader = new BufferedReader(new InputStreamReader(blockInfoConnection.getInputStream()));
                    StringBuilder blockInfoSb = new StringBuilder();
                    String priceLine;
                    while((priceLine = blockInfoReader.readLine())!=null){
                        blockInfoSb.append(priceLine+"\n");
                    }
                    blockInfoJsonObject = new JSONObject(blockInfoSb.toString());

                    blockHandler.sendEmptyMessage(0);
                } catch(Exception e){
                    Log.d(TAG, e.toString());
                    loading.setVisibility(ProgressBar.INVISIBLE);
                    Toast.makeText(getContext(),R.string.block_error, Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    private Handler blockHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            loading.setVisibility(ProgressBar.INVISIBLE);
            if(blockInput.length()<=6){
                try {
                    blockInfoJsonObject = blockInfoJsonObject.getJSONArray("blocks").getJSONObject(0);
                } catch(Exception e){
                    Log.d(TAG, e.toString());
                }
            }
            try{
                nextBlockButton.setVisibility(Button.VISIBLE);
                prevBlockButton.setVisibility(Button.VISIBLE);
                blockNumber.setText(blockInfoJsonObject.get("height").toString());
                hashText.setText(blockInfoJsonObject.get("hash").toString());
                previousBlockText.setText(blockInfoJsonObject.get("prev_block").toString());
                bitsText.setText(blockInfoJsonObject.get("bits").toString());
                transactionsText.setText(blockInfoJsonObject.get("n_tx").toString());
                int time = (int)blockInfoJsonObject.get("time");
                Date date = new Date((long)time*1000);
                String formattedRecordedAt = new SimpleDateFormat("MMM dd yyyy hh:mm ").format(date);
                blockTime.setText(formattedRecordedAt);
            }catch(Exception e){
                Log.d(TAG, e.toString());
            }

            return false;
        }
    });

}
