import 'dart:convert';

import 'package:flutter/services.dart';

import 'media_storage_file.dart';
import 'media_storage_platform_interface.dart';

class MediaStorage {
  Future<String?> getPlatformVersion() {
    return MediaStoragePlatform.instance.getPlatformVersion();
  }

  static const MethodChannel _channel = MethodChannel('media_storage');

  @Deprecated('Use directoryMusic instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_MUSIC = directoryMusic;
  static const String directoryMusic = "Music";

  @Deprecated('Use directoryPodcasts instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_PODCASTS = directoryPodcasts;
  static const String directoryPodcasts = "Podcasts";

  @Deprecated('Use directoryRingtones instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_RINGTONES = directoryRingtones;
  static const String directoryRingtones = "Ringtones";

  @Deprecated('Use directoryAlarms instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_ALARMS = directoryAlarms;
  static const String directoryAlarms = "Alarms";

  @Deprecated('Use directoryNotifications instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_NOTIFICATIONS = directoryNotifications;
  static const String directoryNotifications = "Notifications";

  @Deprecated('Use directoryPictures instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_PICTURES = directoryPictures;
  static const String directoryPictures = "Pictures";

  @Deprecated('Use directoryMovies instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_MOVIES = directoryMovies;
  static const String directoryMovies = "Movies";

  @Deprecated('Use directoryDownloads instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_DOWNLOADS = directoryDownloads;
  static const String directoryDownloads = "Download";

  @Deprecated('Use directoryDcim instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_DCIM = directoryDcim;
  static const String directoryDcim = "DCIM";

  @Deprecated('Use directoryDocuments instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_DOCUMENTS = directoryDocuments;
  static const String directoryDocuments = "Documents";

  @Deprecated('Use directoryScreenShots instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_SCREENSHOTS = directoryScreenShots;
  static const String directoryScreenShots = "Screenshots";

  @Deprecated('Use directoryAudiobooks instead')
  // ignore: constant_identifier_names
  static const String DIRECTORY_AUDIOBOOKS = directoryAudiobooks;
  static const String directoryAudiobooks = "Audiobooks";

  static Future<List<String>> getExternalStorageDirectories() async {
    final List externalStorageDirs = await _channel.invokeMethod('getExternalStorageDirectories');

    List<String> storageInfos = externalStorageDirs.map((storageInfoMap) => ExStoragePath01.getRootDir(storageInfoMap)).toList();
    return storageInfos;
  }

  static Future<List<MediaStorageFile>> getMediaStoreData(String path) async {
    final String externalStorageDirs = await _channel.invokeMethod('getMediaStoreData', {'path': path});
    if (externalStorageDirs.isEmpty) {
      return <MediaStorageFile>[];
    } else {
      final list = json.decode(externalStorageDirs);
      return list.map((item) => MediaStorageFile.fromJSON(item)).toList();
    }
  }

  @Deprecated('Use createDirectory(String path, String folderName) instead')
  // ignore: non_constant_identifier_names
  static Future<String> CreateDir(String path, String folderName) async {
    return createDirectory(path, folderName);
  }

  static Future<String> createDirectory(String path, String folderName) async {
    final String externalStorageDirs = await _channel.invokeMethod('createDirectory', {'createfolderpath': path, 'foldername': folderName});
    return externalStorageDirs;
  }

  static Future<String> getExternalStoragePublicDirectory(String type) async {
    final String externalPublicDir = await _channel.invokeMethod('getExternalStoragePublicDirectory', {'type': type});
    return externalPublicDir;
  }

  @Deprecated('Use deleteFile(String path) instead')
  // ignore: non_constant_identifier_names
  static Future<bool> deletefile(String path) async {
    return deleteFile(path);
  }

  static Future<bool> deleteFile(String path) async {
    final bool externalPublicDir = await _channel.invokeMethod('deleteFile', {'deletepath': path});
    return externalPublicDir;
  }

  /*static Future<bool> deleteDir(String deleteDirpath) async {
    final bool externalPublicDir = await _channel
        .invokeMethod('deleteDir', {'deleteDirpath': deleteDirpath});
    return externalPublicDir;
  }*/

  static Future<bool> getRequestStoragePermission() async {
    final bool isPermission = await _channel.invokeMethod('getRequestStoragePermission');
    return isPermission;
  }
}

class ExStoragePath01 {
  static String getRootDir(String appFilesDir) {
    return appFilesDir.split("/").sublist(0, appFilesDir.split("/").length - 4).join("/");
  }
}
