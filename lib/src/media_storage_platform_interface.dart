import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'media_storage_method_channel.dart';

abstract class MediaStoragePlatform extends PlatformInterface {
  /// Constructs a MediaStoragePlatform.
  MediaStoragePlatform() : super(token: _token);

  static final Object _token = Object();

  static MediaStoragePlatform _instance = MethodChannelMediaStorage();

  /// The default instance of [MediaStoragePlatform] to use.
  ///
  /// Defaults to [MethodChannelMediaStorage].
  static MediaStoragePlatform get instance => _instance;
  
  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MediaStoragePlatform] when
  /// they register themselves.
  static set instance(MediaStoragePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
