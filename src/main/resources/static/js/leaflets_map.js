/**
 * This file contains functions and callbacks specifically for the leaflets map
 **/


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
    LEAFLETS_VARS.map.fitBounds(e.target.getBounds());
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
        this._div.innerHTML = '<h4>US Outage Events</h4>' +  (props ?
            '<b>' + props.name + ', '+GLOBALS.stateIdNameMap[props.STATE]+'</b>'
            : 'Hover over a state');
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
        fillColor: getColor(feature.properties),
        weight: 2,
        opacity: 1,
        color: 'white',
        dashArray: '3',
        fillOpacity: 0.5
    };
}

function getColor(d) {
    return "#ACD1E9";
}