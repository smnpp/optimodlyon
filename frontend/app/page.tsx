"use client";

import Image from "next/image";
import styles from "./page.module.css";
import React from 'react';
import { APIProvider, Map } from '@vis.gl/react-google-maps';

export default function Home() {
  return (
    <div className={styles.page}>
      <Image
        className={styles.logo}
        src="/next.svg"
        alt="Next.js logo"
        width={180}
        height={38}
        priority
      />

      <main className={styles.main}>
        <APIProvider apiKey={process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY || ''}>
          <Map
              style={{width: '800px', height: '500px'}}
              defaultCenter={{lat: 46.54992, lng: 2.44}}
              defaultZoom={3}
              gestureHandling={'greedy'}
              disableDefaultUI={true}
              colorScheme='DARK'
            />
        </APIProvider>

        <div className={styles.buttonsContainer}>
        </div>
      </main>
      <footer className={styles.footer}>
        <p>© 2024 All rights reserved.</p>
      </footer>
    </div>
  );
}
