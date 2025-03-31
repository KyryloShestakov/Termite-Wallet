package com.example.termitewallet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ImportActivity extends AppCompatActivity {

    private EditText publicKeyEditText;
    private EditText privateKeyEditText;
    private EditText addressEditText;
    private Button buttonImport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        publicKeyEditText = findViewById(R.id.public_key);
        privateKeyEditText = findViewById(R.id.private_key);
        addressEditText = findViewById(R.id.address);
        buttonImport = findViewById(R.id.import_button);

        buttonImport.setOnClickListener(v -> getDataFromFields());
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void getDataFromFields() {
        String publicKey = publicKeyEditText.getText().toString().trim();
        String privateKey = privateKeyEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

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
