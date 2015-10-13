var httpRequest = null;
var username = "pltmod";
var password = "pltm0d123"
var runUrl = "http://autoaep.wdf.sap.corp:8080/aep/jobInstance/jobRuns/?format=json&sort=id&order=desc&username=pltmod&modulesJobInstancesFilter=system,systemUltra&jobStatuses=Aborted,Aborting,Completed,Creation%20Failed,Failed,In%20Progress,Initializing,Started&invocationTypes=ARP,Cron,FailureReRun,Manual,Missed&environmentDropDownFilter=qaautocandHanaRot,qaautocandROT&max=50&offset=0";
function createHttpRequest() {
	if (httpRequest == null) {
		// for main browser
		if (window.XMLHttpRequest) {
			httpRequest = new XMLHttpRequest();
			// for some mozilla bug fix
			/*
			 * NO XML mime-type in header，some Mozilla not work;
			 * then，httpRequest.overrideMimeType('text/xml'); override and send
			 * to server header
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
	}
	return httpRequest;
}

function fetchRunningJobs() {
	httpRequest = createHttpRequest();
	httpRequest.open("GET", runRul, true, username, password);
	httpRequest.send(null);
	httpRequest.onreadystatechange = function() {
		if (httpRequest.readyState == 4) {
			if (httpRequest.status == 200) {
				document.getElementById("showmsg").innerHTML = httpRequest.responseText;
			}
		}
	}
}