package com.example.motortextile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.motortextile.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class ResetPasswordActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private EditText phoneReset;
    private Button resetBtn;
    private Random rnd = new Random();
    private int key = rnd.nextInt(900000)+100000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        phoneReset = findViewById(R.id.phone_reset);
        resetBtn = findViewById(R.id.reset_btn);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("Motor Textile", "Motor Textile", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String phone = phoneReset.getText().toString();

                if (TextUtils.isEmpty(phone))
                {
                    Toast.makeText(ResetPasswordActivity.this, "Введіть номер телефону користувача", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                    DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
                    mDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child("Users").child(phone).exists())
                            {
                                Users usersData = snapshot.child("Users").child(phone).getValue(Users.class);

                                if (usersData.getPhone().equals(phone))
                                {
                                    Notification notification = new NotificationCompat.Builder(ResetPasswordActivity.this, "Motor Textile")
                                            .setSmallIcon(R.drawable.camera).setContentTitle("Motor Textile").setContentText("Your cod: "+key)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true).build();

                                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ResetPasswordActivity.this);
                                    managerCompat.notify(1,notification);
                                }
                            }
                            else
                            {
                                Toast.makeText(ResetPasswordActivity.this,"Акаунт з цим номером ("+phone+") не знайдено...",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
});
    }
}