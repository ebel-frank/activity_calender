# Activity Calender
Activity Calender is a productivity app. I built it to help users manage their events/activities, timetable and also take notes. 
So you actually don't miss out on anything from school parties, classes, church fellowships, bootcamps, seminars and lots more.
I also added reminders: 5min before, 30min before and so on; so that you get notified for any event youv'e set. With this feature, 
the app makes sure your'e at the the right place at the right time with the right group doing the right thing.

And as a token from my heart❤️, I added several quotes to the app. We all need motivation once in a while, isn't that right?😇

## Libraries Used
1. **[ambilwarna](https://github.com/yukuku/ambilwarna):**
   - A color picker library that allows users to select colors within the app.
2. **[AssetSQLiteOpenHelper](https://github.com/daolq3012/AssetSQLiteOpenHelper):**
   - A helper library for managing SQLite databases using assets.
3. **[Room Persistence Library](https://developer.android.com/jetpack/androidx/releases/room):**
   - Provides an abstraction layer over SQLite.
4. **[Google Play Services Ads SDK](https://developers.google.com/admob/android/quick-start):**
   - Version: 20.5.0
   - For integrating ads into the app.
5. **[AndroidX Core Splash Screen](https://developer.android.com/jetpack/androidx/releases/core):**
   - Library to create splash screens.

## Permissions Used
- **VIBRATE:** Allows the app to control the device vibration.
- **WRITE_EXTERNAL_STORAGE:** Allows the app to write to external storage.
- **READ_EXTERNAL_STORAGE:** Allows the app to read from external storage.

## Database Details
- **SQLite Database:**
  - Managed using Room Persistence Library, which simplifies database management.
  - Provides compile-time checks of SQL queries and allows seamless database migration.
  - Use of DAOs (Data Access Objects) to define methods for accessing the database.

## Database Architecture
- **Entities:** Defines the schema of tables in the database.
- **DAOs:** Interfaces that define the methods for interacting with the database.
- **Database:** The main access point to the underlying SQLite database, annotated with `@Database`.

# Activity Calender Interface
<img src="https://github.com/ebel-frank/activity_calender/blob/master/Screenshot_4.png" width="300px">  <img src="https://github.com/ebel-frank/activity_calender/blob/master/Screenshot_1.png" width="300px">
<img src="https://github.com/ebel-frank/activity_calender/blob/master/Screenshot_2.png" width="300px">


This app is not on play store or any other store.

# Feel free to fork it and make it better than I did!

## License:
-------
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
