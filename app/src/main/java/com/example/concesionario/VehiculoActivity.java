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
import android.widget.Toast;

public class VehiculoActivity extends AppCompatActivity {

    EditText jetplaca,jetmarca,jetmodelo;
    CheckBox jcbactivov;
    String placa,marca,modelo;

    Button jbtanular;
    ClsOpenHelper admin=new ClsOpenHelper(this,"Concesionario.db",null,1);
    long respuesta;
    Byte sw;
    Byte activacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo);
        //Ocultar la barra de titulo por defecto y asociar objetos
        //Java con objetos Xml
        getSupportActionBar().hide();
        jetplaca=findViewById(R.id.etplaca);
        jetmarca=findViewById(R.id.etmarca);
        jetmodelo=findViewById(R.id.etmodelo);
        jcbactivov=findViewById(R.id.cbactivov);
        jbtanular=findViewById(R.id.btanular);
        sw=0;
        activacion = 0;
    }

    public void Guardar(View view){
        placa=jetplaca.getText().toString();
        marca=jetmarca.getText().toString();
        modelo=jetmodelo.getText().toString();
        if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty()){
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetplaca.requestFocus();
        }else{
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("placa",placa);
            registro.put("marca",marca);
            registro.put("modelo",modelo);

            if(sw==0){
            respuesta=db.insert("TblVehiculo",null,registro);
                if (respuesta == 0){
                    Toast.makeText(this, "Error guardando vehiculo", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Vehiculo guardado", Toast.LENGTH_SHORT).show();
                    Limpiar_campos();
                }
            }
            else{
                respuesta=db.update("TblVehiculo",registro,"placa='"+placa+"'",null);
                sw=0;
                if (respuesta == 0){
                    Toast.makeText(this, "Error modificando vehiculo", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Vehiculo modificado", Toast.LENGTH_SHORT).show();
                    Limpiar_campos();
                }
            }
            db.close();
        }
    }//fin metodo guardar

    public void Anular(View view){
        if (sw == 1){
            sw=0;

            SQLiteDatabase db=admin.getWritableDatabase();
            ContentValues registro=new ContentValues();

            if(activacion == 1){  // desactivar

                registro.put("activo","No");
                respuesta=db.update("TblVehiculo",registro,"placa='"+placa+"'",null);
                if (respuesta > 0){
                    Toast.makeText(this, "Vehiculo desactivado", Toast.LENGTH_SHORT).show();
                    activacion = 0;
                    Limpiar_campos();
                }else{
                    Toast.makeText(this, "Error desactivando vehiculo", Toast.LENGTH_SHORT).show();
                    Limpiar_campos();
                }
            }
            else{
                SQLiteDatabase bd = admin.getReadableDatabase();
                Cursor fila = bd.rawQuery("select * from TblVenta where placa ='" + placa + "' and activo = 'Si' ", null);
                if (fila.moveToNext()) {
                    Toast.makeText(this, "No se puede activar Vehiculo vendido", Toast.LENGTH_SHORT).show();
                    Limpiar_campos();
                }
                else {
                    registro.put("activo","Si"); // activar
                    respuesta=db.update("TblVehiculo",registro,"placa='"+placa+"'",null);
                    if (respuesta > 0){
                        Toast.makeText(this, "Vehiculo activado", Toast.LENGTH_SHORT).show();
                        activacion = 1;
                        Limpiar_campos();
                    }else{
                        Toast.makeText(this, "Error activando vehiculo", Toast.LENGTH_SHORT).show();
                        Limpiar_campos();
                    }
                }
            }
            db.close();
        }else{
            Toast.makeText(this, "Debe primero realizar consultar", Toast.LENGTH_SHORT).show();
        }
    }


    public void Consultar(View view) {
        placa = jetplaca.getText().toString();
        if(placa.isEmpty()){
            Toast.makeText(this, "Placa requerida para la busqueda", Toast.LENGTH_SHORT).show();
            jetplaca.requestFocus();
        }
        else{
            SQLiteDatabase db=admin.getReadableDatabase();
            Cursor fila = db.rawQuery("select * from TblVehiculo where placa ='"+ placa+ "'", null);

            if(fila.moveToNext()){
                sw=1;
                if (sw == 1){
                    jbtanular.setEnabled(true);
                }
                if (fila.getString(3).equals("No")){
                    Toast.makeText(this, "Vehiculo exite, pero esta desactivado", Toast.LENGTH_SHORT).show();
                    activacion = 0;
                }else{
                    jetmarca.setText(fila.getString(1));
                    jetmodelo.setText(fila.getString(2));
                    jcbactivov.setChecked(true);
                    activacion = 1;
                }
            }else{
                Toast.makeText(this, "Vehiculo no registrado", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
    } // fin metodo consultar

    private void Limpiar_campos() {
        jetplaca.setText("");
        jetmarca.setText("");
        jetmodelo.setText("");
        jcbactivov.setChecked(false);
        jbtanular.setEnabled(false);
        jetplaca.requestFocus();
        sw=0;
        activacion=0;
    }
    public void Regresar (View view)
    {
        Intent intmain = new Intent(this,MainActivity.class);
        startActivity(intmain);
    }

    public void Cancelar(View view){
        Limpiar_campos();
    }// Fin Metodo limpiar



    }


