#!rexx


race.0=4
race.1="Eye Guys"
race.2="Ophidians"
race.3="Teal Magi"
race.4="Purple Dragons"

/*
players.=""
address system "ls -1 players/" output stem players.
*/

say "Content-Type: text/html; charset=us-ascii"
say
say "<html>"
say "<head>"
say "<title>Ibex Signup</title>"
say "<style>"
say "body {font-family:Verdana;background-color:black;color:white;opacity:1;}"
say "#bod {font-family:Verdana;background-color:black;color:white;opacity:0;}"
say ".ib {font-family:Verdana;font-size:10pt; font-weight:bold; background-color:#000000; color:#e0a0d0; border:solid 1px white;padding:3px;}"
say "</style>"
say "<script type=""text/javascript"" src=""moo.js""></script>"
say "</head>"

say "<body><div id=bod>"

say "<script type=""text/javascript"">"
say "var bgcb='#000000';var bgcf='#403040';var fgcb='#E0A0D0';var fgcf='#FFFFFF';"
say "function fokus(tr){$(tr).effects().start({'backgroundColor':bgcf,'color':fgcf});}"
say "function blurr(tr){$(tr).effects().start({'backgroundColor':bgcb,'color':fgcb});}"
say "</script>"

say "<h1 style=""border-bottom:solid 1px white"">Ibex signup</h1>"
say "<p>Welcome to Ibex.  To sign up, fill this out and hit the <b>submit</b> button.</p>"
say "<form action=signup.rexx method=post>"
say "<table>"
say "<tr><td>Dynasty name</td><td><input id=d class=ib type=text name=dynasty onfocus=""fokus(this);"" onblur=""blurr(this);""></td></tr>"
say "<tr><td>Your first name</td><td><input id=f class=ib type=text name=firstname onfocus=""fokus(this);"" onblur=""blurr(this);""></td></tr>"
say "<tr><td>Leader's title</td><td><input id=t class=ib type=text name=title onfocus=""fokus(this);"" onblur=""blurr(this);""></td></tr>"
say "</table>"

say "<br><br><br>"

say "<table cellpadding=10 border=0>"
say "<tr><td colspan=4>Select the representation of your species:</td></tr>"
say "<tr><td align=center><img src=images/eyeguy.gif alt=EyeGuy></td>"
say "<td align=center><img src=images/snakeguy.gif alt=Ophidian></td>"
say "<td align=center><img src=images/mageguy.gif alt=TealMagi></td>"
say "<td align=center><img src=images/purpledragonguy.gif alt=PurpleDragon></td></tr>"
say "<tr>"

players='ls'('players/')


do i=1 to race.0

  say "<td align=center>" race.i "<br>"
  if pos(i||".player",players)>0 then
    say "(unavailable)"
  else
    say "<input type=radio name=race value="||i||">"
  say "</td>"
end

say "</tr></table>"
say "<br><input type=submit value=Submit style='background-color:#502030; color:white; border:solid 1px gray;'>"
say "</form>"

/*say "<hr>" players "<hr>"
*/

say "<script type=""text/javascript"">"
say "$('bod').effect('opacity',{duration:2000}).start(0,1);"
say "function fokker(){$('d').focus();}"
say "fokker.delay(2000);"
say "</script>"

say "</div></body>"
say "</html>"
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

  call lineout logfile,me date("U") time() getenv("REMOTE_ADDR") "-" "-" m """"||getenv("HTTP_USER_AGENT")||""""
return 0
