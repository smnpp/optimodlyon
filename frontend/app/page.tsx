"use client";

import Image from "next/image";
import styles from "./page.module.css";
import React from 'react';
import { APIProvider, Map } from '@vis.gl/react-google-maps';
import FileDialog from "./components/home/file-dialog";
import OptimodApiService from "./services/service";

export default function Home() {

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

        <FileDialog
          logo="/archive.svg"
          validateFile={(file) => {
            new OptimodApiService().testApi();
            new OptimodApiService().loadMap(file);
          }}
        />
      </header>

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
      </main>
      <footer className={styles.footer}>
        <p>© 2024 All rights reserved.</p>
      </footer>
    </div>
    
  );
}
