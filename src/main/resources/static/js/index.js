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
    weatherMarkers: [],
    infoPanel: infoPanel,
    currentQueryPartition: -1,
    aggregateQueryHasBeenRun: false,
    timeLapseResults: [],
    aggregateTotalCount: 0
};

function initDefaultValues() {
    $("#regions").val("regions");
    $("#showOutageCheck").prop("checked", "");
    $("#showTwitterCheck").prop("checked", "");
    $("#showWeatherCheck").prop("checked", "");
    $("#timeLapseMode").prop("checked", "");
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

    $('#intervalLength').on('input change', function () {
        $('#sliderValue').text($(this).val());
    });

    $("#timeLapseMode").on('change', function(){
        var checked = $(this).prop("checked");
        if (checked) {
            $("#timeLapseContainer").show();
        }
        else {
            $("#timeLapseContainer").hide();
        }
    });

    initDefaultValues();
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
    query.showTweets = $("#showOutageCheck").prop("checked");
    query.showOutages =  $("#showTwitterCheck").prop("checked");
    query.showWeather = $("#showWeatherCheck").prop("checked");
    query.timeLapse = $("#timeLapseMode").prop("checked");
    query.timeLapseIntervalLengthSeconds = $("#intervalSpeed").val();
    query.numSteps = $("#intervalLength").val();

    var url = !query.timeLapse ? contextRoot+"aggregateQuery/" : contextRoot+"timeLapse/";

    $.post({
        url: url,
        contentType: "application/json",
        data: JSON.stringify(query),
        success: function(results){
            if (!query.timeLapse)
            {
                handleAggregateResponse(results);
                LEAFLETS_VARS.aggregateQueryHasBeenRun = true;
            }
            else
            {
                handleTimeLapseResponse(results);
                LEAFLETS_VARS.aggregateQueryHasBeenRun = true;
            }
        }
    });
}

function handleTimeLapseResponse(results)
{
    // Store the results, and then proceed to display each step.
    LEAFLETS_VARS.timeLapseResults = results;

    renderAllTimelapseSteps(results.timeLapseSteps);
}

function renderAllTimelapseSteps(timeLapseResults)
{
    // Cleat any existing buttons in this container
    $("#timeLapseButtonContainer").empty();
    // Add a "play all" button
    var button = $("<button>");
    button.addClass("btn");
    button.addClass("btn-secondary");
    button.html("Play All Steps");
    $("#timeLapseButtonContainer").append(button);
    button.click(function(){
       renderAllTimelapseSteps(timeLapseResults);
    });

    for (var i=0; i <  timeLapseResults.length; i++)
    {
        var timeLapseResult = timeLapseResults[i];
        doTimeLapseDisplay(timeLapseResult, i);
    }
}

function doTimeLapseDisplay(timeLapseResult, i)
{
    // Build the time lapse button container
    var button = $("<button>");
    button.addClass("btn");
    button.addClass("btn-secondary");
    button.addClass("timeLapseButton");
    button.html(" "+i+" ");
    button.click(function(){
        $(".timeLapseButton").removeClass("timeLapseActive");
       handleAggregateResponse(timeLapseResult);
    });
    $("#timeLapseButtonContainer").append(button);

    var timeToWait = ($("#intervalSpeed").val() * 1000) * i;
    setTimeout(function(){
        handleAggregateResponse(timeLapseResult);
        $(".timeLapseButton").removeClass("timeLapseActive");
        var currentButton = $(".timeLapseButton:nth-child("+(i+2)+")");
        currentButton.addClass('timeLapseActive');
    }, timeToWait);
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

    if (response.climateSamples)
    {
        for (var i in LEAFLETS_VARS.weatherMarkers)
        {
            LEAFLETS_VARS.map.removeLayer(LEAFLETS_VARS.weatherMarkers[i]);
        }

        for (var i in response.climateSamples) {
            var climateSample = response.climateSamples[i];
            var icon = createWeatherMarker(climateSample);
            var marker = L.marker([climateSample.lat, climateSample.lon], {icon: icon}).addTo(LEAFLETS_VARS.map);
            LEAFLETS_VARS.weatherMarkers.push(marker);
        }
    }
}

function createWeatherMarker(dataSample)
{
    var rain = Math.floor(dataSample.precipitationInMM);
    var temp = Math.floor(dataSample.avgTempCelsium);
    var htmlElement = $("<div>");
    htmlElement.addClass("weatherMarker");
    var rainEl = $("<div>");
    rainEl.html(temp+" <i class='fas fa-tint'></i>");
    rainEl.addClass("weatherMarkerRow");
    var tempEl = $("<div>");
    tempEl.html(rain+" <i class='fas fa-thermometer-half'></i>");
    tempEl.addClass("weatherMarkerRow");
    htmlElement.append(tempEl);
    htmlElement.append(rainEl);

    var marker = L.divIcon({
        iconSize: new L.Point(45,55),
        html: htmlElement.html()
    });
    return marker;
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