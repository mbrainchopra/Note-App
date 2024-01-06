package com.example.notes;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddnoteActivity extends AppCompatActivity {
    private ImageButton backButton, selectpic;
    private EditText Edit_tittle, Edit_description;
    private View saveButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);
        saveButton = findViewById(R.id.saveButton);
        Edit_tittle = findViewById(R.id.edit_tittle);
        Edit_description = findViewById(R.id.edit_desc);
        backButton = findViewById(R.id.backButton);
        selectpic = findViewById(R.id.attachmentButton);
        Edit_description.setMovementMethod(LinkMovementMethod.getInstance());
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String noteTitle = Edit_tittle.getText().toString().trim();
                String noteDescription = Edit_description.getText().toString().trim();


                if (TextUtils.isEmpty(noteTitle)) {

                    Toast.makeText(getApplicationContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(noteDescription)) {

                    Toast.makeText(getApplicationContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(AddnoteActivity.this, "Saved! just for testing", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        selectpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkReadExternalStoragePermission()) {

                    startImagePicker();
                } else {

                    requestReadExternalStoragePermission();
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("title") && intent.hasExtra("body")) {

            String title = intent.getStringExtra("title");
            String body = intent.getStringExtra("body");


            Edit_tittle.setText(title);
            Edit_description.setText(body);


        }

        Linkify.addLinks(Edit_description, Linkify.ALL);
        Edit_description.setMovementMethod(LinkMovementMethod.getInstance());
        Edit_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = Edit_description.getText().toString();

            }
        });
    }

    private boolean checkReadExternalStoragePermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_READ_EXTERNAL_STORAGE);
    }

    private void startImagePicker() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            try {

                int cursorPosition = Edit_description.getSelectionStart();


                if (cursorPosition >= 0) {

                    Bitmap compressedBitmap = compressImage(selectedImageUri);


                    int fixedWidth = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
                    int fixedHeight = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());


                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), compressedBitmap);


                    bitmapDrawable.setBounds(0, 0, fixedWidth, fixedHeight);


                    Edit_description.getText().insert(cursorPosition, " ");
                    Edit_description.getText().setSpan(new ImageSpan(bitmapDrawable),
                            cursorPosition, cursorPosition + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                } else {
                    Toast.makeText(this, "Invalid cursor position", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to compress image", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private Bitmap compressImage(Uri imageUri) throws IOException {
        InputStream imageStream = getContentResolver().openInputStream(imageUri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);

        int quality = 75;
        int maxSize = 1024;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

        while (byteArrayOutputStream.toByteArray().length / 1024 > maxSize) {
            byteArrayOutputStream.reset();
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            quality -= 5;
        }

        File compressedFile = new File(getCacheDir(), "compressed_image.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(compressedFile);
        fileOutputStream.write(byteArrayOutputStream.toByteArray());
        fileOutputStream.flush();
        fileOutputStream.close();

        return BitmapFactory.decodeFile(compressedFile.getAbsolutePath());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startImagePicker();
            } else {

                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
