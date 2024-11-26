import Image from "next/image";
import styles from "./page.module.css";
import Map from "./components/home/map";
import Navbar from "./components/home/navbar";

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
        <Map/>
      </main>
      <footer className={styles.footer}>
          <p>© 2024 All rights reserved.</p>
      </footer>
    </div>
  );
}
