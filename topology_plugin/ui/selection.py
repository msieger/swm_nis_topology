import re
from flag_overlay import *
from qgis.core import *
from qgis.gui import *
import sys


class SelectedPoint:

    def __init__(self, rwo_id, schema, rwo_name, geom_name, idx, x, y):
        self.rwo_id = rwo_id
        self.schema = schema
        self.rwo_name = rwo_name
        self.geom_name = geom_name
        self.idx = idx
        self.x = x
        self.y = y


class Selection:

    def __init__(self, iface):
        self.iface = iface
        self.start_map_tool = QgsMapToolEmitPoint(iface.mapCanvas())
        self.start_map_tool.canvasClicked.connect(self._start_clicked)
        self.start_selected = None
        self.end_map_tool = QgsMapToolEmitPoint(iface.mapCanvas())
        self.end_map_tool.canvasClicked.connect(self._end_clicked)
        self.end_selected = None
        self.overlay = FlagOverlay(iface)

    def _start_clicked(self, point, button):
        self.start_selected = self._canvas_clicked(point, button)
        self.overlay.set_start((point.x(), point.y()))

    def _end_clicked(self, point, button):
        self.end_selected = self._canvas_clicked(point, button)
        self.overlay.set_finish((point.x(), point.y()))

    def _canvas_clicked(self, point, button):
        layer = self.iface.mapCanvas().currentLayer()
        extent = QgsVector(0.2, 0.2)
        features = layer.getFeatures(QgsFeatureRequest(QgsRectangle(point + extent, point - extent)))
        feat = QgsFeature()
        while features.nextFeature(feat):
            geom = feat.geometry()
            if geom is None:
                self.start_selected = None
                return
            polyline = geom.asPolyline()
            if polyline is None or len(polyline) == 0:
                self.start_selected = None
                return
            closest = 0
            dist = sys.float_info.max
            for i in range(len(polyline)):
                new_dist = polyline[i].distance(point)
                if new_dist < dist:
                    closest = i
                    dist = new_dist
            uri = QgsDataSourceURI(layer.dataProvider().dataSourceUri())
            return SelectedPoint(self._get_rwo_id(feat), uri.schema(), uri.table(),
                                          uri.geometryColumn(), closest + 1, point.x(), point.y())

    def _extract(self, compound):
        if not re.match('\{\d+,\d+,\d+\}', compound):
            raise Exception('rwo_id ' + compound + " can not be parsed")
        return compound.split(',')[2][:-1]

    def _get_rwo_id(self, f):
        return int(self._extract(f.attribute('rwo_id')))

    def start_selection(self):
        self.iface.mapCanvas().setMapTool(self.start_map_tool)

    def end_selection(self):
        self.iface.mapCanvas().setMapTool(self.end_map_tool)

    def get_selected_start(self):
        return self.start_selected

    def get_selected_end(self):
        return self.end_selected

    def get(self):
        layer = self.iface.mapCanvas().currentLayer()
        if layer is None:
            return []
        features = layer.selectedFeatures()
        return [self._get_rwo_id(f) for f in features]

    def _find_layer(self, type, field):
        canvas = self.iface.mapCanvas()
        for layer in canvas.layers():
            uri = QgsDataSourceURI(layer.dataProvider().dataSourceUri())
            if uri.table() == type and uri.geometryColumn() == field:
                return layer
        return None

    def _group(self, rwos):
        groups = {}
        for rwo in rwos:
            grp = (rwo[1], rwo[2])
            if not grp in groups:
                groups[grp] = []
            elements = groups[grp]
            elements.append(rwo)
        return groups

    def set(self, rwo_ids, layer):
        print(str(rwo_ids))
        fit = layer.getFeatures()
        feat = QgsFeature()
        while fit.nextFeature(feat):
            try:
                feat.attribute('rwo_id')
            except KeyError:
                break
            if self._get_rwo_id(feat) in rwo_ids:
                layer.select(feat.id())
            self.iface.mapCanvas().refresh()

    def set(self, rwos):
        groups = self._group(rwos)
        for group in groups:
            first = groups[group][0]
            layer = self._find_layer(first[1], first[2])
            if layer is None:
                return
            self.set([rwo[1] for rwo in groups[group]], layer)


