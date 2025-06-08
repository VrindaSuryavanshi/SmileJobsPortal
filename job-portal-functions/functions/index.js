const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp({
  credential: admin.credential.cert(require("./serviceAccountKey.json"))
});

exports.sendNotification = functions.https.onRequest((req, res) => {
  if (req.method !== "POST") {
    return res.status(405).send("Method Not Allowed");
  }

  const { token, title, body } = req.body;

  if (!token || !title || !body) {
    return res.status(400).send({ success: false, error: "Missing token, title, or body." });
  }

  const message = {
    notification: { title, body },
    token
  };

  admin.messaging().send(message)
    .then(response => {
      res.status(200).send({ success: true, message: "Notification sent", response });
    })
    .catch(error => {
      res.status(500).send({ success: false, error: error.message });
    });
});
