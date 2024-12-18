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
    MarkerType,
    PickupMarker,
    WarehouseMarker,
} from './components/home/marker';
import {
    GoogleMap,
    GoogleMapApiLoader,
    Polyline,
} from 'react-google-map-wrapper';
import Banner from './components/home/banner';
import { findClosestPoint } from './util';
import TourRequest from './types/tour-request';
import DeliveryRequest from './types/delivery-request';

export default function Home() {
    const [couriers, setCouriers] = React.useState<
        Record<string, google.maps.LatLngLiteral[]>
    >({});
    const [warehouse, setWarehouse] = React.useState<Intersection | null>(null);
    const [deliveryRequests, setDeliveryRequests] = useState<
        DeliveryRequest[] | null
    >(null);
    const [bannerMessage, setBannerMessage] = useState<string | null>(null);
    const [bannerType, setBannerType] = useState<'success' | 'error' | null>(
        null,
    );

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
            const strCouriers = localStorage.getItem('couriers');
            if (!strCouriers) {
                throw new Error('No tour to save');
            }
            const jsonCouriers = JSON.parse(strCouriers) as Courier[];
            const tours: Tour[] = jsonCouriers.map((courier) => courier.tour);
            await apiService.saveTours(tours);
        } catch (error) {
            console.error('Error loading map:', error);
        }
    };

    const handleLoadRequest = async (file: File) => {
        try {
            const tourRequest = await apiService.loadRequest(file);
            const warehouse = tourRequest.warehouse;
            const request = tourRequest.request;

            // Clear existing markers
            setWarehouse(null);
            setDeliveryRequests(null);

            setWarehouse(warehouse);
            setDeliveryRequests(request);

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

            // **Transformer les points en format Intersection**
            const transformToIntersection = (points: any[]): Intersection[] =>
                points.map((point) => ({
                    key: point.id,
                    location: {
                        lat: point.latitude || 0,
                        lng: point.longitude || 0,
                    },
                }));

            // **Extraire les intersections depuis chaque tour**
            const allIntersections: Record<
                string,
                google.maps.LatLngLiteral[]
            > = {};
            tours.forEach((tourItem: any, index: number) => {
                const courierId = `courier_${index + 1}`; // ID auto-généré : courier_1, courier_2, etc.
                const coordinates = tourItem.intersections.map(
                    (intersection: any) => ({
                        lat: intersection.latitude || 0,
                        lng: intersection.longitude || 0,
                    }),
                );
                allIntersections[courierId] = coordinates;
            });

            // **Traiter warehousePoint comme un objet unique**
            const warehouse: Intersection | null = typePoints.warehousePoint
                ? {
                      key: typePoints.warehousePoint.id,
                      location: {
                          lat: typePoints.warehousePoint.latitude,
                          lng: typePoints.warehousePoint.longitude,
                      },
                  }
                : null;

            // **Transformer deliveryRequests au format DeliveryRequest**
            const deliveryRequests: DeliveryRequest[] =
                typePoints.deliveryRequests?.map((request: any) => ({
                    key: request.key,
                    pickupPoint: {
                        key: request.pickupPoint.key,
                        location: {
                            lat: request.pickupPoint.location.latitude,
                            lng: request.pickupPoint.location.longitude,
                        },
                    },
                    deliveryPoint: {
                        key: request.deliveryPoint.key,
                        location: {
                            lat: request.deliveryPoint.location.latitude,
                            lng: request.deliveryPoint.location.longitude,
                        },
                    },
                    pickupDuration: Number(request.pickupDuration),
                    deliveryDuration: Number(request.deliveryDuration),
                })) || [];

            // **Traiter les autres points (pickup et delivery)**
            const deliveryPoints: Intersection[] = transformToIntersection(
                typePoints.deliveryPoints || [],
            );
            const pickupPoints: Intersection[] = transformToIntersection(
                typePoints.pickupPoints || [],
            );

            // **Log pour vérification**
            console.log('Transformed Warehouse Point:', warehouse);
            console.log('Transformed Delivery Requests:', deliveryRequests);
            console.log('Transformed Delivery Points:', deliveryPoints);
            console.log('Transformed Pickup Points:', pickupPoints);
            console.log('Courier Intersections:', allIntersections);

            // **Mise à jour de l'état**
            if (warehouse) {
                setWarehouse(warehouse); // Un seul point
            } else {
                console.warn('No warehouse point found.');
            }

            setDeliveryRequests(deliveryRequests);
            setCouriers(allIntersections);

            // **Message de succès**
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
            const request: TourRequest = {
                key: crypto.randomUUID(),
                request: deliveryRequests!,
                warehouse: warehouse!,
            };
            const courierData = await apiService.computeTours(
                numCouriers,
                request,
            );
            const allCoordinates: Record<string, google.maps.LatLngLiteral[]> =
                {};

            Object.entries(courierData).forEach(
                ([courierId, courier]: [string, Courier]) => {
                    const coordinates = courier.tour.intersections.map(
                        (intersection: Intersection) => ({
                            lat: intersection.location.lat,
                            lng: intersection.location.lng,
                        }),
                    );
                    allCoordinates[courierId] = coordinates;
                },
            );

            // Mettre à jour l'état React pour l'affichage des couriers
            setCouriers(allCoordinates);

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

    const updateMarkerPosition = (
        key: string,
        newPosition: google.maps.LatLngLiteral,
        type: MarkerType,
        newKey: string,
    ) => {
        if (type === MarkerType.Warehouse) {
            setWarehouse((prevWarehouse) =>
                prevWarehouse && prevWarehouse.key === key
                    ? { ...prevWarehouse, location: newPosition, key: newKey }
                    : prevWarehouse,
            );
        } else if (type === MarkerType.Pickup) {
            setDeliveryRequests((prevRequests) =>
                prevRequests!.map((request) =>
                    request.pickupPoint.key === key
                        ? {
                              ...request,
                              pickupPoint: {
                                  ...request.pickupPoint,
                                  location: newPosition,
                                  key: newKey,
                              },
                          }
                        : request,
                ),
            );
        } else if (type === MarkerType.Delivery) {
            setDeliveryRequests((prevRequests) =>
                prevRequests!.map((request) =>
                    request.deliveryPoint.key === key
                        ? {
                              ...request,
                              deliveryPoint: {
                                  ...request.deliveryPoint,
                                  location: newPosition,
                                  key: newKey,
                              },
                          }
                        : request,
                ),
            );
        }
    };

    const handleDragEnd = (
        marker: google.maps.marker.AdvancedMarkerElement,
        event: google.maps.MapMouseEvent,
        key: string,
        type: MarkerType,
        setContent: (content: React.ReactNode) => void,
    ) => {
        if (event.latLng) {
            const newPos = {
                lat: event.latLng.lat(),
                lng: event.latLng.lng(),
            };
            const map = JSON.parse(localStorage.getItem('map') || '[]');
            const closestPoint = findClosestPoint(newPos, map);
            if (closestPoint) {
                marker.position = closestPoint.location;
                updateMarkerPosition(
                    key,
                    closestPoint.location,
                    type,
                    closestPoint.key.toString(),
                );
                const content = (
                    <div style={{ color: 'black' }}>
                        <h3>{type}</h3>
                        <p>
                            Location:{' '}
                            {`${closestPoint.location.lat.toPrecision(8)}, ${closestPoint.location.lng.toPrecision(8)}`}
                        </p>
                    </div>
                );
                setContent(content);
            }
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
                    <div style={{ marginTop: '10px' }}>
                        <label
                            htmlFor="numCouriers"
                            style={{ marginRight: '10px' }}
                        >
                            Couriers:
                        </label>
                        <input
                            id="numCouriers"
                            type="number"
                            min="1"
                            value={numCouriers}
                            onChange={(e) =>
                                setNumCouriers(Number(e.target.value))
                            }
                            style={{ width: '60px', textAlign: 'center' }}
                        />
                    </div>
                    <Button
                        logo={MdCalculate}
                        onClick={handleComputeTour}
                        text="Compute tours"
                    />
                </section>
            ),
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
                        {warehouse && (
                            <WarehouseMarker
                                key={crypto.randomUUID()}
                                warehouse={warehouse}
                                handleDragEnd={(
                                    marker: google.maps.marker.AdvancedMarkerElement,
                                    event: google.maps.MapMouseEvent,
                                    setContent: (
                                        content: React.ReactNode,
                                    ) => void,
                                ) =>
                                    handleDragEnd(
                                        marker,
                                        event,
                                        warehouse.key,
                                        MarkerType.Warehouse,
                                        setContent,
                                    )
                                }
                            />
                        )}

                        {/* Affichage des polylines avec des couleurs dynamiques */}
                        {Object.entries(couriers).map(
                            ([courierId, coordinates], index) => (
                                <Polyline
                                    key={courierId}
                                    path={coordinates}
                                    strokeColor={getDynamicColor(index)} // Couleur dynamique basée sur l'index
                                    strokeOpacity={1.0}
                                    strokeWeight={3.0}
                                    geodesic
                                />
                            ),
                        )}
                        {/* Afficher les relations enrichies */}
                        {deliveryRequests &&
                            deliveryRequests.map((request, index) => (
                                <React.Fragment key={request.key}>
                                    <PickupMarker
                                        key={crypto.randomUUID()}
                                        pickupPoint={request.pickupPoint}
                                        index={index + 1}
                                        handleDragEnd={(
                                            marker: google.maps.marker.AdvancedMarkerElement,
                                            event: google.maps.MapMouseEvent,
                                            setContent: (
                                                content: React.ReactNode,
                                            ) => void,
                                        ) =>
                                            handleDragEnd(
                                                marker,
                                                event,
                                                request.pickupPoint.key,
                                                MarkerType.Pickup,
                                                setContent,
                                            )
                                        }
                                    />
                                    <DeliveryMarker
                                        key={crypto.randomUUID()}
                                        deliveryPoint={request.deliveryPoint}
                                        index={index}
                                        handleDragEnd={(
                                            marker: google.maps.marker.AdvancedMarkerElement,
                                            event: google.maps.MapMouseEvent,
                                            setContent: (
                                                content: React.ReactNode,
                                            ) => void,
                                        ) =>
                                            handleDragEnd(
                                                marker,
                                                event,
                                                request.deliveryPoint.key,
                                                MarkerType.Delivery,
                                                setContent,
                                            )
                                        }
                                    />
                                </React.Fragment>
                            ))}
                    </GoogleMap>
                </GoogleMapApiLoader>
            </main>
            <footer className={styles.footer}>
                <p>© 2024 All rights reserved.</p>
            </footer>
        </div>
    );
}
