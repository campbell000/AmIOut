/**
 * This file contains functions and callbacks specifically for the leaflets map
 **/

var outageIcon = L.icon({
    iconUrl: contextRoot+'images/outageMarker.png',
    iconSize: [45, 55],
    iconAnchor: [22, 94],
    popupAnchor: [-3, -76],
});

// Highlights a partition on the map and updates the info panel when it's hovered over.
function highlightFeature(e) {
    var layer = e.target;

    layer.setStyle({
        weight: 5,
        color: '#666',
        dashArray: '',
        fillOpacity: 0.7
    });

    if (!L.Browser.ie && !L.Browser.opera && !L.Browser.edge) {
        layer.bringToFront();
    }

    LEAFLETS_VARS.infoPanel.update(layer.feature.properties);
}

// Resets the effects of the highlightFeature() function
function resetHighlight(e) {
    LEAFLETS_VARS.geoJsonLayer.resetStyle(e.target);
    LEAFLETS_VARS.infoPanel.update();
}

// Defines events when a feature is interacted with
function onEachFeature(feature, layer) {
    layer.on({
        mouseover: highlightFeature,
        mouseout: resetHighlight,
        click: zoomToFeature
    });
}

// Zooms into a partition when clicked
function zoomToFeature(e) {
    if (LEAFLETS_VARS.aggregateQueryHasBeenRun) {
        plotIndividualEvents(e.target);
    }
    else
    {
        alert("Run a query, and then click on a partition, to see query details!");
    }
}

function plotIndividualEvents(e)
{
    var partitionID = getPartitonID(e.feature.properties);
    var partitioning = $("#partitionSelect").val();
    var query = {
        partitionID: partitionID,
        partitioning: partitioning
    };
    var url = contextRoot+"partitionQuery";

    $.post({
        url: url,
        contentType: "application/json",
        data: JSON.stringify(query),
        success: function(results){
            displayPoints(results);
            LEAFLETS_VARS.currentQueryPartition = partitionID;
            LEAFLETS_VARS.map.fitBounds(e.getBounds());
        }
    });
}

function displayPoints(results) {
    for (var dataPointIndex in results.dataPoints)
    {
        var dataPoint = results.dataPoints[dataPointIndex].dataPoint;
        var lat = dataPoint.centerPoint.lat;
        var lon = dataPoint.centerPoint.lon;
        var marker = L.marker([lat, lon], {icon: outageIcon}).addTo(LEAFLETS_VARS.map);
        marker.bindPopup(generatePopupContent(dataPoint)).openPopup();
    }
}
function generatePopupContent(dataPoint) {
    var keys = ["areaAffected", "disturbanceType", "dateTimeOcurred", "energyLossInMegaWatts", "numCustomersAffected", "restorationDateTime"];
    var content = $("<div>");
    for (var i in keys) {
        var key = keys[i];
        var value = $("<div>");
        value.html(key+": "+dataPoint[key]);
        content.append(value);
    }
    return content.html();
}

// Adds the info popup to the screen
function addInfoPopup()
{
    LEAFLETS_VARS.infoPanel.onAdd = function (map) {
        this._div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
        this.update();
        return this._div;
    };

    // method that we will use to update the control based on feature properties passed
    LEAFLETS_VARS.infoPanel.update = function (props) {
        var desc = 'Run a query to see outage events';
        if (props != null) {
            desc = "<b>";
            var nameVal = props.name ? props.name : props.NAME;
            if (props.aggregateCount)
                nameVal += " (# Events: "+props.aggregateCount+")";

            var state = GLOBALS.stateIdNameMap[props.STATE] ? GLOBALS.stateIdNameMap[props.STATE] : null;
            desc += state == null ? nameVal : nameVal+", "+state;
            desc += "</b>";
            desc += "<br/>Click on a partition to see outage details";
        }
        this._div.innerHTML = '<h4>US Outage Events</h4>' + desc;
    };

    LEAFLETS_VARS.infoPanel.addTo(LEAFLETS_VARS.map);

}

function addPartitionGeoJsonToMap(geoJsonResp)
{
    if (LEAFLETS_VARS.geoJsonLayer != null)
    {
        LEAFLETS_VARS.map.removeLayer(LEAFLETS_VARS.geoJsonLayer)
    }
	LEAFLETS_VARS.geoJsonLayer =
	    L.geoJson(geoJsonResp, {style: doPartitionOutlineStyle, onEachFeature: onEachFeature}).addTo(LEAFLETS_VARS.map);
}

function doPartitionOutlineStyle(feature) {
    return {
        fillColor: getColor(feature.properties.aggregateCount),
        weight: 2,
        opacity: 1,
        color: 'white',
        dashArray: '3',
        fillOpacity: 0.5
    };
}

function getColor(count) {
    var d =  count / LEAFLETS_VARS.aggregateTotalCount;
    return d > 0.825 ?  colorMap[0.825] :
           d > 0.75 ?  colorMap[0.75] :
           d > 0.625 ?  colorMap[0.625] :
           d > 0.5   ?  colorMap[0.5] :
           d > 0.375 ?  colorMap[0.375] :
           d > 0.25  ?  colorMap[0.25] :
           d > 0.125 ?  colorMap[0.125] :
                        colorMap[0];
}

var colorMap = {
    0.825 :  '#800026',
    0.75  : '#BD0026',
    0.625 :  '#E31A1C',
    0.5   :  '#FC4E2A',
    0.375 :  '#FD8D3C',
    0.25  :  '#FEB24C',
    0.125 :  '#FED976',
    0: '#ACD1E9'
};

function getColorForRatio(ratio)
{
    return colorMap[ratio];
}

function initLegend(map)
{
    var legend = L.control({position: 'bottomright'});
    legend.onAdd = function (map) {

        var div = L.DomUtil.create('div', 'info legend'),
            legendStrings = ["12.5%", "12.5%", "25%", "37.5%", "50%", "75%", "87.5%"],
            legendValues = [0, 0.125, 0.25, 0.375, 0.5, 0.625, 0.75, 0.875, 1],
            labels = [];

        div.innerHTML += "<b>Total Events: <span id='totalCount'>"+LEAFLETS_VARS.aggregateTotalCount+"</span></b><br/>";

        // loop through our density intervals and generate a label with a colored square for each interval
        for (var i = 0; i < legendStrings.length; i++) {
            if (legendStrings[i - 1])
            {
                div.innerHTML += '<i style="background:'+getColorForRatio(legendValues[i]) + '"></i> ' +
                                '     >&nbsp;&nbsp;'+legendStrings[i] +'<br/>';
            }
            else {
                div.innerHTML += '<i style="background:'+getColorForRatio(legendValues[i]) + '"></i> ' +
                                '     <&nbsp;&nbsp;'+legendStrings[i] +'<br/>';
            }
        }

        return div;
    };

    LEAFLETS_VARS.legend = legend;
    legend.addTo(map);
}
