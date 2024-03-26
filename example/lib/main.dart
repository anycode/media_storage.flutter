import 'dart:async';

import 'package:flutter/material.dart';
import 'package:media_storage/media_storage.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String path = "";

  // Get storage directory paths
  // Like internal and external (SD card) storage path
  Future<List<MediaStorageFile>> listFiles() async {
    // getExternalStorageDirectories() will return list containing internal storage directory path
    // And external storage (SD card) directory path (if exists)

    final paths = await MediaStorage.getExternalStorageDirectories();
    debugPrint(paths.join(', '));

    path = await MediaStorage.getExternalStoragePublicDirectory(MediaStorage.directoryDownloads);

    debugPrint("list files path $path");
    bool isPermission = await MediaStorage.getRequestStoragePermission();
    if (isPermission) {
      final mediaFiles = await MediaStorage.getMediaStoreData(path);
      debugPrint("list files length  ${mediaFiles.length}");
      return mediaFiles;
    } else {
      await MediaStorage.getRequestStoragePermission();
      return [];
    }
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
        body: FutureBuilder<List<MediaStorageFile>>(
          future: listFiles(),
          builder: (context, snapshot) {
            if(! snapshot.hasData) {
              return Container();
            }
            final fileList = snapshot.data!;
            return ListView.builder(
              scrollDirection: Axis.vertical,
              itemCount: fileList.length,
              itemBuilder: (context, index) {
                return InkWell(
                  onTap: () async {
                    bool? deleted = await showDialog(
                      context: context,
                      builder: (context) => DeleteFileDialog(fileList[index].filepath),
                    );
                    if(deleted == true) {
                      setState(() {});
                    }
                  },
                  child: Padding(
                    padding: const EdgeInsets.symmetric(vertical: 10),
                    child: Text(fileList[index].displayName),
                  ),
                );
              },
            );
          }
        ),
        floatingActionButton: Builder(
          builder: (context) {
            return FloatingActionButton(
                elevation: 0.0,
                backgroundColor: const Color(0xFFE57373),
                onPressed: () async {
                  String? created = await showDialog(
                    context: context,
                    builder: (context) => CreateDirDialog(path),
                  );
                  if(created != null) {
                    setState(() {});
                  }
                },
                child: const Icon(Icons.add));
          }
        ),
      ),
    );
  }
}

class DeleteFileDialog extends StatelessWidget {
  final String filename;

  const DeleteFileDialog(this.filename, {super.key});

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Delete file'),
      content: Text('Do you really want to delete $filename?'),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(context).pop(false),
          child: const Text('Cancel'),
        ),
        FilledButton(
          onPressed: () async {
            bool isDeleted = await MediaStorage.deleteFile(filename);
            if (isDeleted) {
              Navigator.of(context).pop(true);
            }
            debugPrint("delete_media  $isDeleted");
          },
          child: const Text('Yes'),
        ),
      ],
    );
  }
}

class CreateDirDialog extends StatelessWidget {
  final String path;
  final TextEditingController _controller = TextEditingController();

  CreateDirDialog(this.path, {super.key});

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Create directory'),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text('Enter directory name to be created in $path'),
          TextField(controller: _controller),
        ],
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(context).pop(),
          child: const Text('Cancel'),
        ),
        FilledButton(
          onPressed: () async {
            String name = _controller.text;
            final dir = await MediaStorage.createDirectory(path, name);
            debugPrint("create_media  $dir");
            Navigator.of(context).pop(dir);
          },
          child: const Text('Yes'),
        ),
      ],
    );
  }
}
