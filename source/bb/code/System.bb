;==================================================================
;Project Title:  exile     
;Author:         ObelardO
;Email:          obelardos@gmail.com          
;Version:        ex_10.2a   
;Date:           2.10.14   
;Notes:          exile engine system   
;==================================================================

Const EX_VARIABLE_TYPE_INTEGER = 0
Const EX_VARIABLE_TYPE_STRING = 1
Const EX_VARIABLE_TYPE_FLOAT = 2
Const EX_VARIABLE_TYPE_BOOLEAN = 3

Const EX_FILETYPE_NULL = 0
Const EX_FILETYPE_FILE = 1
Const EX_FILETYPE_DIR  = 2

Const AUTOR$   = "ObelardO"
Const VERSION$ = "ex_13.0a"
Const PRODUCT$ = "?"

;==================================================================

Function InitSystem()
	If ExCheckFileExist("base",         EX_FILETYPE_DIR) = EX_FILETYPE_NULL xCreateDir("base")
	If ExCheckFileExist("base\logs",    EX_FILETYPE_DIR) = EX_FILETYPE_NULL xCreateDir("base\logs")
	If ExCheckFileExist("base\content", EX_FILETYPE_DIR) = EX_FILETYPE_NULL xCreateDir("base\content")
	If ExCheckFileExist("base\config",  EX_FILETYPE_DIR) = EX_FILETYPE_NULL xCreateDir("base\config")
	If ExCheckFileExist("base\images",  EX_FILETYPE_DIR) = EX_FILETYPE_NULL xCreateDir("base\images")
End Function

Function ExCheckFileExist(FileName$, NeedFileType)
	If  xFileType(FileName) = NeedFileType
		Return True
	End If

	ConsoleLog("^4[WARNING]^1 Unexistig file ^5'" + ExStrWithoutColorPoints(FileName) + "'^1 was created.")
	
	Return False
End Function

Function ExSaveConfig(FileName$)
	Local ConfigFile$ = WriteFile("base\config\" + FileName)

	For EXVAR.EX_VARIABLE = Each EX_VARIABLE
		WriteLine(ConfigFile, "set " + EXVAR\Name + " " + EXVAR\Value)
	Next

	CloseFile(ConfigFile)
End Function

Function ExSmartLoad(FileName$)
	Select ExStrCut(FileName, 2, ".")
		Case "cfg"	Return ExLoadConfig(FileName)

	End Select

	Return False
End Function

Function ExLoadConfig(FileName$)
	If ExCheckFileExist("base\config\" + FileName, EX_FILETYPE_FILE) = False ExSaveConfig(FileName)

	ConfigFile$ = OpenFile("base\config\" + FileName)

	While Not Eof(ConfigFile)
		ConsoleCommand(ReadLine(ConfigFile))
	Wend

	CloseFile(ConfigFile)

	Return True
End Function

;==================================================================

Type EX_VARIABLE
	Field Name$
	Field Value$
	Field VarType
End Type

Function ExAddVariable(Name$, Value$, VarType)
	For EXVAR.EX_VARIABLE = Each EX_VARIABLE
		If ExStrEqualCheck(EXVAR\Name, Name)
			EXVAR\Value = Value
			Return False
		End If
	Next

	EXVAR.EX_VARIABLE = New EX_VARIABLE

	EXVAR\Name = Name
	EXVAR\Value = Value
	EXVAR\VarType = VarType
End Function

Function ExSetVar(Name$, Value$)
	For EXVAR.EX_VARIABLE = Each EX_VARIABLE
		If ExStrEqualCheck(EXVAR\Name, Name)
			Select EXVAR\VarType
				Case EX_VARIABLE_TYPE_INTEGER
					EXVAR\Value = Value
				Case EX_VARIABLE_TYPE_BOOLEAN
					EXVAR\Value = Value
			End Select
			
			Return True
		End If
	Next
	Return False 
End Function

Function ExGetVar$(Name$)
	For EXVAR.EX_VARIABLE = Each EX_VARIABLE
		If ExStrEqualCheck(EXVAR\Name, Name)
			Select EXVAR\VarType
				Case EX_VARIABLE_TYPE_INTEGER
					Return EXVAR\Value
				Case EX_VARIABLE_TYPE_BOOLEAN
					Return ExBoolToStr(EXVAR\Value)
				Case EX_VARIABLE_TYPE_STRING
					Return EXVAR\Value
			End Select
			
		End If
	Next
End Function

;==================================================================

Type EX_TIMEPOINT
	Field Name$
	Field Value
End Type

Function ExCaptureTime(Name$)
	EXTP.EX_TIMEPOINT = New EX_TIMEPOINT
	EXTP\Name = Name
	EXTP\Value = xMillisecs()
End Function

Function ExReleaseTime(Name$)
	For EXTP.EX_TIMEPOINT = Each EX_TIMEPOINT
		If ExStrEqualCheck(EXTP\Name, Name)
			Result = xMillisecs() - EXTP\Value
			Delete EXTP
			Return Result
		End If
	Next
End Function

;==================================================================

Type EX_COMMAND
	Field Name$
	Field Pattern$
	Field Help$
End Type

Function ExAddCommand(Name$, Pattern$, Help$)
	For EXCOM.EX_COMMAND = Each EX_COMMAND
		If ExStrEqualCheck(EXCOM\Name, Name)
			EXCOM\Pattern = Pattern
			EXCOM\Help = Help
			Return False
		End If
	Next

	EXCOM.EX_COMMAND = New EX_COMMAND
	EXCOM\Name = Name
	EXCOM\Pattern = Pattern
	EXCOM\Help = Help
End Function 

;==================================================================

Function ExStrCut$(InputString$, WordNum = 1, Seperators$ = " ")
        FoundWord  = False
        WordsFound = 0

        For CharLoop = 1 To Len(InputString$)
        	ThisChar$ = Mid$(InputString$, CharLoop, 1)
            If Instr(Seperators$, ThisChar$, 1)
            	If FoundWord
                  	WordsFound = WordsFound + 1
                        If WordsFound = WordNum
                        	Return Word$
                        Else
					Word$ = ""
					FoundWord = False
                        End If
                  End If                                                
            Else
            	FoundWord = True
                Word$ = Word$ + ThisChar$                     
            End If
        Next    

        If (WordsFound + 1) = WordNum Return Word$ Else Return ""
End Function

Function ExStrEqualCheck(InputString$, EqualString$)
	If Lower(InputString) = Lower(EqualString) Return True
	Return False
End Function

Function ExStrIsIntCheck(InputString$)
	For CharLoop = 1 To Len(InputString$)
		ThisChar$ = Mid$(InputString$, CharLoop, 1)
		IntDetected = False
		For IntChar = 48 To 57
			If Asc(ThisChar) = IntChar IntDetected = True
		Next
		If IntDetected = False Return False
	Next
	Return True
End Function

Function ExStrToInt%(InputString$)
	For CharLoop = 1 To Len(InputString$)
		ThisChar$ = Mid$(InputString$, CharLoop, 1)
		IntDetected = False
		For IntChar = 48 To 57
			If Asc(ThisChar) = IntChar IntDetected = True
		Next
		If IntDetected = False Return 0
	Next
	Return Int(InputString)
End Function

Function ExStrToBool(InputString$)
	If InputString = "1" Return True
	If InputString = "true" Return True
	Return False
End Function

Function ExBoolToStr$(InputString$)
	If InputString = "1" Return "true"
	If InputString = "true" Return "true"
	Return "false"
End Function

Function ExStrTabulation$(InputString$, Offset = 0, Seperators = " ", StringLength = 16)
	FirstWord$ = ExStrCut(InputString, 1, Seperators)
	SecndWord$ = ExStrCut(InputString, 2, Seperators)
	Return Str(String(" ", Offset) + FirstWord + String(" ", Int(StringLength - Len(FirstWord))) + " " + Seperators + " " + SecndWord)
End Function

Function ExStrWithoutColorPoints$(InputString$)
	For i = 1 To 10
		InputString = Replace(InputString, "^" + Str(i), "")
	Next	
	Return InputString
End Function

Const TEXT_ALIGN_LEFT = 0
Const TEXT_ALIGN_RIGHT = 1
Const TEXT_ALIGN_CENTER = 2

Function ExDrawColorText(InputString$, PositionX, PositionY, Font = 0, TextAlign = 0)


	
	If Font	xSetFont Font Else xSetFont ConsoleFont

	Local CurrentWidth = xStringWidth(ExStrWithoutColorPoints(InputString))

	Select TextAlign
		Case TEXT_ALIGN_RIGHT
			PositionX = PositionX - CurrentWidth
		Case TEXT_ALIGN_CENTER
			PositionX = PositionX - CurrentWidth * 0.5
	End Select
	
	xColor 0, 0, 0, 255
	xText PositionX + 1, PositionY + 1, ExStrWithoutColorPoints(InputString)
	xColor 255, 255, 255, 255

	Local CurrentString$ = ""
	Local CurrentChar$ = ""

	For i = 1 To Len(InputString)
		CurrentChar = Mid(InputString, i, 1)

		If CurrentChar = "^"
			CurrentChar = ""
		
			Select Int(Mid(InputString, i + 1, 1))
				Case 1: xColor 255, 255, 255, 255
				Case 2: xColor 255, 0, 0, 255
				Case 3: xColor 255, 128, 0, 255
				Case 4: xColor 245, 159, 58, 255;255, 255, 0, 255
				Case 5: xColor 0,255,128, 255;0, 255, 0, 255
				Case 6: xColor 0, 255, 255, 255
				Case 7: xColor 0, 0, 255, 255
				Case 8: xColor 254, 187, 5, 255
				Case 9: xColor 245, 159, 58, 128
				Default
					i = i - 1
					CurrentChar = "^"
			End Select

			i = i + 1
		End If

		xText PositionX + xStringWidth(CurrentString), PositionY, CurrentChar
		
		CurrentString = CurrentString + CurrentChar
	Next

	xColor 255, 255, 255, 255
End Function

Function ExCurveValue#(newvalue#, oldvalue#, increments)
	If increments >  1 Then oldvalue# = oldvalue# - (oldvalue# - newvalue#) / increments 
	If increments <= 1 Then oldvalue# = newvalue# 
	Return oldvalue# 
End Function