function MessageBox$( Message$, Title$, Buttons=1 )

	W = 100
	H = 70
	for A = 0 to CountItems( Message$, chr(10) ) - 1
		if stringwidth( Parse( Message$, A, chr(10) ) ) + 50 > W
			W = stringwidth( Parse( Message$, A, chr(10) ) ) + 50
		endif
	next
	setfont app\fntLabel
	H = H + CountItems( Message$, chr(10) ) * fontheight()
	
	select Buttons
		case 2
			if W < 160 W = 160
		case 3
			if W < 250 W = 250
	end select
	
	winMsgBox = Window( -1,-1, W, H, Title$, 0, true, false, false, true )
	SendMessage( winMsgBox, "WM_SETMODAL" )
	
	Label( -2, 10, Message$, "Center" )

	select Buttons
		case 1
			btnOk = Button( W / 2 - 35, H-50, 70, 20, "Ok" )
		case 2
			btnYes = Button( W / 2 - 75, H-50, 70, 20, "Yes" )
			btnNo = Button( W / 2 + 5, H-50, 70, 20, "No" )
		case 3
			btnYes = Button( W / 2 - 35 - 80 - 35, H-50, 70, 20, "Yes" )
			btnNo = Button( W / 2 - 35, H-50, 70, 20, "No" )
			btnCancel = Button( W / 2 + 45, H-50, 70, 20, "Cancel" )
	end select
	
	SetBuffer BackBuffer()
	
	Ret$ = ""
	Repeat
		UpdateGUI()
		
		Select app\Event
			case EVENT_WINDOW
				select app\WindowEvent
					case winMsgBox
						select app\WindowEventData
							case "Closed"
								Done = true
						end select
				end select
			Case EVENT_GADGET
				Select app\GadgetEvent
					case btnOk
						Done = true
						Ret$ = "Ok"
					case btnYes
						Done = true
						Ret$ = "Yes"
					case btnNo
						Done = true
						Ret$ = "No"
					case btnCancel
						Done = true
						Ret$ = "Cancel"
				end select
		end select

		DrawMouse()
		ResetEvents()

		Flip
		Cls

	Until Done=True
	UpdateGUI()

	DeleteWindow( winMsgBox )
	winMsgBox = 0
	return Ret$

end function
