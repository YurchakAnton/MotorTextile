package com.example.motortextile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.motortextile.Model.Cart;
import com.example.motortextile.Prevalent.Prevalent;
import com.example.motortextile.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtMsg1;
    private double overTotalPrice=0;
    private int kilk=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        txtMsg1 = (TextView) findViewById(R.id.msg1);


        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(kilk>=1) {
                    Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                    intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(CartActivity.this, "Корзина пуста!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        CheckOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                        .child("Materials"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull Cart cart)
            {
                cartViewHolder.txtMaterialAmount.setText("Метраж: "+cart.getAmount());
                cartViewHolder.txtMaterialPrice.setText("Ціна: "+cart.getPrice()+"€");
                cartViewHolder.txtMaterialName.setText("Назва: "+cart.getMname());
                kilk=i+1;
                double oneTypeMaterialPrice = Double.valueOf(cart.getPrice()) * Double.valueOf(cart.getAmount());
                overTotalPrice = overTotalPrice+oneTypeMaterialPrice;

                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Змінити",
                                        "Видалити"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Дії над обраним елементом:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if(which == 0)
                                {
                                    Intent intent = new Intent(CartActivity.this, MaterialDetailsActivity.class);
                                    intent.putExtra("mname", cart.getMname());
                                    startActivity(intent);
                                }
                                if(which == 1)
                                {
                                    AlertDialog.Builder builderanswer = new AlertDialog.Builder(CartActivity.this);
                                    builderanswer.setMessage("Ви впевнені, що бажаєте видалити вибраний елемент?");
                                    builderanswer.setCancelable(true);
                                    builderanswer.setPositiveButton("Видалити", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            Log.v("REMOVE", "removecart");
                                            cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Materials").child(cart.getMid()).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                                                    .child("Materials").child(cart.getMid()).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            Toast.makeText(CartActivity.this, "Матеріал видалено з корзини!", Toast.LENGTH_SHORT).show();
                                                                            finish();
                                                                        }
                                                                    });

                                                        }
                                                    });
                                        }
                                    });

                                    builderanswer.setNegativeButton("Відмінна", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            dialog.cancel();
                                        }
                                    });
                                    AlertDialog alert= builderanswer.create();
                                    alert.show();
                                }
                            }
                        });
                        builder.show();
                    }
                });
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
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        nextProcessBtn.setVisibility(View.GONE);
                    }
                    else if(shippingState.equals("not shipped"))
                    {
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        nextProcessBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}