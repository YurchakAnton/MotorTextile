package com.example.motortextile.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.motortextile.Interface.ItemClickListner;
import com.example.motortextile.R;

public class MaterialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtMaterialName, txtMaterialDescription, txtMaterialPrice, txtMaterialCategory;
    public ImageView imageView;
    public ItemClickListner listner;

    public MaterialViewHolder(View itemView)
    {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.material_image);
        txtMaterialName = (TextView) itemView.findViewById(R.id.material_name);
        txtMaterialCategory = (TextView) itemView.findViewById(R.id.material_category);
        txtMaterialDescription = (TextView) itemView.findViewById(R.id.material_description);
        txtMaterialPrice = (TextView) itemView.findViewById(R.id.material_price);
    }

    public void setItemClickListener(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onCLick(view, getAdapterPosition(),false);
    }
}
