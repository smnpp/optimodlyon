import React, { useState, useCallback } from 'react';
import Intersection from '@/app/types/intersection';
import {
    AdvancedMarker,
    InfoWindow,
    Marker,
    PinElement,
} from 'react-google-map-wrapper';

enum MarkerType {
    Warehouse = 'Warehouse',
    Pickup = 'Pickup Point',
    Delivery = 'Delivery Point',
}

const MarkerWithInfoWindow = ({
    position,
    initialContent,
    color,
    type,
    onPositionChange,
}: {
    position: google.maps.LatLngLiteral;
    initialContent: React.ReactNode;
    color?: string;
    type: MarkerType;
    onPositionChange?: (position: google.maps.LatLngLiteral) => void;
}) => {
    const [isOpen, setOpen] = useState(false);
    const [markerPosition, setMarkerPosition] = useState(position);
    const [content, setContent] = useState(initialContent);

    const handleDragEnd = (
        marker: google.maps.marker.AdvancedMarkerElement,
        event: google.maps.MapMouseEvent,
    ) => {
        if (event.latLng) {
            const newPos = {
                lat: event.latLng.lat(),
                lng: event.latLng.lng(),
            };
            setMarkerPosition(newPos);
            setContent(
                <div style={{ color: 'black' }}>
                    <h3>{type}</h3>
                    <p>
                        Location:{' '}
                        {`${newPos.lat.toPrecision(8)}, ${newPos.lng.toPrecision(8)}`}
                    </p>
                </div>,
            );
            if (onPositionChange) {
                onPositionChange(newPos);
            }
        }
    };

    return (
        <>
            <InfoWindow
                content={<div>{content}</div>}
                onCloseClick={() => setOpen(false)}
                open={isOpen}
            >
                <AdvancedMarker
                    lat={position.lat}
                    lng={position.lng}
                    onClick={() => setOpen(true)}
                    onDragEnd={handleDragEnd}
                    gmpDraggable={true}
                >
                    <PinElement
                        background={color || '#FFF'}
                        glyphColor={'#000'}
                        borderColor={'#000'}
                    />
                </AdvancedMarker>
            </InfoWindow>
        </>
    );
};

export const MapMarker = (props: { pois: Intersection[] }) => {
    return (
        <>
            {props.pois.map((poi: Intersection) => (
                <MarkerWithInfoWindow
                    key={poi.key}
                    position={poi.location}
                    type={MarkerType.Warehouse}
                    initialContent={
                        <p>This is an intersection point: {poi.key}</p>
                    }
                />
            ))}
        </>
    );
};

export const WarehouseMarker = (props: { warehouse: Intersection }) => {
    return (
        <MarkerWithInfoWindow
            position={props.warehouse.location}
            color="#FF0000"
            type={MarkerType.Warehouse}
            initialContent={
                <div style={{ color: 'black' }}>
                    <h3>Warehouse</h3>
                    <p>
                        Location:{' '}
                        {`${props.warehouse.location.lat}, ${props.warehouse.location.lng}`}
                    </p>
                </div>
            }
        />
    );
};

export const DeliveryMarker = (props: { deliveryPoints: Intersection[] }) => {
    return (
        <>
            {props.deliveryPoints.map((poi: Intersection) => (
                <MarkerWithInfoWindow
                    key={poi.key}
                    position={poi.location}
                    color="#00FF00"
                    type={MarkerType.Delivery}
                    initialContent={
                        <div style={{ color: 'black' }}>
                            <h3>Delivery Point</h3>
                            <p>
                                Location:{' '}
                                {`${poi.location.lat}, ${poi.location.lng}`}
                            </p>
                        </div>
                    }
                />
            ))}
        </>
    );
};

export const PickupMarker = (props: { pickupPoints: Intersection[] }) => {
    return (
        <>
            {props.pickupPoints.map((poi: Intersection) => (
                <MarkerWithInfoWindow
                    key={poi.key}
                    position={poi.location}
                    color="#0000FF"
                    type={MarkerType.Pickup}
                    initialContent={
                        <div style={{ color: 'black' }}>
                            <h3>Pickup Point</h3>
                            <p>
                                Location:{' '}
                                {`${poi.location.lat}, ${poi.location.lng}`}
                            </p>
                        </div>
                    }
                />
            ))}
        </>
    );
};
