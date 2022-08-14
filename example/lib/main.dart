
import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:media_storage/media_storage.dart';
import 'package:media_storage_example/AllMedia.dart';

void main() {
  runApp( MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<AllMedia> _filelist = [];
  String path = "";
  @override
  void initState() {
    super.initState();

    getPath();

    // getPublicDirectoryPath();
  }

  // Get storage directory paths
  // Like internal and external (SD card) storage path
  Future<List<AllMedia>> getPath() async {
    // getExternalStorageDirectories() will return list containing internal storage directory path
    // And external storage (SD card) directory path (if exists)

   path = await MediaStorage.getExternalStoragePublicDirectory("");




   print("delete_media  ${_filelist.length}");

  print("download_path $path");
    bool isPermission = await MediaStorage.getRequestStoragePermission();
    if(isPermission){
      List<dynamic>  _exPath = await MediaStorage.getMediaStoreData(path);
      print("deleted_filepath  ${_exPath.length}");
      // List<dynamic> list = json.decode(fileDetails);
      _filelist.clear();
      AllMedia allMedia = AllMedia();
      for(var i=0; i<_exPath.length; i++ ){
        allMedia = AllMedia.fromJSON(_exPath[i]);
        _filelist.add(allMedia);
      }
      print("Permission_log   ${_filelist.length}");
    }else{
      await MediaStorage.getRequestStoragePermission();
    }


    setState(() {

    });

    return _filelist;
  }

  // To get public storage directory path like Downloads, Picture, Movie etc.
  // Use below code
  // Future<void> getPublicDirectoryPath() async {
  //   String path;
  //
  //   path = await MediaStorage.getExternalStoragePublicDirectory(
  //       MediaStorage.DIRECTORY_DOWNLOADS);
  //
  //   setState(() {
  //     print(path); // /storage/emulated/0/Download
  //   });
  // }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        home: Scaffold(
          appBar: AppBar(
            title: const Text('Plugin example app'),
          ),
          body: ListView.builder(
              scrollDirection: Axis.vertical,
              itemCount: _filelist.length,
              itemBuilder: (context, index) {
                return InkWell(
                    onTap: () async {
                      bool isDeleted = await MediaStorage.deletefile(_filelist[index].filepath);
                      if(isDeleted){
                        _filelist =  await getPath();
                      }

                      print("delete_media  $isDeleted");


                    },
                    child:Padding(
                        padding: EdgeInsets.symmetric(vertical: 10),
                        child: Text(_filelist[index].displayName)));
              }),
            floatingActionButton: FloatingActionButton(
            elevation: 0.0,
            child: new Icon(Icons.add),
            backgroundColor: new Color(0xFFE57373),
            onPressed: () async{
              _filelist =  await getPath();
            }
        ),


        ));
  }
}
