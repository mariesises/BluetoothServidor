package com.example.bluetoothservidor;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    CheckBox botonhabilitado, botonvisible;
    TextView nombre_bt;

    //Declaro un adaptador de bluetooth para comprobar si el servicio Bluetooth esta disponible en el dispositivo.
    //Dentro del archivo Manifest hay que declarar dos permisos adecuados para poder usar el Bluetooth.
    private BluetoothAdapter adaptadorBluetooth;

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonhabilitado = findViewById(R.id.botonhabilitado);
        botonvisible = findViewById(R.id.botonvisible);
        nombre_bt = findViewById(R.id.nombrebluetooth);

        //mediante este metodo mostramos el nombre del dispositivo Bluetooth
        nombre_bt.setText(getLocalBluetoothName());

        //Si no es soportado,no se realiza nada
        if (adaptadorBluetooth == null) {
            Toast.makeText(this, "Bluetooth no soportado", Toast.LENGTH_SHORT).show();
            finish();
        }

        //Si esta habilitado el checkbox se marca
        if (adaptadorBluetooth.isEnabled()) {
            botonhabilitado.setChecked(true);
        } else {
            botonhabilitado.setChecked(false);
        }

        /**
         * Implementar el metodo OnCheckedListener para mostrar que hacer en cada caso
         * Cuando no está seleccionado el checkbox quiere decir que el bluetooth está apagado(llama al metodo) y muestra un mensaje
         * Si no realiza un intent en el que busca otra vez la accion para habilitar el bluetooth de nuevo y muestra otro mensaje
         * Para esto hay que añadir el permiso "android.permission.BLUETOOTH_CONNECT" al archivo Manifest
         */
        botonhabilitado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton vistaBoton, boolean seleccionado) {
                if (!seleccionado) {
                    adaptadorBluetooth.disable();
                    Toast.makeText(MainActivity.this, "Apagado", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentencendido = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    //startActivityForResult(intentencendido, 0);
                    ActivityResult.launch(intentencendido);
                    Toast.makeText(MainActivity.this, "Encendido", Toast.LENGTH_SHORT).show();



                }
            }

            ActivityResultLauncher<Intent> ActivityResult = registerForActivityResult(

                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<androidx.activity.result.ActivityResult>() {
                        //Se implementa el método onActivityResult
                        @Override
                        @SuppressLint("LongLogTag")
                        public void onActivityResult(ActivityResult result) {
                            //Se comprueba que el código del resultado sea correcto
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Log.d(TAG, "El usuario ha aceptado la peticion");
                            } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                                Log.d(TAG, "Error o " + "El usuario ha rechazado");
                            }
                        }
                    });
        });

        /**
         * En caso del checkbox visible, indica si el dispositivo bluetooth esta visible para otros dispositivos
         * Para que esta accion funcione hay que añadir el permiso "android.permission.BLUETOOTH_ADVERTISE" al archivo Manifest
         */
        botonvisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton vistaBoton, boolean seleccionado) {
                if (seleccionado) {
                    Intent volversevisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    //startActivityForResult(volversevisible, 0);
                    startActivity(volversevisible);
                    Toast.makeText(MainActivity.this, "Visible por 2 minutos", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }


    //Metodo para recoger el Nombre Bluetooth Local y mostrarlo por pantalla
    public String getLocalBluetoothName() {
        if (adaptadorBluetooth == null) {
            adaptadorBluetooth = BluetoothAdapter.getDefaultAdapter();
        }
        String nombre = adaptadorBluetooth.getName();
        if (nombre == null) {
            nombre = adaptadorBluetooth.getAddress();
        }
        return nombre;
    }

}