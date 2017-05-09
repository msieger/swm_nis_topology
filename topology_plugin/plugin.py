# -*- coding: utf-8 -*-

from ui.toolbar import *
from ui.selection import *
from service.rest_service import *
from qgis.gui import *
from ui.overlay import *

import logging

class Plugin:

    def __init__(self, iface):
        self.iface = iface
        self.toolbar = Toolbar(iface)
        self.selection = None
        self.overlay = None
        self.rest = RestService('localhost:8080')
        self.rest.error.connect(self.on_request_fail)

    def on_request_fail(self, text):
        self.iface.messageBar().pushMessage("Error", text)

    def _node_from_selection(self, sel, callback):
        self.rest.get_json('/node', sel.schema, {'rwo_name': sel.rwo_name, 'geom_name': sel.geom_name, 'x': sel.x, 'y':sel.y},
                           callback)

    def shortest_path(self):
        start = self.selection.get_selected_start()
        end = self.selection.get_selected_end()

        context = {'start_id': None, 'end_id': None}

        def route_callback(route):
            self.overlay.set_result_geometry(route['geometry'])

        def end_node_callback(end_node):

            context['end_id'] = end_node['id']
            self.rest.get_json('/route', start.schema, {'from': context['start_id'], 'to': context['end_id']}, route_callback)

        def start_node_callback(start_node):
            context['start_id'] = start_node['id']
            self._node_from_selection(end, end_node_callback)

        self._node_from_selection(start, start_node_callback)

    def blocking_nodes(self):

        sel = self.selection.get_selected_start()

        def blocked_path_callback(blocked_path):
            self.overlay.set_result_geometry(blocked_path['geometry'])

        def node_callback(node):
            self.rest.get_json('/blocked_path', sel.schema, {'node_id': node["id"]}, blocked_path_callback)

        self._node_from_selection(sel, node_callback)


    def select_start(self):
        self.selection.start_selection()

    def select_end(self):
        self.selection.end_selection()

    def initGui(self):
        self.selection = Selection(self.iface)
        self.overlay = Overlay(self.iface)
        self.toolbar.add("Blocking Nodes", "circle-filled.png", self.blocking_nodes)
        self.toolbar.add("Shortest Path", "circle-filled.png", self.shortest_path)
        self.toolbar.add("Select start", "Cross1.png", self.select_start)
        self.toolbar.add("Select end", "Cross1.png", self.select_end)

    def unload(self):
        self.toolbar.remove_all()
