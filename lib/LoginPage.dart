import 'package:flutter/cupertino.dart';
import 'package:http/http.dart' as http;
import 'MainTabPage.dart';
import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  bool isLoading = false;

  // FlutterSecureStorage 인스턴스 생성
  final storage = const FlutterSecureStorage();

  Future<void> _login() async {
    setState(() {
      isLoading = true;
    });

    final url = Uri.parse('http://localhost:8080/api/v1/login');

    try {
      final response = await http.post(
        url,
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'email': emailController.text,
          'password': passwordController.text,
        }),
      );

      setState(() {
        isLoading = false;
      });

      if (response.statusCode == 200) {
        // 서버 응답 처리
        final Map<String, dynamic> responseBody = json.decode(response.body);

        final String? grantType = responseBody['grantType'];
        final String? accessToken = responseBody['accessToken'];
        final String? refreshToken = responseBody['refreshToken'];

        if (accessToken != null && refreshToken != null) {
          // 토큰 저장
          await storage.write(key: 'grant_type', value: grantType);
          await storage.write(key: 'access_token', value: accessToken);
          await storage.write(key: 'refresh_token', value: refreshToken);
          print("Login successful, tokens stored.");
          // 메인 페이지로 이동
          if (!mounted) return;

          Navigator.pushReplacement(
            context,
            CupertinoPageRoute(
              builder: (context) => const MainTabPage(),
            ),
          );
        } else {
          print("Login failed: ${response.body}");
          throw Exception('Invalid response data');
        }
      } else {
        // 로그인 실패 시 알림 표시
        showCupertinoDialog(
          context: context,
          builder: (BuildContext context) {
            return CupertinoAlertDialog(
              title: const Text('로그인 실패'),
              content: Text('서버 응답: ${response.statusCode}'),
              actions: [
                CupertinoDialogAction(
                  isDefaultAction: true,
                  child: const Text('확인'),
                  onPressed: () {
                    Navigator.pop(context);
                  },
                ),
              ],
            );
          },
        );
      }
    } catch (e) {
      setState(() {
        isLoading = false;
      });

      showCupertinoDialog(
        context: context,
        builder: (BuildContext context) {
          return CupertinoAlertDialog(
            title: const Text('오류 발생'),
            content: Text('로그인 중 오류가 발생했습니다: $e'),
            actions: [
              CupertinoDialogAction(
                isDefaultAction: true,
                child: const Text('확인'),
                onPressed: () {
                  Navigator.pop(context);
                },
              ),
            ],
          );
        },
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(
        middle: Text('로그인'),
      ),
      child: SafeArea(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              CupertinoTextField(
                controller: emailController,
                placeholder: 'ID',
              ),
              const SizedBox(height: 16),
              CupertinoTextField(
                controller: passwordController,
                placeholder: 'Password',
                obscureText: true,
              ),
              const SizedBox(height: 32),
              isLoading
                  ? const CupertinoActivityIndicator() // 로딩 중 표시
                  : CupertinoButton.filled(
                      child: const Text('로그인'),
                      onPressed: _login, // 로그인 함수 호출
                    ),
            ],
          ),
        ),
      ),
    );
  }
}
