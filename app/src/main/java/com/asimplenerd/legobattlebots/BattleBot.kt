package com.asimplenerd.legobattlebots

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.CountDownTimer
import android.util.Log
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private var isConnected = false
    private var isInBattle = false
    private var health = 1.0
    private var armorCount = 0
    private var timer = object : CountDownTimer(10000, 100){

        override fun onTick(millisUntilFinished: Long) {
            if(bluetoothSocket?.isConnected!!){
                this.onFinish()
            }
        }

        override fun onFinish() {
            isConnected = bluetoothSocket?.isConnected!!
            if(!isConnected){
                bluetoothSocket?.close() //Not connected within the time limit.
            }
        }

    }

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
        sendDataToBot("weapon: ${this.weapon}")
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
        if((bluetoothSocket == null && device != null) || !isConnected){
            bluetoothSocket = device!!.createRfcommSocketToServiceRecord(BATTLEBOT_UUID)
            if(bluetoothSocket == null)
                return false
        }
        else if(device == null)
            return false
        try{
            bluetoothSocket?.connect()
        }
        catch(ex : Exception){
            ex.printStackTrace()
            return false
        }
        if(bluetoothSocket?.isConnected!!) {
            inStreamReader = BufferedReader(bluetoothSocket!!.inputStream.reader())
            outStreamWriter = BufferedWriter(bluetoothSocket!!.outputStream.writer())
            outStream = bluetoothSocket!!.outputStream
            try {
                Log.i("Recv BLUETOOTH", "GOT: ${inStreamReader!!.readLine()}")
                val msg = ByteArray(1024)
                strToByteArr("Hello, world!", msg)
                bluetoothSocket!!.outputStream.write(msg)
                readDataFromBluetooth()
            }
            catch (ex : Exception){ return false }
            return true
        }
        bluetoothSocket = null
        outStream = null
        inStream = null
        return false
    }

    fun readDataFromBluetooth() {
        GlobalScope.launch {
            while (bluetoothSocket!!.isConnected && inStreamReader != null) {
                try {
                    val updateData = inStreamReader!!.readLine().trim().split(' ')
                    when (updateData[0]) {
                        "ArmorStatus:" -> {
                            Log.i("ARMOR", "received update")
                            val armorList = updateData[1].split(':')

                            var tempHealth = (armorList[0].toInt() + armorList[1].toInt() + armorList[2].toInt()) / 3.0
                            if(!isInBattle || (isInBattle && tempHealth < health)) {
                                health = tempHealth
                                Log.d("HealthChange", "Health set to $health")
                            }
                        }
                        else -> {
                            Log.i("INFO", "${updateData[0]}")
                        }
                    }
                }catch(ex : Exception){
                    break //Client lost connection
                }
            }
        }
    }

    fun isConnected() : Boolean {
        return bluetoothSocket?.isConnected!!
    }

    fun sendDataToBot(data : String){
        GlobalScope.launch {
            if (outStream != null) {
                val msg = ByteArray(256)
                strToByteArr(data, msg)
                outStream?.write(msg)
            }
        }
    }

    fun enterCombat(){
        this.isInBattle = true
    }

    fun exitCombat(){
        this.isInBattle = false
    }

    private fun strToByteArr(str : String, arr : ByteArray){
        var i = 0;
        while(i < str.length){
            arr[i] = str.get(i).toByte()
            i++
        }
        arr[str.length] = 0.toChar().toByte()
    }

    fun getRemainingHealthPercentage() : Double{
        return health
    }

    fun disconnect(){
        if(bluetoothSocket?.isConnected!!) {
            inStream?.close()
            outStream?.close()
            inStreamReader?.close()
            outStreamWriter?.close()
            bluetoothSocket!!.close()
            this.bluetoothSocket = null
        }
    }

}
