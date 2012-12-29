(function(){
	var socket;
	var canvas;
	var context;

	var OBJECT_SELECTED = false;
	var current_mouse_position;
	var timeout = 0;
	var last_timestamp;
	var SERVER_PORT = 7596;
	
	var connect = function(){
		var server_options = {
			port: SERVER_PORT,
			rememberTransport: false,
			transports: ["websocket", "xhr-polling", "xhr-multipart", "flashsocket"]
		};

		socket = io.connect(document.URL);

		socket.on("message", function(data){
			console.log(data);
			if(data){
				if(OBJECT_SELECTED){
					moveCircle(data);
				}
			}
		});
		
		socket.on("disconnect", function(){
			console.log("The connection was terminated");

		});
	};

	/* animation for translation */
	var moveCircle = function(data){
		if(data[0].action === "CLICK"){
			return;
		}
		var touchPositions = data[0].value;

		var length = touchPositions.length;
		var count = 0;
		
		while(count < length - 1){
			var touch_point_1 = touchPositions[count];
			var touch_point_2 = touchPositions[count + 1];

			var delta_x = 2 * (touch_point_2.x - touch_point_1.x);
			var delta_y= 2 * (touch_point_2.y - touch_point_1.y);

			var timestamp_1 = touch_point_1.timestamp;
			if(timestamp_1 - last_timestamp > 200){
				timeout = 0;
			}
			var timestamp_2 = touch_point_2.timestamp;
			
			timeout += 10;
			var _move_and_draw = function(){
				return function(){
					move_and_draw(delta_x, delta_y);	
				};
			};
			last_timestamp = timestamp_2;
			setTimeout(_move_and_draw(), timeout);
			count++;
		}
	};

	var move_and_draw = function(delta_x, delta_y){
		var new_mouse_position = {};
		new_mouse_position.x = current_mouse_position.x + delta_x;
		new_mouse_position.y = current_mouse_position.y + delta_y;
		current_mouse_position = new_mouse_position;
		draw_circle(new_mouse_position, 50, {refresh:true, fill:true});
	};

	var init_canvas = function(){
		canvas = document.getElementById("demo");
		context = canvas.getContext("2d");

		draw();
		register_event_listeners();
		
	};

	var register_event_listeners = function(){
		addEvent(canvas, "click", click_handler);
	};

	var click_handler = function(event){
		OBJECT_SELECTED = !OBJECT_SELECTED ? true : false;
		if(!OBJECT_SELECTED){
			draw_circle({x: event.pageX, y: event.pageY}, 50, {refresh: true, fill:false});
			return;
		}
		var x = event.pageX;
		var y = event.pageY;
		current_mouse_position = {x: event.pageX, y: event.pageY};
		draw_circle({x: event.pageX, y: event.pageY}, 50, {refresh: true, fill:true});
	};

	var draw = function(){
		context.clearRect(0, 0, canvas.width, canvas.height);
		draw_circle({x : 400, y: 400}, 50, {refresh:true, fill:false});
	};

	var draw_circle = function(origin, radius, options){
		if(options.refresh){
			context.clearRect(0, 0, canvas.width, canvas.height);
		}
		context.beginPath();
		context.strokeStyle = "red";
		context.lineWidth = 5;
		context.arc(origin.x, origin.y, radius, 0, 2 * Math.PI, false);
		context.stroke();	
		if(options.fill){
			context.fillStyle = "yellow";
			context.fill();
		}
	};

	var handler = function(){
		connect();
		init_canvas();
	};

	var addEvent = function(obj, eventName, callback)
	{
		if(obj.attachEvent){
			obj.attachEvent("on" + eventName, callback);
		}
		else{
			obj.addEventListener(eventName, callback, false);
		}
	};

	addEvent(window, "load", handler);
})();
