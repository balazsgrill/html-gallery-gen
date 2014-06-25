<!DOCTYPE html>
<html><head>
	<link rel="stylesheet" type="text/css" href="{{gallery.css}}">
	<script type="text/javascript">
		var images = {{images-data}};
	</script>
	<script type="text/javascript" src="{{gallery.js}}" ></script>
	<meta charset="UTF-8" ></meta>
	<title>{{name}}</title>
	<link rel="shortcut icon" type="image/png" href="{{favicon}}"/>
</head><body onkeydown="handleKey(event);">

{{image-list}}
<div id="modal" onClick="modalClick(); return false;" class="unselectable">
	<a id="prev" onClick="prev(event); return false;" class="unselectable"></a>
	<img id="modalImg" onClick="goImage()" class="unselectable"/>
	<a id="next" onClick="next(event); return false;" class="unselectable" ></a>
</div>
</body></html>