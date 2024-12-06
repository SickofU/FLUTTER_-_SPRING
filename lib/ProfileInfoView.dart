import 'package:flutter/cupertino.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class ProfileInfoView extends StatefulWidget {
  @override
  _ProfileInfoViewState createState() => _ProfileInfoViewState();
}

class _ProfileInfoViewState extends State<ProfileInfoView> {
  final storage = const FlutterSecureStorage();
  Map<String, dynamic>? memberInfo; // 서버에서 가져온 멤버 정보 저장
  bool isLoading = true;
  String? errorMessage;

  @override
  void initState() {
    super.initState();
    fetchMemberInfo();
  }

  Future<void> fetchMemberInfo() async {
    setState(() {
      isLoading = true; // 로딩 상태 시작
      errorMessage = null; // 에러 메시지 초기화
    });

    final token = await storage.read(key: 'access_token');
    print('Token from storage: $token'); // 토큰 문제 확인 로그
    if (token == null) {
      setState(() {
        isLoading = false;
        errorMessage = "No token found. Redirecting to login.";
      });
      Navigator.pushReplacementNamed(context, '/login');
      return;
    }

    final url = 'http://localhost:8080/api/v1/setting/profileinfo';

    try {
      // 요청 로그
      print('Fetching member info...');
      print('Request URL: $url');
      print('Authorization: Bearer $token');

      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
      );

      // 응답 로그
      print('Response Status Code: ${response.statusCode}');
      print('Response Body: ${response.body}');

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          memberInfo = data;
          isLoading = false;
        });
      } else {
        setState(() {
          isLoading = false;
          errorMessage =
              'Failed to fetch member info: ${response.statusCode} ${response.body}';
        });
      }
    } catch (e) {
      setState(() {
        isLoading = false;
        errorMessage = "Error fetching member info: $e";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        middle: Text('Profile Info'),
      ),
      child: Center(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: isLoading
              ? CupertinoActivityIndicator() // 로딩 중 상태 표시
              : errorMessage != null
                  ? Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          errorMessage!,
                          style: TextStyle(
                            color: CupertinoColors.systemRed,
                            fontSize: 16,
                          ),
                          textAlign: TextAlign.center,
                        ),
                        SizedBox(height: 16),
                        CupertinoButton.filled(
                          onPressed: fetchMemberInfo,
                          child: Text('Retry'),
                        ),
                      ],
                    )
                  : memberInfo == null
                      ? Center(
                          child: Text(
                            'No profile info available.',
                            style: TextStyle(fontSize: 18),
                          ),
                        )
                      : Column(
                          mainAxisAlignment: MainAxisAlignment.center, // 중앙 정렬
                          crossAxisAlignment:
                              CrossAxisAlignment.center, // 텍스트 가운데 정렬
                          children: [
                            Text(
                              'ID: ${memberInfo!['id'] ?? 'N/A'}',
                              style: TextStyle(fontSize: 18),
                              textAlign: TextAlign.center,
                            ),
                            SizedBox(height: 8),
                            Text(
                              'Email: ${memberInfo!['email'] ?? 'N/A'}',
                              style: TextStyle(fontSize: 18),
                              textAlign: TextAlign.center,
                            ),
                            SizedBox(height: 8),
                            Text(
                              'Username: ${memberInfo!['username'] ?? 'N/A'}',
                              style: TextStyle(fontSize: 18),
                              textAlign: TextAlign.center,
                            ),
                          ],
                        ),
        ),
      ),
    );
  }
}
