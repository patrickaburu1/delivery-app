/**
 * Created by Max on 13-Aug-18.
 */

function App_Components(){

    this.wizard_template_config = {
        buttons: function() {
            var options = this.options;
            return '<div class="wizard-buttons">' +
                '<a class="btn btn-default btn-outline" href="#' + this.id + '" data-wizard="back" role="button">' + options.buttonLabels.back + '</a>' +
                '<a class="btn btn-primary btn-outline pull-right" href="#' + this.id + '" data-wizard="next" role="button">' + options.buttonLabels.next + '</a>' +
                '<a class="btn btn-success btn-outline pull-right" id="wizard_finish" href="#' + this.id + '" data-wizard="finish" role="button">' + options.buttonLabels.finish + '</a>' +
                '</div>';
        }
    };

    this.wizard_steps_config = ".steps .step, .pearls .pearl";

    this.activeTableAction = function (id, name, edit_role, deactivate_role) {
        var returnStr = '<div class="dropdown"> ' +
            '<button type="button" class="btn btn-outline btn-primary dropdown-toggle btn-sm" id="exampleIconsDropdown" data-toggle="dropdown" aria-expanded="false"> Action<span class="caret"></span> </button> ' +
            '<ul class="dropdown-menu" aria-labelledby="exampleIconsDropdown" role="menu">';
        if( "true" === edit_role){ // if the user has been assigned the edit role, show the menu
            returnStr += '<li role="presentation"><a href="#" class="edit" data-toggle="modal" data-name="'+name+'" data-id="'+ id+'" data-target="#editModal" role="menuitem">' +
                '<i class="icon wb-pencil" aria-hidden="true"></i> Edit </a></li> ';
        }
        if( "true" === deactivate_role){
            returnStr += '<li role="presentation"><a href="#" class="deactivate" data-toggle="modal" data-name="'+name+'" data-id="'+ id +'" data-target="#deactivateModal" role="menuitem">' +
                '<i class="icon wb-trash"  aria-hidden="true"></i> Deactivate</a></li> ';
        }
        returnStr += '</ul></div>';
        return returnStr;
    };

    this.newTableAction = function(id, name, approve_new_role, decline_new_role){
        var actionStr = '<div class="dropdown"> ' +
            '<button type="button" class="btn btn-outline btn-primary dropdown-toggle btn-sm" id="exampleIconsDropdown" data-toggle="dropdown" aria-expanded="false"> Action <span class="caret"></span> </button> ' +
            '<ul class="dropdown-menu" aria-labelledby="exampleIconsDropdown" role="menu">';
        if( "true" === approve_new_role ){
            actionStr +='<li role="presentation"><a class="approveNew" href="#" data-toggle="modal" data-name="' + name + '" data-id="' + id + '" data-target="#approveNewModal" role="menuitem">' +
                '<i class="icon wb-reply" aria-hidden="true"></i> Approve</a></li> ';
        }
        if( "true" === decline_new_role ){
            actionStr += '<li role="presentation"><a class="declineNew" href="#" data-toggle="modal" data-name="' + name + '" data-id="' + id + '" data-target="#declineNewModal" role="menuitem">' +
                '<i class="icon wb-trash" aria-hidden="true"></i> Decline </a></li> ';
        }
        actionStr += '</ul></div>';
        return actionStr;
    };

    this.editedTableAction = function(id, name, view_edited_role ){
        return ("true" === view_edited_role)?'<button type="button" data-toggle="modal" data-name="'+ name +'" data-id="'+
            id +'" data-target="#viewChangesModal" class="btn btn-primary btn-sm viewChanges">' +
            '<i class="icon wb-eye" aria-hidden="true"></i> ' +
            'View Changes</button>':"Insufficient Privileges";
    };

    this.deactivationRequestsTableAction = function(id, name, view_deactivation_reason_role ){
        return ("true" === view_deactivation_reason_role )? '<button type="button" data-toggle="modal" data-name="'+name+'" data-id="'+
            id +'" data-target="#deactivationActionModal" class="btn btn-primary btn-sm deactivationAction">' +
            '<i class="icon wb-eye" aria-hidden="true"></i> ' +
            'View Reason</button>':"Insufficient Privileges";

    };

    this.inactiveTableAction = function(id, name, _activate_role, _delete_role ){
        var actionStr = '<div class="dropdown"> ' +
            '<button type="button" class="btn btn-outline btn-primary dropdown-toggle btn-sm" id="exampleIconsDropdown" data-toggle="dropdown" aria-expanded="false"> Action <span class="caret"></span> </button> ' +
            '<ul class="dropdown-menu" aria-labelledby="exampleIconsDropdown" role="menu">';
        if( "true" === _activate_role){
            actionStr+='<li role="presentation"><a class="activate" href="#" data-toggle="modal" data-name="'+name+'" data-id="'+id+'" data-target="#activateModal" role="menuitem">' +
                '<i class="icon wb-reply" aria-hidden="true"></i> Activate </a></li> ';
        }
        if( "true" === _delete_role){
            actionStr+='<li role="presentation"><a class="delete" href="#" data-toggle="modal" data-name="'+name+'" data-id="'+ id +'" data-target="#deleteModal" role="menuitem">' +
                '<i class="icon wb-trash" aria-hidden="true"></i> Delete </a></li> ';
        }
        actionStr += '</ul></div>';
        return actionStr;
    };

    this.deactivationButtonAction = function (_approve_deactivation_role, _decline_deactivation_role) {

        var btnApproveDeactivation = $('#btnApproveDeactivation');
        var btnDeclineDeactivation = $('#btnDeclineDeactivation');
        if("true" === _approve_deactivation_role)
            btnApproveDeactivation.show();
        else
            btnApproveDeactivation.hide();
        if("true" === _decline_deactivation_role)
            btnDeclineDeactivation.show();
        else
            btnDeclineDeactivation.hide();
        
    };
}

var AppComponents = new App_Components();