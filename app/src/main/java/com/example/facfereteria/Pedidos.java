package com.example.facfereteria;
import android.app.AlertDialog;
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
    private EditText etCodigoPedido, etDescripcion;
    private Button insertar, consultar, actualizar, eliminar;
    private Spinner spinnerCliente;
    private View etFecha;
    TextView textView;
    boolean[] selectedLanguage;
    ArrayList<Integer> langList = new ArrayList<>();
    private TableLayout tableLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        etFecha = view.findViewById(R.id.etFecha);
        ((EditText) etFecha).setText(SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.SHORT, Locale.getDefault()).format(new Date()));
        spinnerCliente = view.findViewById(R.id.spinnerCliente);
        etDescripcion = view.findViewById(R.id.etDescripcion2);
        etCodigoPedido = view.findViewById(R.id.etCodPedido);
        insertar = view.findViewById(R.id.btInsertarPedido);
        consultar = view.findViewById(R.id.btConsultarPedido);
        actualizar = view.findViewById(R.id.btActualizarPedido);
        eliminar = view.findViewById(R.id.btEliminarPedido);
        tableLayout = view.findViewById(R.id.tableLayout);
        List<Clientes> listclientes = obtenerDatosSpinnerCliente();
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
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Manejar caso de nada seleccionado
            }
        });
        spinnerCliente.setAdapter(adapter);
        insertar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Insertar();
            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Consultar();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Actualizar();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Eliminar();
            }
        });

        List<Productos> listaProductos = obtenerDatosProductos();
        List<String> descripcionesProductos = new ArrayList<>();
        for (Productos producto : listaProductos) {
            descripcionesProductos.add(producto.getDescripcion());
        }

        // assign variable
        textView = view.findViewById(R.id.textViewMultiSelect);

        // initialize selected language array
        selectedLanguage = new boolean[descripcionesProductos.toArray(new String[0]).length];

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                // set title
                builder.setTitle("Selecciona los productos");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(descripcionesProductos.toArray(new String[0]), selectedLanguage, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in lang list
                            langList.add(i);
                            // Sort array list
                            Collections.sort(langList);
                        } else {
                            // when checkbox unselected
                            // Remove position from langList
                            langList.remove(Integer.valueOf(i));
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
                        for (int j = 0; j < langList.size(); j++) {
                            stringBuilder.append(descripcionesProductos.toArray(new String[0])[langList.get(j)]);
                            if (j != langList.size() - 1) {
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
                            langList.clear();
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
        // Crea una nueva fila
        TableRow row = new TableRow(getContext());

        // Configura las vistas de la fila
        TextView codigoTextView = new TextView(getContext());
        TextView descripcionTextView = new TextView(getContext());
        TextView valorTextView = new TextView(getContext());

        codigoTextView.setGravity(Gravity.CENTER);
        descripcionTextView.setGravity(Gravity.CENTER);
        valorTextView.setGravity(Gravity.CENTER);

        codigoTextView.setText(String.valueOf(producto.getCodigo()));
        descripcionTextView.setText(producto.getDescripcion());
        valorTextView.setText(String.valueOf(producto.getValor()));

        // Agrega las vistas a la fila
        row.addView(codigoTextView);
        row.addView(descripcionTextView);
        row.addView(valorTextView);

        // Agrega la fila a la tabla
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
}