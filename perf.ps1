.\gradlew clean run

$originalDir = Get-Location
$viewerDir = ".\app\src\main\java\org\db_poultry\perf\viewer"
$csvFileName = ".\app\posteriori.csv"

# Copy CSV to viewer folder
Copy-Item "$originalDir\$csvFileName" "$viewerDir"

Set-Location $viewerDir

uv run main.py "posteriori.csv"

# Delete copied CSV inside viewer folder
Remove-Item ".\posteriori.csv"

Set-Location $originalDir
