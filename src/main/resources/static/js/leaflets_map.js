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

function zoomToFeature(e) {
    LEAFLETS_VARS.map.fitBounds(e.target.getBounds());
}

function addPartitionGeoJsonToMap(geoJsonResp)
{
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