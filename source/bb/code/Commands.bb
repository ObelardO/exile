;==================================================================
;Project Title:  exile     
;Author:         ObelardO
;Email:          obelardos@gmail.com          
;Notes:          system commands list code-file
;==================================================================

ExAddCommand "start",   "", "starting game."
ExAddCommand "set",     "%varname% = %value%", "set new variable value."
ExAddCommand "stop",    "", "stoping game."
ExAddCommand "title",   "", "set the window title."
ExAddCommand "sysinfo", "", "show system information."
ExAddCommand "load",    "%path%", "load any file to system."
ExAddCommand "level",   "%roomcount% %seed% [%debugmode%]", "generate level."
ExAddCommand "xpup",    "", "open character levelup menu."
ExAddCommand "help",    "", "show commands list."
ExAddCommand "vars",    "", "show variables list."