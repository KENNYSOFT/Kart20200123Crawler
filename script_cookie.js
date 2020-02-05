getCookie=function(name){return document.cookie.split("; ").filter(c=>c.startsWith(name+"="))[0].substr(name.length+1);};
makeLine=function(name){return "\tpublic static final String "+name+" = \""+getCookie(name)+"\";";};
var t=document.createElement("textarea");
document.body.appendChild(t);
t.value=["ENC","KENC","NPP"].map(x=>makeLine(x)).join("\n");
t.select();
document.execCommand('copy');
document.body.removeChild(t);