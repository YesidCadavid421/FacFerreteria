package com.example.facfereteria;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Pedidos extends Fragment {
    private EditText etCodigoPedido, etDescripcion, etFecha;
    private Button insertar, consultar, actualizar, eliminar;
    private Spinner spinnerCliente;
    TextView textView;
    boolean[] selectedLanguage;
    ArrayList<Integer> ProductosList = new ArrayList<>();
    private TableLayout tableLayout;

    private Integer cedula;
    List<Clientes> listclientes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        etFecha = view.findViewById(R.id.etFecha);
        etFecha.setText(SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, Locale.getDefault()).format(new Date()));
        spinnerCliente = view.findViewById(R.id.spinnerCliente);
        etDescripcion = view.findViewById(R.id.etDescripcion2);
        etCodigoPedido = view.findViewById(R.id.etCodPedido);
        insertar = view.findViewById(R.id.btInsertarPedido);
        consultar = view.findViewById(R.id.btConsultarPedido);
        actualizar = view.findViewById(R.id.btActualizarPedido);
        eliminar = view.findViewById(R.id.btEliminarPedido);
        tableLayout = view.findViewById(R.id.tableLayout);
        listclientes = obtenerDatosSpinnerCliente();
        List<Map<String, String>> data = new ArrayList<>();
        for (Clientes cliente : listclientes) {
            Map<String, String> item = new HashMap<>();
            item.put("nombre", cliente.getName());
            item.put("cedula", String.valueOf(cliente.getCedula()));
            data.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                requireContext(),
                data,
                android.R.layout.simple_spinner_item,
                new String[]{"nombre"},
                new int[]{android.R.id.text1}
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(adapter);

        spinnerCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String cedulaSeleccionada = data.get(position).get("cedula");
                cedula = Integer.parseInt(cedulaSeleccionada);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Manejar caso de nada seleccionado
            }
        });
        spinnerCliente.setAdapter(adapter);
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

        List<Productos> listaProductos = obtenerDatosProductos();
        List<String> descripcionesProductos = new ArrayList<>();
        for (Productos producto : listaProductos) {
            descripcionesProductos.add(producto.getDescripcion());
        }
        textView = view.findViewById(R.id.textViewMultiSelect);
        selectedLanguage = new boolean[descripcionesProductos.toArray(new String[0]).length];

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Selecciona los productos");
                builder.setCancelable(false);

                builder.setMultiChoiceItems(descripcionesProductos.toArray(new String[0]), selectedLanguage, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            ProductosList.add(i);
                            Collections.sort(ProductosList);
                        } else {
                            ProductosList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SparseBooleanArray checkedItems = ((AlertDialog) dialogInterface).getListView().getCheckedItemPositions();
                        TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
                        tableLayout.removeAllViews();
                        tableLayout.addView(headerRow);
                        for (int j = 0; j < checkedItems.size(); j++) {
                            int position = checkedItems.keyAt(j);
                            boolean isChecked = checkedItems.valueAt(j);
                            if (isChecked) {
                                Productos productoSeleccionado = listaProductos.get(position);
                                agregarProductoATabla(tableLayout, productoSeleccionado);
                            }
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < ProductosList.size(); j++) {
                            stringBuilder.append(descripcionesProductos.toArray(new String[0])[ProductosList.get(j)]);
                            if (j != ProductosList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        textView.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Limpiar Todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
                        tableLayout.removeAllViews();
                        tableLayout.addView(headerRow);
                        // use for loop
                        for (int j = 0; j < selectedLanguage.length; j++) {
                            // remove all selection
                            selectedLanguage[j] = false;
                            // clear language list
                            ProductosList.clear();
                            // clear text view value
                            textView.setText("");
                        }
                    }
                });
                // show dialog
                builder.show();
            }
        });
        return view;
    }

    private void agregarProductoATabla(TableLayout tableLayout, Productos producto) {
        TableRow row = new TableRow(getContext());

        TextView codigoTextView = new TextView(getContext());
        TextView descripcionTextView = new TextView(getContext());
        TextView valorTextView = new TextView(getContext());

        codigoTextView.setGravity(Gravity.CENTER);
        descripcionTextView.setGravity(Gravity.CENTER);
        valorTextView.setGravity(Gravity.CENTER);

        codigoTextView.setText(String.valueOf(producto.getCodigo()));
        descripcionTextView.setText(producto.getDescripcion());
        valorTextView.setText(String.valueOf(producto.getValor()));
        row.addView(codigoTextView);
        row.addView(descripcionTextView);
        row.addView(valorTextView);
        tableLayout.addView(row);
    }

    private List<Clientes> obtenerDatosSpinnerCliente() {
        ConexionBD conexion = new ConexionBD(getContext(),"database",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        Cursor fila = BaseDeDatos.rawQuery("SELECT cedula, nombre, direccion, telefono FROM Cliente", null);
        List<Clientes> listaClientes = new ArrayList<>();
        if (fila != null && fila.moveToFirst()) {
            int indiceCedula = fila.getColumnIndex("cedula");
            int indiceNombre = fila.getColumnIndex("nombre");
            int indiceDireccion = fila.getColumnIndex("direccion");
            int indiceTelefono = fila.getColumnIndex("telefono");
            if (indiceCedula != -1 && indiceNombre != -1 && indiceDireccion != -1 && indiceTelefono != -1) {
                do {
                    Integer cedula = Integer.parseInt(fila.getString(indiceCedula));
                    String nombre = fila.getString(indiceNombre);
                    String direccion = fila.getString(indiceDireccion);
                    Integer telefono = Integer.parseInt(fila.getString(indiceTelefono));
                    Clientes cliente = Clientes.crearCliente(cedula,nombre,direccion,telefono);
                    listaClientes.add(cliente);
                } while (fila.moveToNext());
                fila.close();
            } else {
                Toast.makeText(getContext(),"Una o más columnas no fueron encontradas en clientes.",Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(),"No se encontraron resultados para la lista de clientes.",Toast.LENGTH_LONG).show();
        }
        return listaClientes;
    }

    private List<Productos> obtenerDatosProductos() {
        ConexionBD conexion = new ConexionBD(getContext(),"database",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        Cursor fila = BaseDeDatos.rawQuery("SELECT codigoProducto, descripcion, valor FROM Producto", null);
        List<Productos> listaProductos = new ArrayList<>();
        if (fila != null && fila.moveToFirst()) {
            int indiceCodigoProducto = fila.getColumnIndex("codigoProducto");
            int indiceDescripcion = fila.getColumnIndex("descripcion");
            int indiceValor = fila.getColumnIndex("valor");
            if (indiceCodigoProducto != -1 && indiceDescripcion != -1 && indiceValor != -1) {
                do {
                    Integer codigoProducto = Integer.parseInt(fila.getString(indiceCodigoProducto));
                    String descripcion = fila.getString(indiceDescripcion);
                    Double valor = Double.parseDouble(fila.getString(indiceValor));
                    Productos producto = Productos.crearProducto(codigoProducto,descripcion,valor);
                    listaProductos.add(producto);
                } while (fila.moveToNext());
                fila.close();
            } else {
                Toast.makeText(getContext(),"Una o más columnas no fueron encontradas en productos.",Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(),"No se encontraron resultados para la lista de productos.",Toast.LENGTH_LONG).show();
        }
        return listaProductos;
    }

    private List<Productos> obtenerInformacionDeTabla(TableLayout tableLayout) {
        int rowCount = tableLayout.getChildCount();
        List<Productos> listaProductos = new ArrayList<>();

        for (int i = 1; i < rowCount; i++) {
            View view = tableLayout.getChildAt(i);

            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                int columnCount = row.getChildCount();
                Integer codigoProducto = 0;
                String descripcion = "";
                Double valor = 0.0;

                for (int j = 0; j < columnCount; j++) {
                    View cell = row.getChildAt(j);

                    if (cell instanceof TextView) {
                        TextView textView = (TextView) cell;
                        String cellText = textView.getText().toString();
                        switch (j) {
                            case 0:
                                codigoProducto = Integer.parseInt(cellText);
                                break;
                            case 1:
                                descripcion = cellText;
                                break;
                            case 2:
                                valor = Double.parseDouble(cellText);
                                break;
                        }
                    }
                }
                Productos producto = new Productos(codigoProducto, descripcion, valor);
                listaProductos.add(producto);
            }
        }

        return listaProductos;
    }
    public void Insertar (){
        ConexionBD conexion = new ConexionBD(getContext(),"database",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        List<Productos> listProductosSeleccionados = obtenerInformacionDeTabla(tableLayout);
        if(this.validarVariablesPedido(listProductosSeleccionados)){
            Integer codigoPedido = Integer.parseInt(etCodigoPedido.getText().toString());
            String descripcionPedido = etDescripcion.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String fechaFormateada = sdf.format(new Date()).toString();
            Integer codigoCliente = cedula;
            Double valorFactura = 0.0;
            Cursor fila = BaseDeDatos.rawQuery("SELECT codigoPedido FROM Pedido WHERE codigoPedido ="+ codigoPedido, null);
            if(fila.moveToFirst()) {
                Toast.makeText(getContext(),"Ya existen registros con este código",Toast.LENGTH_LONG).show();
                return;
            }
            ContentValues insertarPedido = new ContentValues();
            ContentValues insertarFactura = new ContentValues();

            insertarPedido.put("codigoPedido",codigoPedido);
            insertarPedido.put("descripcion",descripcionPedido);
            insertarPedido.put("fechaPedido", fechaFormateada);
            insertarPedido.put("codigoCliente", codigoCliente);
            BaseDeDatos.insert("Pedido",null,insertarPedido);

            for (Productos producto : listProductosSeleccionados) {
                ContentValues insertarPedProd = new ContentValues();
                insertarPedProd.put("codigoPedido", codigoPedido);
                insertarPedProd.put("codigoProducto", producto.getCodigo());
                BaseDeDatos.insert("PedProd", null, insertarPedProd);

                valorFactura += Double.parseDouble(producto.getValor().toString());
            }
            insertarFactura.put("codigoFactura",codigoPedido);
            insertarFactura.put("fechaFactura",fechaFormateada);
            insertarFactura.put("valorFactura",valorFactura);
            insertarFactura.put("codigoPedido", codigoPedido);
            BaseDeDatos.insert("Factura", null, insertarFactura);
            etCodigoPedido.setText("");
            etDescripcion.setText("");
            ((EditText) etFecha).setText(SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.SHORT, Locale.getDefault()).format(new Date()));
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            tableLayout.removeAllViews();
            tableLayout.addView(headerRow);
            for (int j = 0; j < selectedLanguage.length; j++) {
                selectedLanguage[j] = false;
                ProductosList.clear();
                textView.setText("");
            }
            Toast.makeText(getContext(),"Registro creado, consulte la factura con el código de pedido",Toast.LENGTH_LONG).show();
        }
        BaseDeDatos.close();
    }

    public void Consultar (){
        ConexionBD conexion = new ConexionBD(getContext(), "database", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getReadableDatabase();  // Usamos getReadableDatabase en lugar de getWritableDatabase para consultas
        if (validarVariableCodPedido()) {
            Integer codigoPedido = Integer.parseInt(etCodigoPedido.getText().toString());
            List<Productos> listaProductos;
            Cursor fila = BaseDeDatos.rawQuery("SELECT descripcion, fechaPedido, codigoCliente FROM Pedido WHERE codigoPedido = " + codigoPedido, null);

            if (fila.moveToFirst()) {
                String descripcionPedido = fila.getString(0);
                String fechaPedido = fila.getString(1);
                String codigoCliente = fila.getString(2);
                etDescripcion.setText(descripcionPedido);
                etFecha.setText(fechaPedido);
                Integer indiceCliente = obtenerIndiceCliente(Integer.parseInt(codigoCliente));
                spinnerCliente.setSelection(indiceCliente);
                listaProductos = obtenerProductosAsociadosAlPedido(codigoPedido, BaseDeDatos);
                BaseDeDatos.close();
                actualizarSeleccionMultiple(listaProductos);
            } else {
                Toast.makeText(getContext(), "Pedido no encontrado", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void Actualizar() {
        ConexionBD conexion = new ConexionBD(getContext(), "database", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        List<Productos> listProductosSeleccionados = obtenerInformacionDeTabla(tableLayout);
        if (this.validarVariablesPedido(listProductosSeleccionados)) {
            Integer codigoPedido = Integer.parseInt(etCodigoPedido.getText().toString());
            Cursor fila = BaseDeDatos.rawQuery("SELECT descripcion, fechaPedido, codigoCliente FROM Pedido WHERE codigoPedido = " + codigoPedido, null);
            if(!fila.moveToFirst()) {
                Toast.makeText(getContext(),"No existen registros con este código para actualizar",Toast.LENGTH_LONG).show();
                return;
            }
            String descripcionPedido = etDescripcion.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String fechaFormateada = sdf.format(new Date()).toString();
            Integer codigoCliente = cedula;
            Double valorFactura = 0.0;

            ContentValues actualizarPedido = new ContentValues();
            actualizarPedido.put("descripcion", descripcionPedido);
            actualizarPedido.put("fechaPedido", fechaFormateada);
            actualizarPedido.put("codigoCliente", codigoCliente);
            String whereClausePedido = "codigoPedido=?";
            String[] whereArgsPedido = {codigoPedido.toString()};
            int filasActualizadasPedido = BaseDeDatos.update("Pedido", actualizarPedido, whereClausePedido, whereArgsPedido);

            if (filasActualizadasPedido > 0) {
                Toast.makeText(getContext(), "Registro actualizado, consulte la factura con el código de pedido", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "No se pudo actualizar el registro", Toast.LENGTH_LONG).show();
                return;
            }
            BaseDeDatos.delete("PedProd", "codigoPedido=?", new String[]{codigoPedido.toString()});
            for (Productos producto : listProductosSeleccionados) {
                ContentValues insertarPedProd = new ContentValues();
                insertarPedProd.put("codigoPedido", codigoPedido);
                insertarPedProd.put("codigoProducto", producto.getCodigo());
                BaseDeDatos.insert("PedProd", null, insertarPedProd);
                valorFactura += Double.parseDouble(producto.getValor().toString());
            }
            ContentValues actualizarFactura = new ContentValues();
            actualizarFactura.put("codigoFactura",codigoPedido);
            actualizarFactura.put("fechaFactura",fechaFormateada);
            actualizarFactura.put("valorFactura",valorFactura);
            actualizarFactura.put("codigoPedido", codigoPedido);

            String whereClauseFactura = "codigoPedido=?";
            String[] whereArgsFactura = {codigoPedido.toString()};
            int filasActualizadasFactura = BaseDeDatos.update("Factura", actualizarFactura, whereClauseFactura, whereArgsFactura);

            if (filasActualizadasFactura > 0) {
                Toast.makeText(getContext(), "Registro actualizado, consulte la factura con el código de pedido", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "No se pudo actualizar el registro de Factura", Toast.LENGTH_LONG).show();
                return;
            }

            BaseDeDatos.close();
            etCodigoPedido.setText("");
            etDescripcion.setText("");
            ((EditText) etFecha).setText(SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.SHORT, Locale.getDefault()).format(new Date()));
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            tableLayout.removeAllViews();
            tableLayout.addView(headerRow);
            for (int j = 0; j < selectedLanguage.length; j++) {
                selectedLanguage[j] = false;
                ProductosList.clear();
                textView.setText("");
            }
        }
    }

    public void Eliminar() {
        ConexionBD conexion = new ConexionBD(getContext(), "database", null, 1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();

        if (this.validarVariableCodPedido()) {
            String codPedido = etCodigoPedido.getText().toString();
            String whereClausePedido = "codigoPedido=?";
            String[] whereArgsPedido = {codPedido};
            Cursor fila = BaseDeDatos.rawQuery("SELECT descripcion, fechaPedido, codigoCliente FROM Pedido WHERE codigoPedido = " + codPedido, null);
            if(!fila.moveToFirst()) {
                Toast.makeText(getContext(),"No existen registros con este código para eliminar",Toast.LENGTH_LONG).show();
                return;
            }
            BaseDeDatos.delete("PedProd", whereClausePedido, whereArgsPedido);
            BaseDeDatos.delete("Factura", whereClausePedido, whereArgsPedido);

            int filasEliminadasPedido = BaseDeDatos.delete("Pedido", whereClausePedido, whereArgsPedido);

            if (filasEliminadasPedido > 0) {
                Toast.makeText(getContext(), "Registro de Pedido eliminado exitosamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "No se pudo eliminar el registro de Pedido", Toast.LENGTH_LONG).show();
                return;
            }
            BaseDeDatos.close();
            etCodigoPedido.setText("");
            etDescripcion.setText("");
            ((EditText) etFecha).setText(SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.SHORT, Locale.getDefault()).format(new Date()));
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            tableLayout.removeAllViews();
            tableLayout.addView(headerRow);
            for (int j = 0; j < selectedLanguage.length; j++) {
                selectedLanguage[j] = false;
                ProductosList.clear();
                textView.setText("");
            }
        }
    }

    private void actualizarSeleccionMultiple(List<Productos> listaProductosParaCheckear) {
        // Obtén la lista completa de productos y descripciones
        List<Productos> listaProductos = obtenerDatosProductos();
        List<String> descripcionesProductos = new ArrayList<>();

        TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
        tableLayout.removeAllViews();
        tableLayout.addView(headerRow);
        for (int j = 0; j < listaProductosParaCheckear.size(); j++) {
                Productos productoSeleccionado = listaProductosParaCheckear.get(j);
                agregarProductoATabla(tableLayout, productoSeleccionado);
        }

        // use for loop
        for (int j = 0; j < selectedLanguage.length; j++) {
            // remove all selection
            selectedLanguage[j] = false;
            // clear language list
            ProductosList.clear();
            // clear text view value
            textView.setText("");
        }

        for (Productos producto : listaProductos) {
            descripcionesProductos.add(producto.getDescripcion());
        }

        selectedLanguage = new boolean[descripcionesProductos.size()];



        for (int i = 0; i < listaProductosParaCheckear.size(); i++) {
            Productos producto = listaProductosParaCheckear.get(i);
            int indiceProducto = descripcionesProductos.indexOf(producto.getDescripcion());
            if (indiceProducto != -1) {
                selectedLanguage[indiceProducto] = true;
                ProductosList.add(indiceProducto);
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < listaProductosParaCheckear.size(); j++) {
            stringBuilder.append(descripcionesProductos.toArray(new String[0])[ProductosList.get(j)]);
            if (j != ProductosList.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        textView.setText(stringBuilder.toString());


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Selecciona los productos");
                builder.setCancelable(false);

                builder.setMultiChoiceItems(descripcionesProductos.toArray(new String[0]), selectedLanguage, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            ProductosList.add(i);
                            Collections.sort(ProductosList);
                        } else {
                            ProductosList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SparseBooleanArray checkedItems = ((AlertDialog) dialogInterface).getListView().getCheckedItemPositions();
                        TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
                        tableLayout.removeAllViews();
                        tableLayout.addView(headerRow);
                        for (int j = 0; j < checkedItems.size(); j++) {
                            int position = checkedItems.keyAt(j);
                            boolean isChecked = checkedItems.valueAt(j);
                            if (isChecked) {
                                Productos productoSeleccionado = listaProductos.get(position);
                                agregarProductoATabla(tableLayout, productoSeleccionado);
                            }
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < ProductosList.size(); j++) {
                            stringBuilder.append(descripcionesProductos.toArray(new String[0])[ProductosList.get(j)]);
                            if (j != ProductosList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        textView.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Limpiar Todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
                        tableLayout.removeAllViews();
                        tableLayout.addView(headerRow);
                        // use for loop
                        for (int j = 0; j < selectedLanguage.length; j++) {
                            // remove all selection
                            selectedLanguage[j] = false;
                            // clear language list
                            ProductosList.clear();
                            // clear text view value
                            textView.setText("");
                        }
                    }
                });
                // show dialog
                builder.show();
            }
        });
    }

    private List<Productos> obtenerProductosAsociadosAlPedido(Integer codigoPedido, SQLiteDatabase BaseDeDatos) {
        List<Productos> productos = new ArrayList<>();
        String consultaSQL = "SELECT P.codigoProducto, P.descripcion, P.valor " +
                "FROM Producto P " +
                "JOIN PedProd PP ON P.codigoProducto = PP.codigoProducto " +
                "WHERE PP.codigoPedido = " + codigoPedido;
        Cursor filaProductos = BaseDeDatos.rawQuery(consultaSQL, null);

        if (filaProductos.moveToFirst()) {
            do {
                Integer codigoProducto = filaProductos.getInt(0);
                String descripcion = filaProductos.getString(1);
                Double valor = filaProductos.getDouble(2);

                Productos producto = Productos.crearProducto(codigoProducto, descripcion, valor);
                productos.add(producto);
            } while (filaProductos.moveToNext());
        }
        filaProductos.close();
        return productos;
    }

    private int obtenerIndiceCliente(Integer codigoCliente) {
        for (int i = 0; i < listclientes.size(); i++) {
            Integer cedulaCliente = listclientes.get(i).getCedula();
            if (codigoCliente.equals(cedulaCliente)) {
                return i;
            }
        }
        return 0;
    }

    private boolean validarVariableCodPedido(){
        String codigoPedidoText = etCodigoPedido.getText().toString();
        if (codigoPedidoText.isEmpty()) {
            Toast.makeText(getContext(), "El campo código del pedido es requerido", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!esNumero(codigoPedidoText)) {
            Toast.makeText(getContext(), "Ingrese solo caracteres numéricos para el código del pedido", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    };

    private boolean validarVariablesPedido(List<Productos> listProductosSeleccionados) {
        String codigoPedidoText = etCodigoPedido.getText().toString();
        String descripcionPedidoText = etDescripcion.getText().toString();
        String codigoClienteText = cedula.toString();

        if (codigoPedidoText.isEmpty()) {
            Toast.makeText(getContext(), "El campo código del pedido es requerido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (descripcionPedidoText.isEmpty()) {
            Toast.makeText(getContext(), "El campo descripción del pedido es requerido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (codigoClienteText.isEmpty()) {
            Toast.makeText(getContext(), "El campo código del cliente es requerido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!esNumero(codigoPedidoText)) {
            Toast.makeText(getContext(), "Ingrese solo caracteres numéricos para el código del pedido", Toast.LENGTH_LONG).show();
            return false;
        }
        if(listProductosSeleccionados.size() < 1) {
            Toast.makeText(getContext(), "Debe de seleccionar almenos un producto", Toast.LENGTH_LONG).show();
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