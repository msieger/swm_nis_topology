from qgis.core import *
from qgis.gui import *
from PyQt4.QtGui import *

LAYER_NAME = 'NIS topology result overlay'


class Overlay:

    def __init__(self, iface):
        self.iface = iface
        self.layer = None

        self.result_geom_id = None

    def _style_layer(self, layer):
        symbol = layer.rendererV2().symbols()[0]
        symbol.setColor(QColor.fromRgb(250, 50, 50))
        symbol.setWidth(2)

    def ensure_layer(self):
        registry = QgsMapLayerRegistry.instance()
        for layer_name in registry.mapLayers():
            layer = registry.mapLayer(layer_name)
            if layer.originalName() == LAYER_NAME:
                self.layer = layer
                break
        if self.layer is None:
            self.layer = QgsVectorLayer('Multilinestring?crs=epsg:31468', LAYER_NAME, 'memory')
            if not self.layer.isValid():
                raise Exception('Overlay layer is not valid')
            self._style_layer(self.layer)
            registry.addMapLayer(self.layer)

    def set_result_geometry(self, wkt):
        self.ensure_layer()
        if self.result_geom_id is not None:
            self.layer.startEditing()
            self.layer.deleteFeature(self.result_geom_id)
            self.layer.commitChanges()
        fet = QgsFeature()
        geom = QgsGeometry.fromWkt(wkt)
        if geom is None:
            raise Exception('Result geometry could not be read')
        fet.setGeometry(geom)
        success, features = self.layer.dataProvider().addFeatures([fet])
        if not success or len(features) != 1:
            raise Exception('Unable to add result feature')
        self.result_geom_id = features[0].id()
        self.layer.updateExtents()
        self.layer.dataProvider().forceReload()
        self.layer.triggerRepaint()
        self.iface.mapCanvas().refresh()
