package com.example.facfereteria;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;


import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.facfereteria.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private EditText etCedula, etNombre, etDireccion, etTelefono;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_Clientes, R.id.nav_Pedidos,R.id.nav_Facturas,R.id.nav_Productos)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    public void Insertar (View view){
        ConexionBD conexion = new ConexionBD(this,"bdClientes",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        String cedula = etCedula.getText().toString();
        String nombre = etNombre.getText().toString();
        String direccion = etDireccion.getText().toString();
        String telefono = etTelefono.getText().toString();
        if(!cedula.isEmpty() && !nombre.isEmpty() && !direccion.isEmpty() && telefono.isEmpty()){
            ContentValues insertar = new ContentValues();
            insertar.put("cedula",cedula);
            insertar.put("nombre",nombre);
            insertar.put("direccion", direccion);
            insertar.put("telefono", telefono);
            BaseDeDatos.insert("Cliente",null,insertar);
            BaseDeDatos.close();
            etCedula.setText("");
            etNombre.setText("");
            etDireccion.setText("");
            etTelefono.setText("");
            Toast.makeText(this,"registro almacenado exitosamente",Toast.LENGTH_LONG).show();
        }

    }

    public void Consultar (View view){
        ConexionBD conexion = new ConexionBD(this, "bdClientes", null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        String cedula = "5";
        if(!cedula.isEmpty()){
            Cursor fila= BaseDeDatos.rawQuery("select nombre, direccion, telefono from Cliente where cedula ="+ cedula, null);
            if (fila.moveToFirst()){
                etNombre.setText(fila.getString(0));
                etDireccion.setText(fila.getString(1));
                etTelefono.setText(fila.getString(2));
                BaseDeDatos.close();
            }else{
                Toast.makeText(this,"Usuario no encontrado",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this,"Debe diligenciar el campo Cedula",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}