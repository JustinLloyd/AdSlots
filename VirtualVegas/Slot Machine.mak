# Microsoft Developer Studio Generated NMAKE File, Format Version 4.20
# ** DO NOT EDIT **

# TARGTYPE "Java Virtual Machine Java Workspace" 0x0809

!IF "$(CFG)" == ""
CFG=Slot Machine - Java Virtual Machine Debug
!MESSAGE No configuration specified.  Defaulting to Slot Machine - Java Virtual\
 Machine Debug.
!ENDIF 

!IF "$(CFG)" != "Slot Machine - Java Virtual Machine Release" && "$(CFG)" !=\
 "Slot Machine - Java Virtual Machine Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE on this makefile
!MESSAGE by defining the macro CFG on the command line.  For example:
!MESSAGE 
!MESSAGE NMAKE /f "Slot Machine.mak"\
 CFG="Slot Machine - Java Virtual Machine Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "Slot Machine - Java Virtual Machine Release" (based on\
 "Java Virtual Machine Java Workspace")
!MESSAGE "Slot Machine - Java Virtual Machine Debug" (based on\
 "Java Virtual Machine Java Workspace")
!MESSAGE 
!ERROR An invalid configuration is specified.
!ENDIF 

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 
################################################################################
# Begin Project
# PROP Target_Last_Scanned "Slot Machine - Java Virtual Machine Debug"
JAVA=jvc.exe

!IF  "$(CFG)" == "Slot Machine - Java Virtual Machine Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir ""
# PROP BASE Intermediate_Dir ""
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir ""
# PROP Intermediate_Dir ""
# PROP Target_Dir ""
OUTDIR=.
INTDIR=.

ALL : "$(OUTDIR)\Slot.class" "$(OUTDIR)\GraphicButton.class"\
 "$(OUTDIR)\LED.class" "$(OUTDIR)\MachineLogic.class"\
 "$(OUTDIR)\PayoutLine.class" "$(OUTDIR)\PotTable.class"\
 "$(OUTDIR)\PotType.class" "$(OUTDIR)\RippleCount.class"\
 "$(OUTDIR)\SendMail.class"

CLEAN : 
	-@erase "$(INTDIR)\GraphicButton.class"
	-@erase "$(INTDIR)\LED.class"
	-@erase "$(INTDIR)\MachineLogic.class"
	-@erase "$(INTDIR)\PayoutLine.class"
	-@erase "$(INTDIR)\PotTable.class"
	-@erase "$(INTDIR)\PotType.class"
	-@erase "$(INTDIR)\RippleCount.class"
	-@erase "$(INTDIR)\SendMail.class"
	-@erase "$(INTDIR)\Slot.class"

# ADD BASE JAVA /O
# ADD JAVA /O

!ELSEIF  "$(CFG)" == "Slot Machine - Java Virtual Machine Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir ""
# PROP BASE Intermediate_Dir ""
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir ""
# PROP Intermediate_Dir ""
# PROP Target_Dir ""
OUTDIR=.
INTDIR=.

ALL : "$(OUTDIR)\Slot.class" "$(OUTDIR)\GraphicButton.class"\
 "$(OUTDIR)\LED.class" "$(OUTDIR)\MachineLogic.class"\
 "$(OUTDIR)\PayoutLine.class" "$(OUTDIR)\PotTable.class"\
 "$(OUTDIR)\PotType.class" "$(OUTDIR)\RippleCount.class"\
 "$(OUTDIR)\SendMail.class"

CLEAN : 
	-@erase "$(INTDIR)\GraphicButton.class"
	-@erase "$(INTDIR)\LED.class"
	-@erase "$(INTDIR)\MachineLogic.class"
	-@erase "$(INTDIR)\PayoutLine.class"
	-@erase "$(INTDIR)\PotTable.class"
	-@erase "$(INTDIR)\PotType.class"
	-@erase "$(INTDIR)\RippleCount.class"
	-@erase "$(INTDIR)\SendMail.class"
	-@erase "$(INTDIR)\Slot.class"

# ADD BASE JAVA /g
# ADD JAVA /g

!ENDIF 

################################################################################
# Begin Target

# Name "Slot Machine - Java Virtual Machine Release"
# Name "Slot Machine - Java Virtual Machine Debug"

!IF  "$(CFG)" == "Slot Machine - Java Virtual Machine Release"

!ELSEIF  "$(CFG)" == "Slot Machine - Java Virtual Machine Debug"

!ENDIF 

################################################################################
# Begin Source File

SOURCE=".\To Do List.txt"

!IF  "$(CFG)" == "Slot Machine - Java Virtual Machine Release"

!ELSEIF  "$(CFG)" == "Slot Machine - Java Virtual Machine Debug"

!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\Slot.java

!IF  "$(CFG)" == "Slot Machine - Java Virtual Machine Release"


"$(INTDIR)\Slot.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Slot Machine - Java Virtual Machine Debug"


"$(INTDIR)\Slot.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\GraphicButton.java

!IF  "$(CFG)" == "Slot Machine - Java Virtual Machine Release"


"$(INTDIR)\GraphicButton.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Slot Machine - Java Virtual Machine Debug"


"$(INTDIR)\GraphicButton.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\LED.java

!IF  "$(CFG)" == "Slot Machine - Java Virtual Machine Release"


"$(INTDIR)\LED.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Slot Machine - Java Virtual Machine Debug"


"$(INTDIR)\LED.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\MachineLogic.java

!IF  "$(CFG)" == "Slot Machine - Java Virtual Machine Release"


"$(INTDIR)\MachineLogic.class" : $(SOURCE) "$(INTDIR)"

"$(INTDIR)\PayoutLine.class" : $(SOURCE) "$(INTDIR)"

"$(INTDIR)\PotTable.class" : $(SOURCE) "$(INTDIR)"

"$(INTDIR)\PotType.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Slot Machine - Java Virtual Machine Debug"


"$(INTDIR)\MachineLogic.class" : $(SOURCE) "$(INTDIR)"

"$(INTDIR)\PayoutLine.class" : $(SOURCE) "$(INTDIR)"

"$(INTDIR)\PotTable.class" : $(SOURCE) "$(INTDIR)"

"$(INTDIR)\PotType.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\RippleCount.java

!IF  "$(CFG)" == "Slot Machine - Java Virtual Machine Release"


"$(INTDIR)\RippleCount.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Slot Machine - Java Virtual Machine Debug"


"$(INTDIR)\RippleCount.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\SendMail.java

!IF  "$(CFG)" == "Slot Machine - Java Virtual Machine Release"


"$(INTDIR)\SendMail.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Slot Machine - Java Virtual Machine Debug"


"$(INTDIR)\SendMail.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\AdSlots.html

!IF  "$(CFG)" == "Slot Machine - Java Virtual Machine Release"

!ELSEIF  "$(CFG)" == "Slot Machine - Java Virtual Machine Debug"

!ENDIF 

# End Source File
# End Target
# End Project
################################################################################
