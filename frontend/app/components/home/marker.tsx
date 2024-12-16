import React, { useState, useCallback } from 'react';
import Intersection from '@/app/types/intersection';
import {
    AdvancedMarker,
    InfoWindow,
    PinElement,
} from 'react-google-map-wrapper';

const MarkerWithInfoWindow = ({
    position,
    content,
    color,
}: {
    position: google.maps.LatLngLiteral;
    content: React.ReactNode;
    color?: string;
}) => {
    const [isOpen, setOpen] = useState(false);

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
                    content={<p>This is an intersection point: {poi.key}</p>}
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
            content={
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
                    content={
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
                    content={
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
