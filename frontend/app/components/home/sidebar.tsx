import React, { useState } from 'react';
import styles from './sidebar.module.css'; // Import du CSS sous forme de module
import { VscClose } from 'react-icons/vsc';
import { IconType } from 'react-icons';

type SidebarProps = {
    items: { id: string; logo: IconType; content: React.ReactNode }[];
};

export default function Sidebar({ items }: SidebarProps) {
    const [activeItem, setActiveItem] = useState<string | null>(null);
    const [sidebarWidth, setSidebarWidth] = useState<number>(200);
    const [isDragging, setIsDragging] = useState<boolean>(false);
    const MIN_WIDTH = 100;

    const handleClick = (item: string) => {
        setActiveItem(item);
    };

    const handleMouseDown = (e: React.MouseEvent) => {
        setIsDragging(true);
    };

    const handleMouseUp = () => {
        setIsDragging(false);
    };

    const handleMouseMove = (e: MouseEvent) => {
        if (isDragging) {
            const newWidth = e.clientX - 59;
            if (newWidth >= MIN_WIDTH) {
                setSidebarWidth(newWidth);
            } else {
                setActiveItem(null);
            }
        }
    };

    React.useEffect(() => {
        document.addEventListener('mousemove', handleMouseMove);
        document.addEventListener('mouseup', handleMouseUp);
        return () => {
            document.removeEventListener('mousemove', handleMouseMove);
            document.removeEventListener('mouseup', handleMouseUp);
        };
    }, [isDragging]);

    const renderContent = () => {
        const activeContent = items.find((item) => item.id === activeItem);
        if (!activeContent) return null;

        return (
            <div
                className={activeItem ? styles['item-bar'] : ''}
                style={{ width: sidebarWidth }}
            >
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
            <div
                className={`${styles.toggleline} ${isDragging ? styles.dragging : ''}`}
                onMouseDown={handleMouseDown}
            ></div>
        </div>
    );
}
