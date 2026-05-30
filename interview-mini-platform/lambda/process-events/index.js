exports.handler = async (event) => {
  // Lambda handler for asynchronous background processing.
  for (const record of event.Records || []) {
    const message = JSON.parse(record.body);
    // Example: send push/email notifications, create audit snapshots, etc.
    console.log("Lambda processed message:", message);
  }

  // Successful return acknowledges all processed records.
  return { statusCode: 200, body: "ok" };
};
