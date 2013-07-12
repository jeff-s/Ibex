#!rexx

thishost=getenv("HTTP_HOST")

if pos("hexibex.us",thishost)>0 then
  rootdir="/kunden/homepages/20/d100701421/htdocs/hexibex/engine/"
else
  rootdir="/Users/jeff/Sites/ibex/"

race.0=4
race.1="Eye Guys"
race.1.i="eyeguy.gif"
race.2="Ophidians"
race.2.i="snakeguy.gif"
race.3="Teal Magi"
race.3.i="mageguy.gif"
race.4="Purple Dragons"
race.4.i="purpledragonguy.gif"


n="0A"x

say "Content-Type: text/html"
say

pathinfo=getenv("PATH_INFO")
if pathinfo="" then pathinfo="/3"

parse var pathinfo +0 "/" theplayer "/" badge

/* read in info */

incoming=rootdir||"incoming/"
playerfile=rootdir||"players/"||theplayer||".player"
playersince=stream(playerfile,"C","QUERY TIMESTAMP")
do while lines(playerfile)>0
  l=linein(playerfile)
  parse var l lside "=" rside
  if lside="dynasty" then dynasty=rside
  if lside="firstname" then firstname=rside
  if lside="title" then title=rside
end
turnsplayed=words("ls"(incoming||theplayer||".*"))
mapdir=rootdir||"maps/"
map=mapdir||'cat'(rootdir||"turn")||".map"
fleetstrength=0
planets=0
do while lines(map)>0
  l=linein(map)
  parse var l lside ":" rside
  if lside="player"||theplayer then fleetstrength=fleetstrength+1
  if lside="planet" then
  do
    parse var rside . "!" owner
    if owner=theplayer then planets=planets+1
  end
end

/* plurals */
s.="s"
s.1=""
if badge<>"badge" then
do 
  say "<html><head><title>Ibex Profile</title>"
  say "<style>"
  say "body {font-family:Tahoma, Arial; color:white; background-color:black;}"
  say ".i {width:312; border:solid 1px #c0b0c0; float:right; margin-left:20px;}"
  say ".t {font-size:85%; color:#c0b0c0; text-align:justify; font-weight:bold; }"
  say ".h {font-size:85%; color:#c0b0c0; text-align:right;}"
  say ".d {font-size:85%; color:white; font-weight:bold;}"
  say "</style>"
  say "</head>"
  say "<body>"
  say "<h1><image class=i src=/engine/images/"||race.theplayer.i||">" race.theplayer "</h1>"
  /* say "<h2>" title firstname dynasty "</h2>" */
  say "<p class=t>Now scattered across multiple galaxies and dimensions, the survivors of The Race have set about building empires for themselves. Although local inhabitants cause occasional snarls in plans (both militarily and politically), ultimately the greatest threat comes from the inevitable clashes with others from The Race whose powers rival their own. (Or some other descriptive blurbage about this race.)</p>"
  say "<h3>Intelligence Report</h3>"
  say "<table>"
  say "<tr><td class=h>Name</td><td class=d>" title firstname dynasty "</td></tr>"
  say "<tr><td class=h>Race</td><td class=d>" race.theplayer "</td></tr>"
  say "<tr><td class=h>Player since</td><td class=d>" playersince "</td></tr>"
  say "<tr><td class=h>Turns played</td><td class=d>" turnsplayed "</td></tr>"
  say "<tr><td class=h>Fleet strength</td><td class=d>" fleetstrength "ship"||s.fleetstrength||"</td></tr>"
  say "<tr><td class=h>Empire</td><td class=d>" planets "planetary system"||s.planets||"</td></tr>"
  say "</table>"
  say "</body></html>"
end
else /*badge*/
do
  say "<html><head><style>"
  say "body {background-color:black; font-size:75%; color:#c0b0c0; font-family:Tahoma, Arial; margin:0px; padding:0px; border:solid 1px #c0b0c0;}"
  say "p {margin:0px; padding:5px;}"
  say "b,a {color:white;}"
  say ".i {width:52; float:right; margin-left:5px; border:0px;}"
  say "</style><body>"
  say "<p><a href=http://hexibex.us/ target=_blank><img class=i src=/engine/images/"||race.theplayer.i||"></a><b>" title firstname dynasty "</b> of the" race.theplayer "<br>"
  s.="s"
  s.1=""
  say "Played <b>" turnsplayed "</b> turn"||s.turnsplayed||", fleet strength <b>" fleetstrength "</b></p>"
  /* say "<p align=right><a target=_blank href=http://hexibex.us/>http://hexibex.us/</a></p>" */
  say "</body></html>"
end
