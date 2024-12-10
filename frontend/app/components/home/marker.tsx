import Intersection from '@/app/types/intersection';
import {
    AdvancedMarker,
    InfoWindow,
    Pin,
    useAdvancedMarkerRef,
} from '@vis.gl/react-google-maps';
import { useCallback, useState } from 'react';

export const MapMarker = (props: { pois: Intersection[] }) => {
    return (
        <>
            {props.pois.map((poi: Intersection) => (
                <AdvancedMarker key={poi.key} position={poi.location}>
                    <Pin
                        background={'#FFFFFF'}
                        glyphColor={'#000'}
                        borderColor={'#000'}
                    />
                </AdvancedMarker>
            ))}
        </>
    );
};

export const WarehouseMarker = (props: { warehouse: Intersection }) => {
    const [markerRef, marker] = useAdvancedMarkerRef();
    const [infoWindowShown, setInfoWindowShown] = useState(false);

    const handleMarkerClick = useCallback(
        () => setInfoWindowShown((isShown) => !isShown),
        [],
    );

    const handleClose = useCallback(() => setInfoWindowShown(false), []);

    return (
        <>
            <AdvancedMarker
                ref={markerRef}
                position={props.warehouse.location}
                onClick={handleMarkerClick}
            >
                <Pin
                    background={'#FF0000'}
                    glyphColor={'#000'}
                    borderColor={'#000'}
                />
            </AdvancedMarker>

            {infoWindowShown && (
                <InfoWindow anchor={marker} onClose={handleClose}>
                    <div style={{ color: 'black' }}>
                        <h3>Warehouse</h3>
                        <p>
                            Location:{' '}
                            {`${props.warehouse.location.lat}, ${props.warehouse.location.lng}`}
                        </p>
                    </div>
                </InfoWindow>
            )}
        </>
    );
};

export const DeliveryMarker = (props: { deliveryPoints: Intersection[] }) => {
    return (
        <>
            {props.deliveryPoints.map((poi: Intersection) => (
                <AdvancedMarker key={poi.key} position={poi.location}>
                    <Pin
                        background={'#00FF00'}
                        glyphColor={'#000'}
                        borderColor={'#000'}
                    />
                </AdvancedMarker>
            ))}
        </>
    );
};

export const PickupMarker = (props: { pickupPoints: Intersection[] }) => {
    return (
        <>
            {props.pickupPoints.map((poi: Intersection) => (
                <AdvancedMarker key={poi.key} position={poi.location}>
                    <Pin
                        background={'#0000FF'}
                        glyphColor={'#000'}
                        borderColor={'#000'}
                    />
                </AdvancedMarker>
            ))}
        </>
    );
};
