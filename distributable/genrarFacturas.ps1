$curretLocation = Get-Location
set-location $PSScriptRoot

if(-not $env:JAVA_HOME)
{
    Write-Error "JAVA_HOME not set"
    break
}

Start-Process -FilePath java `
-ArgumentList '-jar rts-0.0.1-SNAPSHOT-jar-with-dependencies.jar' `
-PassThru -RedirectStandardError $PSScriptRoot\stderr.txt 

Set-Location $curretLocation
# $params = @{
#     FilePath = [string]::Format("{0}\bin\java.exe",$env:JAVA_HOME)
#     WorkingDirectory = $PSScriptRoot
#     ArgumentList = @("-jar", "discovery-registration.jar", "update")
#     RedirectStandardError = "c:\temp\JavaError.txt"
#     PassThru = $true
#     Wait = $true
# }

# $p = Start-Process @params

# if($p.ExitCode -eq 0)
# {
#     Write-Output "Discovery Registration complete"
# }
# else
# {
#     Write-Output "Discovery Registration failed"
# }