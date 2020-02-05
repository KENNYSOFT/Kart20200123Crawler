var nam=[];
var cnt=[];
for(var i=2138;i<=2159;++i)
{
	nam.push(`Map.entry(${i}l, "${GetItemDataList(i).name.replace(/(^\d+ | \(\d+개\)$)/,"")}")`);
	var res=/(?:^(\d+) | \((\d+)개\)$)/.exec(GetItemDataList(i).name);
	if(res!==null)cnt.push(`Map.entry(${i}l, ${res[1]||res[2]})`);
}
document.body.appendChild(t);
t.value=`public static final Map<Long, String> nameMap = Map.ofEntries(${nam.join(", ")});\npublic static final Map<Long, Integer> cntMap = Map.ofEntries(${cnt.join(", ")});`;
t.select();
document.execCommand('copy');
document.body.removeChild(t);