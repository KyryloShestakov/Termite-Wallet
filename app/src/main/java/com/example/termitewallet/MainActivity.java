package com.example.termitewallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.termitewallet.ResponseServices.ApiResponseAddress;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String address;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfigs.getInstance().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        Button button = findViewById(R.id.button_create_wallet);
        Button buttonImport = findViewById(R.id.button_import_wallet);

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

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("address", "2dVjmyEQHhfGe6pGmAYVJzM4WSwXsDJ8da6nCPMH3aS63xqeR1");
            intent.putExtra("privateKey", "MIIEowIBAAKCAQEA2oJViwlza+LwRZRHBpi2m8riZnLHxidQyxp6KlLsuGKEtpAjCisBa5hsZqdbsCXsqvDa2kHJ1yuybn9wSX7o/bENHgkTNqbivW3uW9J0jaYrGwBjnZKaxM0+87uM2X9ylacOJqmdmTogfvy52sD2O7qcwmPR/aBUmn5kWHV8xyv5+roYtniKN8XMu9GCADU2O4YCMEc3OjytwCADhHBf37Si3uFJI2hVhQ/D+hbhtSou0mz7t/AERRhz9uxAmh5HWkxeC+h3LwVfDRzEHCmC94tPjIk/jRdhCBly6ojW6jLuvb0VbrBF+rC7b7Tiz+EdQWPuAF8Rx7H59F8udjIoeQIDAQABAoIBAEHOSKbjxL/0C3pY6n8BFNWcZ0yFOsbhZkKUicAfUy1Adklo5TSVuQrHT7YmpsVI1pqwiKh8caX8gfMnLBDmSJLGdHXcM84cumJkLgm3OuTB629pUZbN5RBp45CRphyGdmK9edSI9m6EA+9aezp3LHDWhYlBeKdSVshkUiIV1+s/M7n7x5K7YfbEpEwD9SIM2mlZ3fPL5lW7/9SW7RQUwvB1BJUPzNaOBvuEejLSKhAHIRPOHyJtMSxuBRc0crmwC8nHu53K40xqT559m3hhFnlSf3E3+7dgHZbuX+Mn4Br6x94P6QJ5fDZxtK7ZEezxr5Ekxo7C2KFM5n9jpP0g5tUCgYEA8wcpUvEY81RK4TtWEwk4VwTHwD51yEpLvBMqLywM4Q4rfaR5yY9Tltr7pPYzQ0T7LeaObeQD4cMBKDZtEFDZ9cWg06m768yejmr1GZ74FLu++biQ9hKcBKp3CPXbuLsxYlXVeJSZGiwQKMZ3OVv/fBbxB8R6p55l0aVw5cwZlB8CgYEA5iwi9N+ZtuXig9Umk/7jFJkYaXu1x0WEQaGqg4M7hRSYFkWlEid3oBlBnNNmhPLVZs5VMlA6YOAjVrwvAWiomdQ7Z6eMCFa/A6zW+gMAzk0dP6o88OkpcG48uh60NZw+4BFrfxerF2sXmiNPXqNlx615FxZMXmHUTJH8nIWmcGcCgYEAtI/5j/o5g3B5QeaICRSsm1qd3qOJdwpcQpwql2mvF/cjMPJm2e53OHnyxZl2dOBnjq7h25uxlqb/0RS+64RmrEftOdW+HW+lzkKBYEvQHfdv6LHMYkdpS74rOqkQWqwaAOms9S3SorNiPilWo3NK2rNg2ViNmMQC+kRlIRcHWhkCgYBprrxCKOSAKN8vXs/+xdpn279st2OIYm/OXrdW+Qt8GC+YklOvmTWA6ffJVPtOcdzv/i76EgIr9t3i9jay8+Xt85dvCWvvbDz6KALw+i4Fizyxva2Lg9MGD2eMoYpmzx3Dbdwol6pz7zUADdRkhKu32fuuUQuxjkOXALS2/VqlYwKBgGn5ZELe6GdR4K52dKq0VA43ybd68pjnfWaVBJanQzqHwC6+3aOgR89kPiPj01kBJvgpfHEyfw2KiSxIu9o48gJfbnVXYQ5yKtBNlstXg36QQirYh6i62mynvFO+gPi7u3uY3mWPQWYeVQ59oFHO4SLvDQjBwzgYfrZuUYD9c3RQ");
            intent.putExtra("publicKey", "MIIBCgKCAQEA2oJViwlza+LwRZRHBpi2m8riZnLHxidQyxp6KlLsuGKEtpAjCisBa5hsZqdbsCXsqvDa2kHJ1yuybn9wSX7o/bENHgkTNqbivW3uW9J0jaYrGwBjnZKaxM0+87uM2X9ylacOJqmdmTogfvy52sD2O7qcwmPR/aBUmn5kWHV8xyv5+roYtniKN8XMu9GCADU2O4YCMEc3OjytwCADhHBf37Si3uFJI2hVhQ/D+hbhtSou0mz7t/AERRhz9uxAmh5HWkxeC+h3LwVfDRzEHCmC94tPjIk/jRdhCBly6ojW6jLuvb0VbrBF+rC7b7Tiz+EdQWPuAF8Rx7H59F8udjIoeQIDAQAB");
            startActivity(intent);

        });

    }
}
