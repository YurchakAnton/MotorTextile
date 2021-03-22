package com.example.motortextile.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motortextile.Interface.ItemClickListner;
import com.example.motortextile.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtMaterialName, txtMaterialPrice, txtMaterialAmount;
    private ItemClickListner itemClickListner;

    public CartViewHolder(@NonNull View itemView)
    {
        super(itemView);
        txtMaterialName = itemView.findViewById(R.id.cart_material_name);
        txtMaterialPrice = itemView.findViewById(R.id.cart_material_price);
        txtMaterialAmount = itemView.findViewById(R.id.cart_material_amount);
    }

    @Override
    public void onClick(View view)
    {
        itemClickListner.onCLick(view, getAdapterPosition(), false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner)
    {
        this.itemClickListner = itemClickListner;
    }
}
