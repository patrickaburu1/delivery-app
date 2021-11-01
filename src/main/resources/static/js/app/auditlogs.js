/*
 * Created by Max on 15-Aug-18.
 */
(function() {

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
            url: "/auditlogs/get-details/"+id,
            success: function ( response ) {
                if (response.status === '00') {
                    document.getElementById("entityID").value = id;
                    document.getElementById("editFirstName").value = response.data.firstName;
                    document.getElementById("editLastName").value = response.data.lastName;
                    document.getElementById("editEmail").value = response.data.email;
                    document.getElementById("editPhone").value = response.data.phone;
                    document.getElementById("createdOn").value = response.data.createdOn;
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

    /* Active data-tables */
    var active_options = {
        processing: true,
        serverSide: true,
        order: [[0, 'desc']],
        ajax:{
            url: "/auditlogs/list-all/1",
            dataSrc: function ( json ) {
                var return_data = [];
                var data = json.data;
                for(var i=0; i < data.length; i++){
                    var names = data[i][5] +" "+ data[i][6];
                    var email = (null !== data[i][8])?data[i][8]:'';
                    return_data.push( {
                        'date': data[i][0],
                        'user': data[i][1],
                        'desc': data[i][2],
                        'status': (data[i][3].toLowerCase() === 'success')?'<span class="label label-success">'+data[i][3]+'</span>':'<span class="label label-danger">'+data[i][3]+'</span>',
                        'oldValue': data[i][4],
                        'newValue': data[i][5],
                        'ip': data[i][6]
                    } )
                }
                return return_data;
            }
        },
        columns: [
            { data: 'date' },
            { data: 'user' },
            { data: 'desc' },
            { data: 'status' },
            { data: 'oldValue' },
            { data: 'newValue' },
            { data: 'ip' }
        ]
    };
    AppUtils.initDataTables($('#active_table'), active_options);
    $('.refresh').click(function () {
        AppUtils.refreshDataTable($('#active_table'));
    })
})();