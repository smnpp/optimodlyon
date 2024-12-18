import TourRequest from './tour-request';
import Tour from './tour';

type Courier = {
    id: string; // Correspond à Long en Java
    isAvailable: boolean; // Correspond à Boolean en Java
    tourRequest: TourRequest;
    tour: Tour;
};

export default Courier;