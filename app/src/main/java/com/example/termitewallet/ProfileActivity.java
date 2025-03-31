package com.example.termitewallet;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView addressView = findViewById(R.id.profile_address);
        TextView publicKeyView = findViewById(R.id.profile_publicKey);
        TextView privateKeyView = findViewById(R.id.profile_privateKey);
        Button backButton = findViewById(R.id.back_button);

        ImageButton copyAddressButton = findViewById(R.id.copy_address);
        ImageButton copyPublicKeyButton = findViewById(R.id.copy_publicKey);
        ImageButton copyPrivateKeyButton = findViewById(R.id.copy_privateKey);

        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        String publicKey = intent.getStringExtra("publicKey");
        String privateKey = intent.getStringExtra("privateKey");

        addressView.setText("Address: " + address);
        publicKeyView.setText("Public Key: " + publicKey);
        privateKeyView.setText("Private Key: " + privateKey);

        backButton.setOnClickListener(v -> finish());

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        copyAddressButton.setOnClickListener(v -> {
            clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Address", address));
            Toast.makeText(ProfileActivity.this, "Address copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        copyPublicKeyButton.setOnClickListener(v -> {
            clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Public Key", publicKey));
            Toast.makeText(ProfileActivity.this, "Public Key copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        copyPrivateKeyButton.setOnClickListener(v -> {
            clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Private Key", privateKey));
            Toast.makeText(ProfileActivity.this, "Private Key copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }
}
