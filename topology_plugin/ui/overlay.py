from qgis.core import *
from qgis.gui import *
from PyQt4.QtGui import *
from util import *

LAYER_NAME = 'NIS topology result overlay'


class Overlay:

    def __init__(self, iface):
        self.iface = iface

    def _style_layer(self, layer):
        symbol = layer.rendererV2().symbols()[0]
        symbol.setColor(QColor.fromRgb(250, 50, 50))
        symbol.setWidth(2)

    def set_result_geometry(self, wkt_list):
        layer = get_overlay(LAYER_NAME, 'Multilinestring?crs=epsg:31468', self._style_layer)
        remove_all(layer)
        feats = []
        for wkt in wkt_list:
            fet = QgsFeature()
            geom = QgsGeometry.fromWkt(wkt)
            if geom is None:
                raise Exception('Result geometry could not be read')
            fet.setGeometry(geom)
            feats.append(fet)
        success, features = layer.dataProvider().addFeatures(feats)
        if not success or len(features) != len(feats):
            raise Exception('Unable to add result feature')
        layer.updateExtents()
        layer.dataProvider().forceReload()
        layer.triggerRepaint()
        self.iface.mapCanvas().refresh()
