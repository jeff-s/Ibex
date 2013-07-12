AppTitle "Ibex 0021 - 11/24/06"

; version history
;
; 0010 - 10/12/05 - add custom guy type; multiple guys; pathfinding w/o shift key
; 0011 - 10/14/05 - redo pathfinding with packed int thingy
; 0012 - 10/15/05 - unified map data structure, more guys
; 0013 - 10/23/05 - keep track of movement points, use red hex path thingy
; 0014 - 11/02/05 - add movement structure, movement queue window, unified color representation
; 0015 - 11/13/05 - talk to server!
; 0016 - 11/29/05 - post to server
; 0017 - 01/04/06 - add error checking when loading media and reading config file
; 0018 - 11/08/06 - get map from server
; 0019 - 11/14/06 - territory borders, stains
; 0020 - 11/17/06 - fix disputed territory bugs, tidy up UI, embiggen map, load other players' info from server, add binary dl's to geturl, get new bgm from server
; 0021 - 11/24/06 - use If-Modified-Since http header when fetching media assets; add visibility
;
; to-do's
; x consistent map data structure representation (nebulae, eyeguys and planets)
; x structure to aggregate pieces and their moves
; x add movement points back after delete
; x figure out how to unselect entire movement list (by learning to spell, apparently)
;   implement movenent as TYPE instead of ugly array
;   updatemap/sendmessage every time through event loop?
;   unifiedcolor brightener/darkener
; - load map from text file
; x load map from server
; x load other players' names/races from server
;   load territory from server
;   add timer check to pathfinding in case it takes too long
; x add check to not re-fetch BGM if it has already been downloaded
;   deny media files based on user agent
; x get visibility from server
;   make an F-key toggle a global-ignore-visibility flag

Global IBEXVER$="0021"

Type Entity
	Field EType ;1=player, 2=planet, 3=nebula
	Field Owner
	Field img
	Field x
	Field y
	Field name$
	Field class$ ;some other descriptive field
	Field classtype ;integer for some other descriptive field
	Field movecost
	Field frame
End Type
Const EPLAYER=1,EPLANET=2,ENEBULA=3

Type Star
	Field x
	Field y
	Field c
	Field hx
	Field hy
End Type

Dim PathStep(63)
Global PathCount=-1

Dim OpenList(1023)
Global OpenListCount=0
Dim ClosedList(1023)
Global ClosedListCount=0

Dim CMoveName$(20)
Dim CMoveStep(20,20)
global CMoveIndex=0

Const PACKBASE=64 ;factor when combining hx/hy pairs
Dim NodeParent(PACKBASE*PACKBASE+PACKBASE)
Dim NodeG(PACKBASE*PACKBASE+PACKBASE)
Dim NodeH(PACKBASE*PACKBASE+PACKBASE)
Dim map_cost(64,64)
Dim map_terr(64,64)
Dim map_terr_new(64,64)
Dim map_vis(64,64)
For i=0 To 63
 For j=0 To 63
  map_terr(i,j)=0
  map_terr_new(i,j)=0
  map_vis(i,j)=0
 Next
Next

Global showhex=False
Global ignorevis=False

Global sf=0 ;frame indicator thingy

Dim bgmfn$(10)
Dim bgmn$(10)
Dim rbgm(10)

Global PMoveMax;=20 ;movement points
Global PNum;=3 ;our player number (for entity collection)
Global TITLE$
Global DYNASTY$
Global FIRSTNAME$
Global server$
Global turnnum

;how to do a post
;aaa$= pcmd("thecommand!","this is content!")

;;;connect to server
fc=FileType("ibexconfig")
If fc<>1 Then
	Print 
	Print "         ERROR: Config file not found!"
	Print
	Print " Be sure the ibexconfig file that you downloaded "
	Print " is in the same directory as the program.  If "
	Print " you've lost the file, you can download a copy "
	Print " from the website.  Unfortunately, I can't tell "
	Print " you which website, because that information is "
	Print " in the config file.  Chicken. Egg. Repeat."
	Print
	Print "            Press a key to exit..."
	WaitKey
	End
End If
Print
Print " Welcome to Ibex. Version: "+ibexver$
Print "--------------------------------"
Print
Print " Reading config file..."
configfile=ReadFile("ibexconfig")
l$=ReadLine(configfile)
If l$<>"[Ibex]" Then
   Print "Bad config file"
   WaitKey
   End
End If
While Not Eof(configfile)
	l$=ReadLine(configfile)
	lp=Instr(l$,"=")
	ll$=Left(l$,lp-1)
	lr$=Mid(l$,lp+1)
	If ll$="server" Then server$=lr$
	If ll$="player" Then pnum=Int(lr$)
Wend
CloseFile(configfile)

Print " Pinging " +server
Print
resp$= scmd("PING")
Print ">"+stripcrlf(resp)
Print
If Left(resp,3)<>"ACK" Then
	Print "ERROR: of some sort..... ending..."
	;Stop
	WaitKey
	End
End If

Print " Getting turn..."
resp$=scmd("GETTURN")
pmovemax=Int(parseturn(resp$,"moves"))
motd$=parseturn(resp$,"motd")
title=parseturn(resp$,"title")
dynasty=parseturn(resp$,"dynasty")
firstname=parseturn(resp$,"firstname")
turnnum=parseturn(resp$,"turn")
Dim players$(4,4)
For i=1 To 4
	players(i,1)=parseturn(resp$,"race"+i)
	players(i,2)=parseturn(resp$,"dynasty"+i)
	players(i,3)=parseturn(resp$,"firstname"+i)
	players(i,4)=parseturn(resp$,"title"+i)
Next
vis$=parseturn(resp$,"vismap")
If vis$<>"" Then
	For i=1 To Len(vis$)
		j=(i-1) / 64
		k=(i-1) Mod 64
		map_vis(j,k)=Int(Mid$(vis$,i,1))
	Next
End If

Print " Getting today's background music..."
bgfn$=parseturn(resp$,"bgm")
m$=geturl(Left(server,Instr(server,"gw.rexx")-1)+"media/"+bgfn$,80,True,True,"media/"+bgfn$)

Print " Getting map..."
resp$=scmd("GETMAP")

Print
Print "--------------------------------"
Print
Print " OK, got it all.  Press a key to start."
FlushKeys
WaitKey

Global Pmovetot=pmovemax

Global pmover$;the guy to move
Global selmover$ ;the guy selected from list

Dim PathColors(10)
MakePathColors()

Const BGMGRP=1

Graphics3D 1024,768;,16;,2
;Graphics 1400,1050,32
SetBuffer BackBuffer()
hidepointer
;;;movemouse 10,10 ;make mac-like
MoveMouse 512,384
Cls
Flip
SeedRnd MilliSecs()

bgm=LoadResource("media/"+bgfn$)
bgmv=75
ChannelVolume bgm,Float(Float(bgmv)/Float(100))

include "blitzui.bb"
include "messagebox.bb"

Global fntTahoma=LoadFont("Tahoma",12,False)

FileExists("media/MidnightIbex.cs")
LoadColourScheme ("media/MidnightIbex.cs")

ibexicon=LoadResource("media/ibexicon.png")
Global bgimg=LoadResource("media/bg.png")
mouseimg=LoadResource("media/jmouse1.png")
MaskImage mouseimg,0,0,0
sonicring=LoadResource("media/blip.wav")
thup=LoadResource("media/thup.wav")
bzzt=LoadResource("media/bzzt.wav")
bweep=LoadResource("media/mtruc.wav")
coolendingsound=LoadResource("media/Cool Ending Sound.wav")

Global mapimg=CreateImage(830,512)
Global tmpimg=CreateImage(960,768)

Global planet1img,planet2img
Global nebulaimg
Dim shipimg(4)
shipimg(1)=LoadAnimResource("media/eyeguy.png",26,37,0,2)
shipimg(2)=LoadAnimResource("media/snakeguy.png",26,37,0,2)
shipimg(3)=LoadAnimResource("media/mageguy.png",26,37,0,2)
shipimg(4)=LoadAnimResource("media/purpledragonguy.png",26,37,0,2)
MaskImage shipimg(1),255,0,255
MidHandle shipimg(1)
MaskImage shipimg(2),255,0,255
MidHandle shipimg(2)
MaskImage shipimg(3),255,0,255
MidHandle shipimg(3)
MaskImage shipimg(4),255,0,255
MidHandle shipimg(4)

;set stains
Global hexstain
Dim stain(5)
stain(1)=makecol(90,80,90) ;gray eyeguys
stain(2)=makecol(0,70,70) ;green ophids
stain(3)=makecol(0,80,80) ;teal magi
stain(4)=makecol(70,0,100) ;purple dragons
stain(5)=makecol(135,61,0) ;orange newly-expanded territory
Dim stainb(5)
stainb(1)=makecol(180,180,200) ;gray eyeguys
stainb(2)=makecol(0,120,0) ;green ophids
stainb(3)=makecol(0,180,170) ;teal magi
stainb(4)=makecol(97,0,157) ;purple dragons
stainb(5)=makecol(255,89,0) ;orange new
Global hexborder
Global hexdisputed

;specks for stars
For i=1 To 4000
	s.star=New star
	s\c=Rand(140,250)
	s\x=Rand(0,830)
	s\y=Rand(0,830)
Next

Global hexs=18
ReDoGFX()

Global hexh
Global hexr
Global hexb
Global hexa
Global hexm#
Global heximg
Global hexcursor
Global hexanchor
Global hexpath,hexpathred
Global hexanchorx=-1
Global hexanchory=-1
Global framen=0
Global framei=1

;parse the map
; but first change any EOLs to chr(10)
r$=""
For i=1 To Len(resp$)
  m$=Mid(resp$,i,1)
  If m$=Chr$(13) Then m$=""
  r$=r$+m$
Next
While r$<>""
	p1=Instr(r$,Chr$(10))
	l$=Left$(r$,p1-1)
	r$=Mid$(r$,p1+1)
	If Len(l$)>0 And l$<>"[IbexMap]" Then
		p2=Instr(l$,":")
		p3=Instr(l$,"~")
		p4=Instr(l$,",",p3)
		map_e$=Left(l$,p2-1)
		map_n$=Mid(l$,p2+1,p3-p2-1)
		map_ex$=Mid(l$,p3+1,p4-p3-1)
		map_ey$=Mid(l$,p4+1)
		If map_e$="planet" Then
			p5=Instr(map_n$,"@")
			p6=Instr(map_n$,"@",p5+1)
			map_p1$=Mid(map_n$,p5+1,p6-p5-1)
			map_p2$=Mid(map_n$,p6+1)
			map_n$=Left(map_n$,p5-1)
		End If
		If Left(map_e$,6)="player" Then
			map_pnum=Int(Mid(map_e$,7))
		End If
		
		e.entity=New entity
		
		If map_e$="planet" Then
			e\etype=EPLANET
			e\owner=0
			If map_p2$="1" Then e\img=planet1img
			If map_p2$="2" Then e\img=planet2img
			e\classtype=Int(map_p2$)
			e\x=Int(map_ex$)
			e\y=Int(map_ey$)
			e\name$=map_n$
			e\movecost=1
			e\class$=map_p1$
		End If
		
		If map_e$="nebula" Then
			e\etype=ENEBULA
			e\owner=0
			e\img=nebulaimg
			e\x=Int(map_ex$)
			e\y=Int(map_ey$)
			e\name$=map_n$
			e\movecost=50
			e\class$="Nebula"
		End If
		
		If Left(map_e$,6)="player" Then
			e\etype=EPLAYER
			e\owner=map_pnum
			e\img=shipimg(map_pnum)
			e\name$=map_n$
			e\x=Int(map_ex$)
			e\y=Int(map_ey$)
			e\movecost=50
			For i=1 To 6
			  j=hexdirection(e\x,e\y,i)
			  If packx(j)<PACKBASE And packy(j)<PACKBASE Then
				If map_terr(packx(j),packy(j))=0 Or map_terr(packx(j),packy(j))=e\owner Then
					map_terr(packx(j),packy(j))=e\owner
				Else
					map_terr(packx(j),packy(j))=99 ;disputed
				End If
				;If e\owner=pnum Then map_vis(packx(j),packy(j))=1 ;get from server now
			  End If
			Next

		End If
		
	End If
Wend

For e.entity =Each entity
	map_terr(e\x,e\y)=e\owner
	;If e\owner=pnum Then map_vis(e\x,e\y)=1 ;get from server now
Next

LoadMapCosts()	

SetBuffer BackBuffer()

global borderc=0
global borderi=1
Flip

Initialise() ;BlitzUI

;Delay 2000 ;for dramatic effect


;MessageBox("Welcome to Ibex","Ibex")

;Window
winMap = Window( 10, 10, 830+15, 512+35, "Map", "0", 1, 0, 1, 1 )
imgMap=ImageBox( 5,5,830,512,mapimg,0,0,0)

;Window
winStatus = Window( 870, 10, 150, 450, "Status", "0", 1, 0, 1, 1 )
lblStatus= Label( 5,5,"",0)
imgstatusthis=Imagebox(110,10,32,32,0,"Outer","Center","Middle")

;Movement Queue Window
winQ = Window( 815, 570, 200, 190, "Movement Queue", "0", 1, 0, 1, 0 )
btnQDel = Button( 100, 130, 81, 25, "Delete Selected", "0", 1, 0, 0 )
lstMoves = ListBox( 5, 6, 185, 110, 20, 20, 10, 0 )
lblMoves = Label(5,150,"Movement: 0 / "+pmovemax)
settooltip (btnQDel,"Remove selected piece from the queue" )

;Window
winMain = Window( 550,600 , 200, 160, "Ibex", ibexicon, 1, 0, 1, 0 )
btnComm = Button( 20, 25, 150, 20, "Play Sound", "0", 1, 0, 0 )
SetToolTip( btnComm, "Plays the 'Sonic Ring' Sound." )
btnQuit = Button( 20, 50, 150, 20, "End Turn", "0", 1, 0, 0 )
btnMap = Button( 20, 75, 150, 20, "View Map", "0", 1, 0, 0 )
settooltip (btnMap,"Re-opens the 'Map' window if you've closed it accidentally" )
btnHexUp=Button(20,100,20,20,"+");,0,1,0,0)
btnHexDown=Button(100,100,20,20,"-");,0,1,0,0)
lblHexSize=Label(60,100,hexs)
grp001 = GroupBox( 11, 15, 180, 115, "Main Command Module" )

;Instr Window
winInstr=Window(10,600,525,150,"Instructions",0,1,0,1,0)
lblInstr=Label(5,5,"",0)

;jukebox Window
winJuke=Window(22,560,200,50,"Ibex Jukebox (Ibox)",0,1,0,1,1)
slVol=Slider(5,5,100,12,bgmv,0,100,"Integer","Horizontal","0,0,64","0,0,255")
lblVol=Label(110,5,"Volume: "+RSet(bgmv,3))

;Windows must only be set as modal after
;the interface has been created.
;SendMessage( winMain, "WM_SETMODAL" )

wxo=7:wyo=29


;sendmessage(imgthisterr,"IM_SETIMAGE",darktile)
n$=Chr(10)
b$=Chr(149)
sendmessage(lblInstr,"LM_SETTEXT",motd$+n$+n$+b+b+"Movement points remaining: "+pmovemax+n$+n$+"Select a piece to move.")

UpdateMap()
sendmessage(imgmap,"im_setimage",mapimg)

MessageBox("You begin a new day in the Ibex dimension.  You have "+pmovemax+" movement points to spend."+n$+n$+motd$,title + " " + firstname + " " + dynasty +" ... turn "+ turnnum)

Repeat

  DrawBG()

  ;bgmv=bgmv*0.999
  ;If bgmv<.05 Then StopChannel bgm

  UpdateGUI()
	
	;sendmessage(lblstatus,"lm_settext","Mouse:" +Chr(10)+"X="+app\mx+Chr(10)+"Y="+app\my+Chr(10)+Chr(10)+"Map Win:" +Chr(10)+"X="+mapwinx+", Y="+mapwiny+Chr(10)+Chr(10)+"mcx="+mcx+", mcy="+mcy+Chr(10)+"Terr="+terrlookup(terr))
	
	Select app\Event
		Case EVENT_WINDOW
		;DebugLog app\windowevent
			Select app\WindowEvent
			
			End Select
		Case EVENT_MENU
			Select app\MenuEvent
			End Select
		Case EVENT_GADGET
			Select app\GadgetEvent
				Case btnQuit
				  PlaySound thup
				  If messagebox("Are you sure?","Confirm Quit",2)="Yes" Then
					t$=BuildTurn()
					PlaySound coolendingsound
					;messagebox(t$,"Sending to server...",1)
					resp$= pcmd("POSTTURN",t$)
					messagebox(resp$,"Response from server...",1)
					app\quit=True
				  End If
				Case btnMap
				  SendMessage(winMap,"WM_OPEN")
				  SendMessage(winMap,"WM_MAXIMIZE")
				  SendMessage(winMap,"WM_BRINGTOFRONT")
				case btnComm
					;sendmessage(lblmotd,"LM_SETTEXT",netgetmotd())
					PlaySound sonicring
					t$=""
					for i=1 to CMoveIndex
					  t$=Chr(10)+t$+Str$(i)+". "+cmovename$(cmoveindex)+" >> "
					  for j=1 to cmovestep(cmoveindex,0)
					    t$=t$+"["+packx(cmovestep(i,j))+","+packy(cmovestep(i,j))+"] "
					  next
					next
					sendmessage(lblinstr,"LM_settext",t$)
					;UpdateMap()
					;sendmessage(imgMap,"IM_SETIMAGE",mapimg)
				Case lstMoves
					thel$=app\gadgeteventdata
					If Len(thel)>0 Then
						PlaySound thup
						p1=Instr(thel," / ")
						p2=Instr(thel," / ",p1+1)
						selmover=Left$(thel,p1-1)
						For e.entity = Each entity
							If e\name=selmover Then
								pathcount=-1
								hexanchorx=-1;e\x
								hexanchory=-1;e\y
							End If
						Next
					Else
						selmover=""
					End If					
					updatemap()
					sendmessage(imgmap,"im_setimage",mapimg)
				Case btnQDel
					qmover= sendmessage(lstMoves,"LM_GETINDEX")
					If qmover=0 Then
						PlaySound bzzt
					Else
						PlaySound bweep
						thel$=sendmessage(lstmoves,"lm_gettext")
						p1=Instr(thel," / ")
						p2=Instr(thel," / ",p1+1)
						selmover=Left$(thel,p1-1)
						mp=Int(Mid(thel,p2+3))
						For i=1 To cmoveindex
							If cmovename(i)=selmover Then
								For j=i To cmoveindex-1
									cmovename(j)=cmovename(j+1)
									For k=0 To 19 
										cmovestep(j,k)=cmovestep(j+1,k)
									Next
								Next
								cmoveindex=cmoveindex-1
								For j=0 To 63
								  For k=0 To 63
								    If map_terr_new(j,k)=i Then map_terr_new(j,k)=0
								    If map_terr_new(j,k)>i Then map_terr_new(j,k)=map_terr_new(j,k)-1
								  Next
								Next
							End If
						Next
						sendmessage(lstmoves,"lm_deleteitem",qmover,True)
						sendmessage(lstmoves,"lm_setindex",0)
						pmovemax=pmovemax+mp
						updatemap()
						sendmessage(imgmap,"im_setimage",mapimg)
						sendmessage(lblmoves,"lm_settext","Movement: " +Str$(pmovetot-pmovemax)+" / " +pmovetot)
					End If
					
				Case imgmap
					mwi=Instr(app\gadgeteventdata,"/")
					mwx=Left(app\gadgeteventdata,mwi-1)
					mwy=Mid(app\gadgeteventdata,mwi+1)
					mhx=packx(xytohexxy(mwx,mwy))
					mhy=packy(xytohexxy(mwx,mwy))
					mapwinx=sendmessage(winmap,"wm_getpos",True)
					mapwiny=sendmessage(winmap,"wm_getpos",False)
					cx=mapwinx+wxo+2
					cy=mapwiny+wyo+1
					cx=cx+(mhx*hexa+(mhy Mod 2)*hexr)
					cy=cy+(mhy*(hexh+hexs))
					;DrawImage hexcursor,cx,cy
					disttxt$=""
					If hexanchorx>=0 And hexanchory>=0 Then
					    thedist=HexDistGuess4(hexanchorx,hexanchory,mhx,mhy)
						disttxt$="Anchor:"+n$+"["+hexanchorx+","+hexanchory+"]";, Distance: "+thedist
					End If
					planetinfo$=""
					If hexanchorx>-1 Then planetinfo$=disttxt$+n$+n$
					planetinfo$=planetinfo$+"Movement: "+pathcount+"/"+pmovemax
					For e.entity=Each entity
						If mhx=e\x And mhy=e\y And e\etype=EPLAYER Then planetinfo=planetinfo+n$+n$+"Ship Designation: "+e\name$
						If mhx=e\x And mhy=e\y And e\etype=EPLANET Then planetinfo=planetinfo+n$+n$+"Solar System Name:"+n$+e\name$+n$+n$+"Classification: "+e\class$
						If mhx=e\x And mhy=e\y And e\etype=ENEBULA Then planetinfo=planetinfo+n$+n$+"Nebula Name:"+n$+e\name$+n$+n$+"Classification: "+e\class$
						;;;If mhx=e\x And mhy=e\y Then sendmessage(imgstatusthis,"im_setimage",e\img)
					Next
					If map_terr(mhx,mhy)<>0 Then
					  If map_terr(mhx,mhy)<>99 Then
						planetinfo$=planetinfo$+n$+n$+"Controlled by: "+players(map_terr(mhx,mhy),1)
					  Else
					    planetinfo$=planetinfo$+n$+n$+"Territory is in dispute."
					  End If
					End If
					planetinfo$=planetinfo$+n$+n$+"Visibility: "+map_vis(mhx,mhy)
					mouseinfo$="Mouse:" +Chr(10)+"X="+app\mx+Chr(10)+"Y="+app\my+Chr(10)+Chr(10)+"Map Win:" +Chr(10)+"X="+mwx+", Y="+mwy+n$+n$+"hx="+mhx+n$+"hy="+mhy+n$+n$+disttxt
					sendmessage(lblstatus,"lm_settext","Cursor:"+n$+"["+mhx+","+mhy+"]"+n$+n$+planetinfo)
					If app\mb1=1 Then
						If hexanchorx=-1 Then
							;check if we're on one of our guys
							h_ok=0
							For e.entity=Each entity
								If e\x=mhx And e\y=mhy And e\etype=EPLAYER And e\owner=pnum Then h_ok=1:pmover=e\name$
							Next
							For i=1 To cmoveindex
								If pmover=cmovename$(i) Then h_ok=2
							Next
							Select h_ok 
								Case 1
									PlaySound thup
									hexanchorx=mhx
									hexanchory=mhy
								Case 0
									PlaySound bzzt
									sendmessage(lblInstr,"LM_SETTEXT",b+b+"Movement points remaining: "+pmovemax+n$+n$+"You may only select your own piece!")
								Case 2
									PlaySound bzzt
									sendmessage(lblInstr,"LM_SETTEXT",b+b+"Movement points remaining: "+pmovemax+n$+n$+"You've already moved that piece!")
							End Select
						Else
							If pathcount>pmovemax Then
								PlaySound bzzt
								sendmessage(lblInstr,"LM_SETTEXT",b+b+"Movement points remaining: "+pmovemax+n$+n$+"You may not move that far!")
							Else
								If pathcount>0 Then
									PlaySound sonicring
									;messagebox("You will move "+pmover+" "+Str$(pathcount)+" spaces.  Movement points remaining:"+Str$(pmovemax-pathcount),"Movement confirmation...")
									AddToMoveSteps()
									pmovemax=pmovemax-pathcount
									hexanchorx=-1
									hexanchory=-1
									pathcount=-1
									map_terr_new(packx(cmovestep(cmoveindex,cmovestep(cmoveindex,0))),packy(cmovestep(cmoveindex,cmovestep(cmoveindex,0))))=cmoveindex
									For i=1 To 6
									  j=hexdirection(packx(cmovestep(cmoveindex,cmovestep(cmoveindex,0))),packy(cmovestep(cmoveindex,cmovestep(cmoveindex,0))),i)
									  map_terr_new(packx(j),packy(j))=cmoveindex
									Next
									updatemap()
									sendmessage(imgmap,"im_setimage",mapimg)
									sendmessage(lblmoves,"lm_settext","Movement: " +Str$(pmovetot-pmovemax)+" / " +pmovetot)
								Else
									PlaySound thup
									hexanchorx=-1
									hexanchory=-1
									pathcount=-1
								End If
							End If
						End If
					End If
					If app\mb2=1 Then
						If hexanchorx>-1 Then
							PlaySound thup
							hexanchorx=-1
							hexanchory=-1
							pathcount=-1
							updatemap()
							sendmessage(imgmap,"im_setimage",mapimg)
							sendmessage(lblInstr,"LM_SETTEXT",b+b+"Movement points remaining: "+pmovemax+n$+n$+"Select a piece to move.")
						EndIf
					End If
					If hexanchorx>=0 And hexanchory>=0 And mhx<PACKBASE And mhy<PACKBASE Then ;do path
					  ;PlaySound thup
					  stt=MilliSecs()
						For i=0 To PACKBASE*PACKBASE+PACKBASE
							NodeG(i)=999
							NodeH(i)=999
							NodeParent(i)=-1
						Next
						goal=pack(mhx,mhy)
						start=pack(hexanchorx,hexanchory)
						ResetOpenListAndClosedList()
						SetOpen (start)
						SetG(start,10)
						SetH(start,HexDistGuess(start,goal)*10)
						finished=0
						While finished=0 ;0=still working; 1=found; 2=impossible
							; check the timer here in case pathfinding takes too long
							If MilliSecs()-stt>2000 Then found=1
							current=GetBestNode()
							If current=-1 Then
								finished=2
							Else
								SetClosed(current)
								If current=goal Then
									finished=1
								Else
									DoPath(current+( 0*PACKBASE+(-1)),current,goal)
									DoPath(current+( 1*PACKBASE+( 0)),current,goal)
									DoPath(current+( 0*PACKBASE+( 1)),current,goal)
									DoPath(current+(-1*PACKBASE+( 0)),current,goal)

									If packy(current) Mod 2=0 Then ;even y's
										DoPath(current+(-1*PACKBASE+(-1)),current,goal)
										DoPath(current+(-1*PACKBASE+( 1)),current,goal)
									Else ;must be odd y's
										DoPath(current+( 1*PACKBASE+(-1)),current,goal)
										DoPath(current+( 1*PACKBASE+( 1)),current,goal)
									End If
								End If
							End If
						Wend
						ste=MilliSecs()
						t=goal
						pathcount=0
						p$="Path: "+nodetoxy(t)
						While t<>start
							p$=p$ + ", "+nodetoxy(nodeparent(t))
							pathcount=pathcount+1
						 	pathstep(pathcount)=t
							t=nodeparent(t)
						Wend
						el=ste-stt
						sendmessage(lblInstr,"lm_settext",p$+"  steps="+pathcount+"  time="+el+n$+n$+"Right-click to cancel movement.")
						UpdateMap()
						sendmessage(imgmap,"im_setimage",mapimg)
						FlushKeys
					End If
				Case slVol
					bgmv=sendmessage(slvol,"SM_GETVALUE")
					sendmessage(lblVol,"lm_settext","Volume: "+RSet(bgmv,3))
					ChannelVolume bgm,Float(Float(bgmv)/Float(100))
				Case btnHexUp
					If hexs<=60 Then
						hexs=hexs+1
						redogfx()
						UpdateMap()
						sendmessage(imgmap,"im_setimage",mapimg)
						sendmessage(lblhexsize,"lm_settext",hexs)
					End If
				Case btnHexDown
					If hexs>=6 Then
						hexs=hexs-1
						redogfx()
						UpdateMap()
						sendmessage(imgmap,"im_setimage",mapimg)
						sendmessage(lblhexsize,"lm_settext",hexs)
					End If
				Default
					DebugLog "Default in event loop"
			End Select
	End Select

	If KeyHit(68) Then ;f10
		SaveBuffer(BackBuffer(),"ibexshot.bmp")
		PlaySound thup
	End If
	
	If KeyHit(67) Then ;f9
		PlaySound thup
		showhex=Not showhex
		updatemap()
		sendmessage(imgmap,"im_setimage",mapimg)
	End If
	
	If KeyHit(66) Then ;f8
		PlaySound thup
		ignorevis=Not ignorevis
		updatemap()
		sendmessage(imgmap,"im_setimage",mapimg)
	End If
	
	mapwinx=sendmessage(winmap,"wm_getpos",True)
	mapwiny=sendmessage(winmap,"wm_getpos",False)
	cx=mapwinx+wxo+2
	cy=mapwiny+wyo+1
	If hexanchorx>=0 And hexanchory>=0 Then
		cxa=cx+(hexanchorx*hexa+(hexanchory Mod 2)*hexr)
		cya=cy+(hexanchory*(hexh+hexs))
		;Text 10,10,""+cxa+"-"+cya
		DrawImage hexanchor,cxa,cya,framen
	EndIf
	framen=framen+framei
	If framen>=63 Or framen<=0 Then framei=-framei

	sf=0
	If framen Mod 20 >10 Then sf=1

    app\mcurrent=mouseimg
	DrawMouse()
	
	ResetEvents()
	
	Flip
	Cls
Until app\quit=True Or KeyHit(1)

Destroy()
End

Function BuildTurn$()
    Local t$,n$
	n$=Chr(10) 
	t$="[IbexPost]"+n$
	t$=t$+"Turn="+turnnum+n$
	t$=t$+"Movecount="+CMoveIndex+n$
	For i=1 To CMoveIndex
	  t$=t$+"Move "+Str$(i)+"="+cmovename$(i)+"~"
	  t$=t$+cmovestep(i,0)+"~"
	  For j=1 To cmovestep(i,0)
	    t$=t$+"["+packx(cmovestep(i,j))+","+packy(cmovestep(i,j))+"] "
	  Next
	  t$=t$+n$
	Next
	t$=t$+"Vismap="
	For i=0 To 63
	  For j=0 To 63
		t$=t$+Str$(map_vis(i,j))
	  Next
	Next
	t$=t$+n$
	Return t$
End Function

Function DrawBG()
  DrawBlock bgimg,0,0
  Color borderc,borderc,borderc
  Rect 0,0,1024,768,0
  Color borderc/2,borderc/2,borderc/2
  Rect 1,1,1022,766,0
  borderc=borderc+borderi
  If borderc>254 Or borderc<1 Then borderi=borderi*-1
End Function

Function UpdateMap()
SetBuffer ImageBuffer(tmpimg)
ClsColor 20,20,20
Cls
ClsColor 0,0,0

For s.star=Each star
	If map_vis(s\hx,s\hy)>0 Or ignorevis Then
		Color s\c,s\c,s\c
		Plot s\x,s\y
	End If
Next

;do territory
For u=0 To 40
 For v=0 To 40
  If map_terr(u,v)>0 And map_terr(u,v)<99 Then
	drawahex(hexstain,u+2,v+2,map_terr(u,v))
	For i=1 To 6
	  j=hexdirection(u,v,i)
	  If map_terr(packx(j),packy(j))<>map_terr(u,v) Then drawahex(hexborder,u+2,v+2,(map_terr(u,v)-1)*6+i)
	  If packx(j)>=PACKBASE-1 Or packy(j)>=PACKBASE-1 Then drawahex(hexborder,u+2,v+2,(map_terr(u,v)-1)*6+i)
	Next
  End If
  If map_terr_new(u,v)>0 Then
	drawahex(hexstain,u+2,v+2,5,1)
	For i=1 To 6
	  j=hexdirection(u,v,i)
	  If map_terr_new(packx(j),packy(j))=0 Then drawahex(hexborder,u+2,v+2,(5-1)*6+i,1)
	  If packx(j)>=PACKBASE-1 Or packy(j)>=PACKBASE-1 Then drawahex(hexborder,u+2,v+2,(5-1)*6+i,1)
	Next
  End If
  If map_terr(u,v)=99 Then drawahex(hexdisputed,u+2,v+2)
 Next
Next

If showhex Then
	For u=-2 To 40
	 For v=-2 To 40
	  drawahex(heximg,u+2,v+2,-1,1)
	 Next
	Next
End If
	
color 240,222,31
SetFont fnttahoma
For e.entity=Each entity
	If e\etype=EPLAYER Then
		drawahex(e\img,e\x+2,e\y+2,sf)
	Else
		drawahex(e\img,e\x+2,e\y+2)
	End If
	If e\etype=EPLANET And (map_vis(e\x,e\y)>0 Or ignorevis=1) Then
    	Text hexxtoxcenter(e\x+2,e\y+2),hexytoycenter(e\y+2)+15,e\name$,True,True
  end if
next

If pathcount>=0 Then
	For i=1 To pathcount; To 1 Step-1
		If pathcount-i+1>pmovemax Then
			drawahex(hexpathred,packx(pathstep(i))+2,packy(pathstep(i))+2,-1,1)
		Else
			drawahex(hexpath,packx(pathstep(i))+2,packy(pathstep(i))+2,-1,1)
		End If
	Next
End If

For i=1 To cmoveindex
  thick=0
  If selmover=cmovename(i) Then
	Color 240,240,240
	thick=1
  Else
  	setcol( pathcolors(i))
  End If
  For j=1 To cmovestep(i,0)-1
    tx1=packx(cmovestep(i,j))+2
    ty1=packy(cmovestep(i,j))+2
    tx2=packx(cmovestep(i,j+1))+2
    ty2=packy(cmovestep(i,j+1))+2
    ;DebugLog tx1+","+ty1+","+tx2+","+ty2
    Line hexxtoxcenter(tx1,ty1),hexytoycenter(ty1),hexxtoxcenter(tx2,ty2),hexytoycenter(ty2)
	If thick=1 Then
		Color 180,180,222
		Line hexxtoxcenter(tx1,ty1)-1,hexytoycenter(ty1)-1,hexxtoxcenter(tx2,ty2)-1,hexytoycenter(ty2)-1
		Line hexxtoxcenter(tx1,ty1)+1,hexytoycenter(ty1)+1,hexxtoxcenter(tx2,ty2)+1,hexytoycenter(ty2)+1
		;Color 180,180,180
		;Line hexxtoxcenter(tx1,ty1)-2,hexytoycenter(ty1)-2,hexxtoxcenter(tx2,ty2)-2,hexytoycenter(ty2)-2
		;Line hexxtoxcenter(tx1,ty1)+2,hexytoycenter(ty1)+2,hexxtoxcenter(tx2,ty2)+2,hexytoycenter(ty2)+2
	End If
  Next
  oval2(hexxtoxcenter(tx2,ty2),hexytoycenter(ty2),9,9,1)
  Color 0,0,0
  oval2(hexxtoxcenter(tx2,ty2),hexytoycenter(ty2),5,5,1)
Next

GrabImage mapimg,2*hexa-2,2*(hexh+hexs)-1
SetBuffer BackBuffer()
End Function


Function HexXtoXcenter(u,v)
  Return ((u)*hexa+((v) Mod 2)*hexr)+hexa/2
End Function

Function HexYtoYcenter(v)
  Return ((v)*(hexh+hexs))+(hexh+hexs)/2
End Function

Function MinOf(a,b)
If a<b Return a
Return b
End Function

Function MaxOf(a,b)
If a>b Return a
Return b
End Function

Function Oval2(x,y,w,h,s)
Oval x-w/2,y-h/2,w,h,s
End Function


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function DrawAHex(i,hxx,hyy,framen=-1,l_ignorevis=0)
    xpx=hxx*hexa+(hyy Mod 2)*hexr
    ypx=hyy*(hexh+hexs)
	;lousy ugly hack to re-align ship images
	If i=shipimg(1) Or i=shipimg(2) Or i=shipimg(3) Or i=shipimg(4) Then xpx=xpx+hexa/2:ypx=ypx+hexb/2
	If ignorevis Then
		l_ignorevis=1
	End If
	If l_ignorevis=1 Or ( l_ignorevis=0 And (map_vis(hxx-2,hyy-2)>0) )
		If framen=-1 Then
			DrawImage i,xpx,ypx
		Else
			DrawImage i,xpx,ypx,framen
		End If
	End If
End Function

Function XYtoHexXY(msx,msy)
xsect=msx/hexa
ysect=msy/(hexh+hexs)

xsectpxl=msx Mod hexa
ysectpxl=msy Mod Int(hexh+hexs)

If ysect Mod 2=0 Then
	;A-section
	hy=ysect
	hx=xsect
	If ysectpxl<(Float(hexh)-Float(xsectpxl)*hexm#) Then
		hy=ysect-1
		hx=xsect-1
	EndIf
	If ysectpxl<(-Float(hexh)+Float(xsectpxl)*hexm#) Then
		hy=ysect-1
		hx=xsect
	End If
Else
	;B section
	If xsectpxl>=hexr Then
		If ysectpxl<(Float(2*hexh)-xsectpxl*hexm#) Then
			hy=ysect-1
			hx=xsect
		Else
			hy=ysect
			hx=xsect
		EndIf
	EndIf
	If xsectpxl<hexr Then
		If ysectpxl<(xsectpxl*hexm#) Then
			hy=ysect-1
			hx=xsect
		Else
			hy=ysect
			hx=xsect-1
		End If
	End If
End If
Return pack(hx,hy)
End Function

Function HexDirection(u,v,dir) ; find coords of hex in direction from given hex
Local rc
If dir=1 Then ;ne
  rc=pack(u+(v Mod 2),v-1)
End If
If dir=2 Then ;e
  rc=pack(u+1,v)
End If
If dir=3 Then ;se
  rc=pack(u+(v Mod 2),v+1)
End If
If dir=4 Then ;sw
  rc=pack(u-((v+1) Mod 2),v+1)
End If
If dir=5 Then ;w
  rc=pack(u-1,v)
End If
If dir=6 Then ;nw
  rc=pack(u-((v+1) Mod 2),v-1)
End If
Return rc
End Function

Function ReDoGFX()
hexh=Sin(30)*hexs
hexr=Cos(30)*hexs
hexb=hexs+2*hexh
hexa=2*hexr
hexm#=Float(hexh)/Float(hexr)

FreeImage heximg
heximg=CreateImage(hexa,hexb)
SetBuffer ImageBuffer(heximg)
Cls
Color 7,64,0
Line hexr,0,hexa-1,hexh
Line hexa-1,hexh,hexa-1,hexh+hexs
Line hexa-1,hexh+hexs,hexr,hexb-1
;Line hexr,hexb-1,0,hexh+hexs
;Line 0,hexh+hexs,0,hexh
;Line 0,hexh,hexr,0

FreeImage hexcursor
hexcursor=CreateImage(hexa,hexb)
SetBuffer ImageBuffer(hexcursor)
Cls
Color 255,192,0
Line hexr,0,hexa-1,hexh
Line hexa-1,hexh,hexa-1,hexh+hexs
Line hexa-1,hexh+hexs,hexr,hexb-1
Line hexr,hexb-1,0,hexh+hexs
Line 0,hexh+hexs,0,hexh
Line 0,hexh,hexr,0
 
FreeImage hexanchor
hexanchor=CreateImage(hexa,hexb,64)
For i=0 To 63
 SetBuffer ImageBuffer(hexanchor,i)
 Cls
 Color i*4,i*3,128+i*2
 Line hexr,0,hexa-1,hexh
 Line hexa-1,hexh,hexa-1,hexh+hexs
 Line hexa-1,hexh+hexs,hexr,hexb-1
 Line hexr,hexb-1,0,hexh+hexs
 Line 0,hexh+hexs,0,hexh
 Line 0,hexh,hexr,0
Next 

FreeImage hexpath
hexpath=CreateImage(hexa,hexb)
SetBuffer ImageBuffer(hexpath)
Cls
Color 0,250,48
Line hexr,0+2,hexa-1-2,hexh
Line hexa-1-2,hexh,hexa-1-2,hexh+hexs
Line hexa-1-2,hexh+hexs,hexr,hexb-1-2
Line hexr,hexb-1-2,0+2,hexh+hexs
Line 0+2,hexh+hexs,0+2,hexh
Line 0+2,hexh,hexr,0+2

FreeImage hexpathred
hexpathred=CreateImage(hexa,hexb)
SetBuffer ImageBuffer(hexpathred)
Cls
Color 222,22,44
Line hexr,0+2,hexa-1-2,hexh
Line hexa-1-2,hexh,hexa-1-2,hexh+hexs
Line hexa-1-2,hexh+hexs,hexr,hexb-1-2
Line hexr,hexb-1-2,0+2,hexh+hexs
Line 0+2,hexh+hexs,0+2,hexh
Line 0+2,hexh,hexr,0+2

;stains
FreeImage hexstain
hexstain=CreateImage(hexa,hexb,6)
For i=1 To 5
	SetBuffer ImageBuffer(hexstain,i)
	Cls
	setcol(stain(i))
	For dy=0 To hexb Step 4
	  For dx=0 To hexa Step 4
	    If xytohexxy(dx,dy)=0 Then Plot dx,dy ;0=pack(0,0)
	  Next
	  For dx=2 To hexa Step 4
	    If xytohexxy(dx,dy+2)=0 Then Plot dx,dy+2
	  Next
	Next
Next

; borders
FreeImage hexborder
hexborder=CreateImage(hexa,hexb,6*5+1) ;6 directions, 4 players +1 for new terr
For i=0 To 4
  setcol(stainb(i+1))
  For j=1 To 6
    SetBuffer ImageBuffer(hexborder,i*6+j)
    Cls
    If j=1 Then
		Line hexr,0+2,hexa-1-2,hexh
	End If
	If j=2 Then 
		Line hexa-1-1,hexh,hexa-1-1,hexh+hexs
	End If
	If j=3 Then		
		Line hexa-1-2,hexh+hexs,hexr,hexb-1-2
	End If
	If j=4 Then
		Line hexr,hexb-1-2,0+2,hexh+hexs
	End If
	If j=5 Then	
		Line 0+1,hexh+hexs,0+1,hexh
	End If
	If j=6 Then
		Line 0+2,hexh,hexr,0+2
	End If
  Next
Next

;disputed
FreeImage hexdisputed
hexdisputed=CreateImage(hexa,hexb)
SetBuffer ImageBuffer(hexdisputed)
Color 100,70,80
For dy=0 To hexb
  For dx=(dy Mod 6) To hexa Step 6
	  If xytohexxy(dx,dy)=0 Then Plot dx,dy ;0=pack(0,0)
  Next
Next

FreeImage planet1img
planet1img=CreateImage(hexa,hexb)
SetBuffer ImageBuffer(planet1img)
For i=10 To 1 Step -1
 Color (11-i)*8,0,(11-i)*12+120
 Oval2( hexa/2,hexb/2,i,i,1)
Next

FreeImage planet2img
planet2img=CreateImage(hexa,hexb)
SetBuffer ImageBuffer(planet2img)
For i=10 To 1 Step -1
 Color (11-i)*8,(11-i)*12+120,0
 Oval2( hexa/2,hexb/2,i,i,1)
Next

FreeImage nebulaimg
nebulaimg=CreateImage(hexa,hexb)
SetBuffer ImageBuffer(nebulaimg)

For i=1 To 250
 Color 170+Rand(-50,50),0,170+Rand(-50,50)
 Plot Rand(0,hexa/4)+Rand(0,hexa/4)+Rand(0,hexa/4)+Rand(0,hexa/4),Rand(0,hexb/4)+Rand(0,hexb/4)+Rand(0,hexb/4)+Rand(0,hexb/4)
Next

For s.star=Each star
	i=xytohexxy(s\x,s\y)
	s\hx=packx(i)-2
	s\hy=packy(i)-2
	If s\hx<0 Or s\hy<0 Then
		s\hx=64
		s\hy=64
	End If
Next


ReAssociateEntitiesAndImages()

End Function

Function ReAssociateEntitiesAndImages()
	For e.entity=Each entity
		If e\etype=EPLANET And e\classtype=1 Then e\img=planet1img
		If e\etype=EPLANET And e\classtype=2 Then e\img=planet2img
		If e\etype=ENEBULA Then e\img=nebulaimg
	Next
End Function


Function HexDistGuess4(x1,y1,x2,y2)
dx=x1-x2
dy=y1-y2
Return (Abs(dx)+Abs(dy)+Abs(dx-dy))/2
;If Sgn(dx)=Sgn(dy) Then
;	thedist=maxof(Abs(dx),Abs(dy))
;Else
;	thedist=Abs(dx)+Abs(dy)
;End If
End Function

Function HexDistGuess(xy1,xy2)
Return hexdistguess4(packx(xy1),packy(xy1),packx(xy2),packy(xy2))
End Function


Function RemoveFromClosed(n)
 For i=1 To ClosedListCount
   If ClosedList(i)=n Then
	For j=i+1 To ClosedListCount
	  ClosedList(j-1)=ClosedList(j)
	Next
	closedlistcount=closedlistcount-1
	Return
   End If
 Next
End Function

Function RemoveFromOpen(n)
 For i=1 To OpenListCount
   If OpenList(i)=n Then
	For j=i+1 To OpenListCount
	  OpenList(j-1)=OpenList(j)
	Next
	openlistcount=openlistcount-1
	Return
   End If
 Next
End Function

Function SetOpen(n)
 RemoveFromClosed(n)

 For i=1 To OpenListCount
   If OpenList(i)=n Then Return
 Next
 openlistcount=openlistcount+1
 openlist(openlistcount)=n
End Function


Function SetClosed(n)
 RemoveFromOpen(n)

 For i=1 To ClosedListCount
   If ClosedList(i)=n Then Return
 Next
 closedlistcount=closedlistcount+1
 closedlist(closedlistcount)=n
End Function

Function SetParent(n,p)
 NodeParent(n)=p
End Function

Function SetG(n,c)
 NodeG(n)=c
End Function

Function SetH(n,h)
 NodeH(n)=h
End Function

Function GetG(n)
 return NodeG(n)
End Function

Function GetH(n)
 Return NodeH(n)
End Function


Function IsClosed(n)
 For i=1 To closedlistcount
  If closedlist(i)=n Then Return True
 Next
 Return False
End Function

Function IsOpen(n)
 For i=1 To openlistcount
  If openlist(i)=n Then Return True
 Next
 Return False
End Function

Function DoPath(successor,current,goal)
If successor<0 Then Return
If successor>PACKBASE*PACKBASE+PACKBASE Then Return
If successor Mod PACKBASE > (PACKBASE*0.9) Then Return
sx=packx(successor)
sy=packy(successor)
If sx<0 Or sx>PACKBASE Then Return
If sy<0 Or sy>PACKBASE Then Return

If Not IsClosed(successor) Then
	If Not IsOpen(successor) Then
		SetOpen(successor)
		SetParent(successor,current)
		SetG(successor,GetG(current)+map_cost(sx,sy))
		SetH(successor,HexDistGuess(successor,goal)*10)
	Else
		If GetG(current)+map_cost(sx,sy)<GetG(successor) Then
			SetParent(successor,current)
			SetG(successor,GetG(current)+map_cost(sx,sy))
		End If
	End If
End If
End Function

Function GetBestNode()
best=9999
j=-1
 For i=1 To openlistcount
  sc=getg(openlist(i))+geth(openlist(i))
  If sc<best Then
	best=sc
	j=openlist(i)
  End If
 Next
Return j

End Function 

Function NodeToXY$(n)
Return "["+packx(n)+","+packy(n)+"]"
End Function

Function AddToMoveSteps()
	CMoveIndex=CMoveIndex+1
	CMoveName$(CMoveIndex)=pmover$
	CMoveStep(CMoveIndex,1)=pack(hexanchorx,hexanchory)
	For i=1 To pathcount
	  CMoveStep(CMoveIndex,i+1)=pathstep(pathcount-i+1)
	next
	CMoveStep(CMoveIndex,0)=pathcount+1
	AddListBoxItem( lstMoves, 0, pmover$ +" / ["+hexanchorx+","+hexanchory+"] - ["+packx(cmovestep(cmoveindex,cmovestep(cmoveindex,0)))+","+packy(cmovestep(cmoveindex,cmovestep(cmoveindex,0)))+"] / "+(cmovestep(cmoveindex,0)-1)+"" )
	selmover=""
End Function

Function LoadMapCosts()
For a=0 To 63
  For b=0 To 63
    i=10
	If map_terr(a,b)<>pnum Then i=i+20
	If map_terr(a,b)=pnum Then i=i-5
    map_cost(a,b)=i
  Next
next

For e.entity=Each entity
  map_cost(e\x,e\y)=map_cost(e\x,e\y)+e\movecost
Next
End Function

Function ResetOpenListAndClosedList()
OpenListCount=0
ClosedListCount=0
End Function

Function MakePathColors()
PathColors(6)=makecol( 200,50,0)
PathColors(5)=makecol(255,150,0)
PathColors(4)=makecol(200,200,0)
PathColors(3)=makecol(100,220,0)
PathColors(2)=makecol(50,50,220)
PathColors(1)=makecol(150,0,190)
End Function

Function pack(thex,they)
If thex<0 Then thex=PACKBASE-1
If they<0 Then they=PACKBASE-1
Return ((thex*PACKBASE)+they)
End Function

Function packx(p)
Return Int(p/PACKBASE)
End Function

Function packy(p)
Return (p Mod PACKBASE)
End Function

Function SetCol(c)
Color (c And %111111110000000000000000) Shr 16 ,(c And %000000001111111100000000) Shr 8, c And %000000000000000011111111
End Function

Function MakeCol(r,g,b)
Return (r Shl 16) Or (g Shl 8) Or b
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Function ParseTurn$(r$,t$)
If Left(r$,6)<>"[Ibex]" Then
	Return "badturn" ;bad turn file
Else
	pti1=Instr(r$,t$)
	If pti1=0 Then
		Return "" ;substring not found
	Else
		pti2=Instr(r$,"=",pti1)
		If pti2=0 Then
			Return "" ;equals sign not found
		Else
			pti3=Instr(r$,Chr(13),pti2)
			Return Mid$(r$,pti2+1,pti3-pti2-1)
		End If
	End If
End If
End Function

Function scmd$(thecmd$)
	Return geturl$(server+"/"+pnum+"/"+thecmd$)
End Function

Function pcmd$(thecmd$,content$)
	Local pu$=posturl$(server+"/"+pnum+"/"+thecmd$,content$)
	Local p=Instr(pu$,Chr(10)+Chr(10))
	Return Mid$(pu$,p+2)
End Function

Function PostURL$(Adresse$,content$,port=80)
	;Http herrausschneiden
	If Left(Upper(Adresse$),7)="HTTP://" Then
		Adresse$ = Mid(adresse$,8,-1)
	EndIf
	
	Local Server$, Seite$
	
	;Unterseite auf dem Server finden:
	x=Instr(Adresse$,"/")
	If x =0 Then
		Server$=Adresse$
		Seite$="/"
	Else
		Server$=Left(Adresse$,x-1)
		Seite$=Right(Adresse$,Len(Adresse$)-x+1)
	EndIf
	
	Local Htmlzeichen$,HTML_Keyword$,l
	Local txt$,m,g,i,tcp,Zeile$,Buchstabe$
	
	;verbingung Öffnen
	tcp = HttpPost(Server$,Seite$,content$,port,Proxy_Adress$,Proxy_port%) ; the last 3 points, are for the Proxy and Ports.
	If tcp = False RuntimeError "unable to connect to address"

	zeile$=""	
	
	;Seite auslesen
	While Eof(tcp) = False
		zeile$=zeile$+ReadLine$(tcp) +Chr(10); Zeile zum bearbeiten
	Wend
	CloseTCPStream(tcp)
		
	Return zeile$
End Function


Function GetURL$(Adresse$,port=80,stripheaders=True,bytemode=False,byteoutfile$="")
	;Http herrausschneiden
	If Left(Upper(Adresse$),7)="HTTP://" Then
		Adresse$ = Mid(adresse$,8,-1)
	EndIf
	
	Local Server$, Seite$
	
	;Unterseite auf dem Server finden:
	x=Instr(Adresse$,"/")
	If x =0 Then
		Server$=Adresse$
		Seite$="/"
	Else
		Server$=Left(Adresse$,x-1)
		Seite$=Right(Adresse$,Len(Adresse$)-x+1)
	EndIf
	
	Local Htmlzeichen$,HTML_Keyword$,l
	Local txt$,m,g,i,tcp,Zeile$,Buchstabe$

	;verbingung Öffnen
	If bytemode=True Then
		;Print byteoutfile
		If FileType(byteoutfile+".lm")=1 Then
			lm=ReadFile (byteoutfile+".lm")
			ims$=ReadLine(lm)
			;Print ims$
			CloseFile(lm)
		Else
			ims=""
		End If
	Else
		ims$=""
	End If
	tcp = HttpGet(Server$,Seite$,port,Proxy_Adress$,Proxy_port%,ims$) ; the last 3 points, are for the Proxy and Ports.
	If tcp = False RuntimeError "unable to connect to address"

	If bytemode=True Then
		http304=False
		t="" 
		Repeat
			header$ = ReadLine (tcp)
			t=t+ header$
			reply$ = ""
			pos = Instr (header$, ": ")
			If pos
				reply$ = Left (header$, pos + 1)
			Else
				;e.g. HTTP/1.1 304 Not Modified
				pos=Instr(header$," ")
				reply$=Mid(header$,pos+1,3)
			EndIf
			Select Lower (reply$)
				Case "content-length: "
					bytesToRead = ReplyContent (header$, reply$)
				Case "date: "
					date$ = ReplyContent (header$, reply$)
				Case "server: "
					server$ = ReplyContent (header$, reply$)
				Case "content-type: "
					contentType$ = ReplyContent (header$, reply$)
				Case "last-modified: "
					lastModified$=ReplyContent(header$,reply)
				Case "304"
					http304=True
				Default
					If gotReply = 0 Then initialReply$ = header$: gotReply = 1
			End Select
		Until header$ = "" Or (Eof (tcp))
		If bytesToRead = 0 Then
			If http304 Then
				CloseTCPStream tcp
				;Print "Not modified."
				Return ""
			Else
				RuntimeError "No content-length. "+t
			End If
		End If
		save = WriteFile (byteoutfile)
		If Not save Then RuntimeError "unable to open writefile " + byteoutfile
		siz=0
		tt1=MilliSecs()
		onemeg=1024*1024
		bank=CreateBank(onemeg)
		temp=ReadAvail(tcp)
		siz=siz+temp
		lastpct=0
		While Not Eof(tcp)
			If temp<onemeg Then
				ReadBytes bank,tcp,0,temp
				WriteBytes bank,save,0,temp
			Else
				ReadBytes bank,tcp,0,onemeg
				WriteBytes bank,save,0,onemeg
			End If
			temp=ReadAvail(tcp)
			siz=siz+temp
			thispct=Int(Float(siz)/Float(bytestoread)*10)
			If thispct<>lastpct Then
				Print " "+thispct+"0%"
				lastpct=thispct
			End If
		Wend
		FreeBank bank
		CloseFile save
		CloseTCPStream tcp
		lm=WriteFile(byteoutfile+".lm")
		WriteLine lm,lastModified$
		CloseFile lm
		;Print bytestoread/(MilliSecs()-tt1) ;kBps
	Else
		zeile$=""	
		;Seite auslesen
		While Eof(tcp) = False
			zeile$=zeile$+ReadLine$(tcp) +Chr(13)+Chr(10); Zeile zum bearbeiten
		Wend
		CloseTCPStream(tcp)
		If stripheaders=True Then
			x=Instr(zeile$,Chr(13)+Chr(10)+Chr(13)+Chr(10))
			zeile$=Mid(zeile$,x+4)
		End If
	End If		
	Return zeile$
End Function


Function HttpGet(server$,path$,port=80,proxy$="",proxyport=0,ims$="")
	Local www
	If Len(proxy$) = 0 proxy$ = server$
	If proxyport = 0 proxyport = port
	www = OpenTCPStream(proxy$,proxyport)
	If www = False Return False
	WriteLine www,"GET "+ path$ + " HTTP/1.0" + Chr$(13)+Chr$(10) + "Host: " + server$ + Chr$(13)+Chr$(10) + "User-Agent: Ibex/"+ IBEXVER + Chr$(13)+Chr$(10) + "Accept: */*"
	If ims$<>"" Then WriteLine www,"If-Modified-Since: "+ims$ 
	WriteLine www,""
	Return www
End Function

Function HttpPost(server$,path$,content$,port=80,proxy$="",proxyport=0)
	Local www,l$
	If Len(proxy$) = 0 proxy$ = server$
	If proxyport = 0 proxyport = port
	www = OpenTCPStream(proxy$,proxyport)
	If www = False Return False
	l="POST "+ path$ + " HTTP/1.0" + Chr$(13)+Chr$(10) + "Host: " + server$ + Chr$(13)+Chr$(10) + "User-Agent: Ibex/"+ IBEXVER + Chr$(13)+Chr$(10) + "Accept: */*" + Chr$(13)+Chr$(10)+"Content-Type: application/ibexturn; charset=us-ascii"+Chr$(13)+Chr$(10)+"Content-Length: "+Len(content$)+Chr$(13)+Chr$(10)
	WriteLine www,l
	WriteLine www,content$
	Return www
End Function

Function ReplyContent$ (header$, reply$)
	Return Right (header$, Len (header$) - Len (reply$))
End Function

Function zpad3$(a$)
Return Right("0000"+a$,3)
End Function

Function StripCRLF$(a$)
Local i,r$,m$
r$=""
For i=1 To Len(a$)
	m$=Mid$(a$,i,1)
	If m$<>Chr(10) And m$<>Chr(13) Then r$=r$+m$
Next
Return r$
End Function


Function LoadAnimResource(f$,a,b,c,d)
FileExists(f$)
rc=LoadAnimImage(f$,a,b,c,d)
Return rc
End Function

Function LoadResource(f$)
Local ext$
	FileExists(f$)
	rc=0
	ext$=Upper$(Right$(f$,4))
	Select ext$
		Case "MIDI",".MID",".MP3",".OGG"
			rc=PlayMusic(f$)
		Case ".WAV"
			rc=LoadSound(f$)
		Case ".PNG","JPEG",".JPG",".GIF",".BMP"
			rc=LoadImage(f$)
		Default
			Print "ERROR: Unhandled resource type ("+ext$+"): " +f$
			RuntimeError "ERROR: Unhandled resource type ("+ext$+"): " +f$
			End
	End Select
	Return rc
End Function

Function FileExists(f$)
Local e
	e=FileType(f$)
	If e<>1 Then
		Print "ERROR: File not found: ("+f$+")"
		RuntimeError "ERROR: File not found: ("+f$+")"
		End
	End If
End Function