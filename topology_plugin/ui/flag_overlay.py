from qgis.core import *
from qgis.gui import *
from PyQt4.QtGui import *
from util import *
from marker import *

START_LAYER_NAME = "NIS topology flag overlay - start"
FINISH_LAYER_NAME = "NIS topology flag overlay - end"


class FlagOverlay:

    def __init__(self, iface):
        self.iface = iface
        self.start = None
        self.finish = None

    def _style_layer(self, layer):
        symbol = QgsMarkerSymbolV2.createSimple({"name": "capital", "width": "4.0", "color": "0,0,0"})
        layer.rendererV2().setSymbol(symbol)


    def get_start_layer(self):
        return get_overlay(START_LAYER_NAME, 'Point?crs=epsg:31468', self._style_layer)

    def get_finish_layer(self):
        return get_overlay(FINISH_LAYER_NAME, 'Point?crs=epsg:31468', self._style_layer)

    def set_start(self, start):
        self.start = start
        self._refresh()

    def set_finish(self, finish):
        self.finish = finish
        self._refresh()

    def create_feature(self, position):
        feat = QgsFeature()
        point = QgsGeometry.fromPoint(QgsPoint(position[0], position[1]))
        feat.setGeometry(point)
        return feat

    def _set(self, position, layer):
        remove_all(layer)
        provider = layer.dataProvider()
        feats = []
        if self.start:
            feats.append(self.create_feature(self.start))
        if self.finish:
            feats.append(self.create_feature(self.finish))
        success, features = provider.addFeatures(feats)
        if not success:
            raise Exception("Could not add feature")
        layer.updateExtents()
        layer.dataProvider().forceReload()
        layer.triggerRepaint()
        self.iface.mapCanvas().refresh()

    def _refresh(self):
        self._set(self.start, self.get_start_layer())
        self._set(self.finish, self.get_finish_layer())

