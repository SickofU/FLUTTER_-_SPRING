import 'package:flutter/cupertino.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'StartPage.dart';

class RegisterPage extends StatefulWidget {
  const RegisterPage({super.key});

  @override
  State<RegisterPage> createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final TextEditingController nameController = TextEditingController();
  final TextEditingController emailController = TextEditingController();
  final TextEditingController friendNameController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  final String registerUrl = "http://localhost:8080/api/v1/register";

  Future<void> registerUser() async {
    final userData = {
      "username": nameController.text,
      "email": emailController.text,
      "friendName": friendNameController.text,
      "password": passwordController.text,
    };

    try {
      final response = await http.post(
        Uri.parse(registerUrl),
        headers: {"Content-Type": "application/json"},
        body: json.encode(userData),
      );

      if (response.statusCode == 200) {
        // 성공 처리
        print("회원가입 성공: ${response.body}");
        Navigator.pushReplacement(
          context,
          CupertinoPageRoute(builder: (context) => const StartPage()),
        );
      } else {
        // 에러 처리
        print("회원가입 실패: ${response.body}");
        showCupertinoDialog(
          context: context,
          builder: (_) => CupertinoAlertDialog(
            title: const Text("오류"),
            content: Text("회원가입 실패: ${json.decode(response.body)}"),
            actions: [
              CupertinoDialogAction(
                isDefaultAction: true,
                child: const Text("확인"),
                onPressed: () {
                  Navigator.pop(context); // Dialog 닫기
                },
              ),
            ],
          ),
        );
      }
    } catch (e) {
      print("에러 발생: $e");
      showCupertinoDialog(
        context: context,
        builder: (_) => CupertinoAlertDialog(
          title: const Text("오류"),
          content: Text("에러 발생: $e"),
          actions: [
            CupertinoDialogAction(
              isDefaultAction: true,
              child: const Text("확인"),
              onPressed: () {
                Navigator.pop(context); // Dialog 닫기
              },
            ),
          ],
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(
        middle: Text('회원가입'),
      ),
      child: SafeArea(
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                CupertinoTextField(
                  controller: nameController,
                  placeholder: '이름',
                ),
                const SizedBox(height: 16),
                CupertinoTextField(
                  controller: emailController,
                  placeholder: '이메일',
                ),
                const SizedBox(height: 16),
                CupertinoTextField(
                  controller: friendNameController,
                  placeholder: '친구 이름',
                ),
                const SizedBox(height: 16),
                CupertinoTextField(
                  controller: passwordController,
                  placeholder: '비밀번호',
                  obscureText: true,
                ),
                const SizedBox(height: 32),
                CupertinoButton.filled(
                  child: const Text('회원가입 완료'),
                  onPressed: () => registerUser(),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
