import 'package:ac_media_storage/ac_media_storage.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  MethodChannelMediaStorage platform = MethodChannelMediaStorage();
  const MethodChannel channel = MethodChannel('media_storage');

  final tester = TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    tester.defaultBinaryMessenger.setMockMethodCallHandler(channel, (MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    tester.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
