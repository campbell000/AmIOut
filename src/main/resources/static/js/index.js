// GLOBALS
var STATES = 0;
var COUNTIES = 1;
var REGIONS = 3
var map = null;
var rawGeoJson = null;
var geoJson;
var infoPanel = L.control();
var collapseMenu = true;

Date.prototype.toDateInputValue = (function() {
    var local = new Date(this);
    local.setMinutes(this.getMinutes() - this.getTimezoneOffset());
    return local.toJSON().slice(0,10);
});

// global variable for holding leaflets controls and state. Global variables are bad, blah blah blah, but this makes it
// easier to split up the javascript files into leaflets interactivity and general page functionality.
var LEAFLETS_VARS = {
    map: map,
    rawGeoJson: rawGeoJson,
    geoJsonLayer: geoJson,
    infoPanel: infoPanel,
    currentQueryPartition: -1,
    aggregateQueryHasBeenRun: false,
    aggregateTotalCount: 0
};

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
	getGeoJsonStates();
	addInfoPopup();

	// Remove leaflets attribution (sorry leaflets team);
	$('.leaflet-control-attribution').children()[0].remove();
	var t = $('.leaflet-control-attribution');
	t.html(t.html().substring(2))

	initDatePickers();

    initLegend(LEAFLETS_VARS.map);
}

function initDatePickers()
{
    var today = new Date();
    var twoWeeksAgo = new Date();
    twoWeeksAgo.setDate(twoWeeksAgo.getDate() - 14);
    $("#queryStartDate").val(twoWeeksAgo.toDateInputValue());
    $("#queryEndDate").val(today.toDateInputValue());
}

$(document).ready(function() {
    initmap();
    $(function () {
      $('[data-toggle="tooltip"]').tooltip()
    });
    $("#menuPopButton").click(function(){
        toggleMenu();
    })

    // Init listener for select box
    $("#partitionSelect").on('change', function(){
        var selectOption = this.value;
        if (selectOption == "states")
            getGeoJsonStates();
        else if (selectOption == "counties")
            getGeoJsonCounties();
        else if (selectOption == "regions")
            getGeoJsonRegions();
    });

    $( document ).ajaxSend(function() {
       $("#snackbar").show();
    });
    $( document ).ajaxComplete(function() {
       $("#snackbar").hide();
    });
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
        case REGIONS:
            fileName = "regions.json";
            break;
    }

    // Make the ajax call to get the geojson
    var url = contextRoot+"geojson/"+fileName;
    $.ajax({
        url: url,
        success: function(results){
            LEAFLETS_VARS.rawGeoJson = results;
            addPartitionGeoJsonToMap(results);
            LEAFLETS_VARS.currentQueryPartition = -1;
            LEAFLETS_VARS.aggregateQueryHasBeenRun = false;
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

function getGeoJsonRegions() {
    getGeoJson(REGIONS);
}

function submitQuery() {
    var query = {};
    query.startDate = $("#queryStartDate").val();
    query.endDate = $("#queryEndDate").val();
    query.partitioning = $("#partitionSelect").val();
    query.showTweets = $("#showOutageCheck").val();
    query.showOutages =  $("#showTwitterCheck").val();
    var url = contextRoot+"aggregateQuery/";

    $.post({
        url: url,
        contentType: "application/json",
        data: JSON.stringify(query),
        success: function(results){
            handleAggregateResponse(results);
            LEAFLETS_VARS.aggregateQueryHasBeenRun = true;
        }
    });
}

function handleAggregateResponse(response)
{
    // get total, update colors based on ratios of total (5/5, 4/5, 3/5, etc)
    var total = 0;
    for (key in response.partitionIdToCountMap)
    {
        total += response.partitionIdToCountMap[key];
    }
    LEAFLETS_VARS.aggregateTotalCount = total;
    $("#totalCount").html(total);

    for (var key in LEAFLETS_VARS.geoJsonLayer._layers)
    {
        var layer = LEAFLETS_VARS.geoJsonLayer._layers[key];
        var layerProps = layer.feature.properties;
        var id = getPartitonID(layerProps);
        layerProps.aggregateCount = response.partitionIdToCountMap[id];
        layer.setStyle(doPartitionOutlineStyle(layer.feature));
    }
}

function getPartitonID(props)
{
    if (props.id)
        return props.id;
    else if (props.GEO_ID)
        return props.GEO_ID;
    else if (props.GEOID)
        return props.GEOID;
}