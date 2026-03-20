package com.termite.wallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.termitewallet.R;
import com.termite.wallet.network.ApiService;
import com.termite.wallet.AppConfigs;
import com.termite.wallet.ResponseServices.ApiResponseAddress;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String address;
    private ApiService apiService;
    private String privateKey;
    private String publicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfigs.getInstance().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        ImageButton button = findViewById(R.id.button_create_wallet);
        ImageButton buttonImport = findViewById(R.id.button_import_wallet);

        button.setOnClickListener(v -> {
            Log.d(TAG, "Sending a GET request to the server...");

            Call<ApiResponseAddress> call = apiService.getAddress();

            call.enqueue(new Callback<ApiResponseAddress>() {
                @Override
                public void onResponse(Call<ApiResponseAddress> call, Response<ApiResponseAddress> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponseAddress apiResponse = response.body();

                        Log.i(TAG, "Successful response from the server: ");
                        Log.i(TAG, "Address: " + apiResponse.getData().getAddress());
                        Log.i(TAG, "Private key: " + apiResponse.getData().getPrivateKey());
                        Log.i(TAG, "Public key: " + apiResponse.getData().getPublicKey());

                        privateKey = apiResponse.getData().getPrivateKey();
                        publicKey = apiResponse.getData().getPublicKey();
                        address = apiResponse.getData().getAddress();
                        String passphrase = "password";

                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("address", apiResponse.getData().getAddress());
                        intent.putExtra("privateKey", apiResponse.getData().getPrivateKey());
                        intent.putExtra("publicKey", apiResponse.getData().getPublicKey());
                        startActivity(intent);
                    } else {
                        Log.e(TAG, "Response failed.: " + response.code() + ", message: " + response.message());
                        Toast.makeText(MainActivity.this, "Failed response from server", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onFailure(Call<ApiResponseAddress> call, Throwable t) {
                    Log.e(TAG, "failed site: " + t.getMessage(), t);
                    Toast.makeText(MainActivity.this, "Failed site: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonImport.setOnClickListener(v -> {
            Log.d(TAG, "Import of wallet");

             Intent intent = new Intent(MainActivity.this, ImportActivity.class);

    startActivity(intent);


        });

    }
}
