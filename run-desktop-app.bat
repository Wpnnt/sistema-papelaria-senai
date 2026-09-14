@echo off
echo ========================================================
echo   Iniciando Sistema de Papelaria SENAI - Modulo Desktop
echo ========================================================
call .\mvnw.cmd compile exec:java "-Dexec.mainClass=com.projetofinal.backend.client.StationeryDesktopApp"
pause
