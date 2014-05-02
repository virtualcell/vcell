echo "visitcmd = " %visitcmd%
echo "pythonscript = " %pythonscript%
set path="%path%;C:\Windows\system32"
echo "path = " %path%
C:\Windows\system32\cmd.exe /K %visitcmd% -cli -uifile %pythonscript%