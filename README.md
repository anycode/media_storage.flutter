# media_storage


media_storage is a flutter plugin that provides internal, external storage path and external public storage path.

https://pub.dev/packages/media_storage

## Features

`MediaStorage.getExternalStoragePublicDirectory()` needs Public Directory Type argument
Below given table contains the types of argument you can pass to `getExternalStoragePublicDirectory()` function

| MediaStorage                         |
| ------------------------------------ |
| MediaStorage.DIRECTORY_MUSIC         |
| MediaStorage.DIRECTORY_PODCASTS      |
| MediaStorage.DIRECTORY_RINGTONES     |
| MediaStorage.DIRECTORY_ALARMS        |
| MediaStorage.DIRECTORY_NOTIFICATIONS |
| MediaStorage.DIRECTORY_PICTURES      |
| MediaStorage.DIRECTORY_MOVIES        |
| MediaStorage.DIRECTORY_DOWNLOADS     |
| MediaStorage.DIRECTORY_DCIM          |
| MediaStorage.DIRECTORY_DOCUMENTS     |
| MediaStorage.DIRECTORY_SCREENSHOTS   |
| MediaStorage.DIRECTORY_AUDIOBOOKS    |

## Usage

First Add `media_storage` as a dipendency in your project `pubspeck.yaml`.

Then, import `media_storage` package.

```dart
import 'package:media_storage/media_storage.dart';
```

Package has these functions

```dart

  // Get storage storage permission in all devices
  Future<bool> getPermission() async {
    bool isPermission = await MediaStorage.getRequestStoragePermission();
    print(isPermission);  // true or false
    return isPermission;
  }
  
  // Get storage directory paths
  Future<void> getPath_1() async {
    var path = await MediaStorage.getExternalStorageDirectory("");
    print(path);  // /storage/emulated/0

  }


  // To get public storage directory path
  Future<void> getPath_2() async {
    var path = await MediaStorage.getExternalStoragePublicDirectory(MediaStorage.DIRECTORY_DOWNLOADS);
    print(path);  // /storage/emulated/0/Download
  }
```

```dart

  Future<List<AllMedia>> getPathFiles() async {
    var path = await MediaStorage.getExternalStoragePublicDirectory("");
    bool isPermission = await MediaStorage.getRequestStoragePermission();
    if(isPermission){
      // To get all or specific Directory files list with all file informations.
      List<dynamic>  _exPath = await MediaStorage.getMediaStoreData(path);
      _filelist.clear();
      AllMedia allMedia = AllMedia();
      for(var i=0; i<_exPath.length; i++ ){
        allMedia = AllMedia.fromJSON(_exPath[i]);
        _filelist.add(allMedia);
      }
    }else{
      await MediaStorage.getRequestStoragePermission();
    }
    return _filelist;
  }
```