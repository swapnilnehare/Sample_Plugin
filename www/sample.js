var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'sample', 'coolMethod', [arg0]);
};


module.exports.register = function (arg0, success, error){
    exec(success, error,'sample','register',[arg0]);
};