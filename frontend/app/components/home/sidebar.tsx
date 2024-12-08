import React, { useState } from 'react';
import styles from './sidebar.module.css'; // Import du CSS sous forme de module
import * as FaIcons from 'react-icons/fa';

enum SidebarItem {
    Tour = 'Tour',
}

export default function Sidebar() {
    const [activeItem, setActiveItem] = useState('');
    const handleClick = (item: string) => {
        setActiveItem(item);
        console.log(`Clicked on ${item}`);
    };

    const renderContent = () => {
        switch (activeItem) {
            case SidebarItem.Tour:
                return (
                    <div className={styles['item-bar-content']}>
                        <p>Nombre de points de passage :</p>
                        <p>durée:</p>
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
                    <div
                        className={styles['sidebar-nav-item']}
                        onClick={() => handleClick(SidebarItem.Tour)}
                    >
                        <FaIcons.FaMapMarkedAlt />
                    </div>
                </nav>
            </div>
            <div className={activeItem ? styles['item-bar'] : ''}>
                {renderContent()}
            </div>
        </div>
    );
}
