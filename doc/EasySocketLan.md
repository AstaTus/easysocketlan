#EasySocketLan

easySocketLan 是一个Android 库，实现局域网中android设备之间通过socket进行连接，互相发送和接受消息，封装了服务端、客户端和消息协议。

## 流程





## 客户端

```
companion object {
        private val CLIENT_NAME: String = "alien"
        private val LAN_SEARCH_PORT: Int = 9000
        
        private val CMSG_MESSAGE_CODE: Int = 100
        private val SMSG_MESSAGE_CODE: Int = 200
    }
    

lanClient = LanClient(CLIENT_NAME, LAN_SEARCH_PORT, object :ILanClientListener{

    override fun onSearchStart() {
    }

    override fun onSearchSuccess() {
    }

    override fun onSearchError(error: String?) {
    }

    override fun onConnectStart() {
    }

    override fun onConnected(id: String) {
    	val message = "Hello World"
    	val json = gson.toJson(message, String::class.java)
         lanClient.send(CMSG_MESSAGE_CODE, json)
    }

    override fun onConnectError(error: String?) {
    }

    override fun onReadStart(id: String?) {
    }

    override fun onWriteStart(id: String?) {
    }

    override fun onRead(id: String?) {
    }

    override fun onWrite(id: String?) {
    }

    override fun onDisconnect(id: String?, error: String?) {
    }
})

lanClient.addHandler(
	object : PacketHandler<String>(SMSG_MESSAGE_CODE, String::class.java){
            override fun parserError(id: String?, name: String?, error: String?) {
            }

            override fun handle(id: String, name: String, message: String?) {
            
                Toast.makeText(this@MainActivity, 
                                "handle:" + error, 
                                Toast.LENGTH_SHORT).show()
            }
        })
```

## 服务器

```
companion object {
        private val LAN_SERVER_SEARCH_PORT: Int = 9000
        private val LAN_SERVER_SOCKET_PORT: Int = 10001
        
        private val CMSG_MESSAGE_CODE: Int = 100
        private val SMSG_MESSAGE_CODE: Int = 200
    }

var lanServer: LanServer =
        LanServer(LAN_SERVER_SEARCH_PORT, LAN_SERVER_SOCKET_PORT, object :ILanServerListener{

            override fun onSearchStart() {
            }

            override fun onSearching() {
            }

            override fun onSearchEnd() {
            }

            override fun onSearchError(p0: String?) {
            }

            override fun onConnectStart() {
            }

            override fun onConnectError(p0: String?) {
            }

            override fun onConnect(p0: String?, p1: Int) {
            }

            override fun onVerification(device: ClientDeviceEntity, isSuccess: Boolean) {
            
            	if(isSuccess){
            		val message = "Hello Client"
            		val json = mGson.toJson(messsage, String::class.java)
            		lanServer.send(device.id, SMSG_MESSAGE_CODE, json)
            	}
            
            }

            override fun onWriteStart(p0: String?) {
            }

            override fun onWrite(p0: String?) {
            }

            override fun onReadStart(p0: String?) {
            }

            override fun onRead(p0: String?) {
            }

            override fun onDisconnect(id: String, error: String?) {
            }
        })
        
lanServer.addHandler(
	object :PacketHandler<String>(CmsgOpCode.CMSG_MESSAGE_CODE, String::class.java){
            override fun parserError(id: String?, name: String?, message: String?) {

            }

            override fun handle(id: String, name: String, message: String?) {
                Toast.makeText(this@MainActivity, 
                                "client: id=" + id + ' ' + name + " message:" + message, 
                                Toast.LENGTH_SHORT).show()
            }
        })
        
lanServer.connect()

//可以多次调用，和connect的监听不冲突
lanServer.search()
```