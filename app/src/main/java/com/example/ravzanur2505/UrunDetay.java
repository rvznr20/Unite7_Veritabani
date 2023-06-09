package com.example.ravzanur2505;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.OneShotPreDrawListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class UrunDetay  extends AppCompatActivity {
    SQLiteDatabase database;

    ImageView urunResim;
    TextView textViewUrunAdi, textViewUrunFiyat, textViewUrunAdet;
    Button btnResimEkle, btnDegistir, btnSil, btnGeri;

    int id;
    ActivityResultLauncher<Intent> galleryLauncher;
    ActivityResultLauncher<String> galleryPermission;
    Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uyg5_urun_detay);

        tanimlamalar();

        Intent i = getIntent();
        id = i.getIntExtra("id", 0);
        try {
            database = this.openOrCreateDatabase("Urun", MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT * FROM urunler WHERE id=?", new String[]{String.valueOf(id)});

            int kolonUrunAdi = cursor.getColumnIndex("urunadi");
            int kolonFiyat = cursor.getColumnIndex("fiyat");
            int kolonAdet = cursor.getColumnIndex("adet");
            int kolonResim = cursor.getColumnIndex("resim");
            while (cursor.moveToNext()) {
                textViewUrunAdi.setText(cursor.getString(kolonUrunAdi));
                textViewUrunFiyat.setText(cursor.getString(kolonFiyat) + "");
                textViewUrunAdet.setText(cursor.getString(kolonAdet));
                byte[] bytes = cursor.getBlob(kolonResim);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                urunResim.setImageBitmap(bitmap);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("SERVİS", "onCreate" + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE));
        registerLauncher();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "İzin Gerekli", Toast.LENGTH_SHORT).show();
            } else {
                galleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        btnResimEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(i);
            }
        });

        btnDegistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), UrunKayit.class);
                i.putExtra("mod", "degistir");
                i.putExtra("id", id);
                startActivity(i);
                finish();
            }
        });

        btnSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String SORGU = "DELETE FROM urunler WHERE id=?";
                    SQLiteStatement result = database.compileStatement(SORGU);
                    result.bindLong(1, id);
                    result.execute();
                    Intent i = new Intent(UrunDetay.this, Uyg3.class);
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        btnGeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UrunDetay.this, Uyg3.class);
                startActivity(i);
                finish();
            }
        });
    }

     public Bitmap resimKucultucu(Bitmap b, int buyukluk) {
         double oran = b.getWidth() / b.getHeight();
         double uzunluk = buyukluk / oran;
         return bitmap.createScaledBitmap(bitmap, buyukluk, (int) uzunluk, true);
     }

     public  void  Kaydet() {
         Bitmap kucukResim = resimKucultucu(bitmap, 250);
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         kucukResim.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
         byte[] bytes = byteArrayOutputStream.toByteArray();
         try {
             ContentValues contentValues = new ContentValues();
             contentValues.put("resim", bytes);
             database.update("urunler", contentValues, "id = " + id, null);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

     private  void  registerLauncher() {
         galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
             @Override
             public void onActivityResult(ActivityResult result) {
                 if (result.getResultCode() == RESULT_OK) {
                     Intent i = result.getData();
                     if (i != null) {
                         Uri galleryUri = i.getData();
                         try {
                             if (Build.VERSION.SDK_INT >= 28) {
                                 ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), galleryUri);
                                 bitmap = ImageDecoder.decodeBitmap(source);
                                 urunResim.setImageBitmap(bitmap);
                                 Kaydet();
                             } else {
                                 bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryUri);
                                 urunResim.setImageBitmap(bitmap);
                                 Kaydet();
                             }
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                 }
             }
         });

         galleryPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
             @Override
             public void onActivityResult(Boolean result) {
                 if (result) {
                     Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                     galleryLauncher.launch(i);
                 } else {
                     Toast.makeText(UrunDetay.this, "izin vermeniz gerekli", Toast.LENGTH_LONG).show();
                 }
             }
         });
     }

     private  void  tanimlamalar() {
         urunResim = findViewById(R.id.urunResim);
         textViewUrunAdi = findViewById(R.id.textView10);
         textViewUrunFiyat = findViewById(R.id.textView11);
         textViewUrunAdet = findViewById(R.id.textView9);
         btnDegistir = findViewById(R.id.btnDegistir);
         btnSil = findViewById(R.id.btnSil);
         btnGeri = findViewById(R.id.btnGeri);
    }
}