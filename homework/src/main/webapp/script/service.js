app.factory('LoginService', function() {
	return {
		login: function($scope, $location, $http, AppData, MessageBox) {
			var request = {
				accountId: $scope.userId,
				password: $scope.passWd
			};
			$http.post(USER_API + '/login', request)
			.then(
				function(responce) {
					if (responce.data != null && responce.data.user != null) {
						AppData.user = responce.data.user;
						AppData.values.wrongAnswers = responce.data.answers;
						$location.path("/top");
					}
					else {
						MessageBox.warn($scope, "ログインに失敗しました。\n正しいユーザーIDとパスワードを入力してください。");
					}
				},
				function(responce) {
					MessageBox.error($scope, "システムエラーが発生しました。\n管理者にお問い合わせください。");
				}
			);
		}
	};
});

app.factory('MainService', function() {
	return {
		start: function($scope, $location, $http, AppData, MessageBox) {
			AppData.values.questionVolume = $scope.volume;
			var request = {
				user: AppData.user,
				questionVolume: $scope.volume
			};
			$http.post(USER_API + '/question', request)
			.then(
				function(responce) {
					AppData.values.questions = responce.data.questions;
					AppData.values.startTime = Date.now();
					$location.path("/question");
				},
				function(responce) {
					MessageBox.error($scope, "システムエラーが発生しました。\n管理者にお問い合わせください。");
				}
			);
		},
		answer: function($scope, $location, $http, AppData, MessageBox) {
			var request = {
				user: AppData.user,
				answers: this.createAnswers($scope.questions),
				elapsed: Date.now() - AppData.values.startTime
			};
			$http.post(USER_API + '/answer', request)
			.then(
				function(responce) {
					AppData.values.answers = responce.data.answers;
					AppData.values.history = responce.data.history;
					var elapsed = parseInt(responce.data.history.elapsed / 1000);
					AppData.values.history.elapsed = parseInt(elapsed / 60) + "分" + (elapsed % 60) + "秒";
					$location.path("/answer");
				},
				function(responce) {
					MessageBox.error($scope, "システムエラーが発生しました。\n管理者にお問い合わせください。");
					$location.path("/login");
				}
			);
		},
		createAnswers: function(questions) {
			var answers = [];
			for (var i = 0; i < questions.length; i++) {
				var answer = {
					question: {
						questionId: questions[i].questionId,
						correct: questions[i].correct,
						question: questions[i].question,
						sepChar: questions[i].sepChar,
						remark: questions[i].remark,
						subject: {
							subjectId: questions[i].subject.subjectId,
							description: questions[i].subject.description,
							subjectType: questions[i].subject.subjectType,
							targetAge: questions[i].subject.targetAge
						}
					},
					answer: questions[i].answer
				};
				answers.push(answer);
			}
			return answers;
		}
	};
});
