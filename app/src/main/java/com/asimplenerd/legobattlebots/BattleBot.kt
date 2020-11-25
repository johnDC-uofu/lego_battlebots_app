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

    companion object{
        val BOT_IDENTIFIER = "BattleBot"
    }

    constructor(id : Int){
        this.id = id
        device = null
    }

    constructor(id : Int, device: BluetoothDevice){
        this.id = id
        this.device = device
    }

    constructor(device: BluetoothDevice, name : String?){
        if(name != null) {
            this.name = name
            val idTemp = name.substring(BOT_IDENTIFIER.length).toIntOrNull()
            Log.d("IDTEMP", idTemp.toString())
            this.setID(idTemp)
        }
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

    fun setID(id : Int?){
        Log.d("IDSET", "using $id")
        this.id = id ?: -1
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

    fun connect() : Boolean{
        if(bluetoothSocket == null && device != null){
            bluetoothSocket = device!!.createRfcommSocketToServiceRecord(BATTLEBOT_UUID)
            if(bluetoothSocket == null)
                return false
        }
        else if(device == null)
            return false
        try{bluetoothSocket?.connect()}
        catch(ex : Exception){
            ex.printStackTrace()
            return false
        }
        if(bluetoothSocket!!.isConnected) {
            inStreamReader = BufferedReader(bluetoothSocket!!.inputStream.reader())
            outStreamWriter = BufferedWriter(bluetoothSocket!!.outputStream.writer())
            try {
                Log.i("Recv BLUETOOTH", "GOT: ${inStreamReader!!.readLine()}")
                val msg = ByteArray(1024)
                strToByteArr("Hello, world!", msg)
                bluetoothSocket!!.outputStream.write(msg)
            }
            catch (ex : Exception){ return false }
            return true
        }
        return false
    }

    private fun strToByteArr(str : String, arr : ByteArray){
        var i = 0;
        while(i < str.length){
            arr[i] = str.get(i).toByte()
            i++
        }
        arr[str.length] = 0.toChar().toByte()
    }

    fun disconnect(){
        if(bluetoothSocket?.isConnected!!) {
            inStream?.close()
            outStream?.close()
            bluetoothSocket?.close()
            this.bluetoothSocket = null
        }
    }

}
