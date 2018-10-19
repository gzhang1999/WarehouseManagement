/*
 * Javascript to process the button for general criteria form
 */

var initQueryButtons = function() {

    var queryButtons = $('[data-buttontype="query"]');

    if(queryButtons.length != 0) {
        queryButtons.each(function(){
            // Init the criteria form to use ajax
            // instead of normal submit
            initQueryForm($(this).data("target"));

            // Add event handler to the button click event
            // so that whenever the button is clicked
            // the target form will be submit
            $(this).click(function() {
                // Let's submit the form with the same id
                var formID = $(this).data("target");
                $("#" + formID).each(function() {
                    $(this).submit();
                });
            });
        });
    }


    var clearButtons = $('[data-buttontype="clear"]');

    if(clearButtons.length != 0) {
        clearButtons.each(function(){

            // Add event handler to the button click event
            // so that whenever the button is clicked
            // all fields in the form will be clear
            $(this).click(function() {
                // Let's submit the form with the same id
                var formID = $(this).data("target");
                $("#" + formID).each(function() {
                    $(this).trigger('reset');
                    // Clear the display table as well
                    var displayTableID = $(this).data("display");
                    var table = $("#" + displayTableID).DataTable();
                    table.clear().draw();
                });
            });
        });
    }
}

var initQueryForm = function(formID) {
    $("#" + formID).each(function() {
       $(this).submit(function(event) {
           // prevent the submit, instead we will use
           // ajax with GET
           event.preventDefault();
           var actionURL = $(this).attr("action");
           // Defulat method is 'GET'
           var method = $(this).attr("method") == undefined ? "GET" : $(this).attr("method");
           var queryString = $(this).serialize();

           // The table to display the result
           var displayTableID = $(this).data("display");
           $.ajax({
               type: method,
               url: actionURL + "?" + queryString,
               contentType: false,
               processData: false,
               displayTable: displayTableID
           }).done(function(res){
               // The page that use this function
               // should have the renderTable implemented
               renderTable(this.displayTable, res.data, res.customField);

           });
       });
    });
}

var initDataTable = function() {

    var datatables = $('[data-tabletype="datatable"]');

    if(datatables.length != 0) {
        datatables.each(function(){
            // Initiate the data table if data-init set to true
            if ($(this).data("init") == true) {
                $(this).DataTable();
            }
        });
    };
}

$(document).ready( function () {
    initQueryButtons();
    initDataTable();
});