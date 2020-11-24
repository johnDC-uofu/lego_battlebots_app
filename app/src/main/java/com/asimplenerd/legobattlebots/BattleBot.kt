package com.asimplenerd.legobattlebots

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.*
import java.lang.Exception

class BattleBot {
    private var id : Int = -1
    private var device : BluetoothDevice? = null
    private var bluetoothSocket : BluetoothSocket? = null
    private var outStream : OutputStream? = null
    private var inStream : InputStream? = null
    private var inStreamReader : BufferedReader? = null
    private var outStreamWriter : BufferedWriter? = null
    private var name : String = "BattleBot"

    constructor(id : Int){
        this.id = id
        device = null
    }

    constructor(id : Int, device: BluetoothDevice){
        this.id = id
        this.device = device
    }

    constructor(device: BluetoothDevice, name : String?){
        if(name != null)
            this.name = name
        this.device = device
    }

    fun setName(name : String){
        this.name = name
    }

    fun setBluetoothDevice(dev : BluetoothDevice){
        device = dev
    }

    fun setID(id : Int){
        this.id = id
    }

    fun hasBluetoothDevice() : Boolean {
        return device == null
    }

    fun getID() : Int{
        return id
    }

    fun retrieveID() : Boolean {
        if (device != null){
            try {
                if(bluetoothSocket == null){
                    //Initialize the socket
                    bluetoothSocket = device?.createRfcommSocketToServiceRecord(BATTLEBOT_UUID)
                }
                if(!bluetoothSocket?.isConnected!!){
                    bluetoothSocket?.connect()
                    outStream = bluetoothSocket?.outputStream
                    inStream = bluetoothSocket?.inputStream
                    if(inStream != null && outStream != null) {
                        inStreamReader = BufferedReader(InputStreamReader(inStream!!))
                        outStreamWriter = BufferedWriter(OutputStreamWriter(outStream!!))
                    }
                }
                //Sock is now connected
                if(inStream != null && outStream != null){
                    outStreamWriter?.write("DEVIDREQ")
                    var resp = inStreamReader?.readLine()
                    id = resp?.toInt()!!
                    return true
                }
            }
            catch (ex : Exception){
                Log.e("BLUETOOTH SOCK ERR", "${ex.printStackTrace()}")
                return false
            }
        }
        return false
    }

}
