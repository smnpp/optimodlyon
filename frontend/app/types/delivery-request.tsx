import Intersection from './intersection';

type DeliveryRequest = {
    key: string;
    pickupPoint: Intersection;
    deliveryPoint: Intersection;
    pickupDuration: number;
    deliveryDuration: number;
};

export default DeliveryRequest;
