$(document).ready(function () {
    "use strict";

    var form = $('#login-form'),
        button = form.find('#login-button'),
        state = button.find('.state'),
        url = form.find('[name=\'url\']');

    function onSubmit(event) {
        event.preventDefault();
        form.addClass('loading');
        button.prop('disabled', true);
        state.html('Authenticating');
        fetch(form.attr('action'), {method: form.attr('method'), body: new FormData(form.get(0))})
            .then(function (response) {
                onResponse(response);
            });
    }

    function onResponse(response) {
        response.json().then(function (json) {
            if (json.success) {
                onSuccessResponse();
            } else {
                onErrorResponse();
            }
        });
    }

    function onErrorResponse() {
        form.addClass('error');
        state.html('<b class="header">Login failed</b> Invalid username or password');
        setTimeout(function () {
            form.removeClass('loading error');
            button.prop('disabled', false);
            state.html('Log in');
        }, 1000);
    }

    function onSuccessResponse() {
        form.addClass('ok');
        state.html('<b class="header">Login success</b> you will be redirected in a second');
        setTimeout(function () {
            window.location.href = url.val();
        }, 1000);
    }

    form.on('submit', function (e) {
        onSubmit(e);
    });

});
