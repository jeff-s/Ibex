AppTitle "Ibex 0016 - 12/06/05"

; version history
;
; 0010 - 10/12/05 - add custom guy type; multiple guys; pathfinding w/o shift key
; 0011 - 10/14/05 - redo pathfinding with packed int thingy
; 0012 - 10/15/05 - unified map data structure, more guys
; 0013 - 10/23/05 - keep track of movement points, use red hex path thingy
; 0014 - 11/02/05 - add movement structure, movement queue window, unified color representation
; 0015 - 11/13/05 - talk to server!
; 0016 - 11/29/05 - post to server
;
; to-do's
; x consistent map data structure representation (nebulae, eyeguys and planets)
; x structure to aggregate pieces and their moves
; x add movement points back after delete
; x figure out how to unselect entire movement list (by learning to spell, apparently)
;   implement movenent as TYPE instead of ugly array
;   updatemap/sendmessage every time through event loop?
;   unifiedcolor brightener/darkener
;   load map from text file
;   load map from server

Global IBEXVER$="0016"

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

Dim PathStep(63)
Global PathCount=-1

Dim OpenList(255)
Global OpenListCount=0
Dim ClosedList(255)
Global ClosedListCount=0

Dim CMoveName$(20)
Dim CMoveStep(20,20)
global CMoveIndex=0

Const PACKBASE=64 ;factor when combining hx/hy pairs
Dim NodeParent(PACKBASE*PACKBASE+PACKBASE)
Dim NodeG(PACKBASE*PACKBASE+PACKBASE)
Dim NodeH(PACKBASE*PACKBASE+PACKBASE)
Dim map_cost(25,25)

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
Print "Pinging " +server
resp$= scmd("PING")
Print stripcrlf(resp)
If Left(resp,3)<>"ACK" Then
	Print "error of some sort..... ending..."
	Stop
	WaitKey
	End
End If
Print "Pung."
Print "Getting turn..."
resp$=scmd("GETTURN")
pmovemax=Int(parseturn(resp$,"moves"))
motd$=parseturn(resp$,"motd")
title=parseturn(resp$,"title")
dynasty=parseturn(resp$,"dynasty")
firstname=parseturn(resp$,"firstname")
turnnum=parseturn(resp$,"turn")

Print "OK, got it all.  Press a key to start."

WaitKey

Global Pmovetot=pmovemax

;.here
;Print "Who do you want to be?"
;Print "1. Eye-Guys"
;Print "2. The Ophidians"
;Print "3. Teal Magi"
;Print "4. Purple Dragons"
;pnum=Int(Input(">"))
;If pnum<0 Or pnum>4 Then Goto here

Global pmover$;the guy to move
Global selmover$ ;the guy selected from list

Dim PathColors(10)
MakePathColors()

Const BGMGRP=1

Graphics 1024,768;,16;,2
;Graphics 1400,1050,32
SetBuffer BackBuffer()
hidepointer
;;;movemouse 10,10 ;make mac-like
MoveMouse 512,384
cls
flip
SeedRnd MilliSecs()

bgmfn(1)="media/At_Zanarkand_Piano_Collection_(Unofficial).Mid"
bgmfn(2)="media/Clouds of Xeen - Landscape.Mid"
bgmfn(3)="media/Hyrule_feild_4_Concert.Mid"
bgmfn(4)="media/MMP_Adventure.Mid"
bgmfn(5)="media/MMP_Nobility.Mid"
bgmfn(6)="media/MMP_Otherworld.Mid"
bgmfn(7)="media/MP2AdventureBegins.Mid"
bgmfn(8)="media/WL3_MapRemix.Mid"
bgmn(1)="Final Fantasy X"
bgmn(2)="Might & Magic, Clouds of Xeen"
bgmn(3)="Legend of Zelda, Ocarina of Time"
bgmn(4)="Adventure *"
bgmn(5)="Nobility *"
bgmn(6)="Otherworld *"
bgmn(7)="Mario Party 2"
bgmn(8)="Wario Land 3"

;bgm=PlayMusic("media/noo.mp3")
bgm=PlayMusic("media/Back To Back.mp3")
bgmv=75
ChannelVolume bgm,Float(Float(bgmv)/Float(100))

include "blitzui.bb"
include "messagebox.bb"

Global fntTahoma=LoadFont("Tahoma",12,False)

LoadColourScheme ("media/MidnightIbex.cs")
;loadcolourscheme("E:\jms\blitz\BlitzUI_130\BlitzUI\Schemes/slate.cs")
ibexicon=loadimage("media/ibexicon.png")
global bgimg=loadimage("media/bg.png")
mouseimg=loadimage("media/jmouse1.png")
maskimage mouseimg,0,0,0
sonicring=LoadSound("media/sonic ring.wav")
thup=LoadSound("media/thup.wav")
bzzt=LoadSound("media/bzzt.wav")
bweep=LoadSound("media/mtruc.wav")
coolendingsound=LoadSound("media/Cool Ending Sound.wav")

Global mapimg=CreateImage(512,512)
Global tmpimg=CreateImage(768,768)

Global planet1img,planet2img
Global nebulaimg
Dim shipimg(4)
shipimg(1)=LoadAnimImage("media/eyeguy.png",26,37,0,2)
shipimg(2)=LoadAnimImage("media/snakeguy.png",26,37,0,2)
shipimg(3)=LoadAnimImage("media/mageguy.png",26,37,0,2)
shipimg(4)=LoadAnimImage("media/purpledragonguy.png",26,37,0,2)
MaskImage shipimg(1),255,0,255
MidHandle shipimg(1)
MaskImage shipimg(2),255,0,255
MidHandle shipimg(2)
MaskImage shipimg(3),255,0,255
MidHandle shipimg(3)
MaskImage shipimg(4),255,0,255
MidHandle shipimg(4)


Global hexs=26
ReDoGFX()
SeedRnd MilliSecs()

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

Restore PlanetData
Read num
For i=1 To num
	Read pl1$,pl2$,pl3
	e.entity = New entity
	e\etype=EPLANET
	e\owner=1
	If pl3=1 Then e\img=planet1img
	If pl3=2 Then e\img=planet2img
	e\classtype=pl3
	e\x=Rand(1,9)
	e\y=Rand(1,9)
	e\name$=pl1$
	e\movecost=1
	e\class$=pl2$
Next
.PlanetData
Data 5
Data "Lomilwa","M",2
Data "Tiltowait","D",1
Data "Maporfic","M",2
Data "Lorto","V",1
Data "Halito","M",2

Restore NebulaData
Read num
For i=1 To num
	Read n1,n2,n3$
	e.entity = New entity
	e\etype=ENEBULA
	e\owner=0
	e\img=nebulaimg
	e\x=n1
	e\y=n2
	e\name$=n3$
	e\movecost=50
	e\class$="Nebula"
Next
.NebulaData
Data 6
data 3,3,"Effervescent Nebula",4,4,"Effervescent Nebula",4,5,"Effervescent Nebula"
data 7,7,"Scintillating Nebula",8,8,"Scintillating Nebula",8,9,"Scintillating Nebula"


Restore Guy1Data
Read num
For i=1 To num
	Read n$,x,y
	e.entity=New entity
	e\eType=EPLAYER
	e\owner=1
	e\img=shipimg(1)
	e\name$=n$
	e\x=x
	e\y=y
	e\movecost=50
Next
.Guy1Data
Data 5
Data "iBall",0,0,"iPod",1,0,"iBex",2,0,"iTalics",3,0,"AyeCap'n",4,0

Restore Guy2Data
Read num
For i=1 To num
	Read n$,x,y
	e.entity=New entity
	e\eType=EPLAYER
	e\owner=2
	e\img=shipimg(2)
	e\name$=n$
	e\x=x
	e\y=y
	e\movecost=50
Next
.Guy2Data
Data 5
Data "FireDog",0,11,"Thrash'r",1,11,"Killbot",2,11,"Zylox",3,11,"Proton",4,11

Restore Guy3Data
Read num
For i=1 To num
	Read n$,x,y
	e.entity=New entity
	e\eType=EPLAYER
	e\owner=3
	e\img=shipimg(3)
	e\name$=n$
	e\x=x
	e\y=y
	e\movecost=50
Next
.Guy3Data
Data 5
Data "Magnus",0,4,"Maggie",1,4,"Magic",0,5,"Madge",0,6,"Magi",1,6

Restore Guy4Data
Read num
For i=1 To num
	Read n$,x,y
	e.entity=New entity
	e\eType=EPLAYER
	e\owner=4
	e\img=shipimg(4)
	e\name$=n$
	e\x=x
	e\y=y
	e\movecost=50
Next
.Guy4Data
Data 5
Data "Purple",9,4,"Violet",10,4,"Orchid",9,5,"Lavendar",9,6,"Mauve",10,6



LoadMapCosts()	

SetBuffer BackBuffer()

global borderc=0
global borderi=1
Flip

Initialise() ;BlitzUI

;Delay 2000 ;for dramatic effect


;MessageBox("Welcome to Ibex","Ibex")

;Window
winMap = Window( 10, 10, 512+15, 512+35, "Map", "0", 1, 0, 1, 1 )
imgMap=ImageBox( 5,5,512,512,mapimg,0,0,0)

;Window
winStatus = Window( 850, 285, 150, 250, "Status", "0", 1, 0, 1, 1 )
lblStatus= Label( 5,5,"",0)
imgstatusthis=Imagebox(110,10,32,32,0,"Outer","Center","Middle")

;Movement Queue Window
winQ = Window( 590, 10, 200, 302, "Movement Queue", "0", 1, 0, 1, 0 )
btnQDel = Button( 100, 242, 81, 25, "Delete Selected", "0", 1, 0, 0 )
lstMoves = ListBox( 5, 6, 185, 220, 20, 20, 10, 0 )
lblMoves = Label(5,260,"Movement: 0 / "+pmovemax)
settooltip (btnQDel,"Remove selected piece from the queue" )

;Window
winMain = Window( 810,10 , 200, 264, "Ibex", ibexicon, 1, 0, 1, 0 )
btnComm = Button( 31, 37, 134, 32, "Play Sound", "0", 1, 0, 0 )
SetToolTip( btnComm, "Plays the 'Sonic Ring' Sound." )
btnQuit = Button( 32, 85, 138, 27, "End Turn", "0", 1, 0, 0 )
btnMap = Button( 46, 133, 81, 50, "View Map", "0", 1, 0, 0 )
settooltip (btnMap,"Re-opens the 'Map' window if you've closed it accidentally" )
btnHexUp=Button(130,130,20,20,"+");,0,1,0,0)
btnHexDown=Button(130,160,20,20,"-");,0,1,0,0)
lblHexSize=Label(160,145,hexs)
grp001 = GroupBox( 11, 15, 170, 193, "Main Command Module" )

;Instr Window
winInstr=Window(10,600,525,150,"Instructions",0,1,0,1,0)
lblInstr=Label(5,5,"",0)

;jukebox Window
winJuke=Window(800,550,200,200,"Ibex Jukebox (Ibox)",0,1,0,1,1)
slVol=Slider(5,5,100,12,bgmv,0,100,"Integer","Horizontal","0,0,64","0,0,255")
lblVol=Label(110,5,"Volume: "+RSet(bgmv,3))
For i=1 To 8
 rbgm(i)=Radio(5,i*15+25,bgmn(i),False,BGMGRP)
Next
sendmessage(rbgm(1),"rm_setchecked",True)


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
					PlaySound sonicring
					messagebox(t$,"Sending to server...",1)
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
					DrawImage hexcursor,cx,cy
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
					For i=1 To 8
					  If app\gadgetevent=rbgm(i) Then
						StopChannel bgm
						bgm=PlayMusic(bgmfn(i))
						ChannelVolume bgm,Float(Float(bgmv)/Float(100))
					  End If
					Next
			End Select
	End Select

	If KeyHit(68) Then ;f10
		SaveBuffer(BackBuffer(),"ibexshot.bmp")
		PlaySound thup
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
Until app\quit=True; Or KeyHit(1)

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

;specks for stars
SeedRnd 1
For i=1 To 1000
	ii=Rand(1,255)
	Color ii,ii,ii
	Plot Rand (0,767),Rand(0,767)
Next
SeedRnd MilliSecs()


For u=0 To 20
 For v=0 To 20
  drawahex(heximg,u,v)
 Next
Next

color 240,222,31
SetFont fnttahoma
For e.entity=Each entity
	If e\etype=EPLAYER Then
		drawahex(e\img,e\x+2,e\y+2,sf)
	Else
		drawahex(e\img,e\x+2,e\y+2)
	End If
	if e\etype=EPLANET then
    	Text hexxtoxcenter(e\x+2,e\y+2),hexytoycenter(e\y+2)+15,e\name$,True,True
  end if
next

If pathcount>=0 Then
	For i=1 To pathcount; To 1 Step-1
		If pathcount-i+1>pmovemax Then
			drawahex(hexpathred,packx(pathstep(i))+2,packy(pathstep(i))+2)
		Else
			drawahex(hexpath,packx(pathstep(i))+2,packy(pathstep(i))+2)
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

Function DrawAHex(i,hxx,hyy,framen=-1)
    xpx=hxx*hexa+(hyy Mod 2)*hexr
    ypx=hyy*(hexh+hexs)
	;lousy ugly hack to re-align ship images
	If i=shipimg(1) Or i=shipimg(2) Or i=shipimg(3) Or i=shipimg(4) Then xpx=xpx+hexa/2:ypx=ypx+hexb/2
	If framen=-1 Then
		DrawImage i,xpx,ypx
	Else
		DrawImage i,xpx,ypx,framen
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
Color 0,216,48
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

SeedRnd MilliSecs()
For i=1 To 500
 Color 190+Rand(0,40),Rand(24,96),224+Rand(-30,30)
 Plot Rand(0,hexa/4)+Rand(0,hexa/4)+Rand(0,hexa/4)+Rand(0,hexa/4),Rand(0,hexb/4)+Rand(0,hexb/4)+Rand(0,hexb/4)+Rand(0,hexb/4)
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
end function

function SetG(n,c)
 NodeG(n)=c
end function

function SetH(n,h)
 NodeH(n)=h
end function

function GetG(n)
 return NodeG(n)
end function

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

function LoadMapCosts()
For a=0 To 25
  For b=0 To 25
    map_cost(a,b)=10
  next
next

for e.entity=each entity
  map_cost(e\x,e\y)=map_cost(e\x,e\y)+e\movecost
next

end function

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
	Return -1
Else
	pti1=Instr(r$,t$)
	If pti1=0 Then
		Return -2
	Else
		pti2=Instr(r$,"=",pti1)
		If pti2=0 Then
			Return -3
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


Function GetURL$(Adresse$,port=80,stripheaders=True)
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
	tcp = HttpGet(Server$,Seite$,port,Proxy_Adress$,Proxy_port%) ; the last 3 points, are for the Proxy and Ports.
	If tcp = False RuntimeError "unable to connect to address"

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
		
	Return zeile$
End Function


Function HttpGet(server$,path$,port=80,proxy$="",proxyport=0)
	Local www
	If Len(proxy$) = 0 proxy$ = server$
	If proxyport = 0 proxyport = port
	www = OpenTCPStream(proxy$,proxyport)
	If www = False Return False
	WriteLine www,"GET "+ path$ + " HTTP/1.0" + Chr$(13)+Chr$(10) + "Host: " + server$ + Chr$(13)+Chr$(10) + "User-Agent: Ibex/"+ IBEXVER + Chr$(13)+Chr$(10) + "Accept: */*" + Chr$(13)+Chr$(10)
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