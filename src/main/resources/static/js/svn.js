window.onload = function() {
	var con = document.getElementById("content");
	var em = document.getElementsByTagName("p");
	for (var i = 0; i < em.length; i++) {
		var text = em[i].innerHTML;
		if (text.indexOf("Already Fix") > 0 || text.indexOf("Not Fixed") > 0) {
			var pos = document.getElementById("high");
			var p = document.createElement("p");
			p.innerHTML = text;
			pos.appendChild(p);
			return;
		}
	}
}