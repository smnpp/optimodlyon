import React from 'react';
import styles from './button.module.css';
import { IconType } from 'react-icons';

enum ButtonColor {
    red = 'red',
    green = 'green',
    purple = 'purple',
    primary = 'primary',
    secondary = 'secondary',
}
interface ButtonProps {
    onClick: (e?: React.MouseEvent<HTMLButtonElement>) => void;
    text: string;
    logo?: IconType;
    children?: React.ReactNode;
    color?: ButtonColor;
    minWidth?: string;
}

const Button: React.FC<ButtonProps> = ({
    onClick,
    text,
    logo,
    children,
    color,
    minWidth,
}) => {
    const buttonClass = [styles.button];
    if (color && styles[color]) {
        buttonClass.push(styles[color]);
    }
    return (
        <button
            onClick={onClick}
            className={buttonClass.join(' ')}
            style={{ minWidth: minWidth }}
        >
            {logo && React.createElement(logo)}
            {text}
            {children}
        </button>
    );
};

export { Button, ButtonColor };
