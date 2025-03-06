package com.example.termitewallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.termitewallet.ResponseServices.ApiResponseTransaction;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.math.BigDecimal;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class SendActivity extends AppCompatActivity {
    private ApiService apiService;
    private EditText recipientInput, amountInput;
    private Button confirmButton;
    private String senderAddress;
    private String privateKey;
    private String publicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.addProvider(new BouncyCastleProvider());
        setContentView(R.layout.activity_send);

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> onBackPressed());
        recipientInput = findViewById(R.id.editTextText2);
        amountInput = findViewById(R.id.transfer_amount);
        confirmButton = findViewById(R.id.button_confirm);

        senderAddress = getIntent().getStringExtra("address");
        privateKey = getIntent().getStringExtra("privateKey");
        publicKey = getIntent().getStringExtra("publicKey");

        Log.d("SendActivity", "Sender Address: " + senderAddress);
        Log.d("SendActivity", "Private Key: " + privateKey);
        Log.d("SendActivity", "Public Key: " + publicKey);

        ImageButton scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(SendActivity.this).initiateScan();
            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfigs.getInstance().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendTransaction(senderAddress, privateKey, publicKey);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void sendTransaction(String senderAddress, String privateKey, String publicKey) throws Exception {


        String recipient = recipientInput.getText().toString();
        String amountString = amountInput.getText().toString();

        if (amountString.isEmpty()) {
            Log.e("SendActivity", "Amount field is empty");
            return;
        }
        Date timestamp = new Date(); // текущее время

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String formattedTimestamp = sdf.format(timestamp);

        BigDecimal amount = new BigDecimal(amountString);
        BigDecimal fee = new BigDecimal("1");
        String id = UUID.randomUUID().toString();

        CryptoUtils.TransactionModel transaction = new CryptoUtils.TransactionModel();
        transaction.setId(id);
        transaction.setSender(senderAddress);
        transaction.setReceiver(recipient);
        transaction.setAmount(amount);
        transaction.setTimestamp(formattedTimestamp);
        transaction.setFee(fee);
        transaction.setPublicKey(publicKey);
        transaction.setData("Unconfirmed");
        transaction.setContract(null);
        transaction.setBlockId(null);

        String signature = CryptoUtils.signTransaction(transaction, privateKey);
        transaction.setSignature(signature);


        Log.d("SendActivity", "Request JSON: " + transaction.toString());


        Log.d("SendActivity", "Transaction Details: " +
                "\nID: " + transaction.getId() +
                "\nSender: " + transaction.getSender() +
                "\nReceiver: " + transaction.getReceiver() +
                "\nPublicKey: " + transaction.getPublicKey() +
                "\nSignature: " + transaction.getSignature() +
                "\nTimestamp: " + transaction.getTimestamp() +
                "\nFee: " + transaction.getFee() +
                "\nData: " + transaction.getData() +
                "\nContract: " + transaction.getContract() +
                "\nAmount: " + transaction.getAmount() +
                "\nBlockId: " + transaction.getBlockId());


        Gson gson = new Gson();
        String transactionJson = gson.toJson(transaction);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Transaction", new JSONObject(transactionJson));  // Вставляем сериализованный объект
        } catch (JSONException e) {
            Log.e("SendActivity", "JSON Exception: " + e.getMessage(), e);
        }

        Log.d("SendActivity", "Request JSON: -- " + jsonObject.toString());

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), jsonObject.toString());


        apiService.sendTransaction(requestBody).enqueue(new Callback<ApiResponseTransaction>() {

            @Override
            public void onResponse(Call<ApiResponseTransaction> call, Response<ApiResponseTransaction> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("SendActivity", "Transaction sent: " + response.body().getMessage());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("SendActivity", "Error: " + response.code() + ", Response: " + errorBody);
                    } catch (Exception e) {
                        Log.e("SendActivity", "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseTransaction> call, Throwable t) {
                Log.e("SendActivity", "Request failed", t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scanning canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                EditText addressEditText = findViewById(R.id.editTextText2);
                if (addressEditText != null) {
                    addressEditText.setText(result.getContents());
                } else {
                    Log.e("SendActivity", "EditText with ID 'address' is null");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }







}
