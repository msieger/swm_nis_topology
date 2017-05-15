from qgis.core import *
from qgis.gui import *
from PyQt4.QtGui import *
import os.path

dir_path = os.path.dirname(os.path.realpath(__file__))



class Marker(QgsMarkerSymbolLayerV2):

    def __init__(self, path):
        QgsMarkerSymbolLayerV2.__init__(self)
        self.path = path
        self.icon = QIcon(os.path.join(dir_path, "icon", path))

    def layerType(self):
        return "PngMarker"

    def properties(self):
        return {}

    def startRender(self, context):
        pass

    def stopRender(self, context):
        pass

    def renderPoint(self, point, context):
        p = context.renderContext().painter()
        p.drawImage(self.icon)

    def clone(self):
        return Marker(self.path)