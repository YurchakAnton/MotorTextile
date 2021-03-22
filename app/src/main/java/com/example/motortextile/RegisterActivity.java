package com.example.motortextile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private Button CreateAccountBotton;
    private EditText InputName, InputEmail, InputCity, InputPhone, InputPassword;
    private ProgressDialog loadingBar;
    private DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountBotton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_name_input);
        InputEmail = (EditText) findViewById(R.id.register_email_input);
        InputCity = (EditText) findViewById(R.id.register_city_input);
        InputPhone = (EditText) findViewById(R.id.register_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);

        CreateAccountBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()
    {
        String name = InputName.getText().toString();
        String email = InputEmail.getText().toString();
        String city = InputCity.getText().toString();
        String phone = InputPhone.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"Введіть ім'я...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Введіть ел. адресу...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(city))
        {
            Toast.makeText(this,"Введіть місто проживання...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"Введіть номер телефону...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Введіть пароль...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Створення акаунту");
            loadingBar.setMessage("Зачекайте, відбувається обробка даних...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(name, email, city, phone, password);
        }
    }

    private void ValidatePhoneNumber(String name, String email, String city, String phone, String password)
    {
        mDataBase = FirebaseDatabase.getInstance().getReference();

        mDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot)
            {
                if (!(datasnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("name", name);
                    userdataMap.put("email", email);
                    userdataMap.put("city", city);
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("adminpanel","0");

                    mDataBase.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this,"Вітаємо! Акаунт створенно...",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,"Помилка: Попробуйте знову через декілька хвилин...",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"Цей номер телефону ("+phone+") вже використовується",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Будь ласка, використайте новий номер телефону",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }
}