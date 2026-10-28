$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $projectRoot ".env"

if (Test-Path -LiteralPath $envFile) {
  foreach ($line in Get-Content -LiteralPath $envFile) {
    if ($line -match '^\s*(DB_URL|DB_USER|DB_PASSWORD|HIBERNATE_DDL)\s*=\s*(.*?)\s*$') {
      $name = $Matches[1]
      $value = $Matches[2]
      $currentValue = [Environment]::GetEnvironmentVariable($name, "Process")

      if (
        [string]::IsNullOrWhiteSpace($currentValue) -and
        $value.Length -ge 2 -and
        (($value.StartsWith('"') -and $value.EndsWith('"')) -or
          ($value.StartsWith("'") -and $value.EndsWith("'")))
      ) {
        $value = $value.Substring(1, $value.Length - 2)
      }

      if ([string]::IsNullOrWhiteSpace($currentValue)) {
        [Environment]::SetEnvironmentVariable($name, $value, "Process")
      }
    }
  }
}

$jdkCandidates = @()

if ($env:JAVA_HOME) {
  $jdkCandidates += $env:JAVA_HOME
}

$jdkSearchRoots = @(
  (Join-Path $HOME ".jdk"),
  "C:\Program Files\Java",
  "C:\Program Files\Eclipse Adoptium"
)

foreach ($searchRoot in $jdkSearchRoots) {
  if (Test-Path -LiteralPath $searchRoot) {
    $jdkCandidates += Get-ChildItem -LiteralPath $searchRoot -Directory -Filter "jdk-25*" |
      Sort-Object Name -Descending |
      Select-Object -ExpandProperty FullName
  }
}

$jdkHome = $null
foreach ($candidate in ($jdkCandidates | Select-Object -Unique)) {
  $javac = Join-Path $candidate "bin\javac.exe"
  $releaseFile = Join-Path $candidate "release"
  if (
    (Test-Path -LiteralPath $javac) -and
    (Test-Path -LiteralPath $releaseFile) -and
    (Get-Content -LiteralPath $releaseFile -Raw) -match 'JAVA_VERSION="25(?:\.|")'
  ) {
    $jdkHome = $candidate
    break
  }
}

if (-not $jdkHome) {
  throw "Java 25 JDK was not found. Install JDK 25 or set JAVA_HOME to its installation folder."
}

$env:JAVA_HOME = $jdkHome
$env:Path = "$(Join-Path $jdkHome 'bin');$env:Path"
$quietJvmOptions = "--enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow"
if ($env:MAVEN_OPTS) {
  $env:MAVEN_OPTS = "$env:MAVEN_OPTS $quietJvmOptions"
} else {
  $env:MAVEN_OPTS = $quietJvmOptions
}

$maven = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $maven) {
  throw "Maven was not found on PATH. Install Maven 3.9 or newer and try again."
}

Push-Location $projectRoot
$previousErrorActionPreference = $ErrorActionPreference
try {
  $ErrorActionPreference = "Continue"
  & $maven.Source -q exec:java
  $mavenExitCode = $LASTEXITCODE
}
finally {
  $ErrorActionPreference = $previousErrorActionPreference
  Pop-Location
}

exit $mavenExitCode
