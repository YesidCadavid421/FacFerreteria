package com.example.facfereteria;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Clientes extends Fragment {
    private EditText etCedula, etNombre, etDireccion, etTelefono;
    private Button insertar, consultar, actualizar, eliminar;

    private Integer cedula;
    private String nombre;
    private String direccion;
    private Integer telefono;

    public Clientes() {
    }

    public Clientes(Integer cedula, String nombre, String direccion, Integer telefono) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public static Clientes crearCliente(Integer cedula, String nombre, String direccion, Integer telefono) {
        return new Clientes(cedula, nombre, direccion, telefono);
    }

    public String getName() {
        return nombre;
    }

    public Integer getCedula() {
        return cedula;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);
        etCedula = view.findViewById(R.id.etCedula);
        etNombre = view.findViewById(R.id.etNombre);
        etDireccion = view.findViewById(R.id.etDireccion);
        etTelefono = view.findViewById(R.id.etTelefono);
        insertar = view.findViewById(R.id.btInsertarCliente);
        consultar = view.findViewById(R.id.btConsultarCliente);
        actualizar = view.findViewById(R.id.btActualizarCliente);
        eliminar = view.findViewById(R.id.btEliminarCliente);

        insertar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Insertar();
            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Consultar();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Actualizar();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Eliminar();
            }
        });
        return view;
    }

    public void Insertar (){
        ConexionBD conexion = new ConexionBD(getContext(),"database",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        if(this.validarVariablesCliente()){
            String cedula = etCedula.getText().toString();
            String nombre = etNombre.getText().toString();
            String direccion = etDireccion.getText().toString();
            String telefono = etTelefono.getText().toString();
            Cursor fila = BaseDeDatos.rawQuery("select nombre, direccion, telefono from Cliente where cedula ="+ cedula, null);
            if(fila.moveToFirst()) {
                Toast.makeText(getContext(),"Ya existen registros con este numero de cedula",Toast.LENGTH_LONG).show();
                return;
            }
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
            Toast.makeText(getContext(),"registro almacenado exitosamente",Toast.LENGTH_LONG).show();
        }
    }

    public void Consultar (){
        ConexionBD conexion = new ConexionBD(getContext(),"database",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        if(this.validarVariableCedula()){
            String cedula = etCedula.getText().toString();
            Cursor fila= BaseDeDatos.rawQuery("select nombre, direccion, telefono from Cliente where cedula ="+ cedula, null);
            if (fila.moveToFirst()){
                etNombre.setText(fila.getString(0));
                etDireccion.setText(fila.getString(1));
                etTelefono.setText(fila.getString(2));
                BaseDeDatos.close();
            } else{
                Toast.makeText(getContext(),"Cliente no encontrado",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void Actualizar (){
        ConexionBD conexion = new ConexionBD(getContext(),"database",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        if(this.validarVariablesCliente()){
            String cedula = etCedula.getText().toString();
            String nombre = etNombre.getText().toString();
            String direccion = etDireccion.getText().toString();
            String telefono = etTelefono.getText().toString();
            Cursor fila = BaseDeDatos.rawQuery("select nombre, direccion, telefono from Cliente where cedula ="+ cedula, null);
            if(!fila.moveToFirst()) {
                Toast.makeText(getContext(),"No existen registros con este numero de cedula para actualizar",Toast.LENGTH_LONG).show();
                return;
            }
            ContentValues actualizar = new ContentValues();
            actualizar.put("nombre",nombre);
            actualizar.put("direccion", direccion);
            actualizar.put("telefono", telefono);
            String whereClause = "cedula=?";
            String[] whereArgs = {cedula};
            int filasActualizadas = BaseDeDatos.update("Cliente",actualizar, whereClause, whereArgs);
            if (filasActualizadas > 0) {
                Toast.makeText(getContext(), "Registro actualizado exitosamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "No se pudo actualizar el registro", Toast.LENGTH_LONG).show();
                return;
            }
            BaseDeDatos.close();
            etCedula.setText("");
            etNombre.setText("");
            etDireccion.setText("");
            etTelefono.setText("");
        }
    }

    public void Eliminar (){
        ConexionBD conexion = new ConexionBD(getContext(),"database",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        String cedula = etCedula.getText().toString();
        if(this.validarVariableCedula()){
            Cursor fila = BaseDeDatos.rawQuery("select nombre, direccion, telefono from Cliente where cedula ="+ cedula, null);
            if(!fila.moveToFirst()) {
                Toast.makeText(getContext(),"No existen registros con este numero de cedula para eliminar",Toast.LENGTH_LONG).show();
                return;
            }
            String whereClause = "cedula=?";
            String[] whereArgs = {cedula};
            int filasEliminadas = BaseDeDatos.delete("Cliente", whereClause, whereArgs);
            if (filasEliminadas > 0) {
                Toast.makeText(getContext(), "Registro eliminado exitosamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "No se pudo eliminar el registro", Toast.LENGTH_LONG).show();
                return;
            }
            BaseDeDatos.close();
            etCedula.setText("");
            etNombre.setText("");
            etDireccion.setText("");
            etTelefono.setText("");
        }
    }

    private boolean validarVariableCedula(){
        String cedulaText = etCedula.getText().toString();
        if (cedulaText.isEmpty()) {
            Toast.makeText(getContext(), "El campo cédula es requerido", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!esNumero(cedulaText)) {
            Toast.makeText(getContext(), "Ingrese solo caracteres numéricos para el número de cédula", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    };

    private boolean validarVariablesCliente(){
        String cedulaText = etCedula.getText().toString();
        String nombreText = etNombre.getText().toString();
        String direccionText = etDireccion.getText().toString();
        String telefonoText = etTelefono.getText().toString();
        if (cedulaText.isEmpty()) {
            Toast.makeText(getContext(), "El campo cédula es requerido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (nombreText.isEmpty()) {
            Toast.makeText(getContext(), "El campo nombre es requerido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (direccionText.isEmpty()) {
            Toast.makeText(getContext(), "El campo dirección es requerido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (telefonoText.isEmpty()) {
            Toast.makeText(getContext(), "El campo telefono es requerido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!esNumero(cedulaText)) {
            Toast.makeText(getContext(), "Ingrese solo caracteres numéricos para el número de cédula", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!esNumero(telefonoText)) {
            Toast.makeText(getContext(), "Ingrese solo caracteres numéricos para el teléfono", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean esNumero(String texto) {
        try {
            Integer.parseInt(texto);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}