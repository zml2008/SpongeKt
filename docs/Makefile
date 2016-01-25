LIBRARIES=latex markdown
PROJECTS=manual

ifeq ("$(wildcard ./makefile-templates/pdfbase.mk)","")
	$(error The makefile-templates folder does not exist. Have you initialized submodules yet?)
endif

include ./makefile-templates/pdfbase.mk
