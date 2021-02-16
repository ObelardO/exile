Const EnemyDensity = 50

Const EnemyKind_Skeleton = 0

Const EnemyStatus_Wait    = 0
Const EnemyStatus_MoveTo  = 1
Const EnemyStatus_PerDoor = 2
Const EnemyStatus_Attack  = 3
Const EnemyStatus_Die     = 4

Const EnemyWalkSpeed#   = 0.02
Const EnemyRunSpeed#    = 0.05
Const EnemyTimeToUpdate = 500
Const EnemyTimeToDie    = 1000

Const EnemyAnim_Wait   = 1
Const EnemyAnim_Run    = 2
Const EnemyAnim_Attack = 3

Const EnemyAnimSpeed_Walk#   = 0.75
Const EnemyAnimSpeed_Run#    = 1.0
Const EnemyAnimSpeed_Attack# = 1.0

Const EnemyAnim_Smooth# = 0.75

Const EnemyExcitedDistance# = 4.5

Const EnemyXPvalue = 5

xCollisions LevelObjectType_Enemy, LevelObjectType_Enemy, SPHERETOSPHERE, RESPONSE_SLIDING_DOWNLOCK

Global LevelEnemyCount
Global EnemyModel
Global EnemyModelTexture

Global ForceDrawHealth = True

Type tEnemy
	Field Base
	Field Entity
	Field Model
	Field Target
	Field TargetDoor.tDoor
	Field Kind
	Field StartHealth#
	Field Health#
	Field Attack#
	Field Status
	Field Excited
	Field Freezed
	Field Room.tRoom
	Field TimeAttack
	Field Time

	Field MagicSprite
	Field MagicFrameID
	Field MagicTime
End Type

Dim Sound_EnemyHurt(4)

Global Sound_EnemyDie

Function InitEnemys()
	EnemyModelTexture = xLoadTexture("base\textures\enemy.jpg")
	EnemyModel = xLoadAnimMesh("base\models\enemy.b3d")
	xEntityTexture EnemyModel, EnemyModelTexture
	xHideEntity    EnemyModel

	xExtractAnimSeq EnemyModel, 2,  4
	xExtractAnimSeq EnemyModel, 20, 59
	xExtractAnimSeq EnemyModel, 99, 129

	For i = 0 To 4
		Sound_EnemyHurt(i) = xLoadSound("base\sounds\enemy\hit" + (i + 1) + ".ogg")
	Next

	Sound_EnemyDie = xLoadSound("base\sounds\enemy\die.ogg")
End Function 

Function GenerateEnemys(Room.tRoom)
	
	Local RoomSquare = Room\W * Room\H

	While RoomSquare > EnemyDensity
		RoomSquare = RoomSquare - EnemyDensity
		RegisterEnemy(Room)
	Wend
End Function

Function RegisterEnemy(Room.tRoom)
	Local EnemyX#, EnemyY#

	LevelEnemyCount = LevelEnemyCount + 1
	EnemyX# = xEntityX(Room\Entity) + Rnd(-Room\W, Room\W) / 2.2 + Rand(-1,1)
	EnemyY# = xEntityZ(Room\Entity) + Rnd(-Room\H, Room\H) / 2.2 + Rand(-1,1)

	Enemy.tEnemy = New tEnemy
	Enemy\Room = Room
	Enemy\Base = xCreatePivot()
	Enemy\Target = xCreatePivot()
	xEntityColor Enemy\Target, 0, 255, 0
	xScaleEntity Enemy\Target, 0.5, 0.001, 0.5

	Enemy\MagicSprite = xCreateSprite()
	xMoveEntity    Enemy\MagicSprite, 0, 0.75, 0
	xEntityParent  Enemy\MagicSprite, Enemy\Base
	xEntityTexture Enemy\MagicSprite, PlayerMagicTexture, 15
	xScaleSprite   Enemy\MagicSprite, 0.75, 0.75
	xEntityFX      Enemy\MagicSprite, FX_FULLBRIGHT
	xHideEntity    Enemy\MagicSprite
	
	Enemy\Entity = xCreateCube(Enemy\Base)
	xScaleEntity    Enemy\Entity, 0.25, 0.5, 0.25
	xMoveEntity     Enemy\Entity, 0, 0.5, 0
	xEntityColor    Enemy\Entity, 255, 255, 255
	xHideEntity     Enemy\Entity
	xEntityPickMode Enemy\Entity, PICK_TRIMESH, True
	xEntityRadius   Enemy\Entity, 0.125
	xEntityType     Enemy\Entity, LevelObjectType_Enemy
	xEntityAlpha    Enemy\Entity, 0.0001

	Enemy\Model = xCopyEntity(EnemyModel, Enemy\Base) 
	xScaleEntity Enemy\Model, 0.45, 0.45, 0.45
	xAnimate Enemy\Model, ANIMATION_LOOP, PlayerAnimSpeed_Walk, PlayerAnim_Run, PlayerAnim_Smooth

	xPositionEntity Enemy\Base,   EnemyX, 0, EnemyY
	xPositionEntity Enemy\Target, EnemyX, 0, EnemyY

	Enemy\Time       = 0
	Enemy\Health     = 6.0 + PlayerLevel - 1 + Enemy\Kind
	Enemy\Attack     = 3.0 + PlayerLevel - 1 + Enemy\Kind 
	Enemy\Freezed    = True
	Enemy\TimeAttack = 1000 / (Enemy\Kind + 1)

	Enemy\StartHealth = Enemy\Health 

	xHideEntity Enemy\Base
End Function

Function ActivateEnemys(Room.tRoom)
	Local TargetX#, TargetY#

	For Enemy.tEnemy = Each tEnemy
		If Enemy\Room = Room
			Enemy\Freezed = False

			xShowEntity Enemy\Base

			If Enemy\Excited
				xAnimate Enemy\Model, ANIMATION_LOOP, EnemyAnimSpeed_Run, EnemyAnim_Run, EnemyAnim_Smooth
			Else
				xAnimate Enemy\Model, ANIMATION_LOOP, EnemyAnimSpeed_Walk, EnemyAnim_Run, EnemyAnim_Smooth
			End If
		End If
	Next
End Function

Function DiactivateEnemys(Room.tRoom)
	For Enemy.tEnemy = Each tEnemy
		If Enemy\Room = Room DiactivateEnemy(Enemy)
	Next
End Function 

Function DiactivateEnemy(Enemy.tEnemy)
	Enemy\Freezed = True
	xHideEntity Enemy\Base
	xAnimate Enemy\Model, ANIMATION_STOP
End Function 

Function DeleteEnemy(Enemy.tEnemy)
	xFreeEntity Enemy\Base
	xFreeEntity Enemy\Target
	Delete Enemy
End Function

Function UpdateEnemys()
	Local TargetX#, TargetY#

	For Enemy.tEnemy = Each tEnemy
		If Enemy\Freezed = False
			Select Enemy\Status
				Case EnemyStatus_Wait
					If Enemy\Excited = False
						If xEntityDistance(Enemy\Base, Enemy\Target) > 0.5
							Enemy\Status = EnemyStatus_MoveTo
							;xAnimate Enemy\Model, ANIMATION_LOOP, PlayerAnimSpeed_Walk, PlayerAnim_Run, PlayerAnim_Smooth
						Else
							TargetX = xEntityX(Enemy\Room\Entity) + Rnd(-Enemy\Room\W, Enemy\Room\W) / 2.4 + Rand(-1,1)
							TargetY = xEntityZ(Enemy\Room\Entity) + Rnd(-Enemy\Room\H, Enemy\Room\H) / 2.4 + Rand(-1,1)
							xPositionEntity Enemy\Target, TargetX, 0, TargetY
							Enemy\Status = EnemyStatus_MoveTo
							;xAnimate Enemy\Model, ANIMATION_LOOP, PlayerAnimSpeed_Walk, PlayerAnim_Run, PlayerAnim_Smooth
						End If
					Else
						If xEntityDistance(Enemy\Base, PlayerBase) > 1.0
							If Enemy\Room = CurrentRoom
								Enemy\Status = EnemyStatus_MoveTo
								;xAnimate Enemy\Model, ANIMATION_LOOP, PlayerAnimSpeed_Run, PlayerAnim_Run, PlayerAnim_Smooth
								TargetX = xEntityX(PlayerBase) + Rnd(-1,1)
								TargetY = xEntityZ(PlayerBase) + Rnd(-1,1)
								xPositionEntity Enemy\Target, TargetX, 0, TargetY
								;EnemyCorrectTarget(Enemy)
							Else
								Door.tDoor = GetUnitingDoor(Enemy\Room, CurrentRoom)
								If Door <> Null
									Enemy\TargetDoor = Door
								
									TargetX = xEntityX(Door\Entity)
									TargetY = xEntityZ(Door\Entity)
									xPositionEntity Enemy\Target, TargetX, 0, TargetY

									;Enemy\TatgetDoor = Door\Entity
									xAnimate Enemy\Model, ANIMATION_LOOP, EnemyAnimSpeed_Run, EnemyAnim_Run, EnemyAnim_Smooth
									Enemy\Status = EnemyStatus_PerDoor
								Else
									xAnimate Enemy\Model, ANIMATION_LOOP, EnemyAnimSpeed_Run, EnemyAnim_Run, EnemyAnim_Smooth
									Enemy\Status = EnemyStatus_Wait
									Enemy\Excited = False
								End If
							End If
						Else
;							xPointEntity    Enemy\Base, PlayerBase
;							xRotateEntity   Enemy\Base, 0, xEntityYaw(Enemy\Base), 0
							Enemy\Time = GameMilliSecs + Enemy\TimeAttack + Rand(100, 500)
							xAnimate Enemy\Model, ANIMATION_ONE, EnemyAnimSpeed_Attack, EnemyAnim_Attack, EnemyAnim_Smooth
							Enemy\Status = EnemyStatus_Attack
							PlayerDamage(Enemy\Attack)
							;xEntityColor Player, 255, 0, 0
							xPlaySound Sound_PlayerAttack(Rand(0, 8))
						End If
					End If
				Case EnemyStatus_PerDoor

					xTurnEntity Enemy\Base, 0, xDeltaYaw(Enemy\Base, Enemy\TargetDoor\Entity) * 0.25, 0

					If xEntityDistance(Enemy\Base, Enemy\TargetDoor\Entity) > 0.8
						xMoveEntity Enemy\Base, 0, 0, EnemyRunSpeed
					Else
						If xEntityDistance(Enemy\Base, PlayerBase) > EnemyExcitedDistance
							Enemy\Excited = False
						End If

						Enemy\Status = EnemyStatus_Wait

						If Enemy\TargetDoor\Status

							If Enemy\Room = Enemy\TargetDoor\Room1 Enemy\Room = Enemy\TargetDoor\Room2 Else Enemy\Room = Enemy\TargetDoor\Room1
							If Enemy\Room = Null Enemy\Room = CurrentRoom
							If Enemy\Room\Status = 0 DiactivateEnemy(Enemy)

						End If
					End If
					
				Case EnemyStatus_MoveTo				
					If Enemy\Excited = False
						
						xTurnEntity Enemy\Base, 0, xDeltaYaw(Enemy\Base, Enemy\Target) * 0.15, 0
			
						If xEntityDistance(Enemy\Base, Enemy\Target) > 0.5
							xMoveEntity Enemy\Base, 0, 0, EnemyWalkSpeed
						Else
							Enemy\Status = EnemyStatus_Wait
							xAnimate Enemy\Model, ANIMATION_LOOP, EnemyAnimSpeed_Walk, EnemyAnim_Run, EnemyAnim_Smooth
						End If

						If xEntityDistance(Enemy\Base, PlayerBase) < EnemyExcitedDistance
							Enemy\Status = EnemyStatus_Wait
							xAnimate Enemy\Model, ANIMATION_LOOP, EnemyAnimSpeed_Run, EnemyAnim_Run, EnemyAnim_Smooth
							Enemy\Excited = True
						End If

					Else

						If Enemy\Time < GameMilliSecs
							Enemy\Time = GameMilliSecs + EnemyTimeToUpdate
							Enemy\Status = EnemyStatus_Wait
						Else
							
							xTurnEntity Enemy\Base, 0, xDeltaYaw(Enemy\Base, Enemy\Target) * 0.25, 0
				
							If xEntityDistance(Enemy\Base, Enemy\Target) > 0.5
								xMoveEntity Enemy\Base, 0, 0, EnemyRunSpeed
							Else
								Enemy\Status = EnemyStatus_Wait
							End If
	
							If xEntityDistance(Enemy\Base, Enemy\Target) > EnemyExcitedDistance
								xAnimate Enemy\Model, ANIMATION_LOOP, EnemyAnimSpeed_Run, EnemyAnim_Run, EnemyAnim_Smooth
								Enemy\Status = EnemyStatus_Wait
								Enemy\Excited = False
							End If
						End If
					End If
				Case EnemyStatus_Attack

					xTurnEntity Enemy\Base, 0, xDeltaYaw(Enemy\Base, PlayerBase) * 0.25, 0
					
					If xEntityDistance(Enemy\Base, PlayerBase) > 1.0
						xAnimate Enemy\Model, ANIMATION_LOOP, EnemyAnimSpeed_Run, EnemyAnim_Run, EnemyAnim_Smooth
						Enemy\Status = EnemyStatus_Wait
					End If
					
					If Enemy\Time < GameMilliSecs
						xAnimate Enemy\Model, ANIMATION_LOOP, EnemyAnimSpeed_Run, EnemyAnim_Run, EnemyAnim_Smooth
						Enemy\Status = EnemyStatus_Wait
					End If

				Case EnemyStatus_Die
					xTurnEntity Enemy\base, 0, 0, 1.25

					If Enemy\Time < GameMilliSecs
						xAnimate Enemy\Model, ANIMATION_STOP
						MustDie = True
						PlayerTotalXP = PlayerTotalXP + EnemyXPvalue
					End If
			End Select

			If ForceDrawHealth
				xCameraProject(Camera, xEntityX(Enemy\Base), 0, xEntityZ(Enemy\Base))
				xColor 32, 32, 32, 200
				xRect xProjectedX() - 26, xProjectedY() - CameraScale - 51, 52, 7, True
				xColor 245, 159 - 80 * (1.0 - Enemy\Health / Enemy\StartHealth), 58, 200
				xRect xProjectedX() - 25, xProjectedY() - CameraScale - 50, 50 * Enemy\Health / Enemy\StartHealth, 5, True
			End If

			If Enemy\MagicFrameID => 0
				If Enemy\MagicFrameID = 0
					xHideEntity Enemy\MagicSprite
				Else
					Enemy\MagicFrameID = Enemy\MagicFrameID - 1
					xEntityTexture Enemy\MagicSprite, PlayerMagicTexture, Enemy\MagicFrameID
				End If
			End If
		End If

		If Enemy\Room <> Null And DebugMode And DebugDrawCon
			xCameraProject(Camera, xEntityX(Enemy\Room\Entity), 0, xEntityZ(Enemy\Room\Entity))
			RoomX = xProjectedX()
			RoomY = xProjectedY()

			xCameraProject(Camera, xEntityX(Enemy\Base), 0, xEntityZ(Enemy\Base))

			xColor 255, 255, 128, 255
	
			xLine RoomX, RoomY, xProjectedX(), xProjectedY()			
		End If

		xEntityColor Enemy\Entity, 255, 255, 255

		If MouseHitL Or MouseHitR
			If Enemy\Status <> EnemyStatus_Die And GameMilliSecs > PlayerLastAttackTime
				If MouseHitL
					If xPickedEntity() = Enemy\Entity And xEntityDistance(PlayerBase, Enemy\Base) < 1.5
	
						PlayerLastAttackTime = GameMilliSecs + PlayerAttackTime
						PlayerStatus = PlayerStatus_Attack
						xAnimate Player, ANIMATION_ONE, EnemyAnimSpeed_Attack, EnemyAnim_Attack, EnemyAnim_Smooth
		
						xTurnEntity PlayerBase, 0, xDeltaYaw(PlayerBase, Enemy\Entity), 0
						
						Enemy\Health = Enemy\Health - (Player_Attack)
						PlayerUseItem(PlayerSwordItem)
						xPlaySound Sound_EnemyHurt(Rand(0, 4))
						DebugLog "Die mutherfucker! " + Enemy\Health 
						If Enemy\Health <= 0
							Enemy\Status = EnemyStatus_Die
							Enemy\Time = GameMilliSecs + EnemyTimeToDie
		
							xPlaySound Sound_EnemyDie
						End If
					End If
				ElseIf MouseHitR And Enemy\MagicFrameID = 0 And PlayerBookItem <> Null And PlayerManaReserve > Float(10) / Player_Mana
				
					If xPickedEntity() = Enemy\Entity And xEntityDistance(PlayerBase, Enemy\Base) < 5.0
						 xShowEntity Enemy\MagicSprite
						 Enemy\MagicFrameID = 15
						 xPlaySound Sound_Fireball
						 PlayerUseItem(PlayerBookItem)
						 Enemy\Health = Enemy\Health - (Player_Mana)
						 Enemy\Excited = True
						 xPlaySound Sound_EnemyHurt(Rand(0, 4))
						 PlayerManaReserve = PlayerManaReserve - Float(10) / Player_Mana
						 DebugLog "AAAA: " + PlayerManaReserve
						 If Enemy\Health <= 0
							Enemy\Status = EnemyStatus_Die
							Enemy\Time = GameMilliSecs + EnemyTimeToDie
		
							xPlaySound Sound_EnemyDie
						End If
					End If
				End If
			End If
		End If

		If MustDie DeleteEnemy(Enemy) MustDie = False
	Next
End Function

Function EnemyCorrectTarget(SourceEnemy.tEnemy)
	Local Distance#

	For Enemy.tEnemy = Each tEnemy
		If Enemy\Freezed = False And SourceEnemy <> Enemy
			Distance = xEntityDistance(Enemy\Target, SourceEnemy\Target)
			If Distance < 1.0
				xPointEntity Enemy\Target, Enemy\Base
				xMoveEntity  Enemy\Target, 0, 0, 1.0
			End If
	
		End If
	Next
End Function

Function DeleteEnemys()
	For Enemy.tEnemy = Each tEnemy
		xFreeEntity Enemy\Base
		Delete Enemy
	Next

	LevelEnemyCount = 0
End Function