#!rexx

thishost=getenv("HTTP_HOST")
requestclientversion=getenv("HTTP_USER_AGENT")

if pos("hexibex.us",thishost)>0 then
  rootdir="/kunden/homepages/20/d100701421/htdocs/hexibex/engine/"
else
  rootdir="/Users/jeff/Sites/ibex/"

logfile=rootdir||"gw.log"
playerfiles=rootdir||"players/"

race.0=4
race.1="Eye Guys"
race.2="The Ophidians"
race.3="Teal Magi"
race.4="Purple Dragons"

n="0A"x

say "Content-Type: text/plain"
say

httphdr="Content-Type: text/plain"||n||n

pathinfo=getenv("PATH_INFO")
if pathinfo="" then pathinfo="/3/GETBGM"

parse var pathinfo +0 "/" theplayer "/" thecmd

upper thecmd
if datatype(theplayer)^="NUM" then signal oops

turn=readall(rootdir||"turn")
turn=changestr(n,turn,"")
turn=changestr("0D"x,turn,"")


select
  when thecmd="PING" then
  do
    say ping(theplayer)
  end
  when thecmd="GETTURN" then
  do
    say getturn(theplayer,turn)
  end
  when thecmd="GETMAP" then
  do
    say getmap(turn)
  end
  when thecmd="GETBGM" then
    say getbgm()
  when thecmd="POSTTURN" then
  do
    if getenv("REQUEST_METHOD")="POST" then
    do
      postdata=charin(,,getenv("CONTENT_LENGTH"))
      say postturn(theplayer,postdata)
    end
    else
      say "Must POST."
  end
  otherwise
    signal oops
end
exit

/*------------------------------*/
ping:
parse arg p
r="ACKNOWLEDGED, PLAYER" p
v=linein("clientversion")
parse var requestclientversion "Ibex/" rcv
if rcv="" then
  r=r||n||"Invoked from non-Ibex client."
else
do
  if v+0 >> rcv+0 then
  do
    r="A newer Ibex client exists.  Go get it."
   end
end
call logg
return r

/*------------------------------*/
getturn:
parse arg p,theturn
r="[Ibex]"n

whole="wget"("-q -O - http://news.bbc.co.uk/rss/newsonline_world_edition/front_page/rss.xml")
parse var whole . "<item>" . "<description>" desc "</description>" .

/* r=r||"motd=This is the Message Of The Day for" date() time() n */
desc=changestr("&apos;",desc,"'")
desc=changestr('&quot;',desc,'"')
r=r||"motd="||desc||n
f=playerfiles||p||".player"

a=readall(f)
dynasty=suckout(a,"dynasty")
firstname=suckout(a,"firstname")
title=suckout(a,"title")

r=r||"title="||title||n
r=r||"firstname="||firstname||n
r=r||"dynasty="||dynasty||n
r=r||"moves=15"||n
r=r||"turn="||turn||n
x=stream(f,"C","CLOSE")

do i=1 to race.0
  r=r||"race"||i||"="||race.i||n
  f=playerfiles||i||".player"
  if lines(f)>0 then
  do
    a=readall(f)
    dynasty=suckout(a,"dynasty")
    firstname=suckout(a,"firstname")
    title=suckout(a,"title")
    r=r||"dynasty"||i||"="||dynasty||n
    r=r||"firstname"||i||"="||firstname||n
    r=r||"title"||i||"="||title||n
  end
end
f=rootdir||"media/"
bgms='ls'("-1" f)
r=r||"bgm="||word(bgms,random(1,words(bgms)))
r=r||n||readall(rootdir||"outgoing/"||p||"."||zpad(theturn))
r=r||n||readall(rootdir||"players/"||p||"."||zpad(theturn))
  
  
call logg(turn)
return r

/*------------------------------*/
nonworking_getbgm:
call logg("Got BGM")
r=""
i=0
f=rootdir||"media/"
bgms='ls'("-1" f)
fn=f||word(bgms,random(1,words(bgms)))
l=chars(fn)
r=charin(fn,1,l)

h="Content-Type: application/ibexbgm"||n
h="Content-Type: application/octet-stream"||n
/*h=h||"Content-Length:" l||n*/
h=h||n||n
r=h||r
r=h "hi"
return r

/*------------------------------*/
getmap:
arg theturn
r= readall(rootdir||"maps/"||theturn||".map")
call logg(theturn)
return r


/*------------------------------*/
postturn: parse arg pnum,pd
r=""  

/* parse out turn num somehow */
outf=rootdir||"incoming/"||pnum||"."||zpad(turn)
"rm" outf
call charout outf,pd,1
"chmod 666" outf

r="Your turn was accepted by the Ibex system."

call logg(turn)
return r

/*------------------------------*/
logg:
  parse arg m

  if m="" then m="-"
  
  parse source . . me
  me=substr(me,lastpos("/",me)+1)
  
  call lineout logfile,me date("U") time() getenv("REMOTE_ADDR") theplayer thecmd m """"||getenv("HTTP_USER_AGENT")||""""
return 0


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

zpad:parse arg a
r=right("00000"||a,3)
return r

/*------------------------------*/
oops:
say "INVALID COMMAND"
