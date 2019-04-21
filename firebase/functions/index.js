// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');
// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

// The database reference to the users
const usersRef = admin.database().ref().child('users');

exports.addUserToDatabase = functions.auth.user().onCreate((user) => {
    usersRef.child(user.uid).set({
        displayName: user.displayName,
        email: user.email,
        photoURL: user.photoURL
	});
});

exports.removeUserFromDatabase = functions.auth.user().onDelete((user) => {
    usersRef.child(user.uid).remove();
});
