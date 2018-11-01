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
           // if this form contains tri-state checkbox, then
           // 1. if the state is checked, then we already have it in the queryString
           // 2. if the state is unchecked, then we need to add checkboxName=off
           //    onto the queryString
           // 3. if the state is intermediate, we don't have to do anything

            var triStateCheckboxs = $(this).find('[data-cbtype="tri-state"]');

            if(triStateCheckboxs.length != 0) {
                triStateCheckboxs.each(function(){
                    if ($(this).data("checked") == "false") {
                        queryString += "&" + $(this).prop("name") + "=off";
                    }
                });
            };

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
               renderTable(this.displayTable, res.data, res.customFields);

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
                if ($(this).data("custominit") == true) {
                    customInitDataTable($(this).prop("id"));
                }
                else if ($(this).data("scrollx") == true) {
                    $(this).DataTable({
                        "scrollX": true
                    });
                }
                else {
                    $(this).DataTable();
                }
            }
        });
    };
}
var initCheckBox = function () {
    var triStateCheckbox = $('[data-cbtype="tri-state"]');

    if(triStateCheckbox.length != 0) {
        triStateCheckbox.each(function(){
            $(this).prop("indeterminate", true);
            // indeterminate checkbox will only have 2 states for the 'check' value: on or off
            // we will need to use customized data field to trace the state of the checkbox
            // so that we can have 3 states instead of 2
            // data-checked:
            // 0: indeterminate
            // 1: checked
            // 2: unchecked
            $(this).data("checked", "indeterminate");
            $(this).click(function(e) {
                switch($(this).data("checked")) {
                    // from indeterminated to checked
                    case "indeterminate":
                        $(this).data("checked","true");
                        $(this).prop("indeterminate",false);
                        $(this).prop("checked",true);
                    break;
                    // from checked to unchecked
                    case "true":
                        $(this).data("checked","false");
                        $(this).prop("indeterminate",false);
                        $(this).prop("checked",false);
                    break;
                    // from anything else to indeterminated
                    default:
                        $(this).data("checked","indeterminate");
                        $(this).prop("indeterminate",true);
                        $(this).prop("checked",false);
                    break;
                }
            })

        });
    };
}
$(document).ready( function () {
    initQueryButtons();
    initDataTable();
    // Init checkbox to be tri-state checkbox
    initCheckBox();
});