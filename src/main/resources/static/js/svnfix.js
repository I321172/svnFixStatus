function refreshPage() {
	window.location.reload();
}
window.onload = function() {
	addEvent(document.getElementById("refreshenv"), "click", refreshEnv);
}

function refreshEnv() {
	var msg = document.getElementById("refreshmsg");
	removeEvent(document.getElementById("refreshenv"), "click", refreshEnv);
	msg.innerHTML = "Refresh the Environment now; Patient and Wait a while";
	var httpRequest = createHttpRequest();
	httpRequest.open("GET", "/refresh/env", true);
	httpRequest.send(null);
	httpRequest.onreadystatechange = function() {
		if (httpRequest.readyState == 4) {
			if (httpRequest.status == 200) {
				msg.innerHTML = httpRequest.responseText;
				var t = setTimeout("window.location.reload();", 3000);
			}
		}
	}
}

function addEvent(obj, type, handler) {
	if (obj.addEventListener) {
		obj.addEventListener(type, handler);
	} else if (obj.attachEvent) {
		obj.attachEvent('on' + type, handler);
	}
}

function removeEvent(obj, type, handler) {
	if (obj.addEventListener) {
		obj.removeEventListener(type, handler);
	} else if (obj.detachEvent) {
		obj.detachEvent('on' + type, handler);
	}
}

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