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
                if ($(this).data("custominit") == true) {
                    customInitDataTable($(this).prop("id"));
                }
                else if ($(this).data("scrollx") == true) {
                    $(this).DataTable({
                        "scrollX": true,
                        fixedHeader: true
                    });
                }
                else {
                    $(this).DataTable();
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

var loadDropdownList = function() {
    $('select').each(function(){
        var variable = $(this).data("variable");
        if (variable != undefined && variable != "") {
            // Call AJAX to load the content of the dropdown list
            // from webservice or cache
            loadDropdownListContent(variable)

            // Register a keydown action so when the user press F5,
            // we will clear cache and reload the content from web service
            $(this).keydown(function(event){
                // F5 code = 116
                if(event.which == 116) {
                    // We won't refresh the whole page
                    event.preventDefault();
                    loadDropdownListContentExcludeCache(variable);
                }

            })
         }
    });
}

var loadDropdownListContentExcludeCache = function(variable) {
    var url = '/ws/control/dropdown/' + variable + "?cache=false";
    $.ajax({
        url:url
    }).done(function( res ) {
        renderSelection(res.data.variable, res.data);
        dropdownListCache.set(url, res.data);
    });

}
var loadDropdownListContent = function(variable) {
    var url = '/ws/control/dropdown/' + variable;
    $.ajax({
        url:url,
        beforeSend: function() {
            if (dropdownListCache.exist(url)) {
                var data = dropdownListCache.get(url);
                renderSelection(data.variable, data);
                return false;
            }
            return true;
        }
    }).done(function( res ) {
        renderSelection(res.data.variable, res.data);
        dropdownListCache.set(url, res.data);
    });

}

var renderSelection = function(variable, data) {

    var selectionControls = $('select[data-variable="' + variable + '"]');
    if(selectionControls.length != 0) {
        selectionControls.each(function(){
            var selectionControl = $(this);

            // Remove all the options before we start to fill in new options
            selectionControl.empty();
            if(data.allowBlankRowFlag) {
                selectionControl.append('<option value=""></option>');
            }
            $.each(data.dropdownOptions, function(i, option){
                selectionControl.append('<option value="' + option.value + '">' + option.text + '</option>');
            });
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
            var variable = inputControl.data("variable");
            // If we already have the variable in cache, then
            // load from cache. Otherwise, get from
            // server
            if (autoCompleteCache.exist(variable)) {
                inputControl.autocomplete({
                    source: autoCompleteCache.get(variable)
                });
            }
            else {
                var url = '/ws/control/autocomplete/' + variable;
                $.ajax({
                    url:url,
                    variable:variable
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
                    autoCompleteCache.set(variable, res.data);
                });

            }


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

var initCryptoCurrent = function() {
    var eosAmount = 0;
    var ethAmount = 1.02;
    var bnbAmount = 296.21;

    // Get the price

    var eosPrice = 3.83;
    var ethPrice = 140.3;
    var bnbPrice = 6.07;

    var eosValue = Number((eosAmount * eosPrice).toFixed(2));
    var ethValue = Number((ethAmount * ethPrice).toFixed(2));
    var bnbValue = Number((bnbAmount * bnbPrice).toFixed(2));

    var acountValue = eosValue + ethValue + bnbValue;

    var eosPercent = Number((eosValue * 100 / acountValue).toFixed(2));
    var ethPercent = Number((ethValue * 100 / acountValue).toFixed(2));
    var bnbPercent = Number((bnbValue * 100 / acountValue).toFixed(2));

                console.log("eosPrice: " + eosPrice + "\n" +
                            "eosAmount: " + eosAmount + "\n" +
                            "eosValue: " + eosValue + "\n" +
                            "eosPercent: " + eosPercent + "\n" +
                            "ethPrice: " + ethPrice + "\n" +
                            "ethAmount: " + ethAmount + "\n" +
                            "ethValue: " + ethValue + "\n" +
                            "ethPercent: " + ethPercent + "\n" +
                            "bnbPrice: " + bnbPrice + "\n" +
                            "bnbAmount: " + bnbAmount + "\n" +
                            "bnbValue: " + bnbValue + "\n" +
                            "bnbPercent: " + bnbPercent)
    var eosInfo = eosPrice + " / " + eosAmount + " / " + eosValue + " / " + eosPercent + "%";
    var ethInfo = ethPrice + " / " + ethAmount + " / " + ethValue + " / " + ethPercent + "%";
    var bnbInfo = bnbPrice + " / " + bnbAmount + " / " + bnbValue + " / " + bnbPercent + "%";
                $("#eosInfo").text(eosInfo);
                $("#ethInfo").text(ethInfo);
                $("#bnbInfo").text(bnbInfo);

}

$(document).ready( function () {
    initQueryButtons();
    initDataTable();
    // Init checkbox to be tri-state checkbox
    initCheckBox();
    loadDropdownList();

    initValidation();

    initAutoCompleteControls();

    initI18n();

    initCryptoCurrent();
});