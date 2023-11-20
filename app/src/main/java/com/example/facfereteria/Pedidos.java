package com.example.facfereteria;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        return view;
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
}