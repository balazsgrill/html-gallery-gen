<!DOCTYPE html>
<html><head>
	<link rel="stylesheet" type="text/css" href="{{gallery.css}}">
	<script type="text/javascript">
		var images = {{images-data}};
	</script>
	<script type="text/javascript" src="{{gallery.js}}" ></script>
</head><body>

{{image-list}}
<div id="modal" onClick="modalClick(); return false;">
	<a id="prev" onClick="prev(event); return false;" ></a>
	<img id="modalImg" onClick="goImage()"/>
	<a id="next" onClick="next(event); return false;" ></a>
</div>
</body></html>