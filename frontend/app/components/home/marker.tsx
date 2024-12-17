import React, { useState } from 'react';
import Intersection from '@/app/types/intersection';
import {
    AdvancedMarker,
    InfoWindow,
    PinElement,
} from 'react-google-map-wrapper';

export enum MarkerType {
    Warehouse = 'Warehouse',
    Pickup = 'Pickup Point',
    Delivery = 'Delivery Point',
}

const MarkerWithInfoWindow = ({
    position,
    initialContent,
    color,
    handleDragEnd,
}: {
    position: google.maps.LatLngLiteral;
    initialContent: React.ReactNode;
    color?: string;
    handleDragEnd?: (
        marker: google.maps.marker.AdvancedMarkerElement,
        event: google.maps.MapMouseEvent,
        setContent: (content: React.ReactNode) => void,
    ) => void;
}) => {
    const [isOpen, setOpen] = useState(false);
    const [content, setContent] = useState(initialContent);

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
                    onDragEnd={(marker, event) =>
                        handleDragEnd &&
                        handleDragEnd(marker, event, setContent)
                    }
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
                    initialContent={
                        <p>This is an intersection point: {poi.key}</p>
                    }
                />
            ))}
        </>
    );
};

export const WarehouseMarker = (props: {
    warehouse: Intersection;
    handleDragEnd: any;
}) => {
    return (
        <MarkerWithInfoWindow
            position={props.warehouse.location}
            color="#FF0000"
            initialContent={
                <div style={{ color: 'black' }}>
                    <h3>Warehouse</h3>
                    <p>
                        Location:{' '}
                        {`${props.warehouse.location.lat}, ${props.warehouse.location.lng}`}
                    </p>
                </div>
            }
            handleDragEnd={props.handleDragEnd}
        />
    );
};

export const DeliveryMarker = (props: {
    index: number;
    deliveryPoint: Intersection;
    handleDragEnd: any;
}) => {
    return (
        <MarkerWithInfoWindow
            position={props.deliveryPoint.location}
            color="#00FF00"
            initialContent={
                <div style={{ color: 'black' }}>
                    <h3>Delivery Point</h3>
                    <p>Index: {props.index}</p> {/* Affiche l'index commun */}
                    {/* Affiche l'ID du courier */}
                    <p>
                        Location:{' '}
                        {`${props.deliveryPoint.location.lat}, ${props.deliveryPoint.location.lng}`}
                    </p>
                </div>
            }
            handleDragEnd={props.handleDragEnd}
        />
    );
};

export const PickupMarker = (props: {
    index: number;
    pickupPoint: Intersection;
    handleDragEnd: any;
}) => {
    return (
        <MarkerWithInfoWindow
            position={props.pickupPoint.location}
            color="#0000FF"
            initialContent={
                <div style={{ color: 'black' }}>
                    <h3>Pickup Point</h3>
                    <p>Index: {props.index}</p>
                    <p>
                        Location:{' '}
                        {`${props.pickupPoint.location.lat}, ${props.pickupPoint.location.lng}`}
                    </p>
                </div>
            }
            handleDragEnd={props.handleDragEnd}
        />
    );
};
