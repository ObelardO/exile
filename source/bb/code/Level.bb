;==================================================================
;Project Title:  ?     
;Author:         ObelardO
;Email:          obelardos@gmail.com          
;Date:           06.02.15
;Notes:          level code-file   
;==================================================================

Const LevelSizeW = 200
Const LevelSizeH = 200

Const LevelMinRoomW = 7
Const LevelMinRoomH = 7

Const LevelMaxRoomW = 16
Const LevelMaxRoomH = 16

Const LevelRoomSize_U = 1
Const LevelRoomSize_R = 2
Const LevelRoomSize_D = 3
Const LevelRoomSize_L = 4

Const LevelCorridorFixW = 2
Const LevelCorridorMinH = 4
Const LevelCorridorMaxH = 8
Const LevelCorridoCross = 3

Const LevelCorridorType_U2D = 1
Const LevelCorridorType_L2R = 2
Const LevelCorridorType_D2U = 3
Const LevelCorridorType_R2L = 4

Const LevelRoomStatus_HIDED   = 0
Const LevelRoomStatus_SHOWING = 1
Const LevelRoomStatus_SHOWED  = 2
Const LevelRoomStatus_HIDING  = 3

Const LevelRoomStatus_CLOSED  = 0
Const LevelRoomStatus_OPENED  = 1

Const LevelObjectType_Floor = 666
Const LevelObjectType_Door  = 665
Const LevelObjectType_Chest = 664
Const LevelObjectType_Enemy = 663

Const ItemKind_Special = 0
Const ItemKind_Key     = 1
Const ItemKind_Sword   = 2
Const ItemKind_Book    = 3
Const ItemKind_Helmet  = 4
Const ItemKind_Chest   = 5
Const ItemKind_Legs    = 6
Const ItemKind_Boots   = 7
Const ItemKindCount = 8

Const ItemClass_Simple   = 0
Const ItemClass_Improved = 1
Const ItemClass_Advanced = 2
Const ItemClassCount = 2

Const PlayerSkill_Nothing = 0
Const PlayerSkill_Power   = 1
Const PlayerSkill_Speed   = 2
Const PlayerSkill_Health  = 3
Const PlayerSkill_Protect = 4
Const PlayerSkill_Attack  = 5
Const PlayerSkill_Mana    = 6
Const PlayerSkillCount = 6

Const ItemMaxID = (ItemKindCount + 1) * (ItemClassCount + 1) - 1

Const LevelMinRoomCount = 5

Dim ItemClassName$(ItemClass_Advanced)
Dim ItemKindName$(ItemKindCount)
Dim ItemSpecialName$(ItemMaxID)

Dim LevelArray(LevelSizeW, LevelSizeH)

Type tRoom
	Field Entity
	Field Status
	Field Alpha#
	Field X, Y
	Field W, H
	Field Activated
	Field DeadLock
	Field ItemID	
End Type

Type tDoor
	Field Entity
	Field MovePart
	Field Status
	Field X, Y
	Field Room1.tRoom
	Field Room2.tRoom
End Type

Type tItemType
	Field ID
	Field Name$
	Field Kind
	Field class
	Field UsePoints
	Field NoneSkill
	Field GiveSkill
	Field NeedSkill
End Type

Type tItem
	Field Chest.tChest
	Field ItemType.tItemType
	Field UsePoints
	Field InInvetory
	Field SlotX, SlotY
End Type 

Global CurrentRoom.tRoom   = Null
Global CurrentDoor.tDoor   = Null 

Global LevelDebugImage
Global LevelDebugMode
Global LevelRoomCountMax
Global LevelRoomCountCur
Global LevelRoomDeadlockCount
Global LevelMapImage
Global LevelMapImageTemp
Global LevelMapImageObjects
Global LevelMapScale#
Global LevelMapWidth

Global LevelLight
Global LevelShader
Global UseShader = True

Const AmbientLightR# = 0;0.01
Const AmbientLightG# = 0;0.02
Const AmbientLightB# = 0;0.03

Const LevelLightR# = 0.9
Const LevelLightG# = 0.8
Const LevelLightB# = 0.7

Const LevelLightInt# = 1.25
Const LevelLightRng# = 5

Global ItemImage
Global LevelCounter
Global LevelCurSeed

Global LevelModel_Wall1
Global LevelModel_Wall2
Global LevelModel_Corner
Global LevelModel_Door
Global LevelModel_Portal1
Global LevelModel_Portal2
Global LevelModel_Plasma

Global LevelTexture_Plasm
Global LevelTexture_d
Global LevelTexture_n

Dim LevelPortalPlasm(3)

Global Sound_DoorOpen
Global Sound_DoorClose
Global Sound_DoorKey
Global Sound_DoorLock

Global Sound_LevelStart
Global Sound_LevelStartChannel

Function InitLevel()
	LoadItemTypes()

	LevelDebugImage = xCreateImage(LevelSizeW, LevelSizeH)
	xResizeImage LevelDebugImage, ExGetVar("r_height"),  ExGetVar("r_height")

	LevelLight = xCreateLight(LIGHT_DIRECTIONAL)
	xTurnEntity LevelLight, 60, -45, 30
	xLightColor LevelLight, 255 * LevelLightR, 255 * LevelLightG, 255 * LevelLightB
	;xAmbientLight 255 * AmbientLightR, 255 * AmbientLightG, 255 * AmbientLightR

	LevelMapWidth = xGraphicsHeight() * Float(xGraphicsHeight()) / xGraphicsWidth()
	LevelMapScale = LevelMapWidth / LevelSizeH

	LevelMapImageTemp = xCreateImage(LevelMapWidth, LevelMapWidth)
	xScaleImage  LevelMapImageTemp, 2, 2
	xMidHandle   LevelMapImageTemp
	xRotateImage LevelMapImageTemp, 45

	LevelMapImageObjects  = xCreateImage(LevelMapWidth, LevelMapWidth)
	xMidHandle   LevelMapImageObjects
	xRotateImage LevelMapImageObjects, 45

	LevelMapImage = xCreateImage(ExGetVar("r_height"), ExGetVar("r_height"))
	xMidHandle   LevelMapImage
	xScaleImage  LevelMapImage, 2.0, 1.0

	ItemImage = xLoadAnimImage("base\images\items.png", 50, 50, 0, (ItemKindCount + 1) * (ItemClassCount + 1))

	LevelModel_Wall1 = xLoadMesh("base\models\wall1.b3d")
	xHideEntity LevelModel_Wall1

	LevelModel_Wall2 = xLoadMesh("base\models\wall2.b3d")
	xHideEntity LevelModel_Wall2

	LevelModel_Corner = xLoadAnimMesh("base\models\corner.b3d")
	xHideEntity LevelModel_Corner

	LevelModel_Door = xLoadAnimMesh("base\models\door.b3d")
	xHideEntity LevelModel_Door

	LevelModel_Portal1 = xLoadMesh("base\models\Portal.b3d")
	LevelModel_Portal2 = xLoadMesh("base\models\Portal.b3d")
	
	xScaleEntity LevelModel_Portal1, 0.05, 0.05, 0.05
	xScaleEntity LevelModel_Portal2, 0.05, 0.05, 0.05

	LevelTexture_Plasm = xLoadTexture("base\textures\plasm.jpg", FLAGS_ALPHA)

	LevelModel_Plasma = xCreatePivot()

	For i = 0 To 3
		LevelPortalPlasm(i) = xCreatePlane()
		xScaleEntity    LevelPortalPlasm(i), 0.8 * (i + 1), 1.0, 0.8 * (i + 1)
		xPositionEntity LevelPortalPlasm(i), 0, 0.05 * i, 0
		xEntityTexture  LevelPortalPlasm(i), LevelTexture_Plasm
		xEntityParent   LevelPortalPlasm(i), LevelModel_Plasma
		xEntityFX       LevelPortalPlasm(i), FX_FULLBRIGHT
		xEntityColor    LevelPortalPlasm(i), 240 - 60 * (i + 1), 255, 60 * (i + 1)
		xTurnEntity     LevelPortalPlasm(i), 0, Rand(45, 135), 0
	Next

	LevelShader = xLoadFXFile("base\shaders\Bump Light.fx")

	LevelTexture_d = xLoadAnimTexture("base\textures\level.jpg", 1, 256, 256, 0, 15)
	LevelTexture_n = xLoadAnimTexture("base\textures\level_n.jpg", 1, 256, 256, 0, 15)

	AttachMat(LevelModel_Portal1, LevelTexture_d, LevelTexture_n, 14)
	AttachMat(LevelModel_Portal2, LevelTexture_d, LevelTexture_n, 14)

	Sound_DoorOpen  = xLoadSound("base\sounds\door\open.ogg")
	Sound_DoorClose = xLoadSound("base\sounds\door\close.ogg")
	Sound_DoorKey   = xLoadSound("base\sounds\door\key.ogg")
	Sound_DoorLock  = xLoadSound("base\sounds\door\lock.ogg")

	Sound_LevelStart = xLoadSound("base\sounds\intro\vocal.ogg")
End Function

Function AttachMat(Entity, Texture_D, Texture_N, TexID = 0)
	If UseShader

	xSetEntityEffect         Entity, LevelShader
	xSetEffectTechnique      Entity, "Point"
	xSetEffectMatrixSemantic Entity, "MatWorldViewProj", WORLDVIEWPROJ
	xSetEffectMatrixSemantic Entity, "MatWorld", WORLD
	;		Shader Varriables
	xSetEffectVector  Entity, "AmbientClr", AmbientLightR, AmbientLightG, AmbientLightB
	xSetEffectVector  Entity, "LightClr"  , LevelLightR, LevelLightG, LevelLightB
	xSetEffectFloat   Entity, "LightInt"  , LevelLightInt
	xSetEffectFloat   Entity, "RngLight"  , LevelLightRng
	xSetEffectFloat   Entity, "DotLight"  , 0.0
	xSetEffectTexture Entity, "tDiffuse"  , Texture_D, TexID
	xSetEffectTexture Entity, "tNormal"   , Texture_N, TexID

	Else
		xEntityTexture Entity, Texture_D, TexID
	End If
End Function

Function PointInRegion(PointX#, PointY#, RegionX#, RegionY#, RegionW#, RegionH#)
	If PointX >= RegionX And PointX <= RegionX + RegionW
	If PointY >= RegionY And PointY <= RegionY + RegionH
		Return True
	End If
	End If

	Return False
End Function

Function RoomConnected(Room.tRoom)
	For OtherRoom.tRoom = Each tRoom
		If OtherRoom <> Room
			If PointInRegion(PlayerPosX,-PlayerPosZ, OtherRoom\X - 1, OtherRoom\Y - 1, OtherRoom\W + 2, OtherRoom\H + 2)
				For Door.tDoor = Each tDoor
					If Door\Status = LevelRoomStatus_OPENED
						If PointInRegion(Door\X, Door\Y, Room\X - 1, Room\Y -1 , Room\W + 2, Room\H + 2)
						If PointInRegion(Door\X, Door\Y, OtherRoom\X - 1, OtherRoom\Y - 1, OtherRoom\W + 2, OtherRoom\H + 2) 
							Return True
						End If	
						End If
					End If
				Next
			End If
		End If
	Next

	Return False
End Function

Function GetUnitingDoor.tDoor(OtherRoom1.tRoom, OtherRoom2.tRoom)
	For Door.tDoor = Each tDoor
		If Door\Room1 = OtherRoom1 And Door\Room2 = OtherRoom2 Return Door
		If Door\Room1 = OtherRoom2 And Door\Room2 = OtherRoom1 Return Door
	Next

	Return Null
End Function

Function GetRoomByCords.tRoom(X, Y)
	For Room.tRoom = Each tRoom
		If PointInRegion(X, Y, Room\X, Room\Y, Room\W, Room\H)
			xEntityColor Room\Entity, 255, 0, 0
			Return Room
		End If
	Next

	Return Null
End Function

Function UpdateDoors()
	If MouseHitL
		If GetLevelObjectType() = LevelObjectType_Door
			For Door.tDoor = Each tDoor
				If xPickedEntity() = Door\Entity And xEntityDistance(PlayerBase, Door\Entity) < 3.0
					If Door\Room1 = Null
						DebugLog "ÝÒÀ ÄÂÅÐÜ ÇÀÏÅÐÒÀ ÍÀÌÅÐÒÂÎ"
						xPlaySound Sound_DoorLock
						Return False
					ElseIf Door\Room1\ItemID
						If PlayerPickUpItem(Door\Room1\ItemID)
							DebugLog "ÄÂÅÐÜ ÎÒÊÐÛÒÀ Ñ ÏÎÌÎÙÜÞ: " + ItemSpecialName(Door\Room1\ItemID)
							Door\Room1\ItemID = 0
							xPlaySound Sound_DoorKey
						Else
							DebugLog "ÝÒÀ ÄÂÅÐÜ ÎÒÊÐÛÂÀÅÒÑß Ñ ÏÎÌÎÙÜÞ: " + ItemSpecialName(Door\Room1\ItemID)
							xPlaySound Sound_DoorLock
							Return False
						End If
					End If
	
					If Door\Room2 = Null
						DebugLog "ÝÒÀ ÄÂÅÐÜ ÇÀÏÅÐÒÀ ÍÀÌÅÐÒÂÎ"
						xPlaySound Sound_DoorLock
						Return False
					ElseIf Door\Room2\ItemID
						If PlayerPickUpItem(Door\Room2\ItemID)
							DebugLog "ÄÂÅÐÜ ÎÒÊÐÛÒÀ Ñ ÏÎÌÎÙÜÞ: " + ItemSpecialName(Door\Room2\ItemID)
							Door\Room1\ItemID = 0
							xPlaySound Sound_DoorKey
						Else
							DebugLog "ÝÒÀ ÄÂÅÐÜ ÎÒÊÐÛÂÀÅÒÑß Ñ ÏÎÌÎÙÜÞ: " + ItemSpecialName(Door\Room2\ItemID)
							xPlaySound Sound_DoorLock
							Return False
						End If
					End If
					
					Door\Status = Not Door\Status
					If Door\Status xPlaySound Sound_DoorOpen Else xPlaySound Sound_DoorClose
					CurrentDoor.tDoor = Door
	
					Exit
				End If
			Next
		End If
	End If

	If CurrentDoor <> Null
		xRotateEntity CurrentDoor\MovePart, 0, ExCurveValue(90 * CurrentDoor\Status, xEntityYaw(CurrentDoor\MovePart), 10), 0
	End If

	If DebugMode And DebugDrawWay
		For Door.tDoor = Each tDoor
			xColor 255, 255, 255
			xCameraProject(camera, xEntityX(Door\entity), xEntityY(Door\entity), xEntityZ(Door\entity))
			DoorProjX = xProjectedX()
			DoorProjY = xProjectedY()
	
			If Door\Room1 <> Null
				xCameraProject(camera, xEntityX(Door\Room1\Entity), xEntityY(Door\Room1\Entity),xEntityZ(Door\Room1\Entity))
				xLine DoorProjX, DoorProjY, xProjectedX(), xProjectedY()
			End If
	
			If Door\Room2 <> Null
				xCameraProject(camera, xEntityX(Door\Room2\Entity), xEntityY(Door\Room2\Entity),xEntityZ(Door\Room2\Entity))
				xLine DoorProjX, DoorProjY, xProjectedX(), xProjectedY()
			End If
		Next
	End If

	If DebugMode And DebugDrawKey Or ForceDrawKey
		For Door.tDoor = Each tDoor
			xCameraProject(camera, xEntityX(Door\entity), xEntityY(Door\entity), xEntityZ(Door\entity) - 0.5)
			DoorProjX = xProjectedX() - 25
			DoorProjY = xProjectedY() - 25

			If Door\Room1 <> Null And Door\Room2 <> Null
				If Door\Room1\Status Or Door\Room2\Status
				If Door\Room1\ItemID xDrawImage ItemImage, DoorProjX, DoorProjY, Door\Room1\ItemID
				If Door\Room2\ItemID xDrawImage ItemImage, DoorProjX, DoorProjY, Door\Room2\ItemID
				End If
			End If
		Next
	End If
End Function 
	
Function UpdateDoorsVisible()
	For Door.tDoor = Each tDoor
		If Door\Room1 <> Null And Door\Room2 <> Null
		If Door\Room1\Status = LevelRoomStatus_HIDED And Door\Room2\Status = LevelRoomStatus_HIDED
			xHideEntity Door\Entity
		Else
			xShowEntity Door\Entity
		End If
		End If
	Next
End Function

Function ClearLevelMapImage()
	xSetBuffer xImageBuffer(LevelMapImageTemp)
		xClsColor 0, 0, 0, 0: xCls
	xSetBuffer xImageBuffer(LevelMapImage)
		xClsColor 0, 0, 0, 0: xCls
	xSetBuffer xImageBuffer(LevelMapImageObjects)
		xClsColor 0, 0, 0, 0: xCls
	xSetBuffer xBackBuffer()
End Function

Function UpdateLevelMapImage(Room.tRoom)
	xAntiAlias False

	xSetBuffer xImageBuffer(LevelMapImageTemp)
				xColor 255, 255, 255, 255
		xRect Room\X * LevelMapScale, Room\Y * LevelMapScale, Room\W * LevelMapScale, Room\H * LevelMapScale

		For Door.tDoor = Each tDoor
			If Door\Room1 = Room Or Door\Room2 = Room
				xColor 250,225,5, 255
				If Door\Room1 <> Null
					If Door\Room1\ItemID > 0 xColor 128,255,0, 255
				Else
					xColor 128, 128, 128, 255
				End If
				If Door\Room2 <> Null
					If Door\Room2\ItemID > 0 xColor 128,255,0, 255
				Else
					xColor 128, 128, 128, 255
				End If
				
				xRect (Door\X - 1) * LevelMapScale, (Door\Y - 1) * LevelMapScale, 2 * LevelMapScale, 2 * LevelMapScale
			End If
		Next
		
		xFlip

	xSetBuffer xImageBuffer(LevelMapImage)
		xClsColor 0, 0, 0, 0: xCls

		xHandleImage LevelMapImageTemp,    PlayerPosX * LevelMapScale,-PlayerPosZ * LevelMapScale
		xHandleImage LevelMapImageObjects, PlayerPosX * LevelMapScale,-PlayerPosZ * LevelMapScale
		
		xDrawImage LevelMapImageTemp,    xImageWidth(LevelMapImage) / 2, xImageHeight(LevelMapImage) / 2
		xDrawImage LevelMapImageObjects, xImageWidth(LevelMapImage) / 2, xImageHeight(LevelMapImage) / 2

		xFlip
	xSetBuffer xBackBuffer()

	xAntiAlias True
End Function

Function UpdateLevelObjectsImage()
	xAntiAlias False

	xSetBuffer xImageBuffer(LevelMapImageObjects)
		xClsColor 0, 0, 0, 0: xCls
		xColor 255, 0, 0, 255
		xRect (PlayerPosX - 1) * LevelMapScale, (PlayerPosZ + 1) *-LevelMapScale, 2 * LevelMapScale, 2 * LevelMapScale, True
		xFlip

	xSetBuffer xImageBuffer(LevelMapImage)
		xClsColor 0, 0, 0, 0: xCls

		xHandleImage LevelMapImageTemp,    PlayerPosX * LevelMapScale,-PlayerPosZ * LevelMapScale
		xHandleImage LevelMapImageObjects, PlayerPosX * LevelMapScale,-PlayerPosZ * LevelMapScale
		
		xDrawImage LevelMapImageTemp,    xImageWidth(LevelMapImage) / 2, xImageHeight(LevelMapImage) / 2
		xDrawImage LevelMapImageObjects, xImageWidth(LevelMapImage) / 2, xImageHeight(LevelMapImage) / 2
		xFlip

	xSetBuffer xBackBuffer()

	xAntiAlias True
End Function

Function UpdateLevel()
	For Room.tRoom = Each tRoom

		If Room\Status > LevelRoomStatus_HIDED
			If UseShader
				xSetEffectVector Room\Entity,"PosLight",xEntityX(PlayerBase),1.0,xEntityZ(PlayerBase)
				xSetEffectVector Room\Entity, "LightClr"  , LevelLightR * Room\Alpha, LevelLightG * Room\Alpha, LevelLightB * Room\Alpha
			End If
			If Room\Activated = False
				Room\Activated = True
				UpdateLevelMapImage(Room)
				UpdatePlayerInterface()
			End If
		End If
	
		Select Room\Status
			Case LevelRoomStatus_HIDED
				PlayerInRoom = PointInRegion(PlayerPosX,-PlayerPosZ, Room\X - 1, Room\Y - 1, Room\W + 2, Room\H + 2)
				If PlayerInRoom Or RoomConnected(Room)
					Room\Status = LevelRoomStatus_SHOWING
					xShowEntity Room\Entity
					UpdateDoorsVisible()
					ActivateEnemys(Room)

					If Room = Last tRoom xShowEntity LevelModel_Plasma
				End If
				If PlayerInRoom CurrentRoom = Room
				
			Case LevelRoomStatus_SHOWING
				Room\Alpha = ExCurveValue(1.0, Room\Alpha, 5)

				If Room\Alpha > 0.9 Room\Alpha = 1.0
				If Room\Alpha = 1.0 Room\Status = LevelRoomStatus_SHOWED  
				
				;xEntityAlpha Room\Entity, Room\Alpha; true
				
			Case LevelRoomStatus_SHOWED
				PlayerInRoom = PointInRegion(PlayerPosX,-PlayerPosZ, Room\X - 1, Room\Y - 1, Room\W + 2, Room\H + 2)
				If Not (PlayerInRoom Or RoomConnected(Room)) Room\Status = LevelRoomStatus_HIDING

				If PlayerInRoom CurrentRoom = Room

			Case LevelRoomStatus_HIDING
				Room\Alpha = ExCurveValue(0.0, Room\Alpha, 5)

				If Room\Alpha < 0.1 Room\Alpha = 0.0
				If Room\Alpha = 0.0
					Room\Status = LevelRoomStatus_HIDED
					xHideEntity Room\Entity
					UpdateDoorsVisible()
					DiactivateEnemys(Room)

					If Room = Last tRoom xHideEntity LevelModel_Plasma
				End If
				;xEntityAlpha Room\Entity, Room\Alpha
		End Select

	Next

	For i = 0 To 3
		If (i Mod 2) = 0
			xTurnEntity LevelPortalPlasm(i), 0, i, 0
		Else
			xTurnEntity LevelPortalPlasm(i), 0,-i, 0
		End If
	Next
End Function

Function UpdateLevelDebugImage()
	xSetBuffer xImageBuffer(LevelDebugImage)

		For i = 0 To LevelSizeW
		For j = 0 To LevelSizeH
			SelectLevelCellColor(LevelArray(i, j))
			xRect i, j, 1, 1
		Next
		Next

		xFlip

	xSetBuffer xBackBuffer()
End Function

Function SelectLevelCellColor(CellIndex)
	Select CellIndex
		Case 0 xColor 0, 0, 0, 196
		
		Case 1 xColor 128,0,128, 255
		
		Case 2 xColor 255,0,128, 255
		Case 3 xColor 255,0,255, 255
		Case 4 xColor 255,128,255, 255
		Case 5 xColor 255,128,192, 255

		Case 6 xColor 0,255,0, 255
		Case 7 xColor 128,255,0, 255
		Case 8 xColor 128,255,128, 255
		Case 9 xColor 0,255,128, 255
		
		Case 11 xColor 128,255,255, 255
	End Select
End Function

Function GenerateLevel(RoomCount = 10, LevelSeed = 0, DebugMode = False)
	xPositionEntity PlayerBase, 0, 0, 0 
	xPositionEntity PlayerTarget, 0, 0, 0

	CurrentRoom.tRoom   = Null
 	CurrentDoor.tDoor   = Null 
 	CurrentChest.tChest = Null
	
	ForcePickLock =True
		
	If RoomCount = 0 RoomCount = 10
	If LevelSeed = 0 LevelSeed = xMillisecs()
	
	LevelDebugMode = DebugMode

	ExCaptureTime("level")
	ConsoleLog("   ^5[INFO]^1 Start generate level. Seed: ^5" + LevelSeed + "^1 Room: ^5" + RoomCount)

	LevelCurSeed = LevelSeed

	SeedRnd LevelSeed

	SaveGame()

	LevelCounter = LevelCounter  + 1

	MouseHitL = False
	LevelRoomCountCur      = 0
	LevelEnemyCount        = 0
	LevelRoomCountMax      = RoomCount
	LevelCorridorsFree     = 0
	LevelRoomDeadlockCount = 0
	ClearLevel()
	GenerateRoom(100, 100)
	UpdateLevelDebugImage

	LevelDebugMode = False

	BuildLevel()
	ClearLevelMapImage()
	DrawPlayerInventory(True)
	UpdatePlayerStats()

	ConsoleLog("   ^5[INFO]^1 level generated in ^6" + ExReleaseTime("level") + "^1 (ms)")

	If LevelRoomCountCur < LevelRoomCountMax
		ConsoleLog("^4[WARNING]^1 Generated only ^6" + LevelRoomCountCur + "^1 of ^6" + LevelRoomCountMax)
	End If

	GameStatus = GameStatus_InGame

	DebugLog "LevelCounter " + LevelCounter

	xCameraPick(camera, 0, 0)
	ForcePickLock = False

	Sound_LevelStartChannel = xPlaySound(Sound_LevelStart)
	xChannelVolume Sound_LevelStartChannel, 0.5
End Function

Function ClearLevel()
	For i = 0 To LevelSizeW
	For j = 0 To LevelSizeH
		LevelArray(i, j) = 0
	Next
	Next

	xEntityParent LevelModel_Portal1, 0
	xEntityParent LevelModel_Portal2, 0

	For Enemy.tEnemy = Each tEnemy
		xFreeEntity Enemy\Base
		xFreeEntity Enemy\Target
		Delete Enemy
	Next

	For Item.tItem = Each tItem
		If Item\InInvetory = False	Delete Item
	Next

	For Chest.tChest = Each tChest
		xFreeEntity Chest\Entity
		Delete Chest
	Next

	For Room.tRoom = Each tRoom
		xFreeEntity Room\Entity
		Delete Room
	Next

	For Door.tDoor = Each tDoor
		xFreeEntity Door\Entity
		Delete Door
	Next
End Function

Function GenerateRoom(X, Y, DeadLock = False)

	While Not ExistEmptySpace
		RoomW = Rand(LevelMinRoomW, LevelMaxRoomW)
		RoomH = Rand(LevelMinRoomH, LevelMaxRoomH)

		If TriedU And TriedR And TriedD And TriedL Exit
	
		Select Rand(LevelRoomSize_U, LevelRoomSize_L)
			
			Case LevelRoomSize_U
				RoomX = X - Rand(2, RoomW - 1)
				RoomY = Y - RoomH - 1
				TriedU = True

			Case LevelRoomSize_R
				RoomX =	X + 1
				RoomY = Y - Rand(2, RoomH - 1)
				TriedR = True

			Case LevelRoomSize_D
				RoomX = X - Rand(2, RoomW - 1)
				RoomY = Y + 1
				TriedD = True

			Case LevelRoomSize_L
				RoomX = X - RoomW - 1
				RoomY = Y - Rand(2, RoomH - 1)
				TriedL = True

		End Select

		ExistEmptySpace = CheckEmptyLevelSpace(RoomX, RoomY, RoomW, RoomH)
	Wend

	If ExistEmptySpace And LevelRoomCountCur < LevelRoomCountMax

		LevelRoomCountCur = LevelRoomCountCur + 1
		DebugLog "GENERATE ROOM ================ " + (LevelRoomCountCur)
		GuiDrawLoading(Float(LevelRoomCountCur) / LevelRoomCountMax)

		RegisterRoom(RoomX, RoomY, RoomW, RoomH, DeadLock)

		For i = RoomX To RoomX + RoomW
		For j = RoomY To RoomY + RoomH
			LevelArray(i, j) = 1
		
			If j = RoomY LevelArray(i, j) = 2
			If i = RoomX LevelArray(i, j) = 3
			
			If j = RoomY + RoomH LevelArray(i, j) = 4
			If i = RoomX + RoomW LevelArray(i, j) = 5

			If i = RoomX And j = RoomY LevelArray(i, j) = 6
			If i = RoomX And j = RoomY + RoomH LevelArray(i, j) = 7
			If i = RoomX + RoomW And j = RoomY LevelArray(i, j) = 8
			If i = RoomX + RoomW And j = RoomY + RoomH LevelArray(i, j) = 9
		Next
		Next

		If LevelDebugMode
			xCls
			UpdateLevelDebugImage()
			xDrawImage LevelDebugImage, 0, 0
			xFlip
		End If

		If DeadLock Return True Else GenerateCorridor(RoomX, RoomY, RoomW, RoomH)
	End If

	Return False
End Function

Function GenerateCorridor(RoomX, RoomY, RoomW, RoomH)

	While Not ExistEmptySpace

		If TriedU And TriedR And TriedD And TriedL Exit
	
		Select Rand(LevelRoomSize_U, LevelRoomSize_L)

			Case LevelRoomSize_U
			
				CorridorW = LevelCorridorFixW
				CorridorH = Rand(LevelCorridorMinH, LevelCorridorMaxH)
				CorridorX = RoomX + Rand(0, RoomW - CorridorW)
				CorridorY = RoomY - 2 - CorridorH

				ExitX = CorridorX + 1
				ExitY = CorridorY - 1
				DoorX = CorridorX + 1
				DoorY = CorridorY + CorridorH + 1

				TriedU = True

			Case LevelRoomSize_R
			
				CorridorW = Rand(LevelCorridorMinH, LevelCorridorMaxH)
				CorridorH = LevelCorridorFixW
				CorridorX = RoomX + RoomW + 2
				CorridorY = RoomY + Rand(0, RoomH - CorridorH)

				ExitX = CorridorX + CorridorW + 1
				ExitY = CorridorY + 1
				DoorX = CorridorX - 1
				DoorY = CorridorY + 1

				TriedR = True

			Case LevelRoomSize_D

				CorridorW = LevelCorridorFixW
				CorridorH = Rand(LevelCorridorMinH, LevelCorridorMaxH)
				CorridorX = RoomX + Rand(0, RoomW - CorridorW)
				CorridorY = RoomY + RoomH + 2

				ExitX = CorridorX + 1
				ExitY = CorridorY + CorridorH + 1
				DoorX = CorridorX + 1
				DoorY = CorridorY - 1

				TriedD = True

			Case LevelRoomSize_L

				CorridorW = Rand(LevelCorridorMinH, LevelCorridorMaxH)
				CorridorH = LevelCorridorFixW
				CorridorX = RoomX - 2 - CorridorW
				CorridorY = RoomY + Rand(0, RoomH - CorridorH)
				
				ExitX = CorridorX - 1
				ExitY = CorridorY + 1
				DoorX = CorridorX + CorridorW + 1
				DoorY = CorridorY + 1

				TriedL = True
		End Select

		ExistEmptySpace = CheckEmptyLevelSpace(CorridorX - 1, CorridorY - 1, CorridorW + 2, CorridorH + 2)
	Wend

	If ExistEmptySpace

		If checkLevelBorder(ExitX, ExitY)
			LevelArray(ExitX, ExitY) = 11
			LevelArray(DoorX, DoorY) = 11
		End If

		For i = CorridorX To CorridorX + CorridorW
		For j = CorridorY To CorridorY + CorridorH
			LevelArray(i, j) = 1
		
			If j = CorridorY LevelArray(i, j) = 2
			If i = CorridorX LevelArray(i, j) = 3
			
			If j = CorridorY + CorridorH LevelArray(i, j) = 4
			If i = CorridorX + CorridorW LevelArray(i, j) = 5

			If i = CorridorX And j = CorridorY LevelArray(i, j) = 6
			If i = CorridorX And j = CorridorY + CorridorH LevelArray(i, j) = 7
			If i = CorridorX + CorridorW And j = CorridorY LevelArray(i, j) = 8
			If i = CorridorX + CorridorW And j = CorridorY + CorridorH LevelArray(i, j) = 9
		Next
		Next

		If Rand(1, LevelCorridoCross) = 1
			GenerateRoom(ExitX, ExitY, True)
			GenerateCorridor(RoomX, RoomY, RoomW, RoomH)
		End If
		
		If GenerateRoom(ExitX, ExitY) = False
			GenerateCorridor(RoomX, RoomY, RoomW, RoomH)

			If CheckExitAvailable(ExitX, ExitY) = False
			
				For i = CorridorX To CorridorX + CorridorW
				For j = CorridorY To CorridorY + CorridorH
					LevelArray(i, j) = 0
				Next
				Next

				LevelArray(ExitX, ExitY) = 0
				LevelArray(DoorX, DoorY) = 0
			Else
				CheckExitAvailable(DoorX, DoorY)
				RegisterRoom(CorridorX, CorridorY, CorridorW, CorridorH, False, True)
			End If
		End If
	Else
		Return False
	End If
End Function

Function RegisterRoom(X, Y, W, H, Deadlock = False, Corridor = False)
	lastRoom.tRoom = Last tRoom

	Room.tRoom = New tRoom
	Room\Entity = xCreatePivot()
	Room\X = X: Room\Y = Y
	Room\W = W: Room\H = H
	Room\Deadlock = Deadlock

	xPositionEntity Room\Entity, X + Float(W) / 2, 0, -(Y + Float(H) / 2)
	xScaleEntity    Room\Entity, Float(w + 3) / 2, 1.0, Float(H + 3) / -2
	xEntityAlpha    Room\Entity, 0.0
	xHideEntity     Room\Entity

	If Corridor = False And Room <> First tRoom	GenerateEnemys(Room)
	If Deadlock LevelRoomDeadlockCount = LevelRoomDeadlockCount + 1
	If Deadlock DebugLog "deadlocks: " + LevelRoomDeadlockCount
	If Corridor And lastRoom <> Null Insert Room Before lastRoom
End Function

Function RegisterDoor(X, Y)
	Door.tDoor = New tDoor
	Door\Entity = xCreateCube()
	Door\X = X: Door\Y = Y

	xScaleEntity    Door\Entity, 0.5, 0.75, 0.5
	xPositionEntity Door\Entity, X, .75,-Y
	xEntityColor    Door\Entity, 255, 0, 0
	xEntityAlpha    Door\Entity, 0.0
	xEntityPickMode Door\Entity, PICK_BOX
	xEntityType     Door\Entity, LevelObjectType_Door

	For Room.tRoom = Each tRoom
		If PointInRegion(Door\X, Door\Y, Room\X - 1, Room\Y - 1, Room\W + 2, Room\H + 2)
			If Door\Room1 = Null Door\Room1 = Room Else Door\Room2 = Room
		End If
	Next
End Function

Function LoadItemTypes()
	ClassFile = ReadFile("base\content\class.txt")
	If ClassFile
		For i = 0 To ItemClassCount
			If Eof(ClassFile) Exit Else ItemClassName(i) = ReadLine(ClassFile)
		Next
		CloseFile ClassFile
	End If

	SkillsFile = ReadFile("base\content\skills.txt")
	If SkillsFile
		For i = 0 To PlayerSkillCount
			If Eof(SkillsFile) Exit Else PlayerSkillName(i) = ReadLine(SkillsFile)
		Next
		CloseFile SkillsFile
	End If

	TypeFile = ReadFile("base\content\types.txt")
	If TypeFile
		For i = 0 To ItemKindCount
			If Eof(TypeFile) Exit Else ItemKindName(i) = ReadLine(TypeFile)
		Next
		CloseFile TypeFile
	End If

	NameFile = ReadFile("base\content\names.txt")
	If NameFile
		While Not Eof(NameFile) 
			NameLine$ =ReadLine(NameFile)
			If NameLine <> ""
				ItemSpecialName(Int(ExStrCut(NameLine, 1, ":"))) = ExStrCut(NameLine, 2, ":")
			End If
		Wend 
		CloseFile NameFile
	End If

	For i = 0 To ItemClass_Advanced
	For j = 0 To ItemKindCount
		RegisterItemType(j, i)
	Next
	Next	
End Function

Function RegisterItemType(Kind, Class)
	If ItemKindName(Kind) = "" Return False

	ItemType.tItemType = New tItemType

	ItemType\Class = class + 1
	ItemType\Kind  = Kind
	ItemType\Name  = ItemClassName(Class) + " " + ExStrCut(ItemKindName(Kind), 1, ":")
	ItemType\ID    = (ItemKindCount + 1) * Class + Kind
	
	ItemSkill$ = ExStrCut(ItemKindName(Kind), 2, ":")

	If ItemSpecialName(ItemType\ID) <> "" ItemType\Name = ItemSpecialName(ItemType\ID) Else ItemSpecialName(ItemType\ID) = ItemType\Name 
	
	If ItemSkill = ""
		ItemType\NoneSkill = True
		ItemType\UsePoints = 1
	Else
		ItemType\NeedSkill = Int(ExStrCut(ItemSkill, 1, "-")) - 1
		ItemType\GiveSkill = Int(ExStrCut(ItemSkill, 2, "-")) - 1
		ItemType\UsePoints = 30 * (Class + 1)
	End If

	DebugLog "ITEM: " + ItemType\Name + "   give: " + PlayerSkillName(ItemType\GiveSkill) + " " + (class + 1) + "  need: " + PlayerSkillName(ItemType\NeedSkill) + " " + (class + 1) + "  id: " + ItemType\ID+  "  use: " + ItemType\UsePoints
End Function

Function RegisterItem(ID, Chest.tChest, InInvetory = False, UsePoints = 0, SlotID = 0)
	ItemType.tItemType = GetItemTypeHandleByID(ID)
	If ItemType = Null Return False
	If Chest = Null And InInvetory = False Return False

	Item.tItem = New tItem
	Item\Chest.tChest = Chest
	Item\ItemType.tItemType = ItemType
	If UsePoints = 0
		If Item\ItemType\NoneSkill
			Item\UsePoints = 1
		Else
			Item\UsePoints = Rand(3, Item\ItemType\UsePoints / 10) * 10
		End If
	Else
		Item\UsePoints = UsePoints
	End If
	Item\InInvetory = InInvetory

	If SlotID
		Item\SlotX = PlayerInvImageX + (SlotID Mod PlayerSlotPerW) * PlayerInvW + PlayerInvX
		Item\SlotY = PlayerInvImageY + (SlotID  /  PlayerSlotPerW) * PlayerInvH + PlayerInvY
	End If

	UpdatePlayerItems(GetPlayerSlotID(Item\SlotX, Item\SlotY), Item)

	DebugLog "item registration: " + ItemType\Name + " " + ExStrCut(ItemKindName(Item\ItemType\Kind), 1, ":")
End Function

Function UpdateItems()

End Function

Function DrawItemInfo(Item.tItem, x = 0, y = 0)
	xSetFont Fnt_s
	
	FrameW = xStringWidth(Item\ItemType\Name) + 60
	If Item\ItemType\NoneSkill
		FrameH = 40
	Else
		FrameH = 60
		If Item\ItemType\GiveSkill > 1 FrameH = FrameH + 20
	End If
	
	If Item\ItemType\UsePoints > 1 FrameH = FrameH + 20

	xColor 24,0,0, 240
	xRect x + 10, y + 10, FrameW, FrameH, True
	xColor 245, 159, 58, 255
	xRect x + 10, y + 10, FrameW, FrameH, False

	ExDrawColorText("^1" + Item\ItemType\Name, x + 20, y + 20, Fnt_s)

	If Item\ItemType\NoneSkill = False
		ExDrawColorText("^4need: " + PlayerSkillName(Item\ItemType\NeedSkill) + " ^1" + Item\ItemType\Class, x + 20, y + 40, Fnt_s)
		If Item\ItemType\GiveSkill > 1
			ExDrawColorText("^4give: " + PlayerSkillName(Item\ItemType\GiveSkill) + " ^1" + Item\ItemType\Class, x + 20, y + 60, Fnt_s)
			If Item\ItemType\UsePoints > 1 ExDrawColorText("^4wear: ^1" + Item\UsePoints + " ^4of ^1" + Item\ItemType\UsePoints, x + 20, y + 80, Fnt_s)
		Else
			If Item\ItemType\UsePoints > 1 ExDrawColorText("^4wear: ^1" + Item\UsePoints + " ^4of ^1" + Item\ItemType\UsePoints, x + 20, y + 60, Fnt_s)
		End If
	End If
		
End Function

Function GetItemTypeHandleByID.tItemType(ID)
	For ItemType.tItemType = Each tItemType
		If ItemType\ID = ID Return ItemType
	Next

	Return Null
End Function

Function CheckExitAvailable(ExitX, ExitY)
	If ExitX < 1 Or ExitX > LevelSizeW - 1 Or ExitY < 1 Or ExitY > LevelSizeH - 1 Return False

	If LevelArray(ExitX - 1, ExitY) > 0 And LevelArray(ExitX + 1, ExitY) > 0 Vertical = True
	If LevelArray(ExitX, ExitY - 1) > 0 And LevelArray(ExitX, ExitY + 1) > 0 Horizont = True

	If LevelArray(ExitX - 1, ExitY - 1) > 0 And LevelArray(ExitX + 1, ExitY - 1) > 0 Dioganal1 = True
	If LevelArray(ExitX - 1, ExitY + 1) > 0 And LevelArray(ExitX + 1, ExitY + 1) > 0 Dioganal2 = True

	If Vertical And LevelArray(ExitX - 1, ExitY) < 6 LevelArray(ExitX - 1, ExitY) = 1
	If Vertical And LevelArray(ExitX + 1, ExitY) < 6 LevelArray(ExitX + 1, ExitY) = 1
	If Horizont And LevelArray(ExitX, ExitY - 1) < 6 LevelArray(ExitX, ExitY - 1) = 1
	If Horizont And LevelArray(ExitX, ExitY + 1) < 6 LevelArray(ExitX, ExitY + 1) = 1

	Result = ((Vertical Or Horizont) And Dioganal1 And Dioganal2)

	If Result
		If Horizont
			LevelArray(ExitX, ExitY) = 11
;			LevelArray(ExitX - 1, ExitY - 1) = 7
;			LevelArray(ExitX + 1, ExitY - 1) = 9
;			LevelArray(ExitX - 1, ExitY + 1) = 6
;			LevelArray(ExitX + 1, ExitY + 1) = 8
		Else
			LevelArray(ExitX, ExitY) = 12
;			LevelArray(ExitX - 1, ExitY - 1) = 8
;			LevelArray(ExitX + 1, ExitY - 1) = 6
;			LevelArray(ExitX - 1, ExitY + 1) = 9
;			LevelArray(ExitX + 1, ExitY + 1) = 7
		End If
	End If

	Return Result
End Function

Function CheckEmptyLevelSpace(x, y, w, h)
	If x < 0 Or y < 0 Or x + w + 1 > LevelSizeW Or y + h + 1> LevelSizeH Return False

	For i = x To x + w
	For j = y To y + h
		If LevelArray(i, j) <> 0 Return False
	Next		
	Next

	Return True
End Function

Function checkLevelBorder(x, y)
	If x < 0 Or x > LevelSizeW Or y < 0 Or y > LevelSizeH Return False
	Return True
End Function

Function CreateWall(i, j, OneOreO, Angle, Parent)
	Local wall

	If (OneOreO Mod 2) = 0
		Wall = xCopyEntity(LevelModel_Wall1)

		AttachMat(Wall, LevelTexture_d, LevelTexture_n, 10)
	Else
		Wall = xCopyEntity(LevelModel_Wall2)
		AttachMat(Wall, LevelTexture_d, LevelTexture_n, 9)
	End If
	
	xPositionEntity Wall, i, 0, -j
	xScaleEntity    Wall, 0.05, 0.075, 0.05
	xTurnEntity     Wall, 0, 90 * Angle, 0
	xEntityParent   Wall, Parent
	xHideEntity     Wall

	xUpdateNormals(Wall)
End Function

Function CreateCorner(i, j, Angle, Parent)

	Local Corner = xCopyEntity(LevelModel_Corner)
	xEntityTexture Corner, LevelTexture_Corner_D

	xPositionEntity Corner, i, 0, -j
	xScaleEntity    Corner, 0.05, 0.075, 0.05
	xTurnEntity     Corner, 0, 90 * Angle, 0
	xEntityParent   Corner, Parent
	xEntityFX       Corner, FX_DISABLECULLING
	xHideEntity     Corner

	Local Flr = xFindChild(Corner, "floor")
	Local Main = xFindChild(Corner, "main")

	Local TextID = Rand(0, 3)
	
	AttachMat(Main, LevelTexture_d, LevelTexture_n, 8)
	AttachMat(Flr, LevelTexture_d, LevelTexture_n, TextID)
	
End Function

Function BuildLevel()
	For i = 0 To LevelSizeW
	For j = 0 To LevelSizeH
		If LevelArray(i, j)
			Cube = CreateFloor()
			
			For Room.tRoom = Each tRoom
				If PointInRegion(i, j, Room\X, Room\Y, Room\W, Room\H)

					xPositionEntity Cube, i, 0, -j				
					xEntityParent   Cube, Room\Entity
					xEntityAlpha    Cube, 1.0
					xEntityType     Cube, LevelObjectType_Floor
					xEntityPickMode Cube, PICK_BOX
					xHideEntity     Cube

					Select LevelArray(i, j)
						Case 2 CreateWall(i, j, i, 0, Room\Entity)
						Case 3 CreateWall(i, j, j, 1, Room\Entity)
						;Case 4 CreateWall(i, j, i, 2, Room\Entity)
						;Case 5 CreateWall(i, j, j, 3, Room\Entity)

						Case 6
							CreateCorner(i, j, 1, Room\Entity)
							xFreeEntity Cube
						Case 7
							CreateCorner(i, j, 2, Room\Entity)
							xFreeEntity Cube
						Case 8
							CreateCorner(i, j, 4, Room\Entity)
							xFreeEntity Cube
						;Case 9 CreateCorner(i, j, 3, Room\Entity)
					End Select
				End If
			Next

			If LevelArray(i, j) = 11 Or LevelArray(i, j) = 12
				RegisterDoor(i, j)
			
				For Door.tDoor = Each tDoor
					If i = Door\X And j = Door\Y

						xPositionEntity Cube, i, 0, -j
						xEntityParent   Cube, Door\Entity

						DoorModel    = xCopyEntity(LevelModel_Door)
						DoorMovePart = xFindChild(DoorModel, "Door")
						DoorWallPart = xFindChild(DoorModel, "wall")

						AttachMat(DoorWallPart, LevelTexture_d, LevelTexture_n, 8)
						AttachMat(DoorMovePart, LevelTexture_d, LevelTexture_n, 12)
						
						If UseShader xSetEffectVector  DoorMovePart, "AmbientClr", 0.35, 0.35, 0.35
												
						xPositionEntity DoorModel, i, 0, -j
						xScaleEntity    DoorModel, 0.05, 0.075, 0.05
						xTurnEntity     DoorModel, 0, 90 * (LevelArray(i, j)  - 11), 0
						xEntityParent   DoorModel, Door\Entity
						xHideEntity     DoorModel
						Door\MovePart = DoorMovePart
					End If
				Next
			End If
		End If
	Next
	Next

	LastRoom.tRoom = Last tRoom
	FirstRoom.tRoom = First tRoom
	Distance# = 1000.0

	If LastRoom <> Null
		If LastRoom\Deadlock = False
			For Room.tRoom = Each tRoom
				If Room\Deadlock And xEntityDistance(Room\Entity, FirstRoom\Entity) < Distance
					Distance = xEntityDistance(Room\Entity, FirstRoom\Entity)
					LastRoom = Room
					Replaced = True
					Room\Deadlock = False
				End If
			Next

			Insert LastRoom After Last tRoom
			If Replaced LevelRoomDeadlockCount = LevelRoomDeadlockCount - 1
		End If

		LastRoom\Deadlock = False
	End If

	For Room.tRoom = Each tRoom
		If Room = First tRoom
			xPositionEntity PlayerBase, xEntityX(Room\Entity), 0, xEntityZ(Room\Entity)
			xPositionEntity PlayerTarget, xEntityX(Room\Entity), 0, xEntityZ(Room\Entity)
			xPositionEntity LevelModel_Portal1, xEntityX(Room\Entity), 0, xEntityZ(Room\Entity)
			xEntityParent   LevelModel_Portal1,  Room\Entity
	
			UpdateLevelObjectsImage()
		ElseIf Room = Last tRoom
			xPositionEntity LevelModel_Portal2, xEntityX(Room\Entity), 0, xEntityZ(Room\Entity)
			xEntityParent   LevelModel_Portal2,  Room\Entity
			xHideEntity     LevelModel_Portal2
			xPositionEntity LevelModel_Plasma, xEntityX(Room\Entity), 0, xEntityZ(Room\Entity)
			xHideEntity     LevelModel_Plasma
			LastRoom = Room
		End If
		If Room\Deadlock
			NewChest.tChest = GenerateChest(Room)

			For i = 0 To Rand(1, LevelChestMaxItemSpawn)
				Select Rand(1, 3)
					Case 1 RegisterItem(Rand(02, 07), NewChest)
					Case 2 RegisterItem(Rand(11, 16), NewChest)
					Case 3 RegisterItem(Rand(20, 25), NewChest)
				End Select
			Next

			RegisterItem(PotionItemID, NewChest)

			If Rand(0, 1) RegisterItem(ManaPotionItemID, NewChest)
		End If
	Next
	LastRoom = Last tRoom

	If LastRoom <> Null And LevelRoomDeadlockCount > 0
		For i = 0 To 2
			DeadLockRoom.tRoom = GetFarestDeadLock(LastRoom)
			If DeadLockRoom <> Null
				CloseRoom(LastRoom, 1 + (9 * (2 -i)))
				RegisterItemInRoom(DeadLockRoom, 1 + (9 * (2 - i)))
				LastRoom = DeadLockRoom
			End If
			If i = 2 Or i = LevelRoomDeadlockCount - 1 Exit
		Next
	End If
End Function

Function CreateFloor()
	Local Mesh = xCreatePlane()
	Local TextID = Rand(0, 3)
	
	AttachMat(Mesh, LevelTexture_d, LevelTexture_n, TextID)


;	xSetEntityEffect         Mesh, LevelShader
;	xSetEffectTechnique      Mesh, "PointDistance"
;	xSetEffectMatrixSemantic Mesh, "MatWorldViewProj", WORLDVIEWPROJ
;	xSetEffectMatrixSemantic Mesh, "MatWorld", WORLD
;	;		Shader Varriables
;	xSetEffectVector  Mesh, "AmbientClr", AmbientLightR, AmbientLightG, AmbientLightB
;	xSetEffectVector  Mesh, "LightClr"  , LevelLightR, LevelLightG, LevelLightB
;	xSetEffectFloat   Mesh, "LightInt"  , LevelLightInt
;	xSetEffectFloat   Mesh, "RngLight"  , LevelLightRng
;	xSetEffectFloat   Mesh, "DotLight"  , 0.1
;	xSetEffectTexture Mesh, "tDiffuse"  , LevelTexture_floor_d
;	xSetEffectTexture Mesh, "tNormal"   , LevelTexture_floor_n
	Return Mesh 
End Function

Function xCreatePlane()
	Mesh = xCreateMesh()
	Surface = xCreateSurface(mesh)

	v1 = xAddVertex(Surface, 0.5, 0, 0.5, 0, 1)
	v2 = xAddVertex(Surface, 0.5, 0,-0.5, 0, 0)
	v3 = xAddVertex(Surface,-0.5, 0,-0.5, 1, 0)
	v4 = xAddVertex(Surface,-0.5, 0, 0.5, 1, 1)

	tri1 = xAddTriangle(Surface, v1, v2, v4)
	tri2 = xAddTriangle(Surface, v3, v4, v2)

	xUpdateNormals Mesh

	Return Mesh 
End Function

Function RegisterItemInRoom(SourceRoom.tRoom, ItemID)
	For Chest.tChest = Each tChest
		If Chest\Room = SourceRoom
			ChestFounded = True
			RegisterItem(ItemID, Chest)
			Exit
		End If
	Next

	If Not ChestFounded RegisterItem(ItemID, GenerateChest(SourceRoom))
End Function

Function GetFarestDeadLock.tRoom(SourceRoom.tRoom)
	Distance = 1000
	DeadLockRoom.tRoom = Null

	For Room.tRoom = Each tRoom
		If Room <> SourceRoom And Room\Deadlock And Room\ItemID = 0 And xEntityDistance(Room\Entity, SourceRoom\Entity) < Distance
			Distance = xEntityDistance(Room\Entity, SourceRoom\Entity)
			DeadLockRoom = Room
		End If
	Next

	Return DeadLockRoom
End Function

Function CloseRoom(Room.tRoom, ItemID)
	Room\ItemID = ItemID
End Function