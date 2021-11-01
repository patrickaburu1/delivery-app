/*
The MIT License

Copyright 2016 Ken Gichia.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
(function ($) {
    $.fn.ajaxGrid = function (p) {
        // The parameters that will be used
        var
            e = this, cols = $('thead > tr > th', e).length,
            viewModal = $('.view-modal', e), editModal = $('.edit-modal', e),
            inactiveRecordsDisplay = false, timeOut = {},
            makerChecker = ( typeof(p.makerChecker) === 'undefined' ) ? true : p.makerChecker,
            currentSource = (typeof (e.data('base-url')) !== 'undefined') ? e.data('base-url') : window.location.pathname,
            oTable;

        // Initialise the approve change grid
        function approveChange(nRow, aData) {
            var html = '', dataHtml = 'data-index="' + aData[(cols - 1)] + '" data-status="' + aData[(cols + 1)] + '"';
            var approve = ' data-toggle="tooltip" data-placement="left" title="Approve"';
            var decline = ' data-toggle="tooltip" data-placement="left" title="Decline"';

            if (aData[cols] === "approve-deny")
                html = '<i class="fa fa-times icon-btn-danger" aria-hidden="true" ' + dataHtml + decline + '></i>&nbsp;&nbsp;<i class="fa fa-check" aria-hidden="true"' + dataHtml + approve + '></i>';
            else if (aData[cols] === "approve")
                html = '<i class="fa fa-check icon-btn-success" aria-hidden="true" ' + dataHtml + approve + '></i>';
            else
                html = '<i class="fa fa-times icon-btn-danger" aria-hidden="true" ' + dataHtml + decline + '></i>';

            // Set the html
            $(nRow).children('td:eq(' + (cols - 1) + ')').html(html);

            // attach the click event on the button approval
            $('.icon-btn', nRow).tooltip().click(function () {
                // The modal to use
                var modal = (aData[cols + 1] === "update") ? viewModal : editModal;

                // Identify whether it is an approval or decline
                $('input[name="action"]', modal).val($(this).hasClass('fa fa-check') ? 'approve' : 'deny');

                // The button to show
                $('button[type="submit"]', modal).addClass('hide');
                if ($(this).hasClass('icon-btn-success')) $('button.btn-success', modal).removeClass('hide');
                else if ($(this).hasClass('icon-btn-danger')) $('button.btn-danger', modal).removeClass('hide');

                // Ensure that one can not edit the modal content
                toggleReadonly(modal, true);

                // Show the relevant modal
                if (typeof(p.fnViewChange) !== 'undefined')
                    p.fnViewChange($(this).data('index'), $(this).data('status'), modal);

                // Show the alert message to indicate that one should set 
                // approve method
                else console.log("Please set the view change method.");
            });
        }

        // Initialise the view change option
        function viewChange(nRow, aData) {
            $(nRow).children('td:eq(' + (cols - 1) + ')').html('<i class="fa fa-eye icon-btn icon-btn-primary" data-index="' + aData[(cols - 1)] + '" data-status="' + aData[(cols + 1)] + '" data-toggle="tooltip" data-placement="left" title="View"></i>');

            // attach the click event on the button approval
            $('.icon-btn', nRow).tooltip().click(function () {
                // The modal to use
                var modal = (aData[cols + 1] === "update") ? viewModal : editModal;

                // The button to show
                $('button[type="submit"]', modal).addClass('hide');

                // Ensure that one can not edit the modal content
                toggleReadonly(modal, true);

                // Show the relevant modal
                if (typeof(p.fnViewChange) !== 'undefined')
                    p.fnViewChange($(this).data('index'), $(this).data('status'), modal);

                // Show the alert message to indicate that one should set 
                // approve method
                else console.log("Please set the view change method.");
            });
        }

        // Initialise the make change option
        function makeChange(nRow, aData) {
            // The html
            var dataHtml = 'data-index="' + aData[(cols - 1)] + '" data-status="' + aData[(cols + 1)] + '"';
            var del = ( !inactiveRecordsDisplay && makerChecker ) ? 'Deactivate' : 'Delete';

            $(nRow).children('td:eq(' + (cols - 1) + ')')
                .html('<i class="fa fa-pencil icon-btn icon-btn-primary" ' + dataHtml + ' data-toggle="tooltip" data-placement="left" title="Edit"></i>&nbsp;&nbsp;<i class="fa fa-trash icon-btn" ' + dataHtml + ' data-toggle="tooltip" data-placement="left" title="' + del + '"></i>');

            // Select the items to delete
            $('.fa fa-trash', nRow).tooltip().click(function () {
                var t = ( !inactiveRecordsDisplay && makerChecker ) ? 'warning' : 'danger';

                showPopover('btn-' + t, 'text-' + t);
                $(this).toggleClass('text-' + t);
            });

            // Show the edit modal
            $('.fa fa-pencil', nRow).tooltip().click(function () {
                // Set the right action
                resetModal(editModal);
                $('input[name="action"]', editModal).val('edit');

                // The button to show
                $('button[type="submit"]', editModal).addClass('hide');
                $('button.btn-info', editModal).removeClass('hide');

                // Show the relevant modal
                if (typeof(p.fnEditForm) !== 'undefined')
                    p.fnEditForm($(this).data('index'), editModal);

                // Show the alert message to indicate that one should set 
                // approve method
                else console.log("Please set the make change method.");
            });
        }

        // Initialise the controls when displaying approval process for inactive
        // records
        function approveInactive(nRow, aData) {
            var html = '', dataHtml = 'data-index="' + aData[(cols - 1)] + '" data-status="' + aData[(cols + 1)] + '"';
            var approve = ' data-toggle="tooltip" data-placement="left" title="Activate"';
            var decline = ' data-toggle="tooltip" data-placement="left" title="Delete"';

            if (aData[cols] === "approve-deny")
                html = '<i class="fa fa-times" ' + dataHtml + decline + '></i>&nbsp;&nbsp;<i class="fa fa-check icon-btn" ' + dataHtml + approve + '></i>';
            else if (aData[cols] === "approve")
                html = '<i class="fa fa-check icon-btn" ' + dataHtml + approve + '></i>';
            else
                html = '<i class="fa fa-times" ' + dataHtml + decline + '></i>';

            // Set the html
            $(nRow).children('td:eq(' + (cols - 1) + ')').html(html);

            // Select the approve event
            $('.fa fa-check', nRow).tooltip().click(function () {
                showPopover('btn-info', 'text-info');
                $(this).toggleClass('text-info');
                $('.fa fa-times', nRow).removeClass('text-danger');
            });

            // Select the items to delete
            $('.fa fa-times', nRow).tooltip().click(function () {
                showPopover('btn-danger', 'text-danger');
                $(this).toggleClass('text-danger');
                $('.fa fa-check', nRow).removeClass('text-info');
            });
        }

        // Show an ajax error on 
        function showAjaxError(jqXHR, textStatus, errorThrown, modal) {
            // Hide the if defined
            if (typeof(modal) !== 'undefined') modal.hide();

            // Show the alert
            swal({
                title: $('.lang-pack > ._ajax_error_title').html(),
                text: $('.lang-pack > ._ajax_error').html(),
                type: "warning"
            }, function (isConfirm) {
                // Show the modal once this is done
                if (typeof(modal) !== 'undefined' && jqXHR.status !== 403)
                    modal.show();

                // If one does not have access, reload the page
                if (jqXHR.status === 403) window.location.reload();
            });

            // Log the error in the console
            console.log("textStatus: " + textStatus);
            console.log("errorThrown: " + errorThrown);
        }

        // Called to reset a given modal of the content set
        function resetModal(modal) {
            // Clear the previous values
            $("input[type='checkbox']", modal).prop('checked', false);
            $('textarea, select', modal).val('');
            $('input', modal).each(function () {
                if ($(this).attr('type') !== 'checkbox') $(this).val('');
            });

            // Ensure that one can edit the content
            toggleReadonly(modal, false);
        }

        // Called whenever one would like to show a message indicating where to
        // proceed once the action has completed
        function showPopover(btn, icon) {
            if ($('table i.' + icon, e).length !== 0 || $('.pull-right > .' + btn, e).data('in-display') === true)
                return true;

            $('.pull-right > .' + btn, e)
                .data('in-display', true)
                .popover('enable')
                .popover('show');

            // If the previous timeout exists, clear it
            if (typeof(timeOut[btn]) !== "undefined")
                clearInterval(timeOut[btn]);

            // Set the timeout
            timeOut[btn] = setTimeout(function () {
                if ($('.pull-right > .' + btn, e).data('in-display') === true) {
                    $('.pull-right > .' + btn, e)
                        .data('in-display', false)
                        .popover('disable')
                        .popover("hide");
                    $('.popover').remove();
                }
            }, 7000);
        }

        // Called to toggle the readonly status of all elements within a given form
        function toggleReadonly(modal, state) {
            $('input, textarea', modal).prop('readonly', state);
            $('input[type="checkbox"]', modal).prop('disabled', state);
        }

        // Called to run an ajax call for items that have been toggled
        function flagRecords(flag, icon, selector, confirmText) {
            var h = [];

            // Get the keys selected
            if ($('table i.' + selector, e).length === 0) {
                swal({
                    title: $('.lang-pack > ._ajax_error_title').html(),
                    text: $('.lang-pack > ._ajax_record_select').html(),
                    type: "warning"
                });
                return false;
            }

            // Whow the confirmation dialog
            swal({
                title: "",
                text: confirmText,
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                closeOnConfirm: true
            }, function (isConfirm) {
                // The action has been approved
                if (isConfirm) {
                    $('table i.' + selector, e).each(function () {
                        h.push($(this).data('index'));
                    });

                    // Show progress
                    Utils.toggleLoading(e, "show");

                    // The request
                    $.ajax({
                        type: 'POST', dataType: "json", url: currentSource,
                        data: {action: flag, keys: window.JSON.stringify(h)},
                        success: function (o) {
                            if (o.status === true) oTable.draw();

                            Utils.toggleLoading(e, "hide");

                            swal({
                                title: (typeof (o.title) !== 'undefined') ? o.title : '',
                                text: o.msg,
                                type: (o.status === true) ? "success" : ((o.status === false) ? "warning" : "")
                            });
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            Utils.toggleLoading(e, "hide");
                            showAjaxError(jqXHR, textStatus, errorThrown);
                        }
                    });
                }

                // The action has been declined
                else {
                    $('table i.' + icon, e).removeClass(selector);
                }
            });
        }

        // Called to refresh the table information
        this.reloadGrid = function () {
            oTable.draw();
        };

        // Change the source of the given grid
        this.changeSource = function (url) {
            if (currentSource !== url) {
                currentSource = url;
                oTable.draw();
            }
        };

        // Ensure that the predefined length is between 10 and 100
        if (typeof (p.pageLength) === 'undefined' || p.pageLength < 10 || p.pageLength > 100)
            p.pageLength = 25;

        // Initialise the oTable object
        oTable = {
            "processing": true, "serverSide": true,
            "ajax": currentSource,
            "order": [[0, "desc"]],
            "pageLength": p.pageLength,
            "rowCallback": function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
                // If the maker checker flag is set
                if (makerChecker) {
                    if (inactiveRecordsDisplay) {
                        if (aData[cols] === "view")
                            viewChange(nRow, aData);
                        else approveInactive(nRow, aData);
                    }
                    else {
                        if (aData[cols] === "approve" || aData[cols] === "deny" || aData[cols] === "approve-deny")
                            approveChange(nRow, aData);
                        else if (aData[cols] === "ok")
                            makeChange(nRow, aData);
                        else viewChange(nRow, aData);
                    }
                }

                // No maker checker applied
                else {
                    makeChange(nRow, aData);
                }

                // in the event one wants to add additional stuff to the actions
                // section on this event
                if (typeof (p.rowCallback) !== 'undefined')
                    p.rowCallback(nRow, aData, editModal);

                // show empty values for the empty tables
                $('td', nRow).each(function () {
                    if ($(this).html() === '')
                        $(this).html('<span style="color:#c3c3c3">' + $('.lang-pack > ._ajax_record_empty').html() + '</span>');
                });
            },
            "drawCallback": function (oSettings) {
                // Remove the portlet loader
                Utils.toggleLoading(e, "hide");

                // Run the callback
                if (typeof (p.drawCallback) !== 'undefined')
                    return p.drawCallback(oSettings);
            },
            "preDrawCallback": function (oSettings) {
                // Show the portlet loader
                Utils.toggleLoading(e, "show");

                // Run the callback
                oSettings.ajax = currentSource;
                if (typeof (p.preDrawCallback) !== 'undefined')
                    return p.preDrawCallback(oSettings);
                return true;
            },
            "ajax": function (sSource, aoData, fnCallback) {
                // in the event one wants to add additional parameters to post
                // to the server
                if (typeof (p.ajax) !== 'undefined')
                    aoData = p.ajax(aoData);

                // Set the inactive record display flag
                aoData.push({name: 'inactiveRecordsDisplay', value: inactiveRecordsDisplay});

                // Send the ajax request
                $.ajax({
                    "dataType": 'json',
                    "type": "GET",
                    "url": currentSource,
                    "data": aoData,
                    "success": function (z) {
                        // If there are parameters defined, 
                        if (typeof (p.fnCallback) === 'function')
                            p.fnCallback(z);

                        // Run the call with the data
                        fnCallback(z);
                    }
                });
            }
        };

        // Disable sorting in the columns with the buttons
        oTable.columnDefs = [
            {
                "orderable": false,
                "targets": [(cols - 1)]
            }
        ];

        // In the event one would like to sort the items in a particular order
        if (typeof (p.order) !== 'undefined')
            oTable.order = p.order;

        // Initialise the data table
        oTable = $('table', e).DataTable(oTable);

        // Refresh the table
        $('.pull-right > .btn-success', e).click(function () {
            oTable.draw();
        });

        // New modal
        $('.pull-right > .btn-primary', e).click(function () {
            // Reset the modal
            resetModal(editModal);

            // Set the right action
            $('input[name="action"]', editModal).val('new');

            // The button to show
            $('button[type="submit"]', editModal).addClass('hide');
            $('button.btn-info', editModal).removeClass('hide');

            // If the new modal function has been defined, call the function
            if (typeof (p.setNewForm) !== 'undefined')
                p.setNewForm(editModal);

            // The default case shows the modal
            else
                editModal.modal('show');
        });

        // Activate records ajax call
        $('.pull-right > .btn-info', e).click(function () {
            clearInterval(timeOut["btn-info"]);

            $(this)
                .data('in-display', false)
                .popover('disable')
                .popover("hide");
            $('.popover').remove();

            flagRecords('activate', 'fa fa-pencil', 'text-info', $('.lang-pack > ._ajax_activate_confirm').html());
        });

        // deactivate records ajax call
        $('.pull-right > .btn-warning', e).click(function () {
            clearInterval(timeOut["btn-warning"]);

            $(this)
                .data('in-display', false)
                .popover('disable')
                .popover("hide");
            $('.popover').remove();

            flagRecords('deactivate', 'fa fa-trash', 'text-warning', $('.lang-pack > ._ajax_deactivate_confirm').html());
        });

        // Delete records ajax call
        $('.pull-right > .btn-danger', e).click(function () {
            clearInterval(timeOut["btn-danger"]);

            $(this)
                .data('in-display', false)
                .popover('disable')
                .popover("hide");
            $('.popover').remove();

            flagRecords('delete', 'fa fa-trash', 'text-danger', $('.lang-pack > ._ajax_delete_confirm').html());
        });

        // The inactive toggle flag
        $('.pull-right > .btn-purple', e).click(function () {
            // Toggle the inactive state
            inactiveRecordsDisplay = !inactiveRecordsDisplay;

            // Reset all items
            $('i', $(this)).removeClass('fa fa-pencil fa fa-trash');
            $('.pull-right > .btn-primary, .pull-right > .btn-warning, .pull-right > .btn-info, .pull-right > .btn-danger', e).addClass('hide');

            // Set the controls
            if (inactiveRecordsDisplay) {
                $('i', $(this)).addClass('fa fa-pencil');
                $('.pull-right > .btn-info, .pull-right > .btn-danger', e).removeClass('hide');
            }
            else {
                $('i', $(this)).addClass('fa fa-trash');
                $('.pull-right > .btn-primary, .pull-right > .btn-warning', e).removeClass('hide');
            }

            // Refresh the grid
            oTable.draw();
        });

        // Save the modal data
        $('form', e).on('submit', function (event) {
            // Prevent for post
            event.preventDefault();
            var modal = $(this).closest(".modal"), frm = $(this);

            // Show progress
            Utils.toggleLoading(frm, "show");

            // validation carried out on each form
            if (typeof(p.validateForm) !== 'undefined' && modal.hasClass('edit-modal') === true && p.validateForm(modal) !== true) {
                Utils.toggleLoading(frm, "hide");
                return false;
            }

            // The request
            $.ajax({
                type: 'POST', dataType: "json",
                url: currentSource,
                data: frm.serializeArray(),
                success: function (o) {
                    if (o.status === true) oTable.draw();

                    // Hide the modal
                    modal.hide();

                    // Hide the progress loader
                    Utils.toggleLoading(frm, "hide");

                    // Show the alert
                    swal({
                        title: (typeof (o.title) !== 'undefined') ? o.title : "",
                        text: o.msg,
                        type: (o.status === true) ? "success" : "warning"
                    }, function (isConfirm) {
                        // Show the modal once this is done
                        modal.show();

                        // If the maker checker has been defined and the record
                        // was successfully saved
                        if (makerChecker === true && o.status === true) modal.modal('hide');
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // Hide the progress loader
                    Utils.toggleLoading(frm, "hide");

                    // Show the error
                    showAjaxError(jqXHR, textStatus, errorThrown, modal);
                }
            });

            // Ensure the form is not posted
            return false;
        });

        // Run aux scripts on the edit modal as per the user request
        if (typeof(p.auxScripts) !== 'undefined') p.auxScripts(editModal);

        // Make sure that all modals allow one to scroll information on the 
        // desktop display
        if (jQuery.browser.mobile !== true) {
            $('.modal-body', e).slimscroll({
                alwaysVisible: true,
                height: '400px',
                size: "5px",
                wheelStep: 5,
                color: "#fff"
            });
        }

        // Allow chaining
        return this;
    };
})(jQuery);