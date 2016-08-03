# Holy Time - Udacity Capstone-Project

Many people meditate about God and reserve time to Him each Sabbath (Exodus 20:8-11), but sometimes the job or other activities make to forget that Holy Time was reserved to enjoy His bless and company.
Holy Time App helps you to not forget your communion with God and give you contents to read and meditate about His greatest love and forgiveness.

Biblically the Sabbath begins and ends with sunset. So depends on your location, you will receive a notifications at the beginning of the Sabbath to read and meditate.

## Technical Specs

### Meditations Data
This app sync the latest 10 week meditation content from http://holytime.gabrielbrieva.cl/api using a DummyAuthenticator and SyncAdapter.
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
<img src="/screenshots/main.jpg" alt="Main" width="300px" >
<img src="/screenshots/notification.jpg" alt="Notification" width="300px" >
<img src="/screenshots/settings.jpg" alt="Settings" width="300px" >
<img src="/screenshots/sunrise_sunset_info.jpg" alt="Custom View" width="300px" >
<img src="/screenshots/tablet_main.jpg" alt="Main on Tablets" width="500px" >
<img src="/screenshots/tablet_detail.jpg" alt="Details on Tablets" width="500px" >
<img src="/screenshots/tablet_detail_collapsed.jpg" alt="Details on Tablets" width="500px" >
