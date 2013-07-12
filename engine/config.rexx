#!rexx

say "Content-Type: application/ibexconfig"
say

f=getenv("PATH_INFO")

parse var f +0 "/" therace "/" .

say "[Ibex]"
/* say "server=http://"getenv("HTTP_HOST")"/ibex/gw.rexx" */
say "server="||changestr("config.rexx","http://"||getenv("HTTP_HOST")||getenv("SCRIPT_NAME"),"gw.rexx")
say "player="therace
call logg
exit

/*------------------------------*/
logg:
  parse arg m

  parse source . . me
  me=substr(me,lastpos("/",me)+1)
  
  thishost=getenv("HTTP_HOST")
  
  if pos("hexibex.us",thishost)>0 then
    logfile="/kunden/homepages/20/d100701421/htdocs/hexibex/engine/gw.log"
  else
    logfile="/Users/jeff/Sites/ibex/gw.log"
  
  call lineout logfile,me date("U") time() getenv("REMOTE_ADDR") therace "-"  m """"||getenv("HTTP_USER_AGENT")||""""
return 0
