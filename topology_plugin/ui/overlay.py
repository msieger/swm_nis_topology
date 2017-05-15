from qgis.core import *
from qgis.gui import *
from PyQt4.QtGui import *
from util import get_overlay

LAYER_NAME = 'NIS topology result overlay'


class Overlay:

    def __init__(self, iface):
        self.iface = iface
        self.layer = None

        self.result_geom_ids = []

    def _style_layer(self, layer):
        symbol = layer.rendererV2().symbols()[0]
        symbol.setColor(QColor.fromRgb(250, 50, 50))
        symbol.setWidth(2)

    def ensure_layer(self):
        self.layer = get_overlay(LAYER_NAME, 'Multilinestring?crs=epsg:31468', self._style_layer)

    def set_result_geometry(self, wkt_list):
        self.ensure_layer()
        if len(self.result_geom_ids) > 0:
            self.layer.startEditing()
            for id in self.result_geom_ids:
                self.layer.deleteFeature(id)
            self.layer.commitChanges()
        feats = []
        for wkt in wkt_list:
            fet = QgsFeature()
            geom = QgsGeometry.fromWkt(wkt)
            if geom is None:
                raise Exception('Result geometry could not be read')
            fet.setGeometry(geom)
            feats.append(fet)
        success, features = self.layer.dataProvider().addFeatures(feats)
        if not success or len(features) != len(feats):
            raise Exception('Unable to add result feature')
        self.result_geom_ids = [f.id() for f in features]
        self.layer.updateExtents()
        self.layer.dataProvider().forceReload()
        self.layer.triggerRepaint()
        self.iface.mapCanvas().refresh()
