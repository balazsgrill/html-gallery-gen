var selection=-1;

function showImage(index){
	index = (index + images.length) % images.length;
	
	var mimg = document.getElementById("modalImg");
	mimg.src = images[index].image;
	var el = document.getElementById("modal");
	el.style.display = "block";
	selection = index;
}

function imgClick(index){
	showImage(index);
	return false;
}

function goImage(){
	if (selection != -1){
		window.location.href=images[selection].origin;
	}
}

function handleKey(event){
	switch(event.keyCode){
	case 37: // Left arrow
		if (selection != -1){
			showImage(selection - 1);
		}
		break;
	case 39: // Right arrow
		if (selection != -1){
			showImage(selection + 1);
		}
		break;
	case 32: // Space
		if (selection != -1){
			showImage(selection + 1);
		} else {
			showImage(0);
		}
		break;
	case 13: // Enter
		if (selection == -1){
			showImage(0);
		}
		break;
	case 8: // Backspace
		if (selection != -1){
			showImage(selection - 1);
		}
		break;
	case 27: // Esc
		if (selection != -1){
			modalClick();
		}
		break;
	}
}

function modalClick(){
	var el = document.getElementById("modal");
	el.style.display = "none";
	selection = -1;
}

function next(e){
	if (selection != -1){
		showImage(selection + 1);
	}
	e.stopPropagation();
}

function prev(e){
	if (selection != -1){
		showImage(selection - 1);
	}
	e.stopPropagation();
}