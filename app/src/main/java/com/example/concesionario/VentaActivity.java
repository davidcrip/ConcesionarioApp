package com.example.concesionario;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VentaActivity extends AppCompatActivity {

    EditText jetcodigo, jetfecha, jetidentificacion, jetplaca;
    TextView jtvnombre, jtvcorreo, jtvmodelo, jtvmarca;
    CheckBox jcbactivoventa;

    Button jbtanular;

    String codigo, fecha;
    String identificacion, nombre, correo;
    String placa, marca, modelo;

    long respuesta;
    Byte sw;



    ClsOpenHelper admin = new ClsOpenHelper(this, "Concesionario.db", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);
        //Ocultar la barra de titulo por defecto y asociar objetos
        //Java con objetos Xml
        getSupportActionBar().hide();
        jetcodigo = findViewById(R.id.etcodigo);
        jetfecha = findViewById(R.id.etfecha);
        jetidentificacion = findViewById(R.id.etidentificacion);
        jetplaca = findViewById(R.id.etplaca);
        jtvnombre = findViewById(R.id.tvnombre);
        jtvcorreo = findViewById(R.id.tvcorreo);
        jtvmodelo = findViewById(R.id.tvmodelo);
        jtvmarca = findViewById(R.id.tvmarca);
        jcbactivoventa = findViewById(R.id.cbactivoventa);
        jbtanular = findViewById(R.id.btanular );
        sw = 0;

    }



    public void Guardar(View view) {
        codigo = jetcodigo.getText().toString();
        fecha = jetfecha.getText().toString();
        identificacion = jetidentificacion.getText().toString();
        nombre = jtvnombre.getText().toString();
        correo = jtvcorreo.getText().toString();
        placa = jetplaca.getText().toString();
        modelo = jtvmodelo.getText().toString();
        marca = jtvmarca.getText().toString();


        if (codigo.isEmpty() || fecha.isEmpty() ||
                identificacion.isEmpty() || nombre.isEmpty() || correo.isEmpty() ||
                placa.isEmpty() || modelo.isEmpty() || marca.isEmpty()) {
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        } else {
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();

            registro.put("codigo", codigo);
            registro.put("fecha", fecha);
            registro.put("identificacion", identificacion);
            registro.put("placa", placa);


            if (sw == 0) {
                SQLiteDatabase bd = admin.getReadableDatabase();
                Cursor fila = bd.rawQuery("select * from TblVenta where placa ='" + placa + "' and activo = 'Si' " , null);
                if (fila.moveToNext()) {
                    Toast.makeText(this, "No se puede guardar, El Vehiculo esta vendido", Toast.LENGTH_SHORT).show();
                    Limpiar_campos();
                }
                else {
                    respuesta = db.insert("TblVenta", null, registro);
                    Desactivar(placa);
                    if (respuesta == 0) {
                        Toast.makeText(this, "Error guardando registro", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "REGISTRO GUARDADO", Toast.LENGTH_SHORT).show();
                        Limpiar_campos();

                    }
                }
            }

            db.close();
        }
    }//fin metodo guardar

    public void ConsultarVenta(View view) {
        codigo = jetcodigo.getText().toString();
        if (codigo.isEmpty()) {
            Toast.makeText(this, "Codigo de venta requerido para la BUSQUEDA", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        } else {

            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("select *" +
                    "from TblVenta inner join TblCliente on TblCliente.identificacion = TblVenta.identificacion " +
                    "inner join TblVehiculo on TblVehiculo.placa = TblVenta.placa where codigo='" + codigo + "'", null);

                    if(fila.moveToNext()) {
                        sw = 1;
                        if (fila.getString(4).equals("No")) {
                            Toast.makeText(this, "Venta Anulada", Toast.LENGTH_SHORT).show();
                        } else {
                            jetfecha.setText(fila.getString(1));
                            jetfecha.setEnabled(false);
                            jcbactivoventa.setChecked(true);
                            jetidentificacion.setText(fila.getString(5));
                            jetidentificacion.setEnabled(false);
                            jtvnombre.setText(fila.getString(6));
                            jtvcorreo.setText(fila.getString(7));
                            jetplaca.setText(fila.getString(9));
                            jetplaca.setEnabled(false);
                            jtvmarca.setText(fila.getString(11));
                            jtvmodelo.setText(fila.getString(10));
                            jbtanular.setEnabled(true);
                            Toast.makeText(this, "Venta existe", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else {
                        Toast.makeText(this, "Venta no registrada", Toast.LENGTH_SHORT).show();
                    }

                db.close();
        }
    } // fin metodo consultar venta

    public void ConsultarCliente(View view) {
        identificacion = jetidentificacion.getText().toString();
        if (identificacion.isEmpty()) {
            Toast.makeText(this, "Identificacion requerida para la busqueda", Toast.LENGTH_SHORT).show();
            jetidentificacion.requestFocus();
        } else {
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("select * from TblCliente where identificacion='" + identificacion + "'", null);
            if (fila.moveToNext()) {
                if (fila.getString(3).equals("No")) {
                    Toast.makeText(this, "Cliente existe pero esta desactivado", Toast.LENGTH_SHORT).show();
                } else {
                    jtvnombre.setText(fila.getString(1));
                    jtvcorreo.setText(fila.getString(2));
                    Toast.makeText(this, "Cliente existe", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Cliente no esta registrado", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
    }//Metodo consultar cliente

    public void ConsultarVehiculo(View view) {
        placa = jetplaca.getText().toString();
        if (placa.isEmpty()) {
            Toast.makeText(this, "Placa requerida para la busqueda", Toast.LENGTH_SHORT).show();
            jetplaca.requestFocus();
        } else {
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("select * from TblVehiculo where placa ='" + placa + "'", null);
            if (fila.moveToNext()) {
                if (fila.getString(3).equals("No")) {
                    Toast.makeText(this, "Vehiculo existe, pero esta desactivado", Toast.LENGTH_SHORT).show();
                    jtvmodelo.setText(fila.getString(1));
                    jtvmarca.setText(fila.getString(2));
                } else {
                    jtvmodelo.setText(fila.getString(1));
                    jtvmarca.setText(fila.getString(2));
                    Toast.makeText(this, "Vehiculo existe", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vehiculo no registrado", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
    } // fin metodo consultar vehiculo

    private void Limpiar_campos() {
        jetcodigo.setText("");
        jetfecha.setText("");
        jetfecha.setEnabled(true);
        jetidentificacion.setText("");
        jetidentificacion.setEnabled(true);
        jetplaca.setText("");
        jetplaca.setEnabled(true);
        jtvnombre.setText("");
        jtvcorreo.setText("");
        jtvmodelo.setText("");
        jtvmarca.setText("");
        jcbactivoventa.setText("");
        jcbactivoventa.setChecked(false);
        jbtanular.setEnabled(false);
        jetcodigo.requestFocus();
        sw=0;
    }

    public void Anular(View view) {
        if (sw == 1) {
            sw = 0;
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();

                registro.put("activo", "No");
                respuesta = db.update("TblVenta", registro, "codigo='" + codigo + "'", null);
                if (respuesta > 0) {
                    Toast.makeText(this, "Venta Anulada", Toast.LENGTH_SHORT).show();
                    Activar(placa);
                    Limpiar_campos();

                } else {
                    Toast.makeText(this, "Error Anulacion Venta", Toast.LENGTH_SHORT).show();
                    Limpiar_campos();
                }

            db.close();
        } else {
            Toast.makeText(this, "Debe primero realizar consultar", Toast.LENGTH_SHORT).show();
        }

    }

    public void Desactivar(String placa) { // activar vehiculo cuando desactiva la venta
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();

            registro.put("activo", "No");
            respuesta = db.update("TblVehiculo", registro, "placa='" + placa + "'", null);
            if (respuesta > 0) {
                //Toast.makeText(this, "Vehiculo desactivado", Toast.LENGTH_SHORT).show();
                Limpiar_campos();
            }
        db.close();
    }

    public void Activar(String placa) { //desactivar vehiculo cuando guarda la venta
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();

        registro.put("activo", "Si");
        respuesta = db.update("TblVehiculo", registro, "placa='" + placa + "'", null);
        if (respuesta > 0) {
            //Toast.makeText(this, "Vehiculo Activado", Toast.LENGTH_SHORT).show();
            Limpiar_campos();
        }
        db.close();
    }

    public void Regresar(View view) {
        Intent intmain = new Intent(this, MainActivity.class);
        startActivity(intmain);
    }

    public void Cancelar(View view) {
        Limpiar_campos();
    }// Fin Metodo limpiar


}