package com.example.motortextile.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motortextile.HomeActivity;
import com.example.motortextile.MainActivity;
import com.example.motortextile.Model.Materials;
import com.example.motortextile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainMaterialsActivity extends AppCompatActivity
{
    private Button applyChangeBtn, deleteMaterialBtn;
    private EditText materialName, materialDescritiption, materialPrice;
    private TextView materialCategory;
    private ImageView materialImageView;
    private String materialID="";
    private DatabaseReference materialsRef;

    public static final String[] materialCategories =
            {
                    "Шкіра",
                    "Шкірзам",
                    "Алькантара",
                    "Динаміка Міко",
                    "Екошкіра",
                    "Велюр",
                    "Каучук",
                    "Ковролін",
                    "Поролон",
                    "Тканина"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_materials);

        materialID = getIntent().getStringExtra("mname");
        materialsRef = FirebaseDatabase.getInstance().getReference().child("Materials").child(materialID);


        applyChangeBtn = findViewById(R.id.apply_changes_btn);
        deleteMaterialBtn = findViewById(R.id.delete_material_btn);
        materialName = findViewById(R.id.material_name_maintain);
        materialDescritiption = findViewById(R.id.material_description_maintain);
        materialPrice = findViewById(R.id.material_price_maintain);
        materialCategory = findViewById(R.id.material_category_maintain);
        materialImageView = findViewById(R.id.material_image_maintain);

        displaySpecificMaterialInfo();

        materialCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                categoryDialog();
            }
        });

        applyChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                applyChanges();
            }
        });

        deleteMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                deleteMaterial();
            }
        });
    }

    private void deleteMaterial()
    {
        Log.v("REMOVE", "removemater");
                materialsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Toast.makeText(AdminMaintainMaterialsActivity.this, "Матеріал видаленно...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminMaintainMaterialsActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

    }

    private void categoryDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_CategoryMotorTextile);
        AlertDialog dialog = builder.create();
        builder.setTitle("Категорія матеріалу").setItems(materialCategories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String category = materialCategories[which];
                materialCategory.setText(category);
            }
        }).show();
    }

    private void applyChanges()
    {
        String name = materialName.getText().toString();
        String description = materialDescritiption.getText().toString();
        String category = materialCategory.getText().toString();
        String price = materialPrice.getText().toString();

        if(name.equals(""))
        {
            Toast.makeText(this, "Введіть назву матеріалу...", Toast.LENGTH_SHORT).show();
        }
        else if(description.equals(""))
        {
            Toast.makeText(this, "Введіть опис матеріалу...", Toast.LENGTH_SHORT).show();
        }
        else if(category.equals(""))
        {
            Toast.makeText(this, "Ввиберіть категорію матеріалу...", Toast.LENGTH_SHORT).show();
        }
        else if(price.equals(""))
        {
            Toast.makeText(this, "Введіть ціну матеріалу...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> materialMap = new HashMap<>();
            materialMap.put("description", description);
            materialMap.put("category", category);
            materialMap.put("price", price);
            materialMap.put("mname", name);

            materialsRef.updateChildren(materialMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(AdminMaintainMaterialsActivity.this, "Успішно змінено!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminMaintainMaterialsActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displaySpecificMaterialInfo()
    {
        materialsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    String image = snapshot.child("image").getValue().toString();
                    String name = snapshot.child("mname").getValue().toString();
                    String description = snapshot.child("description").getValue().toString();
                    String category = snapshot.child("category").getValue().toString();
                    String price = snapshot.child("price").getValue().toString();

                    materialName.setText(name);
                    materialDescritiption.setText(description);
                    materialCategory.setText(category);
                    materialPrice.setText(price);
                    Picasso.get().load(image).into(materialImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}