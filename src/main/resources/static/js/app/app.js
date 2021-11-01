$(function(){
    // The logout script must be operational
    $('#login_form_submit').click(function(){ $('#logoutForm').submit(); });

    // set the csrf tokens
    var _tc = $("meta[name='_csrf']").attr("content");
    var _hc = $("meta[name='_csrf_header']").attr("content");

    // Header
    var headersStomp = {};
    headersStomp[_hc] = _tc;

    var dt_settings = $.fn.dataTable;
    if( 'undefined' !== typeof dt_settings){
        $.fn.dataTable.ext.errMode = 'none';
    }

    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(_hc, _tc);
    });

    $(document).ajaxComplete(function (e, xhr, options) {
        switch ( xhr.status){
            case 403: {
                // the session has expired. Redirect to the login page
                window.location = "/";
            }
            break;
            case 404:
            case 500:{
                if( typeof  dt_settings !== 'undefined'){
                    // this is a datatable error
                    console.log("This is a data table error");
                }
            }
            break;
            default:
                break;
        }
    });
});
