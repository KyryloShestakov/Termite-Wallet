package com.example.termitewallet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termitewallet.ResponseServices.ApiResponseBalance;
import com.example.termitewallet.ResponseServices.ApiResponseTransactionsByAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    private ApiService apiService;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final int delay = 30;
    private RecyclerView recyclerView;
    private List<CryptoUtils.TransactionModel> allTransactions;
    private MyAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        startRepeatingTask();
    }

    @SuppressLint("DiscouragedApi")
    private void startRepeatingTask() {
        String address = getIntent().getStringExtra("address");
        TextView balanceTextView = findViewById(R.id.get_balance);


        scheduler.scheduleAtFixedRate(() -> {
            Log.d("HomeActivity", "Update");
            getBalance(address, balanceTextView);
        }, 0, delay, TimeUnit.SECONDS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String address = getIntent().getStringExtra("address");

        TextView addressTextView = findViewById(R.id.address);

        Button sendButton = findViewById(R.id.send_button);
        Button getButton = findViewById(R.id.get_button);

        String privateKey = getIntent().getStringExtra("privateKey");
        String publicKey = getIntent().getStringExtra("publicKey");

        Log.d("HomeActivity", "Received Address: " + address);
        Log.d("HomeActivity", "Received Private Key: " + privateKey);
        Log.d("HomeActivity", "Received Public Key: " + publicKey);

        addressTextView.setText(address);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfigs.getInstance().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        Log.d("HomeActivity", "Requesting balance for address: " + address);

        TextView balanceTextView = findViewById(R.id.get_balance);
        getBalance(address, balanceTextView);
        getTransactions(address);

        allTransactions = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(allTransactions, address);
        recyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SendActivity.class);
            intent.putExtra("address", address);
            intent.putExtra("privateKey", privateKey);
            intent.putExtra("publicKey", publicKey);
            startActivity(intent);
        });

        getButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, GetActivity.class);
            intent.putExtra("address", address);
            startActivity(intent);
        });

        ImageView profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            intent.putExtra("address", address);
            intent.putExtra("privateKey", privateKey);
            intent.putExtra("publicKey", publicKey);
            startActivity(intent);
        });


    }

    private void getBalance(String address, TextView balanceTextView) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("address", address);
        } catch (JSONException e) {
            Log.e("HomeActivity", "JSON Exception: " + e.getMessage(), e);
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), jsonObject.toString());

        Call<ApiResponseBalance> call = apiService.getBalance(requestBody);

        call.enqueue(new Callback<ApiResponseBalance>() {
            @Override
            public void onResponse(Call<ApiResponseBalance> call, Response<ApiResponseBalance> response) {
                Log.d("HomeActivity", "onResponse called");
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseBalance apiResponse = response.body();
                    if (apiResponse.getBalance() != null) {
                        String balance = apiResponse.getBalance();
                        Log.i("HomeActivity", "Balance received: " + balance);
                        balanceTextView.setText(balance);
                    } else {
                        Log.e("HomeActivity", "getBalance() returned null");
                    }
                } else {
                    Log.e("HomeActivity", "Failed to get balance: " + response.code() + ", message: " + response.message());
                    Toast.makeText(HomeActivity.this, "Failed to retrieve balance", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseBalance> call, Throwable t) {
                Log.e("HomeActivity", "Error fetching balance: " + t.getMessage(), t);
                Toast.makeText(HomeActivity.this, "Error fetching balance: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTransactions(String address) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("address", address);
        } catch (JSONException e) {
            Log.e("HomeActivity", "JSON Exception: " + e.getMessage(), e);
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), jsonObject.toString());

        Call<ApiResponseTransactionsByAddress> call = apiService.getTransactions(requestBody);

        call.enqueue(new Callback<ApiResponseTransactionsByAddress>() {
            @Override
            public void onResponse(Call<ApiResponseTransactionsByAddress> call, Response<ApiResponseTransactionsByAddress> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseTransactionsByAddress apiResponse = response.body();

                    String jsonResponse = apiResponse.toJson();
                    Log.i("HomeActivity", "Response JSON: " + jsonResponse);

                    if (apiResponse.getTransactions() != null) {
                        List<CryptoUtils.TransactionModel> transactions = apiResponse.getTransactions();
                        Log.i("HomeActivity", "Got transactions" + apiResponse.getMessage());

                        allTransactions.clear();
                        allTransactions.addAll(transactions);
                        runOnUiThread(() -> {
                            recyclerView.getAdapter().notifyDataSetChanged();
                        });  // Notify the adapter that the data has changed
                    } else {
                        Log.e("HomeActivity", "getTransactions() returned null");
                    }
                } else {
                    Log.e("HomeActivity", "Failed to get transactions: " + response.code() + ", message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseTransactionsByAddress> call, Throwable t) {
                Log.e("HomeActivity", "Error fetching transactions: " + t.getMessage(), t);
            }
        });

    }
}
