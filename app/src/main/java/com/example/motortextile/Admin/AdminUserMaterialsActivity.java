package com.example.motortextile.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.motortextile.Model.Cart;
import com.example.motortextile.Prevalent.Prevalent;
import com.example.motortextile.R;
import com.example.motortextile.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminUserMaterialsActivity extends AppCompatActivity
{
    private RecyclerView materialsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListRef;

    private String userID="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_materials);

        userID = getIntent().getStringExtra("uid");

        materialsList=findViewById(R.id.materials_list);
        materialsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        materialsList.setLayoutManager(layoutManager);

        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child("Admin View").child(userID).child("Materials");
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef, Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull Cart cart)
            {
                cartViewHolder.txtMaterialAmount.setText("Метраж: "+cart.getAmount());
                cartViewHolder.txtMaterialPrice.setText("Ціна: "+cart.getPrice()+"€");
                cartViewHolder.txtMaterialName.setText("Назва: "+cart.getMname());
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent , false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        materialsList.setAdapter(adapter);
        adapter.startListening();
    }
}