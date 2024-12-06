import 'package:flutter/cupertino.dart';
import 'package:flutter_application_ex/settingtab.dart';

import 'CalendarTab.dart';
import 'settingtab.dart';
import 'hometab.dart';
import 'albumtab.dart';
import 'LoginPage.dart';

class MainTabPage extends StatelessWidget {
  const MainTabPage({super.key});

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async {
        // 뒤로 가기 동작을 막음
        return false;
      },
      child: CupertinoTabScaffold(
        tabBar: CupertinoTabBar(
          items: const [
            BottomNavigationBarItem(
              icon: Icon(CupertinoIcons.home),
              label: 'Home',
            ),
            BottomNavigationBarItem(
              icon: Icon(CupertinoIcons.settings),
              label: 'Settings',
            ),
            BottomNavigationBarItem(
              icon: Icon(CupertinoIcons.cart),
              label: 'Album',
            ),
            BottomNavigationBarItem(
              icon: Icon(CupertinoIcons.calendar),
              label: 'Calendar',
            ),
          ],
        ),
        tabBuilder: (context, index) {
          switch (index) {
            case 0:
              return HomeTab();
            case 1:
              return SettingsTab();
            case 2:
              return AlbumTab();
            case 3:
              return CalendarTab();
            default:
              return HomeTab();
          }
        },
      ),
    );
  }
}
