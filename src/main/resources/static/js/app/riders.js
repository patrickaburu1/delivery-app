/*
 * Created by Max on 15-Aug-18.
 */
(function () {

    var activeType = "ACTIVE";
    var blockedType = "BLOCKED";
    var archivedType = "ARCHIVED";

    $('#btnClose').click(function () {
        AppUtils.refreshDataTables();
    });

    function actions(o, c, p, status, table) {
        // var dataHtml = 'data-index="' + o + '"';
        var dataHtml = 'name="' + table + '"';
        var actions = "";

        actions += '<button  id="' + o + '" class="icon-btn btn  btn-sm fa fa-edit editRider" ' + dataHtml + '  data-toggle="modal" data-target="#editModal" ' +
            ' data-placement="left" title="" >Edit</button>&nbsp;&nbsp;';

        actions += '<button  id="' + o + '" class="icon-btn btn  btn-sm fa fa- riderOrders" ' + dataHtml + '  data-toggle="modal" data-target="#driverOrdersModal" ' +
            ' data-placement="left" title="" >Orders</button>&nbsp;&nbsp;';

        return actions;
    }

    function setStatus(status) {

        if (status === 'active') {
            status = '<span  class="text-success"' +
                ' data-placement="left" title="" >' + status + '</span>&nbsp;&nbsp;';
        } else {
            status = '<span   class="text-warning"' +
                ' data-placement="left" title="" >' + status + '</span>&nbsp;&nbsp;';
        }

        return status;
    }

    function riderState(state) {

        if (state === 'Off') {
            state = '<span  class="btn-sm btn-danger"' +
                ' data-placement="left" title="" >' + state + '</span>&nbsp;&nbsp;';
        } else if (state === 'Available') {
            state = '<span class="btn-sm btn-info"' +
                ' data-placement="left" title="" >' + state + '</span>&nbsp;&nbsp;';
        }
        else if (state === 'Assigned') {
            state = '<span class="btn-sm btn-warning"' +
                ' data-placement="left" title="" >' + state + '</span>&nbsp;&nbsp;';
        }
        else if (state === 'Enroute') {
            state = '<span class="btn-sm btn-success"' +
                ' data-placement="left" title="" >' + state + '</span>&nbsp;&nbsp;';
        }
        else if (state === 'Engaged') {
            state = '<span class="btn-sm btn-dark"' +
                ' data-placement="left" title="" >' + state + '</span>&nbsp;&nbsp;';
        } else {
            state = '<span class="text-info"' +
                ' data-placement="left" title="" >' + state + '</span>&nbsp;&nbsp;';
        }


        return state;
    }

    var refreshUpdatedTable;

    /* Active data-tables */
    var all = {
        processing: true,
        serverSide: true,
        ajax: {
            url: "/drivers-list",
            data: [
                {'status': 'all'}
            ],
            dataSrc: function (json) {
                var return_data = [];
                var data = json.data;
                for (var i = 0; i < data.length; i++) {
                    return_data.push({
                        'names': '<span>' + data[i][1] + ' ' + data[i][2] + '</span>',
                        'contact': '<span>' + data[i][3] + ' <br/>' + data[i][4] + '</span>',
                        //'status':  setStatus(data[i][5]),
                        'state': riderState(data[i][5]),
                        'motorAssigned': data[i][6],
                        'date': data[i][7],
                        'action': actions(data[i][0], data[i][1], data[i][1], data[i][5], 'all_table')
                    })
                }
                return return_data;
            }
        },
        columns: [
            {data: 'names'},
            {data: 'contact'},
            // {data: 'status'},
            {data: 'state'},
            {data: 'motorAssigned'},
            {data: 'date'},
            {data: 'action'}
        ]
    };

    var available = {
        processing: true,
        serverSide: true,
        ajax: {
            url: "/drivers-list",
            data: [
                {'status': 'available'}
            ],
            dataSrc: function (json) {
                var return_data = [];
                var data = json.data;
                for (var i = 0; i < data.length; i++) {
                    return_data.push({
                        'names': '<span>' + data[i][1] + ' ' + data[i][2] + '</span>',
                        'contact': '<span>' + data[i][3] + ' <br/>' + data[i][4] + '</span>',
                        //'status':  setStatus(data[i][5]),
                        'state': riderState(data[i][5]),
                        'motorAssigned': data[i][6],
                        'date': data[i][7],
                        'action': actions(data[i][0], data[i][1], data[i][1], data[i][5], 'all_table')
                    })
                }
                return return_data;
            }
        },
        columns: [
            {data: 'names'},
            {data: 'contact'},
            // {data: 'status'},
            {data: 'state'},
            {data: 'motorAssigned'},
            {data: 'date'},
            {data: 'action'}
        ]
    };

    AppUtils.initDataTables($('#all_table'), all);
    AppUtils.initDataTables($('#available_table'), available);

    $(document).on("click", ".editRider", function () {
        var id = $(this).attr('id');
        $.ajax({
            type: "GET",
            url: "/driver-details/" + id,
            success: function (response) {
                if (response.status === '00') {
                    var data = response.data;
                    motorBikes(id);
                    $("#id").val(id);
                    $("#firstName").val(data.firstName);
                    $("#lastName").val(data.lastName);
                    $("#email").val(data.email);
                    $("#phoneNumber").val(data.phone);
                    $("#status").val(data.status);
                    $("#state").val(data.driverState);
                    $("#motorbikes").val(data.vehicleId);

                } else if (response.status === '01') {
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
            },
            error: function (data) {

                AppUtils.ajaxErrorHandler(data);
            }
        });

        $('#editRiderFrm').formValidation({
            framework: "bootstrap",
            fields: {
                status: {
                    validators: {
                        notEmpty: {
                            message: 'Driver status cannot be empty'
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
                url: '/update-driver',
                type: 'POST',
                data: $form.serialize(),
                success: function (response) {
                    if (response.status === '00') {
                        $form[0].reset();
                        modal.modal('hide');
                        AppUtils.sweetAlertMessage(response.message, 'success');
                        AppUtils.refreshDataTable($('#all_table'));
                        AppUtils.refreshDataTable($('#available_table'));
                    } else if (response.status === '01') {
                        AppUtils.sweetAlertMessage(response.message, 'error');
                    }
                },
                error: function (data) {
                    AppUtils.ajaxErrorHandler(data);
                }
            });
        });

    });

    $(document).on("click", ".riderOrders", function () {
        var id = $(this).attr('id');
        AppUtils.removeData($('#rider_orders'));
        AppUtils.initDataTables($('#rider_orders'),  driverOrders(id));
    });

    function driverOrders(driverId) {

        return {
            processing: true,
            serverSide: true,
            ajax: {
                url: "/driver-orders/"+driverId,
                dataSrc: function (json) {
                    var return_data = [];
                    var data = json.data;
                    for (var i = 0; i < data.length; i++) {
                        return_data.push({
                            'orderNo': +data[i][1],
                            'status': orderState(data[i][2]),
                            'customerInfo': '<span>' + data[i][3] + ' ' + data[i][4] + ' <br/>' + data[i][5] + '</span>',
                            'shipmentAddress': data[i][6],
                            'date': data[i][7],
                            'completedOn': checkDeliveredDate(data[i][2], data[i][8])
                        })
                    }
                    return return_data;
                }
            },
            columns: [
                {data: 'orderNo'},
                {data: 'status'},
                {data: 'customerInfo'},
                {data: 'shipmentAddress'},
                {data: 'date'},
                {data: 'completedOn'}
            ]
        };
    }

    function orderState(state) {

        if (state==='Pending') {
            state= '<span  class="btn-sm btn-danger"' +
                ' data-placement="left" title="" >'+state+'</span>&nbsp;&nbsp;';
        }else if (state==='Rejected') {
            state= '<span  class="btn-sm btn-danger"' +
                ' data-placement="left" title="" >'+state+'</span>&nbsp;&nbsp;';
        }
        else if (state === 'Assigned') {
            state= '<span class="btn-sm  btn-warning"' +
                ' data-placement="left" title="" >'+state+'</span>&nbsp;&nbsp;';
        }

        else if (state === 'Accepted') {
            state= '<span class="btn-sm btn-info"' +
                ' data-placement="left" title="" >'+state+'</span>&nbsp;&nbsp;';
        }
        else if (state === 'In-transit') {
            state= '<span class="btn-sm btn-primary"' +
                ' data-placement="left" title="" >'+state+'</span>&nbsp;&nbsp;';
        }
        else if (state === 'Completed') {
            state= '<span class="btn-sm btn-success"' +
                ' data-placement="left" title="" >'+state+'</span>&nbsp;&nbsp;';
        }
        else {
            state= '<span class="btn-sm btn-dark"' +
                ' data-placement="left" title="" >'+state+'</span>&nbsp;&nbsp;';
        }

        return state;
    }
    function checkDeliveredDate(state,date) {
        if (state === 'Completed') {
            return  date;
        }
        return " ";
    }

    $('#frmNewRider').formValidation({
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
            }
        }
    }).on('success.form.fv', function (e) {
        e.preventDefault(); // to stop the form from submitting
        var $form = $(e.target);
        var modal = $('#newRiderModal');
        $.ajax({
            url: '/new-rider',
            type: 'POST',
            data: $form.serialize(),
            success: function (response) {
                if (response.status === '00') {
                    $form[0].reset();
                    modal.modal('hide');
                    AppUtils.sweetAlertMessage(response.message, 'success');
                    AppUtils.refreshDataTable($('#all_table'));
                } else if (response.status === '01') {
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
            },
            error: function (data) {
                AppUtils.ajaxErrorHandler(data);
            }
        });
    });


   function motorBikes(riderId) {
        $.ajax({
            type: "GET",
            url: "/available-bikes/"+riderId,
            success: function (response) {
                if (response.status === '00') {
                    showAvailableMotorBikes(response.data, 'motorbikes');
                } else if (response.status === '01') {
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
            },
            error: function (data) {
                AppUtils.ajaxErrorHandler(data);
            }
        });
    }

    function showAvailableMotorBikes(bike, selectId) {
        $("#" + selectId).empty();
        var option = '<option value="0"> None </option>';
        $("#" + selectId).append(option);
        for (var i = 0; i < bike.length; i++) {
            var options = '<option value="' + bike[i].id + '">' + bike[i].regNo +'</option>';
            $("#" + selectId).append(options);
        }
    }


})();
