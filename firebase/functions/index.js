// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');
// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

// The database reference to the users
const usersRef = admin.firestore().collection("users");

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
