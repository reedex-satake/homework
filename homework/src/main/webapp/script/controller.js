
app.controller('InitController', function($scope, $location, $http, AppData, MessageBox, LoginService) {
	$scope.login = function()
	{
		LoginService.login($scope, $location, $http, AppData, MessageBox);
	};
});

app.controller('QuestionController', function($scope, $location, $http, AppData, MessageBox, MainService) {
	$scope.user = AppData.user;
	$scope.volume = (AppData.values.questionVolume != null) ? AppData.values.questionVolume : 10;
	$scope.wrongAnswers = AppData.values.wrongAnswers;
	$scope.start = function()
	{
		MainService.start($scope, $location, $http, AppData, MessageBox);
	};
});

app.controller('AnswerController', function($scope, $location, $http, AppData, MessageBox, MainService) {
	$scope.questions = AppData.values.questions;
	$scope.answer = function()
	{
		MainService.answer($scope, $location, $http, AppData, MessageBox);
	};
});

app.controller('EndController', function($scope, $location, $http, AppData, MessageBox) {
	$scope.history = AppData.values.history;
	$scope.answers = AppData.values.answers;
	$scope.finish = function()
	{
		AppData.user = null;
		AppData.values = {};
		window.close();
	}
});
