#import "MediaStoragePlugin.h"
#if __has_include(<media_storage/media_storage-Swift.h>)
#import <media_storage/media_storage-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "media_storage-Swift.h"
#endif

@implementation MediaStoragePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMediaStoragePlugin registerWithRegistrar:registrar];
}
@end
