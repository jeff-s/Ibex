AppTitle "Ibex 0015 - 11/24/05"

; version history
;
; 0010 - 10/12/05 - add custom guy type; multiple guys; pathfinding w/o shift key
; 0011 - 10/14/05 - redo pathfinding with packed int thingy
; 0012 - 10/15/05 - unified map data structure, more guys
; 0013 - 10/23/05 - keep track of movement points, use red hex path thingy
; 0014 - 11/02/05 - add movement structure, movement queue window, unified color representation
; 0015 - 11/13/05 - talk to server!
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

Global IBEXVER$="0015"

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
Print resp
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

Global fntTahoma=LoadFo