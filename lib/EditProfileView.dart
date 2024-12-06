import 'package:flutter/cupertino.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';

class EditProfileView extends StatefulWidget {
  @override
  _EditProfileViewState createState() => _EditProfileViewState();
}

class _EditProfileViewState extends State<EditProfileView> {
  final storage = const FlutterSecureStorage();
  final TextEditingController nameController = TextEditingController();
  final TextEditingController birthdateController = TextEditingController();
  final TextEditingController partnerNameController = TextEditingController();
  final TextEditingController partnerBirthdateController =
      TextEditingController();

  bool isLoading = false;
  String? responseMessage;

  Future<void> saveChanges() async {
    setState(() {
      isLoading = true;
      responseMessage = null;
    });

    final token = await storage.read(key: 'access_token');
    if (token == null) {
      setState(() {
        responseMessage = "No token found. Please log in.";
        isLoading = false;
      });
      Navigator.pushReplacementNamed(context, '/login');
      return;
    }

    final String url = 'http://localhost:8080/api/v1/setting/profileinfo';

    final body = {
      'username': nameController.text,
      'birthdate': birthdateController.text,
      'friendName': partnerNameController.text,
      'friendBirthdate': partnerBirthdateController.text,
    };

    try {
      final response = await http.patch(
        Uri.parse(url),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
        body: json.encode(body),
      );

      setState(() {
        if (response.statusCode == 200) {
          responseMessage = response.body; // 성공 메시지
        } else {
          responseMessage = response.body; // 실패 메시지
        }
      });
    } catch (e) {
      setState(() {
        responseMessage = "An error occurred: $e";
      });
    } finally {
      setState(() {
        isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        middle: Text('Edit Profile'),
      ),
      child: Center(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisSize: MainAxisSize.min, // 콘텐츠를 중앙에 위치
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              CupertinoTextField(
                controller: nameController,
                placeholder: 'Name',
                padding: EdgeInsets.all(16),
              ),
              SizedBox(height: 16),
              CupertinoTextField(
                controller: birthdateController,
                placeholder: 'Birthdate (e.g., 1990-01-01)',
                padding: EdgeInsets.all(16),
              ),
              SizedBox(height: 16),
              CupertinoTextField(
                controller: partnerNameController,
                placeholder: 'Partner Name',
                padding: EdgeInsets.all(16),
              ),
              SizedBox(height: 16),
              CupertinoTextField(
                controller: partnerBirthdateController,
                placeholder: 'Partner Birthdate (e.g., 1992-02-02)',
                padding: EdgeInsets.all(16),
              ),
              SizedBox(height: 32),
              if (isLoading)
                Center(child: CupertinoActivityIndicator())
              else
                CupertinoButton.filled(
                  onPressed: saveChanges,
                  child: Text('Save Changes'),
                ),
              if (responseMessage != null) ...[
                SizedBox(height: 16),
                Center(
                  child: Text(
                    responseMessage!,
                    style: TextStyle(
                      color: responseMessage!.contains("successfully")
                          ? CupertinoColors.activeGreen
                          : CupertinoColors.systemRed,
                    ),
                  ),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
