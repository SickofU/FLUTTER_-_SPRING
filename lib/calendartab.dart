import 'package:flutter/cupertino.dart';
import 'package:image_picker/image_picker.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:intl/intl.dart';
import 'dart:io'; // dart:io 패키지 추가

class CalendarTab extends StatefulWidget {
  const CalendarTab({super.key});

  @override
  State<CalendarTab> createState() => _CalendarTabState();
}

class _CalendarTabState extends State<CalendarTab> {
  DateTime selectedDate = DateTime.now();
  DateTime focusedDate = DateTime(DateTime.now().year, DateTime.now().month, 1);
  XFile? _uploadedImage;
  final TextEditingController _textController = TextEditingController();
  final String baseUrl = "http://localhost:8080/api/v1/calendar";
  final FlutterSecureStorage storage = const FlutterSecureStorage();

  String? token;

  @override
  void initState() {
    super.initState();
    _loadToken();
  }

  Future<void> _loadToken() async {
    final storedToken = await storage.read(key: 'access_token');
    setState(() {
      token = storedToken;
    });
  }

  void _changeMonth(int offset) {
    setState(() {
      focusedDate = DateTime(
        focusedDate.year,
        focusedDate.month + offset,
        1,
      );
    });
  }

  Future<void> _uploadData({String? imagePath, String text = ""}) async {
    if (token == null) {
      print("JWT token is missing");
      return;
    }

    print("Loaded token: $token"); // 디버깅 추가
    print("Selected Date: ${selectedDate.toIso8601String()}");

    final formattedDate = DateFormat('yyyyMMdd').format(selectedDate);
    final uri = Uri.parse("$baseUrl/upload/$formattedDate");
    print("Sending request to: $uri"); // 디버깅 추가

    // Create a Multipart Request
    final request = http.MultipartRequest('POST', uri)
      ..headers['Authorization'] = 'Bearer $token'
      ..headers['Content-Type'] = 'multipart/form-data'
      ..fields['text'] = text;

    // Add the image file
    if (_uploadedImage != null && imagePath != null) {
      try {
        request.files.add(await http.MultipartFile.fromPath(
          'imageFile', // 서버에서 사용하는 파라미터 이름
          imagePath,
        ));
        print("File uploaded: $imagePath");
      } catch (e) {
        print("Error adding file: $e");
        return;
      }
    } else {
      print("No image file selected");
    }

    // Send the request
    try {
      final response = await request.send();
      print("Response status: ${response.statusCode}");
      if (response.statusCode == 200) {
        print("Upload successful");
        final responseBody = await response.stream.bytesToString();
        print("Server Response: $responseBody");
      } else {
        print("Upload failed: ${response.statusCode}");
        final responseBody = await response.stream.bytesToString();
        print("Error Response: $responseBody");
      }
    } catch (e) {
      print("Error: $e");
    }
  }

  void _showUploadModal(BuildContext context) {
    showCupertinoModalPopup(
      context: context,
      builder: (BuildContext context) {
        return CupertinoActionSheet(
          title: Text(
            "${selectedDate.year}년 ${selectedDate.month}월 ${selectedDate.day}일",
            style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
          ),
          actions: [
            CupertinoActionSheetAction(
              onPressed: () async {
                final ImagePicker picker = ImagePicker();
                final XFile? image =
                    await picker.pickImage(source: ImageSource.gallery);
                if (image != null) {
                  setState(() {
                    _uploadedImage = image;
                  });
                  await _uploadData(imagePath: image.path);
                }
                Navigator.pop(context);
              },
              child: const Text('이미지 업로드'),
            ),
            CupertinoActionSheetAction(
              onPressed: () {
                Navigator.pop(context);
                _showTextInputModal(context);
              },
              child: const Text('텍스트 입력'),
            ),
          ],
          cancelButton: CupertinoActionSheetAction(
            onPressed: () {
              Navigator.pop(context);
            },
            child: const Text('취소'),
          ),
        );
      },
    );
  }

  void _showTextInputModal(BuildContext context) {
    showCupertinoModalPopup(
      context: context,
      builder: (BuildContext context) {
        return CupertinoPopupSurface(
          isSurfacePainted: true,
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                CupertinoTextField(
                  controller: _textController,
                  placeholder: '텍스트 입력',
                ),
                const SizedBox(height: 16),
                CupertinoButton.filled(
                  onPressed: () async {
                    await _uploadData(text: _textController.text);
                    Navigator.pop(context);
                  },
                  child: const Text('저장'),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    const double fontSize = 14;
    const double popupWidth = 300;
    const double popupHeight = 350;

    return CupertinoPageScaffold(
      child: Center(
        child: Container(
          width: popupWidth,
          height: popupHeight,
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: CupertinoColors.systemGrey6,
            borderRadius: BorderRadius.circular(12),
            boxShadow: const [
              BoxShadow(
                color: CupertinoColors.black,
                blurRadius: 10,
                offset: Offset(0, 4),
              ),
            ],
          ),
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  CupertinoButton(
                    padding: EdgeInsets.zero,
                    onPressed: () => _changeMonth(-1),
                    child: const Icon(CupertinoIcons.left_chevron),
                  ),
                  Text(
                    "${focusedDate.year}년 ${focusedDate.month}월",
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  CupertinoButton(
                    padding: EdgeInsets.zero,
                    onPressed: () => _changeMonth(1),
                    child: const Icon(CupertinoIcons.right_chevron),
                  ),
                ],
              ),
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 8.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    Text('SUN',
                        style: TextStyle(color: CupertinoColors.systemRed)),
                    Text('MON'),
                    Text('TUE'),
                    Text('WED'),
                    Text('THU'),
                    Text('FRI'),
                    Text('SAT',
                        style: TextStyle(color: CupertinoColors.systemBlue)),
                  ],
                ),
              ),
              Expanded(
                child: GridView.builder(
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 7,
                    crossAxisSpacing: 8,
                    mainAxisSpacing: 8,
                  ),
                  itemCount: DateTime(
                    focusedDate.year,
                    focusedDate.month + 1,
                    0,
                  ).day,
                  itemBuilder: (BuildContext context, int index) {
                    final date = DateTime(
                        focusedDate.year, focusedDate.month, index + 1);

                    bool isToday = date.day == DateTime.now().day &&
                        date.month == DateTime.now().month &&
                        date.year == DateTime.now().year;

                    bool isSelected = date.day == selectedDate.day &&
                        date.month == selectedDate.month &&
                        date.year == selectedDate.year;

                    return GestureDetector(
                      onTap: () {
                        setState(() {
                          selectedDate = date;
                        });
                        _showUploadModal(context);
                      },
                      child: Container(
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          color: isSelected
                              ? CupertinoColors.activeOrange
                              : isToday
                                  ? CupertinoColors.systemRed
                                  : CupertinoColors.systemGrey5,
                        ),
                        alignment: Alignment.center,
                        child: Text(
                          "${index + 1}",
                          style: TextStyle(
                            fontSize: fontSize,
                            color: isSelected || isToday
                                ? CupertinoColors.white
                                : CupertinoColors.black,
                          ),
                        ),
                      ),
                    );
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
