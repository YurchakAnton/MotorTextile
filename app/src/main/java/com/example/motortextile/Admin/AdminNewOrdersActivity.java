package com.example.motortextile.Admin;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motortextile.MailSend.JavaMailAPI;
import com.example.motortextile.MailSend.MailConstants;
import com.example.motortextile.Model.AdminOrders;
import com.example.motortextile.Model.Cart;
import com.example.motortextile.Prevalent.Prevalent;
import com.example.motortextile.R;
import com.example.motortextile.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AdminNewOrdersActivity extends AppCompatActivity
{
    public static Context context;
    private RecyclerView ordersList;
    private DatabaseReference ordersRef, cartOrdersRef, cartListRef;
    private StorageReference pdfDocRef;
    private Bitmap bmp,scalebmp;
    ArrayList<Cart> materials;
    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);
        AdminNewOrdersActivity.context = getBaseContext();
        pdfDocRef = FirebaseStorage.getInstance().getReference().child("Documents");
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        cartOrdersRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View");
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.mt_logo);
        scalebmp = Bitmap.createScaledBitmap(bmp, 700,190,false);

        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef, AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder adminOrdersViewHolder, int i, @NonNull AdminOrders adminOrders)
                    {
                        adminOrdersViewHolder.userName.setText("Замовник: "+adminOrders.getName());
                        adminOrdersViewHolder.userPhone.setText("Телефон: "+adminOrders.getPhone());
                        adminOrdersViewHolder.userTotalPrice.setText("Сума: "+adminOrders.getTotalPrice()+"€");
                        adminOrdersViewHolder.userDateTime.setText("Дата: "+adminOrders.getDate()+" | "+ adminOrders.getTime());
                        adminOrdersViewHolder.userOrderCity.setText("Адреса: "+adminOrders.getCity()+" | НП №: "+adminOrders.getDepartment());
                        adminOrdersViewHolder.userPay.setText("Оплата: "+adminOrders.getPay());
                        adminOrdersViewHolder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                String uID=getRef(i).getKey();
                                Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserMaterialsActivity.class);
                                intent.putExtra("uid", uID);
                                startActivity(intent);
                            }
                        });

                        adminOrdersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Так",
                                                "Ні"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Підтверджую відправку замовлення:");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if(which == 0)
                                        {
                                            String uID = getRef(i).getKey();

                                            materials = new ArrayList<>();

                                            cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
                                                    .child("Admin View").child("3").child("Materials");

                                            listener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                        Cart post = child.getValue(Cart.class);
                                                        materials.add(post);
                                                    }
                                                    createPDF(adminOrders.getName(), adminOrders.getPhone(), adminOrders.getCity(), adminOrders.getDate()+" "+adminOrders.getTime(), adminOrders.getMid(), adminOrders.getTotalPrice());
                                                    sendEmail(adminOrders.getEmail(), adminOrders.getName(), adminOrders.getPhone(), adminOrders.getCity(), adminOrders.getDepartment(), adminOrders.getPay(), adminOrders.getMid());
                                                    RemoveOrderFromTable(uID);

                                                    cartListRef.removeEventListener(listener);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            };
                                            cartListRef.addValueEventListener(listener);

                                            finish();
                                        }
                                        else
                                        {
                                            dialog.cancel();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return new AdminOrdersViewHolder(view);
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userName, userPhone, userTotalPrice, userDateTime, userOrderCity, userPay;
        public Button ShowOrdersBtn;

        public AdminOrdersViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userName = itemView.findViewById(R.id.order_user_name);
            userPhone = itemView.findViewById(R.id.order_phone_number);
            userOrderCity = itemView.findViewById(R.id.order_city_department);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userPay = itemView.findViewById(R.id.order_pay);
            ShowOrdersBtn = itemView.findViewById(R.id.show_all_materials);
        }
    }

    private void RemoveOrderFromTable(String uID)
    {
        Log.v("REMOVE", "removeorder");
        ordersRef.child(uID).removeValue();
        cartOrdersRef.child(uID).removeValue();

        Toast.makeText(AdminNewOrdersActivity.this,"Перегляньте наявність нових замовлень", Toast.LENGTH_LONG).show();
    }


    private void sendEmail(String email, String userName, String userPhone, String userCity, String userDepartment, String userVarPay, String mid)
    {
        String mSubject = "Оформлення замовлення "+"'Motor Textile'";


        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email, mSubject, userName, userPhone, userCity, userDepartment, userVarPay, mid);

        javaMailAPI.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    private void createPDF(String name,String phone, String city, String date, String mid, String total)
    {
        Integer pageWidth=2000, pageHeight=2500;
        PdfDocument pdfDocument = new PdfDocument();

        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth,pageHeight,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        canvas.drawColor(Color.rgb(0,0,0));
        canvas.drawBitmap(scalebmp, pageWidth-1400,pageHeight-2400,paint);
        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(90);
        canvas.drawText("Накладна", pageWidth/2,pageHeight-2100,titlePaint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(70f);
        paint.setColor(Color.WHITE);
        canvas.drawText("Прізвище | Ім'я: "+name,pageWidth/2,pageHeight-2000,paint);
        canvas.drawText("Номер телефону: "+phone,pageWidth/2,pageHeight-1900,paint);
        canvas.drawText("Місто: "+city,pageWidth/2,pageHeight-1800,paint);
        canvas.drawText("Дата: "+date,pageWidth/2,pageHeight-1700,paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        int top = 900;
        int bottom = 1000;
        int text_line = 970;

        int left = 50;
        int right = 1950;

        int col_1 = 780;
        int col_2 = 1230;
        int col_3 = 1580;

        int text_1 = 70;
        int text_2 = 820;
        int text_3 = 1270;
        int text_4 = 1600;

        int count = materials.size();

        canvas.drawRect(left,top,right,bottom, paint);

        for(int i = 1; i <= count+1; i++) {
            canvas.drawRect(left, top + i*100, right, bottom + i*100, paint);
        }

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawText("Матеріал", 280, text_line,paint);
        canvas.drawText("Метраж (м)", text_2, text_line,paint);
        canvas.drawText("Ціна (€)", text_3, text_line,paint);
        canvas.drawText("Всього (€)", text_4, text_line,paint);
        canvas.drawLine(col_1, top, col_1, bottom, paint);
        canvas.drawLine(col_2, top, col_2, bottom, paint);
        canvas.drawLine(col_3, top, col_3, bottom, paint);

       for(int i = 0; i < count; i++){
           top +=100;
           bottom +=100;
           text_line +=100;
           /* set text */
               canvas.drawText(materials.get(i).getMname(), text_1, text_line, paint);
               canvas.drawText(materials.get(i).getAmount(), text_2, text_line, paint);
               canvas.drawText(materials.get(i).getPrice(), text_3, text_line, paint);
               canvas.drawText(String.valueOf(Double.valueOf(materials.get(i).getPrice())*Double.valueOf(materials.get(i).getAmount())), text_4, text_line, paint);
               canvas.drawLine(col_1, top, col_1, bottom, paint);
               canvas.drawLine(col_2, top, col_2, bottom, paint);
               canvas.drawLine(col_3, top, col_3, bottom, paint);
       }
        canvas.drawText("Cума: "+ total+" €", 1300, text_line+100,paint);

        pdfDocument.finishPage(page);

        File file = new File(this.getExternalFilesDir("/"), mid+".pdf");
        try
        {
        pdfDocument.writeTo(new FileOutputStream(file));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        pdfDocument.close();
    }
}