/*
 * Created by Max on 15-Aug-18.
 */
(function () {
    // get the module roles
    var _edit_role = $("#edit_role").val();
    var _deactivate_role = $("#deactivate_role").val();
    var _approve_new_role = $("#approve_new_role").val();
    var _decline_new_role = $("#decline_new_role").val();
    var _view_edited_role = $("#view_edited_role").val();
    var _view_deactivation_reason_role = $("#view_deactivation_reason_role").val();
    var _approve_deactivation_role = $("#approve_deactivation_role").val();
    var _decline_deactivation_role = $("#decline_deactivation_role").val();
    var _activate_role = $("#activate_role").val();
    var _delete_role = $("#delete_role").val();

    $('#btnClose').click(function () {
        AppUtils.refreshDataTables();
    });

    $(document).on("click", ".edit", function () {
        var frm = $("#frmEdit");
        var btnSaveEdit = $("#btnSaveEdit");
        var editModal = $('#editModal');

        var id = $(this).data('id');

        $.ajax({
            type: "GET",
            url: "/systemsettings/get-details/" + id,
            success: function (response) {
                if (response.status === '00') {
                    let ui = $('#editModal');
                    document.getElementById("entityID").value = id;
                    document.getElementById("editName").value = response.data.name;
                    document.getElementById("editValue").value = response.data.value;
                    document.getElementById("editDescription").value = response.data.description;
                    if (response.data.code === 'BANK_TRANSFERS_CHANNEL') {
                        $('#editValue').attr('disabled', true);
                        $('#editValue').addClass('d-none');
                        $('#selectInput').removeAttr('disabled');
                        $('.selectInput').removeClass('d-none');
                        $('#selectInput').empty();
                        $("#selectInput").append(new Option("M-pesa", "MPESA_B2B"));
                        $("#selectInput").append(new Option("Pesa Link", "PESALINK"));
                        $('select[name="value"]', ui).val(response.data.value);
                    }
                    else  if (response.data.code === 'ALLOW_MPESA_STK_LOAN_REPAYMENT') {
                        $('#editValue').attr('disabled', true);
                        $('#editValue').addClass('d-none');
                        $('#selectInput').removeAttr('disabled');
                        $('.selectInput').removeClass('d-none');
                        $('#selectInput').empty();
                        $("#selectInput").append(new Option("true", "true"));
                        $("#selectInput").append(new Option("false", "false"));
                        $('select[name="value"]', ui).val(response.data.value);
                    }
                    else  if (response.data.code === 'ALLOW_LOAD_ANOTHER_ACCOUNT') {
                        $('#editValue').attr('disabled', true);
                        $('#editValue').addClass('d-none');
                        $('#selectInput').removeAttr('disabled');
                        $('.selectInput').removeClass('d-none');
                        $('#selectInput').empty();
                        $("#selectInput").append(new Option("true", "true"));
                        $("#selectInput").append(new Option("false", "false"));
                        $('select[name="value"]', ui).val(response.data.value);
                    }
                    else  if (response.data.code === 'ALLOW_LOAN_REFUND') {
                        $('#editValue').attr('disabled', true);
                        $('#editValue').addClass('d-none');
                        $('#selectInput').removeAttr('disabled');
                        $('.selectInput').removeClass('d-none');
                        $('#selectInput').empty();
                        $("#selectInput").append(new Option("true", "true"));
                        $("#selectInput").append(new Option("false", "false"));
                        $('select[name="value"]', ui).val(response.data.value);
                    }
                    else {
                        $('#editValue').removeAttr('disabled');
                        $('#editValue').removeClass('d-none');
                        $('.selectInput').addClass('d-none');
                        $('#selectInput').attr('disabled', true);
                    }
                } else if (response.status === '01') {
                    editModal.modal('hide');
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
            },
            error: function (data) {
                editModal.modal('hide');
                AppUtils.ajaxErrorHandler(data);
            }
        });
    });

    // Form validation
    $('#frmEdit').formValidation({
        framework: "bootstrap",
        fields: {
            name: {
                validators: {
                    notEmpty: {
                        message: 'Setting Name is required'
                    }
                }
            },
            value: {
                validators: {
                    notEmpty: {
                        message: 'Setting Value is required'
                    }
                }
            }
        }
    }).on('success.form.fv', function (e) {
        e.preventDefault(); // to stop the form from submitting

        var $form = $(e.target);
        var editModal = $('#editModal');
        var id = $("#entityID").val();
        $.ajax({
            url: '/systemsettings/save',
            type: 'POST',
            data: $form.serialize(),
            success: function (response) {
                editModal.modal('hide');
                if (response.status === '00') {
                    AppUtils.sweetAlertMessage(response.message, 'success');
                } else if (response.status === '01') {
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
                AppUtils.refreshDataTables();
            },
            error: function (data) {
                AppUtils.ajaxErrorHandler(data);
            }

        });
    });

    /* Active data-tables */
    var active_options = {
        processing: true,
        serverSide: true,
        ajax: {
            url: "/systemsettings/list-all/1",
            dataSrc: function (json) {
                var return_data = [];
                var data = json.data;
                for (var i = 0; i < data.length; i++) {
                    return_data.push({
                        'names': '<span>' + data[i][0] + '</span></br><span class="text-muted">' + data[i][1] + '</span>',
                        'value': data[i][2],
                        'action': '<span><i class="mdi mdi-pencil edit" data-toggle="modal" data-name="' + data[i][0] + '" data-id="' + data[i][3] + '" data-target="#editModal" ></i></span>'// SwipeComponents.activeTableAction(data[i][8], names, _edit_role, _deactivate_role)
                    })
                }
                return return_data;
            }
        },
        columns: [
            {data: 'names'},
            {data: 'value'},
            {data: 'action'}
        ]
    };

    AppUtils.initDataTables($('#active_table'), active_options);
})();