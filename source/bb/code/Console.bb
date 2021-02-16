;==================================================================
;Project Title:  exile     
;Author:         ObelardO
;Email:          obelardos@gmail.com          
;Version:        ex_10.2a   
;Date:           2.10.14   
;Notes:          exile console code-file   
;==================================================================

Global ConsoleMode
Global ConsoleText$
Global ConsoleTextPref$
Global ConsoleTextPost$
Global ConsoleAlpha#
Global ConsoleImage
Global ConsoleScroll
Global ConsoleLinesCount
Global ConsoleLineHeight
Global ConsoleHeight
Global ConsoleWidth
Global ConsolePosition
Global ConsoleFont
Global ConsoleCursor
Global ConsoleCursorPos
Global ConsoleCursorTime
Global ConsoleLastTime
Global ConsoleSelected

Dim ConsoleLine$(512)
Dim ConsoleCommands$(12)

Function ConsoleCommand(Command$)
	If Command = "" Or Left(Command, 2) = "//" Return False

	Prefix$ = ExStrCut(Command)

	For EXCOM.EX_COMMAND = Each EX_COMMAND
		If ExStrEqualCheck(EXCOM\Name, Prefix)
			DebugLog Command

			Select Prefix
				Case "load"
					
					If ExSmartLoad(ExStrCut(Command, 2))
						ConsoleLog("   ^5[INFO]^1 Load ^5'" + ExStrWithoutColorPoints(ExStrCut(Command, 2)) + "'^1 ok")
					Else
						ConsoleLog("^4[WARNING]^1 File ^5'" + ExStrWithoutColorPoints(ExStrCut(Command, 2)) + "'^1 does not exist!")
					End If
					
				Case "level"
					RoomCount = Int(ExStrCut(Command, 2))
					LevelSeed = Int(ExStrCut(Command, 3))

					;level debugmode
					LDM = ExStrToBool(ExStrCut(Command, 4))

					GenerateLevel(RoomCount, LevelSeed, LDM)

				Case "start"
				
					;If ExGetVar("r_width")  < 800 ExSetVar "r_width",  800
					;If ExGetVar("r_height") < 600 ExSetVar "r_height", 600
					
					Render_width  = ExStrToInt(ExGetVar("r_width"))
					Render_Height = ExStrToInt(ExGetVar("r_height"))
					Render_Depth  = ExStrToInt(ExGetVar("r_depth"))
					Render_Mode   = ExStrToInt(ExGetVar("r_mode"))
					Render_Vsync  = ExStrToBool(ExGetVar("r_vsync"))
					Render_Antial = ExStrToBool(ExGetVar("r_antial"))
					Render_Title$ = ExGetVar("r_title")

					If Render_Antial xSetAntiAliasType AA4SAMPLES
					xGraphics3D Render_width, Render_Height, Render_Depth, Render_Mode, Render_Vsync
					xSetBuffer xBackBuffer()
					xAppTitle Render_Title
					xCreateDSS 1024, 1024
					
					xAntiAlias True
					xClsColor 0, 0, 0, 0

					ResizeConsole()					
				Case "set"
					Variable$ = ExStrCut(Command, 2)
					Value$ = ExStrCut(Command, 3)
					If ExSetVar(Variable, Value)
						ConsoleLog(String(" ", Len(VERSION + ": ")) + "^1" + Variable + String(" ", 12 - Len(Variable)) + " = ^6" + ExGetVar(Variable))
					Else
						ConsoleLog("^4[WARNING]^1 Variable ^5'" + ExStrWithoutColorPoints(Variable) + "'^1 does not exist!")
					End If
					
				Case "title"
					xAppTitle ExStrCut(Command, 2)

				Case "sysinfo"
					DebugGetSystemInformation()
			

				Case "stop"
					End

			End Select

			UpdateVariables()
							
			Return False
					
		End If
	Next
	ConsoleLog("^4[WARNING]^1 Command ^5'" + ExStrWithoutColorPoints(Prefix) + "'^1 does not exist!")
End Function

Function InitConsole()
	ConsoleFont  = xLoadFont("Lucida Console", 10)
End Function

Function ResizeConsole()
	ConsoleHeight     = ExStrToInt(ExGetVar("r_height")) * 0.5
	ConsoleLineHeight = xStringHeight("console")
	ConsoleHeight     = ConsoleHeight - ConsoleHeight Mod ConsoleLineHeight
	ConsoleWidth      = ExStrToInt(ExGetVar("r_width"))
	ConsoleImage      = xCreateImage(ConsoleWidth, ConsoleHeight)
	ConsoleLinesCount = ConsoleHeight / ConsoleLineHeight
End Function

Function UpdateConsole()

	CurrentChar = xGetKey()	

	If CurrentChar = 184 Or CurrentChar = 96
		ConsoleMode = 1 - ConsoleMode
		CurrentChar = 0 
	End If

	If ConsoleMode = 1

		xSetBuffer xImageBuffer(ConsoleImage)
		xSetFont ConsoleFont
		xCls
		xColor 0, 0, 0, 225
		xRect 0, 0, ConsoleWidth, ConsoleHeight, True
	
		If ConsoleCursorTime < xMillisecs()
			ConsoleCursorTime = xMillisecs() + 250
			ConsoleCursor = 1 - ConsoleCursor
		End If
	
		ExDrawColorText("^1" + VERSION + ": " + ConsoleTextPref + ConsoleTextPost, 10, ConsoleHeight - ConsoleLineHeight)

		If ConsoleCursor
			xColor 255, 255, 255, 255
			EnteredTextRealWidth = xStringWidth(ExStrWithoutColorPoints("^1" + VERSION + ": " + ConsoleTextPref))
			xRect EnteredTextRealWidth + 10, ConsoleHeight - ConsoleLineHeight - 1, 10, 14, True

			If ConsoleTextPost <> ""
				xColor 0, 0, 0, 255
				xText EnteredTextRealWidth + 10, ConsoleHeight - ConsoleLineHeight, Left(ConsoleTextPost, 1)
			End If
		End If 
	
		For i = 0 + ConsoleScroll To ConsoleLinesCount + ConsoleScroll - 1
			ExDrawColorText(ConsoleLine(i), 10, ConsoleHeight - ConsoleLineHeight * (i + 2 - ConsoleScroll))
		Next
	
		xColor 255, 255, 255, 255
	
		If xKeyDown(KEY_BACKSPACE)
			If xMillisecs() > ConsoleLastTime
				If Not ConsoleTextPref = "" ConsoleTextPref = Left(ConsoleTextPref, Len(ConsoleTextPref) - 1)
				ConsoleLastTime = xMillisecs() + 80
			End If
			CurrentChar = 0
		End If
	
		If xKeyHit(KEY_ENTER)
			For i = 12 To 1 Step -1
				ConsoleCommands(i) = ConsoleCommands(i - 1)
			Next
			ConsoleCommands(0) = ConsoleTextPref + ConsoleTextPost
		
			ConsoleCommand(ConsoleTextPref + ConsoleTextPost)
			ConsoleText = ""
			ConsoleTextPref = ""
			ConsoleTextPost = ""
			CurrentChar = 0
			ConsoleSelected = 0
		End If

		If xKeyHit(KEY_UP)
			ConsoleTextPref = ConsoleCommands(ConsoleSelected)
			ConsoleTextPost = ""
			ConsoleSelected = ConsoleSelected + 1
			If ConsoleSelected > 12   ConsoleSelected = 0
			If ConsoleCommands(ConsoleSelected) = "" ConsoleSelected = 0
		End If

		If xKeyHit(KEY_DOWN)
			ConsoleTextPref = ConsoleCommands(ConsoleSelected)
			ConsoleTextPost = ""
			ConsoleSelected = ConsoleSelected - 1
			If ConsoleSelected < 0 ConsoleSelected = 12
		End If

		If xKeyDown(KEY_LEFT)
			If xMillisecs() > ConsoleLastTime
				If ConsoleTextPref <> ""
					ConsoleTextPost = Right(ConsoleTextPref, 1) + ConsoleTextPost
					ConsoleTextPref = Left(ConsoleTextPref, Len(ConsoleTextPref) - 1)
					ConsoleLastTime = xMillisecs() + 80
				End If
			End If
			CurrentChar = 0
		End If

		If xKeyDown(KEY_RIGHT)
			If xMillisecs() > ConsoleLastTime
				If ConsoleTextPost <> ""
					ConsoleTextPref = ConsoleTextPref + Left(ConsoleTextPost, 1)
					ConsoleTextPost = Right(ConsoleTextPost, Len(ConsoleTextPost) - 1)
					ConsoleLastTime = xMillisecs() + 80
				End If
			End If
			CurrentChar = 0
		End If
	
		If CurrentChar >= 32 And CurrentChar =< 128
			ConsoleTextPref = ConsoleTextPref + Chr(CurrentChar)
			ConsoleLastTime = xMillisecs()
		End If
	
		;xFlushKeys()

		xColor 255, 255, 255, 255
		ScroolW = 8
		ScrollX = ConsoleWidth - ScroolW
		ScrollH = ConsoleHeight * ConsoleLinesCount / 512
		ScrollY = ConsoleHeight - ScrollH - ConsoleScroll * ConsoleHeight / 512
		xRect ScrollX, ScrollY, ScroolW, ScrollH, 1

		MouseZspd = xMouseZSpeed() 

		If MouseZspd = 1
			If xMouseDown(3) ScrollStep = 10 Else ScrollStep = 1
			ConsoleScroll = ConsoleScroll + ScrollStep
			If ConsoleScroll > (512 - ConsoleLinesCount) ConsoleScroll = (512 - ConsoleLinesCount)
		End If

		If MouseZspd = -1
			If xMouseDown(3) ScrollStep = 10 Else ScrollStep = 1
			ConsoleScroll = ConsoleScroll - ScrollStep
			If ConsoleScroll < 0 ConsoleScroll = 0
		End If
		
		xSetBuffer xBackBuffer()
	
		xDrawImage ConsoleImage, 0, 0

	End If
End Function

Function ConsoleLog(InputString$)
	For i = 512 To 1 Step -1
		ConsoleLine(i) = ConsoleLine(i - 1)
	Next

	ConsoleLine(0) = InputString

	DebugLine(ExStrWithoutColorPoints(InputString))
	DebugLog ExStrWithoutColorPoints(ConsoleLine(0))
End Function