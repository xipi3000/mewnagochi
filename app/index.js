/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// Crontab expression makes sure it executes every hour at minute 0
exports.checkHourAndSendNotif = functions.pubsub.schedule("* * * * *")
    .onRun(async (context) =>{
      // Get the current hour (0-23)
      // Since server in us-central, +2 to get Europe time
      const date = new Date();
      const dateAdjustedButString = date.setHours(date.getHours()+2);
      const dateAdjusted = new Date(dateAdjustedButString);
      const currentHour = dateAdjusted.getHours();
      try {
        // Get realtime database FCMToken and notification hour
        admin.database().ref("/FCMTokens").once("value").then((snapshot)=>{
          // Loop through all values of logged users
          snapshot.forEach((userSnapshot)=>{
            // Compare actual hour with configured hour
            const userHour = userSnapshot.child("notification").val();
            if (parseInt(userHour) == parseInt(currentHour)) {
              // Obtain preferences document since it has all needed info
              const userUid = userSnapshot.child("associatedTo").val();
              admin.firestore().collection("usersPreferences").doc(userUid)
                  .get().then((snapshot)=>{
                    // Get steps data
                    const stepsValue = snapshot.data().currentSteps;
                    const stepsAvg = snapshot.data().stepsAverage;
                    // From steps, confectionate message
                    let titleContent = "";
                    if (stepsValue>=stepsAvg) {
                      titleContent = "Well done today! You outdid yourself!";
                    } else {
                      titleContent = "Oh no, you're under your average today!";
                    }
                    const bodyContent = "Click to sync with cloud.";
                    const userToken = userSnapshot.key;
                    // Create message
                    const message = {
                      notification: {
                        title: titleContent,
                        body: bodyContent,
                      },
                      token: userToken,
                    };
                    // Then send
                    admin.messaging().send(message)
                        .then((response)=>{
                          console.log("Sent notification successfully to "+
                              userUid);
                        })
                        .catch((error)=>{
                          console.log("Notification sent to " +
                          userToken + " failed because " +
                          error);
                        });
                    return null;
                  });
            }
          });
          return null;
        });
      } catch (error) {
        console.error("Error fetching user tokens "+
        "and sending notifications:", error);
        return null;
      }
    });

exports.checkObjectiveAccomplished = functions.firestore
    .document("usersPreferences/{documentId}").onUpdate((snapshot, context) =>{
      const userId = context.params.documentId;
      // Check if user alrady claimed prize today
      const lastDate = snapshot.after.data().lastDateRecieved;
      const canRecievePrize = isDateBefore(lastDate);
      if (!canRecievePrize) {
        console.log("User "+userId+" already claimed prize today, exiting...");
        return null;
      }
      console.log("User "+userId+" hasn't claimed prize yet!");
      // Obtain data to check objective
      const objective = snapshot.after.data().stepsGoal;
      const current = snapshot.after.data().currentSteps;
      if (current >= objective) {
        // We know from database schema that documentId = userId
        // Obtain actual value and add daily prize
        console.log("User "+userId+" cleared their objective!");
        admin.database().ref("/usersMoney").child(userId)
            .get().then((snapshot)=>{
              const actualMoney = Number(snapshot.val());
              admin.database().ref("/usersMoney")
                  .child(context.params.documentId).set(actualMoney+10);
              console.log("Money updated");
              // Get actual date (taking care of the 2hr difference)
              const date = new Date();
              const dateAdjustedButString = date.setHours(date.getHours()+2);
              const dateAdjusted = new Date(dateAdjustedButString);
              const day = dateAdjusted.getDate();
              const month = dateAdjusted.getMonth();
              const year = dateAdjusted.getFullYear();
              const newLastGiven = day+"/"+(month+1)+"/"+year;
              // Update lastDateRecieved with today's date
              // Will retrigger function, lastDateRecieved updated, no problem
              admin.firestore().collection("usersPreferences")
                  .doc(userId).update({lastDateRecieved: newLastGiven});
              console.log("newLastGiven updated");
              admin.database().ref("/FCMTokens").once("value")
                  .then((snapshot)=>{
                    snapshot.forEach((userSnapshot)=>{
                      const userUid = userSnapshot.child("associatedTo").val();
                      if (String(userUid) == userId) {
                        const message = {
                          notification: {
                            title: "You SMASHED that objective!",
                            body: "Enter the app to claim your rewards!",
                          },
                          token: userSnapshot.key,
                        };
                        admin.messaging().send(message)
                            .then((response)=>{
                              console.log("Sent notification successfully to "+
                                  userId);
                            })
                            .catch((error)=>{
                              console.log("Notification sent to " +
                              userSnapshot.key + " failed because " +
                              error);
                            });
                      }
                      return null;
                    });
                  });
            });
      } else {
        console.log("User "+userId+" hasn't got to their objective yet");
      }
      return null;
    });

/**
 * @param {string} lastDateRecieved -> firestore value that tells
 *                                     last time points were claimed
 * @return {Boolean}
 *                   -> True if it is before today (no prize claimed yet)
 *                   -> False if it is today (prize already claimed)
 */
function isDateBefore(lastDateRecieved) {
  // Get today date (at 00:00) new Date().setUTCHours(0, 0, 0, 0)
  const date = new Date();
  const dateAdjustedButString = date.setHours(date.getHours()+2);
  const dateAdjusted = new Date(dateAdjustedButString);
  const dateToday = dateAdjusted.setUTCHours(0, 0, 0, 0);
  // Transform lastDateRecieved to compare
  const [day, month, year] = lastDateRecieved.split("/").map(Number);
  const dateGiven = new Date(year, month - 1, day);
  // dateGiven will be either today or some day before
  return dateGiven<dateToday;
}
