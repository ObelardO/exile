;==================================================================
;Project Title:  exile     
;Author:         ObelardO
;Email:          obelardos@gmail.com          
;Notes:          debug code-file   
;==================================================================

Const EX_DEBUG_ENABLED = 1
Const EX_DEBUG_DISABLED = 0

Global DebugMode
Global DebugDrawWay
Global DebugDrawFps
Global DebugDrawDip
Global DebugDrawMap
Global DebugDrawKey
Global DebugDrawCon
Global DebugFile$

Function UpdateVariables()
	DebugMode    = ExStrToBool(ExGetVar("d_mode"))
	DebugDrawWay = ExStrToBool(ExGetVar("d_drawway"))
	DebugDrawFps = ExStrToBool(ExGetVar("d_drawfps"))
	DebugDrawDip = ExStrToBool(ExGetVar("d_drawdip"))
	DebugDrawMap = ExStrToBool(ExGetVar("d_drawmap"))
	DebugDrawKey = ExStrToBool(ExGetVar("d_drawkey"))
	DebugDrawCon = ExStrToBool(ExGetVar("d_drawcon"))
	UseShader    = ExStrToBool(ExGetVar("r_bump"))
End Function

Function InitDebug()
	If ExStrToBool(ExGetVar("d_mode"))
	
	End If
End Function

Function DebugGetSystemInformation()
	ConsoleLog("   ^5[INFO]^1 System information:")
	ConsoleLog("")
	ConsoleLog("          ^1CPU Name       ^5" + xCPUName())
	ConsoleLog("          ^1CPU Speed      ^5" + xCPUSpeed() + " MHz")
	ConsoleLog("          ^1CPU Vendor     ^5" + xCPUVendor())
	ConsoleLog("          ^1CPU Family     ^5" + xCPUFamily())
	ConsoleLog("          ^1CPU Model      ^5" + xCPUModel())
	ConsoleLog("          ^1CPU Stepping   ^5" + xCPUStepping())
	ConsoleLog("")
	ConsoleLog("          ^1Total Phys mem ^5" + Float(xGetTotalPhysMem()/1024)  + "^1 MB")
	ConsoleLog("          ^1Total page mem ^5" + Float(xGetTotalPageMem()/1024)  + "^1 MB")
	ConsoleLog("")
	ConsoleLog("          ^1Video          ^5" + xVideoInfo())
	ConsoleLog("          ^1Video mem      ^5" + Float(xGetTotalVidLocalMem()/1024)  + "^1 MB")
	ConsoleLog("")
End Function

Function UpdateDebug()

	
	If DebugMode
		If DebugDrawFps ExDrawColorText("FPS: ^6" + xGetFPS(), 10, 10)
		If DebugDrawDip ExDrawColorText("DIP: ^6" + xDIPCounter(), 10, 30)
		If DebugDrawMap xDrawImage LevelDebugImage, 0, 0
;		; Memory information
;		xText 45,  200, "Total Phys: " + Float(xGetTotalPhysMem()/1024)  + " MB"
;		xText 45,  220, "Avail Phys: " + Float(xGetAvailPhysMem()/1024)  + " MB"
;		xText 45,  240, "Total Page: " + Float(xGetTotalPageMem()/1024)  + " MB"
;		xText 45,  260, "Avail Page: " + Float(xGetAvailPageMem()/1024)  + " MB"
;		xText 245, 200, "Used Phys: " + (Float(xGetTotalPhysMem()/1024) - Float(xGetAvailPhysMem()/1024))  + " MB"
;		xText 245, 240, "Used Page: " + (Float(xGetTotalPageMem()/1024) - Float(xGetAvailPageMem()/1024))  + " MB"
;		
		; Video system infromation
;		xText 45,  330, "Video Decription:                     " + xVideoInfo()
;		xText 45,  350, "Total Vid: " + Float(xGetTotalVidMem()/1024)  + " MB"
;		xText 45,  370, "Avail Vid: " + Float(xGetAvailVidMem()/1024)  + " MB"
;		xText 45,  390, "Total Vid Local: " + Float(xGetTotalVidLocalMem()/1024)  + " MB"
;		xText 45,  410, "Avail Vid Local: " + Float(xGetAvailVidLocalMem()/1024)  + " MB"
;		xText 45,  430, "Total Vid Nonlocal: " + Float(xGetTotalVidNonlocalMem()/1024)  + " MB"
;		xText 45,  450, "Avail Vid Nonlocal: " + Float(xGetAvailVidNonlocalMem()/1024)  + " MB"
;		xText 295, 360, "Used Vid : " + (Float(xGetTotalVidMem()/1024) - Float(xGetAvailVidMem()/1024))  + " MB"
;		xText 295, 400, "Used Vid  Local: " + (Float(xGetTotalVidLocalMem()/1024) - Float(xGetAvailVidLocalMem()/1024))  + " MB"
;		xText 295, 440, "Used Vid Nonlocal: " + (Float(xGetTotalVidNonlocalMem()/1024) - Float(xGetAvailVidNonlocalMem()/1024))  + " MB"
	Else

	End If
End Function

Function DebugLine(InputString$)
	If ExStrToBool(ExGetVar("d_mode"))
		If DebugFile
			WriteLine DebugFile, InputString
		Else
			DebugFile = WriteFile(ExGetVar("d_file"))
			WriteLine DebugFile, InputString
		End If
	End If
End Function
