import DeliveryRequest from '../types/delivery-request';
import Intersection from '../types/intersection';
import Tour from '../types/tour';
import TourRequest from '../types/tour-request';

class OptimodApiService {
    private baseUrl: string;

    constructor() {
        this.baseUrl =
            process.env.NEXT_OPTIMODAPI_URL ||
            'http://localhost:10500/optimodapi';
    }

    async loadMap(map: File): Promise<Intersection[]> {
        const fileContent = await this.readFileContent(map);

        const body = {
            'file-content': fileContent,
        };

        localStorage.setItem('map-file', fileContent);

        try {
            const response = await fetch(
                `${this.baseUrl}${'/ActionServlet?action=load-map'}`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(body),
                },
            );
            if (!response.ok) {
                throw new Error(`Error: ${response.statusText}`);
            }
            const data = await response.json();
            const intersections: Intersection[] = data.map.map(
                (item: {
                    id: string;
                    location: { latitude: number; longitude: number };
                }) => {
                    const latitude = item.location.latitude;
                    const longitude = item.location.longitude;

                    const location: google.maps.LatLngLiteral = {
                        lat: latitude,
                        lng: longitude,
                    };

                    return {
                        key: item.id,
                        location: location,
                    };
                },
            );

            localStorage.setItem('map', JSON.stringify(intersections));

            return intersections;
        } catch (error) {
            console.error('Fetch error:', error);
            throw error;
        }
    }

    async loadRequest(request: File): Promise<TourRequest> {
        const fileContent = await this.readFileContent(request);

        const body = {
            'file-content': fileContent,
        };

        localStorage.setItem('request-file', fileContent);

        try {
            const response = await fetch(
                `${this.baseUrl}${'/ActionServlet?action=load-request'}`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(body),
                },
            );
            if (!response.ok) {
                throw new Error(`Error: ${response.statusText}`);
            }
            const data = await response.json();

            const map = JSON.parse(localStorage.getItem('map') || '[]');

            const deliveryRequests: DeliveryRequest[] = data[
                'delivery-requests'
            ].map(
                (item: {
                    id: string;
                    'pickup-point': string;
                    'delivery-point': string;
                    'pickup-duration': number;
                    'delivery-duration': number;
                }) => {
                    const pickupPoint = map.find(
                        (intersection: Intersection) =>
                            intersection.key === item['pickup-point'],
                    );
                    const deliveryPoint = map.find(
                        (intersection: Intersection) =>
                            intersection.key === item['delivery-point'],
                    );

                    if (!pickupPoint || !deliveryPoint) {
                        console.error(
                            'Intersection not found for delivery request, map may not be loaded',
                        );
                    }

                    return {
                        key: item.id,
                        pickupPoint: pickupPoint,
                        deliveryPoint: deliveryPoint,
                        pickupDuration: item['pickup-duration'],
                        deliveryDuration: item['delivery-duration'],
                    };
                },
            );

            const warehouse = map.find(
                (item: Intersection) => item.key === data.warehouse.id,
            );

            if (!warehouse) {
                console.error(
                    'Warehouse not found in map, map may not be loaded',
                );
            }

            const tourRequest: TourRequest = {
                key: data.id,
                request: deliveryRequests,
                warehouse: warehouse,
            };

            localStorage.setItem('request', JSON.stringify(tourRequest));

            return tourRequest;
        } catch (error) {
            console.error('Fetch error:', error);
            throw error;
        }
    }

    async computeTour(): Promise<void> {
        const mapFile = localStorage.getItem('map-file');
        const requestFile = localStorage.getItem('request-file');

        if (!mapFile || !requestFile) {
            console.error(
                'Map and request files must be loaded before computing tour',
            );
            throw new Error('Map and request files must be loaded');
        }
        const body = {
            'map-file': mapFile,
            'request-file': requestFile,
        };

        try {
            const response = await fetch(
                `${this.baseUrl}${'/ActionServlet?action=compute-tour'}`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(body),
                },
            );
            if (!response.ok) {
                throw new Error(`Error: ${response.statusText}`);
            }
            const data = await response.json();

            const Tour = {
                id: data.tour.id,
                duration: data.tour.duration,
                intersections: data.tour.intersections.map(
                    (item: {
                        id: string;
                        location: { latitude: number; longitude: number };
                    }) => {
                        const latitude = item.location.latitude;
                        const longitude = item.location.longitude;

                        const location: google.maps.LatLngLiteral = {
                            lat: latitude,
                            lng: longitude,
                        };

                        return {
                            key: item.id,
                            location: location,
                        };
                    },
                ),
            };

            console.log('Tour:', Tour);
        } catch (error) {
            console.error('Fetch error:', error);
            throw error;
        }
    }

    private readFileContent(file: File): Promise<string> {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = (event) => {
                resolve(event.target?.result as string);
            };
            reader.onerror = (error) => {
                reject(error);
            };
            reader.readAsText(file);
        });
    }
}

export default OptimodApiService;
