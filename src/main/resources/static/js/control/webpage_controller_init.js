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
/*
 * Javascript to init the generic query form
 */

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
/*
 * Javascript to initialize the DataTable
 */

var initDataTable = function() {

    var datatables = $('[data-tabletype="datatable"]');

    if(datatables.length != 0) {
        datatables.each(function(){
            // Initiate the data table if data-init set to true
            if ($(this).data("init") == true) {
                var table;
                if ($(this).data("custominit") == true) {
                    customInitDataTable($(this).prop("id"));
                }
                else if ($(this).data("scrollx") == true) {
                    table = $(this).DataTable({
                        "scrollX": true,
                        fixedHeader: true
                    });
                }
                else {
                    table = $(this).DataTable();
                }

                // In some case, the column header doesn't align with the data rows when initializing.
                // We will need to refresh the table to adjust the alignment
                if ($(this).data("refresh") == true) {
                    table.columns.adjust();
                }

            }
        });
    };
}
/*
 * Javascript to initial a tri-state checkbox
 */

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

/*
 * Javascript to load content of a dropdown list
 */

var dropdownListCache = {
    data: {},
    remove: function (url) {
                delete dropdownListCache.data[url];
    },
    exist: function (url) {
               return dropdownListCache.data.hasOwnProperty(url) && dropdownListCache.data[url] !== null;
    },
    get: function (url) {
             return dropdownListCache.data[url];
    },
    set: function (url, cachedData, callback) {
              dropdownListCache.remove(url);
              dropdownListCache.data[url] = cachedData;
              if ($.isFunction(callback)) callback(cachedData);
    }
};

var initDropdownLists = function() {
    $('select').each(function(){
        var variable = $(this).data("variable");

        if (variable != undefined && variable != "") {
            var selectionControl = $(this);
            loadDropdownList(selectionControl, true, true);
         }
    });
}

var loadDropdownList = function(selectionControl, registerEventOnFilterVariable, loadFromCache) {
    var variable = selectionControl.data("variable");
    var cacheKey = variable;
    var parameters = "";

    if (loadFromCache == false) {
        parameters += "cache=false";
    }

    // check if we need to filter this dropdown list by values
    // from other controls
    if (selectionControl.data("filter-variable")) {
        var filterVariables = selectionControl.data("filter-variable");
        var filterVariableArray = filterVariables.split(",");

        if (filterVariableArray.length > 0) {
            $.each(filterVariableArray, function(index, value) {
                var filterVariableName = value;
                if($("#" + filterVariableName).val()) {
                    var filterVariableValue = $("#" + filterVariableName).val();
                    var filterVariableParameterName = $("#" + filterVariableName).prop("name") ?
                                                      $("#" + filterVariableName).prop("name") : $("#" + filterVariableName).prop("id");
                    parameters = parameters + filterVariableParameterName + "=" + filterVariableValue + "&";
                }

                // every time the filter variable changed, let's refresh the current auto complete
                // control's valid options
                if (registerEventOnFilterVariable) {
                    $("#" + filterVariableName).focusout(function(event){
                        loadDropdownList(selectionControl, false);
                    });
                }
            });
        }
    }

    // Call AJAX to load the content of the dropdown list
    // from webservice or cache
    loadDropdownListContent(selectionControl.prop("id"), variable, parameters, loadFromCache);

    // Register a keydown action so when the user press F5,
    // we will clear cache and reload the content from web service
    selectionControl.keydown(function(event){
        // F5 code = 116
        if(event.which == 116) {
            // We won't refresh the whole page
            event.preventDefault();
            loadDropdownList(variable, false, false);
        }
    });
}

/**************
var loadDropdownListContentExcludeCache = function(variable) {
    var url = '/ws/control/dropdown/' + variable + "?cache=false";
    $.ajax({
        url:url
    }).done(function( res ) {
        renderSelection(res.data.variable, res.data);
        dropdownListCache.set(url, res.data);
    });

}
**************/

var loadDropdownListContent = function(dropdownListID, variable, parameters, loadFromCache) {
    var url = '/ws/control/dropdown/' + variable;
    if (parameters.length > 0) {
        url += "?" + parameters;
    }

    $.ajax({
        url:url,
        beforeSend: function() {
            if (dropdownListCache.exist(url) && loadFromCache != false) {
                var data = dropdownListCache.get(url);
                renderSelection(dropdownListID, data.variable, data);
                return false;
            }
            return true;
        }
    }).done(function( res ) {
        renderSelection(dropdownListID, res.data.variable, res.data);
        dropdownListCache.set(url, res.data);
    });

}

var renderSelection = function(dropdownListID, variable, data) {

    var selectionControls = $("#" + dropdownListID);
    if(selectionControls.length != 0) {
        selectionControls.each(function(){
            var selectionControl = $(this);


            // If the client setup the default selected options, let's select it
            var selectedOption = selectionControl.data("selected-option") === undefined ? "" : selectionControl.data("selected-option");

            // Remove all the options before we start to fill in new options
            selectionControl.empty();
            if(data.allowBlankRowFlag) {
                selectionControl.append('<option value="">          </option>');
            }

            $.each(data.dropdownOptions, function(i, option){

                selectionControl.append('<option value="' + option.value + '">' + $.i18nwd("dropdown." + variable + "." + option.value, option.text) + '</option>');
            });
            if (selectedOption != "") {

                selectionControl.val(selectedOption);
            }


        })
    }
}

/*
 * Javascript to initialize the validation on the input textbox
 * validation type
 * 1. Required / Required
 * 2. Email
 * 3. Password
 * 4. Integer Only
 * 5. Number Only / Number
 * 6. Max Length
 */
var initValidation = function() {

    var inputForValidation = $("input[data-validation]").filter(function () {
        return $.trim($(this).data("validation")) != '';
    });

    if(inputForValidation.length != 0) {
        inputForValidation.each(function(){
            var inputControl = $(this);
            inputControl.change(function(){
                var textValue = $(this).val();
                var validationMethod = inputControl.data("validation");
                var valid = true;
                var message = "";
                switch(validationMethod) {
                    case "Number":
                        if (textValue != "" && !$.isNumeric(textValue)) {
                            valid = false;
                            message = "Number only"
                        }
                    break;
                    case "Integer":
                        if (textValue != "" && (!$.isNumeric(textValue) || Math.floor(textValue) != textValue)) {
                            valid = false;
                            message = "Integer only"
                        }
                    break;
                }
                // If the div for the error message exists, remove it
                if ($("error_" + $(this).prop("id")).length > 0) {
                    $("error_" + $(this).prop("id")).remove();
                }
                if (!valid) {
                    // Add warning message to the below of the control
                    $(this).after("<div class='has-error row' id='error_" + $(this).prop("id") + "'>" + message + "</div>");
                }

            })
        });
    }

}



var autoCompleteCache = {
    data: {},
    remove: function (variable) {
                delete autoCompleteCache.data[variable];
    },
    exist: function (variable) {
               return autoCompleteCache.data.hasOwnProperty(variable) && autoCompleteCache.data[variable] !== null;
    },
    get: function (variable) {
             return autoCompleteCache.data[variable];
    },
    set: function (variable, cachedData, callback) {
              autoCompleteCache.remove(variable);
              autoCompleteCache.data[variable] = cachedData;
              if ($.isFunction(callback)) callback(cachedData);
    }
};


var initAutoCompleteControls = function() {

    var inputForAutoComplete = $("input[data-autocomplete]").filter(function () {
        return $.trim($(this).data("autocomplete")) != '';
    });

    if(inputForAutoComplete.length != 0) {
        inputForAutoComplete.each(function(){
            var inputControl = $(this);
            loadAutoComplete(inputControl, true);
        });
    }
}

var loadAutoComplete = function(inputControl, registerEventOnFilterVariable) {

    var variable = inputControl.data("variable");

    // We will get the data from web service and save it
    // in local cache
    var cacheKey = variable;
    var url = '/ws/control/autocomplete/' + variable;

    if (inputControl.data("filter-variable")) {
        var filterVariables = inputControl.data("filter-variable");
        var filterVariableArray = filterVariables.split(",");

        if (filterVariableArray.length > 0) {
            cacheKey = cacheKey + "#";
            url = url + "?";

            $.each(filterVariableArray, function(index, value) {
                var filterVariableName = value;
                if($("#" + filterVariableName).val()) {
                    var filterVariableValue = $("#" + filterVariableName).val();
                    var filterVariableParameterName = $("#" + filterVariableName).prop("name") ?
                                           $("#" + filterVariableName).prop("name") : $("#" + filterVariableName).prop("id");
                    cacheKey = cacheKey + filterVariableParameterName + "=" + filterVariableValue + "&";
                    url = url + filterVariableParameterName + "=" + filterVariableValue + "&";
                }

                // every time the filter variable changed, let's refresh the current auto complete
                // control's valid options
                if (registerEventOnFilterVariable) {
                    $("#" + filterVariableName).focusout(function(event){
                        loadAutoComplete(inputControl, false);
                    });
                }
            });
        }
    }

    if (autoCompleteCache.exist(cacheKey)) {
        inputControl.autocomplete({
            source: autoCompleteCache.get(cacheKey)
        });
    }
    else {
        $.ajax({
            url:url,
            variable:variable,
            cacheKey:cacheKey
        }).done(function( res ) {

            var matchControls = $("input[data-autocomplete]").filter(function () {
                return $.trim($(this).data("autocomplete")) != '' && $(this).data("variable") == variable;
            });

            if(matchControls.length != 0) {
                matchControls.each(function(){

                    $(this).autocomplete({
                        source: res.data
                    });
                });
            }
            autoCompleteCache.set(cacheKey, res.data);
        });

    }
}

var initI18n = function() {
    var userLang = navigator.language || navigator.userLanguage;
    console.log("userLang: " + userLang);

    var fileName = "message_EN.json";
    var webBrowserLocale = "en"
    if (userLang == "zh-CN") {
        fileName = "message_CN.json";
        webBrowserLocale = "cn";
    }
    $.i18n({
           locale: webBrowserLocale
           })
    .load({
        cn: "/js/control/" + fileName
    }).done( function() {
        console.log($.i18n('i18n.load.success'));
    })

}


var initDatePicker = function() {

    $(".datepicker").each(function(){
        // Always show the date pick under the input control
        $(this).datepicker({
            orientation: "bottom auto"
        });
    })
}


var initLookupController = function() {

    var lookupControllers = $("input[data-controltype='lookup']");

    if(lookupControllers.length != 0) {
        lookupControllers.each(function(){
            var lookupController = $(this);
            var lookupIconHtml = " <span class='input-group-prepend mr-sm-4'> " +
                                 "     <button class='btn btn-light btn-sm' type='button' onclick='lookup(\"" + lookupController.prop("id") + "\", \"" + lookupController.data("variable") + "\")'> " +
                                 "     <i class='fa fa-search'></i></button> " +
                                 " </span>";

            lookupController.after(lookupIconHtml);
        });
    }
}

var lookup = function(id, variable) {
    var url = "/ws/control/lookup/" + variable;
    $.ajax({url:url})
     .done(function(res){

         if (res.status == 0) {
             if ($("#lookupModal").length > 0) {
                 $("#lookupModal").remove();
             }
             var lookupModalHtml = getLookupModalHtml(id, res.data);
             $("main").after(lookupModalHtml);

             $("#lookupResultTable").DataTable({
                 "scrollX": true,
                 fixedHeader: true
             });

             // Add scroll bar in case there're too many columns
             $("#lookupModal").on('shown.bs.modal', function (e) {
                  //Get the datatable which has previously been initialized
                  var dataTable= $('#lookupResultTable').DataTable();
                  dataTable.columns.adjust();
             });

             $("#lookupModal").modal("show");
         }
    });
}


var getLookupModalHtml = function(id, data) {
    lookupModalHtml = "<div class='modal fade' id='lookupModal' tabindex='-1' role='dialog' aria-labelledby='lookupModalLabel' aria-hidden='true'> " +
                      "    <div class='modal-dialog modal-lg' role='document'> " +
                      "        <div class='modal-content'> " +
                      "            <div class='modal-header'> " +
                      "                <h5 class='modal-title'> Lookup </h5> " +
                      "                   <button type='button' class='close' data-dismiss='modal' aria-label='Close'> " +
                      "                      <span aria-hidden='true'>&times;</span> " +
                      "                   </button> " +
                      "            </div> " +
                      "            <div class='modal-body'> " +
                      "                    <table id='lookupResultTable' class='table table-striped table-bordered display nowrap' style='width:100%'> " +
                      "                        <thead> " +
                      "                            <tr> ";
    // Fill in the column name
    $.each(data.resultSet.columns, function(index, column){
        lookupModalHtml += "<td>" + column.columnName + "</td>";
    });
    lookupModalHtml += "                           </tr> " +
                       "                       </thead> " +
                       "                       <tbody> ";

    // check if we need to return any value other than the lookup variable
    var lookupRelatedControl = $('[data-lookup="' + id + '"]');

    $.each(data.resultSet.data, function(index, row){
        lookupModalHtml += " <tr> ";


        var param = "";
        if(lookupRelatedControl.length != 0) {
            lookupRelatedControl.each(function(){
                var relatedControlID = lookupRelatedControl.prop("id");
                var relatedControlVariableName = lookupRelatedControl.data("variable");
                $.each(data.resultSet.columns, function(index, column){
                    if (column.columnName == relatedControlVariableName) {
                        param += relatedControlID + "=" + row[column.columnName] + ";";
                    }
                });
            });
        }

        $.each(data.resultSet.columns, function(index, column){
            if (column.columnName == data.returnColumn) {
                lookupModalHtml += "<td><a href='#' onclick='lookupSelect(\"" + id + "\", \"" + row[column.columnName] + "\", \"" + param + "\")'>" + row[column.columnName] + "</a></td>";
            }
            else {
                lookupModalHtml += "<td>" + row[column.columnName] + "</td>";
            }
        });
        lookupModalHtml += " </tr> ";
    });
    lookupModalHtml += "                   </table>"+
                      "            </div> " +
                      "            <div class='modal-footer'> " +
                      "                <button type='button' class='btn btn-secondary' data-dismiss='modal'>Close</button> " +
                      "            </div> " +
                      "        </div> " +
                      "    </div> " +
                      "</div>";
    return lookupModalHtml
}
var lookupSelect = function(id, value, parameters) {
    $("#" + id).val(value);
    // Parameters is in the format of a list fo key=value,
    // separated by ;
    if (parameters != ""){
        var parameterArray = parameters.split(";");
        $.each(parameterArray, function(index, parameter){
            if (parameter != "") {
                var keyValuePair = parameter.split("=");
                // only continue when it is key=value
                // key is the id of the control and
                // value is the value we would like to fill into the control
                if (keyValuePair.size == 2) {
                    var key = keyValuePair[0];
                    var value = keyValuePair[1];
                    $("#" + key).val(value);
                }
            }
        });
    }
    $("#lookupModal").modal("hide");
}

var initFunctionKeyHandler = function() {
    shortcut.add("Alt+F3",function() {
	    // see if current focused control has a F3 key handler defined
        var focusedElement = $(':focus');
        // only when current element is a input and has a data-fkey = 'F3'
        // defined
        if (focusedElement.is("input:text") && focusedElement.data("fkey") == "F3") {
            var variable = focusedElement.data("variable");
            // Get the next number from the server and fill in the value to
            // current control
            var url = "/ws/systemtool/universalidentifier/" + variable + "/nextnumber";
            $.ajax({url:url})
                .done(function(res){
                    if (res.status == 0) {
                        focusedElement.val(res.data);
                    }
                    // ignore any error
                });
        }
    });


}

$(document).ready( function () {

    // Load i18n first so that if any of the controller
    // need the translation, it can get the right result
    initI18n();

    initQueryButtons();

    initDataTable();
    // Init checkbox to be tri-state checkbox
    initCheckBox();

    initDropdownLists();

    initValidation();

    initAutoCompleteControls();

    initDatePicker();

    initLookupController();

    initFunctionKeyHandler();
});