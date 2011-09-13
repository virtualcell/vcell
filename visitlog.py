# Visit 2.4.0 log file
ScriptVersion = "2.4.0"
if ScriptVersion != Version():
    print "This script is for VisIt %s. It may not work with version %s" % (ScriptVersion, Version())
ShowAllWindows()
Source("/home/CAM/eboyce/Desktop/COMPLETELYDIFFERENT-Visit2.4-Trunk-DEBUG/src/bin/makemovie.py")
RestoreSession("/tmp/SimID_57437624_0_1936989894457831239.session", 0)
