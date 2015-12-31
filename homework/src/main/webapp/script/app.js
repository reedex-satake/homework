'use strict';

const API_ROOT = "/homework/api";
const USER_API = API_ROOT + "/user";

var app = angular.module('HomeworkApp', ['ngRoute', 'ui.bootstrap']);

app.config(['$routeProvider',
	function($routeProvider) {
		$routeProvider
			.when('/login', {
				templateUrl: 'view/login.html',
				controller: 'InitController'
			})
			.when('/top', {
				templateUrl: 'view/top.html',
				controller: 'QuestionController'
			})
			.when('/question', {
				templateUrl: 'view/question.html',
				controller: 'AnswerController'
			})
			.when('/answer', {
				templateUrl: 'view/answer.html',
				controller: 'EndController'
			})
			.otherwise({
				redirectTo: '/login'
			});
	}
]);

app.run(['$rootScope', '$location', 'AppData',
	function($rootScope, $location, AppData) {
		$rootScope.$on('$routeChangeStart', function(event, next, current) {
			if (next.controller != 'InitController' && !AppData.isLogin()) {
				event.preventDefault();
				$location.path('/login');
			}
		});
	}
]);


