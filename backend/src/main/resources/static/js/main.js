$(document).ready(function () {
    "use strict";

    function initWidgets(target) {
        target.find("[data-type!=''][data-type]").each(function() {
           initWidget($(this).data('type'), $(this));
        });
    }

    function initWidget(type, element) {
        switch (type) {
            case 'action':
                initAction(element);
                break;
            case 'focusable':
                initFocusable(element);
                break;
        }
    }

    function initFocusable(target) {
        target.parents('.modal').on('shown.bs.modal', function () {
            target.focus();
        });
    }

    function initAction(target) {
        target.on('click', function (e) {
            e.preventDefault();
            switch (target.prop("tagName").toLowerCase()) {
                case "a":
                    submitAnchor(target);
                    break;
                case "button":
                    submitForm(target.parents('form'));
                    break;
            }
        });
    }

    function submitAnchor(anchor) {
        fetch(anchor.attr('href'), {method: anchor.data('action-method')})
            .then(function (response) {
                onResponse(response, alertGlobalError, alertFieldError);
            });
    }

    function submitForm(form) {
        form.find('.form-group').removeClass('has-danger');
        form.find('.form-control-errors').remove();
        form.find('.form-errors').empty();
        fetch(form.attr('action'), {method: form.attr('method'), body: new FormData(form.get(0))})
            .then(function (response) {
                onResponse(response, formGlobalError(form), formFieldError(form));
            });
    }

    function onResponse(response, globalErrorCallback, fieldErrorCallback) {
        response.json().then(function (result) {
            if (result.errors) {
                globalErrorCallback.call(this, result.errors);
            }
            if (result.fieldErrors) {
                $.each(result.fieldErrors, function (entry) {
                    fieldErrorCallback.call(this, entry, result.fieldErrors[entry]);
                });
            }
            if (result.redirect) {
                window.location.href = result.redirect;
            }
        });
    }

    function alertGlobalError(errors) {
        $.notify({
            message: errors.join('<br />')
        }, {
            type: 'danger',
            allow_dismiss: true,
            placement: {
                from: 'top',
                align: 'center'
            }
        });
    }

    function alertFieldError(field, errors) {
        alertGlobalError(field + ": " + errors.join(". "));
    }

    function formGlobalError(form) {
        return function (errors) {
            var container = form.find('.form-errors');
            var html = '';
            $.each(errors, function (i) {
                html += '<li class="alert alert-danger">';
                html += errors[i];
                html += '</li>';
            });
            container.append($(html));
        }
    }

    function formFieldError(form) {
        return function (field, errors) {
            var html = '<ul class="form-control-errors">';
            $.each(errors, function (i) {
                html += '<li class="alert alert-danger">';
                html += errors[i];
                html += '</li>';
            });
            html += '</ul>';
            var input = form.find('[name=\'' + field + '\']');
            $(html).insertAfter(input);
            input.parents('.form-group').addClass('has-danger');
        }
    }

    initWidgets($(document.body));
});
