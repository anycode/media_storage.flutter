class AllMedia
{
  int mediaId = 0;
  String displayName='';
  String? fileUri;
  String filepath= "";
  int? dateCreated;
  int? dateModified;
  int? media_type;
  String? mime_type;
  String? height;
  String? width;
  int? duration;
  String? size;
  AllMedia();



  AllMedia.fromJSON(Map<String, dynamic> jsonMap) {
    mediaId = jsonMap['mediaId'] ?? '';
    displayName = jsonMap['displayName'] ?? '';
    fileUri = jsonMap['fileUri'];
    filepath = jsonMap['filepath'];
    dateCreated = jsonMap['dateCreate'];
    dateModified = jsonMap['dateModified'];
    media_type = jsonMap['media_type'];
    mime_type = jsonMap['mime_type'];
    height = jsonMap['height'];
    width = jsonMap['width'];
    duration = jsonMap['video_duration'];
    size = jsonMap['size'];

  }


  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is AllMedia &&
          runtimeType == other.runtimeType &&
          mediaId == other.mediaId &&
          displayName == other.displayName &&
          fileUri == other.fileUri &&
          filepath == other.filepath &&
          dateCreated == other.dateCreated &&
          dateModified == other.dateModified &&
          media_type == other.media_type &&
          mime_type == other.mime_type &&
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
      media_type.hashCode ^
      mime_type.hashCode ^
      height.hashCode ^
      width.hashCode ^
      duration.hashCode ^
      size.hashCode;



  Map toMap() {
    var map = new Map<String, dynamic>();
    map["mediaId"] = mediaId;
    map["displayName"] = displayName;
    map["fileUri"] = fileUri;
    map["filepath"] = filepath;
    map["dateCreate"] = dateCreated;
    map["dateModified"] = dateModified;
    map["media_type"] = media_type;
    map["mime_type"] = mime_type;
    map["height"] = height;
    map["width"] = width;
    map["video_duration"] = duration;
    map["size"] = size;
    return map;
  }
}
