package com.example.facfereteria;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConexionBD extends SQLiteOpenHelper {


    public ConexionBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE Cliente(cedula INTEGER PRIMARY KEY, nombre TEXT, direccion TEXT, telefono INTEGER)");
        database.execSQL("CREATE TABLE Producto(codigoProducto INTEGER PRIMARY KEY, descripcion TEXT, valor REAL)");
        database.execSQL("CREATE TABLE Pedido(codigoPedido INTEGER PRIMARY KEY, descripcion TEXT, fechaPedido TEXT, codigoCliente INTEGER, FOREIGN KEY (codigoCliente) REFERENCES Cliente(cedula))");
        database.execSQL("CREATE TABLE PedProd(codigoPedido INTEGER, codigoProducto INTEGER, PRIMARY KEY (codigoPedido, codigoProducto), FOREIGN KEY (codigoPedido) REFERENCES Pedido(codigoPedido), FOREIGN KEY (codigoProducto) REFERENCES Producto(codigoProducto))");
        database.execSQL("CREATE TABLE Factura(codigoFactura INTEGER PRIMARY KEY, fechaFactura TEXT, valorFactura REAL, codigoPedido INTEGER, FOREIGN KEY (codigoPedido) REFERENCES Pedido(codigoPedido))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
