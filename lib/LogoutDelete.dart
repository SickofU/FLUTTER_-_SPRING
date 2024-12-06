import 'package:flutter/cupertino.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

class LogoutDeleteView extends StatelessWidget {
  final storage = const FlutterSecureStorage();
  final String deleteAccountApiUrl =
      'http://localhost:8080/api/v1/account/delete';

  Future<void> logout(BuildContext context) async {
    // 삭제하려는 저장된 토큰
    await storage.delete(key: 'access_token');
    await storage.delete(key: 'refresh_token');
    print('Tokens deleted. Logging out.');

    // 로그인/회원가입 화면으로 이동
    Navigator.pushReplacementNamed(context, '/login');
  }

  Future<void> deleteAccount(BuildContext context) async {
    final token = await storage.read(key: 'access_token');

    if (token == null) {
      print('No token found. Redirecting to login.');
      Navigator.pushReplacementNamed(context, '/login');
      return;
    }

    try {
      final response = await http.delete(
        Uri.parse(deleteAccountApiUrl),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        print('Account deleted successfully.');
        await storage.delete(key: 'access_token');
        await storage.delete(key: 'refresh_token');

        // 계정 삭제 후 로그인/회원가입 화면으로 이동
        Navigator.pushReplacementNamed(context, '/login');
      } else {
        print(
            'Failed to delete account. Response: ${response.statusCode}, ${response.body}');
        showCupertinoDialog(
          context: context,
          builder: (context) {
            return CupertinoAlertDialog(
              title: Text('Error'),
              content: Text('Failed to delete account.'),
              actions: [
                CupertinoDialogAction(
                  child: Text('OK'),
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
      print('Error deleting account: $e');
      showCupertinoDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog(
            title: Text('Error'),
            content: Text('An error occurred: $e'),
            actions: [
              CupertinoDialogAction(
                child: Text('OK'),
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
      navigationBar: CupertinoNavigationBar(
        middle: Text('Logout & Delete Account'),
      ),
      child: Center(
        // 중앙에 배치
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center, // 수직으로 중앙 정렬
            children: [
              CupertinoButton.filled(
                onPressed: () => logout(context),
                child: Text('Logout'),
              ),
              SizedBox(height: 16),
              CupertinoButton(
                onPressed: () => deleteAccount(context),
                child: Text('Delete Account'),
                color: CupertinoColors.destructiveRed,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
