var httpRequests = null;
var resp;
var index = 0;

function createHttpRequest() {
	var httpRequest;

	// for main browser
	if (window.XMLHttpRequest) {
		httpRequest = new XMLHttpRequest();
		// for some mozilla bug fix
		/*
		 * NO XML mime-type in header，some Mozilla not work;
		 * then，httpRequest.overrideMimeType('text/xml'); override and send to
		 * server header
		 */
		if (httpRequest.overrideMimeType) {
			httpRequest.overrideMimeType("text/xml");
		}
		return httpRequest;
	}
	// for old IE 5 6
	if (window.ActiveXObject) {
		try {
			httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e) {
			try {
				httpRequest = new ActiveXObject("Msxml2.XMLHTTP");
			} catch (ex) {
				alert("XMLHTTPRequest Object Create Failure! " + ex.message);
			}
		}
	}
	return httpRequest;
}

function getHttpRequest() {
	return httpRequests[index++ % 3];
}

function initHttpRequest() {
	httpRequests = new Array();
	httpRequests[0] = createHttpRequest();
	httpRequests[1] = createHttpRequest();
	httpRequests[2] = createHttpRequest();
}

function fetchRunningJobs(dom) {
	var content = dom.parentNode.parentNode.getElementsByClassName("content")[0];
	showContent(content, true);
	loading(content, true);
	var httpRequest = getHttpRequest();
	httpRequest.open("GET", convertUrl(dom), true);
	httpRequest.send(null);
	httpRequest.onreadystatechange = function() {
		if (httpRequest.readyState == 4) {
			if (httpRequest.status == 200) {
				loading(content, false);
				showJobs(content, httpRequest.responseText);
			}
		}
	}
}

function changeContent(dom) {
	var content = dom.parentNode.parentNode.getElementsByClassName("content")[0];
	var isShow = dom.value == "Show";
	showContent(content, isShow);
}

function showContent(content, show) {
	if (show) {
		content.removeAttribute("hidden");
		content.parentNode.getElementsByClassName("collapse")[0].value = "Hide";
	} else {
		content.setAttribute("hidden", "true");
		content.parentNode.getElementsByClassName("collapse")[0].value = "Show";
	}
}

function showJobs(dom, response) {
	resp = JSON.parse(response);
	removeAllChild(dom);
	appendSummary(dom);
	appendJobStatus(dom);
}

function appendSummary(dom) {
	var la = document.createElement("label");
	la.innerHTML = "Total Count = " + resp.totalCount + " ; List Count = "
			+ resp.actualCount;
	dom.appendChild(la);
}

function appendJobStatus(dom) {
	var tb = createTable(dom);
	fillTableWithJobStatus(tb)
}

function createTable(dom) {
	var tb = document.createElement("table");
	tb.setAttribute("class", "joblist");
	var len = resp.jobRuns.length;
	var rLen = len / 4;
	for (var i = 0; i < rLen; i++) {
		var row = tb.insertRow(i);
		for (var cLen = 0; cLen < 4; cLen++) {
			row.insertCell(cLen).innerHTML = "&nbsp;"
		}
	}
	dom.appendChild(tb);
	return tb;
}

function createLink(job) {
	var link = document.createElement("a");
	link.setAttribute("href", "#");
	link.setAttribute("class", job.jobStatus);
	link.setAttribute("onclick", "changeHref(this)");
	link.innerHTML = job.jobName;
	return link;
}

// 
function fillTableWithJobStatus(tb) {
	var jobs = resp.jobRuns;
	var index = 0;
	for (var i = 0; i < tb.rows.length; i++) {
		for (var j = 0; j < tb.rows[0].cells.length; j++) {
			if (index < jobs.length) {
				var link = createLink(jobs[index])
				tb.rows[i].cells[j].appendChild(link);
				index++;
			}
		}
	}
}

function changeHref(dom) {
	var type = dom.getAttribute("class");
	if (type.indexOf("Started") > 0 || type.indexOf("Initializing") > 0
			|| type.indexOf("Progress") > 0) {

	}
	dom.setAttribute("href", "http://www.baidu.com");
}

function removeAllChild(dom) {
	var len = dom.childNodes.length;
	for (var i = 0; i < len; i++) {
		dom.removeChild(dom.lastChild);
	}
}

function convertUrl(dom) {
	var user = document.getElementById("user").innerHTML;
	var module = document.getElementById("module").innerHTML;
	var env = document.getElementById("env").innerHTML;
	var status = dom.parentNode.parentNode.getAttribute("id");
	return "/aep?user=" + escape(user) + "&module=" + escape(module)
			+ "&status=" + status + "&env=" + escape(env);
}

function loading(dom, isShow) {
	var loading = document.getElementById("loading");
	if (isShow) {
		dom.appendChild(loading);
		loading.removeAttribute("hidden");
	} else {
		loading.setAttribute("hidden", true);
		document.body.appendChild(loading);
	}
}

window.onload = function() {
	initHttpRequest();
	clickElement("Started");
	clickElement("Initializing");
	clickElement("In Progress");
}

function clickElement(id) {
	document.getElementById(id).getElementsByClassName("refresh")[0].click();
}