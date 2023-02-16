ABOUT

Android project for a Cops and Robbers game using a Distributed Complex Event Processing Architecture.
The application is implemented using the P2P architecture for Social Mobile Computing Applications Digital Avatars.
  * The Digital Avatars framework includes an unique user profile for thrid party applications use and persistence needs.
  * It also includes a Complex Event Processing engine (Siddhi CEP) for distributed event driven scenarios available for the applications.


REQUIREMENTS

* Prior to compile the application it is necessary to create a Firebase and a OneSignal Project to include the identifiers and server keys in the app/build.graddle.
* A google-services.json must be generated and included in the project.
* Also a node.js server must be online to authenticate te users of your app when sending messages using the tokenID of the sender to fetch the email of the user and verify its correspondence.

USAGE

* Install the application and launch it. First it will require to log in with Google to obtain the user data.

* Once the user is logged, you can navigate to the app drawer and see the user data including the OneSignal ID, this ID is important for your friends to add yo as a contact in the app.

* Now you are able to "play" the CEP engine and start the Cops&Robbers with the button in Home layout. 
