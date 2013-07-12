#!/kunden/homepages/20/d100701421/htdocs/hexibex/engine/rexxgd
/* */

DRAW=1

/*
say "Content-Type: image/png"
say
*/

say
say "---------------------------------"
say "Starting at" date("U") time()

Call RxFuncAdd 'GdLoadFuncs', 'rexxgd', 'GdLoadFuncs'
Call GdLoadFuncs

thishost="hostname"()
if pos("mini",thishost)>0 then
  rootdir="/Users/jeff/Sites/ibex/"
else
  rootdir="/kunden/homepages/20/d100701421/htdocs/hexibex/engine/"

turn=readall(rootdir||"turn")
turn=changestr("0A"x,turn,"")
turn=changestr("0D"x,turn,"")

race.0=4
race.1="Eye Guys";race.1.fn="eyeguy1.png"
race.2="Ophidians";race.2.fn="snakeguy1.png"
race.3="Teal Magi";race.3.fn="mageguy1.png"
race.4="Purple Dragons";race.4.fn="purpledragonguy1.png"

do pnum=1 to race.0

  do sss=16 to 48 by 2
  
  say time() "Pnum="||pnum " size="||sss

hexs=sss
sin30=0.5
cos30=0.86602540378
hexh=trunc(sin30*hexs)
hexr=trunc(cos30*hexs)
hexb=hexs+2*hexh
hexa=2*hexr
hexm=hexh/hexr
hex=gdimagecreate(hexa+2,hexb+2)
hbg=gdimagecolorallocate(hex,100,100,100)
gdimagecolortransparent(hex,hbg)
hgr=gdimagecolorallocate(hex,0,64,0)
gdimagesetthickness(hex,1)
gdimagesetantialiased(hex,hgr) /* doesn't seem to work... */
gdimageLine(hex,hexr,0,hexa-1,hexh,"GDANTIALIASED")
gdimageLine(hex,hexa-1,hexh,hexa-1,hexh+hexs,"GDANTIALIASED")
gdimageLine(hex,hexa-1,hexh+hexs,hexr,hexb-1,"GDANTIALIASED")

maxhx=25;maxhy=20
w=(hexa+2)*(maxhx+2);h=(hexb+2)*(maxhy+2)
nw=w;nh=h
map=gdimagecreatetruecolor(w+1,h+1)
bg=gdimagecolorallocate(map,1,1,1)

do 1000
  c=random(128,255)
  cc=gdimagecolorallocate(map,c,c,c)
  gdimagesetpixel(map,random(0,w),random(0,h),cc)
end

do u=0 to maxhx
  do v=0 to maxhy
    call drawahex hex,u,v
  end
end

planet.0=0
nebula.0=0
do i=1 to race.0
  player.i.0=0
end

mapfile=rootdir||"maps/"||turn||".map"
say "close=" stream(mapfile,"Command","CLOSE")
do while lines(mapfile)>0
  l=linein(mapfile)
  parse var l tok ":" nam "~" xx "," yy "!" oo
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
    planet.0=planet.0+1
    en=planet.0
    planet.en=nam2
    planet.en.class=cls
    planet.en.type=typ
    planet.en.x=xx
    planet.en.y=yy
    planet.en.owner=oo
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

neb=makeneb()
do i=1 to nebula.0
	call drawapiece neb,nebula.i.x,nebula.i.y
end

do i=1 to planet.0
	planetimg.i=makeplanet(planet.i.type,planet.i)
	call drawapiece planetimg.i,planet.i.x,planet.i.y
end

/*
neb=makeneb()
call drawapiece neb,2,1
call drawapiece neb,2,2

planet1=makeplanet(1,"Jupiter")
planet2=makeplanet(2,"Venus")
call drawapiece planet1,3,2
call drawapiece planet2,3,3
*/

do i=1 to race.0
  pimg.i=gdimagecreatefrompng(rootdir||"images/"||race.i.fn)
  trans=gdimagecolorexact(pimg.i,255,255,255)
  call gdimagecolortransparent pimg.i,trans
end

do i=1 to player.pnum.0
	call drawapiece pimg.pnum,player.pnum.i.x,player.pnum.i.y
end

/*say rootdir||"images/"||race.pnum.fn
say "pimg=" pimg
call drawapiece pimg,1,1
*/

newmap=gdimagecreatetruecolor(nw+1,nh+1)
call gdimagecopy newmap,map,0,0,trunc(hexa*2)-2,trunc(hexb*1.5)-2,nw,nh

outf=rootdir||"lmaps/map-"||pnum||"-"||sss||".png"
if DRAW=1 then call gdimagepng newmap,outf



do i=1 to race.0
  call gdimagedestroy pimg.i
end
do i=1 to planet.0
  call gdimagedestroy planetimg.i
end
call gdimagedestroy neb

call gdimagedestroy map
call gdimagedestroy newmap
call gdimagedestroy hex

end
end
say "Finished" time()
exit

/* *********************************** */

makeneb:
  ir=gdimagecreate(hexa,hexb)
  cb=gdimagecolorallocate(ir,0,0,0)
  call gdimagecolortransparent ir,cb
  c.1=gdimagecolorallocate(ir,222,55,242)
  c.2=gdimagecolorallocate(ir,0,60,222)
  c.3=gdimagecolorallocate(ir,111,40,255)
  do ix=1 to 3
  do 100
    call gdimagesetpixel ir,random(0,hexa%4)+random(0,hexa%4)+random(0,hexa%4)+random(0,hexa%4),random(0,hexb%4)+random(0,hexb%4)+random(0,hexb%4)+random(0,hexb%4),c.ix
  end
  end
return ir


makeplanet:parse arg n,desc
  padx=64
  pl=gdimagecreate(hexa+padx+padx,hexb)
  pb=gdimagecolorallocate(pl,0,0,0)
  call gdimagecolortransparent pl,pb
  txtcol=gdimagecolorallocate(pl,240,222,31)
  txtfont="GDFONTSMALL"
  tw=gdfontgetwidth(txtfont)
  th=gdfontgetheight(txtfont)
  ttw=tw*length(desc)
  tth=th
  do imp=10 to 1 by -1
    if n=1 then
      cc=gdimagecolorallocate(pl,(11-imp)*8,0,(11-imp)*12+120)
    else
      cc=gdimagecolorallocate(pl,(11-imp)*8,(11-imp)*12+120,0)
    call gdimagefilledellipse pl,hexa%2-1+padx,hexb%2-1,imp,imp,cc
  end
  call gdimagestring pl,txtfont,hexa%2-ttw%2+padx,hexb%2+4,desc,txtcol
return pl

/* *********************************** */
drawahex:arg id,hxx,hyy
    xpx=hxx*hexa+(hyy // 2)*hexr
    ypx=hyy*(hexh+hexs)
    call drawimage id,xpx,ypx
return

drawapiece:arg idd,hxx,hyy
    hxx=hxx+2;hyy=hyy+2
    xpx=hxx*hexa+(hyy // 2)*hexr
    ypx=hyy*(hexh+hexs)
    iw=gdimagegetwidth(idd)
    ih=gdimagegetheight(idd)
    /*say xpx ypx iw ih xpx+(hexa%2)-(iw%2) ypx+(hexb%2)-(ih%2) */
    call drawimage idd,xpx+(hexa%2)-(iw%2),ypx+(hexb%2)-(ih%2)
return


drawimage:arg iddd,ix,iy
  gdimagecopy(map,iddd,ix,iy,0,0,gdimagegetwidth(iddd)-1,gdimagegetheight(iddd)-1)
return


readall:procedure
parse arg thef
r=""
n="0A"x
do while lines(thef)>0
  l=linein(thef)
  r=r||l||n
end
return r
