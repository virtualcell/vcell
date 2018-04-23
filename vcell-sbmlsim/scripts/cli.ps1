param (
    [string]$server = "http://defaultserver",
    [Parameter(Mandatory=$true)][string]$username,
    [string]$password = $( Read-Host "Input password, please" )
)

$Env:Path = "..\localsolvers\win64" + ";" + $Env:Path

Write-Host "not yet implemented for windows 64"

Start-Job 