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
        console.log(`Clicked on ${item}`);
    };

    const renderContent = () => {
        switch (activeItem) {
            case SidebarItem.Tour:
                return (
                    <div className={activeItem ? styles['item-bar'] : ''}>
                        <div className={styles['item-bar-header']}>
                            <VscClose
                                onClick={() => setActiveItem('')}
                                role="button"
                                aria-label="Close item bar"
                            />
                        </div>
                        <div className={styles['item-bar-content']}>
                            <p>Nombre de points de passage :</p>
                            <p>durée:</p>
                            <p>Point de départ :</p>
                            <p>Point d'arrivée :</p>
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
                    <div
                        className={styles['sidebar-nav-item']}
                        onClick={() => handleClick(SidebarItem.Tour)}
                    >
                        <FaMapMarkedAlt />
                    </div>
                </nav>
            </div>
            <div>{renderContent()}</div>
        </div>
    );
}
