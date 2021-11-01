/*
 The utils object will house the common functions that are used by ajaxGrid 
 objects. This utility will require jQuery to be operational and is released 
 under the MIT License.
 
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
function CUtils() {
    // Use this function to get the numeric value of a string passed to it.
    this.numericValue = function (n) {
        if (typeof (n) !== 'string' && $.isNumeric(n))
            return n;

        if (typeof (n) === 'string')
            n = n.replace(/[,]+/gi, '');
        var o = parseFloat(n);
        return isNaN(o) ? 0 : o;
    };

    // Dynamically add a script file to the page
    this.loadScript = function (arr, path) {
        var _arr = $.map(arr, function (scr) {
            return $.getScript((path || "") + scr);
        });

        _arr.push($.Deferred(function (deferred) {
            $(deferred.resolve);
        }));

        return $.when.apply($, _arr);
    };

    this.formObject = function objectifyForm(formObject) {//serialize data function
        var returnArray = {};
        var formArray = formObject.serializeArray();
        for (var i = 0; i < formArray.length; i++) {
            returnArray[formArray[i]['name']] = "" + formArray[i]['value'];
        }
        return returnArray;
    };

    // Dynamically add css files to the page
    this.loadCSS = function (url) {
        var head = document.getElementsByTagName('head')[0];
        if (typeof (url) === 'string')
            url = [url];

        for (var i in url) {
            var link = document.createElement('link');
            link.rel = 'stylesheet';
            link.type = 'text/css';
            link.href = url[i];
            link.media = 'all';
            head.appendChild(link);
        }
    };

    // When one would like to show the remaining characters in a given textarea
    // control
    this.limitCharacters = function (o, ch) {
        // In the event we use the default stuff
        if (typeof (ch) === 'undefined')
            ch = 250;

        // Set the keyup event
        o.keyup(function () {
            var l = ch - $(this).val().length, s = (l > 1) ? 's' : '';

            if (l < 0) {
                $(this).val($(this).val().substr(0, ch));
                l = 0;
            }

            $(this).prev().html(l + ' character' + s);
        });
    };

    // When one would like to enable the popover feature in the view in question
    this.popoverEnable = function (e, cursor) {
        // Get the cursor in question
        cursor = (typeof (cursor) === 'undefined') ? 'pointer' : cursor;

        // Get the element
        var el = (typeof (e) === 'undefined' || e === null) ? $('[data-toggle="popover"]') : $('[data-toggle="popover"]', e);

        // Activate the popovers
        el
            .popover().css('cursor', cursor)
            .hover(function () {
                $(this).popover('show');
            }, function () {
                $(this).popover('hide');
            });
    };

    // Called to set/unset the loading bg in an element
    this.toggleLoading = function (portlet, state) {
        // If the state is not set, end here
        if (typeof (state) === 'undefined')
            return false;

        // // If we are showing the loading progress item
        // if (state === "show")
        //     portlet.append('<div class="card-disabled"><div class="loader-1"></div></div>');
        //
        // // If we are hiding the loading progress
        // else if (state === "hide") {
        //     var pd = portlet.find('.card-disabled');
        //     pd.remove();
        // }
    };

    // Use this to set the portlet loading background on a portlet/card
    this.panelLoading = function (e, state) {
        Utils.toggleLoading(e.closest(".card"), state);
    };

    // This function gives you an instance of the datatable object that can be 
    // used to perform various functions
    this.ajaxTable = function (x, y) {
        function DT(e, o) {
            // The objects managed
            var currentSource = (typeof o.currentSource !== 'undefined') ? o.currentSource : window.location.pathname,
                oTable, columnDefs = (typeof o.columnDefs !== 'undefined') ? o.columnDefs : 'undefined',
                order = (typeof o.order !== 'undefined') ? o.order : undefined;
            // orderCol = (typeof o.orderCol !== 'undefined') ? o.orderCol : 0;
            // columns = (typeof o.columns !== 'undefined') ? o.columns : [];

            var destroy = (typeof o.destroy !== 'undefined') ? o.destroy : false;
            // The source of the datatable
            this.setSource = function (s) {
                if (currentSource !== s) {
                    currentSource = s;
                    oTable.draw();
                }
            };

            // Get the source of the table
            this.getSource = function () {
                return currentSource;
            };

            // When one wants to refresh the grid
            this.draw = function () {
                oTable.draw();
            };

            // When one wants to refresh the grid
            this.destroy = function () {
                oTable.destroy();
            };
            // Ensure that the predefined length is between 10 and 100
            if (typeof (o.pageLength) === 'undefined' || o.pageLength < 10 || o.pageLength > 100)
                o.pageLength = 10;
            // Set the data table parameters
            // console.log(orderCol, orderDir);
            oTable = {
                "processing": true,
                "serverSide": true,
                "order": order,
                "pageLength": o.pageLength,
                destroy: destroy,
                language: {
                    processing: "<img src='../../images/loader.gif'>"
                },
                // "columns": o.columns,
                "columnDefs": columnDefs,

                "rowCallback": function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
                    if (typeof (o.rowCallback) !== 'undefined')
                        o.rowCallback(nRow, aData);
                },
                "drawCallback": function (oSettings) {
                    // Remove the portlet loader
                    Utils.toggleLoading(e, "hide");

                    // Run the callback
                    if (typeof (o.drawCallback) !== 'undefined')
                        return o.drawCallback(oSettings);
                },
                "preDrawCallback": function (oSettings) {
                    // Show the portlet loader
                    Utils.toggleLoading(e, "show");

                    // Run the callback
                    oSettings.ajax = currentSource;
                    if (typeof (o.preDrawCallback) !== 'undefined')
                        return o.preDrawCallback(oSettings);
                    return true;
                },
                "ajax": function (sSource, aoData, fnCallback) {
                    if (typeof (o.ajax) !== 'undefined')
                        aoData = o.ajax(aoData);

                    $.ajax({
                        "dataType": 'json',
                        "type": "GET",
                        "url": currentSource,
                        "data": aoData,
                        "success": function (z) {
                            // If there are parameters defined, 
                            if (typeof (o.fnCallback) === 'function')
                                o.fnCallback(z);

                            // Run the call with the data
                            fnCallback(z);
                        }
                    });
                }
            };

            // Disable sorting in the columns with the buttons
            if (typeof (o.sortAllItems) === 'undefined')
                o.sortAllItems = true;
            if (!o.sortAllItems) {
                oTable.columnDefs = [
                    {"orderable": false, "targets": [($('thead > tr > th', e).length - 1)]}
                ];
            }

            // In the event one would like to sort the items in a particular order
            if (typeof (o.order) !== 'undefined')
                oTable.order = o.order;
            if (typeof (o.columns) !== 'undefined')
                oTable.columns = o.columns;

            // Initialise the table
            if (o.table !== undefined)
                oTable = o.table.DataTable(oTable);
            else
                oTable = $('table', e).DataTable(oTable);

            oTable.on('draw', function () {
                if (oTable.data().count() <= 0) {
                    $('#btnGroupVerticalDrop1').attr('disabled', true)
                } else $('#btnGroupVerticalDrop1').attr('disabled', false)
            });
        }

        return new DT(x, y);
    };

    // Allow one to conduct ajax requests using the ajax grid function as 
    // opposed to having to do it all over again
    this.jsonRequest = function (e, input, fn) {
        // Show that we are processing
        Utils.toggleLoading(e, "show");

        // The ajax request
        $.ajax({
            type: 'POST', dataType: "json", data: input, cache: false,
            url: (typeof (fn.url) !== 'undefined') ? fn.url : ((typeof (e.data('base-url')) !== 'undefined') ? e.data('base-url') : window.location.pathname),
            success: function (o) {
                // Hide the progress bar
                Utils.toggleLoading(e, "hide");

                // Show the response message
                if (typeof (fn.success) === 'function')
                    fn.success(o);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                // Hide the progress bar
                Utils.toggleLoading(e, "hide");

                // Show the error
                if (typeof (fn.error) === 'function') {
                    fn.error(jqXHR, textStatus, errorThrown);
                } else {
                    swal({
                        title: $('.lang-pack > ._ajax_error_title').html(),
                        text: $('.lang-pack > ._ajax_error').html(),
                        type: "warning"
                    }, function (isConfirm) {
                        // If one does not have access, reload the page
                        if (jqXHR.status === 403)
                            window.location.reload();
                    });
                }
            },
            complete: function () {
                // Call the complete function if present
                if (typeof (fn.complete) === 'function')
                    fn.complete();
            }
        });
    };

    // When one will make a request for an entity to display in the view
    this.entityRequest = function (e, input, success) {
        Utils.jsonRequest(e, input, {
            success: function (o) {
                // Set the controls
                if (o.status === true) {
                    if (typeof (success) === 'function')
                        success(o);
                }

                // in the event there are no records associated with the fetch,
                // or an error occured, or one does not have access
                else {
                    swal({
                        title: "",
                        text: o.msg,
                        type: "warning"
                    });
                }
            }
        });
    };

    // Use this function to parse a json encoded string to get the json object
    this.jsonParse = function (jsonString) {
        var jsonObject;

        try {
            // Check if the value is a json string
            jsonObject = window.JSON.parse(jsonString);

            // Handle non-exception-throwing cases:
            // Neither JSON.parse(false) or JSON.parse(1234) throw errors, hence 
            // the type-checking, but... JSON.parse(null) returns 'null', and 
            // typeof null === "object", so we must check for that, too.
            if (jsonObject && jsonObject !== null && typeof jsonObject === "object")
                return jsonObject;
        } catch (exception) {
            console.log("The string applied could not be converted to a json object");
            console.log(jsonString);
            return null;
        }
    };

    // Called to enable all select items with the .require-input class 
    this.requireSelect = function (e) {
        e = (typeof (e) === 'undefined' || e === null) ? $(document) : e;

        $('select.require-select', e).each(function () {
            $(this).change(function () {
                $(this).toggleClass('invalid-input', ($(this).val() === ''));
                $(this).toggleClass('valid-input', ($(this).val() !== ''));
            }).trigger('change');
        });
    };

    // The report utility functions
    this.initReportDateRange = function (e, draw, ranges, startDate, endDate) {
        var yr = new Date().getFullYear();
        var sdate;
        if (undefined === startDate)
            sdate = moment(yr + "-01-01");
        // moment();
        else
            sdate = startDate;
        var edate;
        if (undefined === endDate)
            edate = moment();
        else
            edate = endDate;

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
            opens: 'right',
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

        $('#date-range').on('apply.daterangepicker', function (ev, picker) {
            console.log(picker.startDate.format('YYYY-MM-DD'));
            console.log(picker.endDate.format('YYYY-MM-DD'));
        });

        $('.date-range span', e)
            .html(sdate.format('MMM D, YYYY') + ' - ' + edate.format('MMM D, YYYY'))
            .data('start-date', sdate.format('YYYY-MM-DD'))
            .data('end-date', edate.format('YYYY-MM-DD'));
    };

    // The simple report display
    this.simpleReport = function (o) {
        var childTable, oTable = Utils.ajaxTable(o.parent, {
            sortAllItems: true,
            currentSource: window.location.pathname,
            ajax: function (aoData) {
                aoData.push({name: 'startDate', value: $('.date-range span', o.parent).data('start-date')});
                aoData.push({name: 'endDate', value: $('.date-range span', o.parent).data('end-date')});
                aoData.push({name: 'transType', value: $('.btn-success', o.parent).data('index')});
                return aoData;
            },
            "rowCallback": function (nRow, aData) {
                $(nRow).children('td:eq(0)').html('<a href="#" data-id="' + aData[o.dataCol] + '">' + aData[0] + '</a>');

                for (var ii in o.parentCols) {
                    $(nRow).children('td:eq(' + o.parentCols[ii] + ')')
                        .html(Utils.numericValue(aData[o.parentCols[ii]]).formatMoney(0));
                }

                $('a', nRow).click(function (evt) {
                    evt.preventDefault();
                    o.parent.hide();
                    o.child.toggleClass("hide");

                    var index = $(this).data("id");
                    $('.card-title', o.child).html($(this).html());

                    childTable = Utils.ajaxTable(o.child, {
                        sortAllItems: true,
                        currentSource: window.location.pathname,
                        rowCallback: function (nRow, aData) {
                            for (var ii in o.childCols) {
                                $(nRow).children('td:eq(' + o.childCols[ii] + ')')
                                    .html(Utils.numericValue(aData[o.childCols[ii]]).formatMoney(0));
                            }
                        },
                        ajax: function (aoData) {
                            aoData.push({name: 'startDate', value: $('.date-range span', o.child).data('start-date')});
                            aoData.push({name: 'endDate', value: $('.date-range span', o.child).data('end-date')});
                            aoData.push({name: 'transType', value: $('.btn-success', o.child).data('index')});
                            aoData.push({name: 'data', value: index});
                            return aoData;
                        }
                    });
                });
            }
        });

        $('.btn-info', o.child).click(function () {
            if (!o.parent.is(":visible")) {
                o.parent.show();
                o.child.toggleClass("hide");
                childTable.destroy();
            }
        });
        $('.btn-success', o.child).click(function () {
            if (o.child.is(":visible")) {
                childTable.draw();
            }
        });
        $('.trip-state a', o.child).click(function (evt) {
            evt.preventDefault();
            $('.btn-group > .title-btn', o.child).html($(this).html());
            $('.btn-success', o.child).data('index', $(this).data('index'));
            childTable.draw();
        });
        $('.download-report a', o.child).each(function () {
            $(this).attr('href', window.location.pathname + "/" + $(this).data('action'));
        });
        Utils.initReportDateRange(o.child, function () {
            childTable.draw();
        });

        $('.btn-success', o.parent).click(function () {
            if (o.parent.is(":visible")) {
                oTable.draw();
            }
        });
        $('.trip-state a', o.parent).click(function (evt) {
            evt.preventDefault();
            $('.btn-group > .title-btn', o.child).html($(this).html());
            $('.btn-group > .title-btn', o.parent).html($(this).html());
            $('.btn-success', o.child).data('index', $(this).data('index'));
            $('.btn-success', o.parent).data('index', $(this).data('index'));
            oTable.draw();
        });
        $('.download-report a', o.parent).each(function () {
            $(this).attr('href', window.location.pathname + "/" + $(this).data('action'));
        });
        Utils.initReportDateRange(o.parent, function (start, end) {
            $('.date-range', o.child).data('daterangepicker').setStartDate(start);
            $('.date-range', o.child).data('daterangepicker').setEndDate(end);

            $('.date-range span', o.child)
                .html(start.format('MMM D, YYYY') + ' - ' + end.format('MMM D, YYYY'))
                .data('start-date', start.format('YYYY-MM-DD'))
                .data('end-date', end.format('YYYY-MM-DD'));
            oTable.draw();
        });
    };

    // Initialise the typeahead controls when complete page is fully loaded, 
    // including all frames, objects and images
    this.typeaheadInit = function () {
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
                                    if (jqXHR.status === 403)
                                        window.location.reload();
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

    this.initDataTables = function (table, options) {
        options.autoWidth = false;
        options.language = {
            processing: "<img src='../../images/loader.gif'>"
        };
        return table.DataTable(options);
    };

    this.formatNumber = function (x) {
        var formatter = new Intl.NumberFormat('en-US');
        return formatter.format(x);
    };

    this.formatCurrency = function (x) {
        var formatter = new Intl.NumberFormat('en-US', {style: 'currency', currency: 'KES'});
        return formatter.format(x);
    };

    this.formatSpecificCurrency = function (x, curr) {
        var formatter = new Intl.NumberFormat('en-US', {style: 'currency', currency: curr});
        return formatter.format(x);
    };
}

var Utils = new CUtils();