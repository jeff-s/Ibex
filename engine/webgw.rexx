#!rexx
/* */

say "Content-Type: text/plain"
say
thishost='hostname'()
requestclientversion=getenv("HTTP_USER_AGENT")

if pos("infon",thishost)>0 then
  rootdir="/kunden/homepages/20/d100701421/htdocs/hexibex/engine/"
else
  rootdir="/Users/jeff/Sites/ibex/"

playerfiles=rootdir||"players/"

race.0=4
race.1="Eye Guys"
race.2="The Ophidians"
race.3="Teal Magi"
race.4="Purple Dragons"

qs=getenv("QUERY_STRING")
parse var qs "u=" u
if u="" then u="http://osnews.com/files/recent.xml"
parse var u u "&" .
u=deweb(u)

pathinfo=getenv("PATH_INFO")
upper pathinfo
cmd=changestr("/",pathinfo,"")
cmd=changestr("BORG",cmd,"")
if cmd="" then cmd="FEED"

select
  when cmd="FEED" then
  do
/* whole="wget"("-q -O - http://hexibex.us/blosx.om/index.rss") */
whole="wget"("-q -O -" u)
i=0
do until ptitle=""
  parse var whole . "<item>" . "<title>" ptitle "</title>" . "<link>" plink "</link>" . "<description>" pdesc "</description>" whole
  i=i+1
  rss.i.title=ptitle
  rss.i.link=plink
  pdesc=changestr("]]>",pdesc,"")
  pdesc=changestr("<![CDATA[",pdesc,"")
  pdesc=changestr("&gt;",pdesc,">")
  pdesc=changestr("&lt;",pdesc,"<")
  pdesc=changestr("&amp;",pdesc,"&")
  pdesc=changestr('"',pdesc,"'")
  rss.i.desc=pdesc
end
i=i-1

say '{"items":['
comma=","
do j=1 to i
 if j=i then comma=""
 say ' {"title":"'rss.j.title'", "link":"'rss.j.link'", "desc":"'rss.j.desc'"}'||comma
end
say "]}"
end
when cmd="PLAYERS" then
do
  player.=""
  do i=1 to race.0
    f=playerfiles||i||".player"
    if lines(f)>0 then
    do
      a=readall(f)
      player.i.dynasty=suckout(a,"dynasty")
      player.i.firstname=suckout(a,"firstname")
      player.i.title=suckout(a,"title")
    end
  end

  comma=","
  say '{"players":['
  do i=1 to race.0
    if i=race.0 then comma=""
    say ' {"id":"'i'", "dynasty":"'player.i.dynasty'", "firstname":"'player.i.firstname'", "title":"'player.i.title'"}'||comma
  end
  say "]}"
end
otherwise
do
  say "Invalid command:" cmd
end
end

exit

/* ------- */

readall:procedure
parse arg thef

r=""
n="0A"x
do while lines(thef)>0
  l=linein(thef)
  r=r||l||n
end
return r

suckout:procedure
parse arg a,s
r=""
n="0A"x
f=n||s||"="
p=pos(f,a)
if p>0 then
do
  p2=pos(n,a,p+1)
  r=substr(a,p+length(f),p2-(p+length(f)))
end
return r

/* ------- */
DeWeb: PROCEDURE; PARSE ARG In, Op
/* *******************************************
DeWeb converts hex encoded (e.g. %3B=semi-colon) 
characters in the In string to the equivalent 
ASCII characters and returns the decoded string.
If the 2 characters following a % sign do not
represent a hexadecimal 2 digit number, then 
the % and following 2 characters are returned
unchanged. If the string terminates with a % then
the % sign is returned unchanged. If the final
two characters in the string are a % sign 
followed by a single hexadecimal digit then  
they are returned unchanged.

The optional Op argument contains a set of 
characters which allows you to tell DeWeb to:
'+' convert plus signs (+) to spaces
    in the input before the hex decoding is done.
'*' convert asterisks (*) to percent signs (%) 
    after the decoding.  This option
    is often used with Oracle.
   
Authors: Les Cottrell & Steve Meyer - SLAC

Examples:
  SAY DeWeb('%3Cpre%3e%20%%25Loss  %Util%') 
  results in:  '<pre> %%Loss  %Util%'
  SAY DeWeb('%3cpre%3eName++Address*','*+')
  results in   '<pre>Name  Address%'
******************************************* */
IF POS('+',Op)\=0 THEN In=TRANSLATE(In,' ','+')
Start=1; Decoded=''; String=In
DO WHILE POS('%',String)\=0
   PARSE VAR String Pre'%'+1 Ch +2 In
   IF DATATYPE(Ch,'X') & LENGTH(Ch)=2 THEN 
        Ch=X2C(Ch)
   ELSE DO; In=Ch||In; Ch='%'; END
   Decoded=Decoded||Pre||Ch
   Start=LENGTH(Decoded)+1
   In=Decoded||In
   String=SUBSTR(In,Start)
END
IF POS('*',Op)\=0 THEN In=TRANSLATE(In,'%','*')
RETURN In
