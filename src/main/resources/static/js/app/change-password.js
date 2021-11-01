(function($){

    FormValidation.Validator.passwordValidator = {
        validate: function (validator, $field, options) {
            let value = $field.val();
            if ('' === value) return true;
            //Check the password strength
            if (value.length < 7) {
                return {valid: false, message: 'Password should be at least 7 characters long'};
            }
            //If password contains any uppercase letter
            if (value.toLowerCase() === value) {
                return {valid: false, message: 'Password must contain at least one uppercase character'};
            }
            //If password contains any lowercase letter
            if (value.toUpperCase() === value) {
                return {valid: false, message: 'Password must contain at least one lowercase character'};
            }
            //If password contains any digit
            if (value.search(/[0-9]/) < 0) {
                return {valid: false, message: 'Password must contain at least one digit'};
            }

            return true;
        }
    };

    $("form").formValidation({
        framework: 'bootstrap',
        fields: {
            password: {
                validators: {
                    notEmpty: {message: "Password is required"},
                    passwordValidator: {message: "Invalid password"}
                }
            },
            repeatPassword:{
                validators: { identical:{ field :"password", message:"Your password and confirmation password do not match"}}
            }
        }
    });

})(jQuery);