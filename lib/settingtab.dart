import 'package:flutter/cupertino.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import 'ProfileInfoView.dart';
import 'EditProfileView.dart';
import 'LogoutDelete.dart';

// SettingsTab - Main Settings Tab Class
class SettingsTab extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        middle: Text('Settings'),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            GestureDetector(
              onTap: () => _showChangePhotoDialog(context),
              child: ClipOval(
                child: Image.asset(
                  'assets/default_profile.jpeg',
                  width: 100,
                  height: 100,
                  fit: BoxFit.cover,
                ),
              ),
            ),
            SizedBox(height: 16),
            CupertinoButton(
              color: CupertinoColors.systemPink,
              onPressed: () {
                Navigator.push(
                  context,
                  CupertinoPageRoute(builder: (context) => EditProfileView()),
                );
              },
              child: Text(
                'Edit Profile',
                style: TextStyle(color: CupertinoColors.white),
              ),
            ),
            SizedBox(height: 16),
            CupertinoButton(
              color: CupertinoColors.systemPink,
              onPressed: () {
                Navigator.push(
                  context,
                  CupertinoPageRoute(builder: (context) => ProfileInfoView()),
                );
              },
              child: Text(
                'Profile Info',
                style: TextStyle(color: CupertinoColors.white),
              ),
            ),
            SizedBox(height: 16),
            CupertinoButton(
              color: CupertinoColors.systemPink,
              onPressed: () {
                Navigator.push(
                  context,
                  CupertinoPageRoute(builder: (context) => LogoutDeleteView()),
                );
              },
              child: Text(
                'Logout & Delete Account',
                style: TextStyle(color: CupertinoColors.white),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _showChangePhotoDialog(BuildContext context) {
    showCupertinoDialog(
      context: context,
      builder: (context) => CupertinoAlertDialog(
        title: Text('Change Profile Photo'),
        content: Text('Do you want to change your profile picture?'),
        actions: [
          CupertinoDialogAction(
            onPressed: () => Navigator.pop(context),
            child: Text('No'),
          ),
          CupertinoDialogAction(
            onPressed: () {
              // Logic for attaching photo goes here
              Navigator.pop(context);
            },
            child: Text('Yes'),
          ),
        ],
      ),
    );
  }
}
