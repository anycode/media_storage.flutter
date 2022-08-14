package com.media.storage.media_storage

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import android.content.Context
import java.io.File
import android.os.Environment
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import com.media.storage.media_storage.MethodCallHandlerImpl
import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.lifecycle.Observer
import com.media.storage.media_storage.Object.Model_images



/** MediaStoragePlugin */
class MediaStoragePlugin: FlutterPlugin,ActivityAware {

  private var activityBinding: ActivityPluginBinding? = null
  private lateinit var methodCallHandler: MethodCallHandlerImpl
  private lateinit var activity: Activity



  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {

    methodCallHandler = MethodCallHandlerImpl(binding.applicationContext)
    methodCallHandler.init(binding.binaryMessenger)


  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    if (::methodCallHandler.isInitialized) {
      methodCallHandler.dispose()
    }
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {

    activity = binding.activity
    methodCallHandler.setActivity(binding.activity)
    binding.addActivityResultListener(methodCallHandler)
    activityBinding = binding
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivity() {
    activityBinding?.removeActivityResultListener(methodCallHandler)
    activityBinding = null
    methodCallHandler.setActivity(null)
  }

}
