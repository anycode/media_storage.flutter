import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:media_storage/media_storage_method_channel.dart';

void main() {
  MethodChannelMediaStorage platform = MethodChannelMediaStorage();
  const MethodChannel channel = MethodChannel('media_storage');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
