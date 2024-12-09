import React, { useState } from 'react';
import styles from './sidebar.module.css'; // Import du CSS sous forme de module
import { FaMapMarkedAlt } from 'react-icons/fa';
import { VscClose } from 'react-icons/vsc';
import { IconType } from 'react-icons';

type SidebarProps = {
    items: { id: string; logo: IconType; content: React.ReactNode }[];
};

export default function Sidebar({ items }: SidebarProps) {
    const [activeItem, setActiveItem] = useState<string | null>(null);
    const handleClick = (item: string) => {
        setActiveItem(item);
    };

    const renderContent = () => {
        const activeContent = items.find((item) => item.id === activeItem);
        if (!activeContent) return null;

        return (
            <div className={activeItem ? styles['item-bar'] : ''}>
                <div className={styles['item-bar-header']}>
                    <div className={styles['sidebar-nav-item']}>
                        <VscClose
                            onClick={() => setActiveItem(null)}
                            role="button"
                            aria-label="Close item bar"
                            size={24}
                        />
                    </div>
                </div>
                <div className={styles['item-bar-content']}>
                    {activeContent.content}
                </div>
            </div>
        );
    };

    return (
        <div className={styles.container}>
            <div className={styles.sidebar}>
                <nav className={styles['sidebar-nav']}>
                    {items.map((item) => (
                        <div
                            key={item.id}
                            className={styles['sidebar-nav-item']}
                            onClick={() => handleClick(item.id)}
                            style={{
                                color:
                                    activeItem == item.id
                                        ? 'grey'
                                        : 'lightgrey',
                            }}
                        >
                            <item.logo size={24} />
                        </div>
                    ))}
                </nav>
            </div>
            <div>{renderContent()}</div>
        </div>
    );
}
