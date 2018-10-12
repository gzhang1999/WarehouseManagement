var langs = ['EN', 'CN'];
var langCode = '';
var langJS = null;


var translate = function (jsdata)
{
	$("[mls_id]").each (function (index)
	{
		var strTr = jsdata [$(this).attr ('mls_id')];
	    $(this).html (strTr);
	});
}


langCode = navigator.language.substr (0, 2);
console.log(langCode)
if (langCode in langs)
	$.getJSON('/i18n/message_'+langCode+'.json', translate);
else
	$.getJSON('lang/message_EN.json', translate);