'use client';

import Image from 'next/image';
import styles from './page.module.css';
import React, { useState } from 'react';
import FileDialog from './components/home/file-dialog';
import OptimodApiService from './services/service';
import Intersection from './types/intersection';
import Tour from './types/tour';
import Courier from './types/courier';
import Sidebar from './components/home/sidebar';
import {
    FaMapMarkedAlt,
    FaFileUpload,
    FaArrowCircleDown,
} from 'react-icons/fa';
import { GrDirections } from 'react-icons/gr';

import { Button } from './components/home/button';
import { MdCalculate } from 'react-icons/md';
import {
    DeliveryMarker,
    PickupMarker,
    WarehouseMarker,
} from './components/home/marker';
import {
    GoogleMap,
    GoogleMapApiLoader,
    Polyline,
} from 'react-google-map-wrapper';
import Banner from './components/home/banner';

export default function Home() {
    // const [map, setMap] = React.useState<Intersection[]>([]);
    const [tourCoordinates, setTourCoordinates] = React.useState<
        google.maps.LatLngLiteral[]
    >([]);
    const [couriers, setCouriers] = React.useState<Record<string, google.maps.LatLngLiteral[]>>({});
    const [warehouse, setWarehouse] = React.useState<Intersection | null>(null);
    const [pickupPoints, setPickupPoints] = React.useState<Intersection[]>([]);
    const [bannerMessage, setBannerMessage] = useState<string | null>(null);
    const [bannerType, setBannerType] = useState<'success' | 'error' | null>(
        null,
    );
    const [deliveryPoints, setDeliveryPoints] = React.useState<Intersection[]>(
        [],
    );   
    const [matchedRequests, setMatchedRequests] = React.useState<
        {
            index: number;
            courierId: string;
            pickupPoint: Intersection;
            deliveryPoint: Intersection;
        }[]
    >([]);

    const [numCouriers, setNumCouriers] = React.useState(1);
    const apiService = new OptimodApiService();

    const handleLoadMap = async (file: File) => {
        try {
            const markers = await apiService.loadMap(file);
            setBannerMessage('Map loaded successfully!');
            setBannerType('success');
        } catch (error) {
            console.error('Error loading map:', error);
            setBannerMessage('Error loading map.');
            setBannerType('error');
        }
    };
    const handleSaveTours = async () => {
        try {
            const Jsontours = localStorage.getItem('tours');
            if (!Jsontours) {
                throw new Error('No tour to save');
            }
            const tours = JSON.parse(Jsontours) as Tour[];

            await apiService.saveTours(tours);
        } catch (error) {
            console.error('Error loading map:', error);
        }
    };

    const handleLoadRequest = async (file: File) => {
        try {
            const tourRequest = await apiService.loadRequest(file);
            const warehouse = tourRequest.warehouse;
            const requests = tourRequest.request;

            setWarehouse(warehouse);
            setPickupPoints([...requests.map((req) => req.pickupPoint)]);
            setDeliveryPoints([...requests.map((req) => req.deliveryPoint)]);
            setBannerMessage('Request loaded successfully!');
            setBannerType('success');
        } catch (error) {
            console.error('Error loading tour request:', error);
            setBannerMessage('Error loading tour request.');
            setBannerType('error');
        }
    };

    const handleComputeTour = async () => {
        try {
            const { tour, tourRequest } = await apiService.computeTour();
            const coordinates = tour.intersections.map(
                (intersection: Intersection) => ({
                    lat: intersection.location.lat,
                    lng: intersection.location.lng,
                }),
            );
            setTourCoordinates(coordinates);


            const enrichedRelations: {
                index: number;
                courierId: string;
                pickupPoint: Intersection;
                deliveryPoint: Intersection;
            }[] = [];

            let index = 1;
                tourRequest.request.forEach((deliveryRequest) => {
                    enrichedRelations.push({
                        index: index++, // index unique pour chaque couple pickup-delivery
                        courierId: tourRequest.key, // ID du livreur
                        pickupPoint: deliveryRequest.pickupPoint, 
                        deliveryPoint: deliveryRequest.deliveryPoint, 
                    });
            });

            let tours: Tour[] = [];
            const jsonTours = localStorage.getItem('tours');
            if (jsonTours) {
                tours = JSON.parse(jsonTours) as Tour[];
            }

            tours.push(tour);

            localStorage.setItem('tours', JSON.stringify(tours));
            setMatchedRequests(enrichedRelations);
            setBannerMessage('Tour computed successfully!');
            setBannerType('success');
        } catch (error) {
            console.error('Error computing tour:', error);
            setBannerMessage('Error computing tour.');
            setBannerType('error');
        }
    };
    
    const handleMultipleComputeTour = async () => {
        try {
            const courierData = await apiService.computeMultipleTours(numCouriers);

            const allCoordinates: Record<string, google.maps.LatLngLiteral[]> = {};

            const enrichedRelations: {
                index: number;
                courierId: string;
                pickupPoint: Intersection;
                deliveryPoint: Intersection;
            }[] = [];

    
            Object.entries(courierData).forEach(([courierId, courier]: [string, Courier]) => {
                const coordinates = courier.tour.intersections.map((intersection: Intersection) => ({
                    lat: intersection.location.lat,
                    lng: intersection.location.lng,
                }));
                allCoordinates[courierId] = coordinates;

                let index = 1;
                courier.tourRequest.request.forEach((deliveryRequest) => {
                    enrichedRelations.push({
                        index: index++, // index unique pour chaque couple pickup-delivery
                        courierId: courierId, // ID du livreur
                        pickupPoint: deliveryRequest.pickupPoint, 
                        deliveryPoint: deliveryRequest.deliveryPoint, 
                    });
                });
            });

            // Mettre à jour l'état React pour l'affichage des couriers
            setCouriers(allCoordinates);

            setMatchedRequests(enrichedRelations);

            // Sauvegarder les couriers dans le localStorage
            let storedCouriers: Courier[] = [];
            const jsonCouriers = localStorage.getItem('couriers');

            if (jsonCouriers) {
                storedCouriers = JSON.parse(jsonCouriers) as Courier[];
            }

            // Ajouter les nouveaux couriers et mettre à jour le localStorage
            storedCouriers.push(...Object.values(courierData));
            localStorage.setItem('couriers', JSON.stringify(storedCouriers));
            setBannerMessage('Multiple tours computed successfully!');
            setBannerType('success');
            console.log('Multiple tours computed successfully:', courierData);
        } catch (error) {
            console.error('Error computing multiple tours:', error);
            setBannerMessage('Error computing multiple tours.');
            setBannerType('error');
        }
    };

    const getDynamicColor = (index: number): string => {
        const hue = (index * 137) % 360; // Génère une teinte différente pour chaque index
        return `hsl(${hue}, 70%, 50%)`; // Teinte, saturation et luminosité
    };
    
    
    const sidebarItems = [
        {
            id: 'Map',
            logo: FaMapMarkedAlt,
            content: (
                <section className={styles.section}>
                    <h5>Map</h5>
                    <FileDialog
                        logo={FaFileUpload}
                        text="Load map"
                        validateFile={handleLoadMap}
                    />
                    <FileDialog
                        logo={FaFileUpload}
                        text="Load request"
                        validateFile={handleLoadRequest}
                    />
                    <Button
                        logo={MdCalculate}
                        onClick={handleComputeTour}
                        text="Compute tour"
                    />
                    {/* Zone pour saisir le nombre de livreurs */}
                    <div style={{ marginTop: '10px' }}>
                        <label htmlFor="numCouriers" style={{ marginRight: '10px' }}>
                            Couriers:
                        </label>
                        <input
                            id="numCouriers"
                            type="number"
                            min="1"
                            value={numCouriers}
                            onChange={(e) => setNumCouriers(Number(e.target.value))}
                            style={{ width: '60px', textAlign: 'center' }}
                        />
                    </div>
                    <Button
                        logo={MdCalculate}
                        onClick={handleMultipleComputeTour}
                        text="Compute multiple tours"
                    />
                </section>
            ),
        },
        {
            id: 'Tour',
            logo: GrDirections,
            content: <section className={styles.section}></section>,
        },
        {
            id: 'Save',
            logo: FaArrowCircleDown,
            content: (
                <section>
                    <h5>Save</h5>
                    <Button
                        onClick={handleSaveTours}
                        text="Save tour"
                        logo={FaArrowCircleDown}
                    />
                </section>
            ),
        },
    ];

    return (
        <div className={styles.page}>
            <header className={styles.header}>
                <Image
                    className={styles.logo}
                    src="/logo.svg"
                    alt="OptimodLyon logo"
                    width={500}
                    height={500}
                    priority
                />
            </header>

            <Sidebar items={sidebarItems} />

            <main className={styles.main}>
                {bannerMessage && bannerType && (
                    <Banner
                        message={bannerMessage}
                        type={bannerType}
                        onClose={() => setBannerMessage(null)}
                    />
                )}
                <GoogleMapApiLoader
                    apiKey={process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY || ''}
                    suspense
                >
                    <GoogleMap
                        style={{ width: '800px', height: '500px' }}
                        center={{ lat: 45.75, lng: 4.85 }}
                        zoom={12}
                        containerProps={{ id: 'google-map' }}
                        mapOptions={{
                            mapId: '67b4524f1a110aa8',
                        }}
                    >
                        <Polyline
                            path={tourCoordinates}
                            strokeColor="#FF0000"
                            strokeOpacity={10.0}
                            strokeWeight={2.0}
                            geodesic
                        />

                        {/* Affichage des polylines avec des couleurs dynamiques */}
                        {Object.entries(couriers).map(([courierId, coordinates], index) => (
                            <Polyline
                                key={courierId}
                                path={coordinates}
                                strokeColor={getDynamicColor(index)} // Couleur dynamique basée sur l'index
                                strokeOpacity={1.0}
                                strokeWeight={3.0}
                                geodesic
                            />
                        ))}

                        {warehouse && <WarehouseMarker warehouse={warehouse} />}
                        {/* Afficher les relations enrichies */}
                        {matchedRequests && (
                            <>
                                <PickupMarker matchedRequests={matchedRequests.map(({ index, courierId, pickupPoint }) => ({ index, courierId, pickupPoint }))} />
                                <DeliveryMarker matchedRequests={matchedRequests.map(({ index, courierId, deliveryPoint }) => ({ index, courierId, deliveryPoint }))} />
                            </>
                        )}
                    </GoogleMap>
                </GoogleMapApiLoader>
            </main>
            <footer className={styles.footer}>
                <p>© 2024 All rights reserved.</p>
            </footer>
        </div>
    );
}
