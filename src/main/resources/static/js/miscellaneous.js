
// Display the message at the bottom state message bar
// along with current time stamp
var displaySuccessMessage = function(message, messageID) {
    // if we passed in messageID, try to get the message from i18n translator
    // otherwise, display the message directly
    message = (messageID === undefined) ?  message:
                             $.i18nwd("message." + messageID, message);
    displayStateMessageAtBottom(message);
    promptSuccessMessage(message);
}
var displayFailMessage = function(message, messageID) {
    message = (messageID === undefined) ?  message:
                             $.i18nwd("message." + messageID, message);
    displayStateMessageAtBottom(message);
    // Show fail message long time than normal
    promptFailMessage(message, 2000);
}
var displayWarningMessage = function(message, messageID) {
    message = (messageID === undefined) ?  message:
                             $.i18nwd("message." + messageID, message);
    displayStateMessageAtBottom(message);
    promptWarningMessage(message);
}
var displayInfoMessage = function(message, messageID) {
    message = (messageID === undefined) ?  message:
                             $.i18nwd("message." + messageID, message);
    displayStateMessageAtBottom(message);
    promptInfoMessage(message);
}

var displayStateMessageAtBottom = function(message) {
    var currentdate = new Date();
    $("#stateMessage").text(currentdate.getHours() + ":"  +
                            currentdate.getMinutes() + ":" +
                            currentdate.getSeconds() + "  ===>  " +
                            message);
}
/**
 * Display auto fade popup alert
 * @param message
 * @param style alert-success / alert-danger / alert-warning / alert-info
 * @param time fade time
 */
var promptMessage = function (message, style, time)
{
    style = (style === undefined) ? 'alert-success' : style;
    time = (time === undefined) ? 1200 : time;
    $('<div>')
        .appendTo('body')
        .addClass('alert ' + style)
        .html(message)
        .show()
        .delay(time)
        .fadeOut();
};

var promptSuccessMessage = function(message, time)
{
    promptMessage(message, 'alert-success', time);
};

var promptFailMessage = function(message, time)
{
    promptMessage(message, 'alert-danger', time);
};

var promptWarningMessage = function(message, time)
{
    promptMessage(message, 'alert-warning', time);
};


var promptInfoMessage = function(message, time)
{
    promptMessage(message, 'alert-info', time);
};

/*
 * get display text for any code
 * for example, when display a enum field in a table
 * we will display the description instead of
 * the enum code
 */
var escapeDisplay = function(id, variable, code) {
    var url = '/ws/control/dropdown/' + variable;

    $.ajax({
        url:url,
        beforeSend: function() {
            if (dropdownListCache.exist(url)) {
                var data = dropdownListCache.get(url);
                $.each(data.dropdownOptions, function(i, option){
                    if (option.value == code) {
                        $("#" + id).html(
                             $.i18nwd("dropdown." + variable + "." + option.value, option.text));
                    }
                });
                return false;
            }
            return true;
        }
    }).done(function( res ) {
        $.each(res.data.dropdownOptions, function(i, option){
            if (option.value == code) {
                $("#" + id).html(
                             $.i18nwd("dropdown." + variable + "." + option.value, option.text));
                dropdownListCache.set(url, res.data);
            }
        });
    });
}

var escapeDisplays = function(parentID) {

    // if parent ID is passed in, we will only escape the
    // controls under the parent ID
    var escapeDisplayControl =
        (parentID === undefined) ? $("[data-escapedisplay=true]") : $("#" + parentID).find("[data-escapedisplay=true]");

    if(escapeDisplayControl.length != 0) {
        escapeDisplayControl.each(function(){
            var controlID = $(this).prop("id");
            var variable = $(this).data("variable");
            var code = $.trim($(this).html());

            escapeDisplay(controlID, variable, code);

        });
    }
}