package com.example.facfereteria;

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

public class Facturas extends Fragment {
    private EditText etCodFactura, etFechaFactura, etValorFactura, etCodigoPedidoFactura;
    Button consultar;

    public Facturas() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facturas, container, false);
        etCodFactura = view.findViewById(R.id.etCodFactura);
        etFechaFactura = view.findViewById(R.id.etFecha2);
        etValorFactura = view.findViewById(R.id.etValor2);
        etCodigoPedidoFactura = view.findViewById(R.id.etCodigoPedidoFactura);
        consultar = view.findViewById(R.id.btConsultarFactura);
        consultar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Consultar();
            }
        });
        return view;
    }

    public void Consultar (){
        ConexionBD conexion = new ConexionBD(getContext(),"database",null,1);
        SQLiteDatabase BaseDeDatos = conexion.getWritableDatabase();
        if(this.validarVariableCodFactura()){
            String codFactura = etCodFactura.getText().toString();
            Cursor fila= BaseDeDatos.rawQuery("select fechaFactura, valorFactura, codigoPedido from Factura where codigoFactura ="+ codFactura, null);
            if (fila.moveToFirst()){
                etFechaFactura.setText(fila.getString(0));
                etValorFactura.setText(fila.getString(1));
                etCodigoPedidoFactura.setText(fila.getString(2));
                BaseDeDatos.close();
            } else{
                Toast.makeText(getContext(),"Factura no encontrado con el código digitado",Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean validarVariableCodFactura(){
        String codFacturaText = etCodFactura.getText().toString();
        if (codFacturaText.isEmpty()) {
            Toast.makeText(getContext(), "El campo código del producto es requerido", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!esNumero(codFacturaText)) {
            Toast.makeText(getContext(), "Ingrese solo caracteres numéricos para el código del producto", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    };

    private boolean esNumero(String texto) {
        try {
            Integer.parseInt(texto);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}