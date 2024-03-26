import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:media_storage/src/media_storage_method_channel.dart';

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
