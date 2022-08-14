
import 'dart:convert';

import 'package:flutter/services.dart';

import 'media_storage_platform_interface.dart';

class MediaStorage {
  Future<String?> getPlatformVersion() {
    return MediaStoragePlatform.instance.getPlatformVersion();
  }

  static const MethodChannel _channel = const MethodChannel('media_storage');

  static final String DIRECTORY_MUSIC = "Music";
  static final String DIRECTORY_PODCASTS = "Podcasts";
  static final String DIRECTORY_RINGTONES = "Ringtones";
  static final String DIRECTORY_ALARMS = "Alarms";
  static final String DIRECTORY_NOTIFICATIONS = "Notifications";
  static final String DIRECTORY_PICTURES = "Pictures";
  static final String DIRECTORY_MOVIES = "Movies";
  static final String DIRECTORY_DOWNLOADS = "Download";
  static final String DIRECTORY_DCIM = "DCIM";
  static final String DIRECTORY_DOCUMENTS = "Documents";
  static final String DIRECTORY_SCREENSHOTS = "Screenshots";
  static final String DIRECTORY_AUDIOBOOKS = "Audiobooks";

  static Future<List<String>> getExternalStorageDirectories() async {
    final List externalStorageDirs =
    await _channel.invokeMethod('getExternalStorageDirectories');

    List<String> storageInfos = externalStorageDirs
        .map((storageInfoMap) => ExStoragePath01.getRootDir(storageInfoMap))
        .toList();
    return storageInfos;
  }

 static Future<List<dynamic>> getMediaStoreData(String path) async {
   List<dynamic> list = [];
    final String externalStorageDirs = await _channel.invokeMethod('getMediaStoreData', {'path': path});
    if(externalStorageDirs.isEmpty ){
      return  list;
    }else{
       list = json.decode(externalStorageDirs);
    }
    // List<dynamic> imageinfopath =  externalStorageDirs.map((e) => e.toString()).toList();
    return list;
  }
  static Future<String> CreateDir(String path,String foldername) async {
    final String externalStorageDirs = await _channel.invokeMethod('createDirectory', {'createfolderpath': path,'foldername': foldername});

    return externalStorageDirs;
  }

  static Future<String> getExternalStoragePublicDirectory(String type) async {
    final String externalPublicDir = await _channel
        .invokeMethod('getExternalStoragePublicDirectory', {'type': type});
    return externalPublicDir;
  }

  static Future<bool> deletefile(String deletepath) async {
    final bool externalPublicDir = await _channel
        .invokeMethod('deleteFile', {'deletepath': deletepath});
    return externalPublicDir;
  }
  /*static Future<bool> deleteDir(String deleteDirpath) async {
    final bool externalPublicDir = await _channel
        .invokeMethod('deleteDir', {'deleteDirpath': deleteDirpath});
    return externalPublicDir;
  }*/

  static Future<bool> getRequestStoragePermission() async {
    final bool isPermission = await _channel
        .invokeMethod('getRequestStoragePermission');
    return isPermission;
  }
}

class ExStoragePath01 {
  static String getRootDir(String appFilesDir) {
    return appFilesDir
        .split("/")
        .sublist(0, appFilesDir.split("/").length - 4)
        .join("/");
  }
}


