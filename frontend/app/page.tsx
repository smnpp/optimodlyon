'use client';

import Image from 'next/image';
import styles from './page.module.css';
import React from 'react';
import {
    AdvancedMarker,
    APIProvider,
    Map,
    Pin,
} from '@vis.gl/react-google-maps';
import FileDialog from './components/home/file-dialog';
import OptimodApiService from './services/service';
import Intersection from './types/intersection';
import Sidebar from './components/home/sidebar';
import { FaMapMarkedAlt, FaFileUpload } from 'react-icons/fa';
import { GrDirections } from 'react-icons/gr';

const PoiMarkers = (props: { pois: Intersection[] }) => {
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

export default function Home() {
    const [markers, setMarkers] = React.useState<Intersection[]>([]);
    const apiService = new OptimodApiService();

    const handleLoadMap = async (file: File) => {
        try {
            const markers = await apiService.loadMap(file);
            setMarkers(markers);
        } catch (error) {
            console.error('Error loading map:', error);
        }
    };

    const sidebarItems = [
        {
            id: 'Map',
            logo: FaMapMarkedAlt,
            content: (
                <section>
                    <h5>Map</h5>
                    <FileDialog
                        logo={FaFileUpload}
                        text="Load map"
                        validateFile={handleLoadMap}
                    />
                </section>
            ),
        },
        {
            id: 'Tour',
            logo: GrDirections,
            content: (
                <section>
                    <h5>Tour</h5>
                    <FileDialog
                        logo={FaFileUpload}
                        text="Load request"
                        validateFile={handleLoadMap}
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
                <APIProvider
                    apiKey={process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY || ''}
                >
                    <Map
                        style={{ width: '800px', height: '500px' }}
                        defaultCenter={{ lat: 45.75, lng: 4.85 }}
                        defaultZoom={12}
                        gestureHandling={'greedy'}
                        disableDefaultUI={true}
                        colorScheme="DARK"
                        mapId="map"
                    >
                        <PoiMarkers pois={markers} />
                    </Map>
                </APIProvider>
            </main>
            <footer className={styles.footer}>
                <p>© 2024 All rights reserved.</p>
            </footer>
        </div>
    );
}
