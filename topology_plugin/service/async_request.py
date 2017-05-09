import urllib2
from PyQt4.QtCore import *
import threading
import sys

class AsyncRequest(QObject):

    done = pyqtSignal(int, str)
    fail = pyqtSignal(str)

    def __init__(self, url, params):
        QObject.__init__(self)
        self.url = url
        self.params = params

    def _build_params(self):
        return "&".join([p + "=" + str(self.params[p]) for p in self.params])

    def start(self):
        threading.Thread(target=self.run).start()

    def run(self):
        try:
            res = urllib2.urlopen(self.url, self._build_params())
            self.done.emit(res.getcode(), res.read())
        except urllib2.HTTPError, e:
            self.fail.emit("The server did reject the request")
        except urllib2.URLError, e:
            e = sys.exc_info()[0]
            print(str(e))
            if hasattr(e, 'reason'):
                self.fail.emit("Unable to execute request: " + str(e.reason))
            else:
                self.fail.emit("Unable to execute request")