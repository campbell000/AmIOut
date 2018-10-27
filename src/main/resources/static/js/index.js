// GLOBALS
var STATES = 0;
var COUNTIES = 1;
var map = null;
var geoJson;
var infoPanel = L.control();

// global variable for holding leaflets controls and state. Global variables are bad, blah blah blah, but this makes it
// easier to split up the javascript files into leaflets interactivity and general page functionality.
var LEAFLETS_VARS = {
    map: map,
    geoJsonLayer: geoJson,
    infoPanel: infoPanel

}

function initmap() {
	// set up the map
	LEAFLETS_VARS.map = new L.Map('mapid');

	// create the tile layer with correct attribution
	var osmUrl='https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	var osmAttrib='Map data © <a href="https://openstreetmap.org">OpenStreetMap</a> contributors';
	var osm = new L.TileLayer(osmUrl, {minZoom: 4, maxZoom: 12, attribution: osmAttrib}).addTo(LEAFLETS_VARS.map);

	// start the map in South-East England
	LEAFLETS_VARS.map.setView([37.8, -96], 5);
	getGeoJsonCounties();
	addInfoPopup();
}

function addInfoPopup()
{
    infoPanel.onAdd = function (map) {
        this._div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
        this.update();
        return this._div;
    };

    // method that we will use to update the control based on feature properties passed
    infoPanel.update = function (props) {
        this._div.innerHTML = '<h4>US Outage Events</h4>' +  (props ?
            '<b>' + props.name + '</b>'
            : 'Hover over a state');
    };

    infoPanel.addTo(LEAFLETS_VARS.map);
}

$(document).ready(function() {
    initmap();
});


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


