package com.ahyoxsoft.restauranthub.main;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahyoxsoft.restauranthub.R;
import com.ahyoxsoft.restauranthub.utility.AppConstants;
import com.ahyoxsoft.restauranthub.utility.ConnectionURL;
import com.ahyoxsoft.restauranthub.utility.RequestSingleton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderSuccessPage extends AppCompatActivity {
    private ImageView qrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success_page);

        qrCode = (ImageView) findViewById(R.id.qr_code);

        String foods = getIntent().getStringExtra("FOODS");
        String transactionID = getIntent().getStringExtra("TRANSACTION_ID");
        Double price = getIntent().getDoubleExtra("TOTAL_PRICE", 0);
        String name = getIntent().getStringExtra(AppConstants.RESTAURANT_NAME);

        TextView msg = (TextView) findViewById(R.id.restaurant_name_msg);
        msg.setText("Order received by "+name);

        HashMap<String, String> param = new HashMap<>();
        param.put("totalPrice", String.valueOf(price));
        param.put("transactionID", transactionID);
        param.put("foods", foods);

        String url = RequestSingleton.getEncodeURL(ConnectionURL.TRANSACTION_URL, param);

        try {
            qrCode.setImageBitmap(encodeToBitmap(url));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main);
                Bitmap b = Bitmap.createBitmap(mainLayout.getWidth(), mainLayout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(b);
                mainLayout.draw(canvas);

                ContentValues contentValues = new ContentValues(3);
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "Draw On Me");
                Uri imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                try {
                    OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
                    b.compress(Bitmap.CompressFormat.JPEG, 90, imageFileOS);
                    Toast t = Toast.makeText(OrderSuccessPage.this, "Thank you! Success Page Saved!", Toast.LENGTH_LONG);
                    t.show();
                } catch (Exception e) {
                    Log.e("EXCEPTION", e.getMessage());
                }
            }
        });
    }

    private Bitmap encodeToBitmap(String content) throws WriterException {
        BitMatrix result = null;

        try {
            result = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 200, 200,  null);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 200, 0, 0, w, h);
        return bitmap;
    }
}
