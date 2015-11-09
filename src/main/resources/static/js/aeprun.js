var httpRequests = null;
var httpRequests;
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
	loading(content, true);
	var httpRequest = getHttpRequest();
	httpRequest.open("GET", convertUrl(dom), true);
	httpRequest.send(null);
	httpRequest.onreadystatechange = function() {
		if (httpRequest.readyState == 4) {
			if (httpRequest.status == 200) {
				alert(httpRequest.responseText);
				loading(content, false);
			}
		}
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