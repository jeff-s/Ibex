Print "Hexagons!":Print

Print:Print "ESC to exit"

Print:Print "R-mouse button sets new starting point"

Print:Print "I suggest a size from 20 to 40.":Print
Global s=Input("length of side?")

Graphics 1024,768;,16,2

;s=80.0
Global h=Sin(30)*s
Global r=Cos(30)*s
Global b=s+2*h
Global a=2*r
Global m#=Float(h)/Float(r)

himg=CreateImage(a,b)
SetBuffer ImageBuffer(himg)
Cls
Color 255,255,255
Line r,0,a-1,h
Line a-1,h,a-1,h+s
Line a-1,h+s,r,b-1
;Line r,b-1,0,h+s
;Line 0,h+s,0,h
;Line 0,h,r,0

hrimg=CreateImage(a,b)
SetBuffer ImageBuffer(hrimg)
Cls
Color 255,0,0
Line r,0,a-1,h
Line a-1,h,a-1,h+s
Line a-1,h+s,r,b-1
Line r,b-1,0,h+s
Line 0,h+s,0,h
Line 0,h,r,0

hgimg=CreateImage(a,b)
SetBuffer ImageBuffer(hgimg)
Cls
Color 0,0,255
Line r,0,a-1,h
Line a-1,h,a-1,h+s
Line a-1,h+s,r,b-1
Line r,b-1,0,h+s
Line 0,h+s,0,h
Line 0,h,r,0

SetBuffer BackBuffer()
;DrawImage himg,20,20

cx=100:cy=100


While Not KeyHit(1)
Cls
For u=0 To 30
  For v=0 To 30
	DrawAHex(himg,u,v)
  Next
Next

msx=MouseX()
msy=MouseY()

Color 0,192,255
Line msx,msy-5,msx,msy+5
Line msx-5,msy,msx+5,msy



If MouseDown(2) Then
	cx=msx:cy=msy
End If
Color 0,255,0
Plot cx,cy



If msx<>cx Then
pm#=Float(msy-cy)/Float(msx-cx)
For ii=minof(cx,msx) To maxof(cx,msx)
	jj=cy+pm*(ii-cx)
	Color 200,200,100
	Plot ii,jj
	t$=xytohexxy(ii,jj)
	p=Instr(t,"/")
	hx=Left(t,p-1)
	hy=Mid(t,p+1)
	drawahex(hgimg,hx,hy)
Next
End If

t$=xytohexxy(msx,msy)
p=Instr(t,"/")
hx=Left(t,p-1)
hy=Mid(t,p+1)
DrawAHex(hrimg,hx,hy)

;Color 255,240,240
;Text 0,400,"mousex="+msx+", mousey="+msy+";  xsect="+xsect+", ysect="+ysect
;Text 0,420,"xsectpxl="+xsectpxl+", ysectpxl="+ysectpxl+"  (Float(h)-Float(xsectpxl)*m#)="+(Float(h)-Float(xsectpxl)*m#)
;Text 0,440,"s="+s+", h="+h+", r="+r+", m#="+m
;Text 0,460,st$+": hx="+hx+", hy="+hy
;Text 100,00,"cx="+cx+", cy="+cy+",pm="+pm
Flip
Wend
End

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function DrawAHex(i,hxx,hyy)
    xpx=hxx*a+(hyy Mod 2)*r
    ypx=hyy*(h+s)
	DrawImage i,xpx,ypx
End Function

Function xytohexxy$(msx,msy)
xsect=msx/a
ysect=msy/(h+s)

xsectpxl=msx Mod a
ysectpxl=msy Mod Int(h+s)


If ysect Mod 2=0 Then
  st$="A"
	;A-section
	hy=ysect
	hx=xsect
	If ysectpxl<(Float(h)-Float(xsectpxl)*m#) Then
		st$=st$+"ul"
		hy=ysect-1
		hx=xsect-1
	EndIf
	If ysectpxl<(-Float(h)+Float(xsectpxl)*m#) Then
		st$=st$+"ur"
		hy=ysect-1
		hx=xsect
	End If
Else
st$="B"
	;B section
	If xsectpxl>=r Then
		st$=st$+"r"
		If ysectpxl<(Float(2*h)-xsectpxl*m#) Then
			st$=st$+"a"
			hy=ysect-1
			hx=xsect;-1
		Else
			st$=st$+"b"
			hy=ysect
			hx=xsect
		EndIf
	EndIf
	If xsectpxl<r Then
		st$=st$+"l"
		If ysectpxl<(xsectpxl*m) Then
			st$=st$+"c"
			hy=ysect-1
			hx=xsect
		Else
			st$=st$+"d"
			hy=ysect
			hx=xsect-1
		End If
	End If
End If
Return ""+hx+"/"+hy
End Function

Function MinOf(a,b)
If a<b Return a
Return b
End Function

Function MaxOf(a,b)
If a>b Return a
Return b
End Function
