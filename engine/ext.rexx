#!rexx
/*
   ext.rexx - Ibex external gateway
   
*/

thishost="hostname"()

if thishost<>"mini" then
  rootdir="/kunden/homepages/20/d100701421/htdocs/hexibex/engine/"
else
  rootdir="/Users/jeff/Sites/ibex/"
dbdir=rootdir||"db/"

pathinfo=getenv("PATH_INFO")
if pathinfo="" then pathinfo="/global/leaderboard.csv"
pathinfo=lower(pathinfo)
parse var pathinfo +0 "/" scope "/" cmd "." fmt

cr="0a"x

items.="x"
items.0=0

out=""

call setformat

say "Content-type:" ct
say

select
  when scope="global" then call global
  otherwise
  do
    say "Syntax error:"
    say "scope=" scope
    say "cmd=" cmd
    say "format=" fmt
    exit
  end
end

call emit
say out

exit

global:
select
  when cmd="leaderboard" then
  do
    call readfile "leaderboard.d"
  end
  when cmd="time" then
  do
    call readtime
  end
  otherwise
  do
    say "Syntax error (global)"
  end
end

emit:
select
  when fmt="json" then
  do
   out='{"'||cmd||'": {'||cr
   do i=2 to items.0 /* row 1 is column headings */
     out=out||'   "'||items.i.1||'": {'
     do j=2 to (items.i.0)-1
       out=out||'"'||items.1.j||'":"'||items.i.j||'"'
       if j<(items.i.0)-1 then out=out||', '
     end
     out=out||'}'
     if i<items.0 then out=out||' ,'
     out=out||cr
   end
   out=out||cr||'}}'
  
  end
  otherwise
  do
   out=top
   do i=2 to items.0 /* row 1 is column headings */
     out=out||linestart
     do j=startcol to (items.i.0)-1
       out=out||items.i.j
       if j<(items.i.0)-1 then out=out||sep
     end
     out=out||lineend
   end
   out=out||bot
  end
end
return

readfile:
parse arg fn
thef=dbdir||fn
do while lines(thef)>0
  l=linein(thef)
  n=items.0+1
  items.0=n
  r="dummy"
  j=0
  do while r <> ""
    parse var l x "|" r
    j=j+1
    items.n.j=x
    l=r
  end
  items.n.0=j+1
end
return

readtime:
items.0=2
items.1.0=2
items.1.1="time"
items.2.0=2
items.2.1=date("U") time("N")
return

setformat:
startcol=1
top=""
bot=""
linestart=""
lineend=""
sep=""
ct="text/plain"
select
  when fmt="html" then
  do
    top="<html><body><table>"
    bot=cr || "</table></body></html>"
    linestart=cr || "<tr><td>"
    lineend="</td></tr>"
    sep="</td><td>"
    ct="text/html"
    startcol=2
  end
  when fmt="htmlx" then
  do
    top="<table>"
    bot="</table>"
    linestart=cr || "<tr><td>"
    lineend="</td></tr>"
    sep="</td><td>"
    ct="text/html"
    startcol=2
  end
  when fmt="csv" then
  do
    lineend=cr
    sep=","
    ct="text/plain"
  end
  when fmt="txt" then
  do
    sep=" "
    lineend=cr
    ct="text/plain"
  end
  when fmt="json" then
  do
  /* do it all in 'emit'
    top='{"'||cmd||'":'||cr
    bot=cr||'}}'
    linestart='    {'
    sep=', '
    lineend='},'||cr
    ct="text/plain"
    startcol=2
  */
  end
  otherwise
  do
    say "Invalid file format"
  end
end  

return