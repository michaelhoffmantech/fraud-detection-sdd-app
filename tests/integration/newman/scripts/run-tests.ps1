# Run Integration Tests Script

param(
    [string]$Environment = "local",
    [switch]$GenerateReport = $true,
    [switch]$ValidateOnly = $false
)

function Test-NodeJs {
    try {
        $nodeVersion = node --version
        return $true
    } catch {
        return $false
    }
}

function Test-Newman {
    try {
        $newmanVersion = newman --version
        return $true
    } catch {
        return $false
    }
}

function Install-Prerequisites {
    Write-Host "Checking prerequisites..."
    
    # Check Node.js
    if (-not (Test-NodeJs)) {
        Write-Host "Node.js is not installed or not in PATH. Please install Node.js:" -ForegroundColor Yellow
        Write-Host "1. Visit https://nodejs.org/"
        Write-Host "2. Download and install Node.js LTS"
        Write-Host "3. Restart your terminal"
        Write-Host "4. Run 'node --version' to verify"
        return $false
    }
    
    # Check Newman
    if (-not (Test-Newman)) {
        Write-Host "Newman is not installed. Installing now..." -ForegroundColor Yellow
        npm install -g newman newman-reporter-htmlextra
        if (-not (Test-Newman)) {
            Write-Host "Failed to install Newman. Please install manually:" -ForegroundColor Red
            Write-Host "1. Open a new terminal"
            Write-Host "2. Run: npm install -g newman newman-reporter-htmlextra"
            return $false
        }
    }
    
    return $true
}

# Validate environment first if requested
if ($ValidateOnly) {
    $result = Install-Prerequisites
    if ($result) {
        Write-Host "Environment validation successful!" -ForegroundColor Green
    } else {
        Write-Host "Environment validation failed. Please fix the issues above." -ForegroundColor Red
    }
    exit [int](-not $result)
}

# Run prerequisite check before proceeding
if (-not (Install-Prerequisites)) {
    Write-Error "Missing prerequisites. Please install required tools and try again."
    exit 1
}

# Set default paths
$CollectionPath = Join-Path $PSScriptRoot "..\..\postman\collections\fraud-detection.json"
$EnvironmentPath = Join-Path $PSScriptRoot "..\..\postman\environments\$Environment.json"
$DataPath = Join-Path $PSScriptRoot "..\..\postman\data\test-cases.json"
$ReportPath = Join-Path $PSScriptRoot "..\reports"

# Create reports directory if it doesn't exist
if (!(Test-Path $ReportPath)) {
    New-Item -ItemType Directory -Path $ReportPath | Out-Null
}

# Validate files exist
if (!(Test-Path $CollectionPath)) {
    Write-Error "Collection file not found at: $CollectionPath"
    exit 1
}

if (!(Test-Path $EnvironmentPath)) {
    Write-Error "Environment file not found at: $EnvironmentPath"
    exit 1
}

if (!(Test-Path $DataPath)) {
    Write-Error "Test data file not found at: $DataPath"
    exit 1
}

# Build Newman command
$NewmanCommand = "newman run `"$CollectionPath`" -e `"$EnvironmentPath`" -d `"$DataPath`""

if ($GenerateReport) {
    $NewmanCommand += " -r cli,htmlextra --reporter-htmlextra-export `"$ReportPath\report-$Environment.html`""
}

# Execute tests
try {
    Write-Host "Running integration tests for $Environment environment..."
    Invoke-Expression $NewmanCommand
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Tests completed successfully!" -ForegroundColor Green
        if ($GenerateReport) {
            Write-Host "Report generated at: $ReportPath\report-$Environment.html"
        }
    }
    else {
        Write-Host "Tests failed with exit code: $LASTEXITCODE" -ForegroundColor Red
        exit $LASTEXITCODE
    }
}
catch {
    Write-Error "Error executing tests: $_"
    exit 1
}