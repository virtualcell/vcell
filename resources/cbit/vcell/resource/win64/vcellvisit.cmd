echo "visitcmd = " %visitcmd%
echo "pythonscript = " %pythonscript%
cmd /K "%visitcmd%" -cli -uifile %pythonscript%
