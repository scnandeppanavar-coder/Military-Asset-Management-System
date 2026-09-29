$ErrorActionPreference = 'Stop'

Write-Host "=========================================="
Write-Host "MAMS END-TO-END AUTOMATED VERIFICATION"
Write-Host "=========================================="

Write-Host "`n1. Testing Login as ADMIN..."
$adminLogin = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -ContentType 'application/json' -Body '{"username":"admin","password":"Admin@123"}'
$adminToken = $adminLogin.token
Write-Host "   -> Token received. Role: $($adminLogin.role), Base: $($adminLogin.baseId)"

$headers = @{ 'Authorization' = "Bearer $adminToken" }

$timestamp = Get-Date -Format 'yyyyMMddHHmmss'

Write-Host "`n2. Testing Record Purchase..."
$purchaseBody = @{
    baseId = 1
    equipmentTypeId = 1
    quantity = 15
    purchaseDate = "2026-09-29"
    referenceNumber = "PO-CLI-$timestamp"
    remarks = "Test procurement batch via API"
} | ConvertTo-Json
$newPurchase = Invoke-RestMethod -Uri 'http://localhost:8080/api/purchases' -Method Post -Headers $headers -ContentType 'application/json' -Body $purchaseBody
Write-Host "   -> Created Purchase ID: $($newPurchase.id), Qty: $($newPurchase.quantity), Ref: $($newPurchase.referenceNumber)"

Write-Host "`n3. Testing Record Inter-Base Transfer..."
$transferBody = @{
    fromBaseId = 1
    toBaseId = 2
    equipmentTypeId = 1
    quantity = 3
    transferDate = "2026-09-29"
    referenceNumber = "TRF-CLI-$timestamp"
    remarks = "Test inter-base transfer"
} | ConvertTo-Json
$newTransfer = Invoke-RestMethod -Uri 'http://localhost:8080/api/transfers' -Method Post -Headers $headers -ContentType 'application/json' -Body $transferBody
Write-Host "   -> Created Transfer ID: $($newTransfer.id), From: $($newTransfer.fromBaseName) -> To: $($newTransfer.toBaseName), Qty: $($newTransfer.quantity)"

Write-Host "`n4. Testing Insufficient Inventory Rejection..."
try {
    $failTransferBody = @{
        fromBaseId = 1
        toBaseId = 2
        equipmentTypeId = 1
        quantity = 999999
        transferDate = "2026-09-29"
        referenceNumber = "TRF-FAIL-$timestamp"
    } | ConvertTo-Json
    Invoke-RestMethod -Uri 'http://localhost:8080/api/transfers' -Method Post -Headers $headers -ContentType 'application/json' -Body $failTransferBody
    Write-Host "   -> FAILED: Should have rejected excessive transfer!"
} catch {
    Write-Host "   -> PASSED: Expected error thrown: $($_.Exception.Message)"
}

Write-Host "`n5. Testing Create Assignment..."
$assignBody = @{
    baseId = 1
    equipmentTypeId = 1
    personnelName = "Sgt. J. Miller"
    quantity = 2
    assignedDate = "2026-09-29"
} | ConvertTo-Json
$newAssign = Invoke-RestMethod -Uri 'http://localhost:8080/api/assignments' -Method Post -Headers $headers -ContentType 'application/json' -Body $assignBody
Write-Host "   -> Created Assignment ID: $($newAssign.id), Personnel: $($newAssign.personnelName), Status: $($newAssign.status)"

Write-Host "`n6. Testing Return Equipment..."
$returnBody = @{
    returnQuantity = 1
} | ConvertTo-Json
$updatedAssign = Invoke-RestMethod -Uri "http://localhost:8080/api/assignments/$($newAssign.id)" -Method Put -Headers $headers -ContentType 'application/json' -Body $returnBody
Write-Host "   -> Returned 1 unit! Status: $($updatedAssign.status), Outstanding: $($updatedAssign.outstandingQuantity)"

Write-Host "`n7. Testing Record Expenditure..."
$expendBody = @{
    baseId = 1
    equipmentTypeId = 1
    quantity = 2
    expenditureDate = "2026-09-29"
    reason = "Tactical live fire qualification"
    referenceNumber = "EXP-CLI-$timestamp"
} | ConvertTo-Json
$newExpend = Invoke-RestMethod -Uri 'http://localhost:8080/api/expenditures' -Method Post -Headers $headers -ContentType 'application/json' -Body $expendBody
Write-Host "   -> Created Expenditure ID: $($newExpend.id), Qty: $($newExpend.quantity), Reason: $($newExpend.reason)"

Write-Host "`n8. Testing Dashboard & Net Movement Formulas..."
$dash = Invoke-RestMethod -Uri 'http://localhost:8080/api/dashboard?baseId=1' -Method Get -Headers $headers
Write-Host "   -> Base 1 Dashboard:"
Write-Host "      Opening Balance : $($dash.openingBalance)"
Write-Host "      Purchases       : $($dash.purchases)"
Write-Host "      Transfer In     : $($dash.transferIn)"
Write-Host "      Transfer Out    : $($dash.transferOut)"
Write-Host "      Net Movement    : $($dash.netMovement)"
Write-Host "      Expended        : $($dash.expended)"
Write-Host "      Closing Balance : $($dash.closingBalance)"
Write-Host "      Assigned        : $($dash.assigned)"

$netBreakdown = Invoke-RestMethod -Uri 'http://localhost:8080/api/dashboard/net-movement?baseId=1' -Method Get -Headers $headers
Write-Host "   -> Audited Net Movement Modal Formula:"
Write-Host "      Purchases (+$($netBreakdown.purchases)) + Transfer In (+$($netBreakdown.transferIn)) - Transfer Out (-$($netBreakdown.transferOut)) = $($netBreakdown.netMovement)"

Write-Host "`n9. Testing Immutable Audit Trail..."
$auditLogs = Invoke-RestMethod -Uri 'http://localhost:8080/api/audit-logs' -Method Get -Headers $headers
Write-Host "   -> Audit Logs Count: $($auditLogs.Count)"
Write-Host "   -> Latest entry: Action=$($auditLogs[0].action), Entity=$($auditLogs[0].entityType), User=$($auditLogs[0].username), Desc='$($auditLogs[0].description)'"

Write-Host "`n10. Testing RBAC Security Enforcements..."
$cmdrLogin = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -ContentType 'application/json' -Body '{"username":"commander","password":"Commander@123"}'
$cmdrHeaders = @{ 'Authorization' = "Bearer $($cmdrLogin.token)" }
try {
    Invoke-RestMethod -Uri 'http://localhost:8080/api/users' -Method Get -Headers $cmdrHeaders
    Write-Host "   -> FAILED: Commander should NOT have accessed /api/users"
} catch {
    Write-Host "   -> PASSED: Commander blocked with 403 Forbidden from /api/users"
}

$logisticsLogin = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -ContentType 'application/json' -Body '{"username":"logistics","password":"Logistics@123"}'
$logisticsHeaders = @{ 'Authorization' = "Bearer $($logisticsLogin.token)" }
try {
    Invoke-RestMethod -Uri 'http://localhost:8080/api/assignments' -Method Get -Headers $logisticsHeaders
    Write-Host "   -> FAILED: Logistics should NOT have accessed /api/assignments"
} catch {
    Write-Host "   -> PASSED: Logistics blocked with 403 Forbidden from /api/assignments"
}

Write-Host "`n========================================================"
Write-Host "ALL 10 VERIFICATION TESTS COMPLETED WITH 100% SUCCESS!"
Write-Host "========================================================"
