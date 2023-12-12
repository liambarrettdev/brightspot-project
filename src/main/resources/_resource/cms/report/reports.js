$(function() {
    $('#report-select-button').on('click', function(e) {
        e.preventDefault();

        var filters = $('div#report-filters *'),
            report = $('select#report').val();
        if (report === '') {
            alert('Please select a report');
            return;
        }

        disableElement(filters);
        $.ajax({
            url: '/_api/report/data/?id=' + report + '&action=filters&output=html',
            dataType: 'html',
            success: function(data) {
                $('#report-table').empty();
                $('#report-filters').empty().append(data);
                $('#report-filters input.date').datepicker({
                    format: 'dd/mm/yyyy',
                    autoclose: true
                });

                registerButtonClickHandlers();
                updateFilterClassNames();
                enableElement(filters);
            },
            error: function(jqXHR, exception) {
                alert(jqXHR.responseText);
                enableElement(filters);
            }
        });

        resizeIframe();
    });

    /* Updates the class names on the #report-filters element based on the currently
     * selected values in the <select> controls within the #report-filters element.
     * These class names are use to hide/show controls (via css) which may/may not
     * be applicable for the chosen select box entry
     */
    function updateFilterClassNames() {
        var currentClasses = typeof $('#report-filters').attr('class') !== 'undefined' ? $('#report-filters').attr('class').split(' ') : [];
        $('#report-filters select').each(function() {
            var name = $(this).attr('name'),
                selectedVal = $(this).val();

            // First remove any classes beginning with the name + '_'
            for (i = 0; i < currentClasses.length; i++) {
                if (currentClasses[i].startsWith(name + '_')) {
                    $('#report-filters').removeClass(currentClasses[i]);
                }
            }

            if (selectedVal !== '') {
                $('#report-filters').addClass(name + '_' + selectedVal);
            }
        });
    }

    function registerButtonClickHandlers() {
        $('#report-filters select').off('change').on('change', function(e) {
            e.preventDefault();
            updateFilterClassNames();
        });

        $('#get-report-data-button').off('click').on('click', function(e) {
            e.preventDefault();

            var submitData = $('#report-form').serialize(),
                report = $('select#report').val()
            valid = true;

            if (report === '') {
                alert('Please select a report');
                return;
            }

            $('input.date').each(function() {
                if ($(this).hasClass('required') && $.trim($(this).val()) === '') {
                    alert('Date is required');
                    return valid = false;
                }
            });

            if (valid) {
                $.ajax({
                    url: '/_api/report/data/?id=' + report + '&action=report&output=json',
                    data: submitData,
                    dataType: 'json',
                    success: function(json) {
                        // Add column headings
                        var tableHeaders = '';
                        $.each(json.columns, function(i, val) {
                            tableHeaders += '<th>' + val.label + '</th>';
                        });

                        // Add table content
                        $('#report-table').empty();
                        $('#report-table').append('<table id="displayTable" class="display" cellspacing="0" width="100%"><thead><tr>' + tableHeaders + '</tr></thead></table>');
                        $('#displayTable').dataTable(json);
                        if (json.totalColumn) {
                            $('#displayTable').addClass('hasTotalsRow');
                        }
                        addDownloadButton('download-data', json.downloadUrl, '#get-report-data-button', false);
                        addEmailButton('email-data', json.emailUrl, '#get-report-data-button', false, );
                    }
                });
            }
        });

        $('#update-filters-button').off('click').on('click', function(e) {
            e.preventDefault();
            updateFilters();
        });

        $('.report-type-options').off('change').on('change', function(e) {
            e.preventDefault();
            updateFilters();
        });
    }

    function updateFilters() {
        var filters = $('div#report-filters *'),
            submitData = $('#report-form').serialize(),
            report = $('select#report').val(),
            reportType = $('.report-type-options select').val(),
            fromDate = $('#report-filters input.from_date').val(),
            toDate = $('#report-filters input.to_date').val();

        disableElement(filters);
        $.ajax({
            url: '/_api/report/data/?id=' + report + '&action=filters&output=html',
            data: submitData,
            dataType: 'html',
            success: function(data) {
                $('#report-table').empty();
                $('#report-filters').empty().append(data);

                // persist selected dates
                $('#report-filters input.date').datepicker({
                    format: 'dd/mm/yyyy',
                    autoclose: true
                });

                if (fromDate) {
                    $('#report-filters input.from_date').val(fromDate);
                }

                if (toDate) {
                    $('#report-filters input.to_date').val(toDate);
                }

                // persist selected report subtype
                if (reportType) {
                    $('.report-type-options select').val(reportType);
                }

                registerButtonClickHandlers();
                updateFilterClassNames();
                enableElement(filters);
            },
            error: function(jqXHR, exception) {
                alert(jqXHR.responseText);
                enableElement(filters);
            }
        });
    }

    function resizeIframe() {
        var page = window.parent;
        var iframe = page.document.getElementById('iframe');
        if (iframe) {
            iframe.height = "";
            iframe.height = page.visualViewport.height - 100 + "px";
        }
    }

    function disableElement($el) {
        $el.prop('disabled', true);
        $('.reports-widget').css('cursor', 'wait');
    }

    function enableElement($el) {
        $el.prop('disabled', false);
        $('.reports-widget').css('cursor', 'default');
    }

    function addDownloadButton(id, dataDownloadUrl, parent, emptyParentFirst, label) {
        $('#' + id).remove();
        if (emptyParentFirst) {
            $(parent).empty();
        }

        if (!dataDownloadUrl) {
            return;
        }

        if (!label) {
            label = 'Download';
        }

        $button = $('<button/>', {
            'id': id,
            'text': label,
            'data-download-url': dataDownloadUrl
        });

        $button.off('click').on('click', function(e) {
            e.preventDefault();
            window.open($(this).data('download-url'));
        });

        $(parent).after($button);
    }

    function addEmailButton(id, dataEmailUrl, parent, emptyParentFirst, label) {
        $('#' + id).remove();
        if (emptyParentFirst) {
            $(parent).empty();
        }

        if (!label) {
            label = 'Email';
        }

        $button = $('<button/>', {
            'id': id,
            'text': label,
            'data-email-url': dataEmailUrl
        });

        $button.off('click').on('click', function(e) {
            e.preventDefault();

            $.ajax({
                url: $(this).data('email-url'),
                success: function(data) {
                    alert("Email request has been submitted and should appear in your inbox shortly")
                }
            });
        });

        $(parent).after($button);
    }
});