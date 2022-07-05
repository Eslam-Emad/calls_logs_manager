import 'package:flutter/services.dart';

class CallLogsManager {
  static const MethodChannel _methodChannel = MethodChannel("CALL_LOGS");

  Future<void> getLogs({String? timeDate}) async {
    try {
      final List result =
      await _methodChannel.invokeMethod('getLogs', timeDate);
      print(result);
      // print(result.sublist(0,
      //     result.indexWhere((element) => element["date"] == "1656966361976")));
    } on PlatformException catch (e) {
      print(e);
      rethrow;
    }
  }
}
