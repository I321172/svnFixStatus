var hiddenNum = 0;
function filter() {
	var val = document.getElementById("filter").value;
	var tabl = document.getElementsByTagName("table");
	for (var i = 0; i < tabl.length; i++) {
		var tds = tabl[i].getElementsByClassName("long");
		var var1 = tds[0].innerHTML;
		if (var1.indexOf("File:") >= 0) {
			var1 = var1.substring(var1.lastIndexOf("/") + 1);
			if (var1.indexOf(val) < 0) {
				tabl[i].setAttribute("hidden", "true");
				hiddenNum++;
			}
		}
	}
	changeSizeInfo();
}

function reset() {
	var eles = document.getElementsByTagName("table");
	hiddenNum = 0;
	for (var i = 0; i < eles.length; i++) {
		eles[i].removeAttribute("hidden");
	}
	changeSizeInfo();
}

function filterEvent(event) {
	if (event.keyCode == 13)
		filter();
}

function changeSizeInfo() {
	var p = document.getElementsByTagName("p")[0];
	p.innerHTML = "List Total " + (getTotal() - hiddenNum) + " Files below: ";
}

function getTotal() {
	return document.getElementsByTagName("table").length;
}