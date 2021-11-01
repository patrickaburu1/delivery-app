/*
 * Created by Max on 15-Aug-18.
 */
(function () {


    $('#btnClose').click(function () {
        AppUtils.refreshDataTables();
    });

    function actions(o, c, p, status, table) {
        // var dataHtml = 'data-index="' + o + '"';
        var dataHtml = 'name="' + table + '"';
        var actions = "";
        if (status === 'Pending') {
            actions += '<button  id="' + o + '" class="icon-btn btn  btn-sm fa fa-edit assignOrder" ' + dataHtml + '  data-toggle="modal" data-target="#assignDriverModal" ' +
                ' data-placement="left" title="">Assign</button>&nbsp;&nbsp;';
        }else if (status === 'Rejected') {
            actions += '<button  id="' + o + '" class="icon-btn btn  btn-sm fa fa-edit assignOrder" ' + dataHtml + '  data-toggle="modal" data-target="#assignDriverModal" ' +
                ' data-placement="left" title="">Re-Assign</button>&nbsp;&nbsp;';
        }
        else if (status === 'Assigned') {
            actions += '<button  id="' + o + '" class="icon-btn btn  btn-sm fa fa-edit assignOrder" ' + dataHtml + '  data-toggle="modal" data-target="#assignDriverModal" ' +
                ' data-placement="left" title="" disabled>Assign</button>&nbsp;&nbsp;';
        }else {
            actions += '<button  id="' + o + '" class="icon-btn btn  btn-sm fa fa-edit assignOrder" ' + dataHtml + '  data-toggle="modal" data-target="#assignDriverModal" ' +
                ' data-placement="left" title="" disabled>Assign</button>&nbsp;&nbsp;';
        }

      actions += '<button id="'+o+'"  class="icon-btn  btn-sm  viewMore"  >More Info</button>';

        return actions;
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
    var refreshUpdatedTable;


    /* Active data-tables */
    var all = {
        processing: true,
        serverSide: true,
        ajax: {
            url: "/orders-list",
            data: [
                {'status': 'all'}
            ],
            dataSrc: function (json) {
                var return_data = [];
                var data = json.data;
                for (var i = 0; i < data.length; i++) {
                    return_data.push({
                        'orderNo':  + data[i][1] ,
                        'company': data[i][2],
                        'status': orderState(data[i][3]),
                        'total': AppFormatUtils.formartNumber(data[i][4]),
                        'customerInfo': '<span>'+data[i][5]+' '+data[i][6]+' <br/>'+data[i][7]+'</span>',
                       // 'shipmentAddress': data[i][7],
                        'driverAssigned': '<span>'+data[i][8]+' <br/>'+data[i][9]+'</span>',
                        'date': data[i][10],
                        'completedOn':checkDeliveredDate(data[i][3], data[i][11]),
                        'action': actions(data[i][0], data[i][1], data[i][1], data[i][3], 'all_table')
                    })
                }
                return return_data;
            }
        },
        columns: [
            {data: 'orderNo'},
            {data: 'company'},
            {data: 'status'},
            {data: 'total'},
            {data: 'customerInfo'},
          //  {data: 'shipmentAddress'},
            {data: 'driverAssigned'},
            {data: 'date'},
            {data: 'completedOn'},
            {data: 'action'}
        ]
    };

    var pending = {
        processing: true,
        serverSide: true,
        ajax: {
            url: "/orders-list",
            data: [
                {'status': 'pending'}
            ],
            dataSrc: function (json) {
                var return_data = [];
                var data = json.data;
                for (var i = 0; i < data.length; i++) {
                    return_data.push({
                        'orderNo':  + data[i][1] ,
                        'company':   data[i][2] ,
                        'status': orderState(data[i][3]),
                        'total': data[i][4],
                        'customerInfo': '<span>'+data[i][5]+' '+data[i][6]+' <br/>'+data[i][7]+'</span>',
                        //'shipmentAddress': data[i][7],
                        'driverAssigned': '<span>'+data[i][8]+' <br/>'+data[i][9]+'</span>',
                        'date': data[i][10],
                        'action': actions(data[i][0], data[i][1], data[i][1], data[i][3], 'all_table')
                    })
                }
                return return_data;
            }
        },
        columns: [
            {data: 'orderNo'},
            {data: 'company'},
            {data: 'status'},
            {data: 'total'},
            {data: 'customerInfo'},
            //{data: 'shipmentAddress'},
            {data: 'driverAssigned'},
            {data: 'date'},
            {data: 'action'}
        ]
    };


    var completed = {
        processing: true,
        serverSide: true,
        ajax: {
            url: "/orders-list",
            data: [
                {'status': 'completed'}
            ],
            dataSrc: function (json) {
                var return_data = [];
                var data = json.data;
                for (var i = 0; i < data.length; i++) {
                    return_data.push({
                        'orderNo':  + data[i][1] ,
                        'company':  data[i][2] ,
                        'status': orderState(data[i][3]),
                        'total': data[i][4],
                        'customerInfo': '<span>'+data[i][5]+' '+data[i][6]+' <br/>'+data[i][7]+'</span>',
                        //'shipmentAddress': data[i][7],
                        'driverAssigned': '<span>'+data[i][8]+' <br/>'+data[i][9]+'</span>',
                        'date': data[i][10],
                        'completedOn':checkDeliveredDate(data[i][3], data[i][11]),
                        'action': actions(data[i][0], data[i][1], data[i][1], data[i][3], 'all_table')
                    })
                }
                return return_data;
            }
        },
        columns: [
            {data: 'orderNo'},
            {data: 'company'},
            {data: 'status'},
            {data: 'total'},
            {data: 'customerInfo'},
           // {data: 'shipmentAddress'},
            {data: 'driverAssigned'},
            {data: 'date'},
            {data: 'completedOn'},
            {data: 'action'}
        ]
    };

    AppUtils.initDataTables($('#all_table'), all);
    AppUtils.initDataTables($('#table_pending'), pending);
    AppUtils.initDataTables($('#table_completed'), completed);

    function showAvailableDrivers(drivers, selectId) {
        $("#" + selectId).empty();
        for (i = 0; i < drivers.length; i++) {
            var option = '<option value="' + drivers[i].id + '">' + drivers[i].userLink.firstName + ' '+ drivers[i].userLink.lastName +'</option>';
            $("#" + selectId).append(option);
        }
    }

    $(document).on("click", ".assignOrder", function (evt) {

        var id = $(this).attr('id');

        var editModal = $('#assignDriverModal');

        refreshUpdatedTable = $(this).attr('name');

        console.log("loaded");

        $.ajax({
            type: "GET",
            url: "/available-drivers",
            success: function (response) {
                if (response.status === '00') {
                    document.getElementById("entityID").value = id;

                    showAvailableDrivers(response.data, 'drivers');
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



    var driverValidationOptions = {
        driver: {
            validators: {
                notEmpty: {
                    message: 'Rider is required'
                }
            }
        }
    };

    // Form validation
    $('#frmAssignOrder').formValidation({
        framework: "bootstrap",
        fields: driverValidationOptions
    }).on('success.form.fv', function (e) {
        e.preventDefault(); // to stop the form from submitting
        var $form = $(e.target);
        var editModal = $('#assignDriverModal');
        var id = $("#entityID").val();

        $.ajax({
            url: '/assign-order',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(AppUtils.formObject($form)),
            success: function (response) {
                if (response.status === '00') {
                    AppUtils.sweetAlertMessage(response.message, 'success');
                    editModal.modal('hide');
                    AppUtils.refreshDataTable($('#all_table'));
                    AppUtils.refreshDataTable($('#table_pending'));
                    AppUtils.refreshDataTable($('#table_completed'));
                } else if (response.status === '01') {
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
                $("#assignOrder").removeAttr('disabled');
            },
            error: function (data) {
                AppUtils.ajaxErrorHandler(data);
            }

        });
    });


    $(document).on("click", ".viewMore", function () {

        $('#orderDetailsModal').modal('show');

        var id = $(this).attr('id');

        console.log("loaded");

        $.ajax({
            type: "GET",
            url: "/order-details/"+id,
            success: function (response) {
                if (response.status === '00') {
                    document.getElementById("orderNo").innerText=response.data.orderNo;
                    document.getElementById("orderState").innerText=response.data.orderState;
                    document.getElementById("deliveryDate").innerText= response.data.deliveryDate +' Time '+response.data.deliveryTimeFrom+':00 - '+response.data.deliveryTimeTo+':00';
                    document.getElementById("deliveryNote").innerText=response.data.deliveryNote;

                    document.getElementById("customerName").innerText=response.data.customerInfo.customerNames;
                    document.getElementById("customerPhone").innerText=response.data.customerInfo.customerPhone;

                    var  lng=response.data.customerInfo.deliveryAddress.lng;
                    var  lat=response.data.customerInfo.deliveryAddress.lat;

                    drawDeliveryLocation(lat,lng);

                    orderProducts(response.data.products);

                } else if (response.status === '01') {
                    AppUtils.sweetAlertMessage(response.message, 'error');
                }
            },
            error: function (data) {
                AppUtils.ajaxErrorHandler(data);
            }
        });
    });

    function orderProducts(products) {
        var  itemsSoldTable= $("#orderProducts tbody");
        itemsSoldTable.empty();
        for (i = 0; i < products.length; i++) {
            var item='<tr><td >'+products[i].name+'</td> <td >'+products[i].quantity+'</td>' +
                '<td >'+products[i].vendorName+'</td>';
            itemsSoldTable.append(item);
        }
    }


    function drawDeliveryLocation(lat, lng) {
        var myLatLng = { lat: lat, lng:lng };

        var map = new google.maps.Map(document.getElementById("deliveryMap"), {
            zoom: 15,
            center: myLatLng
        });

        var marker = new google.maps.Marker({
            position: myLatLng,
            map: map,
            title: "Delivery Location!"
        });
    }



})();
