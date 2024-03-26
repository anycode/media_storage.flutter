class MediaStorageFile {
  int mediaId = 0;
  String displayName = '';
  String? fileUri;
  String filepath = "";
  int? dateCreated;
  int? dateModified;
  int? mediaType;
  String? mimeType;
  String? height;
  String? width;
  int? duration;
  String? size;

  MediaStorageFile();

  MediaStorageFile.fromJSON(Map<String, dynamic> jsonMap) {
    mediaId = jsonMap['mediaId'] ?? '';
    displayName = jsonMap['displayName'] ?? '';
    fileUri = jsonMap['fileUri'];
    filepath = jsonMap['filepath'];
    dateCreated = jsonMap['dateCreate'];
    dateModified = jsonMap['dateModified'];
    mediaType = jsonMap['media_type'];
    mimeType = jsonMap['mime_type'];
    height = jsonMap['height'];
    width = jsonMap['width'];
    duration = jsonMap['video_duration'];
    size = jsonMap['size'];
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is MediaStorageFile &&
          runtimeType == other.runtimeType &&
          mediaId == other.mediaId &&
          displayName == other.displayName &&
          fileUri == other.fileUri &&
          filepath == other.filepath &&
          dateCreated == other.dateCreated &&
          dateModified == other.dateModified &&
          mediaType == other.mediaType &&
          mimeType == other.mimeType &&
          height == other.height &&
          width == other.width &&
          duration == other.duration &&
          size == other.size;

  @override
  int get hashCode =>
      mediaId.hashCode ^
      displayName.hashCode ^
      fileUri.hashCode ^
      filepath.hashCode ^
      dateCreated.hashCode ^
      dateModified.hashCode ^
      mediaType.hashCode ^
      mimeType.hashCode ^
      height.hashCode ^
      width.hashCode ^
      duration.hashCode ^
      size.hashCode;

  Map toMap() {
    var map = <String, dynamic>{};
    map["mediaId"] = mediaId;
    map["displayName"] = displayName;
    map["fileUri"] = fileUri;
    map["filepath"] = filepath;
    map["dateCreate"] = dateCreated;
    map["dateModified"] = dateModified;
    map["media_type"] = mediaType;
    map["mime_type"] = mimeType;
    map["height"] = height;
    map["width"] = width;
    map["video_duration"] = duration;
    map["size"] = size;
    return map;
  }
}
