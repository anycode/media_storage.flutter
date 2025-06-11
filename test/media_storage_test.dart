import 'package:ac_media_storage/ac_media_storage.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockMediaStoragePlatform 
    with MockPlatformInterfaceMixin
    implements MediaStoragePlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final MediaStoragePlatform initialPlatform = MediaStoragePlatform.instance;
  TestWidgetsFlutterBinding.ensureInitialized();

  test('$MethodChannelMediaStorage is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelMediaStorage>());
  });

  test('getPlatformVersion', () async {
    MediaStorage mediaStoragePlugin = MediaStorage();
    MockMediaStoragePlatform fakePlatform = MockMediaStoragePlatform();
    MediaStoragePlatform.instance = fakePlatform;
  
    expect(await mediaStoragePlugin.getPlatformVersion(), '42');
  });

  test('create directory', () async {
    MediaStorage mediaStoragePlugin = MediaStorage();
    MockMediaStoragePlatform fakePlatform = MockMediaStoragePlatform();
    MediaStoragePlatform.instance = fakePlatform;

    expect(await MediaStorage.createDirectory(MediaStorage.directoryDownloads, "test"), "test");
  });
}
