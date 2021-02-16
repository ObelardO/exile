;==================================================================
;Project Title:  exile
;Author:         ObelardO
;Email:          obelardos@gmail.com          
;Notes:          chests code-file   
;==================================================================

Const LevelChestStatus_CLOSED = 0
Const LevelChestStatus_OPENED = 1
Const LevelChestMaxItemSpawn = 3

Global CurrentChest.tChest = Null

Global ChestModel
Global ChestTexture_d
Global ChestOpened

Const PotionItemID = 9
Const PotionEffect = 20

Const ManaPotionItemID = 18
Const ManaPotionEffect = 20

Global Sound_ChestOpen

Type tChest
	Field Entity
	Field MovePart
	Field Status
	Field Room.tRoom
End Type

Function InitChest()
	ChestModel = xLoadAnimMesh("base\models\chest.b3d")
	ChestTexture_d = xLoadTexture("base\textures\chest.jpg")
	xEntityTexture ChestModel, ChestTexture_d
	xHideEntity ChestModel

	Sound_ChestOpen = xLoadSound("base\sounds\chest\open.ogg")
End Function

Function GenerateChest.tChest (Room.tRoom)
	Chest.tChest = New tChest
	Chest\Room = Room

	Chest\Entity = xCreateCube()
	xPositionEntity Chest\Entity, xEntityX(Room\Entity), 0, xEntityZ(Room\Entity)
	xScaleEntity    Chest\Entity, 0.5, 0.5, 0.25
	xEntityParent   Chest\Entity, Room\Entity
	xEntityColor    Chest\Entity, 255, 255, 0
	xEntityPickMode Chest\Entity, PICK_TRIMESH, True
	xEntityType     Chest\Entity, LevelObjectType_Chest
	xEntityAlpha    Chest\Entity, 0.0001

	ChestCurModel = xCopyEntity(ChestModel)
	xPositionEntity ChestCurModel, xEntityX(Room\Entity), 0, xEntityZ(Room\Entity)
	xScaleEntity    ChestCurModel, 0.06, 0.065, 0.06
	xEntityParent   ChestCurModel, Chest\Entity
	xHideEntity     ChestCurModel
	Chest\MovePart = xFindChild(ChestCurModel, "top")

	AttachMat(ChestCurModel, LevelTexture_d, LevelTexture_n, 13)

	Return Chest
End Function

Function UpdateChests()
	If MouseHitL And GetLevelObjectType() = LevelObjectType_Chest
		For Chest.tChest = Each tChest
			If xPickedEntity() = Chest\Entity And xEntityDistance(PlayerBase, Chest\Entity) < 2.0
				Chest\Status = LevelChestStatus_OPENED
				PlayerStatus = PlayerStatus_Chest
				CurrentChest.tChest = Chest
				xEntityColor Chest\Entity, 255, 255, 255
				xAnimate Player, ANIMATION_PINGPONG, PlayerAnimSpeed_Wait, PlayerAnim_Wait, PlayerAnim_Smooth

				If ChestOpened = False
					xPlaySound Sound_ChestOpen
					ChestOpened = True
				End If
				Exit
			End If
		Next
	End If

	If CurrentChest <> Null
		xRotateEntity CurrentChest\MovePart, ExCurveValue(120 * CurrentChest\Status - 90, xEntityPitch(CurrentChest\MovePart), 10), 0, 90
	End If
End Function
