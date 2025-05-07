exports.sendNotification = onValueCreated("/jobs/{jobId}", async (event) => {
  const jobData = event.data.val();
  const payload = {
    notification: {
      title: "New Job Alert!",
      body: `New job: ${jobData.title}`,
      sound: "default",
    },
  };

  const db = getDatabase();
  const usersSnapshot = await db.ref("users").once("value");

  const tokens = [];
  usersSnapshot.forEach(userSnap => {
    const token = userSnap.child("fcmToken").val();
    if (token) tokens.push(token);
  });

  if (tokens.length === 0) return;

  return getMessaging().sendToDevice(tokens, payload);
});
