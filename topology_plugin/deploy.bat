    @echo off
    if "%QGIS_PLUGIN_DIR%" == "" (echo "QGIS_PLUGIN_DIR is not defined")
    set plugin_path=%QGIS_PLUGIN_DIR%\topology_plugin\
    rmdir /s /q %plugin_path%
    xcopy * %plugin_path% /s
