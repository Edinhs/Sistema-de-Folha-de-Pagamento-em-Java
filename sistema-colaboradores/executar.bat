@echo off
chcp 65001 > nul
title Sistema de Colaboradores e Folha de Pagamento - Inicializador

REM Verifica se o Java esta no PATH ou se existe no local padrao do Zulu JDK
java -version >nul 2>&1
if %errorlevel% equ 0 (
    set JAVA_EXEC=java
    goto has_java
)

if exist "C:\Program Files\Java\zulu25.32.21-ca-jdk25.0.2-win_x64\bin\java.exe" (
    set JAVA_EXEC="C:\Program Files\Java\zulu25.32.21-ca-jdk25.0.2-win_x64\bin\java.exe"
    goto has_java
)

echo [ERRO] O comando 'java' nao foi encontrado no seu PATH de sistema.
echo.
echo Tentamos buscar o Java no caminho padrao, mas nao foi localizado.
echo Certifique-se de que o Java JDK esta instalado e configurado no PATH.
echo.
echo Pressione qualquer tecla para sair...
pause > nul
exit /b

:has_java
:menu
cls
echo ==================================================
echo   SISTEMA DE COLABORADORES E FOLHA DE PAGAMENTO   
echo ==================================================
echo.
echo Escolha o modo de execucao desejado:
echo.
echo  [1] Interface Grafica Premium (Swing GUI) - Recomendado
echo  [2] Modo Terminal Academico (Console CLI)
echo  [0] Sair
echo.
echo ==================================================
set /p opcao="Digite a opcao (0-2): "

if "%opcao%"=="1" goto run_gui
if "%opcao%"=="2" goto run_console
if "%opcao%"=="0" goto exit
echo.
echo [!] Opcao invalida! Pressione qualquer tecla para tentar novamente.
pause > nul
goto menu

:run_gui
echo.
echo [+] Inicializando a Interface Grafica Premium (Swing)...
%JAVA_EXEC% -cp bin Main
goto end

:run_console
echo.
echo [+] Inicializando a Interface de Console Academica...
%JAVA_EXEC% -cp bin Main --console
goto end

:end
echo.
echo >>> Execucao encerrada. Pressione qualquer tecla para sair...
pause > nul

:exit
