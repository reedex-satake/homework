app.factory('AppData', function() {
	return {
		user: null,
		isLogin: function() {
			return (this.user != null);
		},
		values: {}
	};
});


app.factory('MessageBox', function($uibModal) {
	return {
		info: function($scope, message) {
			var dialogInfo = {
				type: "info",
				title: "情報メッセージ",
				message: message,
				icon: "image/info.png"
			};
			$scope.dialogInfo = dialogInfo;
			$uibModal.open({templateUrl: "view/message.html", scope: $scope});
		},
		warn: function($scope, message) {
			var dialogInfo = {
				type: "warn",
				title: "警告メッセージ",
				message: message,
				icon: "image/alert.png"
			};
			$scope.dialogInfo = dialogInfo;
			$uibModal.open({templateUrl: "view/message.html", scope: $scope});
		},
		error: function($scope, message) {
			var dialogInfo = {
				type: "error",
				title: "エラーメッセージ",
				message: message,
				icon: "image/error.png"
			};
			$scope.dialogInfo = dialogInfo;
			$uibModal.open({templateUrl: "view/message.html", scope: $scope});
		}
	};
});

