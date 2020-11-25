package com.asimplenerd.legobattlebots

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.*
import java.lang.Exception
import java.util.*

class BattleBot {
    private var id : Int = -1
    private var device : BluetoothDevice? = null
    private var bluetoothSocket : BluetoothSocket? = null
    private var outStream : OutputStream? = null
    private var inStream : InputStream? = null
    private var inStreamReader : BufferedReader? = null
    private var outStreamWriter : BufferedWriter? = null
    private var name : String = "BattleBot"
    private var weapon : String = "Sword"

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

    fun setWeapon(type : String){
        when (type.toLowerCase(Locale.US)){
            "axe" -> {this.weapon = "Axe"}
            "sword" -> {this.weapon = "Sword"}
            "lifter" -> {this.weapon = "Lifter"}
        }
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

    fun getDevice()  : BluetoothDevice? {
        return device
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
                    outStreamWriter?.write("DEVIDREQ\n")
                    var success = false
                    var resp = inStreamReader?.readLine()
                    var tempId = resp?.toIntOrNull()
                    var timeout = 20 //number of messages to parse before dc
                    while(tempId == null && timeout > 0){
                        resp = inStreamReader?.readLine()
                        tempId = resp?.toIntOrNull()
                        timeout--
                    }
                    if(timeout > 0) {
                        success = true
                        id = tempId!!
                    }
                    try {
                        inStreamReader?.close()
                        outStreamWriter?.close()
                        bluetoothSocket?.close()
                    }finally {
                        return success
                    }
                }
            }
            catch (ex : Exception){
                return false
            }
        }
        return false
    }

    fun connect(){
        if(bluetoothSocket == null){
            throw Exception("No socket has been created!")
        }
        bluetoothSocket!!.connect()
        inStreamReader = BufferedReader(bluetoothSocket!!.inputStream.reader())
        outStreamWriter = BufferedWriter(bluetoothSocket!!.outputStream.writer())
        outStreamWriter!!.write("HELLO")
        bluetoothSocket!!.close()
    }

}
