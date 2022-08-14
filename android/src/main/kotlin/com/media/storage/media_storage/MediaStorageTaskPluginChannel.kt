package com.media.storage.media_storage

import android.app.Activity
import io.flutter.plugin.common.BinaryMessenger

/** FlutterForegroundTaskPluginChannel */
interface MediaStorageTaskPluginChannel {
	fun init(messenger: BinaryMessenger)
	fun setActivity(activity: Activity?)
	fun dispose()
}
