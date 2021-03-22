package com.example.motortextile;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motortextile.Model.Materials;
import com.example.motortextile.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.zip.DataFormatException;

public class MaterialDetailsActivity extends AppCompatActivity
{
    //private FloatingActionButton addToCart;
    private ImageView materialImage;
    private TextView materialName, materialCategory, materialDescription, materialPrice, totalCountMaterial, closeTextBtn, addToCart;
    private ImageButton incrementBtn, decrementBtn;
    private double countMaterial=1;
    private String materialID="", state="";
    private boolean isImageScaled=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_details);

        materialID = getIntent().getStringExtra("mname");

        materialImage = (ImageView) findViewById(R.id.material_image_details);
        materialName = (TextView) findViewById(R.id.material_name_details);
        materialCategory = (TextView) findViewById(R.id.material_category_details);
        materialDescription = (TextView) findViewById(R.id.material_description_details);
        materialPrice = (TextView) findViewById(R.id.material_price_details);
        incrementBtn = (ImageButton) findViewById(R.id.incrementBtn);
        decrementBtn= (ImageButton) findViewById(R.id.decrementBtn);
        totalCountMaterial = (TextView) findViewById(R.id.count_materials);
        closeTextBtn = (TextView) findViewById(R.id.close_details_btn);
        addToCart = (TextView) findViewById(R.id.mt_add_to_cart_btn);

        materialImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!isImageScaled)
                    v.animate().scaleX(2f).scaleY(2f).setDuration(500);
                if(isImageScaled)
                v.animate().scaleX(1f).scaleY(1f).setDuration(500);
                isImageScaled = !isImageScaled;
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(state.equals("Order Placed") || state.equals("Order Shipped"))
                {
                    Toast.makeText(MaterialDetailsActivity.this,"Ви зможете обрати матеріалу для замовлення, після підтвердження попереднього!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    addingToCartList();
                }
            }
        });



        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(countMaterial>=1 && countMaterial<100)
                {
                    countMaterial += 0.5;
                    totalCountMaterial.setText("" + countMaterial);
                }
            }
        });

        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(countMaterial>1 && countMaterial<100) {
                    countMaterial -= 0.5;
                    totalCountMaterial.setText("" + countMaterial);
                }
            }
        });

        
        getProductDetails(materialID);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(Prevalent.currentOnlineUser.getAdminPanel().equals("1"))


        CheckOrderState();
    }


    private void addingToCartList()
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendarForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calendarForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("mid", materialID);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("category", materialCategory.getText().toString());
        cartMap.put("price", materialPrice.getText().toString());
        cartMap.put("mname", materialName.getText().toString());
        cartMap.put("amount", totalCountMaterial.getText().toString());
        cartMap.put("discount", "");

        cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Materials").child(materialID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                .child("Materials").child(materialID)
                                .updateChildren(cartMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(MaterialDetailsActivity.this, "Матеріал додано у корзину...", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(MaterialDetailsActivity.this, HomeActivity.class);
                                            startActivity(intent );
                                        }
                                    }
                                });
                    }
                });

    }


    private void getProductDetails(String materialID)
    {
        DatabaseReference materialsRef= FirebaseDatabase.getInstance().getReference().child("Materials");

        materialsRef.child(materialID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    Materials materials = snapshot.getValue(Materials.class);

                    materialName.setText(materials.getMname());
                    materialCategory.setText(materials.getCategory());
                    materialDescription.setText(materials.getDescription());
                    materialPrice.setText(materials.getPrice());
                    Picasso.get().load(materials.getImage()).into(materialImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckOrderState()
    {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    String shippingState = snapshot.child("state").getValue().toString();

                    if(shippingState.equals("shipped"))
                    {
                        state = "Order Shipped";
                    }
                    else if(shippingState.equals("not shipped"))
                    {
                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

