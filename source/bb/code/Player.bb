;==================================================================
;Project Title:  exile     
;Author:         ObelardO
;Email:          obelardos@gmail.com          
;Notes:          player code-file   
;==================================================================

Const PlayerStatus_Wait    = 0
Const PlayerStatus_MoveTo  = 1
Const PlayerStatus_PerDoor = 2
Const PlayerStatus_Chest   = 3
Const PlayerStatus_Attack  = 4

Const PlayerInvStatus_Closed = 0
Const PlayerInvStatus_Opened = 1

Global Player
Global PlayerBase
Global PlayerTarget
Global PlayerTargetDoor
Global PlayerStatus
Global PlayerPosX#
Global PlayerPosZ#
Global PlayerLight
Global PlayerInvSelect
Global PlayerDocImage
Global PlayerDocImageX
Global PlayerDocImageY
Global PlayerInvImage
Global PlayerInvImageX
Global PlayerInvImageY
Global PlayerChestImage
Global PlayerChestImageY
Global PlayerImageHealthX
Global PlayerImageHealthY
Global PlayerImageManaX
Global PlayerImageManaY
Global PlayerImageXpX

Global Player_Power#   = 0, PlayerAddPower   = 0
Global Player_Speed#   = 0, PlayerAddSpeed   = 0
Global Player_Protect# = 0, PlayerAddProtect = 0
Global Player_Attack#  = 0, PlayerAddAttack  = 0
Global Player_Mana#    = 0, PlayerAddMana    = 0
Global Player_Health#  = 100
Global PlayerManaReserve = 100
Global PlayerTotalXP# = 0

Const PlayerManaAddTime = 4000
Global PlayerLastManaTime

Global ItemAddPower   = 0
Global ItemAddSpeed   = 0
Global ItemAddProtect = 0
Global ItemAddAttack  = 0
Global ItemAddMana    = 0

Global PlayerLevel    = 1

Dim PlayerSkill(PlayerSkillCount)

Global PlayerInvStatus

Global PlayerWalkDistance#
Const PlayerWalkUseDistance = 16

Global PlayerSwordItem.tItem
Global PlayerBookItem.tItem
Global PlayerChestItem.tItem
Global PlayerHelmetItem.tItem
Global PlayerLegsItem.tItem
Global PlayerBootsItem.tItem
Global PlayerInHandItem.tItem

Const PlayerInvSlotID_Helmet = 0
Const PlayerInvSlotID_Chest  = 1
Const PlayerInvSlotID_Legs   = 2
Const PlayerInvSlotID_Boots  = 3
Const PlayerInvSlotID_sword  = 4
Const PlayerInvSlotID_Book   = 5

Const PlayerInvW = 53
Const PlayerInvH = 81
Const PlayerInvX = 167
Const PlayerInvY = 31
Const PlayerSlotPerW  = 4
Const PlayerSlotWidth = 50
Const PlayerSlotCount = 12
Const PlayerSlotReserved = 6
Const ChestSlotCount = 7
Const ChestInvX = 8
Const ChestInvY = 31
Const ChestInvW = 53
Const ChestInvH = 81

Const PlayerAnim_Wait   = 1
Const PlayerAnim_Run    = 2
Const PlayerAnim_Attack = 3

Const PlayerAnimSpeed_Wait#   = 0.1
Const PlayerAnimSpeed_Run#    = 1.2
Const PlayerAnimSpeed_Attack# = 1.0

Const PlayerAnim_Smooth# = 0.75

Const PlayerAttackTime = 500
Global PlayerLastAttackTime 

Global PlayerTargetTexture
Global PlayerModelTexture

Global PlayerSkillToUp

Const PlayerSpeedFactor# = 0.005

Global PlayerInPortal

Dim Sound_PlayerStep(9)
Global PlayerStepTime
Global PlayerStepChannel

Dim Sound_PlayerAttack(8)

Global Sound_Fireball
Global Sound_Potion

Global PlayerMagicTexture

Function InitPlayer()
	PlayerBase = xCreatePivot()

	PlayerTargetTexture = xLoadTexture("base\textures\target.jpg", FLAGS_ALPHA)
	
	PlayerTarget = xCreatePlane()
	xEntityColor   PlayerTarget, 255, 0, 0
	xScaleEntity   PlayerTarget, 2.0, 1.0, 2.0
	xEntityTexture PlayerTarget, PlayerTargetTexture
	xEntityFX      PlayerTarget, FX_FULLBRIGHT

	PlayerModelTexture = xLoadTexture("base\textures\player.jpg")

	Player = xLoadAnimMesh("base\models\player.b3d", PlayerBase); xCreateSphere(32, PlayerBase)
	xScaleEntity   Player, 0.4, 0.4, 0.4
	xEntityTexture Player, PlayerModelTexture

	xExtractAnimSeq Player, 2,  4
	xExtractAnimSeq Player, 20, 59
	xExtractAnimSeq Player, 99, 129

;	PlayerLight = xCreateLight(LIGHT_SPOT)
;	xEntityParent PlayerLight, PlayerBase
;	xPointEntity  PlayerLight, PlayerBase
;	xLightRange   PlayerLight, 30
;	xLightColor   PlayerLight, 255, 255, 255
;	xMoveEntity   PlayerLight, 0, 10, 0

	PlayerInvImage   = xLoadImage("base\images\inv.png")
	PlayerChestImage = xLoadImage("base\images\chest.png")
	PlayerInvSelect  = xLoadImage("base\images\selectitem.png")

	PlayerInvImageX = (xGraphicsWidth() - xImageWidth(PlayerInvImage)) / 2.0
	PlayerInvImageY = (xGraphicsHeight() - xImageHeight(PlayerInvImage) * 1.5) / 2.0

	PlayerChestImageY = PlayerInvImageY + xImageHeight(PlayerInvImage) + 31

	PlayerDocImage  = xLoadImage("base\images\toolbar.png")
	PlayerDocImageX = (xGraphicsWidth() - xImageWidth(PlayerDocImage)) / 2.0
	PlayerDocImageY = xGraphicsHeight() - xImageHeight(PlayerDocImage)

	PlayerImageHealthX = 25
 	PlayerImageHealthY = xGraphicsHeight() - 75
 	PlayerImageManaX = xGraphicsWidth() - 225
 	PlayerImageManaY = PlayerImageHealthY

	PlayerImageXpX = xGraphicsWidth() / 2 - 100

	For i = 0 To 9
		Sound_PlayerStep(i) = xLoadSound("base\sounds\steps\" + (i + 1) + ".ogg")
	Next

	For i = 0 To 8
		Sound_PlayerAttack(i) = xLoadSound("base\sounds\attack\" + (i + 1) + ".ogg")
	Next

	Sound_Potion = xLoadSound("base\sounds\items\Potion.wav")

	PlayerMagicTexture = xLoadAnimTexture("base\textures\magic.jpg", 2, 64, 64, 0, 16)
	Sound_Fireball = xLoadSound("base\sounds\magic\fireball.ogg")
End Function

Function ResetPlayer()
	For Item.tItem = Each tItem
		Delete Item
	Next

	PlayerPosX = 0
	PlayerPosZ = 0

	PlayerSwordItem = Null
	PlayerBookItem = Null
	PlayerChestItem = Null
	PlayerHelmetItem = Null
	PlayerLegsItem = Null
	PlayerBootsItem = Null
	PlayerInHandItem = Null

	PlayerWalkDistance = 0

	Player_Power    = 0
	Player_Speed    = 0
	Player_Protect  = 0
	Player_Attack   = 0
	Player_Mana#    = 0
	Player_Health#  = 100
	PlayerManaReserve = 100
	PlayerTotalXP   = 0
	
	PlayerAddPower   = 0
	PlayerAddSpeed   = 0
	PlayerAddProtect = 0
	PlayerAddAttack  = 0
	PlayerAddMana    = 0
	
	ItemAddPower   = 0
	ItemAddSpeed   = 0
	ItemAddProtect = 0
	ItemAddAttack  = 0
	ItemAddMana    = 0
	
	PlayerLevel    = 1

	PlayerInvStatus = 0

	PlayerStatus = PlayerStatus_Wait
End Function

Function UpdatePlayerStats()
	PlayerSkill(PlayerSkill_Power)   = 0
	PlayerSkill(PlayerSkill_Speed)   = 0
	PlayerSkill(PlayerSkill_Health)  = 0
	PlayerSkill(PlayerSkill_Protect) = 0
	PlayerSkill(PlayerSkill_Attack)  = 0
	PlayerSkill(PlayerSkill_Mana)    = 0
		
	If PlayerSwordItem  <> Null PlayerSkill(PlayerSwordItem\ItemType\GiveSkill)  = PlayerSkill(PlayerSwordItem\ItemType\GiveSkill)  + PlayerSwordItem\ItemType\Class
	If PlayerBookItem   <> Null PlayerSkill(PlayerBookItem\ItemType\GiveSkill)   = PlayerSkill(PlayerBookItem\ItemType\GiveSkill)   + PlayerBookItem\ItemType\Class
	If PlayerChestItem  <> Null PlayerSkill(PlayerChestItem\ItemType\GiveSkill)  = PlayerSkill(PlayerChestItem\ItemType\GiveSkill)  + PlayerChestItem\ItemType\Class
	If PlayerHelmetItem <> Null PlayerSkill(PlayerHelmetItem\ItemType\GiveSkill) = PlayerSkill(PlayerHelmetItem\ItemType\GiveSkill) + PlayerHelmetItem\ItemType\Class
	If PlayerLegsItem   <> Null PlayerSkill(PlayerLegsItem\ItemType\GiveSkill)   = PlayerSkill(PlayerLegsItem\ItemType\GiveSkill)   + PlayerLegsItem\ItemType\Class
	If PlayerBootsItem  <> Null PlayerSkill(PlayerBootsItem\ItemType\GiveSkill)  = PlayerSkill(PlayerBootsItem\ItemType\GiveSkill)  + PlayerBootsItem\ItemType\Class

	Player_Power   = 1.00 + PlayerAddPower   + PlayerSkill(PlayerSkill_Power)
	Player_Speed   = 1.00 + PlayerAddSpeed   + PlayerSkill(PlayerSkill_Speed)
	Player_Protect = 1.00 + PlayerAddProtect + PlayerSkill(PlayerSkill_Protect) * 0.1 * (PlayerAddProtect + 1)
	Player_Attack  = 1.00 + PlayerAddAttack  + PlayerSkill(PlayerSkill_Attack)
	Player_Mana    = 1.00 + PlayerAddMana    + PlayerSkill(PlayerSkill_Mana)

	DebugLog "~~~~~~~~~~~~~~~~~~~~~~~~"
	DebugLog "skill " + PlayerSkillName(PlayerSkill_Power) +   " = " + Player_Power
	DebugLog "skill " + PlayerSkillName(PlayerSkill_Speed) +   " = " + Player_Speed
	DebugLog "skill " + PlayerSkillName(PlayerSkill_Health) +  " = " + Player_Health
	DebugLog "skill " + PlayerSkillName(PlayerSkill_Protect) + " = " + Player_Protect
	DebugLog "skill " + PlayerSkillName(PlayerSkill_Attack) +  " = " + Player_Attack
	DebugLog "skill " + PlayerSkillName(PlayerSkill_Mana) +    " = " + Player_Mana
End Function

Function PlayerDamage(Damage#)
	Player_Health = Player_Health - (Damage - Player_Protect)
	PlayerUseItem(PlayerChestItem)
	PlayerUseItem(PlayerLegsItem)
	PlayerUseItem(PlayerHelmetItem)

	If Player_Health <= 0 GameStatus = GameStatus_GameOver xPlaySound Sound_MenuGameOver
	DebugLog "damage! :  " + Damage + " .... " + (Damage - Player_Protect)
End Function

Function GetLevelObjectType()
	;If ForcePickLock Return False
	Local Entity = xPickedEntity()
	If Entity Return xGetEntityType(Entity)
End Function

Function DrawPlayerInventory(OnlyUpdate = False)
	Local SlotX, SlotY

	If OnlyUpdate = False
		xColor 0, 0, 0, 128
		xRect 0, 0, xGraphicsWidth(), xGraphicsHeight(), True
	
		xDrawImage PlayerInvImage, PlayerInvImageX, PlayerInvImageY
		If CurrentChest <> Null
			If CurrentChest\Status = LevelChestStatus_OPENED xDrawImage PlayerChestImage, PlayerInvImageX, PlayerChestImageY
		End If
	
		ExDrawColorText("^4" + PlayerSkillName(PlayerSkill_Power)  + ":", PlayerInvImageX + 20, PlayerInvImageY + 120, Fnt_s)
		ExDrawColorText("^4" + PlayerSkillName(PlayerSkill_Speed)  + ":", PlayerInvImageX + 20, PlayerInvImageY + 140, Fnt_s)
		ExDrawColorText("^4" + PlayerSkillName(PlayerSkill_Protect)+ ":", PlayerInvImageX + 20, PlayerInvImageY + 160, Fnt_s)
		ExDrawColorText("^4" + PlayerSkillName(PlayerSkill_Attack) + ":", PlayerInvImageX + 20, PlayerInvImageY + 180, Fnt_s)
		ExDrawColorText("^4" + PlayerSkillName(PlayerSkill_Mana)   + ":", PlayerInvImageX + 20, PlayerInvImageY + 200, Fnt_s)
		ExDrawColorText("^4" + PlayerSkillName(PlayerSkill_Health) + ":", PlayerInvImageX + 20, PlayerInvImageY + 220, Fnt_s)


		ExDrawColorText("^9" + (PlayerAddPower + 1),  PlayerInvImageX + 110, PlayerInvImageY + 120 ,Fnt_s, TEXT_ALIGN_RIGHT)
		ExDrawColorText("^9" + (PlayerAddSpeed + 1),  PlayerInvImageX + 110, PlayerInvImageY + 140, Fnt_s, TEXT_ALIGN_RIGHT)
		ExDrawColorText("^9" + (PlayerAddProtect + 1),PlayerInvImageX + 110, PlayerInvImageY + 160, Fnt_s, TEXT_ALIGN_RIGHT)
		ExDrawColorText("^9" + (PlayerAddAttack + 1), PlayerInvImageX + 110, PlayerInvImageY + 180, Fnt_s, TEXT_ALIGN_RIGHT)
		ExDrawColorText("^9" + (PlayerAddMana + 1),   PlayerInvImageX + 110, PlayerInvImageY + 200, Fnt_s, TEXT_ALIGN_RIGHT)

	
		ExDrawColorText("^1" + Player_Power,   PlayerInvImageX + 150, PlayerInvImageY + 120, Fnt_s, TEXT_ALIGN_RIGHT)
		ExDrawColorText("^1" + Player_Speed ,  PlayerInvImageX + 150, PlayerInvImageY + 140, Fnt_s, TEXT_ALIGN_RIGHT)
		ExDrawColorText("^1" + Player_Protect, PlayerInvImageX + 150, PlayerInvImageY + 160, Fnt_s, TEXT_ALIGN_RIGHT)
		ExDrawColorText("^1" + Player_Attack,  PlayerInvImageX + 150, PlayerInvImageY + 180, Fnt_s, TEXT_ALIGN_RIGHT)
		ExDrawColorText("^1" + Player_Mana,    PlayerInvImageX + 150, PlayerInvImageY + 200, Fnt_s, TEXT_ALIGN_RIGHT)
		ExDrawColorText("^1" + Player_Health,  PlayerInvImageX + 150, PlayerInvImageY + 220, Fnt_s, TEXT_ALIGN_RIGHT)
	
		ExDrawColorText("^4" + PlayerLevel, PlayerInvImageX + 41, PlayerInvImageY + 18, Fnt_m, TEXT_ALIGN_CENTER)
	
		PlayerSlotID = GetPlayerSlotID(MousePosX, MousePosY)
		If PlayerSlotID < PlayerSlotCount
			SlotX = PlayerInvImageX + (PlayerSlotID Mod PlayerSlotPerW) * PlayerInvW + PlayerInvX
			SlotY = PlayerInvImageY + (PlayerSlotID  /  PlayerSlotPerW) * PlayerInvH + PlayerInvY
			xDrawImage PlayerInvSelect, SlotX, SlotY
		End If
	
		ChestSlotID = GetChestSlotID(MousePosX, MousePosY)
		If ChestSlotID < ChestSlotCount And CurrentChest <> Null
			SlotY = PlayerChestImageY + ChestInvY
			SlotX = PlayerInvImageX + (ChestSlotID * ChestInvW) + ChestInvX
			xDrawImage PlayerInvSelect, SlotX, SlotY
		End If

	End If

	For Item.tItem = Each tItem

		If Item\InInvetory

			If Item\SlotX = 0 And Item\SlotY = 0
			
				SlotID = PlayerSlotReserved

				Select Item\ItemType\Kind
					Case ItemKind_Book    If CheckSlotEmpty(PlayerInvSlotID_Book,   Null) SlotID = PlayerInvSlotID_Book  
					Case ItemKind_Sword   If CheckSlotEmpty(PlayerInvSlotID_sword,  Null) SlotID = PlayerInvSlotID_sword 
					Case ItemKind_Helmet  If CheckSlotEmpty(PlayerInvSlotID_Helmet, Null) SlotID = PlayerInvSlotID_Helmet
					Case ItemKind_Chest   If CheckSlotEmpty(PlayerInvSlotID_Chest,  Null) SlotID = PlayerInvSlotID_Chest 
					Case ItemKind_Legs    If CheckSlotEmpty(PlayerInvSlotID_Legs,   Null) SlotID = PlayerInvSlotID_Legs  
					Case ItemKind_Boots   If CheckSlotEmpty(PlayerInvSlotID_Boots,  Null) SlotID = PlayerInvSlotID_Boots
				End Select
				
				While CheckSlotEmpty(SlotID, Null) = False
					SlotID = SlotID + 1
					If SlotID = PlayerSlotCount
						DebugLog "Õ≈“ Ã≈—“¿"
						Delete item
						Return False
					End If
				Wend

				UpdatePlayerItems(SlotID, item)

				Item\SlotX = PlayerInvImageX + (SlotID Mod PlayerSlotPerW) * PlayerInvW + PlayerInvX
				Item\SlotY = PlayerInvImageY + (SlotID  /  PlayerSlotPerW) * PlayerInvH + PlayerInvY
			End If

;			If PointInRegion(MousePosX, MousePosY, Item\SlotX, Item\SlotY, PlayerSlotWidth, PlayerSlotWidth)
;				xDrawImage PlayerInvSelect, Item\SlotX, Item\SlotY
;			End If
			
			If OnlyUpdate = False DrawItem(Item\SlotX, Item\SlotY, Item)
			
		ElseIf Item\Chest = CurrentChest And CurrentChest <> Null
			If Item\SlotX = 0 And Item\SlotY = 0
				SlotID = 0
				While CheckSlotEmpty(SlotID, Item\Chest, False) = False
					SlotID = SlotID + 1
					If SlotID = ChestSlotCount
						DebugLog "Õ≈“ Ã≈—“¿"
						Delete item
						Return False
					End If
				Wend
				Item\SlotY = PlayerChestImageY + ChestInvY
				Item\SlotX = PlayerInvImageX + (SlotID * ChestInvW) + ChestInvX
			ElseIf CurrentChest\Status = LevelChestStatus_OPENED
				If PointInRegion(MousePosX, MousePosY, Item\SlotX, Item\SlotY, PlayerSlotWidth, PlayerSlotWidth)
					xDrawImage PlayerInvSelect, Item\SlotX, Item\SlotY
				End If
			
				If OnlyUpdate = False DrawItem(Item\SlotX, Item\SlotY, Item)
			End If
		End If
	Next

	If PlayerInHandItem <> Null
		DrawItem(MousePosX - PlayerSlotWidth / 2, MousePosY - PlayerSlotWidth / 2, PlayerInHandItem)
	End If

	If OnlyUpdate = True Return 

	;SlotID = GetPlayerSlotID(MousePosX, MousePosY)

	If PlayerSlotID < PlayerSlotCount

		SlotX = PlayerInvImageX + (PlayerSlotID Mod PlayerSlotPerW) * PlayerInvW + PlayerInvX
		SlotY = PlayerInvImageY + (PlayerSlotID  /  PlayerSlotPerW) * PlayerInvH + PlayerInvY
	
;		If CheckSlotEmpty(SlotID, Null) ;SlotSelected = False And 
;;			xColor 255, 255, 255, 64
;;			;xRect SlotX, SlotY, PlayerSlotWidth, PlayerSlotWidth, True
;			xDrawImage PlayerInvSelect, SlotX, SlotY
;		End If

		If MouseHitL
			If Not CheckSlotEmpty(PlayerSlotID, Null)
				For Item.tItem = Each tItem
					If SlotX = Item\SlotX And SlotY = Item\SlotY
						If PlayerInHandItem = Null 
							PlayerInHandItem = item
							item\InInvetory = False
							Item\SlotX = 0
							Item\SlotY = 0

							UpdatePlayerItems(PlayerSlotID, Null)

							xPlaySound Sound_UiTick
						ElseIf CheckSlotReserved(PlayerSlotID, PlayerInHandItem) = False
							Local TempUsePoints = item\UsePoints
							Local TempItemType.tItemType  = item\ItemType
		
							item\UsePoints  = PlayerInHandItem\UsePoints
							item\ItemType   = PlayerInHandItem\ItemType
		
							PlayerInHandItem\UsePoints = TempUsePoints
							PlayerInHandItem\ItemType  = TempItemType

							UpdatePlayerItems(PlayerSlotID, item)

							xPlaySound Sound_UiTack
						End If
					End If
				Next

			ElseIf PlayerInHandItem <> Null
				If CheckSlotReserved(PlayerSlotID, PlayerInHandItem) = False
					NewItem.tItem = New tItem
					NewItem\SlotX = SlotX
					NewItem\SlotY = SlotY
					NewItem\InInvetory = True
					NewItem\UsePoints = PlayerInHandItem\UsePoints
					NewItem\ItemType.tItemType = PlayerInHandItem\ItemType
	
					Delete PlayerInHandItem

					UpdatePlayerItems(PlayerSlotID, NewItem)
	
					MouseHitL = False

					xPlaySound Sound_UiTack
				End If
			End If
		Else
			For Item.tItem = Each tItem
				If SlotX = Item\SlotX And SlotY = Item\SlotY
					DrawItemInfo(Item, MousePosX, MousePosY)
					If MouseHitR
						If Item\ItemType\ID = PotionItemID PlayerUseItem(Item) ElseIf Item\ItemType\ID = ManaPotionItemID PlayerUseItem(Item)
					End If
				End If
			Next
		End If

	ElseIf CurrentChest <> Null
		If CurrentChest\Status = LevelChestStatus_OPENED And ChestSlotID < ChestSlotCount + 1 

			SlotY = PlayerChestImageY + ChestInvY
			SlotX = PlayerInvImageX + (ChestSlotID * ChestInvW) + ChestInvX
	
;			If SlotSelected = False
;			xColor 255, 255, 255, 64
;			;xRect SlotX, SlotY, PlayerSlotWidth, PlayerSlotWidth, True
;			xDrawImage PlayerInvSelect, SlotX, SlotY
;			End If

			If MouseHitL
				If Not CheckSlotEmpty(ChestSlotID, CurrentChest, False)
					For Item.tItem = Each tItem
						If SlotX = Item\SlotX And SlotY = Item\SlotY And Item\Chest = CurrentChest
							If PlayerInHandItem = Null 
								PlayerInHandItem = item
								item\InInvetory = False
								item\Chest = Null
								Item\SlotX = 0
								Item\SlotY = 0

								xPlaySound Sound_UiTick
							Else
								TempUsePoints = item\UsePoints
								TempItemType.tItemType  = item\ItemType
			
								item\UsePoints = PlayerInHandItem\UsePoints
								item\ItemType  = PlayerInHandItem\ItemType
								item\Chest     = CurrentChest
								item\InInvetory = False
			
								PlayerInHandItem\UsePoints = TempUsePoints
								PlayerInHandItem\ItemType  = TempItemType

								xPlaySound Sound_UiTack
							End If
						End If
					Next
	
				ElseIf PlayerInHandItem <> Null 
					NewItem.tItem = New tItem
					NewItem\SlotX = SlotX
					NewItem\SlotY = SlotY
					NewItem\InInvetory = False
					NewItem\UsePoints = PlayerInHandItem\UsePoints
					NewItem\ItemType.tItemType = PlayerInHandItem\ItemType
					NewItem\Chest = CurrentChest
					Delete PlayerInHandItem		
					MouseHitL = False

					xPlaySound Sound_UiTack
				End If
			Else
				For Item.tItem = Each tItem
					If Item\Chest = CurrentChest And item\InInvetory = False
						If SlotX = Item\SlotX And SlotY = Item\SlotY
							DrawItemInfo(Item, MousePosX, MousePosY)
							If MouseHitR
								If Item\ItemType\ID = PotionItemID PlayerUseItem(Item) ElseIf Item\ItemType\ID = ManaPotionItemID PlayerUseItem(Item)
							End If
						End If
					End If
				Next
			End If
		End If
	End If
End Function

Function PlayerPickUpItem(ItemID)
	For Item.tItem = Each tItem
		If Item\InInvetory And Item\ItemType\ID = ItemID
			PlayerUseItem(Item)
			Return True
		End If
	Next

	Return False
End Function

Function PlayerUseItem(Item.tItem)
	If Item = Null Return False

	Item\UsePoints = Item\UsePoints - 1
	DebugLog "ËÒÔÓÎ¸ÁÓ‚‡Ì " + Item\ItemType\ID + "   ÓÒÚ‡ÎÓÒ¸: " + Item\UsePoints

	If Item\UsePoints = 0
		If Item\ItemType\ID = PotionItemID
			Player_Health = Player_Health + PotionEffect
			If Player_Health > 100 Player_Health = 100
			xPlaySound Sound_Potion
		End If

		If Item\ItemType\ID = ManaPotionItemID
			PlayerManaReserve = PlayerManaReserve + ManaPotionEffect
			If PlayerManaReserve > 100 PlayerManaReserve = 100
			xPlaySound Sound_Potion
		End If
	
		Delete Item
		UpdatePlayerStats()
	End If
End Function

Function DrawItem(x, y, Item.tItem, NotInInv = False)
	Local Wear#
	xDrawImage ItemImage, x, y, Item\ItemType\ID
	If Item\ItemType\UsePoints > 1
		Wear = Float(Item\UsePoints) / Item\ItemType\UsePoints
		If NotInInv
			xScaleImage Img_progess, Wear, 1.0
			xDrawImage  Img_progess, x - 9, y - 18
		Else
			
			;xColor 255 - 255 * Wear, 255, 0, 200
			xColor 245, 159 - 80 * (1.0 - wear), 58, 200
			xRect x, y + PlayerSlotWidth - 5,  PlayerSlotWidth *  Wear, 5, True
		End If
	End If
End Function

Function UpdatePlayerItems(SlotID, Item.tItem)
	If Item <> Null
		Select SlotID
			Case PlayerInvSlotID_Helmet If Item\ItemType\kind = ItemKind_Helmet PlayerHelmetItem = item
			Case PlayerInvSlotID_Chest  If Item\ItemType\kind = ItemKind_Chest  PlayerChestItem  = item
			Case PlayerInvSlotID_Legs   If Item\ItemType\kind = ItemKind_Legs   PlayerLegsItem   = item
			Case PlayerInvSlotID_Boots  If Item\ItemType\kind = ItemKind_Boots  PlayerBootsItem  = item
			Case PlayerInvSlotID_sword  If Item\ItemType\kind = ItemKind_Sword  PlayerSwordItem  = item
			Case PlayerInvSlotID_Book   If Item\ItemType\kind = ItemKind_Book   PlayerBookItem   = item
		End Select
	Else
		Select SlotID
			Case PlayerInvSlotID_Helmet PlayerHelmetItem = Null
			Case PlayerInvSlotID_Chest  PlayerChestItem  = Null
			Case PlayerInvSlotID_Legs   PlayerLegsItem   = Null
			Case PlayerInvSlotID_Boots  PlayerBootsItem  = Null
			Case PlayerInvSlotID_sword  PlayerSwordItem  = Null
			Case PlayerInvSlotID_Book   PlayerBookItem   = Null
		End Select
	End If

	UpdatePlayerStats()
End Function

Function CheckSlotReserved(SlotID, Item.tItem)
	If SlotID < PlayerSlotReserved
		DebugLog "item skill: " + PlayerSkillName(Item\ItemType\NeedSkill) + " " + Item\ItemType\Class
		DebugLog "plar skill: " + PlayerSkillName(Item\ItemType\NeedSkill) + " " + GetPlayerSkillBuf(Item\ItemType\NeedSkill)
	
		Select SlotID
			Case PlayerInvSlotID_Helmet If Item\ItemType\Kind <> ItemKind_Helmet Or GetPlayerSkillBuf(Item\ItemType\NeedSkill) < Item\ItemType\Class Return True
			Case PlayerInvSlotID_Chest  If Item\ItemType\Kind <> ItemKind_Chest  Or GetPlayerSkillBuf(Item\ItemType\NeedSkill) < Item\ItemType\Class Return True
			Case PlayerInvSlotID_Legs   If Item\ItemType\Kind <> ItemKind_Legs   Or GetPlayerSkillBuf(Item\ItemType\NeedSkill) < Item\ItemType\Class Return True
			Case PlayerInvSlotID_Boots  If Item\ItemType\Kind <> ItemKind_Boots  Or GetPlayerSkillBuf(Item\ItemType\NeedSkill) < Item\ItemType\Class Return True
			Case PlayerInvSlotID_sword  If Item\ItemType\Kind <> ItemKind_Sword  Or GetPlayerSkillBuf(Item\ItemType\NeedSkill) < Item\ItemType\Class Return True
			Case PlayerInvSlotID_Book   If Item\ItemType\Kind <> ItemKind_Book   Or GetPlayerSkillBuf(Item\ItemType\NeedSkill) < Item\ItemType\Class Return True
		End Select
	End If

	Return False
End Function

Function GetPlayerSkillBuf(SkillID)
	Select SkillID
		Case 1 Return PlayerAddPower   + 1
		Case 2 Return PlayerAddSpeed   + 1
		Case 4 Return PlayerAddProtect + 1
		Case 5 Return PlayerAddAttack  + 1
		Case 6 Return PlayerAddMana    + 1			 
	End Select
End Function

Function CheckSlotEmpty(SlotID, Chest.tChest, InInventory = True)
	Local SlotX, SlotY

	If InInventory = True
		SlotX = PlayerInvImageX + (SlotID Mod PlayerSlotPerW) * PlayerInvW + PlayerInvX
		SlotY = PlayerInvImageY + (SlotID  /  PlayerSlotPerW) * PlayerInvH + PlayerInvY

		For Item.tItem = Each tItem
			If Item\InInvetory = InInventory
				If Item\SlotX = SlotX And Item\SlotY = SlotY Return False
			End If
		Next
		
	ElseIf Chest <> Null
		SlotY = PlayerChestImageY + ChestInvY
		SlotX = PlayerInvImageX + (SlotID * ChestInvW) + ChestInvX
		
		For Item.tItem = Each tItem
			If Item\Chest = Chest
				If Item\SlotX = SlotX And Item\SlotY = SlotY Return False
			End If
		Next
	End If

	Return True
End Function

Function GetPlayerSlotID(x, y)
	Local SlotX, SlotY

	For SlotID = 0 To PlayerSlotCount
		SlotX = PlayerInvImageX + (SlotID Mod PlayerSlotPerW) * PlayerInvW + PlayerInvX
		SlotY = PlayerInvImageY + (SlotID  /  PlayerSlotPerW) * PlayerInvH + PlayerInvY

		If PointInRegion(x, y, SlotX, SlotY, PlayerSlotWidth, PlayerSlotWidth)
			Return SlotID
		End If
	Next

	Return PlayerSlotCount + 1
End Function

Function GetChestSlotID(x, y)
	Local SlotX, SlotY

	SlotY = PlayerChestImageY + ChestInvY

	For SlotID = 0 To ChestSlotCount
		SlotX = PlayerInvImageX + (SlotID * ChestInvW) + ChestInvX

		If PointInRegion(x, y, SlotX, SlotY, PlayerSlotWidth, PlayerSlotWidth)
			Return SlotID
		End If
	Next

	Return ChestSlotCount + 1
End Function

Function UpdatePlayerInterface()
	xDrawImage Img_Overlay, 0, 0
	xDrawImage PlayerDocImage, PlayerDocImageX, PlayerDocImageY

	If PlayerSwordItem  <> Null DrawItem(PlayerDocImageX + 148, PlayerDocImageY + 40,  PlayerSwordItem, True)
	If PlayerBookItem   <> Null DrawItem(PlayerDocImageX + 215, PlayerDocImageY + 40,  PlayerBookItem, True)

	If PlayerHelmetItem <> Null DrawItem(PlayerDocImageX + 12, PlayerDocImageY + 40,  PlayerHelmetItem, True)
	If PlayerChestItem  <> Null DrawItem(PlayerDocImageX + 80, PlayerDocImageY + 40,  PlayerChestItem, True)

	If PlayerLegsItem   <> Null DrawItem(PlayerDocImageX + 284, PlayerDocImageY + 40,  PlayerLegsItem, True)
	If PlayerBootsItem  <> Null DrawItem(PlayerDocImageX + 352, PlayerDocImageY + 40,  PlayerBootsItem, True)

	xColor 169,0,0, 200
	xRect PlayerImageHealthX + 39, PlayerImageHealthY + 19, (Player_Health / 100) * 127, 13, True
	xDrawImage Img_flask, PlayerImageHealthX, PlayerImageHealthY
	
	xColor 0,0,169, 200
	xRect PlayerImageManaX + 39  + 127 - (Float(PlayerManaReserve) / 100) * 127, PlayerImageManaY + 19, (Float(PlayerManaReserve) / 100) * 127, 13, True
	xDrawImage Img_flask, PlayerImageManaX, PlayerImageManaY

	xColor 169, 169, 0, 200
	xRect PlayerImageXpX + 39, 39, (PlayerTotalXP / 100) * 127, 13, True
	xDrawImage Img_flask, PlayerImageXpX, 20, 0
End Function

Function UpdatePlayer()
	MouseHitL = xMouseHit(MOUSE_LEFT)
	MouseHitR = xMouseHit(MOUSE_RIGHT)
	MouseDownL = xMouseDown(MOUSE_LEFT)
	MousePosX = xMouseX()
	MousePosY = xMouseY()

	KeyInvHit = xKeyHit(KEY_E)

	PlayerPosX = xEntityX(PlayerBase)
	PlayerPosZ = xEntityZ(PlayerBase)
	PlayerYaw# = xEntityYaw(PlayerBase)

	UpdatePlayerInterface()

	If xKeyDown(KEY_TAB) And ConsoleMode = False And PlayerStatus < PlayerStatus_Chest And PlayerInvStatus = False
		xColor 0, 0, 0, 128
		xRect 0, 0, xGraphicsWidth(), xGraphicsHeight(), True
		xDrawImage LevelMapImage, xGraphicsWidth() / 2, xGraphicsHeight() / 2
	End If

	If GameStatus <> GameStatus_Pause
		If PointInRegion(MousePosX, MousePosY, 10, 2, 80, 80)
			xDrawImage QuickButtons, 0, 0, 2
			If MouseHitL GameStatus = GameStatus_Pause xPlaySound Sound_UiTick
			MouseHitL = False
			
		Else
			xDrawImage QuickButtons, 0, 0, 0
		End If

		If PointInRegion(MousePosX, MousePosY, WindowWidth - 90, 2, 80, 80)
			xDrawImage QuickButtons, WindowWidth - 150, 0, 3
			If MouseHitL KeyInvHit = True xPlaySound Sound_UiTick
			MouseHitL = False
			
		Else
			xDrawImage QuickButtons, WindowWidth - 150, 0, 1
		End If
	End If

	If KeyInvHit And ConsoleMode = False And PlayerStatus < PlayerStatus_Chest PlayerInvStatus = Not PlayerInvStatus

	If PlayerInvStatus
		DrawPlayerInventory()
		MouseHitL = False

		If PointInRegion(MousePosX, MousePosY, 10, 2, 80, 80)
			xDrawImage QuickButtons, 0, 0, 2
			If MouseHitL GameStatus = GameStatus_Pause xPlaySound Sound_UiTack
			MouseHitL = False
			
		Else
			xDrawImage QuickButtons, 0, 0, 0
		End If

		If PointInRegion(MousePosX, MousePosY, WindowWidth - 90, 2, 80, 80)
			xDrawImage QuickButtons, WindowWidth - 150, 0, 3
			If MouseHitL KeyInvHit = True xPlaySound Sound_UiTack
			MouseHitL = False
			
		Else
			xDrawImage QuickButtons, WindowWidth - 150, 0, 1
		End If
	Else

		If (MouseHitL Or MouseHitR) And PlayerStatus < PlayerStatus_Chest
			
			Entity = xCameraPick(Camera, MousePosX, MousePosY)
	
			If GetLevelObjectType() = LevelObjectType_Floor
				xPositionEntity PlayerTarget, xPickedX(), 0.1, xPickedZ()
				xPointEntity    PlayerTarget, PlayerBase
				xTurnEntity     PlayerTarget, 0, 180, 0
				xRotateEntity   PlayerTarget, 0, xEntityYaw(PlayerTarget), 0

				If PlayerStatus = PlayerStatus_PerDoor Or PlayerStatus = PlayerStatus_MoveTo DontAnimate = True
				;And  xAnimate Player, ANIMATION_PINGPONG, PlayerAnimSpeed_Wait, PlayerAnim_Wait, PlayerAnim_Smooth
				PlayerStatus = PlayerStatus_Wait
			End If
		End If
	
		Select PlayerStatus
			Case PlayerStatus_Wait
				If MouseHitL
					If xEntityDistance(PlayerBase, PlayerTarget) > 0.5
		
						If CurrentRoom <> Null
							If PointInRegion(xEntityX(PlayerTarget),-xEntityZ(PlayerTarget), CurrentRoom\X - 1, CurrentRoom\Y - 1, CurrentRoom\W + 2, CurrentRoom\H + 2)
								PlayerStatus = PlayerStatus_MoveTo
								If Not DontAnimate xAnimate Player, ANIMATION_LOOP, PlayerAnimSpeed_Run, PlayerAnim_Run, PlayerAnim_Smooth
							Else
								Door.tDoor = GetUnitingDoor(CurrentRoom, GetRoomByCords(xEntityX(PlayerTarget),-xEntityZ(PlayerTarget) ))
		
								If Door <> Null 
									PlayerTargetDoor = Door\Entity
									PlayerStatus = PlayerStatus_PerDoor
									If Not DontAnimate xAnimate Player, ANIMATION_LOOP, PlayerAnimSpeed_Run, PlayerAnim_Run, PlayerAnim_Smooth
									UpdateLevelObjectsImage()
								End If
							End If
						End If
					End If
					Return
				End If

				If PlayerInPortal
					If xEntityDistance(LevelModel_Portal2, PlayerBase) > 1.5
						PlayerInPortal = False
					End If
				Else
					If xEntityDistance(LevelModel_Portal2, PlayerBase) < 1.0
						PlayerInPortal = True
						GameStatus = GameStatus_NextLevel
						xPlaySound Sound_MenuLevelEnd
					End If
				End If
				
			Case PlayerStatus_PerDoor
				xTurnEntity PlayerBase, 0, xDeltaYaw(PlayerBase, PlayerTargetDoor) * 0.25, 0
	
				If xEntityDistance(PlayerBase, PlayerTargetDoor) > 0.8
					xMoveEntity PlayerBase, 0, 0, 0.05 + Player_Speed * PlayerSpeedFactor

					PlayerWalkDistance = PlayerWalkDistance + 0.05 + Player_Speed * PlayerSpeedFactor
				Else
					PlayerStatus = PlayerStatus_MoveTo
					UpdateLevelObjectsImage()
				End If

				If PlayerStepTime < GameMilliSecs
					PlayerStepTime = GameMilliSecs + 500
					PlayerStepChannel = xPlaySound(Sound_PlayerStep(Rand(0, 9)))
					xChannelVolume PlayerStepChannel, 0.35
				End If
			
			Case PlayerStatus_MoveTo
	
				xTurnEntity PlayerBase, 0, xDeltaYaw(PlayerBase, PlayerTarget) * 0.25, 0
	
				If xEntityDistance(PlayerBase, PlayerTarget) > 0.5
					xMoveEntity PlayerBase, 0, 0, 0.05 + Player_Speed * PlayerSpeedFactor

					PlayerWalkDistance = PlayerWalkDistance + 0.05 + Player_Speed * PlayerSpeedFactor
				Else
					xAnimate Player, ANIMATION_PINGPONG, PlayerAnimSpeed_Wait, PlayerAnim_Wait, PlayerAnim_Smooth
					PlayerStatus = PlayerStatus_Wait
					UpdateLevelObjectsImage()
				End If

				If PlayerStepTime < GameMilliSecs
					PlayerStepTime = GameMilliSecs + 500
					PlayerStepChannel = xPlaySound(Sound_PlayerStep(Rand(0, 9)))
					xChannelVolume PlayerStepChannel, 0.35
				End If
	
			Case PlayerStatus_Chest
				If KeyInvHit
					PlayerStatus = PlayerStatus_Wait
					
					If CurrentChest <> Null
						CurrentChest\Status = LevelChestStatus_CLOSED
						xEntityColor CurrentChest\Entity, 255, 255, 0
						ChestOpened = False
						xPlaySound Sound_ChestOpen
						;CurrentChest = Null
					End If
				End If

				If CurrentChest <> Null DrawPlayerInventory()

			Case PlayerStatus_Attack
				If GameMilliSecs > PlayerLastAttackTime
					xAnimate Player, ANIMATION_PINGPONG, PlayerAnimSpeed_Wait, PlayerAnim_Wait, PlayerAnim_Smooth
					PlayerStatus = PlayerStatus_Wait

					xPlaySound Sound_PlayerAttack(Rand(0, 8))
				End If
	
		End Select

		xEntityAlpha PlayerTarget, xEntityDistance(PlayerBase, PlayerTarget) / 10

		If PlayerWalkDistance >= PlayerWalkUseDistance
			PlayerUseItem(PlayerBootsItem)
			PlayerWalkDistance = 0
		End If
	End If

	If PlayerTotalXP >= 100
		PlayerLevel = PlayerLevel + 1
		GameStatus = GameStatus_LevelUp
		PlayerSkillToUp = 0
		xPlaySound Sound_MenuLevelUp
	End If

	If PlayerLastManaTime < GameMilliSecs And PlayerManaReserve < 100
		PlayerLastManaTime = GameMilliSecs + PlayerManaAddTime
		PlayerManaReserve = PlayerManaReserve + 1
		DebugLog "MANA"
	End If 

	;If xKeyHit(KEY_F2) PlayerTotalXP = 100
End Function

