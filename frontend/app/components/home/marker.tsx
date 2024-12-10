import Intersection from '@/app/types/intersection';
import { AdvancedMarker, Pin } from '@vis.gl/react-google-maps';

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
    return (
        <>
            <AdvancedMarker position={props.warehouse.location}>
                <Pin
                    background={'#FF0000'}
                    glyphColor={'#000'}
                    borderColor={'#000'}
                />
            </AdvancedMarker>
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
