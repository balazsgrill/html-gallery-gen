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