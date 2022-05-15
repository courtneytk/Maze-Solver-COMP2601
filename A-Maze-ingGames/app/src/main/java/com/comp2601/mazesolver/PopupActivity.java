package com.comp2601.mazesolver;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class PopupActivity extends AppCompatActivity {
    private static Button submitBtn;
    private EditText email;
    private EditText message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        submitBtn = (Button) findViewById(R.id.sendEmail);
        email = (EditText) findViewById(R.id.email);
        message = (EditText) findViewById(R.id.message);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.9));

        submitBtn.setOnClickListener((View v) -> {
            Log.i(TAG, "Send Button Clicked");
            String emailSubject = getResources().getString(R.string.subject);

            if(!email.getText().toString().isEmpty() && !message.getText().toString().isEmpty())
            {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{email.getText().toString()});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,emailSubject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, message.getText().toString());

                emailIntent.setType("message/rfc822");
                if(emailIntent.resolveActivity(getPackageManager()) != null)
                {
                    startActivity(emailIntent);
                }
                else
                {
                    Toast.makeText(PopupActivity.this, "No app on this device can support this request",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}