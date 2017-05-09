from async_request import *
import json


class RestService(QObject):

    error = pyqtSignal(str)

    def __init__(self, host):
        QObject.__init__(self)
        self.host = host

    def get_json(self, path, network, params, cb):
        url = "http://" + self.host + "/" + network + path
        req = AsyncRequest(url, params)

        def callback(code, body):
            if code == 200:
                js = json.loads(body)
                cb(js)
            else:
                print(url + ' returned status ' + str(code))

        req.done.connect(callback)
        req.fail.connect(self.error.emit)
        req.start()
