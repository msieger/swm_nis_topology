
from PyQt4.QtGui import *
import os
import os.path

dir_path = os.path.dirname(os.path.realpath(__file__))


class Toolbar:

    def __init__(self, iface):
        self.iface = iface
        self.actions = []

    def add(self, name, icon, callback):
        icon_path = os.path.join(dir_path, "icon", icon)
        if not os.path.exists(icon_path):
            raise Exception("Icon " + icon_path + " not found")
        action = QAction(QIcon(icon_path), name, self.iface.mainWindow())
        action.triggered.connect(callback)
        self.iface.addToolBarIcon(action)
        self.actions.append(action)

    def remove_all(self):
        for action in self.actions:
            self.iface.removeToolBarIcon(action)
        self.actions = []
