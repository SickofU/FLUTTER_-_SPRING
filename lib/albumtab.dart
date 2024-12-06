import 'package:flutter/cupertino.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';

class AlbumTab extends StatefulWidget {
  const AlbumTab({super.key});

  @override
  State<AlbumTab> createState() => _AlbumTabState();
}

class _AlbumTabState extends State<AlbumTab> {
  DateTime currentDate = DateTime.now();
  List<dynamic> albumEntries = [];
  bool isLoading = true;

  final String apiUrl = 'http://localhost:8080/api/v1/album';
  final storage = const FlutterSecureStorage();
  String? token;

  @override
  void initState() {
    super.initState();
    loadToken();
  }

  Future<void> loadToken() async {
    final storedToken = await storage.read(key: 'access_token');
    if (storedToken != null) {
      setState(() {
        token = storedToken;
      });
      fetchAlbumEntries(currentDate);
    } else {
      print('토큰이 없습니다. 로그인이 필요합니다.');
      setState(() {
        isLoading = false;
      });
    }
  }

  Future<void> fetchAlbumEntries(DateTime date) async {
    if (token == null) {
      print('토큰이 없습니다. 데이터를 가져올 수 없습니다.');
      return;
    }

    setState(() {
      isLoading = true;
    });

    // YYYYMMDD 형식으로 날짜 생성
    final formattedDate =
        '${date.year}${date.month.toString().padLeft(2, '0')}${date.day.toString().padLeft(2, '0')}';
    final url = '$apiUrl/$formattedDate';

    try {
      print('Request URL: $url');
      print('Request Headers: Authorization=Bearer $token');
      print('Request Headers: Content-Type=application/json');

      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
      ).timeout(const Duration(seconds: 15)); // 타임아웃 설정

      // 응답 후 로그 추가
      print('Response Status: ${response.statusCode}');
      print('Response Body: ${response.body}');

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          albumEntries = data;
          isLoading = false;
        });
      } else {
        setState(() {
          albumEntries = [];
          isLoading = false;
        });
        print('Error: ${response.statusCode}, ${response.body}');
      }
    } catch (e) {
      setState(() {
        albumEntries = [];
        isLoading = false;
      });
      print('Error: $e');
    }
  }

  void navigateToPreviousDay() {
    final previousDay = currentDate.subtract(const Duration(days: 1));
    setState(() {
      currentDate = previousDay;
    });
    fetchAlbumEntries(previousDay);
  }

  void navigateToNextDay() {
    final nextDay = currentDate.add(const Duration(days: 1));
    setState(() {
      currentDate = nextDay;
    });
    fetchAlbumEntries(nextDay);
  }

  @override
  Widget build(BuildContext context) {
    final formattedDate =
        '${currentDate.year}-${currentDate.month.toString().padLeft(2, '0')}-${currentDate.day.toString().padLeft(2, '0')}';

    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        middle: Text('Album - $formattedDate'),
        leading: CupertinoButton(
          padding: EdgeInsets.zero,
          onPressed: navigateToPreviousDay,
          child: const Icon(CupertinoIcons.left_chevron),
        ),
        trailing: CupertinoButton(
          padding: EdgeInsets.zero,
          onPressed: navigateToNextDay,
          child: const Icon(CupertinoIcons.right_chevron),
        ),
      ),
      child: SafeArea(
        child: isLoading
            ? const Center(child: CupertinoActivityIndicator())
            : albumEntries.isEmpty
                ? const Center(child: Text('No data available'))
                : ListView.builder(
                    itemCount: albumEntries.length,
                    itemBuilder: (context, index) {
                      final entry = albumEntries[index];
                      return Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          if (entry['text'] != null) Text(entry['text']),
                          if (entry['imageUrl'] != null)
                            Padding(
                              padding: const EdgeInsets.only(top: 8.0),
                              child: Image.network(entry['imageUrl']),
                            ),
                          // Cupertino 스타일의 구분선
                          Container(
                            height: 1.0,
                            color: CupertinoColors.systemGrey5, // 구분선 색상
                            margin: const EdgeInsets.symmetric(vertical: 8.0),
                          ),
                        ],
                      );
                    },
                  ),
      ),
    );
  }
}
