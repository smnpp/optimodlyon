'use client';

import Image from 'next/image';
import styles from './page.module.css';
import React from 'react';
import FileDialog from './components/home/file-dialog';
import OptimodApiService from './services/service';
import Intersection from './types/intersection';
import Sidebar from './components/home/sidebar';
import { FaMapMarkedAlt, FaFileUpload } from 'react-icons/fa';
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

export default function Home() {
    // const [map, setMap] = React.useState<Intersection[]>([]);
    const [tourCoordinates, setTourCoordinates] = React.useState<
        google.maps.LatLngLiteral[]
    >([]);
    const [warehouse, setWarehouse] = React.useState<Intersection | null>(null);
    const [pickupPoints, setPickupPoints] = React.useState<Intersection[]>([]);
    const [deliveryPoints, setDeliveryPoints] = React.useState<Intersection[]>(
        [],
    );
    const apiService = new OptimodApiService();

    const handleLoadMap = async (file: File) => {
        try {
            const markers = await apiService.loadMap(file);
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
        } catch (error) {
            console.error('Error loading tour request:', error);
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
        } catch (error) {
            console.error('Error computing tour:', error);
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
                <GoogleMapApiLoader
                    apiKey={process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY || ''}
                >
                    <GoogleMap
                        style={{ width: '800px', height: '500px' }}
                        center={{ lat: 45.75, lng: 4.85 }}
                        zoom={12}
                        containerProps={{ id: 'google-map' }}
                        mapOptions={{
                            backgroundColor: 'dark',
                            mapId: 'map-id',
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
