package edu.temple.tuf21842.bitcoindashboard;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class BalanceFragment extends Fragment {

    private Button submitButton;
    private EditText walletField;
    private TextView balanceField;
    private int balance;
    private String wallet;
    public BalanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_balance, container, false);
        walletField = (EditText) v.findViewById(R.id.wallet_field);
        balanceField = (TextView) v.findViewById(R.id.balance);
        submitButton = (Button)v.findViewById(R.id.wallet_submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wallet = walletField.getText().toString();
                getBalance();
            }
        });

        return v;
    }


    private void getBalance(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://blockchain.info/q/addressbalance/" + wallet + "?confirmations=6");
                    URLConnection connection = url.openConnection();
                    BufferedReader priceReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    balance = Integer.parseInt(priceReader.readLine());
                    balanceHandler.sendEmptyMessage(0);
                }catch(Exception e){

                }
            }
        }).start();
    }

    private Handler balanceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            balanceField.setText(balance);
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
