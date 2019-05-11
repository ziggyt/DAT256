// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');
// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

// The database reference to the users
const usersRef = admin.firestore().collection("users");

//The database reference to the notifications
const tripNotificationRef = admin.firestore().collection("tripBookingNotification");

exports.addUserToDatabase = functions.auth.user().onCreate((user) => {
    usersRef.doc(user.uid).set({
        displayName: user.displayName,
        email: user.email,
        photoURL: user.photoURL
	});
});

exports.removeUserFromDatabase = functions.auth.user().onDelete((user) => {
    usersRef.doc(user.uid).delete();
});

exports.notifications = functions.firestore
    .document("tripBookingNotification/{tripId}")
    .onUpdate((change, context) => {
      // Get an object representing the document
      // e.g. {'name': 'Marie', 'age': 66}
      const newValue = change.after.data();

      // ...or the previous value before this update
      const previousValue = change.before.data();

      const tripId = context.params.tripId;

      // access a particular field as you would any JS property
      const type = newValue.typeOfChange;

      const payload = {
        data: {
            typeOfChange: type
        }
      }
      return admin.messaging().sendToTopic(tripId, payload)

      // perform desired operations ...
    });


    /*export const notifications =
    functions.firestore.doc("tripBookingNotification/{tripId}").onUpdate(change => {
        const after = change.after.data()
        const payload = {
            data: {
                typeOfChange: after.typeOfChange
            }

        }
        return admin.messaging().sendToTopic("tripId",payload)
    })*/
