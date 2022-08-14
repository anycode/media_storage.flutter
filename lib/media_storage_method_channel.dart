import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'media_storage_platform_interface.dart';

/// An implementation of [MediaStoragePlatform] that uses method channels.
class MethodChannelMediaStorage extends MediaStoragePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  static const methodChannel = const MethodChannel('media_storage');


  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

}


