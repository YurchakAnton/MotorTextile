package com.example.motortextile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.motortextile.Model.Materials;
import com.example.motortextile.ViewHolder.MaterialViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class SearchMaterialsActivity extends AppCompatActivity
{
    private Button searchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_materials);

        inputText = findViewById(R.id.search_material_name);
        searchBtn = findViewById(R.id.search_btn);
        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchMaterialsActivity.this));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                searchInput = inputText.getText().toString();
                if(!searchInput.equals(""))
                onStart();
                else
                    Toast.makeText(SearchMaterialsActivity.this,"Такого товару не існує!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Materials");

        FirebaseRecyclerOptions<Materials> options = new FirebaseRecyclerOptions.Builder<Materials>()
                .setQuery(reference.orderByChild("mname").startAt(searchInput), Materials.class).build();

        FirebaseRecyclerAdapter<Materials, MaterialViewHolder> adapter = new FirebaseRecyclerAdapter<Materials, MaterialViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MaterialViewHolder materialViewHolder, int i, @NonNull Materials materials)
            {
                materialViewHolder.txtMaterialName.setText(materials.getMname());
                materialViewHolder.txtMaterialCategory.setText(materials.getCategory());
                materialViewHolder.txtMaterialDescription.setText(materials.getDescription());
                materialViewHolder.txtMaterialPrice.setText("Ціна: " + materials.getPrice() + "€");
                Picasso.get().load(materials.getImage()).into(materialViewHolder .imageView);

                materialViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(SearchMaterialsActivity.this, MaterialDetailsActivity.class);
                        intent.putExtra("mname", materials.getMname());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_items_layout, parent, false);
                MaterialViewHolder holder = new MaterialViewHolder(view);
                return holder;
            }
        };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}