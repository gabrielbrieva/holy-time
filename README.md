# Holy Time - Udacity Capstone-Project

Many people meditate about God and reserve time to Him each Sabbath (Exodus 20:8-11), but sometimes the job or other activities make to forget that Holy Time was reserved to enjoy His bless and company.
Holy Time App helps you to not forget your communion with God and give you contents to read and meditate about His greatest love and forgiveness.

Biblically the Sabbath begins and ends with sunset. So depends on your location, you will receive a notifications at the beginning of the Sabbath to read and meditate.

## Technical Specs

### Meditations Data
This app sync the latest 10 week meditation content from http://holytime.gabriel.brieva.cl/api using a DummyAuthenticator and SyncAdapter.
The list/grid of meditations content is loaded from device DB and the older content is loaded from the API using a endless technique.

### Libraries

* Butterknife: To bind views using annotations.
* Retrofit: Reduce the code of http request and get advantage of some capabilities of this library like logging and serialization/deserialization.
* Schematic: Generate Content Provider based in some simple interfaces (this has some limitation but is enough).
* SunriseSunsetCalculator: Calculate the sunrise or sunset time based in location.
* PermissionEverywhere: Library to solve the common problem to get permission from outside of Activities.

### Google Integrations

* Android Support Design Library.
* FCM (Firebase Cloud Message): Each time a meditation content is updated, the HolyTime server send a message to FCM to notify all devices subscribed to topic "sync" about this change.
* Compat Notifications: Showing notifications on Android Wear too.

## UI

* Based on Android Design Specs and supported by Android Support Design Library.
* Simple transition between activities.
* A custom view to represent sunrise and sunset times.
* Custom collapsible main title and content of AppBarLayout.

### Screenshots
[[ /screenshots/main.jpg | height = 100px ]]
![Alt text](/screenshots/main.jpg?raw=true "Main")
![Alt text](/screenshots/notification.jpg?raw=true "Notification")
![Alt text](/screenshots/settings.jpg?raw=true "Settings")
![Alt text](/screenshots/sunrise_sunset_info.jpg?raw=true "Custom View")
![Alt text](/screenshots/tablet_main.jpg?raw=true "Main on Tablets")
![Alt text](/screenshots/tablet_detail.jpg?raw=true "Details on Tablets")
![Alt text](/screenshots/tablet_detail_collapsed.jpg?raw=true "Details on Tablets")