(function ($) {
    //Global ajax setup
    'use strict';
    var token = $("meta[name='_csrf']").attr("content"),
        header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
        xhr.setRequestHeader("Accept", "application/json");
    });

    // Initialise the default grid
    $('.group-roles> .col-sm-6').each(function () {
        $('.parent-item', $(this)).attr('data-index', $('[data-code="default"] input', $(this)).data('index'));
        $('[data-code="default"]', $(this)).remove();
    });
    var ui = $('.edit-modal'),
        fnEdit = function (data) {
            setForm(data.entity, ui);
            ui.modal('show');
        };

    // Set the panel click events
    function setClickEvents() {
        var parent = $('.parent-item');
        // The parent checkbox click
        parent.on('ifChecked', function () {
            var panel = $(this).closest('.checkbox').next();
            if (panel.hasClass('role-actions')) {
                panel.iCheck('check');
            }
        });
        parent.on('ifUnchecked', function () {
            var panel = $(this).closest('.checkbox').next();
            if (panel.hasClass('role-actions')) {
                // $('.icheckbox_square-green', panel).removeClass('checked');
                panel.iCheck('uncheck');
            }
        });
    }

    // Set the form data
    function setForm(entity, ui) {
        setClickEvents(ui);
        console.log("*********** set form data "+entity.groupName);
        var data=entity.entity;
        entity=data;
        $('input[name="id"]', ui).val(entity.id);
        $('input[name="groupName"]', ui).val(entity.groupName);
        $('#groupName').val(entity.groupName);
        $('input[name="description"]', ui).val(entity.description);
        $('input[name="action"]', ui).val('edit');
        // Set the checkboxes
        // Start enabling the right checkboxes
        $('input[type="checkbox"]', ui).iCheck('uncheck');
        $.each(entity.groupActions, function (index, value) {
            var el = $('input[data-index="' + value.id + '"]', ui);
            el.iCheck('check');
            // Ensure that the parent is checked
            if (!el.hasClass('parent-item')) {
                el = $('.parent-item', el.closest('.col-sm-6'));
                if (!el.is(':checked')) el.prop('checked', true);
            }
        });

        // ui.modal('show');

    }

    ui.on('show.bs.modal', function () {
        setClickEvents(ui);
    });
    // The Slide toggle click
    $('.pull-right > i', ui).click(function () {
        var panel = $(this).closest('.checkbox').next();
        $(this).closest(".checkbox").next().slideToggle();
        if ($(this).hasClass('fa fa-angle-down')) {
            $(this).removeClass('fa fa-angle-down').addClass('fa fa-angle-up');
            panel.removeClass('d-none');
        }
        else {
            $(this).removeClass('fa fa-angle-up').addClass('fa fa-angle-down');
            panel.addClass('d-none');
        }
    });
    utils.makerchecker.initTables(fnEdit);
    //When to refresh table info
    $('[data-action="refresh"]').click(function () {
        setTimeout(function () {
            utils.makerchecker.refreshMkTables();
        });
    });
    $('.filters-active, .filters-new, .filters-edited, .filters-deactivated, .filters-inactive').on('change', function () {
        utils.makerchecker.updateFilters();
        utils.makerchecker.refreshMkTables();
    });

    $("form", ui)
        .formValidation()
        .on("success.fv.form", function (e) {
            e.preventDefault();
            // Place the checkbox values
            var params = [];
            $('.group-roles > .col-sm-6', ui).each(function () {
                // The params to set
                var key = null, current = $(this);
                $('input[type="checkbox"]', current).each(function () {
                    if ($(this).is(":checked")) {
                        key = $(this).data('index');

                        // If the item is defined
                        if (key !== null && key !== "")
                            params.push(key);
                    }
                });
            });
            // Set the params
            $('input[name="groupRoles"]', ui).val(window.JSON.stringify(params));
            utils.submitForm($(this).serializeArray(), ui);
        });

    ui.on('hidden.bs.modal', function () {
        utils.houseKeep(ui);
        // The button to show
        $('button[type="submit"]', ui).toggleClass('hide', false);
        //always have this default action
        $('input[name="action"]', ui).val('new');
    });

})(jQuery);
