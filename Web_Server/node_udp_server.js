var dgram = require("dgram");
var udp_socket = dgram.createSocket("udp4");

var android_ui_buffer = [];

udp_socket.on("message", function(content, rinfo){
	var _content = content.toString();
	console.log(_content);
	android_ui_buffer.push(content.toString());
	//send_ui_data_to_client();
});

var send_ui_data_to_client = function(){
	io.sockets.emit("message", android_ui_buffer);
};

udp_socket.bind(7330);