package com.example.motortextile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motortextile.Model.Materials;
import com.example.motortextile.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity
{
    private EditText nameEditText, emailEditText, cityEditText, departmentEditText, phoneEditText;
    private Button confirmOrderBtn;
    private TextView txtTotalSum;
    private String totalPrice="", var_pay="";
    private RadioGroup checkPay;
    private static long back_pressed;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalPrice= getIntent().getStringExtra("Total Price");

        nameEditText = (EditText) findViewById(R.id.order_name);
        emailEditText = (EditText) findViewById(R.id.order_email);
        cityEditText = (EditText) findViewById(R.id.order_city);
        departmentEditText = (EditText) findViewById(R.id.order_department);
        phoneEditText = (EditText) findViewById(R.id.order_phone);
        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_button);
        txtTotalSum = (TextView) findViewById(R.id.total_sum);
        checkPay = (RadioGroup) findViewById(R.id.check_pay);
        checkPay.clearCheck();

        checkPay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.pay_delivery:
                        var_pay = "Оплата при отриманні";
                        break;
                    case R.id.pay_card:
                        var_pay = "Оплата картою (реквізити)";
                        break;
                    case R.id.pay_store:
                        var_pay = "Оплата при самовивозі";
                        break;
                }
            }
        });

        txtTotalSum.setText("Сума замовлення: "+String.valueOf(totalPrice)+"€");

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Check();
            }
        });

    }

    private void Check()
    {
        if(TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(this, "Введіть прізвище та ім'я!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cityEditText.getText().toString()))
        {
            Toast.makeText(this, "Введіть місто одержувача!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(departmentEditText.getText().toString()))
        {
            Toast.makeText(this, "Введіть відділення 'Нової пошти' одержувача!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Введіть телефон одержувача!",Toast.LENGTH_SHORT).show();
        }
        else if (var_pay.equals(""))
        {
            Toast.makeText(this, "Виберіть спосіб оплати!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder()
    {
        final String saveCurrentDate, saveCurrentTime;
        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd.MM.yyyy");
        saveCurrentDate = currentDate.format(calendarForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendarForDate.getTime());
        String materialRandomKey = saveCurrentDate +"qqq"+ saveCurrentTime;
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalPrice", totalPrice);
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("email", emailEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("city", cityEditText.getText().toString());
        ordersMap.put("department", departmentEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "not shipped");
        ordersMap.put("pay", var_pay);
        ordersMap.put("mid", materialRandomKey+"zzz"+nameEditText.getText().toString());

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Log.v("REMOVE", "1");
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View").child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Очікуйте підтвердження замовлення від менеджера...", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent );
                                        finish();
                                    }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finish();
        }
        else
        {
            Toast.makeText(this , "Натисніть двічі для виходу!", Toast.LENGTH_LONG).show();
        }
        back_pressed = System.currentTimeMillis();

    }
}