
// Display the message at the bottom state message bar
// along with current time stamp
var displayStateMessage = function(message) {
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