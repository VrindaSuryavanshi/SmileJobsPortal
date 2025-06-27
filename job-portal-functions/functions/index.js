const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendNewJobNotification = functions.database
  .ref("/jobs/{jobId}")
  .onCreate(async (snapshot, context) => {
    const job = snapshot.val();

    const payload = {
      notification: {
        title: `🆕 New Job Posted: ${job.title || "New Opportunity"}`,
        body: `${job.company || "A company"} is hiring at ${job.location || "various locations"}`,
        click_action: "OPEN_MAIN_ACTIVITY",
        sound: "default"
      },
      data: {
        jobId: context.params.jobId,
        jobTitle: job.title || "",
        company: job.company || ""
      }
    };

    try {
      const response = await admin.messaging().sendToTopic("allUsers", payload);
      console.log("Notification sent successfully:", response);
    } catch (error) {
      console.error("Error sending notification:", error);
    }

    return null;
  });
