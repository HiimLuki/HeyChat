'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((data, context) => {
	
	const user_id = context.params.user_id;
	const notification_id = context.params.notification_id;

	console.log('User ID', context.params.user_id);
	
	const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');
	return fromUser.then(fromUserResult => {
		
		const from_user_id = fromUserResult.val().from;
		
		console.log('You have new Notification from:', from_user_id);
		
		const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
		return userQuery.then(userResult => {
			const userName = userResult.val();
			
			const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');
			
	
			return deviceToken.then(result => {
		
				const token_id = result.val();
				
				console.log(token_id);
		
				const payload = {
					notification : {
						title : "Friend Request",
						body: `${userName} has sent you a Friend request.`,
						icon: "default",
						click_action: "in.heycompany.heychat_TARGET_NOTIFICATION"
					},
					data : {
						from_user_id : from_user_id
					}
				};
	
				return admin.messaging().sendToDevice(token_id, payload).then(function(response) {
		
					return console.log('This was the notification Feature');
				}).catch(function(error) {
					console.log('Error sending Notification', error);
				});
			});
		});
	});
});