window.H5Performance || (function() {
    if (navigator.userAgent.indexOf(' Client/') < 0) {
        return;
    }
    var messenger = window.__JSBridgeConsole__ || window.console;
    var log = messenger.log;
    var postMessage = function(msg) {
        log.call(messenger, "h5container.message: " + msg);
    };
    var sendMessageQueue = [];
    var monitorKernel = {
        init: function() {
            this.init = null;
            this.monitorDOMReady();
            this.monitorPageLoad();
            this.monitorJSErrors();
            this.monitorDNSTime();
            this.monitorCacheRate();
        },
        monitorDOMReady: function() {
            var t = this;
            if (document.readyState === 'complete') {
                sendMessageQueue.push({
                    name: 'domReady',
                    value: new Date().getTime(),
                    extra: 'completed'
                });
                t.sendSignal();
            } else {
                document.addEventListener("DOMContentLoaded",
                function(event) {
                    sendMessageQueue.push({
                        name: 'domReady',
                        value: event.timeStamp ? new Date(event.timeStamp).getTime() : new Date().getTime(),
                        extra: 'complete'
                    });
                    t.sendSignal();
                },
                true);
            }
        },
        monitorPageLoad: function() {
            var t = this;
            window.addEventListener("load",
            function(event) {
                sendMessageQueue.push({
                    name: 'pageLoad',
                    value: event.timeStamp ? new Date(event.timeStamp).getTime() : new Date().getTime(),
                    extra: 'load'
                });
                t.sendSignal();
            },
            true);
        },
        monitorJSErrors: function() {
            var t = this;
            window.addEventListener("error",
            function(event) {
                sendMessageQueue.push({
                    name: 'jsErrors',
                    value: event.message,
                    filename: event.filename,
                    lineno: event.lineno
                });
                t.sendSignal();
            },
            true);
        },
        monitorDNSTime: function() {
            var t = this;
            window.addEventListener("load",
            function(event) {
                if (window.performance && window.performance.timing) {
                    sendMessageQueue.push({
                        name: 'dns',
                        value: window.performance.timing.domainLookupEnd - window.performance.timing.domainLookupStart,
                        extra: 'support'
                    });
                } else {
                    sendMessageQueue.push({
                        name: 'dns',
                        value: '',
                        extra: 'notsupport'
                    });
                }
                t.sendSignal();
            },
            true);
        },
        monitorCacheRate: function() {
            var t = this,
            result = {
                name: 'cacheRate'
            },
            resourceArr;
            window.addEventListener("load",
            function(event) {
                if (window.performance && (resourceArr = window.performance.getEntriesByType("resource"))) {
                    if (resourceArr.length > 0) {
                        var cacheCount = 0;
                        for (var i = 0; i < resourceArr.length; i++) {
                            if (resourceArr[i].duration === 0) {
                                cacheCount++;
                            }
                        }
                        result.value = (cacheCount / resourceArr.length).toFixed(4);
                    } else {
                        result.value = 0.0000;
                    }
                    result.extra = 'support';
                } else {
                    result.value = '';
                    result.extra = 'notsupport';
                }
                sendMessageQueue.push(result);
                t.sendSignal();
            },
            true);
        },
        sendSignal: function() {
            var clientId = '' + new Date().getTime() + (Math.random());
            var invokeMsg = JSON.stringify({
                func: 'monitorH5Performance',
                param: {
                    data: sendMessageQueue
                },
                msgType: 'call',
                clientId: clientId
            });
            postMessage(invokeMsg);
        }
    }
    monitorKernel.init();
    window.H5Performance = {
        version: '0.0.1'
    };
})();