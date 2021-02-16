;==================================================================
;Project Title:  ?     
;Author:         ObelardO
;Email:          obelardos@gmail.com          
;Date:           06.02.15
;Notes:          GUI code-file   
;==================================================================

Global Img_BackGround
Global Img_Invantar
Global Img_Frame
Global Img_progess
Global Img_flask
Global Img_Button_l
Global Img_Loading
Global Img_levelUp
Global Img_Overlay
Global Img_Intro
Global Img_Logo
Global Img_LogoShadow
Global ShadowAlpha#
Global Loading_W
Global ButtonL_W
Global ButtonL_H
Global QuickButtons
Global ForceDrawKey = True

Global Fnt_s
Global Fnt_m
Global Fnt_l
Global Fnt_g

Global WindowWidth
Global WindowHeight

Global MouseDownL
Global MouseHitR
Global MouseHitL
Global MousePosX
Global MousePosY

Global Sound_UiTick
Global Sound_UiTack
Global Sound_MenuGameOver
Global Sound_MenuLevelUp
Global Sound_MenuLevelEnd
Global Sound_Intro

Global Sound_MainTheme
Global Sound_MainAmbient
Global Channel_MainTheme
Global Channel_MainAmbient
Global Channel_MainThemeVolume#
Global Channel_MainAmbientVolume#

Function InitGui()
	Local ScaleFactor#

	WindowWidth = xGraphicsWidth()
	WindowHeight = xGraphicsHeight()

	Img_BackGround = xLoadImage("base\images\background.png")
	xMidHandle Img_BackGround
	ScaleFactor = Float(WindowWidth) / xImageWidth(Img_BackGround)
	xScaleImage Img_BackGround, ScaleFactor, ScaleFactor

	Img_Frame = xLoadAnimImage("base\images\frame.png", 25, 25, 0, 8)

	Fnt_s = xLoadFont("base\fonts\Flibustier.ttf", 10, False, False, False)
	Fnt_m = xLoadFont("base\fonts\DARK EMPIRE.ttf", 25, False, False, False)
	Fnt_l = xLoadFont("base\fonts\DARK EMPIRE.ttf", 40, False, False, False)
	Fnt_g = xLoadFont("base\fonts\DARK EMPIRE.ttf", 180, False, False, False)

	Img_progess = xLoadImage("base\images\progres_item.png")
	Img_flask = xLoadImage("base\images\bar.png")

	Img_Button_l = xLoadAnimImage("base\images\largebuttons.png", 355, 54, 0, 3)
	xMidHandle Img_Button_l
	ButtonL_W = xImageWidth(Img_Button_l)
	ButtonL_H = xImageHeight(Img_Button_l)

	Img_Loading = xLoadImage("base\images\bar.png")
	Loading_W = xImageWidth(Img_Loading)

	QuickButtons = xLoadAnimImage("base\images\quickbuttons.png", 150, 80, 0, 4)

	Img_levelUp = xLoadImage("base\images\levelup.png")

	Sound_UiTick = xLoadSound("base\sounds\ui\tick.ogg")
	Sound_UiTack = xLoadSound("base\sounds\ui\tack.ogg")

	Sound_MenuGameOver = xLoadSound("base\sounds\menu\gameover.wav")
	Sound_MenuLevelUp  = xLoadSound("base\sounds\menu\levelup.wav")
	Sound_MenuLevelEnd = xLoadSound("base\sounds\menu\levelend.wav")

	Img_Overlay = xLoadImage("base\images\overlay.png")
	xResizeImage Img_Overlay, WindowWidth, WindowHeight

	Sound_MainTheme = xLoadSound("base\music\MainTheme.ogg")
	xLoopSound Sound_MainTheme
	Channel_MainTheme = xPlaySound(Sound_MainTheme)
	xChannelVolume Channel_MainTheme, 0

	Sound_MainAmbient = xLoadSound("base\music\StaticMotion.ogg")
	xLoopSound Sound_MainAmbient
	Channel_MainAmbient = xPlaySound(Sound_MainAmbient)
	xChannelVolume Channel_MainAmbient, 0

	Img_Intro = xLoadImage("base\textures\intro.jpg")
	xHandleImage  Img_Intro, 550, 0

	Sound_Intro = xLoadSound("base\sounds\intro\story.ogg")

	Img_Logo = xLoadImage("base\images\logo.png")
	xMidHandle  Img_Logo;, xImageWidth(Img_Logo) / 2, 0

	Img_LogoShadow = xLoadImage("base\images\Logo_shadow.png")
	xMidHandle  Img_LogoShadow;, xImageWidth(Img_LogoShadow) / 2, 0
End Function

Function UpdateGui()
	If xKeyHit(KEY_ESCAPE)
		If GameStatus = GameStatus_Pause GameStatus = GameStatus_InGame Else GameStatus = GameStatus_Pause
	End If
End Function

Function GuiDrawMainMenu()
	MouseHitL = xMouseHit(MOUSE_LEFT)
	MouseDownL = xMouseDown(MOUSE_LEFT)
	MousePosX = xMouseX()
	MousePosY = xMouseY()

	xImageAlpha Img_LogoShadow, (Sin(MilliSecs()*.05) + 1) / 2
	xDrawImage Img_BackGround, WindowWidth / 2, WindowHeight / 2
	xDrawImage Img_LogoShadow, WindowWidth / 2, WindowHeight / 4
	xDrawImage Img_Logo, WindowWidth / 2, WindowHeight / 4

	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight - 70 * 5, "new game")
		DrawIntro()
		;GuiDrawMainMenu()
		;DrawFade(WindowWidth / 2, WindowHeight - 70 * 5)
		LevelCounter = 0
		ResetPlayer()
		RegisterItem(02,  Null, True, 30)
		RegisterItem(03,  Null, True, 30)
		GenerateLevel(LevelMinRoomCount + LevelCounter)
		Return 
	End If

	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight  - 70 * 4, "continue")
		GuiDrawMainMenu()
		DrawFade(WindowWidth / 2, WindowHeight  - 70 * 4)
		;xPauseChannel Sound_MainTheme
		;xResumeChannel Sound_MainAmbient
		LoadGame()
		Return 
	End If
	
	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight - 70 * 3, "about")
		GameStatus = GameStatus_MenuAbout
		Return
	End If

	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight - 70 * 2, "exit")
		GuiDrawMainMenu()
		DrawFade(WindowWidth / 2, WindowHeight - 70 * 2)
		End
	End If
End Function

Function GuiDrawPauseMenu()
	MouseHitL = xMouseHit(MOUSE_LEFT)
	MousePosX = xMouseX()
	MousePosY = xMouseY()

	xColor 0, 0, 0, 196
	xRect 0, 0, WindowWidth, WindowHeight, True

	xImageAlpha Img_LogoShadow, (Sin(MilliSecs()*.05) + 1) / 2
	xDrawImage Img_LogoShadow, WindowWidth / 2, WindowHeight / 4
	xDrawImage Img_Logo, WindowWidth / 2, WindowHeight / 4

	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight - 70 * 5, "continue")
		GameStatus = GameStatus_InGame
		Return 
	End If

	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight  - 70 * 4, "exit")
		GuiDrawPauseMenu()
		DrawFade(WindowWidth / 2, WindowHeight  - 70 * 4)
		PlayerInPortal = False
		ClearLevel()
		GameStatus = GameStatus_MainMenu
		;xPauseChannel Sound_MainAmbient 
		;xResumeChannel Sound_MainTheme
	End If
End Function

Function GuiDrawLoading(Progress#)
	xCls
	xSetBuffer xBackBuffer()
	;xDrawImage Img_BackGround, 0, 0
	xColor 169,0,0, 200
	xRect (WindowWidth - Loading_W) / 2 + 39, WindowHeight / 2  + 69, Progress * 127, 13, True
	xDrawImage Img_Loading, (WindowWidth - Loading_W) / 2, WindowHeight / 2 + 50
	ExDrawColorText("loading", WindowWidth / 2, WindowHeight / 2, Fnt_s, TEXT_ALIGN_CENTER)
	ExDrawColorText("^4dungeon " + LevelCounter, WindowWidth / 2, WindowHeight / 2 + 30, Fnt_s, TEXT_ALIGN_CENTER)
	xFlip
End Function

Function GuiDrawLargeButton(x, y, Label$)
	
	If PointInRegion(MousePosX, MousePosY, x - ButtonL_W * 0.5, y - ButtonL_H * 0.5, ButtonL_W, ButtonL_H)
		If MouseHitL
			xPlaySound Sound_UiTick
			Return True
		Else
			xDrawImage Img_Button_l, x, y, 2
		End If
	Else
		xDrawImage Img_Button_l, x, y, 1
	End If

	ExDrawColorText("^8" + Label, x, y - 25, Fnt_m, TEXT_ALIGN_CENTER)
End Function

Function GuiDrawLevelUpMenu()
	MouseHitL = xMouseHit(MOUSE_LEFT)
	MousePosX = xMouseX()
	MousePosY = xMouseY()

	xColor 0, 0, 0, 196
	xRect 0, 0, WindowWidth, WindowHeight, True

	xDrawImage Img_levelUp, PlayerInvImageX, PlayerInvImageY

	ExDrawColorText("^1LEVEL UP", WindowWidth / 2, WindowHeight * 0.1, Fnt_l, TEXT_ALIGN_CENTER)
	ExDrawColorText("^4You are now level ^1" + PlayerLevel, WindowWidth / 2, PlayerInvImageY + 30, Fnt_m, TEXT_ALIGN_CENTER)
	ExDrawColorText("^9Choose a skill for improve:", WindowWidth / 2, PlayerInvImageY + 60, Fnt_m, TEXT_ALIGN_CENTER)
	
	ExDrawColorText("^4" + PlayerSkillName(PlayerSkill_Power)  + ":", PlayerInvImageX + 80, PlayerInvImageY + 110, Fnt_s)
	ExDrawColorText("^4" + PlayerSkillName(PlayerSkill_Speed)  + ":", PlayerInvImageX + 80, PlayerInvImageY + 135, Fnt_s)
	ExDrawColorText("^4" + PlayerSkillName(PlayerSkill_Protect)+ ":", PlayerInvImageX + 80, PlayerInvImageY + 160, Fnt_s)
	ExDrawColorText("^4" + PlayerSkillName(PlayerSkill_Attack) + ":", PlayerInvImageX + 80, PlayerInvImageY + 185, Fnt_s)
	ExDrawColorText("^4" + PlayerSkillName(PlayerSkill_Mana)   + ":", PlayerInvImageX + 80, PlayerInvImageY + 210, Fnt_s)

	ExDrawColorText("^4" + (PlayerAddPower + 1),  PlayerInvImageX + 305, PlayerInvImageY + 110 ,Fnt_s, TEXT_ALIGN_RIGHT)
	ExDrawColorText("^4" + (PlayerAddSpeed + 1),  PlayerInvImageX + 305, PlayerInvImageY + 135, Fnt_s, TEXT_ALIGN_RIGHT)
	ExDrawColorText("^4" + (PlayerAddProtect + 1),PlayerInvImageX + 305, PlayerInvImageY + 160, Fnt_s, TEXT_ALIGN_RIGHT)
	ExDrawColorText("^4" + (PlayerAddAttack + 1), PlayerInvImageX + 305, PlayerInvImageY + 185, Fnt_s, TEXT_ALIGN_RIGHT)
	ExDrawColorText("^4" + (PlayerAddMana + 1),   PlayerInvImageX + 305, PlayerInvImageY + 210, Fnt_s, TEXT_ALIGN_RIGHT)

	xColor 245, 159, 58, 36

	For i = 0 To 4
		If PointInRegion(MousePosX, MousePosY, PlayerInvImageX + 80, PlayerInvImageY + 110 + i * 25, 225, 20)
			xRect PlayerInvImageX + 70, PlayerInvImageY + 105 + i * 25, 245, 25, True

			If MouseHitL PlayerSkillToUp = (i + 1) xPlaySound Sound_UiTick
		End If
	Next

	If PlayerSkillToUp
		ExDrawColorText("^1 + 1",  PlayerInvImageX + 305, PlayerInvImageY + 85 + PlayerSkillToUp * 25 ,Fnt_s)
	
		If GuiDrawLargeButton(WindowWidth / 2, PlayerInvImageY + 300, "done")
			Select PlayerSkillToUp
				Case 1 PlayerAddPower   = PlayerAddPower   + 1
				Case 2 PlayerAddSpeed   = PlayerAddSpeed   + 1
				Case 3 PlayerAddProtect = PlayerAddProtect + 1
				Case 4 PlayerAddAttack  = PlayerAddAttack  + 1
				Case 5 PlayerAddMana    = PlayerAddMana    + 1
			End Select
		
			PlayerSkillToUp   = 0
			PlayerTotalXP     = 0
			Player_Health     = 100
			PlayerManaReserve = 100
			GameStatus        = GameStatus_InGame

			UpdatePlayerStats()
		End If
	End If
End Function

Function GuiDrawNextLevelMenu()
	MouseHitL = xMouseHit(MOUSE_LEFT)
	MousePosX = xMouseX()
	MousePosY = xMouseY()

	xColor 0, 0, 0, 196
	xRect 0, 0, WindowWidth, WindowHeight, True

	ExDrawColorText("^1DUNGEON ^4" + LevelCounter + " ^1COMPLETED", WindowWidth / 2, WindowHeight * 0.1, Fnt_l, TEXT_ALIGN_CENTER)

	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight - 70 * 5, "next level")
		GuiDrawNextLevelMenu()
		DrawFade(WindowWidth / 2, WindowHeight - 70 * 5)
		If Player_Health < 20 Player_Health = 20
		PlayerInPortal = False
		GenerateLevel(LevelMinRoomCount + LevelCounter)
		Return 
	End If

	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight  - 70 * 4, "continue")
		GameStatus = GameStatus_InGame
		Return 
	End If
	
	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight - 70 * 3, "exit")
		GuiDrawNextLevelMenu()
		DrawFade(WindowWidth / 2, WindowHeight - 70 * 3)
		GenerateLevel(LevelMinRoomCount + LevelCounter)
		ClearLevel()
		GameStatus = GameStatus_MainMenu
		;xPauseChannel Sound_MainAmbient 
		;xResumeChannel Sound_MainTheme
	End If
End Function

Function GuiDrawGameOverMenu()
	MouseHitL = xMouseHit(MOUSE_LEFT)
	MousePosX = xMouseX()
	MousePosY = xMouseY()

	xColor 0, 0, 0, 196
	xRect 0, 0, WindowWidth, WindowHeight, True

	ExDrawColorText("^1YOU DIED", WindowWidth / 2, WindowHeight * 0.1, Fnt_l, TEXT_ALIGN_CENTER)

	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight - 70 * 5, "try again")
		GuiDrawGameOverMenu()
		DrawFade(WindowWidth / 2, WindowHeight - 70 * 5)
		LoadGame()
		Return 
	End If

	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight  - 70 * 4, "exit")
		GuiDrawGameOverMenu()
		DrawFade(WindowWidth / 2, WindowHeight - 70 * 4)
		PlayerInPortal = False
		ClearLevel()
		GameStatus = GameStatus_MainMenu
	;	xPauseChannel Sound_MainAmbient 
		;xResumeChannel Sound_MainTheme
	End If
End Function

Function GuiDrawAboutMenu()
	MouseHitL = xMouseHit(MOUSE_LEFT)
	MousePosX = xMouseX()
	MousePosY = xMouseY()

	xImageAlpha Img_LogoShadow, (Sin(MilliSecs()*.05) + 1) / 2
	xDrawImage Img_BackGround, WindowWidth / 2, WindowHeight / 2
	xColor 0, 0, 0, 196
	xRect 0, 0, WindowWidth, WindowHeight, True
	xDrawImage Img_LogoShadow, WindowWidth / 2, WindowHeight / 4
	xDrawImage Img_Logo, WindowWidth / 2, WindowHeight / 4

	ExDrawColorText("^8by", WindowWidth / 2, WindowHeight / 2 - 60, Fnt_m, TEXT_ALIGN_CENTER)
	ExDrawColorText("^8ObelardO", WindowWidth / 2, WindowHeight / 2 - 20, Fnt_l, TEXT_ALIGN_CENTER)
	ExDrawColorText("^9Design and Programming", WindowWidth / 2, WindowHeight / 2 + 30, Fnt_m, TEXT_ALIGN_CENTER)
	ExDrawColorText("^9Special for ^8IGDC.RU^9 ¹131", WindowWidth / 2, WindowHeight / 2 + 90, Fnt_m, TEXT_ALIGN_CENTER)
	

	ExDrawColorText("^9Exile (c) 2016 Vladislav Trubicin aka ObelardO. obelardos@gmail.com", WindowWidth / 2, WindowHeight - 50, Fnt_s, TEXT_ALIGN_CENTER)

	If GuiDrawLargeButton(WindowWidth / 2, WindowHeight - 70 * 2, "back")
		GameStatus = GameStatus_MainMenu
	End If
End Function

Function DrawIntro()
	xChannelVolume Channel_MainTheme, 0
	xChannelVolume Channel_MainAmbient, 0
	Channel_MainThemeVolume = 0.0
	Channel_MainAmbientVolume = 0.0

	xPlaySound Sound_Intro
	Local ScaleFactor#, I#
	ScaleFactor = Float(WindowWidth) / xImageWidth(Img_Intro)
	xScaleImage Img_Intro, ScaleFactor, ScaleFactor

	For i = 0 To 1.0 Step 0.001
		xDrawImage Img_Intro, WindowWidth/ 2, 0
		xScaleImage Img_Intro, ScaleFactor + i, ScaleFactor + i
		xDrawImage Img_Overlay, 0, 0
		If i < 0.5
			xColor 0, 0, 0, 255 - (i * 2.0) * 255
			xRect 0, 0, WindowWidth, WindowHeight, True
		End If
		Delay 5

		If xKeyDown(KEY_ESCAPE) Exit
		
		xFlip
	Next

	xFreeSound Sound_Intro
	xFlushKeys

	DrawFade(WindowWidth / 2, 0, 18)
End Function

Function DrawFade(FadeX = 0, FadeY = 0, FadeSpeed = 1)
	;Return
	Local i#

	If FadeX = 0 FadeX =  WindowWidth / 2
	If FadeY = 0 FadeY =  WindowHeight / 2

	Local ScreenImage = xCreateImage(WindowWidth, WindowHeight)
	xHandleImage  ScreenImage, FadeX, FadeY

	xCopyRect 0, 0, WindowWidth, WindowHeight, 0, 0, xBackBuffer(), xImageBuffer(ScreenImage)

	For i = 0 To 1.0 Step 0.01
		xDrawImage ScreenImage, FadeX, FadeY
		xScaleImage ScreenImage, 1.0 + i, 1.0 + i
		xColor 0, 0, 0, i * 255
		xRect 0, 0, WindowWidth, WindowHeight, True
		Delay FadeSpeed
		xFlip
	Next
End Function

Function UpdateChannels(Volume#)
	Channel_MainThemeVolume = ExCurveValue(Volume, Channel_MainThemeVolume, 50)
	Channel_MainAmbientVolume = ExCurveValue(0.5 - Volume, Channel_MainAmbientVolume, 50)

	xChannelVolume Channel_MainTheme, Channel_MainThemeVolume
	xChannelVolume Channel_MainAmbient, Channel_MainAmbientVolume 
End Function
