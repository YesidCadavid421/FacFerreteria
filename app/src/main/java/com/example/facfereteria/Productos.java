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
import android.widget.LinearLayout;
import android.widget.Toast;

public class Productos extends Fragment {

    private EditText etCodProducto, etDescripcion, etValor;
    Button insertar, consultar, actualizar, eliminar;

    private Integer codigoProducto;
    private String descripcion;
    private Double valor;

    public Productos() {
    }

    public Productos(Integer codigoProducto, String descripcion, Double valor) {
        this.codigoProducto = codigoProducto;
        this.descripcion = descripcion;
        this.valor = valor;
    }

    public static Productos crearProducto(Integer codigoProducto, String descripcion, Double valor) {
        return new Productos(codigoProducto, descripcion, valor);
    }

    public Integer getCodigo() {
        return codigoProducto;
    }

    public Double getValor() {
        return valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_productos, container, false);
        etCodProducto = view.findViewById(R.id.etCodProducto);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etValor = view.findViewById(R.id.etValor);
        insertar = view.findViewById(R.id.btInsertarProducto);
        consultar = view.findViewById(R.id.btConsultarProducto);
        actualizar = view.findViewById(R.id.btActualizarProducto);
        eliminar = view.findViewById(R.id.btEliminarProducto);

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
        String codProducto = etCodProducto.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String valor = etValor.getText().toString();
        if(this.validarVariablesProducto()){
            Cursor fila= BaseDeDatos.rawQuery("select descripcion, valor from Producto where codigoProducto ="+ codProducto, null);
            if(fila.moveToFirst()) {
                Toast.makeText(getContext(),"Ya existen registros con este código",Toast.LENGTH_LONG).show();
                return;
            }
            ContentValues insertar = new ContentValues();
            insertar.put("codigoProducto",codProducto);
            insertar.put("descripcion",descripcion);
            insertar.put("valor", valor);
            BaseDeDatos.insert("Producto",null,insertar);
            BaseDeDatos.close();
            etCodProducto.setText("");
            etDescripcion.setText("");
            etValor.setText("");
            Toast.makeText(getContext(),"registro almacenado exitosamente",Toast.LENGTH_LONG).show();
        }
    }

    public void Consultar (){
        ConexionBD conexion = new ConexionBD(getContext(),"database",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        String codProducto = etCodProducto.getText().toString();
        if(this.validarVariableCodProducto()){
            Cursor fila= BaseDeDatos.rawQuery("select descripcion, valor from Producto where codigoProducto ="+ codProducto, null);
            if (fila.moveToFirst()){
                etDescripcion.setText(fila.getString(0));
                etValor.setText(fila.getString(1));
                BaseDeDatos.close();
            } else{
                Toast.makeText(getContext(),"Producto no encontrado",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void Actualizar (){
        ConexionBD conexion = new ConexionBD(getContext(),"database",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        String codProducto = etCodProducto.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String valor = etValor.getText().toString();
        if(this.validarVariablesProducto()){
            Cursor fila= BaseDeDatos.rawQuery("select descripcion, valor from Producto where codigoProducto ="+ codProducto, null);
            if(!fila.moveToFirst()) {
                Toast.makeText(getContext(),"No existen registros con este código para actualizar",Toast.LENGTH_LONG).show();
                return;
            }
            ContentValues actualizar = new ContentValues();
            actualizar.put("descripcion", descripcion);
            actualizar.put("valor", valor);
            String whereClause = "codigoProducto=?";
            String[] whereArgs = {codProducto};
            int filasActualizadas = BaseDeDatos.update("Producto",actualizar, whereClause, whereArgs);
            if (filasActualizadas > 0) {
                Toast.makeText(getContext(), "Registro actualizado exitosamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "No se pudo actualizar el registro", Toast.LENGTH_LONG).show();
                return;
            }
            BaseDeDatos.close();
            etCodProducto.setText("");
            etDescripcion.setText("");
            etValor.setText("");
        }
    }

    public void Eliminar (){
        ConexionBD conexion = new ConexionBD(getContext(),"database",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        String codProducto = etCodProducto.getText().toString();
        if(this.validarVariablesProducto()){
            Cursor fila= BaseDeDatos.rawQuery("select descripcion, valor from Producto where codigoProducto ="+ codProducto, null);
            if(!fila.moveToFirst()) {
                Toast.makeText(getContext(),"No existen registros con este código para eliminar",Toast.LENGTH_LONG).show();
                return;
            }
            String whereClause = "codigoProducto=?";
            String[] whereArgs = {codProducto};
            int filasEliminadas = BaseDeDatos.delete("Producto", whereClause, whereArgs);
            if (filasEliminadas > 0) {
                Toast.makeText(getContext(), "Registro eliminado exitosamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "No se pudo eliminar el registro", Toast.LENGTH_LONG).show();
                return;
            }
            BaseDeDatos.close();
            etCodProducto.setText("");
            etDescripcion.setText("");
            etValor.setText("");
        }
    }

    private boolean validarVariableCodProducto(){
        String codProductoText = etCodProducto.getText().toString();
        if (codProductoText.isEmpty()) {
            Toast.makeText(getContext(), "El campo código del producto es requerido", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!esNumero(codProductoText)) {
            Toast.makeText(getContext(), "Ingrese solo caracteres numéricos para el código del producto", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    };

    private boolean validarVariablesProducto(){
        String codProductoText = etCodProducto.getText().toString();
        String descripcionText = etDescripcion.getText().toString();
        String valorText = etValor.getText().toString();
        if (codProductoText.isEmpty()) {
            Toast.makeText(getContext(), "El campo código del producto es requerido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (descripcionText.isEmpty()) {
            Toast.makeText(getContext(), "El campo descripción es requerido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (valorText.isEmpty()) {
            Toast.makeText(getContext(), "El campo valor es requerido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!esNumero(codProductoText)) {
            Toast.makeText(getContext(), "Ingrese solo caracteres numéricos para el código del producto", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!esReal(valorText)) {
            Toast.makeText(getContext(), "Ingrese solo caracteres numéricos para el valor usando '.'", Toast.LENGTH_LONG).show();
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

    private boolean esReal(String texto) {
        try {
            Double.parseDouble(texto);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}