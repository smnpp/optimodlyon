import React, { useState } from 'react';
import styles from './sidebar.module.css'; // Import du CSS sous forme de module
import { FaMapMarkedAlt } from 'react-icons/fa';
import { VscClose } from 'react-icons/vsc';

enum SidebarItem {
    Tour = 'Tour',
}

export default function Sidebar() {
    const [activeItem, setActiveItem] = useState('');
    const handleClick = (item: string) => {
        setActiveItem(item);
    };

    const renderContent = () => {
        switch (activeItem) {
            case SidebarItem.Tour:
                return (
                    <div className={activeItem ? styles['item-bar'] : ''}>
                        <div className={styles['item-bar-header']}>
                            <div className={styles['sidebar-nav-item']}>
                                <VscClose
                                    onClick={() => setActiveItem('')}
                                    role="button"
                                    aria-label="Close item bar"
                                    size={24}
                                />
                            </div>

                        </div>
                        <div className={styles['item-bar-content']}>
                            <p>Waypoints:</p>
                            <p>Starting point:</p>
                            <p>End point:</p>
                            <p>Duration:</p>
                        </div>
                    </div>
                );

            default:
                return null;
        }
    };

    return (
        <div className={styles.container}>
            <div className={styles.sidebar}>
                <nav className={styles['sidebar-nav']}>
                    <div className={styles['sidebar-nav-item']}>
                        <FaMapMarkedAlt 
                            onClick={() => handleClick(SidebarItem.Tour)}
                            size={24}
                        />
                    </div>
                </nav>
            </div>
            <div>{renderContent()}</div>
        </div>
    );
}
