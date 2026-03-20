package com.termite.wallet.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.termitewallet.R;
import com.termite.wallet.AppConfigs;
import com.termite.wallet.CryptoUtils;
import com.termite.wallet.ResponseServices.ApiResponseTransaction;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.termite.wallet.network.ApiService;

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
    private EditText recipientInput, amountInput, feeInput;
    private Button confirmButton;
    private String senderAddress;
    private String privateKey;
    private String publicKey;
    private String commissionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.addProvider(new BouncyCastleProvider());
        setContentView(R.layout.activity_send);

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> onBackPressed());
        recipientInput = findViewById(R.id.editTextText2);
        amountInput = findViewById(R.id.transfer_amount);
        feeInput = findViewById(R.id.commission);
        commissionText = feeInput.getText().toString().trim();
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
                    recipientInput.setText("");
                    amountInput.setText("");
                    feeInput.setText("");
                    new AlertDialog.Builder(SendActivity.this)
                            .setTitle("Success")
                            .setMessage("Transaction sent!")
                            .setPositiveButton("Ok", null)
                            .show();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void sendTransaction(String senderAddress, String privateKey, String publicKey) throws Exception {
        String recipient = recipientInput.getText().toString().trim();
        String amountString = amountInput.getText().toString().trim();
        String commissionString = feeInput.getText().toString().trim();

        if (amountString.isEmpty()) {
            Log.e("SendActivity", "Amount field is empty");
            Toast.makeText(this, "Введите сумму", Toast.LENGTH_SHORT).show();
            return;
        }

        if (commissionString.isEmpty()) {
            Log.e("SendActivity", "Commission field is empty");
            Toast.makeText(this, "Введите комиссию", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal amount;
        BigDecimal fee;

        try {
            amount = new BigDecimal(amountString);
            fee = new BigDecimal(commissionString);
        } catch (NumberFormatException e) {
            Log.e("SendActivity", "Неверный формат суммы или комиссии", e);
            Toast.makeText(this, "Неверный формат суммы или комиссии", Toast.LENGTH_SHORT).show();
            return;
        }

        Date timestamp = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String formattedTimestamp = sdf.format(timestamp);
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

        Gson gson = new Gson();
        String transactionJson = gson.toJson(transaction);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Transaction", new JSONObject(transactionJson));
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
