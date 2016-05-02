window.JSBridge||(function(){if(navigator.userAgent.indexOf(' Client/')<0){return;}var messenger=window.__jsBridgeConsole__||window.console;var log=messenger.log;var postMessage=function(msg){log.call(messenger,"h5container.message: "+msg);};var callbackPoll={};window.JSBridge={call:function(func,param,callback){if('string'!==typeof func){return;}if('function'===typeof param){callback=param;param=null;}else if(typeof param!=='object'){param=null;}var clientId=''+new Date().getTime()+(Math.random());if('function'===typeof callback){callbackPoll[clientId]=callback;}var invokeMsg=JSON.stringify({func:func,param:param,msgType:'call',clientId:clientId});postMessage(invokeMsg);},callback:function(clientId,param){var invokeMsg=JSON.stringify({clientId:clientId,param:param});postMessage(invokeMsg);},trigger:function(name,param,clientId){console.log('bridge.trigger '+name);if(name){var evt=document.createEvent('Events');evt.initEvent(name,false,true);if(typeof param==='object'){for(var k in param){evt[k]=param[k];}}evt.clientId=clientId;var prevent=!document.dispatchEvent(evt);if(clientId&&name==='back'){JSBridge.callback(clientId,{prevent:prevent});}}},_invokeJS:function(resp){console.log('bridge._invokeJS: '+resp);resp=JSON.parse(resp);if(resp.msgType==='callback'){var func=callbackPoll[resp.clientId];if(!(typeof resp.keepCallback=='boolean'&&resp.keepCallback)){delete callbackPoll[resp.clientId];}if('function'===typeof func){setTimeout(function(){func(resp.param);},1);}}else if(resp.msgType==='call'){resp.func&&this.trigger(resp.func,resp.param,resp.clientId);}}};var readyEvent=document.createEvent('Events');readyEvent.initEvent('JSBridgeReady',false,false);var docAddEventListener=document.addEventListener;document.addEventListener=function(name,func){if(name===readyEvent.type){setTimeout(function(){func(readyEvent);},1);}else{docAddEventListener.apply(document,arguments);}};JSBridge.startupParams='{startupParams}';document.dispatchEvent(readyEvent);})();