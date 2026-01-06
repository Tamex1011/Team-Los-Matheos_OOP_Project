# Compile and run the app with the MySQL driver on the classpath.
# Usage: powershell -ExecutionPolicy Bypass -File .\build_and_run.ps1

$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $root

if (-not (Test-Path "$root\out")) {
    New-Item -ItemType Directory -Path "$root\out" | Out-Null
}

$cp = "lib\mysql-connector-j-9.4.0.jar;src"
$sources = Get-ChildItem -Path "$root\src" -Recurse -Filter *.java | ForEach-Object { $_.FullName }

javac -d "$root\out" -cp $cp $sources
if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed."
    exit 1
}

java -cp "$root\out;lib\mysql-connector-j-9.4.0.jar" LTOSystem

