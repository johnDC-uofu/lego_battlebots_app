package com.asimplenerd.legobattlebots

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.CountDownTimer
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.util.*

class BattleBot(device: BluetoothDevice, name: String?) {
    private var id : Int = -1
    private var device : BluetoothDevice? = device
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
    private val timeoutMax = 10 //timeout in seconds
    private var timeoutCountDownTimer : CountDownTimer = object : CountDownTimer((timeoutMax * 1000).toLong(), 1000){
        override fun onTick(millisUntilFinished: Long) {
            isConnected = bluetoothSocket?.isConnected == true
        }

        override fun onFinish() {
            if(!isConnected){
                bluetoothSocket?.close()
            }
            this.cancel()
        }

    }

    companion object{
        const val BOT_IDENTIFIER = "BattleBot"
    }

    init {
        if(name != null) {
            this.name = name
            val idTemp = name.substring(BOT_IDENTIFIER.length).toIntOrNull()
            this.setID(idTemp)
        }
    }

    fun setWeapon(type : String){
        when (type.toLowerCase(Locale.US)){
            "axe" -> {this.weapon = "Axe"}
            "sword" -> {this.weapon = "Sword"}
            "lifter" -> {this.weapon = "Lifter"}
        }
        sendDataToBot("weapon: ${this.weapon}")
    }

    private fun setID(id : Int?){
        Log.d("IDSET", "using $id")
        this.id = id ?: -1
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
        timeoutCountDownTimer.start()
        try{
            bluetoothSocket?.connect()
        }
        catch(ex : Exception){
            timeoutCountDownTimer.cancel()
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

    private fun readDataFromBluetooth() {
        GlobalScope.launch {
            while (bluetoothSocket!!.isConnected && inStreamReader != null) {
                try {
                    val updateData = inStreamReader!!.readLine().trim().split(' ')
                    when (updateData[0]) {
                        "ArmorStatus:" -> {
                            Log.i("ARMOR", "received update")
                            val armorList = updateData[1].split(':')

                            val tempHealth = (armorList[0].toInt() + armorList[1].toInt() + armorList[2].toInt()) / 3.0
                            if(!isInBattle || (isInBattle && tempHealth < health)) {
                                health = tempHealth
                                Log.d("HealthChange", "Health set to $health")
                            }
                        }
                        else -> {
                            Log.i("INFO", updateData[0])
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
        val msg = ByteArray(256)
        GlobalScope.launch {
            if (outStream != null) {
                strToByteArr(data, msg)
            }
        }.invokeOnCompletion {
            if(msg.isNotEmpty()){
                try {
                    outStream?.write(msg)
                }catch(ex : IOException){
                    isConnected = false
                    bluetoothSocket?.close()
                }
            }
        }
    }

    fun enterCombat(){
        this.isInBattle = true
        if(bluetoothSocket?.isConnected == true){
            sendDataToBot("EnteringCombat")
        }
    }

    fun exitCombat(){
        this.isInBattle = false
        if(bluetoothSocket?.isConnected == true){
            sendDataToBot("ExitingCombat")
        }
    }

    private fun strToByteArr(str : String, arr : ByteArray){
        var i = 0
        while(i < str.length){
            arr[i] = str[i].toByte()
            i++
        }
        arr[str.length] = 0.toChar().toByte()
    }

    fun getRemainingHealthPercentage() : Double{
        return health
    }

    fun disconnect(){
        if(bluetoothSocket?.isConnected == true) {
            inStream?.close()
            outStream?.close()
            inStreamReader?.close()
            outStreamWriter?.close()
            bluetoothSocket!!.close()
            this.bluetoothSocket = null
        }
    }

}
