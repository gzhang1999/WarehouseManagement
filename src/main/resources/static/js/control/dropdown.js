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
        console.log("init dropdown list: " + variable);
        if (variable != undefined && variable != "") {
            var url = '/ws/control/dropdown/' + variable;
            $.ajax({
                url:url,
                beforeSend: function() {
                    if (dropdownListCache.exist(url)) {
                        var data = dropdownListCache.get(url);
                        renderSelection(data.variable, data);
                        dropdownListCache.set(url, data);
                        return false;
                    }
                    return true;
                }
             })
             .done(function( res ) {
                 console.log("init dropdown list: " + res.data.variable);
                 console.log(">> return : " + JSON.stringify(res.data));
                 renderSelection(res.data.variable, res.data);
             });
         }
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

loadDropdownList();