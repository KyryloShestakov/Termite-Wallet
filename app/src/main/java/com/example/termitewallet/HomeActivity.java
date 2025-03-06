package com.example.termitewallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.termitewallet.ResponseServices.ApiResponseBalance;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    private ApiService apiService;
    private final Handler handler = new Handler();
    private final int delay = 30000;

    @Override
    protected void onStart() {
        super.onStart();
        startRepeatingTask();
    }

    private void startRepeatingTask() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("HomeActivity", "Update");
                String address = getIntent().getStringExtra("address");
                TextView balanceTextView = findViewById(R.id.get_balance);
                getBalance(address, balanceTextView);
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        TextView addressTextView = findViewById(R.id.address);

        Button sendButton = findViewById(R.id.send_button);
        Button getButton = findViewById(R.id.get_button);

        String address = getIntent().getStringExtra("address");
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

                try {
                    Log.d("HomeActivity", "Raw response: " + response.body().toString());
                } catch (Exception e) {
                    Log.e("HomeActivity", "Error logging raw response: " + e.getMessage());
                }


                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeActivity", "Response is successful");

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
                    Log.e("HomeActivity", "Response body: " + response.errorBody());
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
    @Override
    protected void onStop() {
        super.onStop();
        stopRepeatingTask();
    }

    private void stopRepeatingTask() {
        handler.removeCallbacksAndMessages(null);
    }
}
