package com.example.motortextile;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motortextile.Admin.AdminAddNewMaterialActivity;
import com.example.motortextile.Admin.AdminMaintainMaterialsActivity;
import com.example.motortextile.Admin.AdminNewOrdersActivity;
import com.example.motortextile.Model.Materials;
import com.example.motortextile.Prevalent.Prevalent;
import com.example.motortextile.ViewHolder.MaterialViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private DatabaseReference MaterialsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private static long back_pressed;
    NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationView=(NavigationView) findViewById(R.id.nav_view);



        MaterialsRef = FirebaseDatabase.getInstance().getReference().child("Materials");

        Paper.init(this);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Матеріали");
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        userNameTextView.setText(Prevalent.currentOnlineUser.getName());
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }
    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finishAffinity();
        }
        else
        {
            Toast.makeText(this , "Натисніть двічі для виходу!", Toast.LENGTH_LONG).show();
        }
        back_pressed = System.currentTimeMillis();

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Menu menu = navigationView.getMenu();
        if(Prevalent.currentOnlineUser.getAdminPanel().equals("0")) {
            menu.setGroupVisible(R.id.group2, false);
        }
        else if (Prevalent.currentOnlineUser.getAdminPanel().equals("1"))
        {
            menu.setGroupVisible(R.id.group2, true);
        }


        FirebaseRecyclerOptions<Materials> options =
                new FirebaseRecyclerOptions.Builder<Materials>()
                        .setQuery(MaterialsRef, Materials.class)
                        .build();


        FirebaseRecyclerAdapter<Materials, MaterialViewHolder> adapter =
                new FirebaseRecyclerAdapter<Materials, MaterialViewHolder>(options ) {
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
                                if(type.equals("Admin"))
                                {
                                    Intent intent = new Intent(HomeActivity.this, AdminMaintainMaterialsActivity.class);
                                    intent.putExtra("mname", materials.getMname());
                                    startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(HomeActivity.this, MaterialDetailsActivity.class);
                                    intent.putExtra("mname", materials.getMname());
                                    startActivity(intent);
                                }
                            }
                        });

                        registerForContextMenu(materialViewHolder.itemView);
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
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void showMaterialDialog(MaterialViewHolder materialViewHolder,Materials materials)
    {
        materialViewHolder.txtMaterialName.setText(materials.getMname());
        materialViewHolder.txtMaterialCategory.setText(materials.getCategory());
        materialViewHolder.txtMaterialDescription.setText(materials.getDescription());
        materialViewHolder.txtMaterialPrice.setText("Ціна: " + materials.getPrice() + "€");

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        final int MENU_COLOR_RED=1;
        switch (v.getId())
        {
            case R.id.recycler_menu:
                menu.add(0, MENU_COLOR_RED,0, "Red");
                break;
        }
    }

    /*@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart)
        {
            type="";
            toolbar.setTitle("Матеріали");
            fab.setVisibility(View.VISIBLE);
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_search)
        {
            type="";
            toolbar.setTitle("Матеріали");
            fab.setVisibility(View.VISIBLE);
            Intent intent = new Intent(HomeActivity.this, SearchMaterialsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_settings)
        {
            type="";
            toolbar.setTitle("Матеріали");
            fab.setVisibility(View.VISIBLE);
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_logout)
        {
            Paper.book().destroy();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        else if (id == R.id.nav_add_new_material)
        {
            type="";
            toolbar.setTitle("Матеріали");
            fab.setVisibility(View.VISIBLE);
            Intent intent = new Intent(HomeActivity.this, AdminAddNewMaterialActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_check_new_orders)
        {
            type="";
            toolbar.setTitle("Матеріали");
            fab.setVisibility(View.VISIBLE);
            Intent intent = new Intent(HomeActivity.this, AdminNewOrdersActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_change_material)
        {
            type="Admin";
            Toast.makeText(HomeActivity.this,"Виберіть елемент для зміни!",Toast.LENGTH_SHORT).show();
            toolbar.setTitle("Виберіть елемент для зміни");
            fab.setVisibility(View.GONE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}