# media_storage

media_storage is a flutter plugin that provides internal, external storage path and external public storage path.



## Package name change

The original package [*media_storage*](https://pub.dev/packages/media_storage)
is still available on pub.dev but seems not to be maintained. There's my PR to
fix lateinit but not accepted yet.

That's why I published my version as [*ac_media_storage*](https://pub.dev/packages/ac_media_storage).

## Features

`MediaStorage.getExternalStoragePublicDirectory()` needs Public Directory Type argument
Below given table contains the types of argument you can pass to `getExternalStoragePublicDirectory()` function

| MediaStorage                        |
|-------------------------------------|
| MediaStorage.directoryMusic         |
| MediaStorage.directoryPodcasts      |
| MediaStorage.directoryRingtones     |
| MediaStorage.directoryAlarms        |
| MediaStorage.directoryNotifications |
| MediaStorage.directoryPictures      |
| MediaStorage.directoryMovies        |
| MediaStorage.directoryDownloads     |
| MediaStorage.directoryDcim          |
| MediaStorage.directoryDocuments     |
| MediaStorage.directoryScreenshots   |
| MediaStorage.directoryAudiobooks    |

## Usage

First Add `ac_media_storage` as a dependency in your project `pubspec.yaml`.

Then, import `ac_media_storage` package.

```dart
import 'package:ac_media_storage/ac_media_storage.dart';
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
    var path = await MediaStorage.getExternalStoragePublicDirectory(MediaStorage.directoryDownloads);
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