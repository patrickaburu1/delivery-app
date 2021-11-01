(function () {


        $('#btnClose').click(function () {
            AppUtils.refreshDataTables();
        });

        function actions(o, status, table) {
            var dataHtml = 'name="' + table + '"';
            var actions = "";

            actions = '<i id="' + o + '" class="fa fa-pencil icon-btn icon-btn-primary edit" data-index="' + o + '" data-name="System System" data-toggle="modal" data-target="#editModal" ' +
                'data-toggle="tooltip" data-placement="left" title="" data-original-title="Edit"> </i>&nbsp;&nbsp;';

            if (status === 'active')
                actions = actions + '<i id="'+o+'" class="fa fa-trash-o icon-btn deactivate" data-index="' + o + '" data-name="System System" data-toggle="tooltip" data-placement="left" title="" data-original-title="Deactivate"></i>';
            else
                actions = actions + '<i id="'+o+'" class="fa fa-check icon-btn activate" data-index="' + o + '" data-name="System System" data-toggle="tooltip" data-placement="left" title="" data-original-title="activate"></i>';

            return actions;

        }

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

        var all = {
            processing: true,
            serverSide: true,
            ajax: {
                url: "/vehicles-list",
                data: [
                    {'status': 'all'}
                ],
                dataSrc: function (json) {
                    var return_data = [];
                    var data = json.data;
                    for (var i = 0; i < data.length; i++) {
                        return_data.push({
                            'regNo': data[i][1],
                            'status': setStatus(data[i][2]),
                            'driver': data[i][3] + ' ' + data[i][4] + '<br/>' + data[i][5],
                            'dateCreated': data[i][6],
                            'action': actions(data[i][0], data[i][2], 'vehicles')
                        })
                    }
                    return return_data;
                }
            },
            columns: [
                {data: 'regNo'},
                {data: 'status'},
                {data: 'driver'},
                {data: 'dateCreated'},
                {data: 'action'}
            ]
        };

        AppUtils.initDataTables($('#vehicles'), all);


        $('#frmNewVehicle').formValidation({
            framework: "bootstrap",
            fields: {
                regNo: {
                    validators: {
                        notEmpty: {
                            message: 'Reg No cannot be empty'
                        }
                    }
                },
                vehicleType: {
                    validators: {
                        notEmpty: {
                            message: 'Vehicle Type cannot be empty'
                        }
                    }
                },
                vehicleCapacity: {
                    validators: {
                        notEmpty: {
                            message: 'Vehicle Capacity cannot be empty'
                        }
                    }
                }
            }
        }).on('success.form.fv', function (e) {
            e.preventDefault(); // to stop the form from submitting
            var $form = $(e.target);
            var modal = $('#newVehicleModal');
            $.ajax({
                url: '/new-vehicle',
                type: 'POST',
                data: $form.serialize(),
                success: function (response) {
                    if (response.status === '00') {
                        $form[0].reset();
                        modal.modal('hide');
                        AppUtils.sweetAlertMessage(response.message, 'success');
                        AppUtils.refreshDataTable($('#vehicles'));
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
                type: "GET",
                url: "/activate-deactivate/activate/" + id,
                success: function (response) {
                    if (response.status === '00') {
                        AppUtils.sweetAlertMessage(response.message, 'success');
                        AppUtils.refreshDataTable($('#vehicles'));
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
                type: "GET",
                url: "/activate-deactivate/deactivate/" + id,
                success: function (response) {
                    if (response.status === '00') {
                        AppUtils.sweetAlertMessage(response.message, 'success');
                        AppUtils.refreshDataTable($('#vehicles'));
                    } else if (response.status === '01') {
                        AppUtils.sweetAlertMessage(response.message, 'error');
                    }
                },
                error: function (data) {

                    AppUtils.ajaxErrorHandler(data);
                }
            });
        });

        $(document).on("click", ".edit", function () {
            var id = $(this).attr('id');
            $.ajax({
                type: "GET",
                url: "/vehicle-info/" + id,
                success: function (response) {
                    if (response.status === '00') {
                        var data = response.data;
                        $("#id").val(id);
                        $("#regNo1").val(data.regNo);
                        $("#status").val(data.status);
                        $("#assignedTo").val(data.assignedTo);
                        $("#lastModified").val(data.lastModifiedDate);

                    } else if (response.status === '01') {
                        AppUtils.sweetAlertMessage(response.message, 'error');
                    }
                },
                error: function (data) {

                    AppUtils.ajaxErrorHandler(data);
                }
            });
        });

        $('#editVehicleFrm').formValidation({
            framework: "bootstrap",
            fields: {
                regNo: {
                    validators: {
                        notEmpty: {
                            message: 'Reg No cannot be empty'
                        }
                    }
                }
            }
        }).on('success.form.fv', function (e) {
            e.preventDefault(); // to stop the form from submitting
            var $form = $(e.target);
            var modal = $('#editModal');
            $.ajax({
                url: '/edit-vehicle',
                type: 'POST',
                data: $form.serialize(),
                success: function (response) {
                    if (response.status === '00') {
                        $form[0].reset();
                        modal.modal('hide');
                        AppUtils.sweetAlertMessage(response.message, 'success');
                        AppUtils.refreshDataTable($('#vehicles'));
                    } else if (response.status === '01') {
                        AppUtils.sweetAlertMessage(response.message, 'error');
                    }
                },
                error: function (data) {
                    AppUtils.ajaxErrorHandler(data);
                }
            });
        });

    }

)();
