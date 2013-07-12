h=CreateImage(28,24)
SetBuffer ImageBuffer(h)
Cls
Color 255,255,255
Line 0,11,6,0
Line 6,0,20,0
Line 20,0,27,11
Line 27,11,20,23
Line 20,23,6,23
Line 6,23,0,11
Print SaveImage(h,"c:\hex.bmp")
WaitKey
End