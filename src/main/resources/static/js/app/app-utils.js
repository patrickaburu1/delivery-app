var AppFormatUtils= (function (p) {
    "use strict";

    //Initialize default values
    var e = $('.card'), _activeTable, _inactiveTable, _deactivatedTable, _newTable, _editedTable, _deletedTable;
    var _options = {
            _filtersactive: $('.filters-active', e).find('select').serializeArray(),
            filtersnew: $('.filters-new', e).find('select').serializeArray(),
            filtersedited: $('.filters-edited', e).find('select').serializeArray(),
            filtersinactive: $('.filters-inactive', e).find('select').serializeArray(),
            filtersdeactivated: $('.filters-deactivated', e).find('select').serializeArray(),
            filtersdeleted: $('.filters-deleted', e).find('select').serializeArray(),
            _url: window.location.pathname,
            _tactive: $('.tab-active ', e),
            _tnew: $('.table-new', e),
            _tedited: $('.table-edited', e),
            _tinactive: $('.table-inactive', e),
            _tdeactivated: $('.table-deactivated', e),
            _tdeleted: $('.table-deleted', e),
            _token: $("meta[name='_csrf']").attr("content"),
            _header: $("meta[name='_csrf_header']").attr("content"),
            _editview: $('.edit-modal'),
            _deactivateView: $('.deactivate-view'),
            _viewChanges: $('.view-changes'),
            _viewReasons: $('.view-reasons')
        },

        // Called to set/unset the loading bg in an element
        _toggleLoading = function (portlet, state) {
            // If the state is not set, end here
            if (typeof(state) === 'undefined') return false;

            // If we are showing the loading progress item
            if (state === "show")
                portlet.append('<div class="panel-disabled"><div class="loader-1"></div></div>');

            // If we are hiding the loading progress
            else if (state === "hide") {
                var pd = portlet.find('.panel-disabled');
                pd.remove();
            }
        },

        // Use this to set the portlet loading background on a portlet/panel
        _panelLoading = function (el, state) {
            var ui;
            if ("undefined" === el || "" === el || null === el) ui = $(".box");
            else ui = el;
            _toggleLoading(ui, state);
        },
        _formatMoney = function (n, c, d, t) {
            var c = isNaN(c = Math.abs(c)) ? 2 : c,
                d = d == undefined ? "." : d,
                t = t == undefined ? "," : t,
                s = n < 0 ? "-" : "",
                i = String(parseInt(n = Math.abs(Number(n) || 0).toFixed(c))),
                j = (j = i.length) > 3 ? j % 3 : 0;

            return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
        },
        _formatNumbers = function addCommas(nStr) {
            nStr += '';
            var x = nStr.split('.');
            var x1 = x[0];
            var x2 = x.length > 1 ? '.' + x[1] : '';
            var rgx = /(\d+)(\d{3})/;
            while (rgx.test(x1)) {
                x1 = x1.replace(rgx, '$1' + ',' + '$2');
            }
            return x1 + x2;
        },
        // Called to toggle the readonly status of all elements within a given form
        _toggleReadonly = function (ui, state) {
            $('input, textarea', ui).prop('readonly', state);
            $('input[type="checkbox"]', ui).prop('disabled', state);
        },

        //Clean form inputs
        _houseKeep = function (ui) {
            // Clear the previous values
            $(":checkbox, :radio", ui).prop('checked', false);
            $('textarea', ui).val('');
            $('select', ui).prop('selectedIndex', 1);
            $(':input', ui).not(':button, :submit, :checkbox, :radio').val('');
            $(":checkbox, :radio", ui).iCheck('uncheck');
            //Reset form validation errors
            $('form *', ui)
                .filter('.form-group').each(function (e) {
                if ($(this).hasClass("has-error") || $(this).hasClass("has-success")) {
                    $(this).find("[data-fv-result]").value = "NOT_VALIDATED";
                    $(this).find(".help-block").attr("style", "display: none");

                    $(this).toggleClass("has-error", false);
                    $(this).toggleClass("has-success", false);
                }
            }).end()
                .filter(":submit").each(function (e) {
                $(this).prop('disabled', false);
            });

            // Ensure that one can edit the content
            _toggleReadonly(ui, false);
            ui.formValidation('resetForm', true);
        },

        //Sweet alerts
        _alert = {
            error: function (o, callback) {
                swal({title: "", text: o, type: 'warning'}, function () {
                    if (typeof callback === 'function') callback();
                }).then(function (isConfirm) {
                });
            },
            success: function (o, callback) {
                swal({title: "", text: o, type: 'success'}, function () {
                    if (typeof callback === 'function') callback();
                }).then(function (isConfirm) {
                });
            },
            confirm: function (o, type, callback) {
                swal({'title': '', text: o, type: type, showCancelButton: true, closeOnConfirm: true}, function () {
                    if (typeof callback === 'function') callback();
                });
            }
        },
        _DataTable = function (fn) {
            // fn.table.ajaxTable( fn );
            function dt(o) {
                var oTable = {}, table, url, columns;
                table = (typeof o.table === 'undefined') ? $(".table") : o.table;
                url = (typeof o.ajax === 'undefined') ? window.location.pathname : o.ajax;
                columns = (typeof o.columns !== 'undefined') ? o.columns : [];
                //When refreshing a table
                this.draw = function () {
                    oTable.draw();
                };

                //When destroying this table
                this.fnDestroy = function () {
                    oTable.destroy();
                };
                oTable = {
                    "processing": true,
                    "serverSide": true,
                    "ajax": url,
                    "order": [[0, "desc"]],
                    "columns": o.columns,
                    "rowCallback": function (nRow, data, iDisplayIndex, iDisplayIndexFull) {
                        if (typeof o.rowCallback === "function") o.rowCallback(nRow, data);
                    },
                    // "ajax":function( aoData){
                    //     if( typeof o.ajax === "function") o.ajax( aoData );
                    // },
                    "drawCallback": function (oSettings) {
                        // Remove the portlet loader
                        _toggleLoading(e, "hide");

                        // Run the callback
                        if (typeof (o.drawCallback) !== 'undefined')
                            return o.drawCallback(oSettings);
                        return true;
                    },
                    "preDrawCallback": function (oSettings) {
                        // Show the portlet loader
                        _toggleLoading(e, "show");

                        // Run the callback
                        if (typeof (o.preDrawCallback) !== 'undefined')
                            return o.preDrawCallback(oSettings);
                        return true;
                    }
                };

                oTable = table.DataTable(oTable);
            }

            return new dt(fn);
        };

    //DataTable init plugin
    $.fn.ajaxTable = function (fn) {
        //Define default options
        var defaults = {
            ajax: window.location.pathname,
            table: $('table')
        };
        //Merge default and user params
        fn = $.extend(
            // {}, //Empty object to prevent overriding of default options
            defaults, //Default options for the plugin
            fn //User defined options
        );
        var oTable = {};
        //Initiliaze a DataTable object
        oTable = {
            'processing': true,
            'ajax': fn.ajax,
            'order': [[0, 'desc']],
            'serverSide': true,
            'rowCallback': function (nRow, data, iDisplayIndex, iDisplayIndexFull) {
                if (typeof fn.rowCallback === 'function')
                    fn.rowCallback(nRow, data);
            },
            // 'ajax':function( aoData){
            //     if( typeof fn.ajax === 'function')
            //         fn.ajax( aoData );
            // },
            "drawCallback": function (oSettings) {
                // Remove the portlet loader
                _toggleLoading(e, "hide");

                // Run the callback
                if (typeof (fn.drawCallback) !== 'undefined')
                    return fn.drawCallback(oSettings);
            },
            "preDrawCallback": function (oSettings) {
                // Show the portlet loader
                _toggleLoading(e, "show");

                // Run the callback
                // oSettings.ajax = window.location.pathname;
                if (typeof (fn.preDrawCallback) !== 'undefined')
                    return fn.preDrawCallback(oSettings);
                return true;
            }
        };

        //Allow chaining
        return fn.table.DataTable(oTable);
    };

    //Mobile phone validator: include formValidation dependecies
    var _mobileValidator = {
        /**
         * @param {FormValidation.Base} validator The validator plugin instance
         * @param {jQuery} $field The jQuery object represents the field element
         * @param {Object} options The validator options
         * @returns {Boolean}
         */
        init: function (validator, $field, options) {
            //Initiliaze field with intlTelInput options
            $field.intlTelInput({
                geoIpLookup: function (callback) {
                    $.get('https://freegeoip.net/json/', function () {
                    }, "jsonp").always(function (resp) {
                        var countryCode = (resp && resp.country_code) ? resp.country_code : "";
                        callback(countryCode);
                    });
                },
                utilsScript: $("meta[name='_intelutil']").attr("content"),
                autoPlaceholder: true,
                initialCountry: "auto"
            });
            //Revalidate field when country is changed
            var $form = validator.getForm(), fieldname = $field.attr('data-fv-field');
            $form.on('click.country.intphonenumber', '.country-list', function () {
                $form.formValidation('revalidateField', fieldname);
            });
        },
        destroy: function (validator, $field, options) {
            $field.intlTelInput('destroy');
            //Turn off the event
            validator.getForm().off('click.country.intphonenumber');
        },
        validate: function (validator, $field, options) {
            return $field.val() === '' || $field.intlTelInput('isValidNumber');
        }
    };
    var passwordValidator = {
        validate: function (validator, $field, options) {
            var value = $field.val();
            if ('' === value) return true;
            //Check the password strength
            if (value.length < 7) {
                return {valid: false, message: 'Password should be at least 7 characters long'};
            }
            //If password contains any uppercase letter
            if (value.toLowerCase() === value) {
                return {valid: false, message: 'Password must contain at least one uppercase character'};
            }
            //If password contains any lowercase letter
            if (value.toUpperCase() === value) {
                return {valid: false, message: 'Password must contain at least one lowercase character'};
            }
            //If password contains any digit
            if (value.search(/[0-9]/) < 0) {
                return {valid: false, message: 'Password must contain at least one digit'};
            }

            return true;
        }
    };

    //Ajax helpers for making requests
    //Global ajax setup
    var _http = {
        _options: {},
        _404: function () {
            _alert.error($('.lang-pack > ._ajax_error_404', e).html());
        },
        _500: function () {
            _alert.error($('.lang-pack > ._ajax_error_500', e).html());
        },
        _403: function () {
            _alert.confirm($('.lang-pack > ._ajax_error_403', e).html(), 'warning', function () {
                window.location.reload();
            });
        },
        entityRequest: function (input, fnCallback) {

        },
        jsonRequest: function (e, input) {
            if ('undefined' === typeof e) e = p.e;
            var $this =
                $.ajax({
                    type: 'post', data: input, dataType: 'json',
                    beforeSend: function () {
                        //Notify user of the progress of execution
                        _toggleLoading(e, 'show');
                    },
                    complete: function () {
                        //Notify user of end of execution
                        _toggleLoading(e, 'hide');
                    },
                    error: function () {

                    },
                    statusCode: {
                        404: _http._404, 500: _http._500, 403: _http._403
                    }
                });
            return $this;
        },
        endRequest: function (input, view) {
            if ('undefined' === typeof view) view = _options._editview;
            view.hide();
            _http.jsonRequest(e, input).done(function (o) {
                if (o.status === true) {
                    _alert.success(o.msg);
                    _houseKeep(view);
                    makerchecker.refreshTables();
                    view.modal('hide');
                }
                else {
                    _alert.error(o.msg);
                    _houseKeep(view);
                    makerchecker.refreshTables();
                    view.modal('hide');
                }
                $('.modal-backdrop').remove()
            });
        },
        simpleReq: function (input) {
            return _http.jsonRequest(e, input);
        }
    };

    //Create a dynamic table and append to a tab
    p._createTable = function (label) {
        //Create the tab
        $('<li class="closeme"><a href="#random-tab" data-toggle="tab">' + label + '</a></li>').appendTo('#myTab');

        //Create the tab content
        var table = '<table class="table table-striped table-bordered">';
        table += "<thead><tr><th>" + Array.prototype.slice.call(arguments, 1).join("</th><th>") + "</th></tr></thead></table>";
        $('<div class="tab-pane closeme table-tab" id="random-tab" role="tabpanel">' + table + '</div>').appendTo('.tab-content');

        // make the new tab active
        $('#myTab a:last').tab('show');
        //When to destroy this tab
        $(document).on('hide.bs.tab', 'a[data-toggle="tab"]', function (e) {
            var tab = $(e.target);
            if (tab.parent().hasClass("closeme")) {
                tab.parent().remove();
                $('div.tab-content div.table-tab').remove();
            }
        });
    };

    var makerchecker = {
        fnRowActions2: function (o, ident, state, mkflag, roles) {
            var dataHtml = 'data-index="' + o + '" data-name="' + ident + '"';
            var approve = ' data-toggle="tooltip" data-placement="left" title="Approve"';
            var decline = ' data-toggle="tooltip" data-placement="left" title="Decline"';
            var del = ' data-toggle="tooltip" data-placement="left" title="Delete"';
            var activate = ' data-toggle="tooltip" data-placement="left" title="Activate"';

            // var actions = "";
            var actions = "";
            if (state === 0) {
                if ("view" !== mkflag) {
                    actions += '<i class="fa fa-close icon-btn icon-btn-danger declinenew" ' + dataHtml + decline + '></i>&nbsp;&nbsp;' +
                        '<i class="fa fa-check icon-btn icon-btn-success approvenew" ' + dataHtml + approve + '></i>';
                }
                else actions += '<i class="fa fa-eye icon-btn icon-btn-danger view" ' + dataHtml + ' data-toggle="tooltip"' +
                    ' data-placement="left" title="View"></i>';
            }
            else if (state === 1) {
                if (roles != null) {
                    if (roles.edit === 'true')
                        actions += '<i class="fa fa-pencil icon-btn icon-btn-primary edit" ' + dataHtml + ' data-toggle="tooltip"' +
                            ' data-placement="left" title="Edit"></i>';

                    if (roles.deactivate === 'true')
                        actions += '&nbsp;&nbsp;<i class="fa fa-trash-o icon-btn deactivate" ' + dataHtml + ' data-toggle="tooltip" data-placement="left" title="Deactivate"></i>';
                }
                else {
                    actions += '<i class="fa fa-pencil icon-btn icon-btn-primary edit" ' + dataHtml + ' data-toggle="tooltip" data-placement="left" title="Edit"></i>';
                    actions += '&nbsp;&nbsp;<i class="fa fa-trash-o icon-btn deactivate" ' + dataHtml + ' data-toggle="tooltip" data-placement="left" title="Deactivate"></i>';
                }
            }
            else if (state === 2) {
                if ("view" !== mkflag) {
                    actions += '<i class="fa fa-eye icon-btn icon-btn-danger vedit" ' + dataHtml + ' data-toggle="tooltip" data-placement="left" title="View Changes"></i>';
                }
                else {
                    actions += '<i class="fa fa-eye icon-btn icon-btn-primary view" ' + dataHtml + ' data-toggle="tooltip" data-placement="left" title="View"></i>';
                }
            } else if (state === 3) {
                if ("view" !== mkflag) {
                    actions += '<i class="fa fa-eye icon-btn icon-btn-danger vdeactivation" ' + dataHtml + ' data-toggle="tooltip" data-placement="left" title="View Changes"></i>';
                }
                else actions += '<i class="fa fa-eye icon-btn icon-btn-danger view" ' + dataHtml + ' data-toggle="tooltip" data-placement="left" title="View"></i>';
            } else if (state === 4) {
                if ("view" !== mkflag) {
                    actions += '<i class="fa fa-close icon-btn icon-btn-danger delete" ' + dataHtml + del + '></i>&nbsp;&nbsp;' +
                        '<i class="fa fa-check icon-btn icon-btn-success activate" ' + dataHtml + activate + '></i>';
                }
                else actions += '<i class="fa fa-eye icon-btn icon-btn-danger view" ' + dataHtml + ' data-toggle="tooltip" data-placement="left" title="View"></i>';
            }

            return actions;
        },
        flagRecords: function (ident, index, status, type) {
            swal({
                title: '',
                text: $('._ajax_question_' + status).html(),
                type: (typeof type === 'undefined') ? 'info' : type,
                showCancelButton: true,
                showLoaderOnConfirm: true
            }).then(function (isConfirm) {
                if (isConfirm)
                    setTimeout(function () {
                        $.ajax({
                            type: 'post', data: {'action': status, 'index': index}, dataType: 'json',
                            success: function (o) {
                                if (o.status === true) {
                                    _alert.success(o.msg);
                                    makerchecker.refreshTables();
                                    _houseKeep(e);

                                }
                                else {
                                    _houseKeep(e);
                                    _alert.error(o.msg);
                                }
                            },
                            statusCode: {
                                404: _http._404, 500: _http._500, 403: _http._403
                            }
                        });
                    }, 200);
            }).catch(swal.noop)
        },
        fnEdit: function (index, fn) {
            var ui = _options._editview;
            _http
                .jsonRequest(e, {'action': 'fetch', 'index': index})
                .done(function (o) {
                    if (true === o.status) {
                        if ('function' === typeof fn)
                            fn(o);
                    }
                    else _alert.error(o.msg);
                });
        },
        fnView: function (index, fn) {
            var ui = _options._editview;
            // The button to show
            $('button[type="submit"]', ui).toggleClass('hide', true);
            _http
                .jsonRequest(e, {'action': 'fetch', 'index': index})
                .done(function (o) {
                    if (true === o.status) {
                        if ('function' === typeof fn)
                            fn(o);
                    }
                    else _alert.error(o.msg);
                });
        },
        fnDeactivate: function (index, label) {
            var ui = _options._deactivateView;
            ui.find('[name="index"]').val(index).end()
                .find('.identity').html(label).end()
                .modal('show').end();
        },
        fnViewChanges: function (index, hideSubmit) {
            var ui = _options._viewChanges;
            $('button[type="submit"]', ui).toggleClass('hide', hideSubmit);
            _http
                .jsonRequest(e, {'action': 'vedit', 'index': index})
                .done(function (o) {
                    var changes = '<div class="row"><div class="col-md-12 col-sm-12 col-xs-12"><table class="table table-bordered table-condensed"><tr><th>Field</th><th>Old Value</th><th>New Value</th></tr>';
                    var rows = [];
                    $.each(o.data, function (i, val) {
                        rows.push('<tr><td>' + val.field + '</td><td>' + val.oldvalue + '</td><td>' + val.newvalue + '</td></tr>');
                    });
                    changes += rows.join('') + '</table></div></div>';
                    ui.find('[name="index"]').val(index).end()
                        .find('.modal-body').html(changes).end()
                        .modal('show');
                });
        },
        fnViewReasons: function (index, hideSubmit) {
            var $this = _options._viewReasons;
            $('button[type="submit"]', $this).toggleClass('hide', hideSubmit);
            _http
                .jsonRequest(e, {'action': 'fetch-reasons', 'index': index})
                .done(function (o) {
                    if (true === o.status) {
                        $('.user', $this).html(o.editor);
                        $('.reason', $this).html(o.reason);
                        $('.description', $this).html(o.description);
                        $('input[name="index"]', $this).val(o.index);
                        $this.modal('show');
                    }
                });
        },
        activeTable: function (fn, roles) {
            var cols = _options._tactive.find('thead > tr > th', e).length;
            return _options._tactive.ajaxTable({
                table: _options._tactive,
                // destroy: true,
                // buttons: [ 'copy', 'excel', 'pdf', 'colvis' ],
                ajax: {
                    data: [
                        {'fetch-table': '1'},
                        {'filters': _options._filtersactive}

                    ]
                },
                "rowCallback": function (nRow, data) {
                    //Append action buttons
                    $(nRow).children('td:eq(' + (cols - 1) + ')').html(makerchecker.fnRowActions2(data[data.length - 1], data[0], 1, data[cols], roles));
                    // Enable the tooltips
                    $('.icon-btn', nRow).tooltip();

                    //Edit events
                    $('.edit', nRow).click(function () {
                        makerchecker.fnEdit($(this).data('index'), fn);
                    });
                    //Deactivate events
                    $('.deactivate', nRow).click(function () {
                        makerchecker.fnDeactivate($(this).data('index'), $(this).data('name'));
                    });
                }
            });
        },
        deletedTable: function (fn, roles) {
            var cols = _options._tdeleted.find('thead > tr > th', e).length;
            return _options._tdeleted.ajaxTable({
                table: _options._tdeleted,
                ajax: {
                    data: [
                        {'fetch-table': '6'},
                        {'filters': _options.filtersdeleted}
                    ]
                }
            });
        },
        newTable: function (fn) {
            var cols = _options._tnew.find('thead > tr > th', e).length;
            return _options._tnew.ajaxTable({
                table: _options._tnew,
                ajax: {
                    data: [
                        {'fetch-table': '0'},
                        {'filters': _options.filtersnew}
                    ]
                },
                "rowCallback": function (nRow, data, iDisplayIndex, iDisplayIndexFull) {
                    //Append action buttons
                    $(nRow).children('td:eq(' + (cols - 1) + ')').html(makerchecker.fnRowActions2(data[data.length - 1], data[0], 0, data[cols]));
                    // Enable the tooltips
                    $('.icon-btn', nRow).tooltip();
                    //Handle events
                    $('.approvenew', nRow).click(function () {
                        makerchecker.flagRecords($(this).data('name'), $(this).data('index'), "approve");
                    });
                    $('.declinenew', nRow).click(function () {
                        makerchecker.flagRecords($(this).data('name'), $(this).data('index'), "deny", "warning");
                    });

                    $('.view', nRow).click(function () {
                        makerchecker.fnView($(this).data('index'), fn);
                    });
                }
            });
        },
        editedTable: function (fn) {
            var cols = _options._tedited.find('thead > tr > th', e).length;
            return _options._tedited.ajaxTable({
                table: _options._tedited,
                ajax: {
                    data: [
                        {'fetch-table': '2'},
                        {'filters': _options.filtersedited}

                    ]
                },
                "rowCallback": function (nRow, data) {
                    //Append action buttons
                    $(nRow).children('td:eq(' + (cols - 1) + ')').html(makerchecker.fnRowActions2(data[data.length - 1], data[0], 2, data[cols]));
                    // Enable the tooltips
                    $('.icon-btn', nRow).tooltip();
                    //Handle events
                    $('.vedit', nRow).click(function () {
                        makerchecker.fnViewChanges($(this).data('index'), false);
                    });

                    $('.view', nRow).click(function () {
                        makerchecker.fnViewChanges($(this).data('index'), true);
                    });
                }
            });
        },
        deactivatedTable: function (fn) {
            var cols = _options._tdeactivated.find('thead > tr > th', e).length,
                _table = _options._tdeactivated.ajaxTable({
                    table: _options._tdeactivated,
                    ajax: {
                        data: [
                            {'fetch-table': '3'},
                            {'filters': -_options.filtersdeactivated}

                        ]
                    },
                    "rowCallback": function (nRow, data) {
                        //Append action buttons
                        $(nRow).children('td:eq(' + (cols - 1) + ')').html(makerchecker.fnRowActions2(data[data.length - 1], data[0], 3, data[cols]));
                        // Enable the tooltips
                        $('.icon-btn', nRow).tooltip();
                        //Handle events
                        $('.vdeactivation', nRow).click(function () {
                            makerchecker.fnViewReasons($(this).data('index'), false);
                        });

                        $('.view', nRow).click(function () {
                            makerchecker.fnViewReasons($(this).data('index'), true);
                        });
                    }
                });
            return _table;
        },
        inactiveTable: function (fn) {
            var cols = _options._tinactive.find('thead > tr > th', e).length;
            return _options._tinactive.ajaxTable({
                table: _options._tinactive,
                destroy: true,
                ajax: {
                    data: [
                        {'fetch-table': '4'},
                        {'filters': _options.filtersinactive}

                    ]
                },
                "rowCallback": function (nRow, data) {
                    //Append action buttons
                    $(nRow).children('td:eq(' + (cols - 1) + ')').html(makerchecker.fnRowActions2(data[data.length - 1], data[0], 4, data[cols]));
                    // Enable the tooltips
                    $('.icon-btn', nRow).tooltip();
                    //Handle events
                    $('.delete', nRow).click(function () {
                        makerchecker.flagRecords($(this).data('name'), $(this).data('index'), "delete", "warning");
                    });
                    $('.activate', nRow).click(function () {
                        makerchecker.flagRecords($(this).data('name'), $(this).data('index'), "activate");
                    });

                    $('.view', nRow).click(function () {
                        makerchecker.fnView($(this).data('index'), fn);
                    });
                }
            });
        },
        //Initiliaze all maker checker tables
        initTables: function (fn) {
            _activeTable = this.activeTable(fn, null);
            _editedTable = this.editedTable(fn);
            _newTable = this.newTable(fn);
            _deactivatedTable = this.deactivatedTable(fn);
            _inactiveTable = this.inactiveTable(fn);
            _deletedTable = this.deletedTable(fn)
        },
        //Initialize data tables and check if the necessary roles have been assigned to user
        initTablesWithRoles: function (fn, roles) {
            _activeTable = this.activeTable(fn, roles);
            _editedTable = this.editedTable(fn, roles);
            _newTable = this.newTable(fn, roles);
            _deactivatedTable = this.deactivatedTable(fn, roles);
            _inactiveTable = this.inactiveTable(fn, roles);
        },
        //Refresh maker checker tables
        refreshMkTables: function () {
            _activeTable.draw();
            _editedTable.draw();
            _newTable.draw();
            _deactivatedTable.draw();
            _inactiveTable.draw();
        },
        updateFilters: function () {
            _options._filtersactive = $('.filters-active', e).find('select').serializeArray();
            _options.filtersinactive = $('.filters-inactive', e).find('select').serializeArray();
            _options.filtersnew = $('.filters-new', e).find('select').serializeArray();
            _options.filtersdeactivated = $('.filters-deactivated', e).find('select').serializeArray();
            _options.filtersedited = $('.filters-edited', e).find('select').serializeArray();
        },
        //Allow overriding of the refresh function
        refreshTables: function () {
            if ("function" === typeof makerchecker.option || 'undefined' === typeof _activeTable) {
                window.location.reload();
            }

            else makerchecker.refreshMkTables();
        },

        options: {

            refreshFn: null
        }
    };

    //When approving a deactivation request
    $('form', _options._viewReasons).on("submit", function (event) {
        event.preventDefault();
        var action = _options._viewReasons.find('[type=submit]:focus').data('action');
        $('[name="action"]', _options._viewReasons).val(action);
        _http.endRequest($(event.target).serializeArray(), _options._viewReasons);
        _houseKeep(_options._viewReasons);
        return false;
    });

    //When deactivating a record
    $('form', _options._deactivateView)
        .formValidation({
            framework: 'bootstrap',
            fields: {
                reasonNo: {validators: {notEmpty: {message: "Reason is required"}}},
                reasonDescription: {validators: {notEmpty: {message: "Description is required"}}}
            }
        })
        .off('success.fv.form')
        .on('success.fv.form', function (e) {
            e.preventDefault();
            var _request = _http.jsonRequest(_options._deactivateView, $(e.target).serializeArray());
            _request.done(function (o) {
                // _options._deactivateView.hide();
                if (o.status === true) {
                    _alert.success(o.msg);
                    _houseKeep(_options._deactivateView);

                    //Default value
                    $("[name='action']", _options._deactivateView).val("deactivate");
                    _options._deactivateView.modal('hide');
                    makerchecker.refreshTables();
                }
                else _alert.error(o.msg);
                _options._deactivateView.show();
            });
            return false;
        });

    //Clean up deactivation form
    _options._deactivateView.on("hidden.bs.modal", function (e) {
        var ui = $(this);
        _houseKeep(ui);
        $("[name='reasonNo']", ui).val($("[name='reasonNo'] option:first", ui).val());
        $('[name="action"]', ui).val("deactivate");
    });

    //When approving changes
    $('form', _options._viewChanges).on("submit", function (event) {
        event.preventDefault();
        var action = $(this).find('[type=submit]:focus').data('action');
        $('[name="action"]', $(this)).val(action);
        _http.endRequest($(this).serializeArray(), _options._viewChanges);
        return false;
    });

    var reportDateRange = function (e, draw, ranges) {
        var yr = new Date().getFullYear(), sdate = moment(yr + "-01-01"), edate = moment(yr + "-12-31");
        if (typeof (ranges) === 'undefined') {
            ranges = {
                'Today': [moment(), moment()],
                'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                'Last 7 Days': [moment().subtract(6, 'days'), new Date()],
                'Last 30 Days': [moment().subtract(29, 'days'), new Date()],
                'This Month': [moment().startOf('month'), moment().endOf('month')],
                'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
                'Current Year': [moment(yr + "-01-01"), moment(yr + "-12-31")],
                'Previous Year': [moment(yr + "-01-01").subtract(1, 'year'), moment(yr + "-12-31").subtract(1, 'year')]
            };
        }


        $('.date-range', e).daterangepicker({
            ranges: ranges,
            opens: 'left',
            locale: {
                format: 'YYYY-MM-DD',
                startDate: sdate.format('YYYY-MM-DD'),
                endDate: edate.format('YYYY-MM-DD')
            }
        }, function (start, end) {
            $('.date-range span', e)
                .html(start.format('MMM D, YYYY') + ' - ' + end.format('MMM D, YYYY'))
                .data('start-date', start.format('YYYY-MM-DD'))
                .data('end-date', end.format('YYYY-MM-DD'));
            draw(start, end);
        });

        $('.date-range span', e)
            .html(sdate.format('MMM D, YYYY') + ' - ' + edate.format('MMM D, YYYY'))
            .data('start-date', sdate.format('YYYY-MM-DD'))
            .data('end-date', edate.format('YYYY-MM-DD'));
    };

    // Initialise the typeahead controls when complete page is fully loaded,
    // including all frames, objects and images
    var typeaheadRegister = function () {
        $(window).load(function () {
            $('input.typeahead').each(function () {
                var
                    e = $(this),
                    remoteUrl = (typeof (e.data('remote-url')) !== 'undefined') ? e.data('remote-url') : window.location.pathname,
                    hound = new Bloodhound({
                        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
                        queryTokenizer: Bloodhound.tokenizers.whitespace,
                        remote: {
                            // The query
                            url: remoteUrl + '?_q=%QUERY',
                            cache: false,
                            // The additional data params passed to the query
                            ajax: {
                                data: {
                                    _limit: (typeof (e.data('limit')) !== 'undefined') ? e.data('limit') : 20,
                                    _data: function () {
                                        // Set the class to show you are loading the information
                                        e.addClass('tt-autocomplete');

                                        // Set the data function
                                        var att = e.get(0).attributes, xxx = {};

                                        for (var i = 0, n = att.length; i < n; i++) {
                                            if (att[i].nodeName.indexOf('data-') >= 0) {
                                                // set everything else
                                                try {
                                                    // Check if the value is a json string
                                                    var o = window.JSON.parse(att[i].value);

                                                    // Handle non-exception-throwing cases:
                                                    // Neither JSON.parse(false) or JSON.parse(1234) throw errors, hence
                                                    // the type-checking, but... JSON.parse(null) returns 'null', and
                                                    // typeof null === "object", so we must check for that, too.
                                                    if (o && o !== null && typeof o === "object")
                                                        xxx[att[i].nodeName.replace('data-', '')] = o;
                                                    else if (o && o !== null)
                                                        xxx[att[i].nodeName.replace('data-', '')] = o;
                                                } catch (exception) {
                                                    xxx[att[i].nodeName.replace('data-', '')] = att[i].value;
                                                }
                                            }
                                        }

                                        return window.JSON.stringify(xxx);
                                    }
                                },
                                error: function (jqXHR, textStatus, errorThrown) {
                                    if (jqXHR.status === 403) window.location.reload();
                                },
                                complete: function () {
                                    e.removeClass('tt-autocomplete');
                                }
                            }
                        }
                    });

                // Initialise the bloodhound
                hound.initialize();

                // Initialise the typeahead object
                e.typeahead({
                    hint: true,
                    highlight: true
                }, {
                    displayKey: 'value',
                    source: hound.ttAdapter()
                });

                // if there is an input element to attach the id to
                if (typeof e.data('control') !== 'undefined' && e.data('control') !== false) {
                    e.on('typeahead:selected', function (jqO, datum) {
                        document.getElementsByName(e.data('control'))[0].value = (datum.id === '_q') ? '' : datum.id;
                    });
                }
            });
        });
    };
    //Externalize reusable methods
    return {
        makerchecker: makerchecker,
        options: _options,
        createTable: p._createTable,
        toggleLoading: _toggleLoading,
        panelLoading: _panelLoading,
        houseKeep: _houseKeep,
        formatMoney: _formatMoney,
        formartNumber: _formatNumbers,
        DataTable: _DataTable,
        alert: _alert,
        mobileValidator: _mobileValidator,
        passwordValidator: passwordValidator,
        submitForm: _http.endRequest,
        http: _http,
        initReportDateRange: reportDateRange,
        typeaheadInit: typeaheadRegister
    };
}(AppFormatUtils || {}));

