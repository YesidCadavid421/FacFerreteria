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
        database.execSQL("Create table Cliente(cedula int primary key, nombre text, direccion text, telefono int)");
        database.execSQL("Create table Producto(codigoProducto int primary key, descripcion text, valor real)");
        database.execSQL("Create table Pedido(codigoPedido int primary key, descripcion text, fechaPedido date, codigoCliente int, foreign key (codigoCliente) references Cliente(codigigoCliente))");
        database.execSQL("Create table PedProd(codigoPedido int, codigoProducto int, primary key (codigoPedido, codigoProducto), foreign key (codigoPedido) references Pedido(codigoPedido), foreign key (codigoProducto) references Producto(codigoProducto))");
        database.execSQL("Create table Factura(codigoFactura int primary key, fecha date, valorFactura real, codigoPedido int, foreign key (codigoPedido) references Pedido(codigoPedido))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
