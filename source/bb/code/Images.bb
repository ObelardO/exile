;;==================================================================
;;Project Title:  ?     
;;Author:         ObelardO
;;Email:          obelardos@gmail.com          
;;Date:           06.02.15
;;Notes:          GUI code-file   
;;==================================================================

Type tImage
	Field Image
	Field Name$
End Type

Function ExLoadImage(Name$, Width = 100, Height = 100)
	For Image.tImage = Each tImage
		If Lower(Image\Name) = Lower(Name)
			ImageDetected = True
			Exit
		End If
	Next

	If ImageDetected
		ConsoleLog("^4[WARNING]^1 Image ^5'" + ImageName + ".png'^1 is already exist!")
	Else
		Image.tImage = New tImage
		Image\Name  = Name
		Image\Image = LoadImage("base\images\" + ImageName + ".png")
		If Not Image\Image Image\Image = ExCreateErrorImage(Name)

		xResizeImage Image\Image, Width, Height
	End If
End Function

Function ExCreateErrorImage(Name$)
	Image = xCreateImage(512, 512)
	xSetBuffer(xImageBuffer(Image))
	xCls
 	xSetFont FONT_STD
	xColor 255, 255, 255, 255
	xRect 0, 0, 512, 512, 1
	xColor 255, 0, 0, 255
	xText 10, 248, "ERROR! IMAGE:"
	xText 10, 262, Name
	xSetBuffer xBackBuffer()
	Return Image
End Function

Function ExGetImageHandle(ImageName$)
	For Image.tImage = Each tImage
		If Lower(Image\Name) = Lower(Name)
			Return Image\Image
		End If
	Next
	ConsoleLog("^4[WARNING]^1 Image ^5'" + ImageName + "'^1 does not exist!")
	Return ExSmartLoad(ImageName + ".png")
End Function

Function ExDrawImage(ImageName$, x, y, w, h, Resize = True)
	For Image.tImage = Each tImage
		If Lower(Image\Name) = Lower(Name)

			ImageW = xImageWidth(Image\Image)
			ImageH = xImageHeight(Image\Image)

			xResizeImage Image\Image, w, h
			xDrawImage   Image\Image, x, y
			xResizeImage Image\Image, ImageW, ImageH
			ImageDetected = True
		End If
	Next

	If Not ImageDetected ExLoadImage(ImageName)
End Function



