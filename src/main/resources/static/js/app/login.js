(function ($) {

    $("#frmLogin").formValidation({
        framework: 'bootstrap',
        fields: {
            password: {
                validators: {
                    notEmpty: { message: "Password is required" },
                }
            },
            email: {
                validators: {
                    notEmpty: {message: "Email is required"}
                }
            }
        }
    }).on('success.form.fv', function(e) {
        // Prevent form submission
        // e.preventDefault();
        //
        // var $form = $(e.target);
        // // Use Ajax to submit form data
        // $.ajax({
        //     url: "/login",
        //     type: 'POST',
        //     data: $form.serialize(),
        //     success: function( response ) {
        //         if (response.status === '00') {
        //             $form[0].reset();
        //             $('#createModal').modal('hide');
        //             AppUtils.sweetAlertMessage(response.message, 'success');
        //         } else if (response.status === '01') {
        //             AppUtils.sweetAlertMessage(response.message, 'error');
        //         }
        //         AppUtils.refreshDataTables();
        //     },
        //     error: function (data) {
        //         AppUtils.ajaxErrorHandler( data );
        //     }
        // });
    });
})(jQuery);