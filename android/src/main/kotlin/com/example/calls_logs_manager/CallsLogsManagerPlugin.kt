package com.example.calls_logs_manager

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.provider.CallLog
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.util.*


class CallsLogsManagerPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private val permission: String = Manifest.permission.READ_CALL_LOG
    private val requestCode: Int = 1
    private var activityPluginBinding: ActivityPluginBinding? = null
    private var activity: Activity? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "calls_logs_manager")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        Log.d("channel", "on request")
        if (call.method == "getLogs") {
            if (activity?.let { ContextCompat.checkSelfPermission(it, permission) }
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("MY_APP", "request permission")
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(permission),
                        requestCode
                    )
                }
            }
            Log.d("channel", "getLogs")
            val logs = readCallLog()
            result.success(logs)

        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.activityPluginBinding = binding
        this.activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {}

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}

    override fun onDetachedFromActivity() {
        activityPluginBinding = null
        activity = null
    }
    private fun readCallLog(): ArrayList<HashMap<String, String>> {
        val numberCol = CallLog.Calls.NUMBER
        val durationCol = CallLog.Calls.DURATION
        val typeCol = CallLog.Calls.TYPE // 1 - Incoming, 2 - Outgoing, 3 - Missed
        val dateCol = CallLog.Calls.DATE
        Log.d("inReadCallLog", "start getting logs")
        val projection = arrayOf(numberCol, durationCol, typeCol, dateCol)
//        val querySelection = " ?> '1656876596646'"
//        val selectionArgs = arrayOf(dateCol)


        val cal: Calendar = Calendar.getInstance()
        cal.set(2022, Calendar.JULY, 4, 12, 0, 0)
        val date: Date = cal.time

        val querySelection = CallLog.Calls.DATE + ">?"
        val selectionArgs = arrayOf(date.time.toString())

        Log.d("lower than O", "lower than O")

        val cursor = activity?.contentResolver?.query(
            CallLog.Calls.CONTENT_URI,
            projection, querySelection, selectionArgs, CallLog.Calls.DATE + " desc"
        )
        val numberColIdx = cursor!!.getColumnIndex(numberCol)
        val durationColIdx = cursor.getColumnIndex(durationCol)
        val typeColIdx = cursor.getColumnIndex(typeCol)
        val dateColIdx = cursor.getColumnIndex(dateCol)
        Log.d("inReadCallLog", "start getting logs")

        val logsList: ArrayList<HashMap<String, String>> = ArrayList()

        while (cursor.moveToNext()) {
            val number = cursor.getString(numberColIdx)
            val duration = cursor.getString(durationColIdx)
            val type = cursor.getString(typeColIdx)
            val date = cursor.getString(dateColIdx)
            Log.d("MY_APP", "$number $duration $type date : $date ")
            val logsMap: HashMap<String, String> = HashMap()

            logsMap[numberCol] = number
            logsMap[durationCol] = duration
            logsMap[typeCol] = type
            logsMap[dateCol] = date.toString()
            logsList.add(logsMap)
        }

        cursor.close()
        return logsList
    }


}


