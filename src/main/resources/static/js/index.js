// GLOBALS
var STATES = 0;
var COUNTIES = 1;
var map = null;
var geoJson;
var infoPanel = L.control();
var collapseMenu = true;

// global variable for holding leaflets controls and state. Global variables are bad, blah blah blah, but this makes it
// easier to split up the javascript files into leaflets interactivity and general page functionality.
var LEAFLETS_VARS = {
    map: map,
    geoJsonLayer: geoJson,
    infoPanel: infoPanel

}

function initmap() {
	// set up the map
	LEAFLETS_VARS.map = new L.Map('mapid', {zoomControl: false});
	L.control.zoom({position:'bottomright'}).addTo(LEAFLETS_VARS.map);

	// create the tile layer with correct attribution
	var osmUrl='https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	var osmAttrib='<a href="https://sites.google.com/vt.edu/amiout">Am I Out?</a>';
	var osm = new L.TileLayer(osmUrl, {minZoom: 4, maxZoom: 12, attribution: osmAttrib}).addTo(LEAFLETS_VARS.map);

	// start the map in good old AMERICA USA
	LEAFLETS_VARS.map.setView([37.8, -96], 5);
	getGeoJsonCounties();
	addInfoPopup();

	// Remove leaflets attribution (sorry leaflets team);
	$('.leaflet-control-attribution').children()[0].remove();
	var t = $('.leaflet-control-attribution');
	t.html(t.html().substring(2))

}

$(document).ready(function() {
    initmap();
    $(function () {
      $('[data-toggle="tooltip"]').tooltip()
    });
    $("#menuPopButton").click(function(){
        toggleMenu();
    })
});

function toggleMenu()
{
    if (collapseMenu) {
        collapseMenu = false;
        $("#menu").animate({left: "-350px"}, 100);
        $("div.triangle").css({"border-left": "12px solid silver", "border-right": "0px"});
    }
    else
    {
        collapseMenu = true;
        $("#menu").animate({left: "0px"}, 100);
        $("div.triangle").css({"border-right": "12px solid silver", "border-left": "0px"});
    }
}


function getGeoJson(partitionType)
{
    // Derive the filename
    var fileName;
    switch (partitionType) {
        case STATES:
            fileName = "states_small.json";
            break;
        case COUNTIES:
            fileName = "counties.json";
            break;
    }

    // Make the ajax call to get the geojson
    var url = contextRoot+"geojson/"+fileName;
    $.ajax({
        url: url,
        success: function(results){
            addPartitionGeoJsonToMap(results);
        }
    });
}

function getGeoJsonStates()
{
    getGeoJson(STATES);
}


function getGeoJsonCounties()
{
    getGeoJson(COUNTIES);
}


