import React, { useEffect } from 'react';
import styles from './Banner.module.css';

interface BannerProps {
    message: string;
    type: 'success' | 'error';
    onClose: () => void;
    duration?: number;
}

const Banner: React.FC<BannerProps> = ({
    message,
    type,
    onClose,
    duration = 3000,
}) => {
    useEffect(() => {
        const timer = setTimeout(onClose, duration);
        return () => clearTimeout(timer);
    }, [onClose, duration]);
    return (
        <div className={`${styles.banner} ${styles[type]}`}>
            <span>{message}</span>
            <button onClick={onClose} className={styles['close-button']}>
                &times;
            </button>
        </div>
    );
};

export default Banner;
