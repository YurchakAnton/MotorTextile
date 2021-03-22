package com.example.motortextile.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motortextile.HomeActivity;
import com.example.motortextile.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewMaterialActivity extends AppCompatActivity {
    private String CategoryName, Description, Price, Mname, saveCurrentDate, saveCurrentTime;
    private Button AddNewMaterialButton;
    private ImageView InputMaterialImage;
    private TextView InputMaterialCategory;
    private EditText InputMaterialName, InputMaterialDescription, InputMaterialPrice;
    private Uri ImageUri;
    private String materialRandomKey, downloadImageUrl;
    private StorageReference MaterialImagesRef;
    private DatabaseReference MaterialsRef;
    private ProgressDialog loadingBar;
    private static final int GalleryPick = 1;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_material);

        MaterialImagesRef = FirebaseStorage.getInstance().getReference().child("Material Images");
        /*MaterialsRef = FirebaseDatabase.getInstance().getReference().child("Materials");*/


        AddNewMaterialButton = (Button) findViewById(R.id.add_new_material);
        InputMaterialImage = (ImageView) findViewById(R.id.select_material_image);
        InputMaterialName = (EditText) findViewById(R.id.material_name);
        InputMaterialCategory = (TextView) findViewById(R.id.material_category);
        InputMaterialDescription = (EditText) findViewById(R.id.material_description);
        InputMaterialPrice = (EditText) findViewById(R.id.material_price);
        loadingBar = new ProgressDialog(this);

        InputMaterialImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        AddNewMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateMaterial();
            }
        });

        InputMaterialCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
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
                InputMaterialCategory.setText(category);
            }
        }).show();
    }

    private void ValidateMaterial()
    {
        Description = InputMaterialDescription.getText().toString();
        Price = InputMaterialPrice.getText().toString();
        Mname = InputMaterialName.getText().toString();
        CategoryName= InputMaterialCategory.getText().toString();

        if (ImageUri == null)
        {
            Toast.makeText(this, "Фото не вибрано!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Будь ласка введіть опис матеріалу...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(CategoryName))
        {
            Toast.makeText(this, "Будь ласка введіть категорію матеріалу...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this, "Будь ласка введіть ціну матеріалу...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Mname))
        {
            Toast.makeText(this, "Будь ласка введіть назву матеріалу...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreMaterialInformation();
        }
    }

    private void StoreMaterialInformation()
    {
        loadingBar.setTitle("Додання нового матеріалу");
        loadingBar.setMessage("Зачекайте...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        materialRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = MaterialImagesRef.child(ImageUri.getLastPathSegment() + materialRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AdminAddNewMaterialActivity.this, "Помилка: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            SaveMaterialInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            InputMaterialImage.setImageURI(ImageUri);
        }
    }

    private void SaveMaterialInfoToDatabase()
    {
        MaterialsRef = FirebaseDatabase.getInstance().getReference();

        MaterialsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot)
            {
                if (!(datasnapshot.child("Materials").child(Mname).exists()))
                {
                    HashMap<String, Object> materialMap = new HashMap<>();
                    materialMap.put("mid", materialRandomKey);
                    materialMap.put("date", saveCurrentDate);
                    materialMap.put("time", saveCurrentTime);
                    materialMap.put("description", Description);
                    materialMap.put("image", downloadImageUrl);
                    materialMap.put("category", CategoryName);
                    materialMap.put("price", Price);
                    materialMap.put("mname", Mname);

                    MaterialsRef.child("Materials").child(Mname).updateChildren(materialMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(AdminAddNewMaterialActivity.this, "Фото успішно загруженно...", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Toast.makeText(AdminAddNewMaterialActivity.this,"Матеріал додано...",Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(AdminAddNewMaterialActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(AdminAddNewMaterialActivity.this,"Помилка: Попробуйте знову через декілька хвилин...",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(AdminAddNewMaterialActivity.this,"Ця назва матеріалу ("+Mname+") вже використовується...",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(AdminAddNewMaterialActivity.this,"Будь ласка, додайте не існуючий матеріал...",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminAddNewMaterialActivity.this, HomeActivity.class);
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