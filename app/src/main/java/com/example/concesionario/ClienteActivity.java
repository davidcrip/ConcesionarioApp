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

public class ClienteActivity extends AppCompatActivity {

    EditText jetidentificacion,jetnombre,jetcorreo;
    CheckBox jcbactivo;
    String identificacion,nombre,correo;

    Button jbtanular;
    ClsOpenHelper admin=new ClsOpenHelper(this,"Concesionario.db",null,1);
    long respuesta;
    Byte sw;
    Byte activacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        //Ocultar la barra de titulo por defecto y asociar objetos
        //Java con objetos Xml
        getSupportActionBar().hide();
        jetidentificacion=findViewById(R.id.etidentificacion);
        jetnombre=findViewById(R.id.etnombre);
        jetcorreo=findViewById(R.id.etcorreo);
        jcbactivo=findViewById(R.id.cbactivo);
        jbtanular = findViewById(R.id.btanular );
        sw=0;
        activacion = 0;
    }



    public void Guardar(View view){
        identificacion=jetidentificacion.getText().toString();
        nombre=jetnombre.getText().toString();
        correo=jetcorreo.getText().toString();
        if (identificacion.isEmpty() || nombre.isEmpty() || correo.isEmpty()){
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetidentificacion.requestFocus();
        }else{
            SQLiteDatabase db=admin.getWritableDatabase();
            ContentValues registro=new ContentValues();
            registro.put("identificacion",identificacion);
            registro.put("nombre",nombre);
            registro.put("correo",correo);
            if (sw == 0) {
                respuesta = db.insert("TblCliente", null, registro);
                if (respuesta == 0) {
                    Toast.makeText(this, "Error guardando registro", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show();
                    Limpiar_campos();
                }
            }
            else{
                respuesta=db.update("TblCliente",registro,"identificacion='"+identificacion+"'",null);
                sw=0;
                if (respuesta == 0){
                    Toast.makeText(this, "Error modificando registro", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Registro modificado", Toast.LENGTH_SHORT).show();
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
                respuesta=db.update("TblCliente",registro,"identificacion='"+identificacion+"'",null);
                if (respuesta > 0){
                    Toast.makeText(this, "Cliente desactivado", Toast.LENGTH_SHORT).show();
                    activacion = 0;
                    Limpiar_campos();
                }else{
                    Toast.makeText(this, "Error desactivando Cliente", Toast.LENGTH_SHORT).show();
                    Limpiar_campos();
                }
            }
            else{
                registro.put("activo","Si"); // activar
                respuesta=db.update("TblCliente",registro,"identificacion='"+identificacion+"'",null);
                if (respuesta > 0){
                    Toast.makeText(this, "Cliente activado", Toast.LENGTH_SHORT).show();
                    activacion = 1;
                    Limpiar_campos();
                }else{
                    Toast.makeText(this, "Error activando cliente", Toast.LENGTH_SHORT).show();
                    Limpiar_campos();
                }
            }
            db.close();
        }else{
            Toast.makeText(this, "Debe primero realizar consultar", Toast.LENGTH_SHORT).show();
        }
    }

    public void Consultar(View view){
        identificacion=jetidentificacion.getText().toString();
        if (identificacion.isEmpty()){
            Toast.makeText(this, "Identificacion requerida para la busqueda", Toast.LENGTH_SHORT).show();
            jetidentificacion.requestFocus();
        }else{
            SQLiteDatabase db=admin.getReadableDatabase();
            Cursor fila= db.rawQuery("select * from TblCliente where identificacion='"+identificacion+"'",null);
            if (fila.moveToNext()){
                sw=1;
                if (sw == 1){
                jbtanular.setEnabled(true);
                }
                if (fila.getString(3).equals("No")){
                    Toast.makeText(this, "Cliente existe,pero esta anulado", Toast.LENGTH_SHORT).show();
                activacion = 0;
                }else{
                    jetnombre.setText(fila.getString(1));
                    jetcorreo.setText(fila.getString(2));
                    jcbactivo.setChecked(true);
                    activacion = 1;
                }
            }else{
                Toast.makeText(this, "Cliente no registrado", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
    }//Metodo consultar



    public void Regresar(View view){
        Intent intmain=new Intent(this,MainActivity.class);
        startActivity(intmain);
    }

    public void Cancelar(View view){
        Limpiar_campos();
    }//Metodo Cancelar

    private void Limpiar_campos(){
        jetidentificacion.setText("");
        jetnombre.setText("");
        jetcorreo.setText("");
        jcbactivo.setChecked(false);
        jbtanular.setEnabled(false);
        jetidentificacion.requestFocus();
        sw=0;
        activacion=0;
    }
}