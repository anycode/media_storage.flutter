import 'package:flutter_test/flutter_test.dart';
import 'package:media_storage/media_storage.dart';
import 'package:media_storage/media_storage_platform_interface.dart';
import 'package:media_storage/media_storage_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockMediaStoragePlatform 
    with MockPlatformInterfaceMixin
    implements MediaStoragePlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final MediaStoragePlatform initialPlatform = MediaStoragePlatform.instance;

  test('$MethodChannelMediaStorage is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelMediaStorage>());
  });

  test('getPlatformVersion', () async {
    MediaStorage mediaStoragePlugin = MediaStorage();
    MockMediaStoragePlatform fakePlatform = MockMediaStoragePlatform();
    MediaStoragePlatform.instance = fakePlatform;
  
    expect(await mediaStoragePlugin.getPlatformVersion(), '42');
  });
}
