/*
 * Created by Max on 15-Aug-18.
 */
(function () {


    $('#btnClose').click(function () {
        AppUtils.refreshDataTables();
    });

    function actions(o, status, table) {
        var dataHtml = 'name="' + table + '"';
        var actions = "";

     //   actions = '<i id="' + o + '" class="fa fa-pencil icon-btn icon-btn-primary edit"   data-toggle="tooltip"  data-original-title="Edit"  data-toggle="modal" data-target="#editModal">';

        actions += '<button  id="' + o + '" class="icon-btn btn  btn-sm fa fa-pencil  editUser" ' + dataHtml + '  data-toggle="modal" data-target="#editModal" ' +
            ' data-placement="left" title="" >Edit</button>&nbsp;&nbsp;';

        if (status === 'active') {
            actions = actions + '</i>&nbsp;&nbsp;<i  id="' + o + '" class="fa fa-trash-o icon-btn deactivate" data-index="' + o + '" data-name="System System" data-toggle="tooltip" data-placement="left" title="" data-original-title="Deactivate"></i>';
        }else {
            actions = actions + '</i>&nbsp;&nbsp;<i  id="' + o + '" class="fa fa-check icon-btn activate" data-index="' + o + '" data-name="" data-toggle="tooltip" data-placement="left" title="" data-original-title="Activate"></i>';

        }

        return actions;
    }

    var all = {
        processing: true,
        serverSide: true,
        ajax: {
            url: "/backend-users",
            data: [
                {'status': 'all'}
            ],
            dataSrc: function (json) {
                var return_data = [];
                var data = json.data;
                for (var i = 0; i < data.length; i++) {
                    return_data.push({
                        'names': data[i][1] + ' ' + data[i][2],
                        'phone': data[i][3],
                        'email': data[i][4],
                        'userGroup': data[i][5],
                        'status': setStatus(data[i][6]),
                        'dateCreated': data[i][7],
                        'action': actions(data[i][0], data[i][6],'backend_users_t')
                    })
                }
                return return_data;
            }
        },
        columns: [
            {data: 'names'},
            {data: 'phone'},
            {data: 'email'},
            {data: 'userGroup'},
            {data: 'status'},
            {data: 'dateCreated'},
            {data: 'action'}
        ]
    };


    AppUtils.initDataTables($('#backend_users_t'), all);


    function setStatus(status) {
        if (status === 'active') {
            status = '<span  class="text-success"' +
                ' data-placement="left" title="" >' + status + '</span>&nbsp;&nbsp;';
        } else {
            status = '<span   class="text-danger"' +
                ' data-placement="left" title="" >' + status + '</span>&nbsp;&nbsp;';
        }
        return status;
    }

    $('#frmNewUser').formValidation({
        framework: "bootstrap",
        fields: {
            firstName: {
                validators: {
                    notEmpty: {
                        message: 'First Name cannot be empty'
                    }
                }
            },
            lastName: {
                validators: {
                    notEmpty: {
                        message: 'Last Name  cannot be empty'
                    }
                }
            },
            phone: {
                validators: {
                    notEmpty: {
                        message: 'Phone Number  cannot be empty'
                    }
                }
            },
            email: {
                validators: {
                    notEmpty: {
                        message: 'Email cannot be empty'
                    }
                }
            },
            usergroupNo: {
                validators: {
                    notEmpty: {
                        message: 'Select user group'
                    }
                }
            }
        }
    }).on('success.form.fv', function (e) {
        e.preventDefault(); // to stop the form from submitting
        var $form = $(e.target);
        var modal = $('#newUserModal');
        $.ajax({
            url: '/new-user',
            type: 'POST',
            data: $form.serialize(),
            success: function (response) {
                if (response.status === '00') {
                    $form[0].reset();
                    modal.modal('hide');
                    AppUtils.sweetAlertMessage(response.message, 'success');
                    AppUtils.refreshDataTable($('#backend_users_t'));
                } else if (response.status === '01') {
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
            },
            error: function (data) {
                AppUtils.ajaxErrorHandler(data);
            }
        });
    });

    $(document).on("click", ".editUser", function () {
        var id = $(this).attr('id');
        $.ajax({
            type: "GET",
            url: "/user-details/" + id,
            success: function (response) {
                if (response.status === '00') {
                    var data = response.data;
                    $("#entityId").val(id);
                    $("#fname").val(data.firstName);
                    $("#lname").val(data.lastName);
                    $("#emaill").val(data.email);
                    $("#phoneNumber").val(data.phone);
                } else if (response.status === '01') {
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
            },
            error: function (data) {

                AppUtils.ajaxErrorHandler(data);
            }
        });
    });

    $(document).on("click", ".activate", function () {
        var id = $(this).attr('id');
        $.ajax({
            type: "POST",
            url: "/edit-user/activate/" + id,
            success: function (response) {
                if (response.status === '00') {
                    AppUtils.refreshDataTable($('#backend_users_t'));
                } else if (response.status === '01') {
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
            },
            error: function (data) {

                AppUtils.ajaxErrorHandler(data);
            }
        });
    });

    $(document).on("click", ".deactivate", function () {
        var id = $(this).attr('id');
        $.ajax({
            type: "POST",
            url: "/edit-user/deactivate/" + id,
            success: function (response) {
                if (response.status === '00') {
                    AppUtils.refreshDataTable($('#backend_users_t'));
                } else if (response.status === '01') {
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
            },
            error: function (data) {

                AppUtils.ajaxErrorHandler(data);
            }
        });
    });

    $('#editUserFrm').formValidation({
        framework: "bootstrap",
        fields: {
            usergroupNo: {
                validators: {
                    notEmpty: {
                        message: 'User group cannot be empty'
                    }
                }
            },
            firstName: {
                validators: {
                    notEmpty: {
                        message: 'First Name  cannot be empty'
                    }
                }
            },
            lastName: {
                validators: {
                    notEmpty: {
                        message: 'Last Name  cannot be empty'
                    }
                }
            }
        }
    }).on('success.form.fv', function (e) {
        e.preventDefault(); // to stop the form from submitting
        var $form = $(e.target);
        var modal = $('#editModal');
        $.ajax({
            url: '/edit-user',
            type: 'POST',
            data: $form.serialize(),
            success: function (response) {
                if (response.status === '00') {
                    $form[0].reset();
                    modal.modal('hide');
                    AppUtils.sweetAlertMessage(response.message, 'success');
                    AppUtils.refreshDataTable($('#backend_users_t'));
                } else if (response.status === '01') {
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
            },
            error: function (data) {
                AppUtils.ajaxErrorHandler(data);
            }
        });
    });


})();
