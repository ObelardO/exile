;==================================================================
;Project Title:  exile     
;Author:         ObelardO
;Email:          obelardos@gmail.com          
;Date:           06.02.15
;UPD Date:       16.02.21
;version:        ex_14.0a
;Notes:          Main code-file
;==================================================================

Dim PlayerSkillName$(PlayerSkillCount)

Include "code\engine.bb"
Include "code\System.bb"
Include "code\Console.bb"
Include "code\Debug.bb"									
Include "code\Variables.bb"
Include "code\Commands.bb"
Include "code\Level.bb"
Include "code\Gui.bb"
Include "code\Camera.bb"
Include "code\Player.bb"
Include "code\chest.bb"
Include "code\Enemy.bb" 

Const GameStatus_MainMenu  = 0
Const GameStatus_InGame    = 1
Const GameStatus_Pause     = 2
Const GameStatus_LevelUp   = 3
Const GameStatus_NextLevel = 4
Const GameStatus_GameOver  = 5
Const GameStatus_MenuAbout = 6

Global GameStatus

InitSystem()
InitDebug()
InitGame()
InitConsole()	
InitGui()
InitCamera()
InitLevel()
InitEnemys()
InitPlayer()
InitChest()

While Not xWinMessage("WM_CLOSE")
	UpdateGame()
	xFlip
Wend

Function InitGame()
	ExCaptureTime("start")
	DebugGetSystemInformation()
	ConsoleLog("   ^5[INFO]^1 Game starting...")
	
	xSetEngineSetting("Splash::TilingTime", 0);1
	xSetEngineSetting("Splash::AfterTilingTime", 0);3
	xKey("M621B-533Ky-932nf-76e5s-M128Z")

	SeedRnd(xMillisecs())

	If CommandLine() = "run"
		ConsoleCommand("load default.cfg")
		ConsoleCommand("start")
	Else
		Initlauncher()
	End If

	ConsoleLog("   ^5[INFO]^1 Game started in ^6" + ExReleaseTime("start") + "^1 (ms)")
End Function

Function UpdateGame()
	Select GameStatus
		Case GameStatus_MainMenu
			GuiDrawMainMenu()
			UpdateDebug()
			UpdateConsole()
			UpdateChannels(1)
	 	Case GameStatus_InGame
			UpdateCamera()
			UpdateDoors()
			UpdateChests()
			UpdateItems()
			UpdateLevel()
			UpdateEnemys()
			UpdatePlayer()
			UpdateDebug()
			UpdateGui()
			UpdateConsole()
			xUpdateWorld()
			UpdateChannels(0)
		Case GameStatus_Pause
			UpdateCamera()
			UpdatePlayerInterface()
			GuiDrawPauseMenu()
			UpdateGui()
			UpdateDebug()
			UpdateConsole()
		Case GameStatus_LevelUp
			UpdateCamera()
			UpdatePlayerInterface()
			GuiDrawLevelUpMenu()
			UpdateDebug()
			UpdateConsole()
		Case GameStatus_NextLevel
			UpdateCamera()
			UpdatePlayerInterface()
			GuiDrawNextLevelMenu()
			UpdateDebug()
			UpdateConsole()
		Case GameStatus_GameOver
			UpdateCamera()
			UpdatePlayerInterface()
			GuiDrawGameOverMenu()
			UpdateDebug()
			UpdateConsole()
		Case GameStatus_MenuAbout
			GuiDrawAboutMenu()
			UpdateDebug()
			UpdateConsole()
			UpdateChannels(1)
	End Select
End Function

Function StartNewGame()

End Function

Function SaveGame()
	SaveFile = WriteFile("base\saves\save.txt")

	WriteInt   SaveFile, PlayerLevel
	WriteFloat SaveFile, Player_Health
	WriteFloat SaveFile, PlayerManaReserve
	WriteFloat SaveFile, PlayerTotalXP
	WriteInt   SaveFile, PlayerAddPower
	WriteInt   SaveFile, PlayerAddSpeed
	WriteInt   SaveFile, PlayerAddProtect
	WriteInt   SaveFile, PlayerAddAttack
	WriteInt   SaveFile, PlayerAddMana
	WriteLine  SaveFile, (LevelMinRoomCount + LevelCounter) + " " + LevelCurSeed

	For Item.tItem = Each tItem
		If Item\InInvetory WriteLine SaveFile, Item\ItemType\ID + " " + Item\UsePoints + " " + GetPlayerSlotID(Item\SlotX + 10, Item\SlotY + 10)  
	Next

	CloseFile SaveFile
End Function

Function LoadGame()
	Local ReadedLine$
	Local LevelSeed$
	
	LevelCounter = 0
	ResetPlayer()
	ClearLevel()

	SaveFile = OpenFile("base\saves\save.txt")

	If SaveFile
	
		PlayerLevel       = ReadInt(SaveFile)
		Player_Health     = ReadFloat(SaveFile)
		PlayerManaReserve = ReadFloat(SaveFile)
		PlayerTotalXP     = ReadFloat(SaveFile)	
		PlayerAddPower    = ReadInt(SaveFile)
		PlayerAddSpeed    = ReadInt(SaveFile)
		PlayerAddProtect  = ReadInt(SaveFile)
		PlayerAddAttack   = ReadInt(SaveFile)
		PlayerAddMana     = ReadInt(SaveFile)
		LevelSeed         = ReadLine(SaveFile)
		LevelCounter      = Int(ExStrCut(LevelSeed, 1)) - LevelMinRoomCount

		While Not Eof(SaveFile)
			ReadedLine = ReadLine(SaveFile)
			
			RegisterItem(ExStrCut(ReadedLine, 1),  Null, True, ExStrCut(ReadedLine, 2), ExStrCut(ReadedLine, 3))
		Wend

		ConsoleCommand("level " + LevelSeed)
	Else
		xCls
		ExDrawColorText("you have to start a new game", WindowWidth / 2, WindowHeight / 2, Fnt_s, TEXT_ALIGN_CENTER)
		xFlip
		Delay 1000
	End If
End Function

Function Initlauncher()
	xAppWindowFrame False
	xAppTitle "exile - launcher"
	xSetAntiAliasType AA9SAMPLES 
	xGraphics3D 600, 400, 32, False, True

	Local BackGroundImg = xLoadImage("base\images\background.png")
	xResizeImage BackGroundImg, 600, 400

	Local LogoImg = xLoadImage("base\images\logo_s.png")
	xMidHandle LogoImg

	Local RaildImg = xLoadImage("base\images\rails.png")
	Local TumblrImg = xLoadAnimImage("base\images\tumbler.png", 25, 25, 0, 2)
	Local ButtonImg = xLoadAnimImage("base\images\button.png", 160, 40, 0, 2)

	Local ClickSound = xLoadSound("base\sounds\ui\tick.ogg")

	Local OpenSound = xLoadSound("base\sounds\intro\vocal.ogg")
	xPlaySound OpenSound

	Local MouseHitL
	Local MouseHitR
	Local MousePosX
	Local MousePosY
    
	Local stp_WWidth
	Local stp_WHeight
	Local stp_WMode
	Local stp_WDepth
	Local stp_VSync
	Local stp_antial
	Local stp_Title$
	Local stp_Dmode
	Local stp_Dfile$
	Local stp_Dfps
	Local stp_Dmap
	Local stp_Ddip
	Local stp_Dway
	Local stp_Dkey
	Local stp_Dcon
	Local stp_Bump
	
	Local ConfigFile$
	Local CurrentLine$

	Local Parametr$
	Local strValue$
	Local intValue$

	Local CurGfxMode

	Local ResStrWidth

	Local FntDefault = xLoadFont("base\fonts\DARK EMPIRE.ttf", 15, False, False, False): xSetFont FntDefault
	;Local FntDefault = xLoadFont("Segoe UI", 12): xSetFont FntDefault
	
	ConfigFile = ReadFile("base\config\default.cfg")

	If ConfigFile
		While Not Eof(ConfigFile)
			CurrentLine = ReadLine(ConfigFile)

			Parametr = ExStrCut(CurrentLine, 2)
			strValue = ExStrCut(CurrentLine, 3)
			intValue = Int(strValue)
		
			Select Lower(Parametr)
				Case "r_width"   stp_WWidth  = intValue
				Case "r_height"  stp_WHeight = intValue
				Case "r_depth"   stp_WDepth  = intValue
				Case "r_mode"    stp_WMode   = intValue
				Case "r_vsync"   stp_VSync   = intValue
				Case "r_title"   stp_Title   = strValue
				Case "r_antial"  stp_antial  = intValue
				Case "d_mode"    stp_Dmode   = intValue
				Case "d_file"    stp_Dfile   = strValue
				Case "d_drawfps" stp_Dfps    = intValue
				Case "d_drawmap" stp_Dmap    = intValue
				Case "d_drawdip" stp_Ddip    = intValue
				Case "d_drawway" stp_Dway    = intValue
				Case "d_drawkey" stp_Dkey    = intValue
				Case "d_drawcon" stp_Dcon    = intValue
				Case "r_bump"    stp_Bump    = intValue
			End Select

		Wend

		If stp_antial stp_antial = 1

		CloseFile ConfigFile
	Else
		ExecFile("exile.exe run")
		End
	End If

	While Not (xKeyDown(KEY_ESCAPE) Or xWinMessage("WM_CLOSE"))
		xDrawImage BackGroundImg, 0, 0
		xDrawImage LogoImg, 300, 75

		MouseHitL = xMouseHit(MOUSE_LEFT)
		MouseHitR = xMouseHit(MOUSE_RIGHT)
		MousePosX = xMouseX()
		MousePosY = xMouseY()

		If MousePosX > 320 And MousePosX < 400
		If MousePosY > 140 And MousePosY < 170
			If MouseHitL
				CurGfxMode = CurGfxMode + 1
				If CurGfxMode = xCountGfxModes() CurGfxMode = 0
				stp_WWidth =  xGfxModeWidth(CurGfxMode)
				stp_WHeight = xGfxModeHeight(CurGfxMode)
			End If

			If MouseHitR
				CurGfxMode = CurGfxMode - 1
				If CurGfxMode = -1 CurGfxMode =  xCountGfxModes() - 1
				stp_WWidth =  xGfxModeWidth(CurGfxMode)
				stp_WHeight = xGfxModeHeight(CurGfxMode)
			End If

			ResStrWidth = xStringWidth(stp_WWidth + " x " + stp_WHeight)

			xColor 122, 80, 29, 255
			xRect 350 - ResStrWidth * 0.5, 140, ResStrWidth, 24, True
			xOval 350 - ResStrWidth * 0.5 - 12, 140, 24, 24, True
			xOval 350 + ResStrWidth * 0.5 - 12, 140, 24, 24, True
		End If
		End If

		stp_WMode  = LauncherDrawSwicher(320, 170, stp_WMode,  MousePosX, MousePosY, MouseHitL, RaildImg, TumblrImg, ClickSound)
		stp_antial = LauncherDrawSwicher(320, 200, stp_antial, MousePosX, MousePosY, MouseHitL, RaildImg, TumblrImg, ClickSound)
		stp_VSync  = LauncherDrawSwicher(320, 230, stp_VSync,  MousePosX, MousePosY, MouseHitL, RaildImg, TumblrImg, ClickSound)
		stp_Bump   = LauncherDrawSwicher(320, 260, stp_Bump,     MousePosX, MousePosY, MouseHitL, RaildImg, TumblrImg, ClickSound)

		ExDrawColorText "^8 resolution", 280, 140, FntDefault, TEXT_ALIGN_RIGHT
		ExDrawColorText "^8 fullscreen", 280, 170, FntDefault, TEXT_ALIGN_RIGHT
		ExDrawColorText "^8 antialias",  280, 200, FntDefault, TEXT_ALIGN_RIGHT
		ExDrawColorText "^8 vsync",      280, 230, FntDefault, TEXT_ALIGN_RIGHT
		ExDrawColorText "^8 shaders",    280, 260, FntDefault, TEXT_ALIGN_RIGHT

		ExDrawColorText "^8" + stp_WWidth + " ^1x ^8" + stp_WHeight, 350, 140, FntDefault, TEXT_ALIGN_CENTER

		xDrawImage ButtonImg, 100, 330, 0
		xDrawImage ButtonImg, 340, 330, 0

		If PointInRegion(MousePosX, MousePosY, 100, 330, 160, 40)
			xDrawImage ButtonImg, 100, 329, 1
			If MouseHitL
				xPlaySound ClickSound
				End
			End If
		End If

		If PointInRegion(MousePosX, MousePosY, 340, 330, 160, 40)
			xDrawImage ButtonImg, 340, 329, 1
			If MouseHitL
				xPlaySound ClickSound

				ConfigFile = WriteFile("base\config\default.cfg")

				WriteLine ConfigFile, "set r_width " + stp_WWidth
				WriteLine ConfigFile, "set r_height " + stp_WHeight
				WriteLine ConfigFile, "set r_depth " + stp_WDepth
				WriteLine ConfigFile, "set r_mode " + stp_WMode
				WriteLine ConfigFile, "set r_vsync " + stp_VSync
				WriteLine ConfigFile, "set r_title " + stp_Title
				WriteLine ConfigFile, "set r_antial " + stp_antial
				WriteLine ConfigFile, "set d_file " + stp_Dmode
				WriteLine ConfigFile, "set d_mode " +  stp_Dfile
				WriteLine ConfigFile, "set d_drawfps " + stp_Dfps
				WriteLine ConfigFile, "set d_drawmap " + stp_Dmap
				WriteLine ConfigFile, "set d_drawdip " + stp_Ddip
				WriteLine ConfigFile, "set d_drawway " + stp_Dway
				WriteLine ConfigFile, "set d_drawkey " + stp_Dkey
				WriteLine ConfigFile, "set d_drawcon " + stp_Dcon
				WriteLine ConfigFile, "set r_bump " +  stp_Bump
							
				CloseFile ConfigFile
			
				ExecFile("exile.exe run")
				End
			End If
		End If

		ExDrawColorText "^8exit", 180, 335, FntDefault, TEXT_ALIGN_CENTER
		ExDrawColorText "^8play", 420, 335, FntDefault, TEXT_ALIGN_CENTER

		xFlip
	Wend

	End
End Function

Function LauncherDrawSwicher(PosX, PosY, Variable, MousePosX, MousePosY, MouseHitL, RaildImg, TumblrImg, Sound)
	SwicherW = 60

	If Variable
		xDrawImage RaildImg, PosX, PosY
		xDrawImage TumblrImg, PosX + SwicherW - 25, PosY, 1
	Else
		xDrawImage RaildImg, PosX, PosY
		xDrawImage TumblrImg, PosX, PosY, 0
	End If

	If MousePosX > PosX And MousePosX < PosX + SwicherW
	If MousePosY > PosY And MousePosY < PosY + 25
		If MouseHitL
			xPlaySound Sound
			Return Not Variable
		End If
	End If
	End If

	Return Variable
End Function

End





