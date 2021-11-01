/**
 * Created by Max on 13-Aug-18.
 */
function App_Utils(){

    var active_table = $('#active_table');
    var new_table = $('#newly_created_table');
    var edited_table = $('#edited_table');
    var deactivation_request_table = $('#deactivation_request_table');
    var inactive_table = $('#inactive_table');

    this.formObject = function objectifyForm(formObject) {//serialize data function
        var returnArray = {};
        var formArray = formObject.serializeArray();
        for (var i = 0; i < formArray.length; i++){
            returnArray[formArray[i]['name']] = ""+formArray[i]['value'];
        }
        return returnArray;
    };

    /* A simple sweet alert message */
    this.sweetAlertMessage = function(message, type){
        swal({
            title: "",
            text:message,
            type: type
        });
    };

    this.sweetAlertMessageWithTitle = function( message, title, type){
        swal({
                title: title,
                text: message,
                animation: false,
                type: type
            }
        );
    };

    this.ajaxErrorHandler = function ( data ) {
        switch (data.status){
            case 404:
                this.sweetAlertMessageWithTitle("Could not serve the request since the URL is unavailable", "404", 'warning');
                break;
            case 500:
                this.sweetAlertMessageWithTitle(data.statusText, "An Exception has occurred. Contact Admin", 'warning');
                break;
            default:
                this.sweetAlertMessageWithTitle(data.statusText, "An Exception has occurred. Contact Admin", 'warning');
                break;

        }
    };

    this.populateTwoSelectBoxes = function (selectBoxID, selectBoxID2, url, text, id) {
        var placeholder = '<option value=""> -- Select '+text+' -- </option>';
        selectBoxID.find('option')
            .remove()
            .end()
            .append(placeholder)
        ;
        selectBoxID2.find('option')
            .remove()
            .end()
            .append(placeholder);

        $.ajax({
            type: "GET",
            url: url,
            data: {id: id},
            success: function ( response ) {
                for (var i = 0; i < response.length; i++) {
                    if( url.indexOf("accounts") !== -1){
                        selectBoxID.append($("<option />").val(response[i].id).text(response[i].accountNumber + " - " +response[i].accountName));
                        selectBoxID2.append($("<option />").val(response[i].id).text(response[i].accountNumber + " - " +response[i].accountName));
                    }else{
                        selectBoxID.append($("<option />").val(response[i].id).text(response[i].name));
                        selectBoxID2.append($("<option />").val(response[i].id).text(response[i].name));
                    }
                }
            },
            error: function (data) {
                SwipeUtils.ajaxErrorHandler(data);
            }

        });
    };

    this.populateSelectBox = function (selectBoxID, url, text, id) {
        selectBoxID.find('option')
            .remove()
            .end()
            .append('<option value=""> -- Select '+text+' -- </option>')
        ;

        $.ajax({
            type: "GET",
            url: url,
            data: {id: id},
            success: function ( response ) {
                for (var i = 0; i < response.length; i++) {
                    if( url.indexOf("accounts") !== -1){
                        selectBoxID.append($("<option />").val(response[i].id).text(response[i].accountNumber + " - " +response[i].accountName));
                    }else{
                        selectBoxID.append($("<option />").val(response[i].id).text(response[i].name));
                    }
                }
            },
            error: function (data) {
                SwipeUtils.ajaxErrorHandler(data);
            }

        });
    };

    /* Allow numbers input only */
    this.acceptNumbersInputOnly = function( element ){
        element.keypress(function(event){
            var key = window.event ? event.keyCode : event.which;

            if (event.keyCode === 8 || event.keyCode === 46
                || event.keyCode === 37 || event.keyCode === 39) {
                return true;
            }
            else if ( key < 48 || key > 57 ) {
                return false;
            }
            else return true;
        });
    };

    this.addKeyValueToAForm = function(theForm, key, value) {
        // Create a hidden input element, and append it to the form:
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = value;
        theForm.appendChild(input);
    };

    this.capitalizeFirstLetter = function (string) {
        return string.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
    };

    this.refreshDataTables =  function () {
        active_table.DataTable().ajax.reload();
        new_table.DataTable().ajax.reload();
        edited_table.DataTable().ajax.reload();
        deactivation_request_table.DataTable().ajax.reload();
        inactive_table.DataTable().ajax.reload();
    };

    this.refreshDataTable =  function (table) {
        table.DataTable().ajax.reload();
    };

    this.initDataTables = function (table, options) {
        options.autoWidth = false;
        options.language = {
            processing: "<img src='../../images/loader.gif'>"
        };
       return table.DataTable( options );
    };

    this.removeData = function (table) {
        table.DataTable().destroy();
    };
}

Number.prototype.formatNumber = function( toFixed ) {

    var n = this,
        c = ("undefined" !== typeof toFixed)?toFixed:2,
        d = ".",
        t = ",",
        s = n < 0 ? "-" : "",
        i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "",
        j = (j = i.length) > 3 ? j % 3 : 0;
    return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
};

var AppUtils = new App_Utils();
