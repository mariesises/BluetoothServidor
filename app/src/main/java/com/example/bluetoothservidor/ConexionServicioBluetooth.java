package com.example.bluetoothservidor;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConexionServicioBluetooth {

    private static final String TAG = "ConexionServicioBluetooth";

    private static final String nombreapp = "nombreapp";

    private static final UUID miUUid = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private AceptarHilo miaceptarhilo;

    private ConectarHilo miconexionhilo;
    private BluetoothDevice midispositivo;
    private UUID dispositivoUUID;

    private final BluetoothAdapter adaptadorBluetooth;
    Context micontexto;

    public ConexionServicioBluetooth(Context context) {
        micontexto = context;
        adaptadorBluetooth = BluetoothAdapter.getDefaultAdapter();
    }

    //Hilo que va a estar siempre abierto hasta que acepte una conexion o sea cancelado
    private class AceptarHilo extends Thread {
        //El servidor local socket
        private final BluetoothServerSocket servidorsocket;

        @SuppressLint("LongLogTag")
        public AceptarHilo() {
            BluetoothServerSocket tmp = null;

            //Creamos un nuevo escuchador servidor socket

            try {
                tmp = adaptadorBluetooth.listenUsingInsecureRfcommWithServiceRecord(nombreapp, miUUid);
                Log.d(TAG, "Hilo aceptado : Usando servidor: " + miUUid);
            } catch (IOException e) {
                e.printStackTrace();
            }

            servidorsocket = tmp;
        }

        @SuppressLint("LongLogTag")
        public void iniciar() {
            Log.d(TAG, "AceptarHilo ejecutandose");

            BluetoothSocket socket = null;

            try {
                //Esto es un bloqueo de la llamada y solo devolverá una conexion satisfactoria o una excepcion
                Log.d(TAG, "Ejecutandose: Conexion con el servidor iniciandose...");

                socket = servidorsocket.accept();

                Log.d(TAG, "Ejecutandose: Conexion con el servidor aceptada");

            } catch (IOException e) {
                Log.e(TAG, "AceptarHilo: IOException" + e.getMessage());
            }
        }

        @SuppressLint("LongLogTag")
        public void cancelar(){
            Log.d(TAG, "Cancelando AceptarHilo");
            try{
                servidorsocket.close();
            }catch (IOException e){
                Log.e(TAG, "cancelar: Cerrando AceptarHilo por error " + e.getMessage());
            }
        }
    }

    /**
     * Este hilo se ejecuta al intentar hacer una conexión saliente con un dispositivo
     * Se ejecuta directamente a través de la conexión, ya sea con éxito o si falla.
     */

    private class ConectarHilo extends Thread{
        private  BluetoothSocket misocket;

        @SuppressLint("LongLogTag")
        public ConectarHilo(BluetoothDevice dispositivo, UUID uuid){
            Log.d(TAG, "Conectando Hilo: Iniciando");
            midispositivo = dispositivo;
            dispositivoUUID = uuid;
        }

        @SuppressLint("LongLogTag")
        public void iniciar(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "Ejecutando ConectarHilo");

            //Coge un BluetoothSocket para una conexion con el dispositivo Bluetooth
            try{
                Log.d(TAG, "ConectarHilo: Intentando crear una conexion usando UUID: " + miUUid);
                tmp = midispositivo.createRfcommSocketToServiceRecord(dispositivoUUID);
            }catch(IOException e){
                Log.e(TAG, "ConectarHilo: No se pudo crear una conexion con el socket" + e.getMessage());
            }

            misocket = tmp;
            //Cancelar siempre el descubrimiento porque ralentizará la conexión
            adaptadorBluetooth.cancelDiscovery();

            // Hacer una conexion el Bluetooth

            try {
                //Esto es un bloqueo de la llamada y solo devolverá una conexion satisfactoria o una excepcion
                misocket.connect();

                Log.d(TAG, "Iniciar: ConectarHilo conectado");
            } catch (IOException e) {
                // Close the socket
                try {
                    misocket.close();
                    Log.d(TAG, "Iniciar: Socket cerrado");
                } catch (IOException e1) {
                    Log.e(TAG, "ConexionHilo: Imposible cerrar conexion con el socket " + e1.getMessage());
                }
                Log.d(TAG, "ConexionHilo: No se puede conectar con el UUID:  " + miUUid );
            }

        }
    }
}
