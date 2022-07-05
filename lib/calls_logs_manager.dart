import 'package:flutter/services.dart';

class CallLogsManager {
  static const MethodChannel _methodChannel = MethodChannel("CALL_LOGS");

  Future getLogs({String? timeDate}) async {
    try {
      final List result = await _methodChannel.invokeMethod('getLogs', timeDate);
      print(result);
      // print(result.sublist(0,
      //     result.indexWhere((element) => element["date"] == "1656966361976")));
      return result;
    } on PlatformException catch (e) {
      print(e);
      rethrow;
    }
  }
}
