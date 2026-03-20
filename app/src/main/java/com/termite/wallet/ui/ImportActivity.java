package com.termite.wallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.termitewallet.R;
import com.termite.wallet.crypto.RsaAddressVerifier;

public class ImportActivity extends AppCompatActivity {

    private EditText publicKeyEditText;
    private EditText privateKeyEditText;
    private EditText addressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        publicKeyEditText = findViewById(R.id.public_key);
        privateKeyEditText = findViewById(R.id.private_key);
        addressEditText = findViewById(R.id.address);

        Button buttonImport = findViewById(R.id.import_button);

        buttonImport.setOnClickListener(v -> getDataFromFields());
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void getDataFromFields() {
        String publicKey = publicKeyEditText.getText().toString().trim();
        String privateKey = privateKeyEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();



        Log.d("ImportActivity", "PrivateKey: " + privateKey);
        Log.d("ImportActivity", "Address: " + address);

        var valid = RsaAddressVerifier.isValid(privateKey, address);

        Log.d("ImportActivity", "Validation result: " + valid);
        if (!valid)
        {
            Toast.makeText(this, "Data not valid", Toast.LENGTH_SHORT).show();
            return;
        }
        if (publicKey.isEmpty() || privateKey.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(ImportActivity.this, HomeActivity.class);
        intent.putExtra("address", address);
        intent.putExtra("privateKey", privateKey);
        intent.putExtra("publicKey", publicKey);
        startActivity(intent);
    }
}
