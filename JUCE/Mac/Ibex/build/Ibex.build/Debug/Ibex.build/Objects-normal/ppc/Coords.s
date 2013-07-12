	.section __TEXT,__text,regular,pure_instructions
	.section __TEXT,__picsymbolstub1,symbol_stubs,pure_instructions,32
	.section __DWARF,__debug_frame,regular,debug
Lsection__debug_frame:
	.section __DWARF,__debug_info,regular,debug
Lsection__debug_info:
	.section __DWARF,__debug_abbrev,regular,debug
Lsection__debug_abbrev:
	.section __DWARF,__debug_aranges,regular,debug
Lsection__debug_aranges:
	.section __DWARF,__debug_macinfo,regular,debug
Lsection__debug_macinfo:
	.section __DWARF,__debug_line,regular,debug
Lsection__debug_line:
	.section __DWARF,__debug_loc,regular,debug
Lsection__debug_loc:
	.section __DWARF,__debug_pubnames,regular,debug
Lsection__debug_pubnames:
	.section __DWARF,__debug_pubtypes,regular,debug
Lsection__debug_pubtypes:
	.section __DWARF,__debug_str,regular,debug
Lsection__debug_str:
	.section __DWARF,__debug_ranges,regular,debug
Lsection__debug_ranges:
	.machine ppc7400
	.section __DWARF,__debug_abbrev,regular,debug
Ldebug_abbrev0:
	.section __DWARF,__debug_info,regular,debug
Ldebug_info0:
	.section __DWARF,__debug_line,regular,debug
Ldebug_line0:
	.text
Ltext0:
	.align 2
	.globl __ZN6CoordsC2Ev
__ZN6CoordsC2Ev:
LFB867:
LM1:
	nop
	nop
	nop
	nop
	nop
	stmw r30,-8(r1)
LCFI0:
	stwu r1,-48(r1)
LCFI1:
	mr r30,r1
LCFI2:
	stw r3,72(r30)
LBB2:
LM2:
	lwz r2,72(r30)
	li r0,0
	stw r0,0(r2)
	lwz r2,72(r30)
	li r0,0
	stw r0,4(r2)
LBE2:
LM3:
	lwz r1,0(r1)
	lmw r30,-8(r1)
	blr
LFE867:
	.align 2
	.globl __ZN6CoordsC1Ev
__ZN6CoordsC1Ev:
LFB868:
LM4:
	nop
	nop
	nop
	nop
	nop
	mflr r0
LCFI3:
	stmw r30,-8(r1)
LCFI4:
	stw r0,8(r1)
LCFI5:
	stwu r1,-80(r1)
LCFI6:
	mr r30,r1
LCFI7:
	stw r3,104(r30)
LBB3:
LM5:
	lwz r3,104(r30)
	bl __ZN6CoordsC2Ev
LBE3:
	lwz r1,0(r1)
	lwz r0,8(r1)
	mtlr r0
	lmw r30,-8(r1)
	blr
LFE868:
	.align 2
	.globl __ZN6CoordsC2ERKS_
__ZN6CoordsC2ERKS_:
LFB870:
LM6:
	nop
	nop
	nop
	nop
	nop
	stmw r30,-8(r1)
LCFI8:
	stwu r1,-48(r1)
LCFI9:
	mr r30,r1
LCFI10:
	stw r3,72(r30)
	stw r4,76(r30)
LBB4:
LM7:
	lwz r2,76(r30)
	lwz r0,0(r2)
	lwz r2,72(r30)
	stw r0,0(r2)
	lwz r2,76(r30)
	lwz r0,4(r2)
	lwz r2,72(r30)
	stw r0,4(r2)
LBE4:
LM8:
	lwz r1,0(r1)
	lmw r30,-8(r1)
	blr
LFE870:
	.align 2
	.globl __ZN6CoordsC1ERKS_
__ZN6CoordsC1ERKS_:
LFB871:
LM9:
	nop
	nop
	nop
	nop
	nop
	mflr r0
LCFI11:
	stmw r30,-8(r1)
LCFI12:
	stw r0,8(r1)
LCFI13:
	stwu r1,-80(r1)
LCFI14:
	mr r30,r1
LCFI15:
	stw r3,104(r30)
	stw r4,108(r30)
LBB5:
LM10:
	lwz r3,104(r30)
	lwz r4,108(r30)
	bl __ZN6CoordsC2ERKS_
LBE5:
	lwz r1,0(r1)
	lwz r0,8(r1)
	mtlr r0
	lmw r30,-8(r1)
	blr
LFE871:
	.align 2
	.globl __ZN6CoordsaSERKS_
__ZN6CoordsaSERKS_:
LFB872:
LM11:
	nop
	nop
	nop
	nop
	nop
	stmw r30,-8(r1)
LCFI16:
	stwu r1,-48(r1)
LCFI17:
	mr r30,r1
LCFI18:
	stw r3,72(r30)
	stw r4,76(r30)
LBB6:
LM12:
	lwz r2,76(r30)
	lwz r0,0(r2)
	lwz r2,72(r30)
	stw r0,0(r2)
LM13:
	lwz r2,76(r30)
	lwz r0,4(r2)
	lwz r2,72(r30)
	stw r0,4(r2)
LM14:
	lwz r0,72(r30)
LBE6:
LM15:
	mr r3,r0
	lwz r1,0(r1)
	lmw r30,-8(r1)
	blr
LFE872:
	.align 2
	.globl __ZN6CoordseqERKS_
__ZN6CoordseqERKS_:
LFB873:
LM16:
	nop
	nop
	nop
	nop
	nop
	stmw r30,-8(r1)
LCFI19:
	stwu r1,-48(r1)
LCFI20:
	mr r30,r1
LCFI21:
	stw r3,72(r30)
	stw r4,76(r30)
LBB7:
LM17:
	lwz r2,72(r30)
	lwz r9,0(r2)
	lwz r2,76(r30)
	lwz r0,0(r2)
	cmpw cr7,r9,r0
	bne cr7,L12
	lwz r2,72(r30)
	lwz r9,4(r2)
	lwz r2,76(r30)
	lwz r0,4(r2)
	cmpw cr7,r9,r0
	bne cr7,L12
LM18:
	li r0,1
	stw r0,24(r30)
	b L15
L12:
LM19:
	li r0,0
	stw r0,24(r30)
L15:
	lwz r0,24(r30)
LBE7:
LM20:
	mr r3,r0
	lwz r1,0(r1)
	lmw r30,-8(r1)
	blr
LFE873:
	.align 2
	.globl __ZN6CoordsC2Eii
__ZN6CoordsC2Eii:
LFB875:
LM21:
	nop
	nop
	nop
	nop
	nop
	stmw r30,-8(r1)
LCFI22:
	stwu r1,-48(r1)
LCFI23:
	mr r30,r1
LCFI24:
	stw r3,72(r30)
	stw r4,76(r30)
	stw r5,80(r30)
LBB8:
LM22:
	lwz r2,72(r30)
	lwz r0,76(r30)
	stw r0,0(r2)
	lwz r2,72(r30)
	lwz r0,80(r30)
	stw r0,4(r2)
LBE8:
LM23:
	lwz r1,0(r1)
	lmw r30,-8(r1)
	blr
LFE875:
	.align 2
	.globl __ZN6CoordsC1Eii
__ZN6CoordsC1Eii:
LFB876:
LM24:
	nop
	nop
	nop
	nop
	nop
	mflr r0
LCFI25:
	stmw r30,-8(r1)
LCFI26:
	stw r0,8(r1)
LCFI27:
	stwu r1,-80(r1)
LCFI28:
	mr r30,r1
LCFI29:
	stw r3,104(r30)
	stw r4,108(r30)
	stw r5,112(r30)
LBB9:
LM25:
	lwz r3,104(r30)
	lwz r4,108(r30)
	lwz r5,112(r30)
	bl __ZN6CoordsC2Eii
LBE9:
	lwz r1,0(r1)
	lwz r0,8(r1)
	mtlr r0
	lmw r30,-8(r1)
	blr
LFE876:
	.align 2
	.globl __ZN6CoordsD2Ev
__ZN6CoordsD2Ev:
LFB878:
LM26:
	nop
	nop
	nop
	nop
	nop
	stmw r30,-8(r1)
LCFI30:
	stwu r1,-48(r1)
LCFI31:
	mr r30,r1
LCFI32:
	stw r3,72(r30)
LM27:
	lwz r1,0(r1)
	lmw r30,-8(r1)
	blr
LFE878:
	.align 2
	.globl __ZN6CoordsD1Ev
__ZN6CoordsD1Ev:
LFB879:
LM28:
	nop
	nop
	nop
	nop
	nop
	mflr r0
LCFI33:
	stmw r30,-8(r1)
LCFI34:
	stw r0,8(r1)
LCFI35:
	stwu r1,-80(r1)
LCFI36:
	mr r30,r1
LCFI37:
	stw r3,104(r30)
LBB10:
LM29:
	lwz r3,104(r30)
	bl __ZN6CoordsD2Ev
LBE10:
	lwz r1,0(r1)
	lwz r0,8(r1)
	mtlr r0
	lmw r30,-8(r1)
	blr
LFE879:
	.align 2
	.globl __ZN6Coords5setXYEii
__ZN6Coords5setXYEii:
LFB880:
LM30:
	nop
	nop
	nop
	nop
	nop
	stmw r30,-8(r1)
LCFI38:
	stwu r1,-48(r1)
LCFI39:
	mr r30,r1
LCFI40:
	stw r3,72(r30)
	stw r4,76(r30)
	stw r5,80(r30)
LBB11:
LM31:
	lwz r2,72(r30)
	lwz r0,76(r30)
	stw r0,0(r2)
LM32:
	lwz r2,72(r30)
	lwz r0,80(r30)
	stw r0,4(r2)
LBE11:
LM33:
	lwz r1,0(r1)
	lmw r30,-8(r1)
	blr
LFE880:
	.section __DWARF,__debug_frame,regular,debug
Lframe0:
	.set L$set$0,LECIE0-LSCIE0
	.long L$set$0
LSCIE0:
	.long	0xffffffff
	.byte	0x1
	.ascii "\0"
	.byte	0x1
	.byte	0x7c
	.byte	0x41
	.byte	0xc
	.byte	0x1
	.byte	0x0
	.align 2
LECIE0:
LSFDE0:
	.set L$set$1,LEFDE0-LASFDE0
	.long L$set$1
LASFDE0:
	.set L$set$2,Lframe0-Lsection__debug_frame
	.long L$set$2
	.long	LFB867
	.set L$set$3,LFE867-LFB867
	.long L$set$3
	.byte	0x4
	.set L$set$4,LCFI1-LFB867
	.long L$set$4
	.byte	0xe
	.byte	0x30
	.byte	0x9f
	.byte	0x1
	.byte	0x9e
	.byte	0x2
	.byte	0x4
	.set L$set$5,LCFI2-LCFI1
	.long L$set$5
	.byte	0xd
	.byte	0x1e
	.align 2
LEFDE0:
LSFDE2:
	.set L$set$6,LEFDE2-LASFDE2
	.long L$set$6
LASFDE2:
	.set L$set$7,Lframe0-Lsection__debug_frame
	.long L$set$7
	.long	LFB868
	.set L$set$8,LFE868-LFB868
	.long L$set$8
	.byte	0x4
	.set L$set$9,LCFI6-LFB868
	.long L$set$9
	.byte	0xe
	.byte	0x50
	.byte	0x9f
	.byte	0x1
	.byte	0x9e
	.byte	0x2
	.byte	0x11
	.byte	0x41
	.byte	0x7e
	.byte	0x4
	.set L$set$10,LCFI7-LCFI6
	.long L$set$10
	.byte	0xd
	.byte	0x1e
	.align 2
LEFDE2:
LSFDE4:
	.set L$set$11,LEFDE4-LASFDE4
	.long L$set$11
LASFDE4:
	.set L$set$12,Lframe0-Lsection__debug_frame
	.long L$set$12
	.long	LFB870
	.set L$set$13,LFE870-LFB870
	.long L$set$13
	.byte	0x4
	.set L$set$14,LCFI9-LFB870
	.long L$set$14
	.byte	0xe
	.byte	0x30
	.byte	0x9f
	.byte	0x1
	.byte	0x9e
	.byte	0x2
	.byte	0x4
	.set L$set$15,LCFI10-LCFI9
	.long L$set$15
	.byte	0xd
	.byte	0x1e
	.align 2
LEFDE4:
LSFDE6:
	.set L$set$16,LEFDE6-LASFDE6
	.long L$set$16
LASFDE6:
	.set L$set$17,Lframe0-Lsection__debug_frame
	.long L$set$17
	.long	LFB871
	.set L$set$18,LFE871-LFB871
	.long L$set$18
	.byte	0x4
	.set L$set$19,LCFI14-LFB871
	.long L$set$19
	.byte	0xe
	.byte	0x50
	.byte	0x9f
	.byte	0x1
	.byte	0x9e
	.byte	0x2
	.byte	0x11
	.byte	0x41
	.byte	0x7e
	.byte	0x4
	.set L$set$20,LCFI15-LCFI14
	.long L$set$20
	.byte	0xd
	.byte	0x1e
	.align 2
LEFDE6:
LSFDE8:
	.set L$set$21,LEFDE8-LASFDE8
	.long L$set$21
LASFDE8:
	.set L$set$22,Lframe0-Lsection__debug_frame
	.long L$set$22
	.long	LFB872
	.set L$set$23,LFE872-LFB872
	.long L$set$23
	.byte	0x4
	.set L$set$24,LCFI17-LFB872
	.long L$set$24
	.byte	0xe
	.byte	0x30
	.byte	0x9f
	.byte	0x1
	.byte	0x9e
	.byte	0x2
	.byte	0x4
	.set L$set$25,LCFI18-LCFI17
	.long L$set$25
	.byte	0xd
	.byte	0x1e
	.align 2
LEFDE8:
LSFDE10:
	.set L$set$26,LEFDE10-LASFDE10
	.long L$set$26
LASFDE10:
	.set L$set$27,Lframe0-Lsection__debug_frame
	.long L$set$27
	.long	LFB873
	.set L$set$28,LFE873-LFB873
	.long L$set$28
	.byte	0x4
	.set L$set$29,LCFI20-LFB873
	.long L$set$29
	.byte	0xe
	.byte	0x30
	.byte	0x9f
	.byte	0x1
	.byte	0x9e
	.byte	0x2
	.byte	0x4
	.set L$set$30,LCFI21-LCFI20
	.long L$set$30
	.byte	0xd
	.byte	0x1e
	.align 2
LEFDE10:
LSFDE12:
	.set L$set$31,LEFDE12-LASFDE12
	.long L$set$31
LASFDE12:
	.set L$set$32,Lframe0-Lsection__debug_frame
	.long L$set$32
	.long	LFB875
	.set L$set$33,LFE875-LFB875
	.long L$set$33
	.byte	0x4
	.set L$set$34,LCFI23-LFB875
	.long L$set$34
	.byte	0xe
	.byte	0x30
	.byte	0x9f
	.byte	0x1
	.byte	0x9e
	.byte	0x2
	.byte	0x4
	.set L$set$35,LCFI24-LCFI23
	.long L$set$35
	.byte	0xd
	.byte	0x1e
	.align 2
LEFDE12:
LSFDE14:
	.set L$set$36,LEFDE14-LASFDE14
	.long L$set$36
LASFDE14:
	.set L$set$37,Lframe0-Lsection__debug_frame
	.long L$set$37
	.long	LFB876
	.set L$set$38,LFE876-LFB876
	.long L$set$38
	.byte	0x4
	.set L$set$39,LCFI28-LFB876
	.long L$set$39
	.byte	0xe
	.byte	0x50
	.byte	0x9f
	.byte	0x1
	.byte	0x9e
	.byte	0x2
	.byte	0x11
	.byte	0x41
	.byte	0x7e
	.byte	0x4
	.set L$set$40,LCFI29-LCFI28
	.long L$set$40
	.byte	0xd
	.byte	0x1e
	.align 2
LEFDE14:
LSFDE16:
	.set L$set$41,LEFDE16-LASFDE16
	.long L$set$41
LASFDE16:
	.set L$set$42,Lframe0-Lsection__debug_frame
	.long L$set$42
	.long	LFB878
	.set L$set$43,LFE878-LFB878
	.long L$set$43
	.byte	0x4
	.set L$set$44,LCFI31-LFB878
	.long L$set$44
	.byte	0xe
	.byte	0x30
	.byte	0x9f
	.byte	0x1
	.byte	0x9e
	.byte	0x2
	.byte	0x4
	.set L$set$45,LCFI32-LCFI31
	.long L$set$45
	.byte	0xd
	.byte	0x1e
	.align 2
LEFDE16:
LSFDE18:
	.set L$set$46,LEFDE18-LASFDE18
	.long L$set$46
LASFDE18:
	.set L$set$47,Lframe0-Lsection__debug_frame
	.long L$set$47
	.long	LFB879
	.set L$set$48,LFE879-LFB879
	.long L$set$48
	.byte	0x4
	.set L$set$49,LCFI36-LFB879
	.long L$set$49
	.byte	0xe
	.byte	0x50
	.byte	0x9f
	.byte	0x1
	.byte	0x9e
	.byte	0x2
	.byte	0x11
	.byte	0x41
	.byte	0x7e
	.byte	0x4
	.set L$set$50,LCFI37-LCFI36
	.long L$set$50
	.byte	0xd
	.byte	0x1e
	.align 2
LEFDE18:
LSFDE20:
	.set L$set$51,LEFDE20-LASFDE20
	.long L$set$51
LASFDE20:
	.set L$set$52,Lframe0-Lsection__debug_frame
	.long L$set$52
	.long	LFB880
	.set L$set$53,LFE880-LFB880
	.long L$set$53
	.byte	0x4
	.set L$set$54,LCFI39-LFB880
	.long L$set$54
	.byte	0xe
	.byte	0x30
	.byte	0x9f
	.byte	0x1
	.byte	0x9e
	.byte	0x2
	.byte	0x4
	.set L$set$55,LCFI40-LCFI39
	.long L$set$55
	.byte	0xd
	.byte	0x1e
	.align 2
LEFDE20:
	.globl __ZN6CoordsC2Ev.eh
__ZN6CoordsC2Ev.eh = 0
.no_dead_strip __ZN6CoordsC2Ev.eh
	.globl __ZN6CoordsC1Ev.eh
__ZN6CoordsC1Ev.eh = 0
.no_dead_strip __ZN6CoordsC1Ev.eh
	.globl __ZN6CoordsC2ERKS_.eh
__ZN6CoordsC2ERKS_.eh = 0
.no_dead_strip __ZN6CoordsC2ERKS_.eh
	.globl __ZN6CoordsC1ERKS_.eh
__ZN6CoordsC1ERKS_.eh = 0
.no_dead_strip __ZN6CoordsC1ERKS_.eh
	.globl __ZN6CoordsaSERKS_.eh
__ZN6CoordsaSERKS_.eh = 0
.no_dead_strip __ZN6CoordsaSERKS_.eh
	.globl __ZN6CoordseqERKS_.eh
__ZN6CoordseqERKS_.eh = 0
.no_dead_strip __ZN6CoordseqERKS_.eh
	.globl __ZN6CoordsC2Eii.eh
__ZN6CoordsC2Eii.eh = 0
.no_dead_strip __ZN6CoordsC2Eii.eh
	.globl __ZN6CoordsC1Eii.eh
__ZN6CoordsC1Eii.eh = 0
.no_dead_strip __ZN6CoordsC1Eii.eh
	.globl __ZN6CoordsD2Ev.eh
__ZN6CoordsD2Ev.eh = 0
.no_dead_strip __ZN6CoordsD2Ev.eh
	.globl __ZN6CoordsD1Ev.eh
__ZN6CoordsD1Ev.eh = 0
.no_dead_strip __ZN6CoordsD1Ev.eh
	.globl __ZN6Coords5setXYEii.eh
__ZN6Coords5setXYEii.eh = 0
.no_dead_strip __ZN6Coords5setXYEii.eh
	.text
Letext0:
	.section __DWARF,__debug_info,regular,debug
	.long	0x8f8
	.short	0x2
	.set L$set$56,Ldebug_abbrev0-Lsection__debug_abbrev
	.long L$set$56
	.byte	0x4
	.byte	0x1
	.ascii "GNU C++ 4.0.1 (Apple Inc. build 5465)\0"
	.byte	0x4
	.ascii "/Users/jeff/Documents/juce/extras/jeffproj/Mac/Ibex/../../common/Coords.cpp\0"
	.long	Ltext0
	.long	Letext0
	.set L$set$57,Ldebug_line0-Lsection__debug_line
	.long L$set$57
	.byte	0x2
	.byte	0x1
	.byte	0x6
	.ascii "signed char\0"
	.byte	0x2
	.byte	0x1
	.byte	0x8
	.ascii "unsigned char\0"
	.byte	0x2
	.byte	0x2
	.byte	0x5
	.ascii "short int\0"
	.byte	0x2
	.byte	0x2
	.byte	0x7
	.ascii "short unsigned int\0"
	.byte	0x2
	.byte	0x4
	.byte	0x5
	.ascii "int\0"
	.byte	0x3
	.ascii "__uint32_t\0"
	.byte	0x2
	.byte	0x2d
	.long	0xe7
	.byte	0x2
	.byte	0x4
	.byte	0x7
	.ascii "unsigned int\0"
	.byte	0x2
	.byte	0x8
	.byte	0x5
	.ascii "long long int\0"
	.byte	0x2
	.byte	0x8
	.byte	0x7
	.ascii "long long unsigned int\0"
	.byte	0x2
	.byte	0x4
	.byte	0x5
	.ascii "long int\0"
	.byte	0x4
	.byte	0x4
	.byte	0x7
	.byte	0x2
	.byte	0x1
	.byte	0x6
	.ascii "char\0"
	.byte	0x3
	.ascii "__darwin_size_t\0"
	.byte	0x2
	.byte	0x5a
	.long	0x150
	.byte	0x2
	.byte	0x4
	.byte	0x7
	.ascii "long unsigned int\0"
	.byte	0x3
	.ascii "__darwin_wchar_t\0"
	.byte	0x2
	.byte	0x66
	.long	0xce
	.byte	0x3
	.ascii "__darwin_rune_t\0"
	.byte	0x2
	.byte	0x6b
	.long	0x165
	.byte	0x5
	.byte	0x4
	.byte	0x6
	.long	0x131
	.long	0x1a6
	.byte	0x7
	.long	0x12e
	.byte	0x7
	.byte	0x0
	.byte	0x8
	.byte	0x4
	.long	0x131
	.byte	0x2
	.byte	0x8
	.byte	0x4
	.ascii "double\0"
	.byte	0x9
	.ascii "$_8\0"
	.byte	0x10
	.byte	0x3
	.byte	0x51
	.long	0x205
	.byte	0xa
	.ascii "__min\0"
	.byte	0x3
	.byte	0x52
	.long	0x17d
	.byte	0x2
	.byte	0x23
	.byte	0x0
	.byte	0xa
	.ascii "__max\0"
	.byte	0x3
	.byte	0x53
	.long	0x17d
	.byte	0x2
	.byte	0x23
	.byte	0x4
	.byte	0xa
	.ascii "__map\0"
	.byte	0x3
	.byte	0x54
	.long	0x17d
	.byte	0x2
	.byte	0x23
	.byte	0x8
	.byte	0xa
	.ascii "__types\0"
	.byte	0x3
	.byte	0x55
	.long	0x205
	.byte	0x2
	.byte	0x23
	.byte	0xc
	.byte	0x0
	.byte	0x8
	.byte	0x4
	.long	0xd5
	.byte	0x9
	.ascii "$_9\0"
	.byte	0x8
	.byte	0x3
	.byte	0x58
	.long	0x23f
	.byte	0xa
	.ascii "__nranges\0"
	.byte	0x3
	.byte	0x59
	.long	0xce
	.byte	0x2
	.byte	0x23
	.byte	0x0
	.byte	0xa
	.ascii "__ranges\0"
	.byte	0x3
	.byte	0x5a
	.long	0x23f
	.byte	0x2
	.byte	0x23
	.byte	0x4
	.byte	0x0
	.byte	0x8
	.byte	0x4
	.long	0x1b6
	.byte	0x9
	.ascii "$_10\0"
	.byte	0x14
	.byte	0x3
	.byte	0x5d
	.long	0x275
	.byte	0xa
	.ascii "__name\0"
	.byte	0x3
	.byte	0x5e
	.long	0x275
	.byte	0x2
	.byte	0x23
	.byte	0x0
	.byte	0xa
	.ascii "__mask\0"
	.byte	0x3
	.byte	0x5f
	.long	0xd5
	.byte	0x2
	.byte	0x23
	.byte	0x10
	.byte	0x0
	.byte	0x6
	.long	0x131
	.long	0x285
	.byte	0x7
	.long	0x12e
	.byte	0xd
	.byte	0x0
	.byte	0xb
	.ascii "$_11\0"
	.short	0xc5c
	.byte	0x3
	.byte	0x62
	.long	0x3f0
	.byte	0xa
	.ascii "__magic\0"
	.byte	0x3
	.byte	0x63
	.long	0x196
	.byte	0x2
	.byte	0x23
	.byte	0x0
	.byte	0xa
	.ascii "__encoding\0"
	.byte	0x3
	.byte	0x64
	.long	0x3f0
	.byte	0x2
	.byte	0x23
	.byte	0x8
	.byte	0xa
	.ascii "__sgetrune\0"
	.byte	0x3
	.byte	0x66
	.long	0x42a
	.byte	0x2
	.byte	0x23
	.byte	0x28
	.byte	0xa
	.ascii "__sputrune\0"
	.byte	0x3
	.byte	0x67
	.long	0x454
	.byte	0x2
	.byte	0x23
	.byte	0x2c
	.byte	0xa
	.ascii "__invalid_rune\0"
	.byte	0x3
	.byte	0x68
	.long	0x17d
	.byte	0x2
	.byte	0x23
	.byte	0x30
	.byte	0xa
	.ascii "__runetype\0"
	.byte	0x3
	.byte	0x6a
	.long	0x45a
	.byte	0x2
	.byte	0x23
	.byte	0x34
	.byte	0xa
	.ascii "__maplower\0"
	.byte	0x3
	.byte	0x6b
	.long	0x46a
	.byte	0x3
	.byte	0x23
	.byte	0xb4,0x8
	.byte	0xa
	.ascii "__mapupper\0"
	.byte	0x3
	.byte	0x6c
	.long	0x46a
	.byte	0x3
	.byte	0x23
	.byte	0xb4,0x10
	.byte	0xa
	.ascii "__runetype_ext\0"
	.byte	0x3
	.byte	0x73
	.long	0x20b
	.byte	0x3
	.byte	0x23
	.byte	0xb4,0x18
	.byte	0xa
	.ascii "__maplower_ext\0"
	.byte	0x3
	.byte	0x74
	.long	0x20b
	.byte	0x3
	.byte	0x23
	.byte	0xbc,0x18
	.byte	0xa
	.ascii "__mapupper_ext\0"
	.byte	0x3
	.byte	0x75
	.long	0x20b
	.byte	0x3
	.byte	0x23
	.byte	0xc4,0x18
	.byte	0xa
	.ascii "__variable\0"
	.byte	0x3
	.byte	0x77
	.long	0x194
	.byte	0x3
	.byte	0x23
	.byte	0xcc,0x18
	.byte	0xa
	.ascii "__variable_len\0"
	.byte	0x3
	.byte	0x78
	.long	0xce
	.byte	0x3
	.byte	0x23
	.byte	0xd0,0x18
	.byte	0xa
	.ascii "__ncharclasses\0"
	.byte	0x3
	.byte	0x7d
	.long	0xce
	.byte	0x3
	.byte	0x23
	.byte	0xd4,0x18
	.byte	0xa
	.ascii "__charclasses\0"
	.byte	0x3
	.byte	0x7e
	.long	0x47a
	.byte	0x3
	.byte	0x23
	.byte	0xd8,0x18
	.byte	0x0
	.byte	0x6
	.long	0x131
	.long	0x400
	.byte	0x7
	.long	0x12e
	.byte	0x1f
	.byte	0x0
	.byte	0xc
	.long	0x17d
	.long	0x419
	.byte	0xd
	.long	0x419
	.byte	0xd
	.long	0x139
	.byte	0xd
	.long	0x424
	.byte	0x0
	.byte	0x8
	.byte	0x4
	.long	0x41f
	.byte	0xe
	.long	0x131
	.byte	0x8
	.byte	0x4
	.long	0x419
	.byte	0x8
	.byte	0x4
	.long	0x400
	.byte	0xc
	.long	0xce
	.long	0x44e
	.byte	0xd
	.long	0x17d
	.byte	0xd
	.long	0x1a6
	.byte	0xd
	.long	0x139
	.byte	0xd
	.long	0x44e
	.byte	0x0
	.byte	0x8
	.byte	0x4
	.long	0x1a6
	.byte	0x8
	.byte	0x4
	.long	0x430
	.byte	0x6
	.long	0xd5
	.long	0x46a
	.byte	0x7
	.long	0x12e
	.byte	0xff
	.byte	0x0
	.byte	0x6
	.long	0x17d
	.long	0x47a
	.byte	0x7
	.long	0x12e
	.byte	0xff
	.byte	0x0
	.byte	0x8
	.byte	0x4
	.long	0x245
	.byte	0x2
	.byte	0x4
	.byte	0x4
	.ascii "float\0"
	.byte	0x2
	.byte	0x4
	.byte	0x2
	.ascii "bool\0"
	.byte	0xf
	.set L$set$58,LASF0-Lsection__debug_str
	.long L$set$58
	.byte	0x8
	.byte	0x4
	.byte	0x7
	.long	0x5ba
	.byte	0xa
	.ascii "x\0"
	.byte	0x4
	.byte	0x22
	.long	0xce
	.byte	0x2
	.byte	0x23
	.byte	0x0
	.byte	0xa
	.ascii "y\0"
	.byte	0x4
	.byte	0x23
	.long	0xce
	.byte	0x2
	.byte	0x23
	.byte	0x4
	.byte	0x10
	.byte	0x1
	.set L$set$59,LASF0-Lsection__debug_str
	.long L$set$59
	.byte	0x4
	.byte	0x9
	.byte	0x1
	.long	0x4c9
	.byte	0x11
	.long	0x5ba
	.byte	0x1
	.byte	0x0
	.byte	0x10
	.byte	0x1
	.set L$set$60,LASF0-Lsection__debug_str
	.long L$set$60
	.byte	0x4
	.byte	0xb
	.byte	0x1
	.long	0x4e2
	.byte	0x11
	.long	0x5ba
	.byte	0x1
	.byte	0xd
	.long	0x5c0
	.byte	0x0
	.byte	0x10
	.byte	0x1
	.set L$set$61,LASF0-Lsection__debug_str
	.long L$set$61
	.byte	0x4
	.byte	0xd
	.byte	0x1
	.long	0x500
	.byte	0x11
	.long	0x5ba
	.byte	0x1
	.byte	0xd
	.long	0xce
	.byte	0xd
	.long	0xce
	.byte	0x0
	.byte	0x12
	.byte	0x1
	.ascii "operator=\0"
	.byte	0x4
	.byte	0xf
	.ascii "_ZN6CoordsaSERKS_\0"
	.long	0x5c0
	.byte	0x1
	.long	0x535
	.byte	0x11
	.long	0x5ba
	.byte	0x1
	.byte	0xd
	.long	0x5c0
	.byte	0x0
	.byte	0x12
	.byte	0x1
	.ascii "operator==\0"
	.byte	0x4
	.byte	0x11
	.ascii "_ZN6CoordseqERKS_\0"
	.long	0x489
	.byte	0x1
	.long	0x56b
	.byte	0x11
	.long	0x5ba
	.byte	0x1
	.byte	0xd
	.long	0x5c0
	.byte	0x0
	.byte	0x13
	.byte	0x1
	.ascii "~Coords\0"
	.byte	0x4
	.byte	0x13
	.byte	0x1
	.long	0x589
	.byte	0x11
	.long	0x5ba
	.byte	0x1
	.byte	0x11
	.long	0xce
	.byte	0x1
	.byte	0x0
	.byte	0x14
	.byte	0x1
	.ascii "setXY\0"
	.byte	0x4
	.byte	0x1f
	.ascii "_ZN6Coords5setXYEii\0"
	.byte	0x1
	.byte	0x11
	.long	0x5ba
	.byte	0x1
	.byte	0xd
	.long	0xce
	.byte	0xd
	.long	0xce
	.byte	0x0
	.byte	0x0
	.byte	0x8
	.byte	0x4
	.long	0x491
	.byte	0x15
	.byte	0x4
	.long	0x5c6
	.byte	0xe
	.long	0x491
	.byte	0x16
	.long	0x4b5
	.byte	0x1
	.byte	0x3
	.byte	0x0
	.long	0x5e2
	.byte	0x17
	.set L$set$62,LASF1-Lsection__debug_str
	.long L$set$62
	.long	0x5e2
	.byte	0x1
	.byte	0x0
	.byte	0xe
	.long	0x5ba
	.byte	0x18
	.long	0x5cb
	.ascii "_ZN6CoordsC2Ev\0"
	.long	LFB867
	.long	LFE867
	.byte	0x1
	.byte	0x6e
	.long	0x613
	.byte	0x19
	.long	0x5d7
	.byte	0x3
	.byte	0x8e
	.byte	0xc8,0x0
	.byte	0x0
	.byte	0x18
	.long	0x5cb
	.ascii "_ZN6CoordsC1Ev\0"
	.long	LFB868
	.long	LFE868
	.byte	0x1
	.byte	0x6e
	.long	0x644
	.byte	0x1a
	.set L$set$63,LASF1-Lsection__debug_str
	.long L$set$63
	.long	0x5e2
	.byte	0x1
	.byte	0x3
	.byte	0x8e
	.byte	0xe8,0x0
	.byte	0x0
	.byte	0x16
	.long	0x4c9
	.byte	0x1
	.byte	0x9
	.byte	0x0
	.long	0x666
	.byte	0x17
	.set L$set$64,LASF1-Lsection__debug_str
	.long L$set$64
	.long	0x5e2
	.byte	0x1
	.byte	0x1b
	.set L$set$65,LASF2-Lsection__debug_str
	.long L$set$65
	.byte	0x1
	.byte	0x9
	.long	0x666
	.byte	0x0
	.byte	0xe
	.long	0x5c0
	.byte	0x18
	.long	0x644
	.ascii "_ZN6CoordsC2ERKS_\0"
	.long	LFB870
	.long	LFE870
	.byte	0x1
	.byte	0x6e
	.long	0x6a3
	.byte	0x19
	.long	0x650
	.byte	0x3
	.byte	0x8e
	.byte	0xc8,0x0
	.byte	0x19
	.long	0x65a
	.byte	0x3
	.byte	0x8e
	.byte	0xcc,0x0
	.byte	0x0
	.byte	0x18
	.long	0x644
	.ascii "_ZN6CoordsC1ERKS_\0"
	.long	LFB871
	.long	LFE871
	.byte	0x1
	.byte	0x6e
	.long	0x6e6
	.byte	0x1a
	.set L$set$66,LASF1-Lsection__debug_str
	.long L$set$66
	.long	0x5e2
	.byte	0x1
	.byte	0x3
	.byte	0x8e
	.byte	0xe8,0x0
	.byte	0x1c
	.set L$set$67,LASF2-Lsection__debug_str
	.long L$set$67
	.byte	0x1
	.byte	0x9
	.long	0x6e6
	.byte	0x3
	.byte	0x8e
	.byte	0xec,0x0
	.byte	0x0
	.byte	0xe
	.long	0x5c0
	.byte	0x1d
	.long	0x500
	.byte	0x1
	.long	LFB872
	.long	LFE872
	.byte	0x1
	.byte	0x6e
	.long	0x71d
	.byte	0x1a
	.set L$set$68,LASF1-Lsection__debug_str
	.long L$set$68
	.long	0x5e2
	.byte	0x1
	.byte	0x3
	.byte	0x8e
	.byte	0xc8,0x0
	.byte	0x1c
	.set L$set$69,LASF2-Lsection__debug_str
	.long L$set$69
	.byte	0x1
	.byte	0xf
	.long	0x71d
	.byte	0x3
	.byte	0x8e
	.byte	0xcc,0x0
	.byte	0x0
	.byte	0xe
	.long	0x5c0
	.byte	0x1e
	.long	0x535
	.byte	0x1
	.byte	0x18
	.long	LFB873
	.long	LFE873
	.byte	0x1
	.byte	0x6e
	.long	0x755
	.byte	0x1a
	.set L$set$70,LASF1-Lsection__debug_str
	.long L$set$70
	.long	0x5e2
	.byte	0x1
	.byte	0x3
	.byte	0x8e
	.byte	0xc8,0x0
	.byte	0x1c
	.set L$set$71,LASF2-Lsection__debug_str
	.long L$set$71
	.byte	0x1
	.byte	0x18
	.long	0x71d
	.byte	0x3
	.byte	0x8e
	.byte	0xcc,0x0
	.byte	0x0
	.byte	0x16
	.long	0x4e2
	.byte	0x1
	.byte	0x20
	.byte	0x0
	.long	0x780
	.byte	0x17
	.set L$set$72,LASF1-Lsection__debug_str
	.long L$set$72
	.long	0x5e2
	.byte	0x1
	.byte	0x1f
	.ascii "x_\0"
	.byte	0x1
	.byte	0x20
	.long	0x780
	.byte	0x1f
	.ascii "y_\0"
	.byte	0x1
	.byte	0x20
	.long	0x780
	.byte	0x0
	.byte	0xe
	.long	0xce
	.byte	0x18
	.long	0x755
	.ascii "_ZN6CoordsC2Eii\0"
	.long	LFB875
	.long	LFE875
	.byte	0x1
	.byte	0x6e
	.long	0x7c4
	.byte	0x19
	.long	0x761
	.byte	0x3
	.byte	0x8e
	.byte	0xc8,0x0
	.byte	0x19
	.long	0x76b
	.byte	0x3
	.byte	0x8e
	.byte	0xcc,0x0
	.byte	0x19
	.long	0x775
	.byte	0x3
	.byte	0x8e
	.byte	0xd0,0x0
	.byte	0x0
	.byte	0x18
	.long	0x755
	.ascii "_ZN6CoordsC1Eii\0"
	.long	LFB876
	.long	LFE876
	.byte	0x1
	.byte	0x6e
	.long	0x812
	.byte	0x1a
	.set L$set$73,LASF1-Lsection__debug_str
	.long L$set$73
	.long	0x5e2
	.byte	0x1
	.byte	0x3
	.byte	0x8e
	.byte	0xe8,0x0
	.byte	0x20
	.ascii "x_\0"
	.byte	0x1
	.byte	0x20
	.long	0x780
	.byte	0x3
	.byte	0x8e
	.byte	0xec,0x0
	.byte	0x20
	.ascii "y_\0"
	.byte	0x1
	.byte	0x20
	.long	0x780
	.byte	0x3
	.byte	0x8e
	.byte	0xf0,0x0
	.byte	0x0
	.byte	0x16
	.long	0x56b
	.byte	0x1
	.byte	0x26
	.byte	0x0
	.long	0x839
	.byte	0x17
	.set L$set$74,LASF1-Lsection__debug_str
	.long L$set$74
	.long	0x5e2
	.byte	0x1
	.byte	0x21
	.ascii "__in_chrg\0"
	.long	0x780
	.byte	0x1
	.byte	0x0
	.byte	0x18
	.long	0x812
	.ascii "_ZN6CoordsD2Ev\0"
	.long	LFB878
	.long	LFE878
	.byte	0x1
	.byte	0x6e
	.long	0x865
	.byte	0x19
	.long	0x81e
	.byte	0x3
	.byte	0x8e
	.byte	0xc8,0x0
	.byte	0x0
	.byte	0x18
	.long	0x812
	.ascii "_ZN6CoordsD1Ev\0"
	.long	LFB879
	.long	LFE879
	.byte	0x1
	.byte	0x6e
	.long	0x896
	.byte	0x1a
	.set L$set$75,LASF1-Lsection__debug_str
	.long L$set$75
	.long	0x5e2
	.byte	0x1
	.byte	0x3
	.byte	0x8e
	.byte	0xe8,0x0
	.byte	0x0
	.byte	0x1e
	.long	0x589
	.byte	0x1
	.byte	0x2a
	.long	LFB880
	.long	LFE880
	.byte	0x1
	.byte	0x6e
	.long	0x8d6
	.byte	0x1a
	.set L$set$76,LASF1-Lsection__debug_str
	.long L$set$76
	.long	0x5e2
	.byte	0x1
	.byte	0x3
	.byte	0x8e
	.byte	0xc8,0x0
	.byte	0x20
	.ascii "x_\0"
	.byte	0x1
	.byte	0x2a
	.long	0x780
	.byte	0x3
	.byte	0x8e
	.byte	0xcc,0x0
	.byte	0x20
	.ascii "y_\0"
	.byte	0x1
	.byte	0x2a
	.long	0x780
	.byte	0x3
	.byte	0x8e
	.byte	0xd0,0x0
	.byte	0x0
	.byte	0x22
	.ascii "::\0"
	.byte	0x5
	.byte	0x0
	.long	0x8ee
	.byte	0x23
	.set L$set$77,LASF3-Lsection__debug_str
	.long L$set$77
	.byte	0x3
	.byte	0x84
	.long	0x285
	.byte	0x1
	.byte	0x1
	.byte	0x0
	.byte	0x23
	.set L$set$78,LASF3-Lsection__debug_str
	.long L$set$78
	.byte	0x3
	.byte	0x84
	.long	0x285
	.byte	0x1
	.byte	0x1
	.byte	0x0
	.section __DWARF,__debug_abbrev,regular,debug
	.byte	0x1
	.byte	0x11
	.byte	0x1
	.byte	0x25
	.byte	0x8
	.byte	0x13
	.byte	0xb
	.byte	0x3
	.byte	0x8
	.byte	0x11
	.byte	0x1
	.byte	0x12
	.byte	0x1
	.byte	0x10
	.byte	0x6
	.byte	0x0
	.byte	0x0
	.byte	0x2
	.byte	0x24
	.byte	0x0
	.byte	0xb
	.byte	0xb
	.byte	0x3e
	.byte	0xb
	.byte	0x3
	.byte	0x8
	.byte	0x0
	.byte	0x0
	.byte	0x3
	.byte	0x16
	.byte	0x0
	.byte	0x3
	.byte	0x8
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x49
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x4
	.byte	0x24
	.byte	0x0
	.byte	0xb
	.byte	0xb
	.byte	0x3e
	.byte	0xb
	.byte	0x0
	.byte	0x0
	.byte	0x5
	.byte	0xf
	.byte	0x0
	.byte	0xb
	.byte	0xb
	.byte	0x0
	.byte	0x0
	.byte	0x6
	.byte	0x1
	.byte	0x1
	.byte	0x49
	.byte	0x13
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x7
	.byte	0x21
	.byte	0x0
	.byte	0x49
	.byte	0x13
	.byte	0x2f
	.byte	0xb
	.byte	0x0
	.byte	0x0
	.byte	0x8
	.byte	0xf
	.byte	0x0
	.byte	0xb
	.byte	0xb
	.byte	0x49
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x9
	.byte	0x13
	.byte	0x1
	.byte	0x3
	.byte	0x8
	.byte	0xb
	.byte	0xb
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0xa
	.byte	0xd
	.byte	0x0
	.byte	0x3
	.byte	0x8
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x49
	.byte	0x13
	.byte	0x38
	.byte	0xa
	.byte	0x0
	.byte	0x0
	.byte	0xb
	.byte	0x13
	.byte	0x1
	.byte	0x3
	.byte	0x8
	.byte	0xb
	.byte	0x5
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0xc
	.byte	0x15
	.byte	0x1
	.byte	0x49
	.byte	0x13
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0xd
	.byte	0x5
	.byte	0x0
	.byte	0x49
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0xe
	.byte	0x26
	.byte	0x0
	.byte	0x49
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0xf
	.byte	0x13
	.byte	0x1
	.byte	0x3
	.byte	0xe
	.byte	0xb
	.byte	0xb
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x10
	.byte	0x2e
	.byte	0x1
	.byte	0x3f
	.byte	0xc
	.byte	0x3
	.byte	0xe
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x3c
	.byte	0xc
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x11
	.byte	0x5
	.byte	0x0
	.byte	0x49
	.byte	0x13
	.byte	0x34
	.byte	0xc
	.byte	0x0
	.byte	0x0
	.byte	0x12
	.byte	0x2e
	.byte	0x1
	.byte	0x3f
	.byte	0xc
	.byte	0x3
	.byte	0x8
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x87,0x40
	.byte	0x8
	.byte	0x49
	.byte	0x13
	.byte	0x3c
	.byte	0xc
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x13
	.byte	0x2e
	.byte	0x1
	.byte	0x3f
	.byte	0xc
	.byte	0x3
	.byte	0x8
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x3c
	.byte	0xc
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x14
	.byte	0x2e
	.byte	0x1
	.byte	0x3f
	.byte	0xc
	.byte	0x3
	.byte	0x8
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x87,0x40
	.byte	0x8
	.byte	0x3c
	.byte	0xc
	.byte	0x0
	.byte	0x0
	.byte	0x15
	.byte	0x10
	.byte	0x0
	.byte	0xb
	.byte	0xb
	.byte	0x49
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x16
	.byte	0x2e
	.byte	0x1
	.byte	0x47
	.byte	0x13
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x20
	.byte	0xb
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x17
	.byte	0x5
	.byte	0x0
	.byte	0x3
	.byte	0xe
	.byte	0x49
	.byte	0x13
	.byte	0x34
	.byte	0xc
	.byte	0x0
	.byte	0x0
	.byte	0x18
	.byte	0x2e
	.byte	0x1
	.byte	0x31
	.byte	0x13
	.byte	0x87,0x40
	.byte	0x8
	.byte	0x11
	.byte	0x1
	.byte	0x12
	.byte	0x1
	.byte	0x40
	.byte	0xa
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x19
	.byte	0x5
	.byte	0x0
	.byte	0x31
	.byte	0x13
	.byte	0x2
	.byte	0xa
	.byte	0x0
	.byte	0x0
	.byte	0x1a
	.byte	0x5
	.byte	0x0
	.byte	0x3
	.byte	0xe
	.byte	0x49
	.byte	0x13
	.byte	0x34
	.byte	0xc
	.byte	0x2
	.byte	0xa
	.byte	0x0
	.byte	0x0
	.byte	0x1b
	.byte	0x5
	.byte	0x0
	.byte	0x3
	.byte	0xe
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x49
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x1c
	.byte	0x5
	.byte	0x0
	.byte	0x3
	.byte	0xe
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x49
	.byte	0x13
	.byte	0x2
	.byte	0xa
	.byte	0x0
	.byte	0x0
	.byte	0x1d
	.byte	0x2e
	.byte	0x1
	.byte	0x47
	.byte	0x13
	.byte	0x3a
	.byte	0xb
	.byte	0x11
	.byte	0x1
	.byte	0x12
	.byte	0x1
	.byte	0x40
	.byte	0xa
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x1e
	.byte	0x2e
	.byte	0x1
	.byte	0x47
	.byte	0x13
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x11
	.byte	0x1
	.byte	0x12
	.byte	0x1
	.byte	0x40
	.byte	0xa
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x1f
	.byte	0x5
	.byte	0x0
	.byte	0x3
	.byte	0x8
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x49
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x20
	.byte	0x5
	.byte	0x0
	.byte	0x3
	.byte	0x8
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x49
	.byte	0x13
	.byte	0x2
	.byte	0xa
	.byte	0x0
	.byte	0x0
	.byte	0x21
	.byte	0x5
	.byte	0x0
	.byte	0x3
	.byte	0x8
	.byte	0x49
	.byte	0x13
	.byte	0x34
	.byte	0xc
	.byte	0x0
	.byte	0x0
	.byte	0x22
	.byte	0x39
	.byte	0x1
	.byte	0x3
	.byte	0x8
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x1
	.byte	0x13
	.byte	0x0
	.byte	0x0
	.byte	0x23
	.byte	0x34
	.byte	0x0
	.byte	0x3
	.byte	0xe
	.byte	0x3a
	.byte	0xb
	.byte	0x3b
	.byte	0xb
	.byte	0x49
	.byte	0x13
	.byte	0x3f
	.byte	0xc
	.byte	0x3c
	.byte	0xc
	.byte	0x0
	.byte	0x0
	.byte	0x0
	.section __DWARF,__debug_pubnames,regular,debug
	.long	0xe7
	.short	0x2
	.set L$set$79,Ldebug_info0-Lsection__debug_info
	.long L$set$79
	.long	0x8fc
	.long	0x5e7
	.ascii "Coords::Coords\0"
	.long	0x613
	.ascii "Coords::Coords\0"
	.long	0x66b
	.ascii "Coords::Coords\0"
	.long	0x6a3
	.ascii "Coords::Coords\0"
	.long	0x6eb
	.ascii "Coords::operator=\0"
	.long	0x722
	.ascii "Coords::operator==\0"
	.long	0x785
	.ascii "Coords::Coords\0"
	.long	0x7c4
	.ascii "Coords::Coords\0"
	.long	0x839
	.ascii "Coords::~Coords\0"
	.long	0x865
	.ascii "Coords::~Coords\0"
	.long	0x896
	.ascii "Coords::setXY\0"
	.long	0x0
	.section __DWARF,__debug_pubtypes,regular,debug
	.long	0x87
	.short	0x2
	.set L$set$80,Ldebug_info0-Lsection__debug_info
	.long L$set$80
	.long	0x8fc
	.long	0xd5
	.ascii "__uint32_t\0"
	.long	0x139
	.ascii "__darwin_size_t\0"
	.long	0x165
	.ascii "__darwin_wchar_t\0"
	.long	0x17d
	.ascii "__darwin_rune_t\0"
	.long	0x1b6
	.ascii "$_8\0"
	.long	0x20b
	.ascii "$_9\0"
	.long	0x245
	.ascii "$_10\0"
	.long	0x285
	.ascii "$_11\0"
	.long	0x491
	.ascii "Coords\0"
	.long	0x0
	.section __DWARF,__debug_aranges,regular,debug
	.long	0x1c
	.short	0x2
	.set L$set$81,Ldebug_info0-Lsection__debug_info
	.long L$set$81
	.byte	0x4
	.byte	0x0
	.short	0x0
	.short	0x0
	.long	Ltext0
	.set L$set$82,Letext0-Ltext0
	.long L$set$82
	.long	0x0
	.long	0x0
	.section __DWARF,__debug_line,regular,debug
	.set L$set$83,LELT0-LSLT0
	.long L$set$83
LSLT0:
	.short	0x2
	.set L$set$84,LELTP0-LASLTP0
	.long L$set$84
LASLTP0:
	.byte	0x1
	.byte	0x1
	.byte	0xf6
	.byte	0xf5
	.byte	0xa
	.byte	0x0
	.byte	0x1
	.byte	0x1
	.byte	0x1
	.byte	0x1
	.byte	0x0
	.byte	0x0
	.byte	0x0
	.byte	0x1
	.ascii "/Developer/SDKs/MacOSX10.5.sdk/usr/include"
	.byte	0
	.ascii "/Developer/SDKs/MacOSX10.5.sdk/usr/include/ppc"
	.byte	0
	.ascii "/Users/jeff/Documents/juce/extras/jeffproj/Mac/Ibex/../../common"
	.byte	0
	.byte	0x0
	.ascii "Coords.cpp\0"
	.byte	0x3
	.byte	0x0
	.byte	0x0
	.ascii "ppc/_types.h\0"
	.byte	0x1
	.byte	0x0
	.byte	0x0
	.ascii "runetype.h\0"
	.byte	0x1
	.byte	0x0
	.byte	0x0
	.ascii "Coords.h\0"
	.byte	0x3
	.byte	0x0
	.byte	0x0
	.ascii "<built-in>\0"
	.byte	0x0
	.byte	0x0
	.byte	0x0
	.byte	0x0
LELTP0:
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM1
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM2
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM3
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM4
	.byte	0x10
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM5
	.byte	0x18
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM6
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM7
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM8
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM9
	.byte	0x10
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM10
	.byte	0x18
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM11
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM12
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM13
	.byte	0x15
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM14
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM15
	.byte	0x15
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM16
	.byte	0x17
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM17
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM18
	.byte	0x15
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM19
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM20
	.byte	0x15
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM21
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM22
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM23
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM24
	.byte	0x10
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM25
	.byte	0x18
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM26
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM27
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM28
	.byte	0x12
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM29
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM30
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM31
	.byte	0x16
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM32
	.byte	0x15
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	LM33
	.byte	0x15
	.byte	0x0
	.byte	0x5
	.byte	0x2
	.long	Letext0
	.byte	0x0
	.byte	0x1
	.byte	0x1
LELT0:
	.section __DWARF,__debug_str,regular,debug
LASF2:
	.ascii "other\0"
LASF1:
	.ascii "this\0"
LASF0:
	.ascii "Coords\0"
LASF3:
	.ascii "_DefaultRuneLocale\0"
	.constructor
	.destructor
	.align 1
	.subsections_via_symbols
