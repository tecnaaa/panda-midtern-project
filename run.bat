@echo off
cd /d %~dp0
if not exist out mkdir out
javac -d out vn\edu\tdtu\edocument\MainSwingUI.java
java -cp out vn.edu.tdtu.edocument.MainSwingUI
pause