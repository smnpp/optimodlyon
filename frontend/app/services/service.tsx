import { request } from 'http';
import DeliveryRequest from '../types/delivery-request';
import Intersection from '../types/intersection';
import Tour from '../types/tour';
import Courier from '../types/courier';

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
    async loadTour(request: File): Promise<string> {
        const fileContent = await this.readFileContent(request);

        const body = {
            'file-content': fileContent,
        };

        try {
            const response = await fetch(
                `${this.baseUrl}${'/ActionServlet?action=restore-tour'}`,

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

            const data = await response;

            return data.text();
        } catch (error) {
            console.error('Fetch error:', error);
            throw error;
        }
    }

    async computeTours(
        numCouriers: number,
        request: TourRequest,
    ): Promise<Record<string, Courier>> {
        const mapFile = localStorage.getItem('map-file');

        if (!mapFile || !request) {
            console.error(
                'Map and request files must be loaded before computing multiple tours',
            );
            throw new Error('Map and request files must be loaded');
        }

        const body = {
            'map-file': mapFile,
            request: request,
            'num-couriers': numCouriers,
        };

        try {
            const response = await fetch(
                `${this.baseUrl}${'/ActionServlet?action=compute-multiple-tours'}`,
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

            // Charger la carte depuis le localStorage
            const map = JSON.parse(localStorage.getItem('map') || '[]');

            const couriers: Record<string, Courier> = Object.fromEntries(
                Object.entries(data.couriers).map(
                    ([courierId, courierData]: any) => {
                        // Trouver l'entrepôt dans la carte
                        const warehouse = map.find(
                            (item: Intersection) =>
                                item.key ===
                                courierData.tourRequest.warehouse.id,
                        );

                        if (!warehouse) {
                            console.error('Warehouse not found in map');
                        }

                        return [
                            courierId,
                            {
                                id: courierId,
                                isAvailable: courierData.isAvailable,

                                // Création du TourRequest
                                tourRequest: {
                                    key: courierData.tourRequest.id,
                                    warehouse: warehouse,
                                    request:
                                        courierData.tourRequest.deliveryRequests.map(
                                            (deliveryRequest: any) => {
                                                // Points de pickup et de livraison
                                                const pickupPoint = map.find(
                                                    (
                                                        intersection: Intersection,
                                                    ) =>
                                                        intersection.key ===
                                                        deliveryRequest[
                                                            'pickup-point'
                                                        ],
                                                );
                                                const deliveryPoint = map.find(
                                                    (
                                                        intersection: Intersection,
                                                    ) =>
                                                        intersection.key ===
                                                        deliveryRequest[
                                                            'delivery-point'
                                                        ],
                                                );

                                                if (
                                                    !pickupPoint ||
                                                    !deliveryPoint
                                                ) {
                                                    console.error(
                                                        'Intersection not found for delivery request, map may not be loaded',
                                                    );
                                                }

                                                return {
                                                    key: deliveryRequest.id,
                                                    pickupPoint: pickupPoint,
                                                    deliveryPoint:
                                                        deliveryPoint,
                                                    pickupDuration:
                                                        deliveryRequest[
                                                            'pickup-duration'
                                                        ],
                                                    deliveryDuration:
                                                        deliveryRequest[
                                                            'delivery-duration'
                                                        ],
                                                };
                                            },
                                        ),
                                },

                                // Création du plan de livraison (tour)
                                tour: {
                                    id: courierData.tour.id,
                                    duration: courierData.tour.duration,
                                    intersections:
                                        courierData.tour.intersections.map(
                                            (item: {
                                                id: string;
                                                location: {
                                                    latitude: number;
                                                    longitude: number;
                                                };
                                            }) => {
                                                const location: google.maps.LatLngLiteral =
                                                    {
                                                        lat: item.location
                                                            .latitude,
                                                        lng: item.location
                                                            .longitude,
                                                    };

                                                return {
                                                    key: item.id,
                                                    location: location,
                                                };
                                            },
                                        ),
                                },
                            },
                        ];
                    },
                ),
            );

            return couriers;
        } catch (error) {
            console.error('Fetch error:', error);
            throw error;
        }
    }

    async saveTours(tours: Tour[]): Promise<void> {
        if (!Array.isArray(tours)) {
            throw new Error(
                'Invalid input: Tours must be an array of Tour objects.',
            );
        }
        const request = localStorage.getItem('request');
        const mapFile = localStorage.getItem('map-file');
        if (!request) {
            throw new Error('No request to save');
        }

        const parsedRequest: TourRequest = JSON.parse(request);
        const requests: DeliveryRequest[] = parsedRequest.request;
        const warehouse = parsedRequest.warehouse;
        // console.log('Requests:', requests);
        // console.log('Delivery points:', deliveryPoints);
        // console.log('Pickup points:', pickupPoints);
        // console.log('Warehouse:', warehouse);
        const body = {
            tours: tours.map((tour) => {
                if (
                    typeof tour.id !== 'string' ||
                    typeof tour.duration !== 'number' ||
                    !Array.isArray(tour.intersections)
                ) {
                    throw new Error(
                        'Invalid input: Each Tour must have a valid id, duration, and intersections.',
                    );
                }

                return {
                    id: tour.id,
                    duration: tour.duration,
                    intersections: tour.intersections.map((intersection) => {
                        if (
                            !intersection ||
                            !intersection.key ||
                            !intersection.location
                        ) {
                            throw new Error(
                                'Invalid input: Each intersection must have a key and a location.',
                            );
                        }
                        return {
                            key: intersection.key,
                            location: intersection.location, // Inclut également la propriété `location`
                        };
                    }),
                };
            }),
            deliveryRequests: requests,
            warehouse: warehouse,
            'map-file': mapFile,
        };

        try {
            const response = await fetch(
                `${this.baseUrl}${'/ActionServlet?action=save-tour'}`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(body),
                },
            );
            if (!response.ok) {
                const errorDetails = await response.text();
                throw new Error(
                    `Failed to save tours. Status: ${response.status}, Message: ${response.statusText}, Details: ${errorDetails}`,
                );
            }
        } catch (error) {
            console.error('Error saving tours:', (error as Error).message);
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
