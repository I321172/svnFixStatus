var preTarget;
var data;
var original;
var isMain = true;
var sortFlag = true;
var packagedata;
window.onload = function() {
	var tab = document.getElementById("myTable");
	original = readTable(tab);
}

function selectRow(dom) {
	if (preTarget != undefined)
		preTarget.style.background = "#FFFFFF";
	preTarget = dom.parentNode;
	preTarget.style.background = "#43CD80";
}

function getValues(dom) {
	var tab = dom.parentNode.parentNode;
	if (tab.getAttribute("id") != "thead") {
		isMain = false;
		return readTempTable();
	} else {
		isMain = true;
		return readMainTable();
	}
}

function readTable(tab) {
	var col = tab.rows[0].cells.length;
	var tb = new Array();
	for (var i = 0; i < tab.rows.length; i++) {
		var temp = new Array();
		for (var j = 0; j < col; j++) {
			if (j == 0) {
				temp[j] = tab.rows[i].cells[j].innerHTML;
				temp[j + 1] = tab.rows[i].cells[j].getAttribute("title");
			} else
				temp[j + 1] = tab.rows[i].cells[j].innerHTML;
		}
		tb[i] = temp;
	}
	return tb;
}

function readTempTable() {
	var tab = document.getElementById("tempTable");
	return readTable(tab);
}

function readMainTable() {
	if (data == undefined) {
		var tab = document.getElementById("myTable");
		data = readTable(tab);
	}
	return data;
}

function getIndex(node) {
	if (node) {
		var index = 0;
		while (node = node.previousSibling) {
			if (node.nodeType == 1)
				index++;
		}
		return index;
	}
}

function sortArray(dom) {
	var sortIndex = getIndex(dom) + 1;
	var arr = getValues(dom);

	for (var i = 0; i < arr.length; i++) {
		for (var j = 0; j < i; j++) {
			if (sortFlag) {
				if (parseFloat(arr[i][sortIndex]) < parseFloat(arr[j][sortIndex])) {
					var temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
			} else {
				if (parseFloat(arr[i][sortIndex]) > parseFloat(arr[j][sortIndex])) {
					var temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
			}
		}
	}
	return arr;
}

function sortTable(dom) {
	var arr = sortArray(dom);
	fillTable(arr);
	sortFlag = !sortFlag;
}

function fillTable(arr) {
	if (isMain) {
		tab = document.getElementById("myTable");
	} else {
		tab = document.getElementById("tempTable");
	}
	var col = tab.rows[0].cells.length;
	for (var i = 0; i < arr.length; i++) {
		for (var j = 0; j < col; j++) {
			if (j == 0) {
				tab.rows[i].cells[j].innerHTML = arr[i][j];
				tab.rows[i].cells[j].setAttribute("title", arr[i][j + 1]);
				if (arr[i][j] == arr[i][j + 1]) {
					tab.rows[i].cells[j].parentNode.setAttribute("class",
							"package");
				} else {
					tab.rows[i].cells[j].parentNode.setAttribute("class",
							"class");
				}
			} else
				tab.rows[i].cells[j].innerHTML = arr[i][j + 1];
		}
	}
}

function createTable(arr) {
	var table = document.createElement("table");
	table.setAttribute("border", "1");
	table.setAttribute("id", "created");
	var thead = createHeader();
	var tbody = createBody(arr);
	table.appendChild(thead);
	table.appendChild(tbody);
	document.getElementById("newTable").appendChild(table);
}

function createTR(arr) {
	var tr = document.createElement("tr");

	for (var j = 0; j < arr.length - 1; j++) {
		var td = document.createElement("td");
		if (j == 0) {
			td.innerHTML = arr[j];
			td.setAttribute("onclick", "selectRow(this)");
			td.setAttribute("id", "name");
			td.setAttribute("title", arr[j + 1]);
			if (arr[j] == arr[j + 1])
				tr.setAttribute("class", "package");
		} else {
			td.innerHTML = arr[j + 1];
		}
		tr.appendChild(td);
	}

	return tr;
}

function createBody(arrs) {
	var tbody = document.createElement("tbody");
	for (var i = 0; i < arrs.length; i++) {
		var tr = createTR(arrs[i]);
		tbody.appendChild(tr);
	}
	tbody.setAttribute("id", "tempTable");
	return tbody;
}

function createHeader(arr) {
	var thead = document.createElement("thead");
	var tr = document.createElement("tr");
	var orig = document.getElementById("thead");
	for (var j = 0; j < orig.rows[0].cells.length; j++) {
		var td = document.createElement("th");
		if (j > 0) {
			td.setAttribute("onclick", "sortTable(this)");
		}
		td.innerHTML = orig.rows[0].cells[j].innerHTML;
		tr.appendChild(td);
	}
	thead.appendChild(tr);
	return thead;
}

function getFilterArr(val) {
	var res = new Array();
	var index = 0;
	for (var i = 0; i < original.length; i++) {
		if (original[i][1].toLowerCase().indexOf(val.toLowerCase()) >= 0) {
			res[index++] = original[i];
		}
	}
	return res;
}

function filter() {
	removeCreated();
	var val = document.getElementById("filterValue").value;
	if (val != "") {
		var arr = getFilterArr(val);
		if (arr.length > 0)
			createTable(arr);
	} else {
		removeCreated();
	}
}

function showPackage() {
	removeCreated();
	var arr = getPackage();
	createTable(arr);
}

function getPackage() {
	if (packageData == undefined) {
		isMain = false;
		var tab = document.getElementById("myTable");
		var col = tab.rows[0].cells.length;
		var packageData = new Array();
		var index = 0;
		for (var i = 0; i < tab.rows.length; i++) {

			if (tab.rows[i].getAttribute("class") == "package") {
				var temp = new Array();
				for (var j = 0; j < col; j++) {
					if (j == 0) {
						temp[j] = tab.rows[i].cells[j].innerHTML;
						temp[j + 1] = tab.rows[i].cells[j]
								.getAttribute("title");
					} else
						temp[j + 1] = tab.rows[i].cells[j].innerHTML;
				}
				packageData[index++] = temp;
			}
		}
	}
	return packageData;
}

function removeCreated() {
	var table = document.getElementById("created");
	if (table != undefined) {
		table.parentNode.removeChild(table);
	}
}
function reset() {
	removeCreated();
	document.getElementById("filterValue").value = "";
	isMain = true;
	fillTable(original);
}