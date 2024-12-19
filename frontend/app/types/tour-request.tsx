import DeliveryRequest from './delivery-request';
import Intersection from './intersection';

type TourRequest = {
    key: string;
    request: DeliveryRequest[];
    warehouse: Intersection;
};

export default TourRequest;
