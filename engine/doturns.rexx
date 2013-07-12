#!/kunden/homepages/20/d100701421/htdocs/hexibex/engine/rexx
/*
Be sure to put the big long shebang on the server
'cause cron can't find the rexx interpreter otherwise

  #!/kunden/homepages/20/d100701421/htdocs/hexibex/engine/rexx
  
*/

say; say"--------------------------------"
say date("U") time()
say "Processing turn..."

thishost="hostname"()

if pos("mini",thishost)>0 then
do
  rootdir="/Users/jeff/Sites/ibex/"
  turntime=rootdir||"filedata/turntime"
  statsfile=rootdir||"filedata/stats"
  dbdir=rootdir||"db/"
end
else
do
  rootdir="/kunden/homepages/20/d100701421/htdocs/hexibex/engine/"
  turntime="/kunden/homepages/20/d100701421/htdocs/hexibex/plugins/filedata/turntime"
  statsfile="/kunden/homepages/20/d100701421/htdocs/hexibex/plugins/filedata/stats"
  dbdir=rootdir||"db/"
end
statsfile2=statsfile||"2"
statsdbfile=dbdir||"leaderboard.d"

turn=readall(rootdir||"turn")
turn=changestr("0A"x,turn,"")
turn=changestr("0D"x,turn,"")
newturn=turn+1
say "This turn=" turn "  Next turn=" newturn

race.0=4
race.1="Eye Guys"
race.2="The Ophidians"
race.3="Teal Magi"
race.4="Purple Dragons"



planet.0=0
nebula.0=0
do i=1 to race.0
  player.i.0=0
end
do i=0 to 63
  do j=0 to 63
    pmap.i.j=0
    planetmap.i.j=0
  end
end

mapfile=rootdir||"maps/"||turn||".map"
say "Reading" mapfile
do while lines(mapfile)>0
  l=linein(mapfile)
  parse var l tok ":" nam "~" xx "," yy
  if tok="nebula" then
  do
    nebula.0=nebula.0+1
    en=nebula.0
    nebula.en=nam
    nebula.en.x=xx
    nebula.en.y=yy
  end
  if tok="planet" then
  do
    parse var nam nam2 "@" cls "@" typ
    parse var yy yy2 "!" own
    planet.0=planet.0+1
    en=planet.0
    planet.en=nam2
    planet.en.class=cls
    planet.en.type=typ
    planet.en.x=xx
    planet.en.y=yy2
    planet.en.owner=own
    planetmap.xx.yy2=1 /* don't care who owns it, just mark that it's there */
  end
  do i=1 to race.0
    if tok="player"||i then
    do
      player.i.0=player.i.0+1
      en=player.i.0
      player.i.en=nam
      player.i.en.x=xx
      player.i.en.y=yy
    end
  end
end

newmapfile=rootdir||"maps/"||newturn||".map"
"rm" newmapfile

say "Writing" newmapfile

call lineout newmapfile,"[IbexMap]"

/* nothing special to do with nebulas */
do i=1 to nebula.0
  l="nebula:"||nebula.i||"~"||nebula.i.x||","||nebula.i.y
  call lineout newmapfile,l
end
call lineout newmapfile,""

do i=1 to race.0
  do j=0 to 63
    do k=0 to 63
      vis.i.j.k=0
    end
  end
end

/* set vis for planets */
do i=1 to planet.0
  if planet.i.type=2 then
  do
    px=planet.i.x
    py=planet.i.y
    do j=1 to race.0
      call setvis j,px,py,1
      do k=1 to 6
        w=hexdirection(px,py,k)
        parse var w wx "/" wy
        call setvis j,wx,wy,1
      end
    end
  end
end

/* move each piece */
do i=1 to race.0
  pmoves.=""
  pmoves.0=0
  call readturn i
  do j=1 to player.i.0
    
    en=player.i.j
    
    if pmoves.en <> "" then
    do
      dest=word(pmoves.en.steps,pmoves.en.stepcount)
      parse var dest "[" destx "," desty "]"
      player.i.j.x=destx
      player.i.j.y=desty
      /* piece moved, add destination to pmap */
      pmap.destx.desty=i
      
      do k=1 to pmoves.en.stepcount
        dest=word(pmoves.en.steps,k)
        parse var dest "[" destx "," desty "]"
        call setvis i,destx,desty,3
        do l=1 to 6
          w=hexdirection(destx,desty,l)
          parse var w wx "/" wy
          call setvis i,wx,wy,1
        end
      end
        
    end
    else
    do /* piece did not move, so add its location to pmap */
      destx=player.i.j.x
      desty=player.i.j.y
      pmap.destx.desty=i
    end

    l="player"||i||":"||player.i.j||"~"||player.i.j.x||","||player.i.j.y
    call lineout newmapfile,l
    
    call setvis i,player.i.j.x,player.i.j.y,7
    do k=1 to 6
      w=hexdirection(player.i.j.x,player.i.j.y,k)
      parse var w wx "/" wy
      call setvis i,wx,wy,5
      do l=1 to 6
        w2=hexdirection(wx,wy,l)
        parse var w2 w2x "/" w2y
        call setvis i,w2x,w2y,3
      end
    end
  end
  call lineout newmapfile,""
  
  outg=rootdir||"outgoing/"||i||"."||zpad(newturn)
  "rm" outg
  call lineout outg,"[IbexOut]"
  newvis="vismap="
  do vx=0 to 63
    do vy=0 to 63
      newvis=newvis||vis.i.vx.vy
    end
  end
  call lineout outg,newvis
end

/* read in previous production */
do i=1 to race.0
  res.i.natrium=0
  res.i.dysprosium=0
  res.i.pentium=0
  res.i.smithore=0
  res.i.money=0

  prodf=rootdir||"players/"||i||"."||zpad(turn)
  do while lines(prodf)>0
    l=linein(prodf)
    parse var l lres "=" lamt
    if lres="natrium" then res.i.natrium=lamt
    if lres="dysprosium" then res.i.dysprosium=lamt
    if lres="pentium" then res.i.pentium=lamt
    if lres="smithore" then res.i.smithore=lamt
    if lres="money" then res.i.money=lamt
  end
end

/* figure out who controls planets */
do i=1 to planet.0
  px=planet.i.x
  py=planet.i.y
  po=planet.i.owner
  
  do j=0 to 4
    sc.j=0 /* sc means surrounding count */
  end
  do j=1 to 6
    w=hexdirection(px,py,j)
    parse var w wx "/" wy
    pl=pmap.wx.wy
    sc.pl=sc.pl+1
  end
  /* find who has the most */
  maxpl=po /* the previous owner */
  maxc=0
  do j=1 to 4
    if sc.j>maxc then
    do
      maxpl=j
      maxc=sc.j
    end
  end
  planet.i.owner=maxpl
end

do i=1 to planet.0
  l="planet:"||planet.i||"@"||planet.i.class||"@"||planet.i.type||"~"||planet.i.x||","||planet.i.y||"!"||planet.i.owner
  call lineout newmapfile,l
end
call lineout newmapfile,""

/* figure out production */
do i=1 to race.0
  outg =rootdir||"outgoing/"||i||"."||zpad(newturn)
  pec=0 /* production event counter */
  planetcount.i=0
  do j=1 to planet.0
    if planet.j.owner=i then
    do
      pec=pec+1
      planetcount.i=planetcount.i+1
      pfix="production"||pec||"="
      select
        when planet.j.type=1 then /* blue, natrium */
          do
            call lineout outg,pfix||planet.j||":Natrium:10"
            res.i.natrium=res.i.natrium+10
          end
        when planet.j.type=2 then /* yellow, dysprosium */
          do
            call lineout outg,pfix||planet.j||":Dysprosium:2"
            res.i.dysprosium=res.i.dysprosium+2
          end
        when planet.j.type=3 then /* red, pentium */
          do
            call lineout outg,pfix||planet.j||":Pentium:5"
            res.i.pentium=res.i.pentium+5
          end
        when planet.j.type=4 then /* orange, smithore */
          do
            call lineout outg,pfix||planet.j||":Smithore:10"
            res.i.smithore=res.i.smithore+10
          end
        when planet.j.type=5 then /* cyan, money */
          do
            call lineout outg,pfix||planet.j||":Money:1000"
            res.i.money=res.i.money+1000
          end
        when planet.j.type=6 then /* gray, new ship */
          do
            /* see if there's a place to put the new ship */
            newship=""
            do k=1 to 6
              w=hexdirection(planet.j.x,planet.j.y,k)
              parse var w wx "/" wy
              if (pmap.wx.wy=0) & (planetmap.wx.wy=0) then newship=w
            end
            if newship ^= "" then
            do
               thename=randname()
               parse var newship wx "/" wy
               call lineout outg,pfix||planet.j||":Ship:"||thename
               call lineout newmapfile,"player"||i||":"||thename||"~"||wx||","||wy
               player.i.0=player.i.0+1 /* update count, but not the rest of the player. structure */
               /* TODO set visibility for radius around new ship */
            end
            else
            do /* no room! let player know */
               call lineout outg,pfix||planet.j||":NoShip:"||"NoShip"
            end
          end
        otherwise
          nop
      end /* select */
    end /* if owner */
  end /* for planet */

  say "closing" outg stream(outg,"C","CLOSE")
  "chmod 666" outg
  
  prodf=rootdir||"players/"||i||"."||zpad(newturn)
  call lineout prodf,"[IbexRes]"
  call lineout prodf,"natrium="||res.i.natrium
  call lineout prodf,"dysprosium="||res.i.dysprosium
  call lineout prodf,"pentium="||res.i.pentium
  call lineout prodf,"smithore="||res.i.smithore
  call lineout prodf,"money="||res.i.money
  
  say "closing" prodf stream(prodf,"C","CLOSE")
  "chmod 666" prodf
  
end /* for race */
    
say "closing" newmapfile stream(newmapfile,"C","CLOSE")  
"chmod 666" newmapfile

turnfile=rootdir||"turn"
say "closing" turnfile stream(turnfile,"C","CLOSE")
call lineout turnfile,newturn,1
say "closing (again)" turnfile stream(turnfile,"C","CLOSE")
"chmod 666" turnfile

/* update turntime file for website */
call lineout turntime,date("NORMAL",date("BASE")+1,"BASE") "00:01:00",1

/* TODO output some stats for home page */
"rm" statsfile
"rm" statsfile2
"rm" statsdbfile

call lineout statsdbfile,"id|desc|race|qty|uom"

plmax=0
plwho=""
do i=1 to race.0
  if player.i.0>=plmax then
  do
    plwho=race.i
    plmax=player.i.0
  end  
end
call lineout statsfile,"<b>Mightiest Military</b><br/>"||plwho||", "||plmax "ships<hr>"
call lineout statsfile2,'<p class="statitem"><b>Mightiest Military</b><br/>'||plwho||', '||plmax 'ships</p>'
call lineout statsdbfile,"might|Mightiest Military|"||plwho||"|"||plmax||"|ships"

plmax=0
plwho=""
do i=1 to race.0
  if planetcount.i>plmax then
  do
    plwho=race.i
    plmax=planetcount.i
  end
end
call lineout statsfile,"<b>Expandedest Empire</b><br/>"||plwho||", "||plmax "systems<hr>"
call lineout statsfile2,'<p class="statitem"><b>Expandedest Empire</b><br/>'||plwho||', '||plmax 'systems</p>'
call lineout statsdbfile,"expand|Expandedest Empire|"||plwho||"|"||plmax||"|systems"

plmax=0
plwho=""
do i=1 to race.0
  if res.i.money>plmax then
  do
    plwho=race.i
    plmax=res.i.money
  end
end
call lineout statsfile,"<b>Wealthiest Weasel</b><br>"||plwho||", "||plmax "money units<hr>"
call lineout statsfile2,'<p class="statitem"><b>Wealthiest Weasel</b><br>'||plwho||', '||plmax 'money units</p>'
call lineout statsdbfile,"wealth|Wealthiest Weasel|"||plwho||"|"||plmax||"|money units"

"chmod 666" statsfile
"chmod 666" statsfile2
"chmod 666" statsdbfile

say "Finished!"
say date("U") time()
exit
/* ********************************** */


readturn:
arg thepl
  thef=rootdir||"incoming/"||thepl||"."||zpad(turn)
  do while lines(thef)>0
    l=linein(thef)
    if left(l,9)=="Movecount" then
    do
      parse var l "Movecount=" movecount
      pmoves.0=movecount
    end
    if left(l,5)=="Move " then
    do
      parse var l "Move " m "=" n "~" pstepcount "~" psteps
      pmoves.n=m
      pmoves.n.stepcount=pstepcount
      pmoves.n.steps=psteps
    end
    if left(l,6)=="Vismap" then
    do
      parse var l "Vismap=" vismap
      do ii=1 to length(vismap)
        vx=(ii-1) %  64
        vy=(ii-1) // 64
        v=substr(vismap,ii,1)
        if v>0 then v=v-1 /* -1 to decrease vis each turn */
        call setvis thepl,vx,vy,v
      end
    end
  end
return

/* ********************************** */
HexDirection:
procedure
arg u,v,dir

If dir=1 Then /* ne */  r=pack(u+(v // 2),v-1)If dir=2 Then /* e */  r=pack(u+1,v)If dir=3 Then /* se */  r=pack(u+(v // 2),v+1)If dir=4 Then /* sw */  r=pack(u-((v+1) // 2),v+1)If dir=5 Then /* w */  r=pack(u-1,v)If dir=6 Then /* nw */  r=pack(u-((v+1) // 2),v-1)return r

pack:arg x,y
return x||"/"||y

setvis:
arg svi,svx,svy,newvis
oldvis=vis.svi.svx.svy
if newvis>>oldvis then vis.svi.svx.svy=newvis
return
/* ********************************** */

randname:
procedure
f="QZXWKR"
fl=length(f)
o="oiaueyfv"
ol=length(o)

r=substr(f,random(1,fl),1)

l=random(3,8)
do i=1 to l
  r=r||substr(o,random(1,ol),1)
end

return r



/* ********************************** */
readall:procedure
parse arg thef
r=""
n="0A"x
do while lines(thef)>0
  l=linein(thef)
  r=r||l||n
end
return r

zpad:procedure
parse arg a
r=right("00000"||a,3)
return r
