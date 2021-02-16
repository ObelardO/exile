;==================================================================
;Project Title:  exile
;Author:         ObelardO
;Email:          obelardos@gmail.com          
;Notes:          camera code-file   
;==================================================================

Global Camera
Global CameraBase
Global CameraZPos = 500
Global CameraScale# = 50
Global CameraNeedScale# = 50
Global CameraPosX#
Global CameraPosZ#

Global GameMilliSecs

Global ForcePickLock

Function InitCamera()
	CameraBase = xCreatePivot()
	Camera = xCreateCamera(CameraBase)

	xCameraClsColor Camera, 0, 0, 0
	xTurnEntity     Camera, 30, 45, 0
	xMoveEntity     Camera, 0, 0, -CameraZPos
	xScaleEntity    Camera, 1, 1, CameraScale
End Function

Function UpdateCamera()
	xCls

	GameMilliSecs = xMillisecs()

	CameraPosX = xEntityX(CameraBase)
	CameraPosZ = xEntityZ(CameraBase)

	If CameraPosX <> PlayerPosX CameraPosX = ExCurveValue(PlayerPosX, CameraPosX, 20)
	If CameraPosZ <> PlayerPosZ CameraPosZ = ExCurveValue(PlayerPosZ, CameraPosZ, 20)

	xPositionEntity CameraBase, CameraPosX, 0, CameraPosZ

	CameraNeedScale = CameraNeedScale + xMouseZSpeed() * 10
	If CameraNeedScale < 50 CameraNeedScale = 50
	If CameraNeedScale > 100 CameraNeedScale = 100
	
	CameraScale = ExCurveValue(CameraNeedScale, CameraScale, 10)
	xScaleEntity    Camera, 1, 1, CameraScale

	
	xRenderWorld
End Function