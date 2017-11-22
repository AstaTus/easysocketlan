#EasySocketLan

EasySocketLan is Android library, this library used for connect the android devices in the LAN through the sockets, send and receive messages to each other, encapsulating the server, client, and message protocols.



## Download

add jitpack repository address

```
 allprojects {
    repositories {
    jcenter()
    maven {
        url 'https://jitpack.io' 
    }
 }
```

add gradle dependency

```
dependencies {
  compile 'com.github.AstaTus:easysocketlan:v0.7.0'
}
```



## Connect Flow

![https://raw.githubusercontent.com/AstaTus/easysocketlan/master/doc/CONNECT%20FLOW.jpg](https://raw.githubusercontent.com/AstaTus/easysocketlan/master/doc/CONNECT%20FLOW.jpg)



## Useage

### client

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



### Server

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

//this funcation can call more than once, and doesn't conflict the connect thread
lanServer.search()
```