'use client';

import Image from 'next/image';
import styles from './page.module.css';
import React, { useState } from 'react';
import FileDialog from './components/home/file-dialog';
import OptimodApiService from './services/service';
import Intersection from './types/intersection';
import Tour from './types/tour';
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
    const [warehouse, setWarehouse] = React.useState<Intersection | null>(null);
    const [pickupPoints, setPickupPoints] = React.useState<Intersection[]>([]);
    const [bannerMessage, setBannerMessage] = useState<string | null>(null);
    const [bannerType, setBannerType] = useState<'success' | 'error' | null>(
        null,
    );
    const [deliveryPoints, setDeliveryPoints] = React.useState<Intersection[]>(
        [],
    );
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

    const handleRestoreTour = async (file: File) => {
        try {
            const str = await apiService.loadTour(file);
            console.log('Raw response:', str);

            const parsedRequest = JSON.parse(str);
            console.log('Parsed JSON:', parsedRequest);

            const tour = parsedRequest.tour;
            const tours = tour.tours || [];

            if (!tour || !tour.typePoints) {
                throw new Error("Missing 'typePoints' in the JSON structure.");
            }

            const typePoints = tour.typePoints;

            // Transformer les points en format Intersection
            const transformToIntersection = (points: any[]): Intersection[] =>
                points.map((point) => ({
                    key: point.id,
                    location: { lat: point.latitude, lng: point.longitude },
                }));

            // Extraire les intersections depuis chaque tour
            const allIntersections: google.maps.LatLngLiteral[] = tours.flatMap(
                (tourItem: any) =>
                    tourItem.intersections.map((intersection: any) => ({
                        lat: intersection.latitude,
                        lng: intersection.longitude,
                    })),
            );

            // Traiter warehousePoint comme un objet unique
            const warehouse: Intersection | null = typePoints.warehousePoint
                ? {
                      key: typePoints.warehousePoint.id,
                      location: {
                          lat: typePoints.warehousePoint.latitude,
                          lng: typePoints.warehousePoint.longitude,
                      },
                  }
                : null;

            // Mise à jour de l'état avec une vérification
            if (warehouse) {
                setWarehouse(warehouse);
            }

            // Traiter pickupPoints et deliveryPoints normalement
            const deliveryPoints: Intersection[] = transformToIntersection(
                typePoints.deliveryPoints || [],
            );
            const pickupPoints: Intersection[] = transformToIntersection(
                typePoints.pickupPoints || [],
            );

            // Log pour vérification
            console.log('Transformed Warehouse Point:', warehouse);
            console.log('Transformed Delivery Points:', deliveryPoints);
            console.log('Transformed Pickup Points:', pickupPoints);
            console.log('Tour Intersections:', allIntersections);

            // Mise à jour de l'état
            if (warehouse) {
                setWarehouse(warehouse); // Un seul point
            } else {
                console.warn('No warehouse point found.');
            }

            setDeliveryPoints(deliveryPoints);
            setPickupPoints(pickupPoints);
            setTourCoordinates(allIntersections);

            setBannerMessage('Restore successfully!');
            setBannerType('success');
        } catch (error) {
            console.error('Error restoring tour:', error);
            setBannerMessage(
                'Error restoring tour. Please check the file format and try again.',
            );
            setBannerType('error');
        }
    };
    const handleComputeTour = async () => {
        try {
            const tour = await apiService.computeTour();
            const coordinates = tour.intersections.map(
                (intersection: Intersection) => ({
                    lat: intersection.location.lat,
                    lng: intersection.location.lng,
                }),
            );
            setTourCoordinates(coordinates);
            let tours: Tour[] = [];
            const jsonTours = localStorage.getItem('tours');
            if (jsonTours) {
                tours = JSON.parse(jsonTours) as Tour[];
            }

            tours.push(tour);

            localStorage.setItem('tours', JSON.stringify(tours));
            setBannerMessage('Tour computed successfully!');
            setBannerType('success');
        } catch (error) {
            console.error('Error computing tour:', error);
            setBannerMessage('Error computing tour.');
            setBannerType('error');
        }
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
                <section className={styles.section}>
                    <h5>Save</h5>
                    <Button
                        onClick={handleSaveTours}
                        text="Save tour"
                        logo={FaArrowCircleDown}
                    />
                    <FileDialog
                        logo={FaFileUpload}
                        text="Restore tour"
                        validateFile={handleRestoreTour}
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
                        {warehouse && <WarehouseMarker warehouse={warehouse} />}
                        {pickupPoints && (
                            <PickupMarker pickupPoints={pickupPoints} />
                        )}
                        {deliveryPoints && (
                            <DeliveryMarker deliveryPoints={deliveryPoints} />
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
