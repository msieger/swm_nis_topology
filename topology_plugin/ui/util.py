from qgis.core import *
from qgis.gui import *
from PyQt4.QtGui import *


def get_overlay(overlay_name, uri, style_func):
    registry = QgsMapLayerRegistry.instance()
    layer = None
    for layer_name in registry.mapLayers():
        current_layer = registry.mapLayer(layer_name)
        if current_layer.originalName() == overlay_name:
            layer = current_layer
            break
    if layer is None:
        layer = QgsVectorLayer(uri, overlay_name, 'memory')
        if not layer.isValid():
            raise Exception('Overlay layer is not valid')
        style_func(layer)
        registry.addMapLayer(layer)
    return layer


def remove_all(layer):
    fids = []
    feat = QgsFeature()
    features = layer.getFeatures()
    while features.nextFeature(feat):
        fids.append(feat.id())
    features.close()
    layer.startEditing()
    if not layer.deleteFeatures(fids):
        raise Exception("Could not delete features")
    layer.commitChanges()
