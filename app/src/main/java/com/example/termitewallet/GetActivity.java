package com.example.termitewallet;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GetActivity extends AppCompatActivity {
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get);

        address = getIntent().getStringExtra("address");

        TextView addressTextView = findViewById(R.id.address_view);
        addressTextView.setText(address);

        ImageView qrImageView = findViewById(R.id.qrImageView);
        generateQRCode(address, qrImageView);

        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void generateQRCode(String text, ImageView imageView) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
